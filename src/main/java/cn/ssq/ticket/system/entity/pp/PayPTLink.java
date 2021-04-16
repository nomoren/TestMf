package cn.ssq.ticket.system.entity.pp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="request")
public class PayPTLink implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String businessNo;
	private String systemId;
	private String operateTime;
	private String sign;
	private String service;
	private String  orderNo;
	private String  orderAmt;
	private String  hddxmc;
	private String  orderType;
	private String  yyfhcs;
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
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(String orderAmt) {
		this.orderAmt = orderAmt;
	}
	public String getHddxmc() {
		return hddxmc;
	}
	public void setHddxmc(String hddxmc) {
		this.hddxmc = hddxmc;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getYyfhcs() {
		return yyfhcs;
	}
	public void setYyfhcs(String yyfhcs) {
		this.yyfhcs = yyfhcs;
	}
	@Override
	public String toString() {
		return "PayPTLink [userId=" + userId + ", businessNo=" + businessNo + ", systemId=" + systemId
				+ ", operateTime=" + operateTime + ", sign=" + sign + ", service=" + service + ", orderNo=" + orderNo
				+ ", orderAmt=" + orderAmt + ", hddxmc=" + hddxmc + ", orderType=" + orderType + ", yyfhcs=" + yyfhcs
				+ "]";
	}
}
