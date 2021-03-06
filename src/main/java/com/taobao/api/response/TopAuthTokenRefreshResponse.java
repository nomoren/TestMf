package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.top.auth.token.refresh response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TopAuthTokenRefreshResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6465512958867479928L;

	/** 
	 * 返回的是json信息
	 */
	@ApiField("token_result")
	private String tokenResult;


	public void setTokenResult(String tokenResult) {
		this.tokenResult = tokenResult;
	}
	public String getTokenResult( ) {
		return this.tokenResult;
	}
	


}
