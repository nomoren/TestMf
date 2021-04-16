package cn.ssq.ticket.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;


/** 
 * 
 */
@TableName("t_refund")
public class Refund  implements Serializable
{

	private static final long serialVersionUID = 6178517769385273776L;

	/**
     * id
     */
	@TableId(value = "refund_id", type = IdType.AUTO)
    private Long refundId ;

    /**
     * 订单id
     */
    private Long orderId ;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单来源
     */
    private String orderSource;
    /**
     * 店铺
     */
    private String orderShop;
    /**
     * 对应agent用户
     */
    private Integer agentId;

    /**
     * 乘客id
     */
    private  Long passengerId;
    
    /**
     * 乘客退废票类型
     */
    private String refundType;

    /**
     * 航司申请类型
     */
    private String airRefundType;

    /**
     * 航班航空公司二字码
     */
    private String airlineCode;
    /**
     * 航班号
     */
    
    private String flightNo;
    /**
     * 航班机型
     */
    private String flightType;
    /**
     * 航班出发城市三字码
     */
    private String depCityCode;
    /**
     * 航班到达城市三字码
     */
    private String arrCityCode;
    /**
     * 航班起飞日期
     */
    private String flightDate;

    /**
     * 航班舱位代码
     */
    private String cabin;
    /**
     * 出票舱位
     */
    private String printTicketCabin;

    /**
     * 实退公司票面价
     */
    private String cpTktPrice ;
    /**
     * 公司费率
     */
    private String cpRetRate ;
    /**
     * 公司费
     */
    private String cpRetPrice ;
    /**
     * 公司实退
     */
    
    private String cpRealPrice ;
    /**
     * 公司申请日期
     */
    private String cpAppDate ;
    /**
     * 公司日期
     */
    private String cpRemDate ;
    /**
     * 公司操作人
     */
    private String cpRemBy ;
    /**
     * 实退客户票面价
     */
    
    private String cRealTPrice ;
    /**
     * 客户费率
     */
    private String cRetRate ;
    /**
     * 客户费
     */
    private String cRetPrice ;
    /**
     * 客户实退
     */
    private Double cRealPrice ;
    /**
     * 实退客户总计
     */
    private Double cRealRetreatTotal ;
    /**
     * 客户申请退款日期
     */
    private String cAppDate ;
    /**
     * 客户退款日期
     */
   // @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private String cRemDate ;
    /**
     * 退款操作人
     */
    private String cRemBy ;
    /**
     * 实退航空公司票面价
     */
    private String airRealTPrice ;
    /**
     * 航空费率
     */
    private String airRetRate ;
    /**
     * 航空费
     */
    private String airRetPrice ;
    
    /**
     * 航空预退
     */
    private BigDecimal airEstimatePrice ;
    /**
     * 航空实退
     */
    private BigDecimal airRealPrice ;
    /**
     * 航空申请日期
     */
    //@JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private String airAppDate ;
    /**
     * 航空操作人
     */
    private String airRetBy;
    /**
     * 航空退款日期
     */
    private String airRemDate ;
    
    /**
     * 利润（公乘）
     */
    private String cpProfit ;
    
    /**
     * 利润（航公）
     */
    private String airProfit ;
    
    /**
     * 利润（航乘）
     */
    private String acProfit ;
    
    /**
     * 差额
     */
    private String balance ;
    /**
     * 利润
     */
    private String profit ;
    /**
     * 退快递费
     */
    private String reexPrice ;
    /**
     * 退保险费
     */
    private String insutePrice ;
    /**
     * 单号
     */
    private String retNo;
    /**
     * 退款工号
     */
    private String remJobNo;
    /**
     * 状态
     */
    private String retState ;
    /**
     * 备注
     */
    private String remark;
    /**
     * 退款备注
     */
    @TableField("ORDERREMARK")
    private String orderRemark ;
    /**
     * 航班是否正常
     */
    private String flightStatus;
    /**
     * 客户退款状态
     */
    private String cRemState ;
    /**
     * 航空公司退款状态
     */
    private String airRemState ;
    /**
     * 公司退款状态
     */
    private String cpRemState ;

    /**
     * 取消pnr状态
     */
    @TableField("XEPNR_STATUS")
    private String xePnrStatus ;
    /**
     * 取消pnr人
     */
    @TableField("XEPNR_by")
    private String xePnrBy ;
    /**
     * 取消pnr日期
     */
    @TableField("XEPNR_DATE")
    private String xePnrDate ;
    
