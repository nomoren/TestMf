package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.jipiao.agent.order.ticket response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class JipiaoAgentOrderTicketResponse extends TaobaoResponse {

	private static final long serialVersionUID = 1428574911657311691L;

	/** 
	 * 返回接口调用完成后，整个订单是否成功
	 */
	@ApiField("is_order_success")
	private Boolean isOrderSuccess;

	/** 
	 * 返回操作成功失败信息
	 */
	@ApiField("is_success")
	private Boolean isSuccess;

	/** 
	 * 返回回填票号操作成功失败信息
	 */
	@ApiField("is_ticket_success")
	private Boolean isTicketSuccess;


	public void setIsOrderSuccess(Boolean isOrderSuccess) {
		this.isOrderSuccess = isOrderSuccess;
	}
	public Boolean getIsOrderSuccess( ) {
		return this.isOrderSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public Boolean getIsSuccess( ) {
		return this.isSuccess;
	}

	public void setIsTicketSuccess(Boolean isTicketSuccess) {
		this.isTicketSuccess = isTicketSuccess;
	}
	public Boolean getIsTicketSuccess( ) {
		return this.isTicketSuccess;
	}
	


}
