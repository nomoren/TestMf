package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSellerRefundGetResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.get request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSellerRefundGetRequest extends BaseTaobaoRequest<AlitripSellerRefundGetResponse> {
	
	

	/** 
	* 申请单ID
	 */
	private Long applyId;

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public Long getApplyId() {
		return this.applyId;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.seller.refund.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("apply_id", this.applyId);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerRefundGetResponse> getResponseClass() {
		return AlitripSellerRefundGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(applyId, "applyId");
	}
	

}