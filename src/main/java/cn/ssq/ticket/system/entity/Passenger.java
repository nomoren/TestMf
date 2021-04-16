package cn.ssq.ticket.system.entity;


import java.io.Serializable;





import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


/** 
 * 乘客
 */
@TableName("t_passenger")
public class Passenger implements Serializable{

	public Change getChange() {
		return change;
	}

	public void setChange(Change change) {
		this.change = change;
	}
	private static final long serialVersionUID = 1L;
	/**
	 * 乘客id
	 */
	@TableId(value = "passenger_id", type = IdType.AUTO)
	private Long passengerId ;
	/**
	 * 订单id
	 */
	private Long orderId;

	/**
	 * 订单号
	 */
	private String orderNo ;

	/**
	 * 订单来源
	 */
	private String orderSource ;
	/**
	 * 店铺
	 */
	private String orderShop ;
	/**
	 * 对应agent用户
	 */
	private Integer agentId = null;
	/**
	 * 乘机人姓名
	 */
	private String name ;
	/**
	 * 乘机人证件类型：0，身份证；1，护照；2，学生证；3，军人证；4，回乡证；5，台胞证；6，港澳台胞；7，国际海员；8，外国人永久居留证；9，其它证件
	 */
	private String certType ;
	/**
	 * 乘机人证件号码
	 */
	private String certNo ;
	/**
	 * 乘机人类型
	 */
	private String passengerType ;
	/**
	 * 乘机人生日
	 */
	private String birthday ;
	/**
	 * 常旅客卡号
	 */
	private String tripCardNo ;
	/**
	 * pnr
	 */
	private String pnr ;
	/**
	 * 航班航空公司二字码
	 */
	private String airlineCode ;
	/**
	 * 航班号
	 */
	private String flightNo ;
	/**
	 * 航班类型
	 */
	private String flightType ;
	/**
	 * 航班出发城市三字码
	 */
	private String depCityCode ;
	/**
	 * 航班到达城市三字码
	 */
	private String arrCityCode ;
	/**
	 * 航班起飞日期
	 */
	private String flightDepDate ;
	/**
	 * 航班到达日期
	 */
	private String flightArrDate ;
	/**
	 * 航班起飞时间
	 */
	private String depTime ;
	/**
	 * 航班到达时间
	 */
	private String arrTime ;
	/**
	 * 航班舱位代码
	 */
	private String cabin ;
	/**
	 * 出票舱位
	 */
	private String printTicketCabin ;
	/**
	 * 票号
	 */
	private String ticketNo ;
	/**
	 * 航班机建费
	 */
	private String fee ;
	/**
	 * 航班燃油税
	 */
	private String tax ;
	/**
	 * 销售票面价格
	 */
	private String ticketPrice ;
	/**
	 * 销售价格
	 */
	private String sellPrice ;
	/**
	 * 黑屏价
	 */
	private String blackPrice ;
	/**
	 * 采购票面价
	 */
	private String purchPrice ;
	/**
	 * 采购价返佣
	 */
	private String purchCommission ;
	/**
	 * 应付采购价
	 */
	private String payPurchPrice ;
	/**
	 * 政策来源
	 */
	private String policyFrom ;
	/**
	 * 政策类型
	 */
	private String policyType ;
	/**
	 * 返点
	 */
	private String backPoint ;
	/**
	 * 收款方式
	 */
	private String reciptWay ;
	/**
	 * 采购地
	 */
	private String purchPalse ;
	/**
	 * 实收金额
	 */
	private String actualPrice ;
	/**
	 * 利润调整
	 */
	private String profitAdjustment ;
	/**
	 * 实收款手续费
	 */
	private String actualPoundage ;
	/**
	 * 淘宝佣金
	 */
	private String tbCommission ;
	/**
	 * 保险金额
	 */
	private String insurePrice ;
	/**
	 * 保险份数
	 */
	private String insureCount ;
	/**
	 * 保险低价
	 */
	private String insureDj ;
	/**
	 * 保险返佣(去哪儿返给ETWIN)
	 */
	private String insureCommission ;
	/**
	 * 支付卡号
	 */
	private String payCard ;
	/**
	 * 行程单号
	 */
	private String traNo ;
	/**
	 * 保单号
	 */
	private String insNo ;
	/**
	 * 保单流水号
	 */
	private String insSerialNo ;
	/**
	 * C站订单号
	 */
	private String cOrderNo ;
	/**
	 * 状态
	 */
	private String status ;

