package cn.ssq.ticket.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;


/** 
 * 订单改签
 */
@TableName("t_change")
public class Change implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 改签id
	 */
	@TableId(value = "change_id", type = IdType.AUTO)
	private Long changeId;

	/**
	 * 订单id
	 */
	private Long orderId;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 订单来源
	 */
	private String orderSource;
	/**
	 * 店铺
	 */
	private String orderShop;
	/**
	 * 对应agent用户
	 */
	private Integer agentId;

	/**
	 * 乘客id
	 */
	private Long passengerId;

	/**
	 * 改签乘客名
	 */
	private String passengerName;

	/**
	 * 改签票号
	 */
	private String tktNo;
	/**
	 * 入款账户
	 */
	private String incAccount;
	/**
	 * 收入改签费
	 */
	private String revenuePrice;
	/**
	 * 改签交易流水号
	 */
	private String tradeNo;
	/**
	 * 改签采购地
	 */
	private String pruchPalse;
	/**
	 * 支出账户（卡号）
	 */
	private String payAccount;
	/**
	 * 支出改签费
	 */
	private String payPrice;
	/**
	 * 改签差价
	 */
	private String diffPrice;
	/**
	 * 改签状态
	 */
	private String state;
	/**
	 * 原来pnr
	 */
	private String sPnr;
	/**
	 * 原来
	 */
	private String sAirlineCode;
	/**
	 * 原来航班号
	 */
	private String sFlightNo;
	/**
	 * 原来航班机型
	 */
	private String sFlightType;
	/**
	 * 原来航班出发城市三字码
	 */
	private String sDepCityCode;
	/**
	 * 原来航班到达城市三字码
	 */
	private String sArrCityCode;
	/**
	 * 原来航班日期
	 */
	private String sFlightDate;
	/**
	 * 原来航班舱位代码
	 */
	private String sCabin;
	/**
	 * 改签pnr
	 */
	private String pnr;
	/**
	 * 改签航班号
	 */
	private String flightNo;
	/**
	 * 改签舱位
	 */
	private String cabin;
	/**
	 * 改签航班日期
	 */
	private String flightDate;
	/**
	 * 新的c站订单号 NEW_C_ORDER_NO
	 */
	@TableField("NEW_C_ORDER_NO")
	private String newCOrderNo;

	/**
	 * 改签时间
	 */
	private String changeDate;

	/**
	 * 改签开始时间（查询用）
	 */
	@TableField(exist = false)
	private String changeDateStart;

	/**
	 * 改签结束时间（查询用）
	 */
	@TableField(exist = false)
	private String changeDateEnd;

	/**
	 * 改签备注
	 */
	private String remark;
	
	/**
	 * 升舱费
	 */
	private String upgradeFee;

	/**
	 * 录入人
	 */
	private String createBy;
	/**
	 * 录入日期
	 */
	private String createDate;
	/**
	 * 修改人
	 */
	private String modifyBy;
	/**
	 * 修改日期
	 */
	private String modifyDate;
	/**
	 * 支出流水号
	 */
	private String bussinsNo;
	
	private String processBy ;
	
		
	public String getBussinsNo() {
		return bussinsNo;
	}
	public void setBussinsNo(String bussinsNo) {
		this.bussinsNo = bussinsNo;
	}
	public String getProcessBy() {
		return processBy;
	}
	public void setProcessBy(String processBy) {
		this.processBy = processBy;
	}
	/**
	 * 获取改签id<p>
	 * @return  changeId  改签id<br>
	 */
	public long getChangeId()
	{
		return changeId;
	}
	/**
	 * 设置改签id<p>
	 * @param  changeId  改签id<br>
	 */
	public void setChangeId(Long changeId)
	{
		this.changeId = changeId;
	}
	/**
	 * 获取订单id<p>
	 * @return  orderId  订单id<br>
	 */
	public Long getOrderId()
	{
		return orderId;
	}
	/**
	 * 设置订单id<p>
	 * @param  orderId  订单id<br>
	 */
	public void setOrderId(Long orderId)
	{
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
	 * 获取乘客id<p>
	 * @return  passengerId  乘客id<br>
	 */
	public Long getPassengerId()
	{
		return passengerId;
	}
	/**
	 * 设置乘客id<p>
	 * @param  passengerId  乘客id<br>
	 */
	public void setPassengerId(Long passengerId)
	{
		this.passengerId = passengerId;
	}
	/**
	 * 获取改签票号<p>
	 * @return  tktNo  改签票号<br>
	 */
	public String getTktNo()
	{
		return tktNo;
	}
	/**
	 * 设置改签票号<p>
	 * @param  tktNo  改签票号<br>
	 */
	public void setTktNo(String tktNo)
	{
		this.tktNo = tktNo;
	}
	/**
	 * 获取入款账户<p>
	 * @return  incAccount  入款账户<br>
	 */
	public String getIncAccount()
	{
		return incAccount;
	}
	/**
	 * 设置入款账户<p>
	 * @param  incAccount  入款账户<br>
	 */
	public void setIncAccount(String incAccount)
	{
		this.incAccount = incAccount;
	}
	/**
	 * 获取收入改签费<p>
	 * @return  revenuePrice  收入改签费<br>
	 */
	public String getRevenuePrice()
	{
		return revenuePrice;
	}
	/**
	 * 设置收入改签费<p>
	 * @param  revenuePrice  收入改签费<br>
	 */
	public void setRevenuePrice(String revenuePrice)
	{
		this.revenuePrice = revenuePrice;
	}
	/**
	 * 获取改签交易流水号<p>
	 * @return  tradeNo  改签交易流水号<br>
	 */
	public String getTradeNo()
	{
		return tradeNo;
	}
	/**
	 * 设置改签交易流水号<p>
	 * @param  tradeNo  改签交易流水号<br>
	 */
	public void setTradeNo(String tradeNo)
	{
		this.tradeNo = tradeNo;
	}
	/**
	 * 获取改签采购地<p>
	 * @return  pruchPalse  改签采购地<br>
	 */
	public String getPruchPalse()
	{
		return pruchPalse;
	}
	/**
	 * 设置改签采购地<p>
	 * @param  pruchPalse  改签采购地<br>
	 */
	public void setPruchPalse(String pruchPalse)
	{
		this.pruchPalse = pruchPalse;
	}
	/**
	 * 获取支出账户（卡号）<p>
	 * @return  payAccount  支出账户（卡号）<br>
	 */
	public String getPayAccount()
	{
		return payAccount;
	}
	/**
	 * 设置支出账户（卡号）<p>
	 * @param  payAccount  支出账户（卡号）<br>
	 */
	public void setPayAccount(String payAccount)
	{
		this.payAccount = payAccount;
	}
	/**
	 * 获取支出改签费<p>
	 * @return  payPrice  支出改签费<br>
	 */
	public String getPayPrice()
	{
		return payPrice;
	}
	/**
	 * 设置支出改签费<p>
	 * @param  payPrice  支出改签费<br>
	 */
	public void setPayPrice(String payPrice)
	{
		this.payPrice = payPrice;
	}
	/**
	 * 获取改签差价<p>
	 * @return  diffPrice  改签差价<br>
	 */
	public String getDiffPrice()
	{
		return diffPrice;
	}
	/**
	 * 设置改签差价<p>
	 * @param  diffPrice  改签差价<br>
	 */
	public void setDiffPrice(String diffPrice)
	{
		this.diffPrice = diffPrice;
	}
	/**
	 * 获取改签状态<p>
	 * @return  state  改签状态<br>
	 */
	public String getState()
	{
		return state;
	}
	/**
	 * 设置改签状态<p>
	 * @param  state  改签状态<br>
	 */
	public void setState(String state)
	{
		this.state = state;
	}
	/**
	 * 获取原来pnr<p>
	 * @return  sPnr  原来pnr<br>
	 */
	public String getsPnr()
	{
		return sPnr;
	}
	/**
	 * 设置原来pnr<p>
	 * @param  sPnr  原来pnr<br>
	 */
	public void setsPnr(String sPnr)
	{
		this.sPnr = sPnr;
	}
	/**
	 * 获取原来pnr<p>
	 * @return  sAirlineCode  原来pnr<br>
	 */
	public String getsAirlineCode()
	{
		return sAirlineCode;
	}
	/**
	 * 设置原来pnr<p>
	 * @param  sAirlineCode  原来pnr<br>
	 */
	public void setsAirlineCode(String sAirlineCode)
	{
		this.sAirlineCode = sAirlineCode;
	}
	/**
	 * 获取原来航班号<p>
	 * @return  sFlightNo  原来航班号<br>
	 */
	public String getsFlightNo()
	{
		return sFlightNo;
	}
	/**
	 * 设置原来航班号<p>
	 * @param  sFlightNo  原来航班号<br>
	 */
	public void setsFlightNo(String sFlightNo)
	{
		this.sFlightNo = sFlightNo;
	}
	/**
	 * 获取原来航班机型<p>
	 * @return  sFlightType  原来航班机型<br>
	 */
	public String getsFlightType()
	{
		return sFlightType;
	}
	/**
	 * 设置原来航班机型<p>
	 * @param  sFlightType  原来航班机型<br>
	 */
	public void setsFlightType(String sFlightType)
	{
		this.sFlightType = sFlightType;
	}
	/**
	 * 获取原来航班出发城市三字码<p>
	 * @return  sDepCityCode  原来航班出发城市三字码<br>
	 */
	public String getsDepCityCode()
	{
		return sDepCityCode;
	}
	/**
	 * 设置原来航班出发城市三字码<p>
	 * @param  sDepCityCode  原来航班出发城市三字码<br>
	 */
	public void setsDepCityCode(String sDepCityCode)
	{
		this.sDepCityCode = sDepCityCode;
	}
	/**
	 * 获取原来航班到达城市三字码<p>
	 * @return  sArrCityCode  原来航班到达城市三字码<br>
	 */
	public String getsArrCityCode()
	{
		return sArrCityCode;
	}
	/**
	 * 设置原来航班到达城市三字码<p>
	 * @param  sArrCityCode  原来航班到达城市三字码<br>
	 */
	public void setsArrCityCode(String sArrCityCode)
	{
		this.sArrCityCode = sArrCityCode;
	}
	/**
	 * 获取原来航班日期<p>
	 * @return  sFlightDate  原来航班日期<br>
	 */
	public String getsFlightDate()
	{
		return sFlightDate;
	}
	/**
	 * 设置原来航班日期<p>
	 * @param  sFlightDate  原来航班日期<br>
	 */
	public void setsFlightDate(String sFlightDate)
	{
		this.sFlightDate = sFlightDate;
	}
	/**
	 * 获取原来航班舱位代码<p>
	 * @return  sCabin  原来航班舱位代码<br>
	 */
	public String getsCabin()
	{
		return sCabin;
	}
	/**
	 * 设置原来航班舱位代码<p>
	 * @param  sCabin  原来航班舱位代码<br>
	 */
	public void setsCabin(String sCabin)
	{
		this.sCabin = sCabin;
	}
	/**
	 * 获取改签pnr<p>
	 * @return  pnr  改签pnr<br>
	 */
	public String getPnr()
	{
		return pnr;
	}
	/**
	 * 设置改签pnr<p>
	 * @param  pnr  改签pnr<br>
	 */
	public void setPnr(String pnr)
	{
		this.pnr = pnr;
	}
	/**
	 * 获取改签航班号<p>
	 * @return  flightNo  改签航班号<br>
	 */
	public String getFlightNo()
	{
		return flightNo;
	}
	/**
	 * 设置改签航班号<p>
	 * @param  flightNo  改签航班号<br>
	 */
	public void setFlightNo(String flightNo)
	{
		this.flightNo = flightNo;
	}
	/**
	 * 获取改签舱位<p>
	 * @return  cabin  改签舱位<br>
	 */
	public String getCabin()
	{
		return cabin;
	}
	/**
	 * 设置改签舱位<p>
	 * @param  cabin  改签舱位<br>
	 */
	public void setCabin(String cabin)
	{
		this.cabin = cabin;
	}
	/**
	 * 获取改签航班日期<p>
	 * @return  flightDate  改签航班日期<br>
	 */
	public String getFlightDate()
	{
		return flightDate;
	}
	/**
	 * 设置改签航班日期<p>
	 * @param  flightDate  改签航班日期<br>
	 */
	public void setFlightDate(String flightDate)
	{
		this.flightDate = flightDate;
	}
	/**
	 * 获取新的c站订单号<p>
	 * @return  newCOrderNo  新的c站订单号<br>
	 */
	public String getNewCOrderNo()
	{
		return newCOrderNo;
	}
	/**
	 * 设置新的c站订单号<p>
	 * @param  newCOrderNo  新的c站订单号<br>
	 */
	public void setNewCOrderNo(String newCOrderNo)
	{
		this.newCOrderNo = newCOrderNo;
	}
	/**
	 * 获取改签备注<p>
	 * @return  remark  改签备注<br>
	 */
	public String getRemark()
	{
		return remark;
	}
	/**
	 * 设置改签备注<p>
	 * @param  remark  改签备注<br>
	 */
	public void setRemark(String remark)
	{
		this.remark = remark;
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
	public String getPassengerName()
	{
		return this.passengerName;
	}
	public void setPassengerName(String passengerName)
	{
		this.passengerName = passengerName;
	}
	public String getChangeDate()
	{
		return this.changeDate;
	}
	public void setChangeDate(String changeDate)
	{
		this.changeDate = changeDate;
	}
	public String getChangeDateStart()
	{
		return this.changeDateStart;
	}
	public void setChangeDateStart(String changeDateStart)
	{
		this.changeDateStart = changeDateStart;
	}
	public String getChangeDateEnd()
	{
		return this.changeDateEnd;
	}
	public void setChangeDateEnd(String changeDateEnd)
	{
		this.changeDateEnd = changeDateEnd;
	}
	
	public String getUpgradeFee() {
		return upgradeFee;
	}
	public void setUpgradeFee(String upgradeFee) {
		this.upgradeFee = upgradeFee;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	@Override
	public String toString() {
		return "Change [changeId=" + changeId + ", orderId=" + orderId + ", orderNo=" + orderNo + ", orderSource="
				+ orderSource + ", orderShop=" + orderShop + ", agentId=" + agentId + ", passengerId=" + passengerId
				+ ", passengerName=" + passengerName + ", tktNo=" + tktNo + ", incAccount=" + incAccount
				+ ", revenuePrice=" + revenuePrice + ", tradeNo=" + tradeNo + ", pruchPalse=" + pruchPalse
				+ ", payAccount=" + payAccount + ", payPrice=" + payPrice + ", diffPrice=" + diffPrice + ", state="
				+ state + ", sPnr=" + sPnr + ", sAirlineCode=" + sAirlineCode + ", sFlightNo=" + sFlightNo
				+ ", sFlightType=" + sFlightType + ", sDepCityCode=" + sDepCityCode + ", sArrCityCode=" + sArrCityCode
				+ ", sFlightDate=" + sFlightDate + ", sCabin=" + sCabin + ", pnr=" + pnr + ", flightNo=" + flightNo
				+ ", cabin=" + cabin + ", flightDate=" + flightDate + ", newCOrderNo=" + newCOrderNo + ", changeDate="
				+ changeDate + ", changeDateStart=" + changeDateStart + ", changeDateEnd=" + changeDateEnd + ", remark="
				+ remark + ", upgradeFee=" + upgradeFee + ", createBy=" + createBy + ", createDate=" + createDate
				+ ", modifyBy=" + modifyBy + ", modifyDate=" + modifyDate + ", bussinsNo=" + bussinsNo + ", processBy="
				+ processBy + "]";
	}

}
