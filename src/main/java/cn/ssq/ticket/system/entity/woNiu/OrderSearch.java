package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

public class OrderSearch implements Serializable{


    private static final long serialVersionUID = -3554767308337655030L;
    private String orderNo;


    public OrderSearch(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
