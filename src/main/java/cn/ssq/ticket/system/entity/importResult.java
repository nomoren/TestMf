package cn.ssq.ticket.system.entity;

import cn.stylefeng.guns.core.common.annotion.ExcelField;

import java.io.Serializable;

public class importResult implements Serializable{
    private static final long serialVersionUID = -5582200558089212464L;

    private String mfOrderNo;

    private String orderNo;

    private String ticketNo;

    private String passenger;

    private String refundReason;

    private String remark;

    private String refundNo;

    @ExcelField(title="订单号      ", type = 0, align = 2, sort = 0)
    public String getMfOrderNo() {
        return mfOrderNo;
    }

    public void setMfOrderNo(String mfOrderNo) {
        this.mfOrderNo = mfOrderNo;
    }

    @ExcelField(title="出票平台订单号", type = 0, align = 2, sort = 1)
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @ExcelField(title="票号       ", type = 0, align = 2, sort = 2)
    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    @ExcelField(title="乘机人      ", type = 0, align = 2, sort = 3)
    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

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

    @Override
    public String toString() {
        return "importResult{" +
                "mfOrderNo='" + mfOrderNo + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", ticketNo='" + ticketNo + '\'' +
                ", passenger='" + passenger + '\'' +
                ", refundReason='" + refundReason + '\'' +
                ", remark='" + remark + '\'' +
                ", refundNo='" + refundNo + '\'' +
                '}';
    }
}