	private String isAirRet;

	/**
	 * 是否录入过退票
	 */
	private String isImRet ;
	/**
	 * 是否录入过行程单
	 */
	private String isImTra ;
	/**
	 * 是否录入过改签/改签数
	 */
	private String isImChange ;
	/**
	 * 是否录入过保险
	 */
	private String isImInsute ;
	/**
	 * 客票状态
	 */
	private String ticketStatus;

	/**
	 * 出票单子单id
	 */
	private String cChildrenId;


	/**
	 * 取消pnr状态
	 */
	@TableField("XEPNR_STATUS")
	private String xePnrStatus ;



	public String getIsAirRet() {
		return isAirRet;
	}
	/**
	 * 取消pnr人
	 */
	@TableField("XEPNR_by")
	private String xePnrBy ;
	/**
	 * 取消pnr日期
	 */
	@TableField("XEPNR_DATE")
	private String xePnrDate ;
	/**
	 * 取消行程单状态
	 */
	private String cancelTraStatus ;
	/**
	 * 出票人
	 */
	private String printTicketBy ;
	/**
	 * 出票日期
	 */
	private String printTicketDate ;

	/**
	 * 录入人
	 */
	private String createBy ;
	/**
	 * 录入日期
	 */
	private String createDate ;
	/**
	 * 修改人
	 */
	private String modifyBy ;
	/**
	 * 修改日期
	 */
	private String modifyDate ;
	/**
	 * 退票对象
	 */
	@TableField(exist = false)
	private Refund refund;

	/**
	 * 改签对象
	 */
	@TableField(exist = false)
	private Change change;
	
	private String purchOrderNo;

	private String remarks;


	public void setIsAirRet(String isAirRet) {
		this.isAirRet = isAirRet;
	}

	public String getcChildrenId() {
		return cChildrenId;
	}

