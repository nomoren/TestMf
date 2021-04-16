package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;
import com.taobao.api.TaobaoObject;
import java.util.Date;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerRefundGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 2616994831539923192L;

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
 * 退款航段信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class ReturnTicketSegment extends TaobaoObject {

	private static final long serialVersionUID = 2769571696355536645L;

	/**
		 * 到达机场三字码
		 */
		@ApiField("arr_airport_code")
		private String arrAirportCode;
		/**
		 * 到达城市
		 */
		@ApiField("arr_city")
		private String arrCity;
		/**
		 * 单个航段机场建设费用（分）
		 */
		@ApiField("build_fee")
		private Long buildFee;
		/**
		 * 出发机场三字码
		 */
		@ApiField("dep_airport_code")
		private String depAirportCode;
		/**
		 * 出发城市
		 */
		@ApiField("dep_city")
		private String depCity;
		/**
		 * 起飞时间
		 */
		@ApiField("dep_time")
		private Date depTime;
		/**
		 * 航班号
		 */
		@ApiField("flight_no")
		private String flightNo;
		/**
		 * 航段ID
		 */
		@ApiField("id")
		private Long id;
		/**
		 * 单个航段机场建燃油费用
		 */
		@ApiField("oil_tax")
		private Long oilTax;
		/**
		 * 改签手续费(分)
		 */
		@ApiField("refund_modify_fee")
		private Long refundModifyFee;
		/**
		 * 升舱手续费（分）
		 */
		@ApiField("refund_upgrade_fee")
		private Long refundUpgradeFee;
		/**
		 * 票状态是否挂起
		 */
		@ApiField("suspend")
		private Boolean suspend;
		/**
		 * 票号信息
		 */
		@ApiField("ticket_no")
		private String ticketNo;
		/**
		 * 去程0 回程1
		 */
		@ApiField("trip_type")
		private Long tripType;
	

	public String getArrAirportCode() {
			return this.arrAirportCode;
		}
		public void setArrAirportCode(String arrAirportCode) {
			this.arrAirportCode = arrAirportCode;
		}
		public String getArrCity() {
			return this.arrCity;
		}
		public void setArrCity(String arrCity) {
			this.arrCity = arrCity;
		}
		public Long getBuildFee() {
			return this.buildFee;
		}
		public void setBuildFee(Long buildFee) {
			this.buildFee = buildFee;
		}
		public String getDepAirportCode() {
			return this.depAirportCode;
		}
		public void setDepAirportCode(String depAirportCode) {
			this.depAirportCode = depAirportCode;
		}
		public String getDepCity() {
			return this.depCity;
		}
		public void setDepCity(String depCity) {
			this.depCity = depCity;
		}
		public Date getDepTime() {
			return this.depTime;
		}
		public void setDepTime(Date depTime) {
			this.depTime = depTime;
		}
		public String getFlightNo() {
			return this.flightNo;
		}
		public void setFlightNo(String flightNo) {
			this.flightNo = flightNo;
		}
		public Long getId() {
			return this.id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getOilTax() {
			return this.oilTax;
		}
		public void setOilTax(Long oilTax) {
			this.oilTax = oilTax;
		}
		public Long getRefundModifyFee() {
			return this.refundModifyFee;
		}
		public void setRefundModifyFee(Long refundModifyFee) {
			this.refundModifyFee = refundModifyFee;
		}
		public Long getRefundUpgradeFee() {
			return this.refundUpgradeFee;
		}
		public void setRefundUpgradeFee(Long refundUpgradeFee) {
			this.refundUpgradeFee = refundUpgradeFee;
		}
		public Boolean getSuspend() {
			return this.suspend;
		}
		public void setSuspend(Boolean suspend) {
			this.suspend = suspend;
		}
		public String getTicketNo() {
			return this.ticketNo;
		}
		public void setTicketNo(String ticketNo) {
			this.ticketNo = ticketNo;
		}
		public Long getTripType() {
			return this.tripType;
		}
		public void setTripType(Long tripType) {
			this.tripType = tripType;
		}

}

	/**
 * 人的费用信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class ReturnApplyPassenge extends TaobaoObject {

	private static final long serialVersionUID = 1823358176387522935L;

	/**
		 * 优惠后票面价
		 */
		@ApiField("discount_ticket_price")
		private Long discountTicketPrice;
		/**
		 * 人ID
		 */
		@ApiField("id")
		private Long id;
		/**
		 * 乘机人姓名
		 */
		@ApiField("passenger_name")
		private String passengerName;
		/**
		 * 乘机人类型
		 */
		@ApiField("passenger_type")
		private Long passengerType;
		/**
		 * 退款手续费
		 */
		@ApiField("refund_fee")
		private Long refundFee;
		/**
		 * 退款金额
		 */
		@ApiField("refund_money")
		private Long refundMoney;
		/**
		 * 退款航段信息
		 */
		@ApiListField("return_ticket_segment")
		@ApiField("return_ticket_segment")
		private List<ReturnTicketSegment> returnTicketSegment;
		/**
		 * 票价信息(分)
		 */
		@ApiField("ticket_price")
		private Long ticketPrice;
		/**
		 * 优惠券金额
		 */
		@ApiField("voucher_price")
		private Long voucherPrice;
	

	public Long getDiscountTicketPrice() {
			return this.discountTicketPrice;
		}
		public void setDiscountTicketPrice(Long discountTicketPrice) {
			this.discountTicketPrice = discountTicketPrice;
		}
		public Long getId() {
			return this.id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getPassengerName() {
			return this.passengerName;
		}
		public void setPassengerName(String passengerName) {
			this.passengerName = passengerName;
		}
		public Long getPassengerType() {
			return this.passengerType;
		}
		public void setPassengerType(Long passengerType) {
			this.passengerType = passengerType;
		}
		public Long getRefundFee() {
			return this.refundFee;
		}
		public void setRefundFee(Long refundFee) {
			this.refundFee = refundFee;
		}
		public Long getRefundMoney() {
			return this.refundMoney;
		}
		public void setRefundMoney(Long refundMoney) {
			this.refundMoney = refundMoney;
		}
		public List<ReturnTicketSegment> getReturnTicketSegment() {
			return this.returnTicketSegment;
		}
		public void setReturnTicketSegment(List<ReturnTicketSegment> returnTicketSegment) {
			this.returnTicketSegment = returnTicketSegment;
		}
		public Long getTicketPrice() {
			return this.ticketPrice;
		}
		public void setTicketPrice(Long ticketPrice) {
			this.ticketPrice = ticketPrice;
		}
		public Long getVoucherPrice() {
			return this.voucherPrice;
		}
		public void setVoucherPrice(Long voucherPrice) {
			this.voucherPrice = voucherPrice;
		}

}

	/**
 * 申请单详情
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class ReturnTicketDetail extends TaobaoObject {

	private static final long serialVersionUID = 2718534986859249736L;

	/**
		 * 申请单ID
		 */
		@ApiField("apply_id")
		private Long applyId;
		/**
		 * 退票原因
		 */
		@ApiField("apply_reason_type")
		private Long applyReasonType;
		/**
		 * 退票申请时间
		 */
		@ApiField("apply_time")
		private Date applyTime;
		/**
		 * creditMoney
		 */
		@ApiField("credit_money")
		private Long creditMoney;
		/**
		 * 退票成功时间
		 */
		@ApiField("first_process_time")
		private Date firstProcessTime;
		/**
		 * 订单号
		 */
		@ApiField("order_id")
		private Long orderId;
		/**
		 * 退款成功时间
		 */
		@ApiField("pay_success_time")
		private Date paySuccessTime;
		/**
		 * 申退原因
		 */
		@ApiField("reason")
		private String reason;
		/**
		 * 退款手续费（分）
		 */
		@ApiField("refund_fee")
		private Long refundFee;
		/**
		 * 退款金额（退给买家的钱）（分）
		 */
		@ApiField("refund_money")
		private Long refundMoney;
		/**
		 * 人的费用信息
		 */
		@ApiListField("return_apply_passenge")
		@ApiField("return_apply_passenge")
		private List<ReturnApplyPassenge> returnApplyPassenge;
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
		public Long getApplyReasonType() {
			return this.applyReasonType;
		}
		public void setApplyReasonType(Long applyReasonType) {
			this.applyReasonType = applyReasonType;
		}
		public Date getApplyTime() {
			return this.applyTime;
		}
		public void setApplyTime(Date applyTime) {
			this.applyTime = applyTime;
		}
		public Long getCreditMoney() {
			return this.creditMoney;
		}
		public void setCreditMoney(Long creditMoney) {
			this.creditMoney = creditMoney;
		}
		public Date getFirstProcessTime() {
			return this.firstProcessTime;
		}
		public void setFirstProcessTime(Date firstProcessTime) {
			this.firstProcessTime = firstProcessTime;
		}
		public Long getOrderId() {
			return this.orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		public Date getPaySuccessTime() {
			return this.paySuccessTime;
		}
		public void setPaySuccessTime(Date paySuccessTime) {
			this.paySuccessTime = paySuccessTime;
		}
		public String getReason() {
			return this.reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		public Long getRefundFee() {
			return this.refundFee;
		}
		public void setRefundFee(Long refundFee) {
			this.refundFee = refundFee;
		}
		public Long getRefundMoney() {
			return this.refundMoney;
		}
		public void setRefundMoney(Long refundMoney) {
			this.refundMoney = refundMoney;
		}
		public List<ReturnApplyPassenge> getReturnApplyPassenge() {
			return this.returnApplyPassenge;
		}
		public void setReturnApplyPassenge(List<ReturnApplyPassenge> returnApplyPassenge) {
			this.returnApplyPassenge = returnApplyPassenge;
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

	private static final long serialVersionUID = 2284641224711847442L;

	/**
		 * 系统自动生成
		 */
		@ApiField("errorCode")
		private String errorCode;
		/**
		 * 系统自动生成
		 */
		@ApiField("errorMsg")
		private String errorMsg;
		/**
		 * 申请单详情
		 */
		@ApiField("results")
		private ReturnTicketDetail results;
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
		public ReturnTicketDetail getResults() {
			return this.results;
		}
		public void setResults(ReturnTicketDetail results) {
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
