package cn.ssq.ticket.system.entity;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;



/** 
 * 行程单
 */
@TableName("t_travel")
public class Travel implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 行程单邮寄id
	 */
	@TableId(value = "Travel_id", type = IdType.AUTO)
	private Integer travelId;

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
	 * 配送方式
	 */
	private String sendWay ;
	/**
	 * 邮寄地址
	 */
	private String traAddress ;
	/**
	 * 行程单成本价
	 */
	private String traCostprice ;
	/**
	 * 行程单费
	 */
	private String traPrice ;
	/**
	 * 收件人
	 */
	private String receiver ;
	/**
	 * 联系号码
	 */
	private String contactPhone ;
	/**
	 * 邮寄方式
	 */
	private String postWay ;
	/**
	 * 邮件单号
	 */
	private String postNo ;
	/**
	 * 取件人
	 */
	private String takeBy ;
	/**
	 * 取件时间
	 */
	private String takeTime ;
	/**
	 * 打印方式
	 */
	private String printWay ;
	/**
	 * 打印状态
	 */
	private String printState ;    
	/**
	 * 处理状态
	 */
	private String state ;
	/**
	 * 备注
	 */
	private String remark ;
	/**
	 * 补单
	 */
	private String addTicket ;
	/**
	 * 行程单打印张数
	 */
	private String printCount ;

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
	 * 打印日期
	 */
	@TableField(exist = false)
	private String printStartDate ;  
	/**
	 * 打印日期
	 */
	@TableField(exist = false)
	private String printEndDate ;  

	/**
	 * 获取行程单邮寄id<p>
	 * @return  travelId  行程单邮寄id<br>
	 */
	
	public Integer getTravelId(){
		return travelId;
	}
	/**
	 * 设置行程单邮寄id<p>
	 * @param  travelId  行程单邮寄id<br>
	 */
	public void setTravelId(Integer travelId)
	{
		this.travelId = travelId;
	}
	
	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	/**
	 * 获取订单号<p>
	 * @return  orderNo  订单号<br>
	 */
	public String getOrderNo()
	{
		return orderNo;
	}
	/**
	 * 设置订单号<p>
	 * @param  orderNo  订单号<br>
	 */
	public void setOrderNo(String orderNo)
	{
		this.orderNo = orderNo;
	}
	/**
	 * 获取订单来源<p>
	 * @return  orderSource  订单来源<br>
	 */
	public String getOrderSource()
	{
		return orderSource;
	}
	/**
	 * 设置订单来源<p>
	 * @param  orderSource  订单来源<br>
	 */
	public void setOrderSource(String orderSource)
	{
		this.orderSource = orderSource;
	}
	/**
	 * 获取店铺<p>
	 * @return  orderShop  店铺<br>
	 */
	public String getOrderShop()
	{
		return orderShop;
	}
	/**
	 * 设置店铺<p>
	 * @param  orderShop  店铺<br>
	 */
	public void setOrderShop(String orderShop)
	{
		this.orderShop = orderShop;
	}
	/**
	 * 获取对应agent用户<p>
	 * @return  agentId  对应agent用户<br>
	 */

	public Integer getAgentId()
	{
		return agentId;
	}
	/**
	 * 设置对应agent用户<p>
	 * @param  agentId  对应agent用户<br>
	 */
	public void setAgentId(Integer agentId)
	{
		this.agentId = agentId;
	}
	/**
	 * 获取配送方式<p>
	 * @return  sendWay  配送方式<br>
	 */

	public String getSendWay()
	{
		return sendWay;
	}
	/**
	 * 设置配送方式<p>
	 * @param  sendWay  配送方式<br>
	 */
	public void setSendWay(String sendWay)
	{
		this.sendWay = sendWay;
	}
	/**
	 * 获取邮寄地址<p>
	 * @return  traAddress  邮寄地址<br>
	 */

	public String getTraAddress()
	{
		return traAddress;
	}
	/**
	 * 设置邮寄地址<p>
	 * @param  traAddress  邮寄地址<br>
	 */
	public void setTraAddress(String traAddress)
	{
		this.traAddress = traAddress;
	}
	/**
	 * 获取行程单成本价<p>
	 * @return  traCostprice  行程单成本价<br>
	 */

	public String getTraCostprice()
	{
		return traCostprice;
	}
	/**
	 * 设置行程单成本价<p>
	 * @param  traCostprice  行程单成本价<br>
	 */
	public void setTraCostprice(String traCostprice)
	{
		this.traCostprice = traCostprice;
	}
	/**
	 * 获取行程单费<p>
	 * @return  traPrice  行程单费<br>
	 */

	public String getTraPrice()
	{
		return traPrice;
	}
	/**
	 * 设置行程单费<p>
	 * @param  traPrice  行程单费<br>
	 */
	public void setTraPrice(String traPrice)
	{
		this.traPrice = StringUtils.trimToNull(traPrice);
	}
	/**
	 * 获取收件人<p>
	 * @return  receiver  收件人<br>
	 */

	public String getReceiver()
	{
		return receiver;
	}
	/**
	 * 设置收件人<p>
	 * @param  receiver  收件人<br>
	 */
	public void setReceiver(String receiver)
	{
		this.receiver = receiver;
	}
	/**
	 * 获取联系号码<p>
	 * @return  contactPhone  联系号码<br>
	 */

	public String getContactPhone()
	{
		return contactPhone;
	}
	/**
	 * 设置联系号码<p>
	 * @param  contactPhone  联系号码<br>
	 */
	public void setContactPhone(String contactPhone)
	{
		this.contactPhone = contactPhone;
	}
	/**
	 * 获取邮寄方式<p>
	 * @return  postWay  邮寄方式<br>
	 */

	public String getPostWay()
	{
		return postWay;
	}
	/**
	 * 设置邮寄方式<p>
	 * @param  postWay  邮寄方式<br>
	 */
	public void setPostWay(String postWay)
	{
		this.postWay = postWay;
	}
	/**
	 * 获取邮件单号<p>
	 * @return  postNo  邮件单号<br>
	 */

	public String getPostNo()
	{
		return postNo;
	}
	/**
	 * 设置邮件单号<p>
	 * @param  postNo  邮件单号<br>
	 */
	public void setPostNo(String postNo)
	{
		this.postNo = postNo;
	}
	/**
	 * 获取取件人<p>
	 * @return  takeBy  取件人<br>
	 */

	public String getTakeBy()
	{
		return takeBy;
	}
	/**
	 * 设置取件人<p>
	 * @param  takeBy  取件人<br>
	 */
	public void setTakeBy(String takeBy)
	{
		this.takeBy = takeBy;
	}
	/**
	 * 获取取件时间<p>
	 * @return  takeTime  取件时间<br>
	 */
	//	@Temporal(TemporalType.DATE)

	public String getTakeTime()
	{
		return takeTime;
	}
	/**
	 * 设置取件时间<p>
	 * @param  takeTime  取件时间<br>
	 */
	public void setTakeTime(String takeTime)
	{
		this.takeTime = takeTime;
	}
	/**
	 * 获取打印方式<p>
	 * @return  printWay  打印方式<br>
	 */

	public String getPrintWay()
	{
		return printWay;
	}
	/**
	 * 设置打印方式<p>
	 * @param  printWay  打印方式<br>
	 */
	public void setPrintWay(String printWay)
	{
		this.printWay = printWay;
	}
	/**
	 * 获取处理状态<p>
	 * @return  state  处理状态<br>
	 */

	public String getState()
	{
		return state;
	}
	/**
	 * 设置处理状态<p>
	 * @param  state  处理状态<br>
	 */
	public void setState(String state)
	{
		this.state = state;
	}
	/**
	 * 获取备注<p>
	 * @return  remark  备注<br>
	 */

	public String getRemark()
	{
		return remark;
	}
	/**
	 * 设置备注<p>
	 * @param  remark  备注<br>
	 */
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	/**
	 * 获取补单<p>
	 * @return  addTicket  补单<br>
	 */

	public String getAddTicket()
	{
		return addTicket;
	}
	/**
	 * 设置补单<p>
	 * @param  addTicket  补单<br>
	 */
	public void setAddTicket(String addTicket)
	{
		this.addTicket = addTicket;
	}
	/**
	 * 获取行程单打印张数<p>
	 * @return  printCount  行程单打印张数<br>
	 */

	public String getPrintCount()
	{
		return printCount;
	}
	/**
	 * 设置行程单打印张数<p>
	 * @param  printCount  行程单打印张数<br>
	 */
	public void setPrintCount(String printCount)
	{
		this.printCount = printCount;
	}
	/**
	 * 获取pnr<p>
	 * @return  pnr  pnr<br>
	 */

	public String getPnr()
	{
		return pnr;
	}
	/**
	 * 设置pnr<p>
	 * @param  pnr  pnr<br>
	 */
	public void setPnr(String pnr)
	{
		this.pnr = pnr;
	}
	/**
	 * 获取航班航空公司二字码<p>
	 * @return  airlineCode  航班航空公司二字码<br>
	 */

	public String getAirlineCode()
	{
		return airlineCode;
	}
	/**
	 * 设置航班航空公司二字码<p>
	 * @param  airlineCode  航班航空公司二字码<br>
	 */
	public void setAirlineCode(String airlineCode)
	{
		this.airlineCode = airlineCode;
	}
	/**
	 * 获取航班号<p>
	 * @return  flightNo  航班号<br>
	 */

	public String getFlightNo()
	{
		return flightNo;
	}
	/**
	 * 设置航班号<p>
	 * @param  flightNo  航班号<br>
	 */
	public void setFlightNo(String flightNo)
	{
		this.flightNo = flightNo;
	}
	/**
	 * 获取航班类型<p>
	 * @return  flightType  航班类型<br>
	 */

	public String getFlightType()
	{
		return flightType;
	}
	/**
	 * 设置航班类型<p>
	 * @param  flightType  航班类型<br>
	 */
	public void setFlightType(String flightType)
	{
		this.flightType = flightType;
	}
	/**
	 * 获取航班出发城市三字码<p>
	 * @return  depCityCode  航班出发城市三字码<br>
	 */

	public String getDepCityCode()
	{
		return depCityCode;
	}
	/**
	 * 设置航班出发城市三字码<p>
	 * @param  depCityCode  航班出发城市三字码<br>
	 */
	public void setDepCityCode(String depCityCode)
	{
		this.depCityCode = depCityCode;
	}
	/**
	 * 获取航班到达城市三字码<p>
	 * @return  arrCityCode  航班到达城市三字码<br>
	 */

	public String getArrCityCode()
	{
		return arrCityCode;
	}
	/**
	 * 设置航班到达城市三字码<p>
	 * @param  arrCityCode  航班到达城市三字码<br>
	 */
	public void setArrCityCode(String arrCityCode)
	{
		this.arrCityCode = arrCityCode;
	}
	/**
	 * 获取航班起飞日期<p>
	 * @return  flightDepDate  航班起飞日期<br>
	 */
	//	@Temporal(TemporalType.DATE)

	public String getFlightDepDate()
	{
		return flightDepDate;
	}
	/**
	 * 设置航班起飞日期<p>
	 * @param  flightDepDate  航班起飞日期<br>
	 */
	public void setFlightDepDate(String flightDepDate)
	{
		this.flightDepDate = flightDepDate;
	}
	/**
	 * 获取航班到达日期<p>
	 * @return  flightArrDate  航班到达日期<br>
	 */
	//	@Temporal(TemporalType.DATE)

	public String getFlightArrDate()
	{
		return flightArrDate;
	}
	/**
	 * 设置航班到达日期<p>
	 * @param  flightArrDate  航班到达日期<br>
	 */
	public void setFlightArrDate(String flightArrDate)
	{
		this.flightArrDate = flightArrDate;
	}
	/**
	 * 获取航班起飞时间<p>
	 * @return  depTime  航班起飞时间<br>
	 */

	public String getDepTime()
	{
		return depTime;
	}
	/**
	 * 设置航班起飞时间<p>
	 * @param  depTime  航班起飞时间<br>
	 */
	public void setDepTime(String depTime)
	{
		this.depTime = depTime;
	}
	/**
	 * 获取航班到达时间<p>
	 * @return  arrTime  航班到达时间<br>
	 */

	public String getArrTime()
	{
		return arrTime;
	}
	/**
	 * 设置航班到达时间<p>
	 * @param  arrTime  航班到达时间<br>
	 */
	public void setArrTime(String arrTime)
	{
		this.arrTime = arrTime;
	}
	/**
	 * 获取航班舱位代码<p>
	 * @return  cabin  航班舱位代码<br>
	 */

	public String getCabin()
	{
		return cabin;
	}
	/**
	 * 设置航班舱位代码<p>
	 * @param  cabin  航班舱位代码<br>
	 */
	public void setCabin(String cabin)
	{
		this.cabin = cabin;
	}
	/**
	 * 获取出票舱位<p>
	 * @return  printTicketCabin  出票舱位<br>
	 */

	public String getPrintTicketCabin()
	{
		return printTicketCabin;
	}
	/**
	 * 设置出票舱位<p>
	 * @param  printTicketCabin  出票舱位<br>
	 */
	public void setPrintTicketCabin(String printTicketCabin)
	{
		this.printTicketCabin = printTicketCabin;
	}
	/**
	 * 获取录入人<p>
	 * @return  createBy  录入人<br>
	 */

	public String getCreateBy()
	{
		return createBy;
	}
	/**
	 * 设置录入人<p>
	 * @param  createBy  录入人<br>
	 */
	public void setCreateBy(String createBy)
	{
		this.createBy = createBy;
	}
	/**
	 * 获取录入日期<p>
	 * @return  createDate  录入日期<br>
	 */

	public String getCreateDate()
	{
		return createDate;
	}
	/**
	 * 设置录入日期<p>
	 * @param  createDate  录入日期<br>
	 */
	public void setCreateDate(String createDate)
	{
		this.createDate = createDate;
	}
	/**
	 * 获取修改人<p>
	 * @return  modifyBy  修改人<br>
	 */

	public String getModifyBy()
	{
		return modifyBy;
	}
	/**
	 * 设置修改人<p>
	 * @param  modifyBy  修改人<br>
	 */
	public void setModifyBy(String modifyBy)
	{
		this.modifyBy = modifyBy;
	}
	/**
	 * 获取修改日期<p>
	 * @return  modifyDate  修改日期<br>
	 */

	public String getModifyDate()
	{
		return modifyDate;
	}
	/**
	 * 设置修改日期<p>
	 * @param  modifyDate  修改日期<br>
	 */
	public void setModifyDate(String modifyDate)
	{
		this.modifyDate = modifyDate;
	}

	public String getPrintState()
	{
		return this.printState;
	}
	public void setPrintState(String printState)
	{
		this.printState = printState;
	}

	public String getPrintStartDate()
	{
		return this.printStartDate;
	}
	public void setPrintStartDate(String printStartDate)
	{
		this.printStartDate = printStartDate;
	}

	public String getPrintEndDate()
	{
		return this.printEndDate;
	}
	public void setPrintEndDate(String printEndDate)
	{
		this.printEndDate = printEndDate;
	}
	@Override
	public String toString() {
		return "Travel [travelId=" + travelId + ", orderId=" + orderId
				+ ", orderNo=" + orderNo + ", orderSource=" + orderSource
				+ ", orderShop=" + orderShop + ", agentId=" + agentId
				+ ", sendWay=" + sendWay + ", traAddress=" + traAddress
				+ ", traCostprice=" + traCostprice + ", traPrice=" + traPrice
				+ ", receiver=" + receiver + ", contactPhone=" + contactPhone
				+ ", postWay=" + postWay + ", postNo=" + postNo + ", takeBy="
				+ takeBy + ", takeTime=" + takeTime + ", printWay=" + printWay
				+ ", printState=" + printState + ", state=" + state
				+ ", remark=" + remark + ", addTicket=" + addTicket
				+ ", printCount=" + printCount + ", pnr=" + pnr
				+ ", airlineCode=" + airlineCode + ", flightNo=" + flightNo
				+ ", flightType=" + flightType + ", depCityCode=" + depCityCode
				+ ", arrCityCode=" + arrCityCode + ", flightDepDate="
				+ flightDepDate + ", flightArrDate=" + flightArrDate
				+ ", depTime=" + depTime + ", arrTime=" + arrTime + ", cabin="
				+ cabin + ", printTicketCabin=" + printTicketCabin
				+ ", createBy=" + createBy + ", createDate=" + createDate
				+ ", modifyBy=" + modifyBy + ", modifyDate=" + modifyDate
				+ ", printStartDate=" + printStartDate + ", printEndDate="
				+ printEndDate + "]";
	}

}
