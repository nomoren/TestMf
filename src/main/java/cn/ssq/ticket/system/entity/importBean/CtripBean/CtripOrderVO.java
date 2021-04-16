package cn.ssq.ticket.system.entity.importBean.CtripBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 携程订单VO
 * @author Administrator
 *
 */
public class CtripOrderVO {
	/**
	 * 订单号
	 */
	private String OrderID;
	/**
	 * 出票单号
	 */
	private String IssueBillID;
	/**
	 * 订单状态          
	 */
	private String orderStatus;
	
	private String airlineRecodeNo;
	
	/**
	 * 国际/国内         N：国内I：国际
	 */
	private String flightClass;
	
	/**
	 * 订单类型           S：单程D：往返
	 */
	private String flightWay;
	
	/**
	 * 下单时间
	 */
	private String orderDateTime;
	
	/**
	 * 飞行机构
	 */
	private String flightAgency;
	
	/**
	 * 航程简述
	 */
	private String orderDesc;
	
	/**
	 * 送票费
	 */
	private String sendTicketFee;
	
	/**
	 * 订单来源
	 */
	private String bookingChannel;
	
	/**
	 * 订单待出票时间
	 */
	private String tBookingDateTime;
	
	/**
	 * 联系人姓名
	 */
	private String contactName;
	
	/**
	 * 联系电话
	 */
	private String contactTel;
	/**
	 * 政策号,携程内部政策编号
	 */
	private String policyID;
	/**
	 * 政策代码
	 */
	private String policyCode;
	
	/**
	 * 产品类型
	 */
	private String tricType;
	
	private String lastPrintTicketTime;
	
	/**
	 * 是否申请取消
	 */
	private String cancelIssueStatus;
	
	
	
	public String getCancelIssueStatus() {
		return cancelIssueStatus;
	}

	public void setCancelIssueStatus(String cancelIssueStatus) {
		this.cancelIssueStatus = cancelIssueStatus;
	}

	public String getTricType() {
		return tricType;
	}

	public void setTricType(String tricType) {
		this.tricType = tricType;
	}

	public String getLastPrintTicketTime() {
		return lastPrintTicketTime;
	}

	public void setLastPrintTicketTime(String lastPrintTicketTime) {
		this.lastPrintTicketTime = lastPrintTicketTime;
	}

	public String getPolicyID() {
		return policyID;
	}

	public void setPolicyID(String policyID) {
		this.policyID = policyID;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

	/**
	 * 配送方式
	 * AIR  机场自取
	 * EMS  EMS邮递
	 * GET  市内自取
	 * PJN  不要报销凭证
	 * PJS  邮寄行程单
	 * SND  市内配送
	 * 空表示不需要配送
	 */
	private String getTicketWay;
	
	/**
	 * 配送地址
	 */
	private String sendTicketAddr;
	
	/**
	 * 乘机人信息
	 */
	private List<CtripPassengerVO> passengerList = new ArrayList<CtripPassengerVO>();
	
	/**
	 * 航段信息
	 */
	private List<CtripFlightVO> flightList = new ArrayList<CtripFlightVO>();
	
	private String policyRemark;
	
	public String getPolicyRemark() {
		return policyRemark;
	}

	public void setPolicyRemark(String policyRemark) {
		this.policyRemark = policyRemark;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getFlightClass() {
		return flightClass;
	}

	public void setFlightClass(String flightClass) {
		this.flightClass = flightClass;
	}

	public String getFlightWay() {
		return flightWay;
	}

	public void setFlightWay(String flightWay) {
		this.flightWay = flightWay;
	}

	public String getOrderDateTime() {
		return orderDateTime;
	}

	public void setOrderDateTime(String orderDateTime) {
		this.orderDateTime = orderDateTime;
	}

	public String getFlightAgency() {
		return flightAgency;
	}

	public void setFlightAgency(String flightAgency) {
		this.flightAgency = flightAgency;
	}

	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	public String getSendTicketFee() {
		return sendTicketFee;
	}

	public void setSendTicketFee(String sendTicketFee) {
		this.sendTicketFee = sendTicketFee;
	}

	public String getBookingChannel() {
		return bookingChannel;
	}

	public void setBookingChannel(String bookingChannel) {
		this.bookingChannel = bookingChannel;
	}

	public String gettBookingDateTime() {
		return tBookingDateTime;
	}

	public void settBookingDateTime(String tBookingDateTime) {
		this.tBookingDateTime = tBookingDateTime;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getGetTicketWay() {
		return getTicketWay;
	}

	public void setGetTicketWay(String getTicketWay) {
		this.getTicketWay = getTicketWay;
	}

	public String getSendTicketAddr() {
		return sendTicketAddr;
	}

	public void setSendTicketAddr(String sendTicketAddr) {
		this.sendTicketAddr = sendTicketAddr;
	}

	public List<CtripPassengerVO> getPassengerList() {
		return passengerList;
	}

	public void setPassengerList(List<CtripPassengerVO> passengerList) {
		this.passengerList = passengerList;
	}

	public List<CtripFlightVO> getFlightList() {
		return flightList;
	}

	public void setFlightList(List<CtripFlightVO> flightList) {
		this.flightList = flightList;
	}
	
	public String getIssueBillID() {
		return IssueBillID;
	}

	public void setIssueBillID(String issueBillID) {
		IssueBillID = issueBillID;
	}

	public String getAirlineRecodeNo() {
		return airlineRecodeNo;
	}

	public void setAirlineRecodeNo(String airlineRecodeNo) {
		this.airlineRecodeNo = airlineRecodeNo;
	}

	@Override
	public String toString() {
		return "CtripOrderVO [OrderID=" + OrderID + ", IssueBillID="
				+ IssueBillID + ", orderStatus=" + orderStatus
				+ ", airlineRecodeNo=" + airlineRecodeNo + ", flightClass="
				+ flightClass + ", flightWay=" + flightWay + ", orderDateTime="
				+ orderDateTime + ", flightAgency=" + flightAgency
				+ ", orderDesc=" + orderDesc + ", sendTicketFee="
				+ sendTicketFee + ", bookingChannel=" + bookingChannel
				+ ", tBookingDateTime=" + tBookingDateTime + ", contactName="
				+ contactName + ", contactTel=" + contactTel + ", policyID="
				+ policyID + ", policyCode=" + policyCode
				+ ", lastPrintTicketTime=" + lastPrintTicketTime
				+ ", getTicketWay=" + getTicketWay + ", sendTicketAddr="
				+ sendTicketAddr + ", passengerList=" + passengerList
				+ ", flightList=" + flightList + ", policyRemark="
				+ policyRemark + "]";
	}

}
