package cn.ssq.ticket.system.entity;

import java.io.Serializable;

import cn.stylefeng.guns.core.common.annotion.ExcelField;

/**
 * 易宝退款账单
 * @author ME
 *
 */
public class YiBaoRefundCheckingDelete implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String bussinsName;
	
	private String refundDate;
	
	private String dealDate;
	
	private String dealMoney;
	
	private String startTicket;
	
	private String endTicket;
	
	private String refundMoney;
	
	private String bussinsNo; 
	
	private String result;

	private Long importId;
	
	private boolean isMerge;
	
	
	@ExcelField(title="收款方姓名      ", type = 0, align = 2, sort = 0)
	public String getBussinsName() {
		return bussinsName;
	}

	public void setBussinsName(String bussinsName) {
		this.bussinsName = bussinsName;
	}

	@ExcelField(title="退款请求时间   ", type = 0, align = 2, sort = 1)
	public String getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(String refundDate) {
		this.refundDate = refundDate;
	}

	@ExcelField(title="交易成功时间   ", type = 0, align = 2, sort = 2)
	public String getDealDate() {
		return dealDate;
	}

	public void setDealDate(String dealDate) {
		this.dealDate = dealDate;
	}

	@ExcelField(title="交易金额", type = 0, align = 2, sort = 3)
	public String getDealMoney() {
		return dealMoney;
	}

	public void setDealMoney(String dealMoney) {
		this.dealMoney = dealMoney;
	}

	@ExcelField(title="起始票号   ", type = 0, align = 2, sort = 4)
	public String getStartTicket() {
		return startTicket;
	}

	public void setStartTicket(String startTicket) {
		this.startTicket = startTicket;
	}

	@ExcelField(title="终止票号   ", type = 0, align = 2, sort = 5)
	public String getEndTicket() {
		return endTicket;
	}

	public void setEndTicket(String endTicket) {
		this.endTicket = endTicket;
	}

	@ExcelField(title="退款金额", type = 0, align = 2, sort = 6)
	public String getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(String refundMoney) {
		this.refundMoney = refundMoney;
	}

	@ExcelField(title="商户订单号      ", type = 0, align = 2, sort = 7)
	public String getBussinsNo() {
		return bussinsNo;
	}

	public void setBussinsNo(String bussinsNo) {
		this.bussinsNo = bussinsNo;
	}

	@ExcelField(title="对账结果        ", type = 1, align = 2, sort = 8)
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

	
	public boolean isMerge() {
		return isMerge;
	}

	public void setMerge(boolean isMerge) {
		this.isMerge = isMerge;
	}

	@Override
	public String toString() {
		return "YiBaoRefundChecking [bussinsName=" + bussinsName + ", refundDate=" + refundDate + ", dealDate="
				+ dealDate + ", dealMoney=" + dealMoney + ", startTicket=" + startTicket + ", endTicket=" + endTicket
				+ ", refundMoney=" + refundMoney + ", bussinsNo=" + bussinsNo + ", result=" + result + ", importId="
				+ importId + ", isMerge=" + isMerge + "]";
	}

	

}
