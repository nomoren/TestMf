package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.JipiaoAgentOrderBdetailResponse;

/**
 * TOP API: taobao.jipiao.agent.order.bdetail request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class JipiaoAgentOrderBdetailRequest extends BaseTaobaoRequest<JipiaoAgentOrderBdetailResponse> {
	
	

	/** 
	* 订单号
	 */
	private Long orderId;

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOrderId() {
		return this.orderId;
	}

	public String getApiMethodName() {
		return "taobao.jipiao.agent.order.bdetail";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("order_id", this.orderId);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<JipiaoAgentOrderBdetailResponse> getResponseClass() {
		return JipiaoAgentOrderBdetailResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(orderId, "orderId");
	}
	

}