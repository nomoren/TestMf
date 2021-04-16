package cn.stylefeng.guns.core.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class TrustedSsoAuthenticationToken implements AuthenticationToken {

	 private String username;

	    public TrustedSsoAuthenticationToken(){}

	    public TrustedSsoAuthenticationToken(String username){
	        this.username = username;
	    }

	    public Object getPrincipal(){
	        return this.username;
	    }

	    public Object getCredentials() {
	  return null;
	    }

	    public void setUsername(String username){
	        this.username = username;
	    }

	    public String getUsername(){
	        return this.username;
	    }

	    public void clear(){
	        this.username = null;
	    }

	    public String toString(){
	        return "username="+this.username;
	    }

}
