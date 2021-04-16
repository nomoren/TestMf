package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import java.util.Date;


/**
 * 退订单列表返回对象
 *
 * @author top auto create
 * @since 1.0, null
 */
public class ReturnApplyDo extends TaobaoObject {

	private static final long serialVersionUID = 3768359619527746657L;

	/**
	 * 航线二字码
	 */
	@ApiField("airline_code")
	private String airlineCode;

	/**
	 * 退票提交时间
	 */
	@ApiField("apply_time")
	private Date applyTime;

	/**
	 * 到达机场三字码
	 */
	@ApiField("arr_airport_code")
	private String arrAirportCode;

	/**
	 * 舱位
	 */
	@ApiField("cabin")
	private String cabin;

	/**
	 * 出发机场三字码
	 */
	@ApiField("dep_airport_code")
	private String depAirportCode;

	/**
	 * 到达时间
	 */
	@ApiField("dep_time")
	private Date depTime;

	/**
	 * 退票成功时间
	 */
	@ApiField("first_process_time")
	private Date firstProcessTime;

	/**
	 * 航班号
	 */
	@ApiField("flight_no")
	private String flightNo;

	/**
	 * 数据项id
	 */
	@ApiField("id")
	private Long id;

	/**
	 * 
	 */
	@ApiField("is_voluntary")
	private Boolean isVoluntary;

	/**
	 * 订单号
	 */
	@ApiField("order_id")
	private Long orderId;

	/**
	 * 乘机人姓名
	 */
	@ApiField("passenger_name")
	private String passengerName;

	/**
	 * 退款成功时间
	 */
	@ApiField("pay_success_time")
	private Date paySuccessTime;

	/**
	 * 退票原因
	 */
	@ApiField("reason_content")
	private String reasonContent;

	/**
	 * 退票手续费（单位：元）
	 */
	@ApiField("refund_fee")
	private Long refundFee;

	/**
	 * 退款金额（单位：元）
	 */
	@ApiField("refund_money")
	private Long refundMoney;

	/**
	 * 
	 */
	@ApiField("refund_reason")
	private String refundReason;

	/**
	 * 退票状态，1：初始，2：接受，3：确认，4：失败，5：买家处理，6：卖家处理，7：等待小二回填，8：退款成功
	 */
	@ApiField("status")
	private Long status;

	/**
	 * 票号
	 */
	@ApiField("ticket_no")
	private String ticketNo;


	public String getAirlineCode() {
		return this.airlineCode;
	}
	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}

	public Date getApplyTime() {
		return this.applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public String getArrAirportCode() {
		return this.arrAirportCode;
	}
	public void setArrAirportCode(String arrAirportCode) {
		this.arrAirportCode = arrAirportCode;
	}

	public String getCabin() {
		return this.cabin;
	}
	public void setCabin(String cabin) {
		this.cabin = cabin;
	}

	public String getDepAirportCode() {
		return this.depAirportCode;
	}
	public void setDepAirportCode(String depAirportCode) {
		this.depAirportCode = depAirportCode;
	}

	public Date getDepTime() {
		return this.depTime;
	}
	public void setDepTime(Date depTime) {
		this.depTime = depTime;
	}

	public Date getFirstProcessTime() {
		return this.firstProcessTime;
	}
	public void setFirstProcessTime(Date firstProcessTime) {
		this.firstProcessTime = firstProcessTime;
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

	public Boolean getIsVoluntary() {
		return this.isVoluntary;
	}
	public void setIsVoluntary(Boolean isVoluntary) {
		this.isVoluntary = isVoluntary;
	}

	public Long getOrderId() {
		return this.orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getPassengerName() {
		return this.passengerName;
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public Date getPaySuccessTime() {
		return this.paySuccessTime;
	}
	public void setPaySuccessTime(Date paySuccessTime) {
		this.paySuccessTime = paySuccessTime;
	}

	public String getReasonContent() {
		return this.reasonContent;
	}
	public void setReasonContent(String reasonContent) {
		this.reasonContent = reasonContent;
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

	public String getRefundReason() {
		return this.refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public Long getStatus() {
		return this.status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}

	public String getTicketNo() {
		return this.ticketNo;
	}
	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

}
