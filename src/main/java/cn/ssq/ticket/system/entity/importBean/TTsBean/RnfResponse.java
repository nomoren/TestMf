package cn.ssq.ticket.system.entity.importBean.TTsBean;

import java.io.Serializable;

public class RnfResponse implements Serializable{
	private static final long serialVersionUID = 1L;

	private String transactionId="";
	
	private String result="success";
	
	private String errMsg="";

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	@Override
	public String toString() {
		return "RnfResponse [transactionId=" + transactionId + ", result="
				+ result + ", errMsg=" + errMsg + "]";
	}
	
	
	
	
}
