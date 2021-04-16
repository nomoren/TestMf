package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;
import com.taobao.api.TaobaoObject;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.modify.list response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerModifyListResponse extends TaobaoResponse {

	private static final long serialVersionUID = 3159483925362288284L;

	/** 
	 * 改签订单列表
	 */
	@ApiListField("order_list")
	@ApiField("sync_order_do")
	private List<SyncOrderDo> orderList;

	/** 
	 * 查出总记录数
	 */
	@ApiField("total_item")
	private Long totalItem;


	public void setOrderList(List<SyncOrderDo> orderList) {
		this.orderList = orderList;
	}
	public List<SyncOrderDo> getOrderList( ) {
		return this.orderList;
	}

	public void setTotalItem(Long totalItem) {
		this.totalItem = totalItem;
	}
	public Long getTotalItem( ) {
		return this.totalItem;
	}
	
	/**
 * 改签后航班信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Flight extends TaobaoObject {

	private static final long serialVersionUID = 4348974438949897643L;

	/**
		 * 改签后航空公司二字码
		 */
		@ApiField("air_line_code")
		private String airLineCode;
		/**
		 * 改签后到达机场三字码
		 */
		@ApiField("arr_airport")
		private String arrAirport;
		/**
		 * 改签后出发机场三字码
		 */
		@ApiField("dep_airport")
		private String depAirport;
		/**
		 * 改签后出发时间
		 */
		@ApiField("dep_date")
		private String depDate;
		/**
		 * 改签后航班号
		 */
		@ApiField("flight_no")
		private String flightNo;
	

	public String getAirLineCode() {
			return this.airLineCode;
		}
		public void setAirLineCode(String airLineCode) {
			this.airLineCode = airLineCode;
		}
		public String getArrAirport() {
			return this.arrAirport;
		}
		public void setArrAirport(String arrAirport) {
			this.arrAirport = arrAirport;
		}
		public String getDepAirport() {
			return this.depAirport;
		}
		public void setDepAirport(String depAirport) {
			this.depAirport = depAirport;
		}
		public String getDepDate() {
			return this.depDate;
		}
		public void setDepDate(String depDate) {
			this.depDate = depDate;
		}
		public String getFlightNo() {
			return this.flightNo;
		}
		public void setFlightNo(String flightNo) {
			this.flightNo = flightNo;
		}

}

	/**
 * 乘客信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Passenger extends TaobaoObject {

	private static final long serialVersionUID = 6276843146478923669L;

	/**
		 * 乘客生日
		 */
		@ApiField("birthday")
		private String birthday;
		/**
		 * 乘客证件号码
		 */
		@ApiField("cert_num")
		private String certNum;
		/**
		 * 改签前的票号
		 */
		@ApiField("old_ticket_no")
		private String oldTicketNo;
		/**
		 * 乘客姓名
		 */
		@ApiField("passenger_name")
		private String passengerName;
		/**
		 * 改签前的pnr
		 */
		@ApiField("pnr")
		private String pnr;
		/**
		 * 改签后的票号
		 */
		@ApiField("ticket_no")
		private String ticketNo;
	

	public String getBirthday() {
			return this.birthday;
		}
		public void setBirthday(String birthday) {
			this.birthday = birthday;
		}
		public String getCertNum() {
			return this.certNum;
		}
		public void setCertNum(String certNum) {
			this.certNum = certNum;
		}
		public String getOldTicketNo() {
			return this.oldTicketNo;
		}
		public void setOldTicketNo(String oldTicketNo) {
			this.oldTicketNo = oldTicketNo;
		}
		public String getPassengerName() {
			return this.passengerName;
		}
		public void setPassengerName(String passengerName) {
			this.passengerName = passengerName;
		}
		public String getPnr() {
			return this.pnr;
		}
		public void setPnr(String pnr) {
			this.pnr = pnr;
		}
		public String getTicketNo() {
			return this.ticketNo;
		}
		public void setTicketNo(String ticketNo) {
			this.ticketNo = ticketNo;
		}

}

	/**
 * 改签订单列表
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class SyncOrderDo extends TaobaoObject {

	private static final long serialVersionUID = 1245194888937576623L;

	/**
		 * 申请单id
		 */
		@ApiField("apply_id")
		private Long applyId;
		/**
		 * 改签后的舱位
		 */
		@ApiField("cabin")
		private String cabin;
		/**
		 * 改签前的舱位
		 */
		@ApiField("last_cabin")
		private String lastCabin;
		/**
		 * 改签备注信息
		 */
		@ApiField("memo")
		private String memo;
		/**
		 * 改签后航班信息
		 */
		@ApiField("modify_after_flight")
		private Flight modifyAfterFlight;
		/**
		 * 改签前航班信息
		 */
		@ApiField("modify_before_flight")
		private Flight modifyBeforeFlight;
		/**
		 * 改签费(单位分)
		 */
		@ApiField("modify_fee")
		private Long modifyFee;
		/**
		 * 订单id
		 */
		@ApiField("order_id")
		private Long orderId;
		/**
		 * 乘客信息
		 */
		@ApiField("passenger")
		private Passenger passenger;
		/**
		 * 申请单状态。1：初始状态，2：已改签成功，3：已拒绝，4：未付款（已回填退票费），5：已付款
		 */
		@ApiField("status")
		private Long status;
		/**
		 * 升舱费(单位分)
		 */
		@ApiField("upgrade_fee")
		private Long upgradeFee;
	

	public Long getApplyId() {
			return this.applyId;
		}
		public void setApplyId(Long applyId) {
			this.applyId = applyId;
		}
		public String getCabin() {
			return this.cabin;
		}
		public void setCabin(String cabin) {
			this.cabin = cabin;
		}
		public String getLastCabin() {
			return this.lastCabin;
		}
		public void setLastCabin(String lastCabin) {
			this.lastCabin = lastCabin;
		}
		public String getMemo() {
			return this.memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public Flight getModifyAfterFlight() {
			return this.modifyAfterFlight;
		}
		public void setModifyAfterFlight(Flight modifyAfterFlight) {
			this.modifyAfterFlight = modifyAfterFlight;
		}
		public Flight getModifyBeforeFlight() {
			return this.modifyBeforeFlight;
		}
		public void setModifyBeforeFlight(Flight modifyBeforeFlight) {
			this.modifyBeforeFlight = modifyBeforeFlight;
		}
		public Long getModifyFee() {
			return this.modifyFee;
		}
		public void setModifyFee(Long modifyFee) {
			this.modifyFee = modifyFee;
		}
		public Long getOrderId() {
			return this.orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		public Passenger getPassenger() {
			return this.passenger;
		}
		public void setPassenger(Passenger passenger) {
			this.passenger = passenger;
		}
		public Long getStatus() {
			return this.status;
		}
		public void setStatus(Long status) {
			this.status = status;
		}
		public Long getUpgradeFee() {
			return this.upgradeFee;
		}
		public void setUpgradeFee(Long upgradeFee) {
			this.upgradeFee = upgradeFee;
		}

}



}
