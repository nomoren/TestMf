package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSellerRefundmoneyConfirmResponse;

/**
 * TOP API: taobao.alitrip.seller.refundmoney.confirm request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSellerRefundmoneyConfirmRequest extends BaseTaobaoRequest<AlitripSellerRefundmoneyConfirmResponse> {
	
	

	/** 
	* 申请单id
	 */
	private Long applyId;

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public Long getApplyId() {
		return this.applyId;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.seller.refundmoney.confirm";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("apply_id", this.applyId);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerRefundmoneyConfirmResponse> getResponseClass() {
		return AlitripSellerRefundmoneyConfirmResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}