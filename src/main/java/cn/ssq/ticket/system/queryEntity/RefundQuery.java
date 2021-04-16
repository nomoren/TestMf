package cn.ssq.ticket.system.queryEntity;

public class RefundQuery {

	//查询退票单号
	private String retNo;
	//航班号
	private String flightNo;
	//退票状态(乘)
	private String cRemState;
	//退票状态(供)
	private String airRemState;
	//申请日期开始(乘)
	private String cAppStartDate;
	//申请日期结束(乘)
	private String cAppEndDate;
	//航班开始时间
	private String flightStartDate;
	//航班结束时间
	private String flightEndDate;
	//订单号
	private String orderNo;
	//乘机人姓名
	private String name;
	//票号
	private String ticketNo;
	//订单来源
	private String orderSource;
	//店铺
	private String orderShop;
	//编码状态
	private String xepnrStatus;
	//锁定人
    private String processBy;

    private String isAuto;

	private Integer jump;

	private Integer page;

    public String getProcessBy() {
        return processBy;
    }

    public void setProcessBy(String processBy) {
        this.processBy = processBy;
    }

    public String getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(String isAuto) {
        this.isAuto = isAuto;
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

	public String getRetNo() {
		return retNo;
	}

	public void setRetNo(String retNo) {
		this.retNo = retNo;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getcRemState() {
		return cRemState;
	}

	public void setcRemState(String cRemState) {
		this.cRemState = cRemState;
	}

	public String getcAppStartDate() {
		return cAppStartDate;
	}

	public void setcAppStartDate(String cAppStartDate) {
		this.cAppStartDate = cAppStartDate;
	}

	public String getcAppEndDate() {
		return cAppEndDate;
	}

	public void setcAppEndDate(String cAppEndDate) {
		this.cAppEndDate = cAppEndDate;
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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
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

	public String getAirRemState() {
		return airRemState;
	}

	public void setAirRemState(String airRemState) {
		this.airRemState = airRemState;
	}

	public String getXepnrStatus() {
		return xepnrStatus;
	}

	public void setXepnrStatus(String xepnrStatus) {
		this.xepnrStatus = xepnrStatus;
	}

    @Override
    public String toString() {
        return "RefundQuery{" +
                "retNo='" + retNo + '\'' +
                ", flightNo='" + flightNo + '\'' +
                ", cRemState='" + cRemState + '\'' +
                ", airRemState='" + airRemState + '\'' +
                ", cAppStartDate='" + cAppStartDate + '\'' +
                ", cAppEndDate='" + cAppEndDate + '\'' +
                ", flightStartDate='" + flightStartDate + '\'' +
                ", flightEndDate='" + flightEndDate + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", name='" + name + '\'' +
                ", ticketNo='" + ticketNo + '\'' +
                ", orderSource='" + orderSource + '\'' +
                ", orderShop='" + orderShop + '\'' +
                ", xepnrStatus='" + xepnrStatus + '\'' +
                ", isAuto='" + isAuto + '\'' +
                ", jump=" + jump +
                ", page=" + page +
                '}';
    }
}
