package cn.ssq.ticket.system.entity.ExportBean;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.util.DictUtils;
import cn.stylefeng.guns.core.common.annotion.ExcelField;

/**
 * 改签报表
 * @author Administrator
 *
 */
public class ChangeReport extends Purchase{

	private static final long serialVersionUID = 1L;


	private boolean isOne = false;

	public boolean isOne() {
		return isOne;
	}
	public void setOne(boolean isOne) {
		this.isOne = isOne;
	}
	
	@ExcelField(title="日期", align=1, sort=1)
	public String getPrinticketDate(){
		return DateFormatUtils.format(super.getPrintTicketDate(), "yyyy-MM-dd HH:mm:ss").split(" ")[0];
	}

	@ExcelField(title="平台", align=1, sort=2)
	public String getOrderSource() {
		return DictUtils.getDictName("order_source", super.getOrderSource());
	}
		
	@ExcelField(title="订单号       ", align=1, sort=10)
	public String getOrderNo(){
		return super.getOrderNo();
	}
	
	@ExcelField(title="改期后票号 ", align=1, sort=11)
	public String getNewTicketNo(){
		return super.getNewTicketNo();
	}

	@ExcelField(title="支付平台", align=1, sort=12)
	public String getPurchPalse(){
		return super.getSupplier();
	}
	
	@ExcelField(title="支付价格", align=1, sort=13)
	public String getPayPurchPrice(){
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

	@ExcelField(title="收款价格", align=1, sort=14)
	public String getConsumerPayPurchPrice(){
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

	@ExcelField(title="利润", align=3, sort=15)
	public Double getProfit(){
		return new BigDecimal(super.getCustomerAmount()).subtract(new BigDecimal(super.getPayAmount())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	

	@ExcelField(title="支付方式", align=1, sort=21)
	public String getPayWay(){
		return DictUtils.getDictName("order_recipt_way", super.getPayWay());
	}

	@ExcelField(title="政策代码  ", align=1, sort=22)
	public String getpolicyCodes(){
		return getPolicyCode();
	}
	
	@ExcelField(title="出票员", align=1, sort=23)
	public String getPrintTicketBy(){
		String name=super.getEmployeeName();
		if(StringUtils.isEmpty(name)){
			return "";
		}else{
			return DictUtils.getDictName("ssq_user", name);
		}
	}
	
	@ExcelField(title="乘机人", align=1, sort=24)
	public String getPanssengerName(){
		return super.getPassengerNames();
	}
	
	
	@ExcelField(title="原票号       ", align=1, sort=25)
	public String getssTicketNo(){
		return getsTicketNo();
	}
	
	@ExcelField(title="原航班号", align=1, sort=26)
	public String getsFlightNos(){
		return getsFlightNo();
	}
	
	@ExcelField(title="原仓位", align=1, sort=27)
	public String getsCabins(){
		return getsCabin();
	}

	@ExcelField(title="改签起飞日期", align=1, sort=28)
	public String getNewFlightDates(){
		return getNewFlightDate();
	}

	@ExcelField(title="改签航班号", align=1, sort=29)
	public String getNewFlightNos(){
		return getNewFlightNo();
	}
	
	@ExcelField(title="改签仓位", align=1, sort=30)
	public String getNewCabins(){
		return getNewCabin();
	}
	
	@ExcelField(title="改签申请时间   ", align=1, sort=31)
	public String getApplyDates(){
		return getApplyDate();
	}
	
	
	@ExcelField(title="改签最后修改时间   ", align=1, sort=31)
	public String getLastModDates(){
		return DateFormatUtils.format(super.getPrintTicketDate(), "yyyy-MM-dd HH:mm:ss");
	}

	

	@ExcelField(title="备注                                          ", align=1, sort=100)
	public String getRemarks() {
		return super.getRemark();
	}
	
	private String airlineCode;

	private String sTicketNo;

	private String sFlightNo;
	
	private String sCabin;
	
	private String newFlightNo;
	
	private String newFlightDate;
	
	private String newCabin;
	
	private String applyDate;
	
	private String lastModDate;
	
	private String policyCode;
	
	
	
	
	public String getPolicyCode() {
		return policyCode;
	}
	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}
	public String getLastModDate() {
		return lastModDate;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public void setLastModDate(String lastModDate) {
		this.lastModDate = lastModDate;
	}
	public String getNewFlightNo() {
		return newFlightNo;
	}
	public void setNewFlightNo(String newFlightNo) {
		this.newFlightNo = newFlightNo;
	}
	public String getNewFlightDate() {
		return newFlightDate;
	}
	public void setNewFlightDate(String newFlightDate) {
		this.newFlightDate = newFlightDate;
	}
	public String getNewCabin() {
		return newCabin;
	}
	public void setNewCabin(String newCabin) {
		this.newCabin = newCabin;
	}
	public String getsFlightNo() {
		return sFlightNo;
	}
	public void setsFlightNo(String sFlightNo) {
		this.sFlightNo = sFlightNo;
	}
	public String getsCabin() {
		return sCabin;
	}
	public void setsCabin(String sCabin) {
		this.sCabin = sCabin;
	}
	public String getsTicketNo() {
		return sTicketNo;
	}

	public void setsTicketNo(String sTicketNo) {
		this.sTicketNo = sTicketNo;
	}

	public String getAirlineCode() {
		return airlineCode;
	}

	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}







}
