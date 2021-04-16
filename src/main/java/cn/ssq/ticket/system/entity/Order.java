package cn.ssq.ticket.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;




/**
 * 订单
 */

@TableName("t_order")
public class Order  implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
     * 订单id
     */
	@TableId(value = "order_id", type = IdType.AUTO)
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
     * 订单支付状态
     */
    private String payStatus ;

    /**
     * 订单出票方式
     */
    private String bookWay ;

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

    /**
     * agent用户
     */
    private Integer agentId;
    /**
     * 总金额
     */
    private String totalPrice ;
    /**
     * 总票面价
     */
    private String totalTicketPrice ;
    /**
     * 总燃油机建
     */
    private String totalTax ;
    /**
     * 乘机人数
     */
    private String passengerCount ;
    /**
     * 佣金
     */
    private Double commission ;
    /**
     * 佣金比率
     */
    private Double commissionDiscount ;
    /**
     * 联系人姓名
     */
    private String relationName ;
    /**
     * 联系人手机号
     */
    private String relationMobile ;
    /**
     * 联系人备用电话
     */
    private String relationPhoneBak ;
    /**
     * 联系人邮箱
     */
    private String relationEmail ;
    /**
     * 买家支付宝帐号
     */
    private String accountNo ;
    /**
     * 支付宝交易号
     */
    private String alipayTradeNo ;
    /**
     * 政策id
     */
    private String policyId ;
    /**
     * 机票政策类型
     */
    private String policyType ;
    /**
     * 行程单总金额
     */
    private String traTotalPrice ;    
    /**
     * 行程单状态
     */
    private String traStatus ;

    /**
     * 保险总金额
     */
    private String insureTotalPrice ;
    
    /**
     * 保险状态
     */
    private String insureStatus ;
    /**
     * 退票/废票状态
     */
    private String refundStatus ;
    /**
     * 改签状态
     */
    private String changeStatus ;
    /**
     * 行程单处理状态
     */
    private String travelSendStatus ;
    /**
     * 航变状态
     */
    private String flightChangeStatus ;
    /**
     * 航变统计
     */
    private Integer flightChangeCount ;
    /**
     * 是否来源于接口导入
     */
    private String interfaceImport ;
    /**
     * 通过报表导入次数
     */
    private Integer reportImportCount ;
    /**
     * 订单备注
     */
    private String remark ;
    /**
     * 出票备注
     */
    private String ticketRemark ;
    /**
     * 支付时间
     */
    private Date payDate ;
    /**
     * 更新票号时间
     */
    private String updateTicketDate ;
    /**
     * 更新票号状态
     */
    private String updateTicketStatus ;
    /**
     * 出票人
     */
    private String printTicketBy ;
    /**
     * 出票时间
     */
    private String printTicketDate ;
    /**
     * 外部订单号
     */
    private String outOrderNo ;
    /**
     * 外部政策id
     */
    private String outPolicyId ;
    /**
     * 外部政策返点
     */
    private String outPolicyCommission ;
    /**
     * 外部订单总额
     */
    private String outOrderTotalPiece ;
    /**
     * 外部政策来源
     */
    private String outPolicySource ;
    /**
     * 外部订单更新状态
     */
    private String outOrderStatus ;
    /**
     * 外部订单支付时间
     */
    private String outOrderPaymentDate ;
    
    /**
     * 航班航空公司二字码
     */
    private String airlineCode ;
    /**
     * 航班号
     */
    private String flightNo ;
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
     * 航班起飞日期
     */
    private String flightDate ;

    /**
     * 航班舱位代码
     */
    private String cabin ;
    
    /**
     * 退票备注
     */
    private String refundRemark ;
    
    /**
     * 改签备注
     */
    private String changeRemark ;
    
    /**
     * 航班备注
     */
    private String flightChangeRemark ;
    
    /**
     * 行程单备注
     */
    private String travelRemark ;
    
    /**
     * 订单处理人
     */
    private String processBy ;
    
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
     * 支付方式
     */
    private String reciptWay ;
    
    /**
     * 平台订单创建日期
     */
    private String cAddDate;
    
    /**
     * 政策描述
     */
    private String policyTypeDes;
    
    /**
     * 采购单号
     */
    private String purchaseNo;

    private String bigPnr;
    
    /**
     * 进单/产品类型
     */
    private String ticType;

    private String isAuto;

    private String avStatus;


    private String lastPrintTicketTime;

    //政策备注
    private String policyRemark;
    
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
	/**
     * 总价/人数
     */
    @TableField(exist = false)
    private String priceCount;

    /**
     * 紧急程度
     */
    @TableField(exist = false)
    private Integer degree;

    @TableField(exist = false)
    private String degreeStr;

    /**
     * 相同行程订单数
     */
    @TableField(exist = false)
    private Integer sameCount;
    
    @TableField("pnr_txt")
    private String pnr;
    
    @TableField(exist = false)
    private String remarkStr;


    public String getDegreeStr() {
        return degreeStr;
    }

    public void setDegreeStr(String degreeStr) {
        this.degreeStr = degreeStr;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

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

    public String getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(String isAuto) {
        this.isAuto = isAuto;
    }

    public String getRemarkStr() {
		return remarkStr;
	}
	public void setRemarkStr(String remarkStr) {
		this.remarkStr = remarkStr;
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
	public String getReciptWay() {
		return reciptWay;
	}
	public void setReciptWay(String reciptWay) {
		this.reciptWay = reciptWay;
	}
    
	public String getPnr() {
		return pnr;
	}
	public void setPnr(String pnr) {
		this.pnr = pnr;
	}
	
	public String getPriceCount() {
		return priceCount;
	}
	public void setPriceCount(String priceCount) {
		this.priceCount = priceCount;
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
	public String getcOrderNo() {
		return cOrderNo;
	}
	public void setcOrderNo(String cOrderNo) {
		this.cOrderNo = cOrderNo;
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
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getBookWay() {
		return bookWay;
	}
	public void setBookWay(String bookWay) {
		this.bookWay = bookWay;
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
	public String getTotalTax() {
		return totalTax;
	}
	public void setTotalTax(String totalTax) {
		this.totalTax = totalTax;
	}
	public String getPassengerCount() {
		return passengerCount;
	}
	public void setPassengerCount(String passengerCount) {
		this.passengerCount = passengerCount;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
	public Double getCommissionDiscount() {
		return commissionDiscount;
	}
	public void setCommissionDiscount(Double commissionDiscount) {
		this.commissionDiscount = commissionDiscount;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public String getRelationMobile() {
		return relationMobile;
	}
	public void setRelationMobile(String relationMobile) {
		this.relationMobile = relationMobile;
	}
	public String getRelationPhoneBak() {
		return relationPhoneBak;
	}
	public void setRelationPhoneBak(String relationPhoneBak) {
		this.relationPhoneBak = relationPhoneBak;
	}
	public String getRelationEmail() {
		return relationEmail;
	}
	public void setRelationEmail(String relationEmail) {
		this.relationEmail = relationEmail;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAlipayTradeNo() {
		return alipayTradeNo;
	}
	public void setAlipayTradeNo(String alipayTradeNo) {
		this.alipayTradeNo = alipayTradeNo;
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
	public String getTraStatus() {
		return traStatus;
	}
	public void setTraStatus(String traStatus) {
		this.traStatus = traStatus;
	}
	public String getInsureTotalPrice() {
		return insureTotalPrice;
	}
	public void setInsureTotalPrice(String insureTotalPrice) {
		this.insureTotalPrice = insureTotalPrice;
	}
	public String getInsureStatus() {
		return insureStatus;
	}
	public void setInsureStatus(String insureStatus) {
		this.insureStatus = insureStatus;
	}
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getChangeStatus() {
		return changeStatus;
	}
	public void setChangeStatus(String changeStatus) {
		this.changeStatus = changeStatus;
	}
	public String getTravelSendStatus() {
		return travelSendStatus;
	}
	public void setTravelSendStatus(String travelSendStatus) {
		this.travelSendStatus = travelSendStatus;
	}
	public String getFlightChangeStatus() {
		return flightChangeStatus;
	}
	public void setFlightChangeStatus(String flightChangeStatus) {
		this.flightChangeStatus = flightChangeStatus;
	}
	public Integer getFlightChangeCount() {
		return flightChangeCount;
	}
	public void setFlightChangeCount(Integer flightChangeCount) {
		this.flightChangeCount = flightChangeCount;
	}
	public String getInterfaceImport() {
		return interfaceImport;
	}
	public void setInterfaceImport(String interfaceImport) {
		this.interfaceImport = interfaceImport;
	}
	public Integer getReportImportCount() {
		return reportImportCount;
	}
	public void setReportImportCount(Integer reportImportCount) {
		this.reportImportCount = reportImportCount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTicketRemark() {
		return ticketRemark;
	}
	public void setTicketRemark(String ticketRemark) {
		this.ticketRemark = ticketRemark;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public String getUpdateTicketDate() {
		return updateTicketDate;
	}
	public void setUpdateTicketDate(String updateTicketDate) {
		this.updateTicketDate = updateTicketDate;
	}
	public String getUpdateTicketStatus() {
		return updateTicketStatus;
	}
	public void setUpdateTicketStatus(String updateTicketStatus) {
		this.updateTicketStatus = updateTicketStatus;
	}
	public String getPrintTicketBy() {
		return printTicketBy;
	}
	public void setPrintTicketBy(String printTicketBy) {
		this.printTicketBy = printTicketBy;
	}
	public String getPrintTicketDate() {
		return printTicketDate;
	}
	public void setPrintTicketDate(String printTicketDate) {
		this.printTicketDate = printTicketDate;
	}
	public String getOutOrderNo() {
		return outOrderNo;
	}
	public void setOutOrderNo(String outOrderNo) {
		this.outOrderNo = outOrderNo;
	}
	public String getOutPolicyId() {
		return outPolicyId;
	}
	public void setOutPolicyId(String outPolicyId) {
		this.outPolicyId = outPolicyId;
	}
	public String getOutPolicyCommission() {
		return outPolicyCommission;
	}
	public void setOutPolicyCommission(String outPolicyCommission) {
		this.outPolicyCommission = outPolicyCommission;
	}
	public String getOutOrderTotalPiece() {
		return outOrderTotalPiece;
	}
	public void setOutOrderTotalPiece(String outOrderTotalPiece) {
		this.outOrderTotalPiece = outOrderTotalPiece;
	}
	public String getOutPolicySource() {
		return outPolicySource;
	}
	public void setOutPolicySource(String outPolicySource) {
		this.outPolicySource = outPolicySource;
	}
	public String getOutOrderStatus() {
		return outOrderStatus;
	}
	public void setOutOrderStatus(String outOrderStatus) {
		this.outOrderStatus = outOrderStatus;
	}
	public String getOutOrderPaymentDate() {
		return outOrderPaymentDate;
	}
	public void setOutOrderPaymentDate(String outOrderPaymentDate) {
		this.outOrderPaymentDate = outOrderPaymentDate;
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
	public String getRefundRemark() {
		return refundRemark;
	}
	public void setRefundRemark(String refundRemark) {
		this.refundRemark = refundRemark;
	}
	public String getChangeRemark() {
		return changeRemark;
	}
	public void setChangeRemark(String changeRemark) {
		this.changeRemark = changeRemark;
	}
	public String getFlightChangeRemark() {
		return flightChangeRemark;
	}
	public void setFlightChangeRemark(String flightChangeRemark) {
		this.flightChangeRemark = flightChangeRemark;
	}
	public String getTravelRemark() {
		return travelRemark;
	}
	public void setTravelRemark(String travelRemark) {
		this.travelRemark = travelRemark;
	}
	public String getProcessBy() {
		return processBy;
	}
	public void setProcessBy(String processBy) {
		this.processBy = processBy;
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

	@Override
	public String toString() {
		return "Order{" +
				"orderId=" + orderId +
				", orderNo='" + orderNo + '\'' +
				", cOrderNo='" + cOrderNo + '\'' +
				", tripType='" + tripType + '\'' +
				", status='" + status + '\'' +
				", payStatus='" + payStatus + '\'' +
				", bookWay='" + bookWay + '\'' +
				", orderSource='" + orderSource + '\'' +
				", websiteOrderSource='" + websiteOrderSource + '\'' +
				", orderShop='" + orderShop + '\'' +
				", agentId=" + agentId +
				", totalPrice='" + totalPrice + '\'' +
				", totalTicketPrice='" + totalTicketPrice + '\'' +
				", totalTax='" + totalTax + '\'' +
				", passengerCount='" + passengerCount + '\'' +
				", commission=" + commission +
				", commissionDiscount=" + commissionDiscount +
				", relationName='" + relationName + '\'' +
				", relationMobile='" + relationMobile + '\'' +
				", relationPhoneBak='" + relationPhoneBak + '\'' +
				", relationEmail='" + relationEmail + '\'' +
				", accountNo='" + accountNo + '\'' +
				", alipayTradeNo='" + alipayTradeNo + '\'' +
				", policyId='" + policyId + '\'' +
				", policyType='" + policyType + '\'' +
				", traTotalPrice='" + traTotalPrice + '\'' +
				", traStatus='" + traStatus + '\'' +
				", insureTotalPrice='" + insureTotalPrice + '\'' +
				", insureStatus='" + insureStatus + '\'' +
				", refundStatus='" + refundStatus + '\'' +
				", changeStatus='" + changeStatus + '\'' +
				", travelSendStatus='" + travelSendStatus + '\'' +
				", flightChangeStatus='" + flightChangeStatus + '\'' +
				", flightChangeCount=" + flightChangeCount +
				", interfaceImport='" + interfaceImport + '\'' +
				", reportImportCount=" + reportImportCount +
				", remark='" + remark + '\'' +
				", ticketRemark='" + ticketRemark + '\'' +
				", payDate=" + payDate +
				", updateTicketDate='" + updateTicketDate + '\'' +
				", updateTicketStatus='" + updateTicketStatus + '\'' +
				", printTicketBy='" + printTicketBy + '\'' +
				", printTicketDate='" + printTicketDate + '\'' +
				", outOrderNo='" + outOrderNo + '\'' +
				", outPolicyId='" + outPolicyId + '\'' +
				", outPolicyCommission='" + outPolicyCommission + '\'' +
				", outOrderTotalPiece='" + outOrderTotalPiece + '\'' +
				", outPolicySource='" + outPolicySource + '\'' +
				", outOrderStatus='" + outOrderStatus + '\'' +
				", outOrderPaymentDate='" + outOrderPaymentDate + '\'' +
				", airlineCode='" + airlineCode + '\'' +
				", flightNo='" + flightNo + '\'' +
				", flightType='" + flightType + '\'' +
				", depCityCode='" + depCityCode + '\'' +
				", arrCityCode='" + arrCityCode + '\'' +
				", flightDate='" + flightDate + '\'' +
				", cabin='" + cabin + '\'' +
				", refundRemark='" + refundRemark + '\'' +
				", changeRemark='" + changeRemark + '\'' +
				", flightChangeRemark='" + flightChangeRemark + '\'' +
				", travelRemark='" + travelRemark + '\'' +
				", processBy='" + processBy + '\'' +
				", createBy='" + createBy + '\'' +
				", createDate='" + createDate + '\'' +
				", modifyBy='" + modifyBy + '\'' +
				", modifyDate='" + modifyDate + '\'' +
				", reciptWay='" + reciptWay + '\'' +
				", cAddDate='" + cAddDate + '\'' +
				", policyTypeDes='" + policyTypeDes + '\'' +
				", purchaseNo='" + purchaseNo + '\'' +
				", bigPnr='" + bigPnr + '\'' +
				", ticType='" + ticType + '\'' +
				", isAuto='" + isAuto + '\'' +
				", lastPrintTicketTime='" + lastPrintTicketTime + '\'' +
				", policyRemark='" + policyRemark + '\'' +
				", priceCount='" + priceCount + '\'' +
				", sameCount=" + sameCount +
				", pnr='" + pnr + '\'' +
				", remarkStr='" + remarkStr + '\'' +
				'}';
	}


}
	
