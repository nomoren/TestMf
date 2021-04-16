package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tmc.topic.group.delete response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TmcTopicGroupDeleteResponse extends TaobaoResponse {

	private static final long serialVersionUID = 1373218496123428343L;

	/** 
	 * true
	 */
	@ApiField("result")
	private Boolean result;


	public void setResult(Boolean result) {
		this.result = result;
	}
	public Boolean getResult( ) {
		return this.result;
	}
	


}
