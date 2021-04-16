
package cn.stylefeng.guns.core.shiro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.util.StringUtils;

import cn.stylefeng.roses.core.util.HttpContext;
import cn.stylefeng.roses.core.util.SpringContextHolder;

import com.smart.sso.client.ApplicationPermission;
import com.smart.sso.client.SessionPermission;
import com.smart.sso.client.SessionUser;
import com.smart.sso.client.SessionUtils;
import com.smart.sso.client.SmartContainer;
import com.smart.sso.rpc.AuthenticationRpcService;
import com.smart.sso.rpc.RpcPermission;

public class SSORealm extends AuthorizingRealm {
	public SSORealm(){
        // 设置无需凭证，因为从sso认证后才会有用户名
setCredentialsMatcher(new AllowAllCredentialsMatcher());
        // 设置token为我们自定义的
setAuthenticationTokenClass(TrustedSsoAuthenticationToken.class);
}

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
//        UserAuthService shiroFactory = UserAuthServiceServiceImpl.me();
//        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
//        User user = shiroFactory.user(token.getUsername());
//        ShiroUser shiroUser = shiroFactory.shiroUser(user);
//        return shiroFactory.info(shiroUser, user, super.getName());
        
    	TrustedSsoAuthenticationToken token = (TrustedSsoAuthenticationToken)authcToken;
    	Object username = token.getPrincipal();
//      String username = token.getUsername();
      //不允许无username
      if(username==null){
                          // 自定义异常，于前端捕获
        throw new AccountException("用户名不允许为空!");
      }
      
      return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
//        SessionUser sessionUser = SessionUtils.getSessionUser(HttpContext.getRequest());
//    	String CREDENTIALS = "6bf968f0c7b39ddbb7c8aa836b74d092060ed9b85b620a7fb088ecc48c6b3efd696bbd820f74c14f66c80c86c557c4bfda51b8a3743d3d568cc5c08410b7bb6a";
//        return new SimpleAuthenticationInfo(sessionUser,CREDENTIALS, getName());
		// 登录用户名
//		request.setAttribute("userName", sessionUser.getAccount());
		
//		SessionPermission sessionPermission = SessionUtils.getSessionPermission(HttpContext.getRequest());
//		if (sessionPermission != null){
			// 登录用户当前应用的菜单
//			request.setAttribute("userMenus", sessionPermission.getMenuList());
//			// 登录用户当前应用的权限
//			request.setAttribute("userPermissions", sessionPermission.getPermissionSet());
			// 设置当前登录用户没有的权限，以便控制前台按钮的显示或者隐藏
//			model.addAttribute("sessionUserNoPermissions",
//					sessionPermission == null ? null : sessionPermission.getNoPermissions());
			
//			String credentials = user.getPassword();
//
//	        // 密码加盐处理
//	        String source = user.getSalt();
//	        ByteSource credentialsSalt = new Md5Hash(source);
//	        return new SimpleAuthenticationInfo(shiroUser, credentials, credentialsSalt, realmName);
//		}
//		return null;
    }

    /**
     * 权限认证
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        UserAuthService shiroFactory = UserAuthServiceServiceImpl.me();
//        ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
//        List<Long> roleList = shiroUser.getRoleList();
//
//        Set<String> permissionSet = new HashSet<>();
//        Set<String> roleNameSet = new HashSet<>();
//
//        for (Long roleId : roleList) {
//            List<String> permissions = shiroFactory.findPermissionsByRoleId(roleId);
//            if (permissions != null) {
//                for (String permission : permissions) {
//                    if (ToolUtil.isNotEmpty(permission)) {
//                        permissionSet.add(permission);
//                    }
//                }
//            }
//            String roleName = shiroFactory.findRoleNameByRoleId(roleId);
//            roleNameSet.add(roleName);
//        }
//
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        info.addStringPermissions(permissionSet);
//        info.addRoles(roleNameSet);
//        return info;
    	SessionPermission sessionPermission = SessionUtils.getSessionPermission(HttpContext.getRequest());
      Set<String> permissionSet = new HashSet<>();
      Set<String> roleNameSet = new HashSet<>();
      SessionUser sessionUser = SessionUtils.getSessionUser(HttpContext.getRequest());
      if(sessionUser.getAccount().equals("admin")){
        roleNameSet.add("admin");
      		}

		if (sessionPermission != null){
			for(String rp:sessionPermission.getPermissionSet()){
				permissionSet.add(rp);
			}
		}
		 SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
       info.addStringPermissions(permissionSet);
       info.addRoles(roleNameSet);
       return info;
       
       
   
    }

    /**
     * 设置认证加密方式
     */
    /*@Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher md5CredentialsMatcher = new HashedCredentialsMatcher();
//        md5CredentialsMatcher.setHashAlgorithmName(ShiroKit.hashAlgorithmName);
//        md5CredentialsMatcher.setHashIterations(ShiroKit.hashIterations);
        md5CredentialsMatcher.setHashAlgorithmName(Sha512Hash.ALGORITHM_NAME);
        super.setCredentialsMatcher(md5CredentialsMatcher);
    }*/
    
   /**
    * 清空用户关联权限认证，待下次使用时重新加载。
    * @param principal
    */
   public void clearCachedAuthorizationInfo(String principal){
     SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
     clearCachedAuthorizationInfo(principals);
   }

   /**
    * 清空所有关联认证
    */
   public void clearAllCachedAuthorizationInfo(){
     Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
     if (cache != null) {
       for (Object key : cache.keys()) {
         cache.remove(key);
       }
     }
   }
}
