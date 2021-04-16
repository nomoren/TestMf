package cn.ssq.ticket.system.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="request")
public class PPRefundDetail implements Serializable{

    private static final long serialVersionUID = 3185761169803068260L;

    private String userId;
    private String businessNo;
    private String systemId;
    private String sign;
    private String operateTime;
    private String service;
    private String refundNo;

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
    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @XmlElement
    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    @XmlElement
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @XmlElement
    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    @Override
    public String toString() {
        return "PPRefundDetail{" +
                "userId='" + userId + '\'' +
                ", businessNo='" + businessNo + '\'' +
                ", systemId='" + systemId + '\'' +
                ", sign='" + sign + '\'' +
                ", operateTime='" + operateTime + '\'' +
                ", service='" + service + '\'' +
                ", refundNo='" + refundNo + '\'' +
                '}';
    }
}
