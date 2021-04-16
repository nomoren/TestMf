package cn.ssq.ticket.system.entity.importBean.TTsBean;

public class DataParam {

	private String changeCode;
	
	private String orderDesc;
	
	private String orderNo;
	
	private String time;
	
	private String transactionId;

	public String getChangeCode() {
		return changeCode;
	}

	public void setChangeCode(String changeCode) {
		this.changeCode = changeCode;
	}

	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		return "DataParam [changeCode=" + changeCode + ", orderDesc="
				+ orderDesc + ", orderNo=" + orderNo + ", time=" + time
				+ ", transactionId=" + transactionId + "]";
	}
	
	
	
}
