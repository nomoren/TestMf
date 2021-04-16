package cn.ssq.ticket.system.entity.importBean;

public class Item {
	//乘客票号
	private String ticketNo;
	
	//出票订单号
	private String ticketOrderId;
	
	//金额
	private String costPrice;
	
	//乘客信息
	private Person person;

	
	
	public String getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(String costPrice) {
		this.costPrice = costPrice;
	}

	public String getTicketOrderId() {
		return ticketOrderId;
	}

	public void setTicketOrderId(String ticketOrderId) {
		this.ticketOrderId = ticketOrderId;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	public String toString() {
		return "Item [ticketNo=" + ticketNo + ", ticketOrderId="
				+ ticketOrderId + ", person=" + person + "]";
	}
	
	
}
