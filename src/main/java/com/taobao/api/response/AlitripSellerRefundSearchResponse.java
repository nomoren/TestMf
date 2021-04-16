package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;
import com.taobao.api.TaobaoObject;
import java.util.Date;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.search response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerRefundSearchResponse extends TaobaoResponse {

	private static final long serialVersionUID = 5116953719241518555L;

	/** 
	 * 返回结果
	 */
	@ApiField("result")
	private ResultDo result;


	public void setResult(ResultDo result) {
		this.result = result;
	}
	public ResultDo getResult( ) {
		return this.result;
	}
	
	/**
 * ReturnTicketDo
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class ReturnTicketDo extends TaobaoObject {

	private static final long serialVersionUID = 8581225982475242818L;

	/**
		 * 申请单ID
		 */
		@ApiField("apply_id")
		private Long applyId;
		/**
		 * 申请时间
		 */
		@ApiField("apply_time")
		private Date applyTime;
		/**
		 * 订单号
		 */
		@ApiField("order_id")
		private Long orderId;
		/**
		 * 申请单状态（1初始 2接受 3确认 4失败 5买家处理 6卖家处理 7等待小二回填 8退款成功）
		 */
		@ApiField("status")
		private Long status;
	

	public Long getApplyId() {
			return this.applyId;
		}
		public void setApplyId(Long applyId) {
			this.applyId = applyId;
		}
		public Date getApplyTime() {
			return this.applyTime;
		}
		public void setApplyTime(Date applyTime) {
			this.applyTime = applyTime;
		}
		public Long getOrderId() {
			return this.orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		public Long getStatus() {
			return this.status;
		}
		public void setStatus(Long status) {
			this.status = status;
		}

}

	/**
 * 返回结果
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class ResultDo extends TaobaoObject {

	private static final long serialVersionUID = 1813548132679829634L;

	/**
		 * 错误码
		 */
		@ApiField("errorCode")
		private String errorCode;
		/**
		 * 错误信息
		 */
		@ApiField("errorMsg")
		private String errorMsg;
		/**
		 * ReturnTicketDo
		 */
		@ApiListField("results")
		@ApiField("return_ticket_do")
		private List<ReturnTicketDo> results;
		/**
		 * 调用是否成功
		 */
		@ApiField("success")
		private Boolean success;
	

	public String getErrorCode() {
			return this.errorCode;
		}
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		public String getErrorMsg() {
			return this.errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public List<ReturnTicketDo> getResults() {
			return this.results;
		}
		public void setResults(List<ReturnTicketDo> results) {
			this.results = results;
		}
		public Boolean getSuccess() {
			return this.success;
		}
		public void setSuccess(Boolean success) {
			this.success = success;
		}

}



}
