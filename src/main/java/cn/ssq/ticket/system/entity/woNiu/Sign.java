package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

public class Sign implements Serializable{


    private static final long serialVersionUID = 5859495863723283176L;

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
