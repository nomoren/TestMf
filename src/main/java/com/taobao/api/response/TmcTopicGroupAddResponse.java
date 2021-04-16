package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tmc.topic.group.add response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TmcTopicGroupAddResponse extends TaobaoResponse {

	private static final long serialVersionUID = 1411464634569625648L;

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
