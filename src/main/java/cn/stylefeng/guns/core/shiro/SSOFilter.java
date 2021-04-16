package cn.stylefeng.guns.core.shiro;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import com.smart.sso.client.SessionPermission;
import com.smart.sso.client.SessionUser;
import com.smart.sso.client.SessionUtils;
import com.smart.sso.client.SmartContainer;
import com.smart.sso.client.SsoFilter;
import com.smart.sso.client.SsoResultCode;
import com.smart.sso.rpc.AuthenticationRpcService;
import com.smart.sso.rpc.RpcPermission;
import com.smart.sso.rpc.RpcUser;

/**
 * 单点登录及Token验证Filter
 * 
 * @author ff
 */
public class SSOFilter extends SsoFilter{
	private String ssoAppCode;
	private boolean noValidateServer= false;
	@Override
	public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uri=request.getRequestURI();
		//这个接口是提供给平台做订单状态变化通知的，直接放行
		if(uri.contains("/erp/notic/orderNotic")){
			return true;
		}
		if (isAjaxRequest(request)) {
			//System.out.println("是ajax请求");
			return true;
		}
		String token = getLocalToken(request);
		if (token == null) {
			token = request.getParameter(SSO_TOKEN_NAME);
			if (token != null) {
				invokeAuthInfoInSession(request, token);
				// 再跳转一次当前URL，以便去掉URL中token参数
				response.sendRedirect(getRemoveTokenBackUrl(request));
				return false;
			}
		}
		else if (noValidateServer||authenticationRpcService.validate(token)) {// 验证token是否有效
			return true;
		}
		redirectLogin(request, response);
		return false;
	}

	/**
	 * 获取Session中token
	 * 
	 * @param request
	 * @return
	 */
	private String getLocalToken(HttpServletRequest request) {
		SessionUser sessionUser = SessionUtils.getSessionUser(request);
		return sessionUser == null ? null : sessionUser.getToken();
	}

	/**
	 * 存储sessionUser
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private void invokeAuthInfoInSession(HttpServletRequest request, String token) throws IOException {
		RpcUser rpcUser = authenticationRpcService.findAuthInfo(token);
		if (rpcUser != null) {
			SessionUtils.setSessionUser(request, new SessionUser(token, rpcUser.getAccount()));
			// 登录用户名
//			request.setAttribute("userName", sessionUser.getAccount());
			//String  ssoAppCode = "ssq-sso-erp";
		      SmartContainer sc = SpringContextHolder.getBean("smartFilter");
		      AuthenticationRpcService authenticationRpcService = sc.getAuthenticationRpcService();
//		      HttpServletRequest request = HttpContext.getRequest();
		      SessionUser sessionUser = SessionUtils.getSessionUser(request);
		      
		      String token2 =sessionUser.getToken();
		      List<RpcPermission> dbList = authenticationRpcService.findPermissionList(token2, ssoAppCode);

				List<RpcPermission> menuList = new ArrayList<RpcPermission>();
				Set<String> operateSet = new HashSet<String>();
				 
//			       Set<String> permissionSet = new HashSet<>();
//				      Set<String> roleNameSet = new HashSet<>();
					for (RpcPermission menu : dbList) {
						if (menu.getIsMenu()) {
							menuList.add(menu);
						}
						if (menu.getUrl() != null) {
							operateSet.add(menu.getUrl());
//							permissionSet.add(menu.getUrl());
						}
					}
				
				
		      SessionPermission sessionPermission = new SessionPermission();
				// 设置登录用户菜单列表
				sessionPermission.setMenuList(menuList);

				// 保存登录用户权限列表
				sessionPermission.setPermissionSet(operateSet);
				SessionUtils.setSessionPermission(request, sessionPermission);
	    	TrustedSsoAuthenticationToken ttoken = new TrustedSsoAuthenticationToken(rpcUser.getAccount());
		      SecurityUtils.getSubject().login(ttoken);
			
		}
	}

	/**
	 * 跳转登录
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void redirectLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (isAjaxRequest(request)) {
			responseJson(response, SsoResultCode.SSO_TOKEN_ERROR, "未登录或已超时");
		}
		else {
			SessionUtils.invalidate(request);

			String ssoLoginUrl = new StringBuilder().append(ssoServerUrl)
					.append("/login?backUrl=").append(URLEncoder.encode(getBackUrl(request), "utf-8")).toString();

			response.sendRedirect(ssoLoginUrl);
		}
	}

	/**
	 * 去除返回地址中的token参数
	 * @param request
	 * @return
	 */
	private String getRemoveTokenBackUrl(HttpServletRequest request) {
		String backUrl = getBackUrl(request);
		return backUrl.substring(0, backUrl.indexOf(SSO_TOKEN_NAME) - 1);
	}

	/**
	 * 返回地址
	 * @param request
	 * @return
	 */
	private String getBackUrl(HttpServletRequest request) {
		return new StringBuilder().append(request.getRequestURL())
				.append(request.getQueryString() == null ? "" : "?" + request.getQueryString()).toString();
	}
	
	public void setSsoAppCode(String ssoAppCode) {
		this.ssoAppCode = ssoAppCode;
	}
}