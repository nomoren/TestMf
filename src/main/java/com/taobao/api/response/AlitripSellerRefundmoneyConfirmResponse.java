package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.refundmoney.confirm response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerRefundmoneyConfirmResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6862156375456788168L;

	/** 
	 * 是否成功确认
	 */
	@ApiField("is_success")
	private Boolean isSuccess;


	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public Boolean getIsSuccess( ) {
		return this.isSuccess;
	}
	


}
