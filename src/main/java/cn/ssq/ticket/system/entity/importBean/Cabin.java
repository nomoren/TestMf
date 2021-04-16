package cn.ssq.ticket.system.entity.importBean;

public class Cabin {

	private String flightNo;
	private String seatCode;
	private String departureDate;
	private String departureTime;
	private String departAirportCode;
	private String departureAirportTerminal;
	private String arrivalDate;
	private String arrivalTime;
	private String arriveAirportCode;
	private String arrivalAirportTerminal;
	public String getFlightNo() {
		return flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	public String getSeatCode() {
		return seatCode;
	}
	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}
	public String getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	public String getDepartAirportCode() {
		return departAirportCode;
	}
	public void setDepartAirportCode(String departAirportCode) {
		this.departAirportCode = departAirportCode;
	}
	public String getDepartureAirportTerminal() {
		return departureAirportTerminal;
	}
	public void setDepartureAirportTerminal(String departureAirportTerminal) {
		this.departureAirportTerminal = departureAirportTerminal;
	}
	public String getArrivalDate() {
		return arrivalDate;
	}
	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getArriveAirportCode() {
		return arriveAirportCode;
	}
	public void setArriveAirportCode(String arriveAirportCode) {
		this.arriveAirportCode = arriveAirportCode;
	}
	public String getArrivalAirportTerminal() {
		return arrivalAirportTerminal;
	}
	public void setArrivalAirportTerminal(String arrivalAirportTerminal) {
		this.arrivalAirportTerminal = arrivalAirportTerminal;
	}
	@Override
	public String toString() {
		return "Cabin [flightNo=" + flightNo + ", seatCode=" + seatCode
				+ ", departureDate=" + departureDate + ", departureTime="
				+ departureTime + ", departAirportCode=" + departAirportCode
				+ ", departureAirportTerminal=" + departureAirportTerminal
				+ ", arrivalDate=" + arrivalDate + ", arrivalTime="
				+ arrivalTime + ", arriveAirportCode=" + arriveAirportCode
				+ ", arrivalAirportTerminal=" + arrivalAirportTerminal + "]";
	}
	
}
