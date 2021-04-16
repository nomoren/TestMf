package cn.ssq.ticket.system.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class RefundInfo implements Serializable{

	private static final long serialVersionUID = 1L;

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
     * 航空申请日期
     */
    private String airAppDate ;
    
    /**
     * 航空退款日期
     */
    private String airRemDate ;
    
    private String isRet;
    
    /**
     * 退款总额
     */
    private BigDecimal cRealPrice;

    private String passengerName;

    private String xePnrStatus;

    public String getXePnrStatus() {

        return xePnrStatus;
    }

    public void setXePnrStatus(String xePnrStatus) {
        this.xePnrStatus = xePnrStatus;
    }

    public String getIsRet() {
		return isRet;
	}

	public void setIsRet(String isRet) {
		this.isRet = isRet;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
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

	public String getAirAppDate() {
		return airAppDate;
	}

	public void setAirAppDate(String airAppDate) {
		this.airAppDate = airAppDate;
	}

	public String getAirRemDate() {
		return airRemDate;
	}

	public void setAirRemDate(String airRemDate) {
		this.airRemDate = airRemDate;
	}

	public BigDecimal getcRealPrice() {
		return cRealPrice;
	}

	public void setcRealPrice(BigDecimal cRealPrice) {
		this.cRealPrice = cRealPrice;
	}

    @Override
    public String toString() {
        return "RefundInfo{" +
                "cAppDate='" + cAppDate + '\'' +
                ", cRemDate='" + cRemDate + '\'' +
                ", airAppDate='" + airAppDate + '\'' +
                ", airRemDate='" + airRemDate + '\'' +
                ", isRet='" + isRet + '\'' +
                ", cRealPrice=" + cRealPrice +
                ", passengerName='" + passengerName + '\'' +
                ", xePnrStatus='" + xePnrStatus + '\'' +
                '}';
    }


}
