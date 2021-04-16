package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.JipiaoAgentOrderDetailResponse;

/**
 * TOP API: taobao.jipiao.agent.order.detail request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class JipiaoAgentOrderDetailRequest extends BaseTaobaoRequest<JipiaoAgentOrderDetailResponse> {
	
	

	/** 
	* 淘宝订单id列表，当前支持列表长度为1，即当前只支持单个订单详情查询
	 */
	private String orderIds;

	public void setOrderIds(String orderIds) {
		this.orderIds = orderIds;
	}

	public String getOrderIds() {
		return this.orderIds;
	}

	public String getApiMethodName() {
		return "taobao.jipiao.agent.order.detail";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("order_ids", this.orderIds);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<JipiaoAgentOrderDetailResponse> getResponseClass() {
		return JipiaoAgentOrderDetailResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(orderIds, "orderIds");
		RequestCheckUtils.checkMaxListSize(orderIds, 1, "orderIds");
	}
	

}