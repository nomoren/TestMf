package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tmc.auth.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TmcAuthGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 8686623117631885245L;

	/** 
	 * result
	 */
	@ApiField("result")
	private String result;


	public void setResult(String result) {
		this.result = result;
	}
	public String getResult( ) {
		return this.result;
	}
	


}
