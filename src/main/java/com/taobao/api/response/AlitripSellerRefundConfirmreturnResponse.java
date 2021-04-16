package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.confirmreturn response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerRefundConfirmreturnResponse extends TaobaoResponse {

	private static final long serialVersionUID = 8282584991637919543L;

	/** 
	 * 是否成功
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
