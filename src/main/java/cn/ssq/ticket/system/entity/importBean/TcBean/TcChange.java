package cn.ssq.ticket.system.entity.importBean.TcBean;

public class TcChange {
	 
	//订单号
	private String orderSerialNo;
	
	//改签单号
	private String changeSerialNo;
	
	//改签状态
	private String changeStatusDesc;

	public String getOrderSerialNo() {
		return orderSerialNo;
	}

	public void setOrderSerialNo(String orderSerialNo) {
		this.orderSerialNo = orderSerialNo;
	}

	public String getChangeSerialNo() {
		return changeSerialNo;
	}

	public void setChangeSerialNo(String changeSerialNo) {
		this.changeSerialNo = changeSerialNo;
	}

	public String getChangeStatusDesc() {
		return changeStatusDesc;
	}

	public void setChangeStatusDesc(String changeStatusDesc) {
		this.changeStatusDesc = changeStatusDesc;
	}

	@Override
	public String toString() {
		return "TcChange [orderSerialNo=" + orderSerialNo + ", changeSerialNo="
				+ changeSerialNo + ", changeStatusDesc=" + changeStatusDesc
				+ "]";
	}
}
