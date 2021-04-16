package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

public class OrderInfo implements Serializable{
    private static final long serialVersionUID = -6647930864444594295L;


    private String  noPayAmount;
    private String  orderNo;
    private String  status;

    public String getNoPayAmount() {
        return noPayAmount;
    }

    public void setNoPayAmount(String noPayAmount) {
        this.noPayAmount = noPayAmount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "noPayAmount='" + noPayAmount + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
