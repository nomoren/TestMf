package cn.ssq.ticket.system.entity.importBean.TcBean;

public class TcOrderVO {

	private String orderNo;
	
	private String flightWay;
	
	private String status;

	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getFlightWay() {
		return flightWay;
	}

	public void setFlightWay(String flightWay) {
		this.flightWay = flightWay;
	}

	@Override
	public String toString() {
		return "TcOrderVO [orderNo=" + orderNo + ", flightWay=" + flightWay
				+ ", status=" + status + "]";
	}
	
	
}
