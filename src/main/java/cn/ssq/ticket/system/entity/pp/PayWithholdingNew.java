package cn.ssq.ticket.system.entity.pp;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="request")
public class PayWithholdingNew implements Serializable{

	private static final long serialVersionUID = 1L;
	private String userId;
	private String businessNo;
	private String systemId;
	private String operateTime;
	private String sign;
	private String service;
	private String orderNo;
	private String orderType;
	private String hddxmc;
	private String cplx;
	
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
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	@XmlElement
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	@XmlElement
	public String getHddxmc() {
		return hddxmc;
	}
	public void setHddxmc(String hddxmc) {
		this.hddxmc = hddxmc;
	}
	@XmlElement
	public String getCplx() {
		return cplx;
	}
	public void setCplx(String cplx) {
		this.cplx = cplx;
	}
	
	
	
	
	
}
