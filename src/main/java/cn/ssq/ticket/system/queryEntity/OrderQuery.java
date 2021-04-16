package cn.ssq.ticket.system.queryEntity;

import java.io.Serializable;

/**
 * 订单查询条件实体类
 * @author Administrator
 *
 */
public class OrderQuery implements Serializable{

	private static final long serialVersionUID = 1L;

	//订单开始时间 
	private String startDate;
	
	//订单结束时间
	private String endDate;
	
	//订单编号
	private String orderNo;
	
	//订单来源
	private String orderSource;
	
	//店铺
	private String orderShop;
	
	//乘客姓名
	private String name;
	
	//pnr
	private String pnr;
	
	//票号
	private String ticketNo;
	
	//航班开始时间
	private String flightStartDate;
	
	//航班结束时间
	private String flightEndDate;
	
	//航班号
	private String flight;
	
	//订单状态
	private Integer orderStatus;
	
	
	private String flightDate;
	
	//分页
	private Integer jump;
	
	private Integer page;

	private Integer limit;
	
	public String getFlightDate() {
		return flightDate;
	}

	public void setFlightDate(String flightDate) {
		this.flightDate = flightDate;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getOrderShop() {
		return orderShop;
	}

	public void setOrderShop(String orderShop) {
		this.orderShop = orderShop;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public String getFlight() {
		return flight;
	}

	public void setFlight(String flight) {
		this.flight = flight;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getFlightStartDate() {
		return flightStartDate;
	}

	public void setFlightStartDate(String flightStartDate) {
		this.flightStartDate = flightStartDate;
	}

	public String getFlightEndDate() {
		return flightEndDate;
	}

	public void setFlightEndDate(String flightEndDate) {
		this.flightEndDate = flightEndDate;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getJump() {
		return jump;
	}

	public void setJump(Integer jump) {
		this.jump = jump;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "OrderQuery [startDate=" + startDate + ", endDate=" + endDate
				+ ", orderNo=" + orderNo + ", orderSource=" + orderSource
				+ ", orderShop=" + orderShop + ", name=" + name + ", pnr="
				+ pnr + ", ticketNo=" + ticketNo + ", flightStartDate="
				+ flightStartDate + ", flightEndDate=" + flightEndDate
				+ ", flight=" + flight + ", orderStatus=" + orderStatus
				+ ", flightDate=" + flightDate + ", jump=" + jump + ", page="
				+ page + ", limit=" + limit + "]";
	}

	
}
