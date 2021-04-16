package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSellerRefundConfirmreturnResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.confirmreturn request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSellerRefundConfirmreturnRequest extends BaseTaobaoRequest<AlitripSellerRefundConfirmreturnResponse> {
	
	

	/** 
	* 退票申请单
	 */
	private Long applyId;

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public Long getApplyId() {
		return this.applyId;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.seller.refund.confirmreturn";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("apply_id", this.applyId);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerRefundConfirmreturnResponse> getResponseClass() {
		return AlitripSellerRefundConfirmreturnResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(applyId, "applyId");
	}
	

}