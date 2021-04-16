package cn.ssq.ticket.system.entity;

import java.io.Serializable;

public class B2BRefundDatail implements Serializable{
    private static final long serialVersionUID = -6834724561866815965L;

    private String requestid;
    private String airCode;//航司二字码
    private String policyType;
    private String mfOrderNo;//本地订单号

    public String getMfOrderNo() {
        return mfOrderNo;
    }

    public void setMfOrderNo(String mfOrderNo) {
        this.mfOrderNo = mfOrderNo;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
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

    @Override
    public String toString() {
        return "B2BRefundDatail{" +
                "requestid='" + requestid + '\'' +
                ", airCode='" + airCode + '\'' +
                ", policyType='" + policyType + '\'' +
                '}';
    }
}
