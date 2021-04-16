package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

public class ValidateInfo implements Serializable{
    private static final long serialVersionUID = -540753321978864603L;

    private boolean isOk;

    private String errMsg;

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
