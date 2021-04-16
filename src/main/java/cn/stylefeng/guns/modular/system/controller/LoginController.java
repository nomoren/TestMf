/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.stylefeng.guns.modular.system.controller;

import static cn.stylefeng.roses.core.util.HttpContext.getIp;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.stylefeng.guns.core.common.node.MenuNode;
import cn.stylefeng.guns.core.log.LogManager;
import cn.stylefeng.guns.core.log.factory.LogTaskFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.shiro.TrustedSsoAuthenticationToken;
import cn.stylefeng.guns.core.util.ApiMenuFilter;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.HttpContext;

import com.google.common.collect.Lists;
import com.smart.sso.client.SessionPermission;
import com.smart.sso.client.SessionUser;
import com.smart.sso.client.SessionUtils;
import com.smart.sso.rpc.RpcPermission;

/**
 * 登录控制器
 *
 * @author fengshuonan
 * @Date 2017年1月10日 下午8:25:24
 */
@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 跳转到主页
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:41 PM
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {

        //获取当前用户角色列表
       /* ShiroUser user = ShiroKit.getUserNotNull();
        List<Long> roleList = user.getRoleList();

        if (roleList == null || roleList.size() == 0) {
            ShiroKit.getSubject().logout();
            model.addAttribute("tips", "该用户没有角色，无法登陆");
            return "/login.html";
        }

        List<MenuNode> menus = userService.getUserMenuNodes(roleList);
        model.addAttribute("menus", menus);*/
    	
    	
		SessionPermission sessionPermission = SessionUtils.getSessionPermission(getHttpServletRequest());
		if (sessionPermission != null){
			// 登录用户当前应用的菜单
//			request.setAttribute("userMenus", sessionPermission.getMenuList());
//			// 登录用户当前应用的权限
//			request.setAttribute("userPermissions", sessionPermission.getPermissionSet());
			// 设置当前登录用户没有的权限，以便控制前台按钮的显示或者隐藏
//			model.addAttribute("sessionUserNoPermissions",
//					sessionPermission == null ? null : sessionPermission.getNoPermissions());
			List<MenuNode> menus  = Lists.newArrayList();
			for(RpcPermission rp:sessionPermission.getMenuList()){
				MenuNode m = new MenuNode();
				m.setId(rp.getId().longValue());
				m.setIsmenu(rp.getIsMenu()?"Y":"N");
				m.setName(rp.getName());
				m.setNum(1);
				if(null==rp.getParentId()){
					m.setParentId(0L);
					m.setLevels(1);
				}else{
				m.setParentId(rp.getParentId().longValue());
				m.setLevels(2);
				}
				m.setUrl(rp.getUrl());//            menuItem.setUrl(ConfigListener.getConf().get("contextPath") + menuItem.getUrl());
				
				menus.add(m);
			}
			List<MenuNode> titles = MenuNode.buildTitle(menus);
             ApiMenuFilter.build(titles);
			model.addAttribute("menus", titles);
		}

        return "/index.html";
    }

    /**
     * 跳转到登录页面
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:41 PM
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        if (ShiroKit.isAuthenticated() || ShiroKit.getUser() != null) {
            return REDIRECT + "/";
        } else {
            return "/login.html";
        }
    }

    /**
     * 点击登录执行的动作
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:42 PM
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginVali() {

        String username = super.getPara("username").trim();
        String password = super.getPara("password").trim();
        String remember = super.getPara("remember");

        Subject currentUser = ShiroKit.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray());

        //如果开启了记住我功能
        if ("on".equals(remember)) {
            token.setRememberMe(true);
        } else {
            token.setRememberMe(false);
        }

        //执行shiro登录操作
        currentUser.login(token);

        //登录成功，记录登录日志
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        LogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser.getId(), getIp()));

        ShiroKit.getSession().setAttribute("sessionFlag", true);

        return REDIRECT + "/";
    }

    /**
     * 退出登录
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:42 PM
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut() {
//        LogManager.me().executeLog(LogTaskFactory.exitLog(ShiroKit.getUserNotNull().getId(), getIp()));
        
        
        String ssoServerUrl="http://14.152.95.93:8180/ssq-sso-server";
        String ssoBackUrl="/";
        HttpServletRequest request = HttpContext.getRequest();
        SessionUtils.invalidate(request);
    	String logoutUrl = new StringBuilder().append(ssoServerUrl)
    			.append("/logout?backUrl=").append(getLocalUrl(request)).append(ssoBackUrl).toString();
    	
    	ShiroKit.getSubject().logout();
        deleteAllCookie();
        return REDIRECT + logoutUrl;
//        return REDIRECT + "/login";
    }
    
    


/**
 * 获取当前上下文路径
 * 
 * @param request
 * @return
 */
private String getLocalUrl(HttpServletRequest request) {
	return new StringBuilder().append(request.getScheme()).append("://").append(request.getServerName()).append(":")
			.append(request.getServerPort() == 80 ? "" : request.getServerPort()).append(request.getContextPath())
			.toString();
}
    
}
