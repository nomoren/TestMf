package cn.ssq.ticket.system.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_purchase")
public class Purchase implements Serializable{
	private static final long serialVersionUID = 1L;

	@TableId(value = "purchase_id", type = IdType.AUTO)
	private Long purchaseId;
	
	private Long orderId;		
	
	private String orderNo;		// 订单号
	
	private String cOrderNo;   //出票单号
	
	private String employeeName;		// 出票员
	
	private Double customerAmount;		// 客户支付金额
	
	private Double payAmount;		// 支付金额
	
	private Double profit;			//利润
	
	private Double realPay;       //实际支付
	
	private String payWay;		// 支付方式
	
	private String tradeNo;		// 交易流水号
	
	private String supplier;		// 出票供应商
	
	private String supplierNo;		// 供应商订单号
	
	private String remark;		// 备注
	
	private String flag;		// 标识
	
	private String passengerNames;		// 乘机人列表
	
	private String operateType;
	
	private Date printTicketDate; //出票日期
	
	private String orderSource;//来源
	
	private String orderShop;//店铺
	
	private String purchaseNo;//采购单号
	
    private String cAddDate;//平台订单创建日期
    
    private String flightDate;//航班日期
    
    private String businessNo;//商户订单号
    
    private String type;//0 正常报表   1差错报表 2改签报表s
    
    private Double returnMoney; //差错报表退款金额
    
    private String returnDate;  //差错报表退款日期
    
    private String newTicketNo;//新票号，针对改期类型
	
	@TableField(exist = false)
	private List<Passenger> passengerList;
	
	@TableField(exist = false)
	private Integer passengerNum;
	
	
	
	
	
	public String getNewTicketNo() {
		return newTicketNo;
	}
	
	public void setNewTicketNo(String newTicketNo) {
		this.newTicketNo = newTicketNo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getReturnMoney() {
		return returnMoney;
	}
	public void setReturnMoney(Double returnMoney) {
		this.returnMoney = returnMoney;
	}
	public String getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}
	public Double getRealPay() {
		return realPay;
	}
	public void setRealPay(Double realPay) {
		this.realPay = realPay;
	}
	public String getFlightDate() {
		return flightDate;
	}
	public void setFlightDate(String flightDate) {
		this.flightDate = flightDate;
	}
	public String getcAddDate() {
		return cAddDate;
	}
	public void setcAddDate(String cAddDate) {
		this.cAddDate = cAddDate;
	}
	public String getcOrderNo() {
		return cOrderNo;
	}
	public void setcOrderNo(String cOrderNo) {
		this.cOrderNo = cOrderNo;
	}
	public String getPurchaseNo() {
		return purchaseNo;
	}
	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}
	public Integer getPassengerNum() {
		return passengerNum;
	}
	public void setPassengerNum(Integer passengerNum) {
		this.passengerNum = passengerNum;
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
	public List<Passenger> getPassengerList() {
		return passengerList;
	}
	public void setPassengerList(List<Passenger> passengerList) {
		this.passengerList = passengerList;
	}
	public Long getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(Long purchaseId) {
		this.purchaseId = purchaseId;
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
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Double getCustomerAmount() {
		return customerAmount;
	}
	public void setCustomerAmount(Double customerAmount) {
		this.customerAmount = customerAmount;
	}
	public Double getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}
	public Double getProfit() {
		return profit;
	}
	public void setProfit(Double profit) {
		this.profit = profit;
	}
	public String getPayWay() {
		return payWay;
	}
	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getSupplierNo() {
		return supplierNo;
	}
	public void setSupplierNo(String supplierNo) {
		this.supplierNo = supplierNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getPassengerNames() {
		return passengerNames;
	}
	public void setPassengerNames(String passengerNames) {
		this.passengerNames = passengerNames;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public Date getPrintTicketDate() {
		return printTicketDate;
	}
	public void setPrintTicketDate(Date printTicketDate) {
		this.printTicketDate = printTicketDate;
	}
	
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	@Override
	public String toString() {
		return "Purchase [purchaseId=" + purchaseId + ", orderId=" + orderId + ", orderNo=" + orderNo + ", cOrderNo="
				+ cOrderNo + ", employeeName=" + employeeName + ", customerAmount=" + customerAmount + ", payAmount="
				+ payAmount + ", profit=" + profit + ", realPay=" + realPay + ", payWay=" + payWay + ", tradeNo="
				+ tradeNo + ", supplier=" + supplier + ", supplierNo=" + supplierNo + ", remark=" + remark + ", flag="
				+ flag + ", passengerNames=" + passengerNames + ", operateType=" + operateType + ", printTicketDate="
				+ printTicketDate + ", orderSource=" + orderSource + ", orderShop=" + orderShop + ", purchaseNo="
				+ purchaseNo + ", cAddDate=" + cAddDate + ", flightDate=" + flightDate + ", businessNo=" + businessNo
				+ ", type=" + type + ", returnMoney=" + returnMoney + ", returnDate=" + returnDate + ", newTicketNo="
				+ newTicketNo + ", passengerList=" + passengerList + ", passengerNum=" + passengerNum + "]";
	}

	
}
