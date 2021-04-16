package cn.ssq.ticket.system.entity.importBean;

import java.io.Serializable;

public class TickSubConfirmParam implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String toString() {
		return "TickSubConfirmParam [Id=" + id + ", ticketNo=" + ticketNo
				+ ", pnrCode=" + pnrCode + ", isDirect=" + isDirect + "]";
	}
	private Integer id;
	private String ticketNo;
	private String pnrCode;
	private Integer isDirect;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTicketNo() {
		return ticketNo;
	}
	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}
	public String getPnrCode() {
		return pnrCode;
	}
	public void setPnrCode(String pnrCode) {
		this.pnrCode = pnrCode;
	}
	public Integer getIsDirect() {
		return isDirect;
	}
	public void setIsDirect(Integer isDirect) {
		this.isDirect = isDirect;
	}
	
	
	
}