    /**
     * 退行程单状态
     */
    private String reTravelStatus ;
    
    /**
     * 乘客人名
     */
    private String passengerName ;
    
    /**
     * 录入人
     */
    private String createBy ;
    /**
     * 录入日期
     */
    private String createDate ;
    /**
     * 修改人
     */
    private String modifyBy ;
    /**
     * 修改日期
     */
    private String modifyDate ;
    /**
     * 等延误
     */
    private String isDelay;
    /**
     * 票号
     */
    private String ticketNo ;
    
    /**
     * 是否已退款
     */
    private String isRefund;
    
    private String processBy;

    private String pnr;

    private String flightTime;

    private String isAuto;

    private String policyType;

    private String outOrderNo;
    
    @TableField(exist = false)
    private String otherRemark;
    
    /**
     * 航班日期-航班号
     */
    @TableField(exist = false)
    private String flightDateNo;

    private String purchPalse;
    
    private String businessNo;//商户订单号

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public String getProcessBy() {
		return processBy;
	}

	public void setProcessBy(String processBy) {
		this.processBy = processBy;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public String getPurchPalse() {
		return purchPalse;
	}

	public void setPurchPalse(String purchPalse) {
		this.purchPalse = purchPalse;
	}

	public String getFlightDateNo() {
		return flightDateNo;
	}

	public void setFlightDateNo(String flightDateNo) {
		this.flightDateNo = flightDateNo;
	}

	public Long getRefundId() {
		return refundId;
	}

	public void setRefundId(Long refundId) {
		this.refundId = refundId;
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

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public String getRefundType() {
		return refundType;
	}

	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}

	public String getAirlineCode() {
		return airlineCode;
	}

	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
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

	public String getCabin() {
		return cabin;
	}

	public void setCabin(String cabin) {
		this.cabin = cabin;
	}

	public String getPrintTicketCabin() {
		return printTicketCabin;
	}

	public void setPrintTicketCabin(String printTicketCabin) {
		this.printTicketCabin = printTicketCabin;
	}

	public String getCpTktPrice() {
		return cpTktPrice;
	}

	public void setCpTktPrice(String cpTktPrice) {
		this.cpTktPrice = cpTktPrice;
	}

	public String getCpRetRate() {
		return cpRetRate;
	}

	public void setCpRetRate(String cpRetRate) {
		this.cpRetRate = cpRetRate;
	}

	public String getCpRetPrice() {
		return cpRetPrice;
	}

	public void setCpRetPrice(String cpRetPrice) {
		this.cpRetPrice = cpRetPrice;
	}

	public String getCpRealPrice() {
		return cpRealPrice;
	}

	public void setCpRealPrice(String cpRealPrice) {
		this.cpRealPrice = cpRealPrice;
	}

	public String getCpAppDate() {
		return cpAppDate;
	}

	public void setCpAppDate(String cpAppDate) {
		this.cpAppDate = cpAppDate;
	}

	public String getCpRemDate() {
		return cpRemDate;
	}

	public void setCpRemDate(String cpRemDate) {
		this.cpRemDate = cpRemDate;
	}

	public String getCpRemBy() {
		return cpRemBy;
	}

	public void setCpRemBy(String cpRemBy) {
		this.cpRemBy = cpRemBy;
	}

	public String getcRealTPrice() {
		return cRealTPrice;
	}

	public void setcRealTPrice(String cRealTPrice) {
		this.cRealTPrice = cRealTPrice;
	}

	public String getcRetRate() {
		return cRetRate;
	}

	public void setcRetRate(String cRetRate) {
		this.cRetRate = cRetRate;
	}

	public String getcRetPrice() {
		return cRetPrice;
	}

	public void setcRetPrice(String cRetPrice) {
		this.cRetPrice = cRetPrice;
	}



	public Double getcRealPrice() {
		return cRealPrice;
	}

	public void setcRealPrice(Double cRealPrice) {
		this.cRealPrice = cRealPrice;
	}

	public Double getcRealRetreatTotal() {
		return cRealRetreatTotal;
	}

	public void setcRealRetreatTotal(Double cRealRetreatTotal) {
		this.cRealRetreatTotal = cRealRetreatTotal;
	}

	public String getcAppDate() {
		return cAppDate;
	}

	public void setcAppDate(String cAppDate) {
		this.cAppDate = cAppDate;
	}

	public String getcRemDate() {
		return cRemDate;
	}

	public void setcRemDate(String cRemDate) {
		this.cRemDate = cRemDate;
	}

	public String getcRemBy() {
		return cRemBy;
	}

	public void setcRemBy(String cRemBy) {
		this.cRemBy = cRemBy;
	}

	public String getAirRealTPrice() {
		return airRealTPrice;
	}

	public void setAirRealTPrice(String airRealTPrice) {
		this.airRealTPrice = airRealTPrice;
	}

	public String getAirRetRate() {
		return airRetRate;
	}

	public void setAirRetRate(String airRetRate) {
		this.airRetRate = airRetRate;
	}

	public String getAirRetPrice() {
		return airRetPrice;
	}

	public void setAirRetPrice(String airRetPrice) {
		this.airRetPrice = airRetPrice;
	}

	public BigDecimal getAirEstimatePrice() {
		return airEstimatePrice;
	}

	public void setAirEstimatePrice(BigDecimal airEstimatePrice) {
		this.airEstimatePrice = airEstimatePrice;
	}

	public BigDecimal getAirRealPrice() {
		return airRealPrice;
	}

	public void setAirRealPrice(BigDecimal airRealPrice) {
		this.airRealPrice = airRealPrice;
	}

	public String getAirAppDate() {
		return airAppDate;
	}

	public void setAirAppDate(String airAppDate) {
		this.airAppDate = airAppDate;
	}

	public String getAirRetBy() {
		return airRetBy;
	}

	public void setAirRetBy(String airRetBy) {
		this.airRetBy = airRetBy;
	}

	public String getAirRemDate() {
		return airRemDate;
	}

	public void setAirRemDate(String airRemDate) {
		this.airRemDate = airRemDate;
	}

	public String getCpProfit() {
		return cpProfit;
	}

	public void setCpProfit(String cpProfit) {
		this.cpProfit = cpProfit;
	}

	public String getAirProfit() {
		return airProfit;
	}

	public void setAirProfit(String airProfit) {
		this.airProfit = airProfit;
	}

	public String getAcProfit() {
		return acProfit;
	}

	public void setAcProfit(String acProfit) {
		this.acProfit = acProfit;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public String getReexPrice() {
		return reexPrice;
	}

	public void setReexPrice(String reexPrice) {
		this.reexPrice = reexPrice;
	}

	public String getInsutePrice() {
		return insutePrice;
	}

	public void setInsutePrice(String insutePrice) {
		this.insutePrice = insutePrice;
	}

	public String getRetNo() {
		return retNo;
	}

	public void setRetNo(String retNo) {
		this.retNo = retNo;
	}

	public String getRemJobNo() {
		return remJobNo;
	}

	public void setRemJobNo(String remJobNo) {
		this.remJobNo = remJobNo;
	}

	public String getRetState() {
		return retState;
	}

	public void setRetState(String retState) {
		this.retState = retState;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOrderRemark() {
		return orderRemark;
	}

	public void setOrderRemark(String orderRemark) {
		this.orderRemark = orderRemark;
	}

	public String getFlightStatus() {
		return flightStatus;
	}

	public void setFlightStatus(String flightStatus) {
		this.flightStatus = flightStatus;
	}

	public String getcRemState() {
		return cRemState;
	}

	public void setcRemState(String cRemState) {
		this.cRemState = cRemState;
	}

	public String getAirRemState() {
		return airRemState;
	}

	public void setAirRemState(String airRemState) {
		this.airRemState = airRemState;
	}

	public String getCpRemState() {
		return cpRemState;
	}

	public void setCpRemState(String cpRemState) {
		this.cpRemState = cpRemState;
	}

	public String getXePnrStatus() {
		return xePnrStatus;
	}

	public void setXePnrStatus(String xePnrStatus) {
		this.xePnrStatus = xePnrStatus;
	}

	public String getXePnrBy() {
		return xePnrBy;
	}

	public void setXePnrBy(String xePnrBy) {
		this.xePnrBy = xePnrBy;
	}

	public String getXePnrDate() {
		return xePnrDate;
	}

	public void setXePnrDate(String xePnrDate) {
		this.xePnrDate = xePnrDate;
	}

	public String getReTravelStatus() {
		return reTravelStatus;
	}

	public void setReTravelStatus(String reTravelStatus) {
		this.reTravelStatus = reTravelStatus;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getIsDelay() {
		return isDelay;
	}

	public void setIsDelay(String isDelay) {
		this.isDelay = isDelay;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public String getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(String isRefund) {
		this.isRefund = isRefund;
	}

    public String getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(String flightTime) {
        this.flightTime = flightTime;
    }

    public String getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(String isAuto) {
        this.isAuto = isAuto;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getAirRefundType() {
        return airRefundType;
    }

    public void setAirRefundType(String airRefundType) {
        this.airRefundType = airRefundType;
    }

    @Override
    public String toString() {
        return "Refund{" +
                "refundId=" + refundId +
                ", orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", orderSource='" + orderSource + '\'' +
                ", orderShop='" + orderShop + '\'' +
                ", agentId=" + agentId +
                ", passengerId=" + passengerId +
                ", refundType='" + refundType + '\'' +
                ", airlineCode='" + airlineCode + '\'' +
                ", flightNo='" + flightNo + '\'' +
                ", flightType='" + flightType + '\'' +
                ", depCityCode='" + depCityCode + '\'' +
                ", arrCityCode='" + arrCityCode + '\'' +
                ", flightDate='" + flightDate + '\'' +
                ", cabin='" + cabin + '\'' +
                ", printTicketCabin='" + printTicketCabin + '\'' +
                ", cpTktPrice='" + cpTktPrice + '\'' +
                ", cpRetRate='" + cpRetRate + '\'' +
                ", cpRetPrice='" + cpRetPrice + '\'' +
                ", cpRealPrice='" + cpRealPrice + '\'' +
                ", cpAppDate='" + cpAppDate + '\'' +
                ", cpRemDate='" + cpRemDate + '\'' +
                ", cpRemBy='" + cpRemBy + '\'' +
                ", cRealTPrice='" + cRealTPrice + '\'' +
                ", cRetRate='" + cRetRate + '\'' +
                ", cRetPrice='" + cRetPrice + '\'' +
                ", cRealPrice=" + cRealPrice +
                ", cRealRetreatTotal=" + cRealRetreatTotal +
                ", cAppDate='" + cAppDate + '\'' +
                ", cRemDate='" + cRemDate + '\'' +
                ", cRemBy='" + cRemBy + '\'' +
                ", airRealTPrice='" + airRealTPrice + '\'' +
                ", airRetRate='" + airRetRate + '\'' +
                ", airRetPrice='" + airRetPrice + '\'' +
                ", airEstimatePrice=" + airEstimatePrice +
                ", airRealPrice=" + airRealPrice +
                ", airAppDate='" + airAppDate + '\'' +
                ", airRetBy='" + airRetBy + '\'' +
                ", airRemDate='" + airRemDate + '\'' +
                ", cpProfit='" + cpProfit + '\'' +
                ", airProfit='" + airProfit + '\'' +
                ", acProfit='" + acProfit + '\'' +
                ", balance='" + balance + '\'' +
                ", profit='" + profit + '\'' +
                ", reexPrice='" + reexPrice + '\'' +
                ", insutePrice='" + insutePrice + '\'' +
                ", retNo='" + retNo + '\'' +
                ", remJobNo='" + remJobNo + '\'' +
                ", retState='" + retState + '\'' +
                ", remark='" + remark + '\'' +
                ", orderRemark='" + orderRemark + '\'' +
                ", flightStatus='" + flightStatus + '\'' +
                ", cRemState='" + cRemState + '\'' +
                ", airRemState='" + airRemState + '\'' +
                ", cpRemState='" + cpRemState + '\'' +
                ", xePnrStatus='" + xePnrStatus + '\'' +
                ", xePnrBy='" + xePnrBy + '\'' +
                ", xePnrDate='" + xePnrDate + '\'' +
                ", reTravelStatus='" + reTravelStatus + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createDate='" + createDate + '\'' +
                ", modifyBy='" + modifyBy + '\'' +
                ", modifyDate='" + modifyDate + '\'' +
                ", isDelay='" + isDelay + '\'' +
                ", ticketNo='" + ticketNo + '\'' +
                ", isRefund='" + isRefund + '\'' +
                ", processBy='" + processBy + '\'' +
                ", pnr='" + pnr + '\'' +
                ", flightTime='" + flightTime + '\'' +
                ", isAuto='" + isAuto + '\'' +
                ", policyType='" + policyType + '\'' +
                ", otherRemark='" + otherRemark + '\'' +
                ", flightDateNo='" + flightDateNo + '\'' +
                ", purchPalse='" + purchPalse + '\'' +
                ", businessNo='" + businessNo + '\'' +
                '}';
    }


}
