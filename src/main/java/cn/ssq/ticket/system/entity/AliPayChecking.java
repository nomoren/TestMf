package cn.ssq.ticket.system.entity;

import java.io.Serializable;

import cn.stylefeng.guns.core.common.annotion.ExcelField;

/**
 * 支付宝对账
 * @author ME
 *
 */
public class AliPayChecking implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String tradeNo;		// 交易流水号
	
	private String bussinsNo; 
	
	private String createDate;
	
	private String payDate;
	
	private String alterDate;
	
	private String tradeSource;
	
	private String type;
	
	private String counterparty;
	
	private String name;
	
	private double money;
	
	private String inOrOut;
	
	private String tradeStatus;
	
	private String serviceCharge;
	
	private String successRefund;

	private String remark;
	
	private String moneyStatus;
	
	private String result;
	
	private Long importId;

	@ExcelField(title="交易号                  ", type = 0, align = 2, sort = 0)
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@ExcelField(title="商家订单号               ", type = 0, align = 2, sort = 1)
	public String getBussinsNo() {
		return bussinsNo;
	}

	public void setBussinsNo(String bussinsNo) {
		this.bussinsNo = bussinsNo;
	}

	@ExcelField(title="交易创建时间              ", type = 0, align = 2, sort = 2)
	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	@ExcelField(title="付款时间                ", type = 0, align = 2, sort = 3)
	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	@ExcelField(title="最近修改时间              ", type = 0, align = 2, sort = 4)
	public String getAlterDate() {
		return alterDate;
	}

	public void setAlterDate(String alterDate) {
		this.alterDate = alterDate;
	}

	@ExcelField(title="交易来源地     ", type = 0, align = 2, sort = 5)
	public String getTradeSource() {
		return tradeSource;
	}

	public void setTradeSource(String tradeSource) {
		this.tradeSource = tradeSource;
	}

	@ExcelField(title="类型              ", type = 0, align = 2, sort = 6)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@ExcelField(title="交易对方            ", type = 0, align = 2, sort = 7)
	public String getCounterparty() {
		return counterparty;
	}

	public void setCounterparty(String counterparty) {
		this.counterparty = counterparty;
	}

	@ExcelField(title="商品名称                ", type = 0, align = 2, sort = 8)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ExcelField(title="金额（元）   ", type = 0, align = 2, sort = 9)
	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@ExcelField(title="收/支     ", type = 0, align = 2, sort = 10)
	public String getInOrOut() {
		return inOrOut;
	}

	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
	}
	
	@ExcelField(title="交易状态    ", type = 0, align = 2, sort = 12)
	public String getTradeStatus() {
		return tradeStatus;
	}
	
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	
	@ExcelField(title="服务费（元）   ", type = 0, align = 2, sort = 13)
	public String getServiceCharge() {
		return serviceCharge;
	}
	
	public void setServiceCharge(String serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	
	@ExcelField(title="成功退款（元）  ", type = 0, align = 2, sort = 14)
	public String getSuccessRefund() {
		return successRefund;
	}
	
	public void setSuccessRefund(String successRefund) {
		this.successRefund = successRefund;
	}

	@ExcelField(title="备注                  ", type = 0, align = 2, sort = 15)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@ExcelField(title="资金状态     ", type = 0, align = 2, sort = 16)
	public String getMoneyStatus() {
		return moneyStatus;
	}

	public void setMoneyStatus(String moneyStatus) {
		this.moneyStatus = moneyStatus;
	}

	@ExcelField(title="对账结果        ", type = 1, align = 2, sort = 17)
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Long getImportId() {
		return importId;
	}

	public void setImportId(Long importId) {
		this.importId = importId;
	}

	@Override
	public String toString() {
		return "AliPayChecking [tradeNo=" + tradeNo + ", bussinsNo="
				+ bussinsNo + ", createDate=" + createDate + ", payDate="
				+ payDate + ", alterDate=" + alterDate + ", tradeSource="
				+ tradeSource + ", type=" + type + ", counterparty="
				+ counterparty + ", name=" + name + ", money=" + money
				+ ", inOrOut=" + inOrOut + ", tradeStatus=" + tradeStatus
				+ ", serviceCharge=" + serviceCharge + ", successRefund="
				+ successRefund + ", remark=" + remark + ", moneyStatus="
				+ moneyStatus + ", result=" + result + ", importId=" + importId
				+ "]";
	}
}

