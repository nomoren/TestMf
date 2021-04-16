package cn.ssq.ticket.system.entity;

import java.io.Serializable;

public class LastId implements Serializable{

	private static final long serialVersionUID = 1L;

	private String orderSource;
	
	private String lastId;

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getLastId() {
		return lastId;
	}

	public void setLastId(String lastId) {
		this.lastId = lastId;
	}

	@Override
	public String toString() {
		return "LastId [orderSource=" + orderSource + ", lastId=" + lastId
				+ "]";
	}
	
	
	
}
