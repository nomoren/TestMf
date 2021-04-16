package cn.ssq.ticket.system.entity;

import cn.stylefeng.guns.core.common.annotion.ExcelField;


/**
 * 采购单导入报表实体
 * @author Administrator
 *
 */
public class PurchImport  implements java.io.Serializable{

	private static final long serialVersionUID = -4515488858596864147L;

	private String orderNo;
	
	private String tradeNo;
	
	private String passengerName;
	
	private String purchaseSite;
	
	private String purchasePrice;
	
	private String payWay;
	
	private String remarks;
	
	private String result;
	
	private Long importId;
	
	
	
	public Long getImportId() {
		return importId;
	}


	public void setImportId(Long importId) {
		this.importId = importId;
	}

	@ExcelField(title="导入结果", type = 1, align = 2, sort = 7)
	public String getResult() {
		return result;
	}


	
	public void setResult(String result) {
		this.result = result;
	}


	@ExcelField(title="订单号      ", type = 0, align = 2, sort = 0)
	public String getOrderNo() {
		return orderNo;
	}
	
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	
	@ExcelField(title="交易流水号     ", type = 0, align = 2, sort = 1)
	public String getTradeNo() {
		return tradeNo;
	}


	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}


	@ExcelField(title="乘客名       ", type = 0, align = 2, sort = 2)
	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	@ExcelField(title="出票地", type = 0, align = 2, sort = 3)
	public String getPurchaseSite() {
		return purchaseSite;
	}

	public void setPurchaseSite(String purchaseSite) {
		this.purchaseSite = purchaseSite;
	}
	@ExcelField(title="支付价格", type = 0, align = 2, sort = 4)
	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	@ExcelField(title="支付方式", type = 0, align = 2, sort = 5)
	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	
	@ExcelField(title="备注       ", type = 0, align = 2, sort =6)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "ReportImport [orderNo=" + orderNo + ", passengerName="
				+ passengerName + ", purchaseSite=" + purchaseSite
				+ ", purchasePrice=" + purchasePrice + ", payWay=" + payWay
				+ ", remarks=" + remarks + ", result=" + result + ", importId="
				+ importId + "]";
	}

	
	
	 
	
}
