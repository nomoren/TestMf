package cn.ssq.ticket.system.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;




/**
 * 订单vo
 */
public class OrderVO  implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo ;
    
    /**
     * C站订单号
     */
    private String cOrderNo ;

    /**
     * 航程类型
     */
    private String tripType ;

    /**
     * 订单状态
     */
    private String status ;
    /**
     * 总金额
     */
    private String totalPrice ;
    /**
     * 总票面价
     */
    private String totalTicketPrice ;
    /**
     * 乘机人数
     */
    private String passengerCount ;
    /**
     * 政策id
     */
    private String policyId ;
    
    private String policyTypeDes;
    /**
     * 机票政策类型
     */
    private String policyType ;
    /**
     * 行程单总金额
     */
    private String traTotalPrice ;    
    /**
     * 航班类型
     */
    private String flightType ;
    /**
     * 航班出发城市三字码
     */
    private String depCityCode ;
    /**
     * 航班到达城市三字码
     */
    private String arrCityCode ;
    /**
     * 机建
     */
    private String totalTax;
  
    /**
     * 航班起飞日期
     */
    private String flightDate ;
    /**
     * 舱位代码
     */
    private String cabin;
    
    /**
     * 出票备注
     */
    private String ticketRemark ;
    
    /**
     * 支付方式
     */
    private String reciptWay ;
    
    /**
     * 订单来源
     */
    private String orderSource ;

	/**
	 * 官网看到的订单来源
	 */
	private String websiteOrderSource ;

    /**
     * 店铺
     */
    private String orderShop ;
    
    private String policyRemark;
    
    private String pnr;
    
    private String bigPnr;
    
    private String flightNo;
  
    /**
     * 平台订单创建日期
     */
    private String cAddDate;
    
    /**
     * 联系人名
     */
    private String relationName;
    
    /**
     * 行程单状态
     */
    private String traStatus ;

  
    private Date createDate;

    private String flightDateNo;
    
    private String priceCount;
   
	private String processBy;
	
	private String purchaseNo;
    
	private String ticType;
	
    private List<Passenger> passengetList;

	private List<Flight> flightList;
	
	private List<Purchase> purchList;
    
    private Travel travel;
    
    private String lastPrintTicketTime;
    
    private String reamrkStr;

    private String refundPrice;

    private String refundReason;
    
    private Order order;


    private Integer sameCount;

    private String avStatus;

    public String getAvStatus() {
        return avStatus;
    }

    public void setAvStatus(String avStatus) {
        this.avStatus = avStatus;
    }

    public String getWebsiteOrderSource() {
        return websiteOrderSource;
    }

    public void setWebsiteOrderSource(String websiteOrderSource) {
        this.websiteOrderSource = websiteOrderSource;
    }

    public Integer getSameCount() {
        return sameCount;
    }

    public void setSameCount(Integer sameCount) {
        this.sameCount = sameCount;
    }

    public String getRefundPrice() {
		return refundPrice;
	}

	public void setRefundPrice(String refundPrice) {
		this.refundPrice = refundPrice;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getTraStatus() {
		return traStatus;
	}

	public void setTraStatus(String traStatus) {
		this.traStatus = traStatus;
	}

	public String getPolicyTypeDes() {
		return policyTypeDes;
	}

	public void setPolicyTypeDes(String policyTypeDes) {
		this.policyTypeDes = policyTypeDes;
	}

	public String getTicType() {
		return ticType;
	}

	public void setTicType(String ticType) {
		this.ticType = ticType;
	}

	public String getPolicyRemark() {
		return policyRemark;
	}

	public void setPolicyRemark(String policyRemark) {
		this.policyRemark = policyRemark;
	}

	public String getReamrkStr() {
		return reamrkStr;
	}

	public void setReamrkStr(String reamrkStr) {
		this.reamrkStr = reamrkStr;
	}

	public String getLastPrintTicketTime() {
		return lastPrintTicketTime;
	}

	public void setLastPrintTicketTime(String lastPrintTicketTime) {
		this.lastPrintTicketTime = lastPrintTicketTime;
	}

	public String getBigPnr() {
		return bigPnr;
	}

	public void setBigPnr(String bigPnr) {
		this.bigPnr = bigPnr;
	}

	public String getcAddDate() {
		return cAddDate;
	}

	public void setcAddDate(String cAddDate) {
		this.cAddDate = cAddDate;
	}

	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public List<Purchase> getPurchList() {
		return purchList;
	}

	public void setPurchList(List<Purchase> purchList) {
		this.purchList = purchList;
	}

	public String getProcessBy() {
		return processBy;
	}

	public void setProcessBy(String processBy) {
		this.processBy = processBy;
	}

	public String getFlightDateNo() {
		return flightDateNo;
	}

	public void setFlightDateNo(String flightDateNo) {
		this.flightDateNo = flightDateNo;
	}

	public String getPriceCount() {
		return priceCount;
	}

	public void setPriceCount(String priceCount) {
		this.priceCount = priceCount;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
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

	public String getcOrderNo() {
		return cOrderNo;
	}


	public void setcOrderNo(String cOrderNo) {
		this.cOrderNo = cOrderNo;
	}


	public String getReciptWay() {
		return reciptWay;
	}


	public void setReciptWay(String reciptWay) {
		this.reciptWay = reciptWay;
	}


	public String getTicketRemark() {
		return ticketRemark;
	}


	public void setTicketRemark(String ticketRemark) {
		this.ticketRemark = ticketRemark;
	}


	public String getRelationName() {
		return relationName;
	}


	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}


	public String getCabin() {
		return cabin;
	}


	public void setCabin(String cabin) {
		this.cabin = cabin;
	}


	public String getTotalTax() {
		return totalTax;
	}


	public void setTotalTax(String totalTax) {
		this.totalTax = totalTax;
	}

	public List<Passenger> getPassengetList() {
		return passengetList;
	}


	public void setPassengetList(List<Passenger> passengetList) {
		this.passengetList = passengetList;
	}


	public List<Flight> getFlightList() {
		return flightList;
	}


	public void setFlightList(List<Flight> flightList) {
		this.flightList = flightList;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTripType() {
		return tripType;
	}

	public void setTripType(String tripType) {
		this.tripType = tripType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getTotalTicketPrice() {
		return totalTicketPrice;
	}

	public void setTotalTicketPrice(String totalTicketPrice) {
		this.totalTicketPrice = totalTicketPrice;
	}

	public String getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(String passengerCount) {
		this.passengerCount = passengerCount;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getTraTotalPrice() {
		return traTotalPrice;
	}

	public void setTraTotalPrice(String traTotalPrice) {
		this.traTotalPrice = traTotalPrice;
	}

	public String getFlightType() {
		return flightType;
	}

	public void setFlightType(String flightType) {
		this.flightType = flightType;
	}

	public String getDepCityCode() {
		return depCityCode;
	}

	public void setDepCityCode(String depCityCode) {
		this.depCityCode = depCityCode;
	}

	public String getArrCityCode() {
		return arrCityCode;
	}

	public void setArrCityCode(String arrCityCode) {
		this.arrCityCode = arrCityCode;
	}

	public String getFlightDate() {
		return flightDate;
	}

	public void setFlightDate(String flightDate) {
		this.flightDate = flightDate;
	}


	public Travel getTravel() {
		return travel;
	}


	public void setTravel(Travel travel) {
		this.travel = travel;
	}

    @Override
    public String toString() {
        return "OrderVO{" +
                "orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", cOrderNo='" + cOrderNo + '\'' +
                ", tripType='" + tripType + '\'' +
                ", status='" + status + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", totalTicketPrice='" + totalTicketPrice + '\'' +
                ", passengerCount='" + passengerCount + '\'' +
                ", policyId='" + policyId + '\'' +
                ", policyTypeDes='" + policyTypeDes + '\'' +
                ", policyType='" + policyType + '\'' +
                ", traTotalPrice='" + traTotalPrice + '\'' +
                ", flightType='" + flightType + '\'' +
                ", depCityCode='" + depCityCode + '\'' +
                ", arrCityCode='" + arrCityCode + '\'' +
                ", totalTax='" + totalTax + '\'' +
                ", flightDate='" + flightDate + '\'' +
                ", cabin='" + cabin + '\'' +
                ", ticketRemark='" + ticketRemark + '\'' +
                ", reciptWay='" + reciptWay + '\'' +
                ", orderSource='" + orderSource + '\'' +
                ", websiteOrderSource='" + websiteOrderSource + '\'' +
                ", orderShop='" + orderShop + '\'' +
                ", policyRemark='" + policyRemark + '\'' +
                ", pnr='" + pnr + '\'' +
                ", bigPnr='" + bigPnr + '\'' +
                ", flightNo='" + flightNo + '\'' +
                ", cAddDate='" + cAddDate + '\'' +
                ", relationName='" + relationName + '\'' +
                ", traStatus='" + traStatus + '\'' +
                ", createDate=" + createDate +
                ", flightDateNo='" + flightDateNo + '\'' +
                ", priceCount='" + priceCount + '\'' +
                ", processBy='" + processBy + '\'' +
                ", purchaseNo='" + purchaseNo + '\'' +
                ", ticType='" + ticType + '\'' +
                ", passengetList=" + passengetList +
                ", flightList=" + flightList +
                ", purchList=" + purchList +
                ", travel=" + travel +
                ", lastPrintTicketTime='" + lastPrintTicketTime + '\'' +
                ", reamrkStr='" + reamrkStr + '\'' +
                ", refundPrice='" + refundPrice + '\'' +
                ", refundReason='" + refundReason + '\'' +
                ", order=" + order +
                ", sameCount=" + sameCount +
                '}';
    }


}
	
