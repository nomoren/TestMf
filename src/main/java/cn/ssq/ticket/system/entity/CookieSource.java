package cn.ssq.ticket.system.entity;

import java.io.Serializable;

public class CookieSource implements Serializable{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String source; 
	
	private String cookie;
	
	private String password ;
	
	private String username ;
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "CookieSource [source=" + source + ", cookie=" + cookie
				+ ", password=" + password + ", username=" + username + "]";
	}

	
}
