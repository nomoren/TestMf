package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.openuid.get.bytrade response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenuidGetBytradeResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6736912114321328962L;

	/** 
	 * 当前交易tid对应买家的openuid
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
