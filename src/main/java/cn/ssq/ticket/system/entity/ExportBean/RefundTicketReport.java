package cn.ssq.ticket.system.entity.ExportBean;

import cn.ssq.ticket.system.entity.RefundVO;
import cn.ssq.ticket.system.util.DictUtils;
import cn.stylefeng.guns.core.common.annotion.ExcelField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;


/** 
 * 退票报表
 */
public class RefundTicketReport extends RefundVO {
	
	private static final long serialVersionUID = 1L;

	@ExcelField(title="录入时间   ", align=1, sort=10)
	public String getCreateDate(){
		return super.getCreateDate();
	}
	@ExcelField(title="订单来源", align=1, sort=15)
	public String getOrderSource(){
		return  DictUtils.getDictName("order_source", super.getOrderSource())+super.getOrderShop();
	}
	@ExcelField(title="订单号        ", align=1, sort=20)
	public String getOrderNo(){
		return super.getOrderNo();
	}
	@ExcelField(title="政策代码", align=3, sort=30)
	public String getPolicy(){
		return super.getPassenger().getPolicyType();
	}
	@ExcelField(title="航班号", align=3, sort=40)
	public String getFlightNo(){
		return super.getFlightNo();
	}
	@ExcelField(title="航段", align=3, sort=50)
	public String getAirlin(){
		return super.getDepCityCode()+"-"+super.getArrCityCode();
	}
	@ExcelField(title="航班日期", align=1, sort=60)
	public String getFlightDate(){
		return super.getFlightDate();
	}
	@ExcelField(title="出票舱位", align=1, sort=70)
	public String getCabin(){
		if(super.getPassenger()!=null){			
			return super.getPassenger().getPrintTicketCabin();
		}
		return "";
	}
	@ExcelField(title="采购价", align=1, sort=80)
	public String getPay(){
		if(super.getPurchase()!=null){			
			return super.getPurchase().getPayAmount().toString();
		}
		return "";
	}
	@ExcelField(title="采购地", align=1, sort=85)
	public String getSupplier(){
		if(super.getPurchase()!=null){			
			return super.getPurchase().getSupplier();
		}
		return "";
	}
	@ExcelField(title="支付方式", align=1, sort=90)
	public String getPayWay(){
		return DictUtils.getDictName("order_recipt_way", super.getPurchase().getPayWay());
	}

	@ExcelField(title="交易流水号", align=1, sort=91)
	public String getTradeNo(){
		return super.getBusinessNo();
	}

	@ExcelField(title="票号      ", align=1, sort=100)
	public String getTicketNo(){
		return super.getTicketNo();
	}
	@ExcelField(title="退票乘客", align=1, sort=180)
	public String getName(){
		return super.getPassengerName();
	}
    @ExcelField(title="申请类型", align=1, sort=181)
    public String getRefundType(){
        String refundType = super.getRefundType();
        if(StringUtils.isEmpty(refundType)){
            refundType="2";
        }
        return DictUtils.getDictName("refund_type_passenger",refundType);
    }
	@ExcelField(title="退票单号     ", align=1, sort=185)
	public String getRetNo(){
		return super.getRetNo();
	}
	@ExcelField(title="出票时间", align=1, sort=186)
	public String getPrintDate(){
		if(super.getPassenger()!=null){			
			return super.getPassenger().getPrintTicketDate();
		}
		return "";
	}
	@ExcelField(title="出票员", align=1, sort=187)
	public String getPrintBy(){
		if(super.getPassenger()!=null){			
			//return super.getPassenger().getPrintTicketBy();
			return DictUtils.getDictName("ssq_user", super.getPassenger().getPrintTicketBy());
		}
		return "";
	}
	
	@ExcelField(title="实退(乘)", align=1, sort=190)
	public String getcRealPrice2(){
		if(null==super.getcRealPrice()){
			return null;
		}
		return super.getcRealPrice().toString();
	}
	@ExcelField(title="退票员", align=1, sort=205)
	public String getCreateBy(){
		//return super.getProcessBy();
		return DictUtils.getDictName("ssq_user", super.getProcessBy());
	}
	@ExcelField(title="退票状态(供)", align=1, sort=210)
	public String getAirRemState(){
		return DictUtils.getDictName("refund_status", super.getAirRemState());
	}
	@ExcelField(title="退票状态(乘)", align=1, sort=215)
	public String getcRemState(){
		return DictUtils.getDictName("refund_status", super.getAirRemState());
	}
	@ExcelField(title="申请日期(供)", align=1, sort=220)
	public String getAirAppDate2(){
		if(null==super.getAirAppDate()){
			return "";
		}else{
			return DateFormatUtils.format(super.getAirAppDate(), "yyyy-MM-dd HH:mm:ss");
		}
		
	}
	@ExcelField(title="退款日期(乘)", align=1, sort=225)
	public String getcRemDate2(){
		if(null==super.getcRemDate()){
			return "";
		}else{
			return DateFormatUtils.format(super.getcRemDate(), "yyyy-MM-dd HH:mm:ss");
		}
	
	}
	@ExcelField(title="退款日期(供)", align=1, sort=230)
	public String getAirRemDate2(){	
		if(null==super.getAirRemDate()){
			return "";
		}else{
			return DateFormatUtils.format(super.getAirRemDate(), "yyyy-MM-dd HH:mm:ss");
		}
		
	}
	@ExcelField(title="供应预退", align=1, sort=240)
	public String getAirEstimatePrice2(){
		if(null==super.getAirEstimatePrice()){
			return null;
		}
		return super.getAirEstimatePrice().toString();
	}
	@ExcelField(title="供应实退", align=1, sort=250)
	public String getAirRealPrice2(){
		if(null==super.getAirRealPrice()){
			return null;
		}
		return super.getAirRealPrice().toString();
	}
	@ExcelField(title="预计利润", align=1, sort=260)
	public String getEstimateProfit(){
		if(null==super.getAirEstimatePrice() || null==super.getcRealPrice()){
			return null;
		}
		return super.getAirEstimatePrice().subtract(super.getcRealPrice()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
	}
	@ExcelField(title="实际利润", align=1, sort=270)
	public String getProfit2(){
		if(null==super.getAirRealPrice()|| null==super.getcRealPrice()){
			return null;
		}
		return super.getAirRealPrice().subtract(super.getcRealPrice()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
	}
	@ExcelField(title="出票备注          ", align=1, sort=280)
	public String getPrintRemarks() {
		if(super.getPurchase()!=null){			
			return super.getPurchase().getRemark();
		}
		return "";
	}
	@ExcelField(title="退票备注          ", align=1, sort=290)
	public String getRemarks() {
		return super.getRemark();
	}
}
