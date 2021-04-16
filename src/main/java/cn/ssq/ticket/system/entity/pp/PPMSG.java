package cn.ssq.ticket.system.entity.pp;

import java.io.Serializable;

public class PPMSG implements Serializable{
	private static final long serialVersionUID = 1L;
	private String flightDate;
	private String fromCity;
	private String toCity;
	private String ticketPrice;
	private String flightNo;
	private String seatClass;
	private String passengerName;
	private String passengerCard;
	private String pnr;
	private String bigPnr;
	private String fromDatetime;
	private String toDatetime;
	private String cardType;
	private String passengerType;
	public String getFlightDate() {
		return flightDate;
	}
	public void setFlightDate(String flightDate) {
		this.flightDate = flightDate;
	}
	public String getFromCity() {
		return fromCity;
	}
	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}
	public String getToCity() {
		return toCity;
	}
	public void setToCity(String toCity) {
		this.toCity = toCity;
	}
	public String getTicketPrice() {
		return ticketPrice;
	}
	public void setTicketPrice(String ticketPrice) {
		this.ticketPrice = ticketPrice;
	}
	public String getFlightNo() {
		return flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	public String getSeatClass() {
		return seatClass;
	}
	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
	}
	public String getPassengerName() {
		return passengerName;
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	public String getPassengerCard() {
		return passengerCard;
	}
	public void setPassengerCard(String passengerCard) {
		this.passengerCard = passengerCard;
	}
	public String getPnr() {
		return pnr;
	}
	public void setPnr(String pnr) {
		this.pnr = pnr;
	}
	public String getBigPnr() {
		return bigPnr;
	}
	public void setBigPnr(String bigPnr) {
		this.bigPnr = bigPnr;
	}
	public String getFromDatetime() {
		return fromDatetime;
	}
	public void setFromDatetime(String fromDatetime) {
		this.fromDatetime = fromDatetime;
	}
	public String getToDatetime() {
		return toDatetime;
	}
	public void setToDatetime(String toDatetime) {
		this.toDatetime = toDatetime;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getPassengerType() {
		return passengerType;
	}
	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}
	@Override
	public String toString() {
		return "PPMSG [flightDate=" + flightDate + ", fromCity=" + fromCity
				+ ", toCity=" + toCity + ", ticketPrice=" + ticketPrice
				+ ", flightNo=" + flightNo + ", seatClass=" + seatClass
				+ ", passengerName=" + passengerName + ", passengerCard="
				+ passengerCard + ", pnr=" + pnr + ", bigPnr=" + bigPnr
				+ ", fromDatetime=" + fromDatetime + ", toDatetime="
				+ toDatetime + ", cardType=" + cardType + ", passengerType="
				+ passengerType + "]";
	}


}
