package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.JipiaoAgentOrderTicketResponse;

/**
 * TOP API: taobao.jipiao.agent.order.ticket request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class JipiaoAgentOrderTicketRequest extends BaseTaobaoRequest<JipiaoAgentOrderTicketResponse> {
	
	

	/** 
	* 淘宝系统订单id
	 */
	private Long orderId;

	/** 
	* 成功订单参数：列表元素结构为——
1.航班号（注：是订单里的航班号，非实际承运航班号）;
2.旧乘机人姓名;
3.新乘机人姓名;
4.票号 （乘机人、航段对应的票号）
说明：
1.往返订单，2段航班对应1个票号的，需要2条success_info记录，分别对应去程、回程；
2.有时用户输入的乘机人姓名输入错误或者有生僻字，代理商必须输入新的名字以保证验真通过；即旧乘机人姓名和新乘机人姓名不需要变化时可以相同
	 */
	private String successInfo;

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOrderId() {
		return this.orderId;
	}

	public void setSuccessInfo(String successInfo) {
		this.successInfo = successInfo;
	}

	public String getSuccessInfo() {
		return this.successInfo;
	}

	public String getApiMethodName() {
		return "taobao.jipiao.agent.order.ticket";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("order_id", this.orderId);
		txtParams.put("success_info", this.successInfo);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<JipiaoAgentOrderTicketResponse> getResponseClass() {
		return JipiaoAgentOrderTicketResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(orderId, "orderId");
		RequestCheckUtils.checkNotEmpty(successInfo, "successInfo");
		RequestCheckUtils.checkMaxListSize(successInfo, 18, "successInfo");
	}
	

}