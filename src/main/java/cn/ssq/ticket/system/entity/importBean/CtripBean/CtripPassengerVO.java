package cn.ssq.ticket.system.entity.importBean.CtripBean;

public class CtripPassengerVO {
	/**
	 * 乘客序号
	 */
	private String passengerIndex;
	/**
	 * 记录编号      代理人系统PNR 若未订位,默认”000000”
	 */
	private String recordNo;
	
	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	/**
	 * 乘客姓名
	 */
	private String passengerName;
	/**
	 * 舱位
	 */
	private String subClass;
	
	public String getSubClass() {
		return subClass;
	}

	public void setSubClass(String subClass) {
		this.subClass = subClass;
	}

	/**
	 * 乘客性别     M:男性;F:女性
	 */
	private String gender;
	
	/**
	 * 证件号
	 */
	private String cardNO;
	
	/**
	 * 乘客类型
	 */
	private String passengerType;
	
	/**
	 * 出生日期
	 */
	private String birthDay;
	
	/**
	 * 证件类型
	 */
	private String cardType;
	
	private String printPrice;
	
	/**
	 * 票号
	 */
	private String ticketNO;
	/**
	 * 年龄类型
	 */
	private String ageType;
	
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

	public String getAgeType() {
		return ageType;
	}

	public void setAgeType(String ageType) {
		this.ageType = ageType;
	}

	public String getPassengerIndex() {
		return passengerIndex;
	}

	public void setPassengerIndex(String passengerIndex) {
		this.passengerIndex = passengerIndex;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCardNO() {
		return cardNO;
	}

	public void setCardNO(String cardNO) {
		this.cardNO = cardNO;
	}

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getTicketNO() {
		return ticketNO;
	}

	public void setTicketNO(String ticketNO) {
		this.ticketNO = ticketNO;
	}

	public String getPrintPrice() {
		return printPrice;
	}

	public void setPrintPrice(String printPrice) {
		this.printPrice = printPrice;
	}

	@Override
	public String toString() {
		return "CtripPassengerVO [passengerIndex=" + passengerIndex
				+ ", recordNo=" + recordNo + ", passengerName=" + passengerName
				+ ", subClass=" + subClass + ", gender=" + gender + ", cardNO="
				+ cardNO + ", passengerType=" + passengerType + ", birthDay="
				+ birthDay + ", cardType=" + cardType + ", printPrice="
				+ printPrice + ", ticketNO=" + ticketNO + ", ageType="
				+ ageType + ", cost=" + cost + ", oilFee=" + oilFee + ", tax="
				+ tax + "]";
	}
	
}