	public void setcChildrenId(String cChildrenId) {
		this.cChildrenId = cChildrenId;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}




	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}



	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getOrderShop() {
		return orderShop;
	}

	public void setOrderShop(String orderShop) {
		this.orderShop = orderShop;
	}

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getTripCardNo() {
		return tripCardNo;
	}

	public void setTripCardNo(String tripCardNo) {
		this.tripCardNo = tripCardNo;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getAirlineCode() {
		return airlineCode;
	}

	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getFlightType() {
		return flightType;
	}

	public void setFlightType(String flightType) {
		this.flightType = flightType;
	}

	public String getDepCityCode() {
		return depCityCode;
	}

	public void setDepCityCode(String depCityCode) {
		this.depCityCode = depCityCode;
	}

	public String getArrCityCode() {
		return arrCityCode;
	}

	public void setArrCityCode(String arrCityCode) {
		this.arrCityCode = arrCityCode;
	}

	public String getFlightDepDate() {
		return flightDepDate;
	}

	public void setFlightDepDate(String flightDepDate) {
		this.flightDepDate = flightDepDate;
	}

	public String getFlightArrDate() {
		return flightArrDate;
	}

	public void setFlightArrDate(String flightArrDate) {
		this.flightArrDate = flightArrDate;
	}

	public String getDepTime() {
		return depTime;
	}

	public void setDepTime(String depTime) {
		this.depTime = depTime;
	}

	public String getArrTime() {
		return arrTime;
	}

	public void setArrTime(String arrTime) {
		this.arrTime = arrTime;
	}

	public String getCabin() {
		return cabin;
	}

	public void setCabin(String cabin) {
		this.cabin = cabin;
	}

	public String getPrintTicketCabin() {
		return printTicketCabin;
	}

	public void setPrintTicketCabin(String printTicketCabin) {
		this.printTicketCabin = printTicketCabin;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(String ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	public String getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(String sellPrice) {
		this.sellPrice = sellPrice;
	}

	public String getBlackPrice() {
		return blackPrice;
	}

	public void setBlackPrice(String blackPrice) {
		this.blackPrice = blackPrice;
	}

	public String getPurchPrice() {
		return purchPrice;
	}

	public void setPurchPrice(String purchPrice) {
		this.purchPrice = purchPrice;
	}

	public String getPurchCommission() {
		return purchCommission;
	}

	public void setPurchCommission(String purchCommission) {
		this.purchCommission = purchCommission;
	}

	public String getPayPurchPrice() {
		return payPurchPrice;
	}

	public void setPayPurchPrice(String payPurchPrice) {
		this.payPurchPrice = payPurchPrice;
	}

	public String getPolicyFrom() {
		return policyFrom;
	}

	public void setPolicyFrom(String policyFrom) {
		this.policyFrom = policyFrom;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getBackPoint() {
		return backPoint;
	}

	public void setBackPoint(String backPoint) {
		this.backPoint = backPoint;
	}

	public String getReciptWay() {
		return reciptWay;
	}

	public void setReciptWay(String reciptWay) {
		this.reciptWay = reciptWay;
	}

	public String getPurchPalse() {
		return purchPalse;
	}

	public void setPurchPalse(String purchPalse) {
		this.purchPalse = purchPalse;
	}

	public String getActualPrice() {
		return actualPrice;
	}

	public void setActualPrice(String actualPrice) {
		this.actualPrice = actualPrice;
	}

	public String getProfitAdjustment() {
		return profitAdjustment;
	}

	public void setProfitAdjustment(String profitAdjustment) {
		this.profitAdjustment = profitAdjustment;
	}

	public String getActualPoundage() {
		return actualPoundage;
	}

	public void setActualPoundage(String actualPoundage) {
		this.actualPoundage = actualPoundage;
	}

	public String getTbCommission() {
		return tbCommission;
	}

	public void setTbCommission(String tbCommission) {
		this.tbCommission = tbCommission;
	}

	public String getInsurePrice() {
		return insurePrice;
	}

	public void setInsurePrice(String insurePrice) {
		this.insurePrice = insurePrice;
	}

	public String getInsureCount() {
		return insureCount;
	}

	public void setInsureCount(String insureCount) {
		this.insureCount = insureCount;
	}

	public String getInsureDj() {
		return insureDj;
	}

	public void setInsureDj(String insureDj) {
		this.insureDj = insureDj;
	}

	public String getInsureCommission() {
		return insureCommission;
	}

	public void setInsureCommission(String insureCommission) {
		this.insureCommission = insureCommission;
	}

	public String getPayCard() {
		return payCard;
	}

	public void setPayCard(String payCard) {
		this.payCard = payCard;
	}

	public String getTraNo() {
		return traNo;
	}

	public void setTraNo(String traNo) {
		this.traNo = traNo;
	}

	public String getInsNo() {
		return insNo;
	}

	public void setInsNo(String insNo) {
		this.insNo = insNo;
	}

	public String getInsSerialNo() {
		return insSerialNo;
	}

	public void setInsSerialNo(String insSerialNo) {
		this.insSerialNo = insSerialNo;
	}

	public String getcOrderNo() {
		return cOrderNo;
	}

	public void setcOrderNo(String cOrderNo) {
		this.cOrderNo = cOrderNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsImRet() {
		return isImRet;
	}

	public void setIsImRet(String isImRet) {
		this.isImRet = isImRet;
	}

	public String getIsImTra() {
		return isImTra;
	}

	public void setIsImTra(String isImTra) {
		this.isImTra = isImTra;
	}

	public String getIsImChange() {
		return isImChange;
	}

	public void setIsImChange(String isImChange) {
		this.isImChange = isImChange;
	}

	public String getIsImInsute() {
		return isImInsute;
	}

	public void setIsImInsute(String isImInsute) {
		this.isImInsute = isImInsute;
	}

	public String getXePnrStatus() {
		return xePnrStatus;
	}

	public void setXePnrStatus(String xePnrStatus) {
		this.xePnrStatus = xePnrStatus;
	}

	public String getXePnrBy() {
		return xePnrBy;
	}

	public void setXePnrBy(String xePnrBy) {
		this.xePnrBy = xePnrBy;
	}

	public String getXePnrDate() {
		return xePnrDate;
	}

	public void setXePnrDate(String xePnrDate) {
		this.xePnrDate = xePnrDate;
	}

	public String getCancelTraStatus() {
		return cancelTraStatus;
	}

	public void setCancelTraStatus(String cancelTraStatus) {
		this.cancelTraStatus = cancelTraStatus;
	}

	public String getPrintTicketBy() {
		return printTicketBy;
	}

	public void setPrintTicketBy(String printTicketBy) {
		this.printTicketBy = printTicketBy;
	}

	public String getPrintTicketDate() {
		return printTicketDate;
	}

	public void setPrintTicketDate(String printTicketDate) {
		this.printTicketDate = printTicketDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getPurchOrderNo() {
		return purchOrderNo;
	}

	public void setPurchOrderNo(String purchOrderNo) {
		this.purchOrderNo = purchOrderNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "Passenger [passengerId=" + passengerId + ", orderId=" + orderId
				+ ", orderNo=" + orderNo + ", orderSource=" + orderSource
				+ ", orderShop=" + orderShop + ", agentId=" + agentId
				+ ", name=" + name + ", certType=" + certType + ", certNo="
				+ certNo + ", passengerType=" + passengerType + ", birthday="
				+ birthday + ", tripCardNo=" + tripCardNo + ", pnr=" + pnr
				+ ", airlineCode=" + airlineCode + ", flightNo=" + flightNo
				+ ", flightType=" + flightType + ", depCityCode=" + depCityCode
				+ ", arrCityCode=" + arrCityCode + ", flightDepDate="
				+ flightDepDate + ", flightArrDate=" + flightArrDate
				+ ", depTime=" + depTime + ", arrTime=" + arrTime + ", cabin="
				+ cabin + ", printTicketCabin=" + printTicketCabin
				+ ", ticketNo=" + ticketNo + ", fee=" + fee + ", tax=" + tax
				+ ", ticketPrice=" + ticketPrice + ", sellPrice=" + sellPrice
				+ ", blackPrice=" + blackPrice + ", purchPrice=" + purchPrice
				+ ", purchCommission=" + purchCommission + ", payPurchPrice="
				+ payPurchPrice + ", policyFrom=" + policyFrom
				+ ", policyType=" + policyType + ", backPoint=" + backPoint
				+ ", reciptWay=" + reciptWay + ", purchPalse=" + purchPalse
				+ ", actualPrice=" + actualPrice + ", profitAdjustment="
				+ profitAdjustment + ", actualPoundage=" + actualPoundage
				+ ", tbCommission=" + tbCommission + ", insurePrice="
				+ insurePrice + ", insureCount=" + insureCount + ", insureDj="
				+ insureDj + ", insureCommission=" + insureCommission
				+ ", payCard=" + payCard + ", traNo=" + traNo + ", insNo="
				+ insNo + ", insSerialNo=" + insSerialNo + ", cOrderNo="
				+ cOrderNo + ", status=" + status + ", isAirRet=" + isAirRet
				+ ", isImRet=" + isImRet + ", isImTra=" + isImTra
				+ ", isImChange=" + isImChange + ", isImInsute=" + isImInsute
				+ ", ticketStatus=" + ticketStatus + ", cChildrenId="
				+ cChildrenId + ", xePnrStatus=" + xePnrStatus + ", xePnrBy="
				+ xePnrBy + ", xePnrDate=" + xePnrDate + ", cancelTraStatus="
				+ cancelTraStatus + ", printTicketBy=" + printTicketBy
				+ ", printTicketDate=" + printTicketDate + ", createBy="
				+ createBy + ", createDate=" + createDate + ", modifyBy="
				+ modifyBy + ", modifyDate=" + modifyDate + ", purchOrderNo="
				+ purchOrderNo + ", remarks=" + remarks + ", refund=" + refund
				+ "]";
	}






}
