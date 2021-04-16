package cn.ssq.ticket.system.entity;

import cn.stylefeng.guns.core.common.annotion.ExcelField;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="request")
public class PPRefund  implements Serializable{
    private static final long serialVersionUID = 4263687817897270384L;

    private String userId;
    private String businessNo;
    private String systemId;
    private String orderNo;
    private String refundType="1";//退票
    private String travelRange;
    private String ticketNo;
    private String passenger;
    private String isCancelSeat="3";//座位已取消
    private String refundReason;
    private String sign;
    private String operateTime;
    private String service;

    private String mfOrderNo;//本地订单号
    private Long importId;//本次退票标识
    private String remark;//备注
    private String refundNo;//退票成功后的退票单号

    @ExcelField(title="订单号      ", type = 0, align = 2, sort = 0)
    public String getMfOrderNo() {
        return mfOrderNo;
    }

    public void setMfOrderNo(String mfOrderNo) {
        this.mfOrderNo = mfOrderNo;
    }

    @XmlElement
    @ExcelField(title="出票平台订单号", type = 0, align = 2, sort = 1)
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }


    @XmlElement
    @ExcelField(title="票号       ", type = 0, align = 2, sort = 2)
    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    @XmlElement
    @ExcelField(title="乘机人      ", type = 0, align = 2, sort = 3)
    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    @XmlElement
    @ExcelField(title="航程  ", type = 0, align = 2, sort = 4)
    public String getTravelRange() {
        return travelRange;
    }

    public void setTravelRange(String travelRange) {
        this.travelRange = travelRange;
    }


    @XmlElement
    @ExcelField(title="类型", type = 0, align = 2, sort = 5)
    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }


    @ExcelField(title="备注             ", type = 0, align = 2, sort = 6)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ExcelField(title="退票单号", type = 0, align = 2, sort = 7)
    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    @XmlElement
    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    @XmlElement
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    @XmlElement
    public String getSystemId() {
        return systemId;
    }
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @XmlElement
    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }


    @XmlElement
    public String getIsCancelSeat() {
        return isCancelSeat;
    }

    public void setIsCancelSeat(String isCancelSeat) {
        this.isCancelSeat = isCancelSeat;
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

    public void setService(String server) {
        this.service = server;
    }



    public Long getImportId() {
        return importId;
    }

    public void setImportId(Long importId) {
        this.importId = importId;
    }


    @Override
    public String toString() {
        return "PPRefund{" +
                "userId='" + userId + '\'' +
                ", businessNo='" + businessNo + '\'' +
                ", systemId='" + systemId + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", refundType='" + refundType + '\'' +
                ", travelRange='" + travelRange + '\'' +
                ", ticketNo='" + ticketNo + '\'' +
                ", passenger='" + passenger + '\'' +
                ", isCancelSeat='" + isCancelSeat + '\'' +
                ", refundReason='" + refundReason + '\'' +
                ", sign='" + sign + '\'' +
                ", operateTime='" + operateTime + '\'' +
                ", service='" + service + '\'' +
                ", mfOrderNo='" + mfOrderNo + '\'' +
                ", refundNo='" + refundNo + '\'' +
                ", importId=" + importId +
                ", remark='" + remark + '\'' +
                '}';
    }

}
