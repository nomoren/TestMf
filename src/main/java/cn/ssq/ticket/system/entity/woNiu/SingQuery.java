package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

public class SingQuery implements Serializable{
    private static final long serialVersionUID = 6902212804903882433L;

    private String  bankCode;

    private String  signedAccount;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getSignedAccount() {
        return signedAccount;
    }

    public void setSignedAccount(String signedAccount) {
        this.signedAccount = signedAccount;
    }
}
