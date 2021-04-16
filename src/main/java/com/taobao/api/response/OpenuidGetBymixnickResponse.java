package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.openuid.get.bymixnick response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenuidGetBymixnickResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6459635848416394378L;

	/** 
	 * OpenUID
	 */
	@ApiField("open_uid")
	private String openUid;


	public void setOpenUid(String openUid) {
		this.openUid = openUid;
	}
	public String getOpenUid( ) {
		return this.openUid;
	}
	


}
