package cn.ssq.ticket.system.entity.pp;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="request")
public class RealtimePolicy implements Serializable{
	private static final long serialVersionUID = 1L;
	private String userId;
	private String businessNo;
	private String systemId;
	private String operateTime;
	private String sign;
	private String service;
	private String flightDate;
	private String fromCity;
	private String toCity;
	private String passengerType;
	private String travelType;
	private String airways;
	private String ticketPrice;
	private String discount;
	private String flightNo;
	private String seatClass;
	private String systemType;
	private String transitCity;
	private String backFlightDate;
	private String pnrContent;
	private String patContent;
	
	@XmlElement
	public String getSystemType() {
		return systemType;
	}
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}
	@XmlElement
	public String getTransitCity() {
		return transitCity;
	}
	public void setTransitCity(String transitCity) {
		this.transitCity = transitCity;
	}
	@XmlElement
	public String getBackFlightDate() {
		return backFlightDate;
	}
	public void setBackFlightDate(String backFlightDate) {
		this.backFlightDate = backFlightDate;
	}
	@XmlElement
	public String getPnrContent() {
		return pnrContent;
	}
	public void setPnrContent(String pnrContent) {
		this.pnrContent = pnrContent;
	}
	@XmlElement
	public String getPatContent() {
		return patContent;
	}
	public void setPatContent(String patContent) {
		this.patContent = patContent;
	}
	@XmlElement
	public String getSeatClass() {
		return seatClass;
	}
	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
	}
	@XmlElement
	public String getFlightNo() {
		return flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
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
	public String getFlightDate() {
		return flightDate;
	}
	public void setFlightDate(String flightDate) {
		this.flightDate = flightDate;
	}
	@XmlElement
	public String getFromCity() {
		return fromCity;
	}
	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}
	@XmlElement
	public String getToCity() {
		return toCity;
	}
	public void setToCity(String toCity) {
		this.toCity = toCity;
	}
	@XmlElement
	public String getPassengerType() {
		return passengerType;
	}
	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}
	@XmlElement
	public String getTravelType() {
		return travelType;
	}
	public void setTravelType(String travelType) {
		this.travelType = travelType;
	}
	@XmlElement
	public String getAirways() {
		return airways;
	}
	public void setAirways(String airways) {
		this.airways = airways;
	}
	@XmlElement
	public String getTicketPrice() {
		return ticketPrice;
	}
	public void setTicketPrice(String ticketPrice) {
		this.ticketPrice = ticketPrice;
	}
	@XmlElement
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	
	
	
	
}
