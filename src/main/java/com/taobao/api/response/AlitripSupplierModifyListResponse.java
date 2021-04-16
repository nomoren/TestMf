package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;
import com.taobao.api.TaobaoObject;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.supplier.modify.list response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSupplierModifyListResponse extends TaobaoResponse {

	private static final long serialVersionUID = 3299957323146983753L;

	/** 
	 * 改签信息列表
	 */
	@ApiListField("order_list")
	@ApiField("bb_sync_order_dto")
	private List<BbSyncOrderDto> orderList;


	public void setOrderList(List<BbSyncOrderDto> orderList) {
		this.orderList = orderList;
	}
	public List<BbSyncOrderDto> getOrderList( ) {
		return this.orderList;
	}
	
	/**
 * 改签后航班信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Flight extends TaobaoObject {

	private static final long serialVersionUID = 8878687398645752427L;

	/**
		 * 航空公司二字码
		 */
		@ApiField("air_line_code")
		private String airLineCode;
		/**
		 * 到达机场三字码
		 */
		@ApiField("arr_airport")
		private String arrAirport;
		/**
		 * 出发机场三字码
		 */
		@ApiField("dep_airport")
		private String depAirport;
		/**
		 * 到达日期
		 */
		@ApiField("dep_date")
		private String depDate;
		/**
		 * 航班号
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
 * 乘机人信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Passenger extends TaobaoObject {

	private static final long serialVersionUID = 7651324566473146785L;

	/**
		 * 证件类型
		 */
		@ApiField("cert_num")
		private String certNum;
		/**
		 * 证件号码
		 */
		@ApiField("cert_type")
		private String certType;
		/**
		 * 乘机人姓名
		 */
		@ApiField("passenger_name")
		private String passengerName;
		/**
		 * PNR编码
		 */
		@ApiField("pnr")
		private String pnr;
		/**
		 * 票号
		 */
		@ApiField("ticket_no")
		private String ticketNo;
	

	public String getCertNum() {
			return this.certNum;
		}
		public void setCertNum(String certNum) {
			this.certNum = certNum;
		}
		public String getCertType() {
			return this.certType;
		}
		public void setCertType(String certType) {
			this.certType = certType;
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
 * 改签信息列表
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class BbSyncOrderDto extends TaobaoObject {

	private static final long serialVersionUID = 1397459469926964985L;

	/**
		 * 申请单id
		 */
		@ApiField("apply_id")
		private Long applyId;
		/**
		 * 改签后舱位
		 */
		@ApiField("cabin")
		private String cabin;
		/**
		 * 改签前舱位
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
		 * 乘机人信息
		 */
		@ApiField("passenger")
		private Passenger passenger;
		/**
		 * 改签前PNR编码
		 */
		@ApiField("pnr")
		private String pnr;
		/**
		 * 改签前票号
		 */
		@ApiField("ticket_no")
		private String ticketNo;
	

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
		public Passenger getPassenger() {
			return this.passenger;
		}
		public void setPassenger(Passenger passenger) {
			this.passenger = passenger;
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



}
