package cn.ssq.ticket.system.entity.pp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="request")
public class CreateOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	private String userId;
	private String businessNo;
	private String systemId;
	private String operateTime;
	private String sign;
	private String service;
	private String pnrNo;
	private String productType;
	private String policyId;
	private String travelType;
	private String travelRange;
	private String flightNo;
	private String realFlightNo;
	private String seatClass;
	private String fromDatetime;
	private String toDatetime;
	private String tofromterminal;
	private String terminal;
	private String passenger;
	private String passengerType;
	private String cardType;
	private String cardId;
	private String passengerMobile;
	private String scny;
	private String yq;
	private String tax;
	private String settlementPrice;
	private String ifChangeOrder;
	private String ifNFDSpecial;
	private String ifMzcwChangOrder;
	private String disCount;
	private String bigPnrNo;
	
	@XmlElement
	public String getBigPnrNo() {
		return bigPnrNo;
	}
	public void setBigPnrNo(String bigPnrNo) {
		this.bigPnrNo = bigPnrNo;
	}
	@XmlElement
	public String getDisCount() {
		return disCount;
	}
	public void setDisCount(String disCount) {
		this.disCount = disCount;
	}
	@XmlElement
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@XmlElement
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	@XmlElement
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	@XmlElement
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	@XmlElement
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@XmlElement
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	@XmlElement
	public String getPnrNo() {
		return pnrNo;
	}
	public void setPnrNo(String pnrNo) {
		this.pnrNo = pnrNo;
	}
	@XmlElement
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	@XmlElement
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	@XmlElement
	public String getTravelType() {
		return travelType;
	}
	public void setTravelType(String travelType) {
		this.travelType = travelType;
	}
	@XmlElement
	public String getTravelRange() {
		return travelRange;
	}
	public void setTravelRange(String travelRange) {
		this.travelRange = travelRange;
	}
	@XmlElement
	public String getFlightNo() {
		return flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	@XmlElement
	public String getRealFlightNo() {
		return realFlightNo;
	}
	public void setRealFlightNo(String realFlightNo) {
		this.realFlightNo = realFlightNo;
	}
	@XmlElement
	public String getSeatClass() {
		return seatClass;
	}
	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
	}
	@XmlElement
	public String getFromDatetime() {
		return fromDatetime;
	}
	public void setFromDatetime(String fromDatetime) {
		this.fromDatetime = fromDatetime;
	}
	@XmlElement
	public String getToDatetime() {
		return toDatetime;
	}
	public void setToDatetime(String toDatetime) {
		this.toDatetime = toDatetime;
	}
	@XmlElement
	public String getTofromterminal() {
		return tofromterminal;
	}
	public void setTofromterminal(String tofromterminal) {
		this.tofromterminal = tofromterminal;
	}
	@XmlElement
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	@XmlElement
	public String getPassenger() {
		return passenger;
	}
	public void setPassenger(String passenger) {
		this.passenger = passenger;
	}
	@XmlElement
	public String getPassengerType() {
		return passengerType;
	}
	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}
	@XmlElement
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	@XmlElement
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	@XmlElement
	public String getPassengerMobile() {
		return passengerMobile;
	}
	public void setPassengerMobile(String passengerMobile) {
		this.passengerMobile = passengerMobile;
	}
	@XmlElement
	public String getScny() {
		return scny;
	}
	public void setScny(String scny) {
		this.scny = scny;
	}
	@XmlElement
	public String getYq() {
		return yq;
	}
	public void setYq(String yq) {
		this.yq = yq;
	}
	@XmlElement
	public String getTax() {
		return tax;
	}
	public void setTax(String tax) {
		this.tax = tax;
	}
	@XmlElement
	public String getSettlementPrice() {
		return settlementPrice;
	}
	public void setSettlementPrice(String settlementPrice) {
		this.settlementPrice = settlementPrice;
	}
	@XmlElement
	public String getIfChangeOrder() {
		return ifChangeOrder;
	}
	public void setIfChangeOrder(String ifChangeOrder) {
		this.ifChangeOrder = ifChangeOrder;
	}
	@XmlElement
	public String getIfNFDSpecial() {
		return ifNFDSpecial;
	}
	public void setIfNFDSpecial(String ifNFDSpecial) {
		this.ifNFDSpecial = ifNFDSpecial;
	}
	@XmlElement
	public String getIfMzcwChangOrder() {
		return ifMzcwChangOrder;
	}
	public void setIfMzcwChangOrder(String ifMzcwChangOrder) {
		this.ifMzcwChangOrder = ifMzcwChangOrder;
	}
	@Override
	public String toString() {
		return "CreateOrder [userId=" + userId + ", businessNo=" + businessNo
				+ ", systemId=" + systemId + ", operateTime=" + operateTime
				+ ", sign=" + sign + ", service=" + service + ", pnrNo="
				+ pnrNo + ", productType=" + productType + ", policyId="
				+ policyId + ", travelType=" + travelType + ", travelRange="
				+ travelRange + ", flightNo=" + flightNo + ", realFlightNo="
				+ realFlightNo + ", seatClass=" + seatClass + ", fromDatetime="
				+ fromDatetime + ", toDatetime=" + toDatetime
				+ ", tofromterminal=" + tofromterminal + ", terminal="
				+ terminal + ", passenger=" + passenger + ", passengerType="
				+ passengerType + ", cardType=" + cardType + ", cardId="
				+ cardId + ", passengerMobile=" + passengerMobile + ", scny="
				+ scny + ", yq=" + yq + ", tax=" + tax + ", settlementPrice="
				+ settlementPrice + ", ifChangeOrder=" + ifChangeOrder
				+ ", ifNFDSpecial=" + ifNFDSpecial + ", ifMzcwChangOrder="
				+ ifMzcwChangOrder + ", disCount=" + disCount + ", bigPnrNo="
				+ bigPnrNo + "]";
	}
	
	
	
	
}
