package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

/**
 * @Author: Administrator on 2019/4/23 9:29
 * @param:
 * @return:
 * @Description:订单支付参数实体类
 **/
public class PayParam implements Serializable{
    private static final long serialVersionUID = 5863894543982979169L;
    private String orderNo;
    private String pmCode;
    private String bankCode;
    private String paymentMerchantCode;
    private String curId;
    private String bgRetUrl;


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

    public String getPaymentMerchantCode() {
        return paymentMerchantCode;
    }

    public void setPaymentMerchantCode(String paymentMerchantCode) {
        this.paymentMerchantCode = paymentMerchantCode;
    }

    public String getCurId() {
        return curId;
    }

    public void setCurId(String curId) {
        this.curId = curId;
    }

    public String getBgRetUrl() {
        return bgRetUrl;
    }

    public void setBgRetUrl(String bgRetUrl) {
        this.bgRetUrl = bgRetUrl;
    }

    @Override
    public String toString() {
        return "PayParam{" +
                "orderNo='" + orderNo + '\'' +
                ", pmCode='" + pmCode + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", paymentMerchantCode='" + paymentMerchantCode + '\'' +
                ", curId='" + curId + '\'' +
                ", bgRetUrl='" + bgRetUrl + '\'' +
                '}';
    }
}
