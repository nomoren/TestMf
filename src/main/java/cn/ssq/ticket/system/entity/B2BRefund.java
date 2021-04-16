package cn.ssq.ticket.system.entity;

import cn.stylefeng.guns.core.common.annotion.ExcelField;

import java.io.Serializable;

/**
 * b2b接口退票实体类
 */
public class B2BRefund implements Serializable{
    private static final long serialVersionUID = -3306416103871085020L;

    private String requestid;
    private String pnr;
    private String orderNo;
    private String ticketNo;
    private String refundReason;

    private String mfOrderNo;//本地订单号
    private String refundNo;//退票成功后的退票单号
    private Long importId;//本次退票标识
    private String remark;//备注
    private String airCode;//航司二字码
    private String policyType;


    @ExcelField(title="订单号      ", type = 0, align = 2, sort = 0)
    public String getMfOrderNo() {
        return mfOrderNo;
    }

    public void setMfOrderNo(String mfOrderNo) {
        this.mfOrderNo = mfOrderNo;
    }

    @ExcelField(title="出票平台订单号", type = 0, align = 2, sort = 1)
    public String getOrderid() {
        return orderNo;
    }

    public void setOrderid(String orderid) {
        this.orderNo = orderid;
    }

    @ExcelField(title="票号      ", type = 0, align = 2, sort = 2)
    public String getTicketno() {
        return ticketNo;
    }


    public void setTicketno(String ticketno) {
        this.ticketNo = ticketno;
    }

    @ExcelField(title="编码 ", type = 0, align = 2, sort = 3)
    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    @ExcelField(title="类型      ", type = 0, align = 2, sort = 4)
    public String getReason() {
        return refundReason;
    }

    public void setReason(String reason) {
        this.refundReason = reason;
    }

    @ExcelField(title="备注      ", type = 0, align = 2, sort = 5)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ExcelField(title="退票单号      ", type = 0, align = 2, sort = 6)
    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public Long getImportId() {
        return importId;
    }

    public void setImportId(Long importId) {
        this.importId = importId;
    }

    public String getAirCode() {
        return airCode;
    }

    public void setAirCode(String airCode) {
        this.airCode = airCode;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    @Override
    public String toString() {
        return "B2BRefund{" +
                "pnr='" + pnr + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", ticketNo='" + ticketNo + '\'' +
                ", reason='" + refundReason + '\'' +
                ", mfOrderNo='" + mfOrderNo + '\'' +
                ", refundNo='" + refundNo + '\'' +
                ", importId=" + importId +
                ", remark='" + remark + '\'' +
                ", airCode='" + airCode + '\'' +
                ", policyType='" + policyType + '\'' +
                '}';
    }

}
