package cn.ssq.ticket.system.entity.ExportBean;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.util.DictUtils;
import cn.stylefeng.guns.core.common.annotion.ExcelField;
/** 
 * 其他平台财务报表
 */

public class OtherReport extends Purchase{
	private static final long serialVersionUID = 1L;
	
	private boolean isOne = false;
	
	public boolean isOne() {
		return isOne;
	}
	public void setOne(boolean isOne) {
		this.isOne = isOne;
	}


	@ExcelField(title="出票日期", align=1, sort=10)
	public String getPrinticketDate(){
		return DateFormatUtils.format(super.getPrintTicketDate(), "yyyy-MM-dd");
	}


	@ExcelField(title="订单号", align=1, sort=20)
	public String getOrderNo(){
		return super.getOrderNo();
	}

	@ExcelField(title="票号     ", align=1, sort=25)
	public String getTicketNo(){
		String ticketNO="";
		for(Passenger p:super.getPassengerList()){
			if(this.getPassengerNames().trim().equals(p.getName())){
				ticketNO=p.getTicketNo();
				break;
			}else{
				continue;
			}
		}
		return ticketNO;
	}

	@ExcelField(title="采购地", align=1, sort=30)
	public String getPurchPalse(){
		return super.getSupplier();
	}

	@ExcelField(title="支付价格", align=3, sort=40)
	public String getPayPurchPriceR(){
		if(super.getPassengerNum()>1){
			if(isOne){
				return super.getPayAmount().toString();
			}else{
				return "0";
			}
		}else{				
			return super.getPayAmount().toString();
		}
	}

	@ExcelField(title="收款总价", align=3, sort=50)
	public String getActualPriceR(){
		if(super.getPassengerNum()>1){
			if(isOne){
				return super.getCustomerAmount().toString();
			}else{
				return "0";
			}
		}else{			
			return super.getCustomerAmount().toString();
		}
	}

	@ExcelField(title="利润", align=3, sort=60)
	public Double getProfit(){
		if(StringUtils.isEmpty(super.getProfit().toString())){
			return 0.0;
		}
		if(super.getPassengerNum()>1){
			return new BigDecimal(getActualPriceR()).subtract(new BigDecimal(getPayPurchPriceR())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{			
			return super.getProfit();
		}
	}

	@ExcelField(title="支付方式", align=1, sort=70)
	public String getPayWay(){
		return DictUtils.getDictName("order_recipt_way", super.getPayWay());
	}

	@ExcelField(title="备注", align=1, sort=80)
	public String getRemarks() {
		return super.getRemark();
	}

	@ExcelField(title="政策代码", align=1, sort=85)
	public String getPolicyType(){
		return super.getPassengerList().get(0).getPolicyType();
	}
	
	@ExcelField(title="起飞日期", align=1, sort=85)
	public String getFlightDate(){
		return super.getPassengerList().get(0).getFlightDepDate()+" "+super.getPassengerList().get(0).getDepTime();
	}

	@ExcelField(title="出票人员", align=1, sort=90)
	public String getPrintTicketBy(){
		String name=super.getEmployeeName();
		if(StringUtils.isEmpty(name)){
			return "";
		}else{
			return DictUtils.getDictName("ssq_user", name);
		}
	}
	@ExcelField(title="名字", align=1, sort=91)
	public String getPName(){
		return this.getPassengerNames();
	}
	
	@ExcelField(title="航班号", align=1, sort=93)
	public String getFlightNo(){
		return super.getPassengerList().get(0).getFlightNo();
	}
	@ExcelField(title="航线", align=1, sort=94)
	public String getLine(){
		Passenger passenger = super.getPassengerList().get(0);
		return passenger.getDepCityCode()+"-"+passenger.getArrCityCode();
	}
	@ExcelField(title="舱位", align=1, sort=95)
	public String getCaben(){
		String cabin="";
		for(Passenger p:super.getPassengerList()){
			if(this.getPassengerNames().trim().equals(p.getName())){
				cabin=p.getCabin();
				break;
			}else{
				continue;
			}
		}
		return cabin;
	}
	
	

	@ExcelField(title="支付流水号          ", align=1, sort=96)
	public String getTradeNo() {
		return super.getTradeNo();
	}
	
	@ExcelField(title="实际支付价格", align=1, sort=100)
	public Double getRealPay() {
		if(super.getPassengerNum()>1){
			if(isOne){
				return super.getRealPay();
			}else{
				return null;
			}
		}else{				
			return super.getRealPay();
		}
	}
	
	@ExcelField(title="报表类型", align=1, sort=105)
	public String isError() {
		return DictUtils.getDictName("purchType", super.getType());
	}
	
	@ExcelField(title="差错退款金额", align=1, sort=110)
	public Double getRetrunMoney() {
		if(super.getType().equals("1")){	
			if(super.getPassengerNum()>1){
				if(isOne){
					return super.getReturnMoney();
				}else{
					return 0.0;
				}
			}else{				
				return super.getReturnMoney();
			}
		}else{
			return null;
		}
	}
	
	@ExcelField(title="差错退款日期", align=1, sort=120)
	public String getRetrunDate() {
		if(super.getType().equals("1")){	
			if(super.getPassengerNum()>1){
				if(isOne){
					return super.getReturnDate();
				}else{
					return "";
				}
			}else{				
				return super.getReturnDate();
			}
		}else{
			return "";
		}
	}
	
	
	public String getAirlineCode(){
		return super.getPassengerList().get(0).getAirlineCode();
	}
}
