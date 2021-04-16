package cn.ssq.ticket.system.entity;

import cn.ssq.ticket.system.util.ToolsUtil;

import java.util.Date;
import java.util.Locale;





public class Info {
	private boolean isCreate;
	private String dep;
	private String arr;
	private String data;
	private String fno;
	private String avh;
	private String[] cabinCodes;
	private String name;
	private String cardNo;
	private String pnr;
	private String cabin;
	private String stime;
	private Integer beginHour;
	public Info() {

	}

	public Info(String cmd) {
		String[] cmda = cmd.toUpperCase().trim().split(",");
		this.dep = cmda[0];
		this.arr = cmda[1];
		this.data = cmda[2];
		this.stime = cmda[3];
		Date d = ToolsUtil.getStringToDate(data, "yyyy-MM-dd");
		String dd = ToolsUtil.getDateToString(d, "dMMM", Locale.ENGLISH);
		
		this.fno = cmda[4];
		
		avh = "AVH/"+dep+arr+"/"+dd+"/"+stime+"/"+fno.substring(0, 2)+"/D";
		
	}

	public boolean isCreate() {
		return isCreate;
	}

	public void setCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public String getArr() {
		return arr;
	}

	public void setArr(String arr) {
		this.arr = arr;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getFno() {
		return fno;
	}

	public void setFno(String fno) {
		this.fno = fno;
	}

	public String[] getCabinCodes() {
		return cabinCodes;
	}

	public void setCabinCodes(String[] cabinCodes) {
		this.cabinCodes = cabinCodes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getInfoStr() {
		return dep + "," + arr + "," + data + "," + fno + "," + name + ","
				+ cardNo + "," + pnr + "," + cabin;
	}

	public String getCabin() {
		return cabin;
	}

	public void setCabin(String cabin) {
		this.cabin = cabin;
	}

	public String getAvh() {
		return avh;
	}

	public void setAvh(String avh) {
		this.avh = avh;
	}

	public Integer getBeginHour() {
		return beginHour;
	}

	public void setBeginHour(Integer beginHour) {
		this.beginHour = beginHour;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}
}
