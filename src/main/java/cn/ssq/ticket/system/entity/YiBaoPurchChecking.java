package cn.ssq.ticket.system.entity;

import java.io.Serializable;

import cn.stylefeng.guns.core.common.annotion.ExcelField;

/**
 * 易宝支付账单
 * @author ME
 *
 */
public class YiBaoPurchChecking implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String tradeNo;		// 交易流水号
	
	private String bussinsNo; 
	
	private String payeeNo;
	
	private String bussinsName;
	
	private String commodityType;
	
	private String status;
	
	private String out;
	
	private String payType;
	
	private String date;
	
	private String completeDate;
	
	private String orderName;
	
	private String orderDescribe;
	
	private String account;
	
	private String extendInfo;
	
	private String errorCode;
	
	private String errorInfo;
	
	private String startTicket;
	
	private Long importId;

	private String result;

	@ExcelField(title="商户订单号", type = 0, align = 2, sort = 1)
	public String getBussinsNo() {
		return bussinsNo;
	}

	public void setBussinsNo(String bussinsNo) {
		this.bussinsNo = bussinsNo;
	}
	
	@ExcelField(title="易宝流水号           ", type = 0, align = 2, sort = 2)
	public String getTradeNo() {
		return tradeNo;
	}
	
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	
	
	@ExcelField(title="收款方商编", type = 0, align = 2, sort = 3)
	public String getPayeeNo() {
		return payeeNo;
	}

	public void setPayeeNo(String payeeNo) {
		this.payeeNo = payeeNo;
	}

	@ExcelField(title="收款方名称", type = 0, align = 2, sort = 4)
	public String getBussinsName() {
		return bussinsName;
	}

	public void setBussinsName(String bussinsName) {
		this.bussinsName = bussinsName;
	}
	
	
	@ExcelField(title="商品类别", type = 0, align = 2, sort = 5)
	public String getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(String commodityType) {
		this.commodityType = commodityType;
	}

	@ExcelField(title="订单状态", type = 0, align = 2, sort = 6)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@ExcelField(title="订单金额", type = 0, align = 2, sort = 7)
	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	@ExcelField(title="支付类型", type = 0, align = 2, sort = 8)
	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	@ExcelField(title="下单时间", type = 0, align = 2, sort = 9)
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	@ExcelField(title="完成时间", type = 0, align = 2, sort = 10)
	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}

	@ExcelField(title="商品名称", type = 0, align = 2, sort = 11)
	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	@ExcelField(title="商品描述", type = 0, align = 2, sort = 12)
	public String getOrderDescribe() {
		return orderDescribe;
	}

	public void setOrderDescribe(String orderDescribe) {
		this.orderDescribe = orderDescribe;
	}

	@ExcelField(title="支付操作员", type = 0, align = 2, sort = 13)
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@ExcelField(title="扩展信息", type = 0, align = 2, sort = 14)
	public String getExtendInfo() {
		return extendInfo;
	}

	public void setExtendInfo(String extendInfo) {
		this.extendInfo = extendInfo;
	}

	@ExcelField(title="错误码", type = 0, align = 2, sort = 15)
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@ExcelField(title="错误信息", type = 0, align = 2, sort = 15)
	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	@ExcelField(title="起始票号     ", type = 0, align = 2, sort = 16)
	public String getStartTicket() {
		return startTicket;
	}

	public void setStartTicket(String startTicket) {
		this.startTicket = startTicket;
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
		return "YiBaoChecking [tradeNo=" + tradeNo + ", bussinsNo=" + bussinsNo + ", payeeNo=" + payeeNo
				+ ", bussinsName=" + bussinsName + ", commodityType=" + commodityType + ", status=" + status + ", out="
				+ out + ", payType=" + payType + ", date=" + date + ", completeDate=" + completeDate + ", orderName="
				+ orderName + ", orderDescribe=" + orderDescribe + ", account=" + account + ", extendInfo=" + extendInfo
				+ ", errorCode=" + errorCode + ", errorInfo=" + errorInfo + ", startTicket=" + startTicket
				+ ", importId=" + importId + ", result=" + result + "]";
	}
	
	
}
