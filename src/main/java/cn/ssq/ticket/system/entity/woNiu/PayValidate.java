package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

public class PayValidate implements Serializable{

    private static final long serialVersionUID = -8670554258521842548L;
    private String orderNo;

    private String pmCode;

    private String bankCode;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPmCode() {
        return pmCode;
    }

    public void setPmCode(String pmCode) {
        this.pmCode = pmCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}
