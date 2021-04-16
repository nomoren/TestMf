package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import java.util.Date;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.buyer.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripBuyerGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4672852835659815545L;

	/** 
	 * 请求内容，如阿里小号
	 */
	@ApiField("content")
	private String content;

	/** 
	 * 有效期
	 */
	@ApiField("expires")
	private Date expires;


	public void setContent(String content) {
		this.content = content;
	}
	public String getContent( ) {
		return this.content;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}
	public Date getExpires( ) {
		return this.expires;
	}
	


}
