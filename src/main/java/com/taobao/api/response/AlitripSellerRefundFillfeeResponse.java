package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.fillfee response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerRefundFillfeeResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4434735134643947483L;

	/** 
	 * 结果
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
