package cn.ssq.ticket.system.entity;

import java.util.Map;

public class FlightInfo {
	private String flightNo;
	private String stime;
	private String dpt;
	private String arr;
	private String date;
	private Map<String,String> map;
	public String getFlightNo() {
		return flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	public String getStime() {
		return stime;
	}
	public void setStime(String stime) {
		this.stime = stime;
	}
	public String getDpt() {
		return dpt;
	}
	public void setDpt(String dpt) {
		this.dpt = dpt;
	}
	public String getArr() {
		return arr;
	}
	public void setArr(String arr) {
		this.arr = arr;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Map<String, String> getMap() {
		return map;
	}
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	@Override
	public String toString() {
		return "FlightInfo [flightNo=" + flightNo + ", stime=" + stime + ", dpt=" + dpt + ", arr=" + arr + ", date="
				+ date + ", map=" + map + "]";
	}
	
	
}
