package cn.ssq.ticket.system.entity;

import java.io.Serializable;

/***
 * 同程航变
 * @author Administrator
 *
 */
public class TcFlightChange implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String orgFlightNo;
	
	private String orgStartPort;
	
	private String orgEndPort;
	
	private String orgDepTime;
	
	private String orgArrTime;
	
	private String newFlightNo;
	
	private String newStartPort;
	
	private String newEndPort;
	
	private String newDepTime;
	
	private String newArrTime;
	
	private String changeType;
	
	private String addType;
	
	private String pnrInfo;
	
	private String messageInfo;
	
	private String changeReason;

	public String getOrgFlightNo() {
		return orgFlightNo;
	}

	public void setOrgFlightNo(String orgFlightNo) {
		this.orgFlightNo = orgFlightNo;
	}

	public String getOrgStartPort() {
		return orgStartPort;
	}

	public void setOrgStartPort(String orgStartPort) {
		this.orgStartPort = orgStartPort;
	}

	public String getOrgEndPort() {
		return orgEndPort;
	}

	public void setOrgEndPort(String orgEndPort) {
		this.orgEndPort = orgEndPort;
	}

	public String getOrgDepTime() {
		return orgDepTime;
	}

	public void setOrgDepTime(String orgDepTime) {
		this.orgDepTime = orgDepTime;
	}

	public String getOrgArrTime() {
		return orgArrTime;
	}

	public void setOrgArrTime(String orgArrTime) {
		this.orgArrTime = orgArrTime;
	}

	public String getNewFlightNo() {
		return newFlightNo;
	}

	public void setNewFlightNo(String newFlightNo) {
		this.newFlightNo = newFlightNo;
	}

	public String getNewStartPort() {
		return newStartPort;
	}

	public void setNewStartPort(String newStartPort) {
		this.newStartPort = newStartPort;
	}

	public String getNewEndPort() {
		return newEndPort;
	}

	public void setNewEndPort(String newEndPort) {
		this.newEndPort = newEndPort;
	}

	public String getNewDepTime() {
		return newDepTime;
	}

	public void setNewDepTime(String newDepTime) {
		this.newDepTime = newDepTime;
	}

	public String getNewArrTime() {
		return newArrTime;
	}

	public void setNewArrTime(String newArrTime) {
		this.newArrTime = newArrTime;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public String getAddType() {
		return addType;
	}

	public void setAddType(String addType) {
		this.addType = addType;
	}

	public String getPnrInfo() {
		return pnrInfo;
	}

	public void setPnrInfo(String pnrInfo) {
		this.pnrInfo = pnrInfo;
	}

	public String getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}

	public String getChangeReason() {
		return changeReason;
	}

	public void setChangeReason(String changeReason) {
		this.changeReason = changeReason;
	}

	@Override
	public String toString() {
		return "TcFlightChange [orgFlightNo=" + orgFlightNo + ", orgStartPort="
				+ orgStartPort + ", orgEndPort=" + orgEndPort + ", orgDepTime="
				+ orgDepTime + ", orgArrTime=" + orgArrTime + ", newFlightNo="
				+ newFlightNo + ", newStartPort=" + newStartPort
				+ ", newEndPort=" + newEndPort + ", newDepTime=" + newDepTime
				+ ", newArrTime=" + newArrTime + ", changeType=" + changeType
				+ ", addType=" + addType + ", pnrInfo=" + pnrInfo
				+ ", messageInfo=" + messageInfo + ", changeReason="
				+ changeReason + "]";
	}
	
	
	

}
