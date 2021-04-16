package cn.ssq.ticket.system.entity;

import java.io.Serializable;

public class AirLine implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String id;
	private Integer airlineId;		// 
	private String airlineCode;		// 
	private String ename;		// 
	private String cname;		// 
	private String shortName;		// 
	private String iconImage;		// 
	private String code1;		// 
	private String code2;		// 
	@Override
	public String toString() {
		return "AirLine [id=" + id + ", airlineId=" + airlineId
				+ ", airlineCode=" + airlineCode + ", ename=" + ename
				+ ", cname=" + cname + ", shortName=" + shortName
				+ ", iconImage=" + iconImage + ", code1=" + code1 + ", code2="
				+ code2 + ", createDate=" + createDate + ", createBy="
				+ createBy + ", web=" + web + ", actionCode=" + actionCode
				+ ", allowCHDTicket=" + allowCHDTicket + "]";
	}
	private String createDate;		// 
	private String createBy;		// 
	private String web;
	private String actionCode;
	private String allowCHDTicket;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getAirlineId() {
		return airlineId;
	}
	public void setAirlineId(Integer airlineId) {
		this.airlineId = airlineId;
	}
	public String getAirlineCode() {
		return airlineCode;
	}
	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getIconImage() {
		return iconImage;
	}
	public void setIconImage(String iconImage) {
		this.iconImage = iconImage;
	}
	public String getCode1() {
		return code1;
	}
	public void setCode1(String code1) {
		this.code1 = code1;
	}
	public String getCode2() {
		return code2;
	}
	public void setCode2(String code2) {
		this.code2 = code2;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public String getActionCode() {
		return actionCode;
	}
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
	public String getAllowCHDTicket() {
		return allowCHDTicket;
	}
	public void setAllowCHDTicket(String allowCHDTicket) {
		this.allowCHDTicket = allowCHDTicket;
	}
	
	

}
