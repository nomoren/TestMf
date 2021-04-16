package cn.ssq.ticket.system.entity.importBean;


public class PnrInfo {

	private Integer id;
	private String pnrCode;
	private Float ticketPrice;
	private Float costPrice;
	private Float salePrice;
	private String taxFee;
	private Person person;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPnrCode() {
		return pnrCode;
	}
	public void setPnrCode(String pnrCode) {
		this.pnrCode = pnrCode;
	}
	public Float getTicketPrice() {
		return ticketPrice;
	}
	public void setTicketPrice(Float ticketPrice) {
		this.ticketPrice = ticketPrice;
	}
	public Float getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(Float costPrice) {
		this.costPrice = costPrice;
	}
	public String getTaxFee() {
		return taxFee;
	}
	public void setTaxFee(String taxFee) {
		this.taxFee = taxFee;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public Float getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}
	@Override
	public String toString() {
		return "PnrInfo [id=" + id + ", pnrCode=" + pnrCode + ", ticketPrice="
				+ ticketPrice + ", costPrice=" + costPrice + ", salePrice="
				+ salePrice + ", taxFee=" + taxFee + ", person=" + person + "]";
	}
	
}
