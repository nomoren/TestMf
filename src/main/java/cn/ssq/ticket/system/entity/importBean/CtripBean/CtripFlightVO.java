package cn.ssq.ticket.system.entity.importBean.CtripBean;

public class CtripFlightVO {
	/**
	 * 航程序号
	 */
	private String sequence;
	
	/**
	 * 航班号
	 */
	private String flight;
	
	/**
	 * 服务等级     F头等C公务Y经济
	 */
	private String cclass;
	
	/**
	 * 舱位
	 */
	private String subClass;
	
	/**
	 * 起飞机场
	 */
	private String dport;
	
	/**
	 * 到达机场
	 */
	private String aport;
	
	/**
	 * 起飞时间
	 */
	private String takeOffTime;
	/**
	 * 到达时间
	 */
	private String arrivalTime;
	
	/**
	 * 票面价
	 */
	private String printPrice;
	
	/**
	 * 销售价
	 */
	private String price;
	
	/**
	 * 底价
	 */
	private String cost;
	
	/**
	 * 燃油附加费
	 */
	private String oilFee;
	
	/**
	 * 机场建设费
	 */
	private String tax;
	
	/**
	 * 记录编号      代理人系统PNR 若未订位,默认”000000”
	 */
	private String recordNo;
	
	/**
	 * 授权OFFICE号
	 */
	private String officeNo;
	
	/**
	 * 政策号,携程内部政策编号
	 */
	private String policyID;
	/**
	 * 政策代码
	 */
	private String policyCode;
	
	/**
	 * 签转说明
	 */
	private String endNote;
	
	/**
	 * 更改说明
	 */
	private String rerNote;
	
	/**
	 * 退票说明
	 */
	private String refNote;
	
	/**
	 * 英文签转说明
	 */
	private String e_EndNote;
	
	/**
	 * 英文更改说明
	 */
	private String e_RerNote;
	
	/**
	 * 英文退票说明
	 */
	private String e_RefNote;
	
	/**
	 * 底价扣率    航空公司佣金,是指（票面价-结算价）/票面价,仅供参考
	 */
	private String costRate;

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getFlight() {
		return flight;
	}

	public void setFlight(String flight) {
		this.flight = flight;
	}

	public String getCclass() {
		return cclass;
	}

	public void setCclass(String cclass1) {
		cclass = cclass1;
	}

	public String getSubClass() {
		return subClass;
	}

	public void setSubClass(String subClass) {
		this.subClass = subClass;
	}

	public String getDport() {
		return dport;
	}

	public void setDport(String dport) {
		this.dport = dport;
	}

	public String getAport() {
		return aport;
	}

	public void setAport(String aport) {
		this.aport = aport;
	}

	public String getTakeOffTime() {
		return takeOffTime;
	}

	public void setTakeOffTime(String takeOffTime) {
		this.takeOffTime = takeOffTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getPrintPrice() {
		return printPrice;
	}

	public void setPrintPrice(String printPrice) {
		this.printPrice = printPrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getOilFee() {
		return oilFee;
	}

	public void setOilFee(String oilFee) {
		this.oilFee = oilFee;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getOfficeNo() {
		return officeNo;
	}

	public void setOfficeNo(String officeNo) {
		this.officeNo = officeNo;
	}

	public String getPolicyID() {
		return policyID;
	}

	public void setPolicyID(String policyID) {
		this.policyID = policyID;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

	public String getEndNote() {
		return endNote;
	}

	public void setEndNote(String endNote) {
		this.endNote = endNote;
	}

	public String getRerNote() {
		return rerNote;
	}

	public void setRerNote(String rerNote) {
		this.rerNote = rerNote;
	}

	public String getRefNote() {
		return refNote;
	}

	public void setRefNote(String refNote) {
		this.refNote = refNote;
	}

	public String getE_EndNote() {
		return e_EndNote;
	}

	public void setE_EndNote(String eEndNote) {
		e_EndNote = eEndNote;
	}

	public String getE_RerNote() {
		return e_RerNote;
	}

	public void setE_RerNote(String eRerNote) {
		e_RerNote = eRerNote;
	}

	public String getE_RefNote() {
		return e_RefNote;
	}

	public void setE_RefNote(String eRefNote) {
		e_RefNote = eRefNote;
	}

	public String getCostRate() {
		return costRate;
	}

	public void setCostRate(String costRate) {
		this.costRate = costRate;
	}
	
}
