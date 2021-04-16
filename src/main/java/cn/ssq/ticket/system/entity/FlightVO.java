package cn.ssq.ticket.system.entity;

import java.io.Serializable;
import java.util.List;



/**
 * 航段
 */

public class FlightVO implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
     * 航班id
     */
    private Integer flightId ;

    /**
     * 订单id
     */
    private Long orderId ;
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
    private Integer agentId ;

    /**
     * 航段id（编号）
     */
    private String segmentId ;
    /**
     * 航段类型
     */
    private String segmentType ;

    /**
     * 航班航空公司二字码
     */
    private String airlineCode ;
    /**
     * 航班号
     */
    private String flightNo ;
    /**
     * 航班机型
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
     * 舱位数量
     */
    //@TableField(exist = false)
    private String cabinCount ;
    /**
     * 航班机建费
     */
    //@TableField(exist = false)
    private String fee ;
    /**
     * 航班燃油税
     */
    //@TableField(exist = false)
    private String tax ;
    /**
     * 舱位销售价格
     */
   // @TableField(exist = false)
    private String sellPrice ;
    /**
     * 价格有效时间
     */
   // @TableField(exist = false)
    private String priceEffectiveTime ;
    /**
     * 退票规定
     */
   // @TableField(exist = false)
    private String reFundRule ;
    /**
     * 改签规定
     */
   // @TableField(exist = false)
    private String changeRule ;
    

    /**
     * 经停城市
     */
    //@TableField(exist = false)
    private String midCityCode ;

    /**
     * 点击费
     */
   // @TableField(exist = false)
    private String clickPrice ;
    /**
     * 所属政策组
     */
   // @TableField(exist = false)
    private String policyGroup ;

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
     * 起飞时间 (日期+时间)
     */
    //@TableField(exist = false)
    private String depTimes;
    
    /**
     * 到达时间(日期+时间)
     */
    //@TableField(exist = false)
    private String arrTimes;
    
    private String flightChange;
    
    private String remark;
    
    private List<Passenger> passengers;
    
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFlightChange() {
		return flightChange;
	}
	public void setFlightChange(String flightChange) {
		this.flightChange = flightChange;
	}
	public List<Passenger> getPassengers() {
		return passengers;
	}
	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}
	public String getDepTimes() {
		return depTimes;
	}
	public void setDepTimes(String depTimes) {
		this.depTimes = depTimes;
	}
	public String getArrTimes() {
		return arrTimes;
	}
	public void setArrTimes(String arrTimes) {
		this.arrTimes = arrTimes;
	}
	public Integer getFlightId() {
		return flightId;
	}
	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
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
	public String getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(String segmentId) {
		this.segmentId = segmentId;
	}
	public String getSegmentType() {
		return segmentType;
	}
	public void setSegmentType(String segmentType) {
		this.segmentType = segmentType;
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
	public String getCabinCount() {
		return cabinCount;
	}
	public void setCabinCount(String cabinCount) {
		this.cabinCount = cabinCount;
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
	public String getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(String sellPrice) {
		this.sellPrice = sellPrice;
	}
	public String getPriceEffectiveTime() {
		return priceEffectiveTime;
	}
	public void setPriceEffectiveTime(String priceEffectiveTime) {
		this.priceEffectiveTime = priceEffectiveTime;
	}
	public String getReFundRule() {
		return reFundRule;
	}
	public void setReFundRule(String reFundRule) {
		this.reFundRule = reFundRule;
	}
	public String getChangeRule() {
		return changeRule;
	}
	public void setChangeRule(String changeRule) {
		this.changeRule = changeRule;
	}
	public String getMidCityCode() {
		return midCityCode;
	}
	public void setMidCityCode(String midCityCode) {
		this.midCityCode = midCityCode;
	}
	public String getClickPrice() {
		return clickPrice;
	}
	public void setClickPrice(String clickPrice) {
		this.clickPrice = clickPrice;
	}
	public String getPolicyGroup() {
		return policyGroup;
	}
	public void setPolicyGroup(String policyGroup) {
		this.policyGroup = policyGroup;
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
	@Override
	public String toString() {
		return "FlightVO [flightId=" + flightId + ", orderId=" + orderId
				+ ", orderNo=" + orderNo + ", orderSource=" + orderSource
				+ ", orderShop=" + orderShop + ", agentId=" + agentId
				+ ", segmentId=" + segmentId + ", segmentType=" + segmentType
				+ ", airlineCode=" + airlineCode + ", flightNo=" + flightNo
				+ ", flightType=" + flightType + ", depCityCode=" + depCityCode
				+ ", arrCityCode=" + arrCityCode + ", flightDepDate="
				+ flightDepDate + ", flightArrDate=" + flightArrDate
				+ ", depTime=" + depTime + ", arrTime=" + arrTime + ", cabin="
				+ cabin + ", printTicketCabin=" + printTicketCabin
				+ ", cabinCount=" + cabinCount + ", fee=" + fee + ", tax="
				+ tax + ", sellPrice=" + sellPrice + ", priceEffectiveTime="
				+ priceEffectiveTime + ", reFundRule=" + reFundRule
				+ ", changeRule=" + changeRule + ", midCityCode=" + midCityCode
				+ ", clickPrice=" + clickPrice + ", policyGroup=" + policyGroup
				+ ", createBy=" + createBy + ", createDate=" + createDate
				+ ", modifyBy=" + modifyBy + ", modifyDate=" + modifyDate
				+ ", depTimes=" + depTimes + ", arrTimes=" + arrTimes
				+ ", flightChange=" + flightChange + ", passengers="
				+ passengers + "]";
	}
}
