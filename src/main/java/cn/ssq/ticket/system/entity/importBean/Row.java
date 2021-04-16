package cn.ssq.ticket.system.entity.importBean;

import java.util.List;

public class Row {

	private String id;
	private Float totalAmount;
	private int status;
	private String clearPositionTime;
	private int settlementWay;
	private String settlementTime;
	private String policyId;
	private String addTime;
	private String policyDes;
	private String policyRemark;
	private String adultRebate;             
	private String adultBackCash;            
	private String childRebate;
	private String childBackCash;            
	private String childSinRebate;
	private String childSinBackCash;      
	private String babyRebate;
	private String babyBackCash;
	private String orderId;
	private String productName="";
	private TicketLockItem ticketLockItem;
	private List<Cabin> cabinList;
	private List<PnrInfo> pnrInfoList;
	
	
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public TicketLockItem getTicketLockItem() {
		return ticketLockItem;
	}
	public void setTicketLockItem(TicketLockItem ticketLockItem) {
		this.ticketLockItem = ticketLockItem;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Float getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getClearPositionTime() {
		return clearPositionTime;
	}
	public void setClearPositionTime(String clearPositionTime) {
		this.clearPositionTime = clearPositionTime;
	}
	public int getSettlementWay() {
		return settlementWay;
	}
	public void setSettlementWay(int settlementWay) {
		this.settlementWay = settlementWay;
	}
	public String getSettlementTime() {
		return settlementTime;
	}
	public void setSettlementTime(String settlementTime) {
		this.settlementTime = settlementTime;
	}
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getPolicyDes() {
		return policyDes;
	}
	public void setPolicyDes(String policyDes) {
		this.policyDes = policyDes;
	}
	public String getPolicyRemark() {
		return policyRemark;
	}
	public void setPolicyRemark(String policyRemark) {
		this.policyRemark = policyRemark;
	}
	public String getAdultRebate() {
		return adultRebate;
	}
	public void setAdultRebate(String adultRebate) {
		this.adultRebate = adultRebate;
	}
	public String getAdultBackCash() {
		return adultBackCash;
	}
	public void setAdultBackCash(String adultBackCash) {
		this.adultBackCash = adultBackCash;
	}
	public String getChildRebate() {
		return childRebate;
	}
	public void setChildRebate(String childRebate) {
		this.childRebate = childRebate;
	}
	public String getChildBackCash() {
		return childBackCash;
	}
	public void setChildBackCash(String childBackCash) {
		this.childBackCash = childBackCash;
	}
	public String getChildSinRebate() {
		return childSinRebate;
	}
	public void setChildSinRebate(String childSinRebate) {
		this.childSinRebate = childSinRebate;
	}
	public String getChildSinBackCash() {
		return childSinBackCash;
	}
	public void setChildSinBackCash(String childSinBackCash) {
		this.childSinBackCash = childSinBackCash;
	}
	public String getBabyRebate() {
		return babyRebate;
	}
	public void setBabyRebate(String babyRebate) {
		this.babyRebate = babyRebate;
	}
	public String getBabyBackCash() {
		return babyBackCash;
	}
	public void setBabyBackCash(String babyBackCash) {
		this.babyBackCash = babyBackCash;
	}
	public List<Cabin> getCabinList() {
		return cabinList;
	}
	public void setCabinList(List<Cabin> cabinList) {
		this.cabinList = cabinList;
	}
	public List<PnrInfo> getPnrInfoList() {
		return pnrInfoList;
	}
	public void setPnrInfoList(List<PnrInfo> pnrInfoList) {
		this.pnrInfoList = pnrInfoList;
	}
	@Override
	public String toString() {
		return "Row [id=" + id + ", totalAmount=" + totalAmount + ", status="
				+ status + ", clearPositionTime=" + clearPositionTime
				+ ", settlementWay=" + settlementWay + ", settlementTime="
				+ settlementTime + ", policyId=" + policyId + ", addTime="
				+ addTime + ", policyDes=" + policyDes + ", policyRemark="
				+ policyRemark + ", adultRebate=" + adultRebate
				+ ", adultBackCash=" + adultBackCash + ", childRebate="
				+ childRebate + ", childBackCash=" + childBackCash
				+ ", childSinRebate=" + childSinRebate + ", childSinBackCash="
				+ childSinBackCash + ", babyRebate=" + babyRebate
				+ ", babyBackCash=" + babyBackCash + ", orderId=" + orderId
				+ ", ticketLockItem=" + ticketLockItem + ", cabinList="
				+ cabinList + ", pnrInfoList=" + pnrInfoList + "]";
	}
	
}
