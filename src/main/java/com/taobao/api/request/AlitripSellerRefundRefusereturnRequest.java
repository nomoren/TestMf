package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSellerRefundRefusereturnResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.refusereturn request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSellerRefundRefusereturnRequest extends BaseTaobaoRequest<AlitripSellerRefundRefusereturnResponse> {
	
	

	/** 
	* 申请单ID
	 */
	private Long applyId;

	/** 
	* 拒绝理由
	 */
	private String reason;

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public Long getApplyId() {
		return this.applyId;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return this.reason;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.seller.refund.refusereturn";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("apply_id", this.applyId);
		txtParams.put("reason", this.reason);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerRefundRefusereturnResponse> getResponseClass() {
		return AlitripSellerRefundRefusereturnResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(applyId, "applyId");
		RequestCheckUtils.checkNotEmpty(reason, "reason");
	}
	

}