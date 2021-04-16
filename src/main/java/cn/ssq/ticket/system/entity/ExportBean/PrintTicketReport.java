package cn.ssq.ticket.system.entity.ExportBean;

import java.util.List;
import org.apache.commons.lang.time.DateFormatUtils;
import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.util.DictUtils;
import cn.stylefeng.guns.core.common.annotion.ExcelField;
import cn.stylefeng.roses.core.util.ToolUtil;

/** 
 * 采购(出票)报表
 */
public class PrintTicketReport extends Purchase{
	private static final long serialVersionUID = 1L;

	@ExcelField(title="出票日期", align=1, sort=10)
	public String getPrinticketDate(){
		return DateFormatUtils.format(super.getPrintTicketDate(), "yyyy-MM-dd HH:mm:ss");
	}
	
/*	@ExcelField(title="订单创建日期", align=1, sort=11)
	public String getCreateDate(){
		return super.getcAddDate();
	}*/

	@ExcelField(title="订单号", align=1, sort=20)
	public String getOrderNo(){
		return super.getOrderNo();
	}
	@ExcelField(title="乘机人", align=1, sort=30)
	public String getName(){
		return super.getPassengerNames();
	}
	@ExcelField(title="航班号", align=1, sort=40)
	public String getFlightNo(){
		return super.getPassengerList().get(0).getFlightNo();
	}

	@ExcelField(title="航段", align=1, sort=50)
	public String getFlightCourse(){
		return (super.getPassengerList().get(0).getDepCityCode()+"-"+super.getPassengerList().get(0).getArrCityCode());
	}
	
	/*@ExcelField(title="舱位", align=1, sort=60)
	public String getCabin(){
		return super.getPassengerList().get(0).getCabin();
	}*/

	@ExcelField(title="出票舱位", align=1, sort=70)
	public String getPrintTicketCabin(){
		return super.getPassengerList().get(0).getPrintTicketCabin();
	}

	@ExcelField(title="航班日期", align=1, sort=110)
	public String getFlightDepDate(){
		return super.getPassengerList().get(0).getFlightDepDate();
	}

	@ExcelField(title="出票地", align=1, sort=130)
	public String getPurchPalse(){
		if(ToolUtil.isNum(super.getSupplier())){
			return DictUtils.getDictName("order_purch_place", super.getSupplier());
		}
		return super.getSupplier();
	}
	
	@ExcelField(title="采购总价", align=1, sort=135)
	public String getPayPurchPrice(){
		return super.getPayAmount().toString();
	}

	
	@ExcelField(title="实际采购价", align=1, sort=136)
	public String getRealPrice(){
		return super.getRealPay().toString();
	}
	
	@ExcelField(title="交易流水号        ", align=1, sort=140)
	public String getTradeNo() {
		return super.getTradeNo();
	}
	@ExcelField(title="票号            ", align=1, sort=150)
	public String getTicketNo(){
		String[] names = super.getPassengerNames().split(",");
		List<Passenger> list=super.getPassengerList();
		StringBuilder sb=new StringBuilder();
		for(String name:names){
			for(Passenger p:list){
				if(name.trim().equals(p.getName())){
					sb.append(p.getTicketNo()).append(",");
				}else{
					continue;
				}
			}
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	@ExcelField(title="支付方式", align=1, sort=160)
	public String getPayWay(){
		if(ToolUtil.isNum(super.getPayWay())){
			return DictUtils.getDictName("order_recipt_way", super.getPayWay());
		}
		return super.getPayWay();
	}
	@ExcelField(title="政策渠道", align=1, sort=170)
	public String getPolicyType(){
		return super.getPassengerList().get(0).getPolicyType();
	}
	@ExcelField(title="出票员", align=1, sort=180)
	public String getPrintTicketBy(){
		return super.getEmployeeName();
	}
	@ExcelField(title="备注", align=1, sort=190)
	public String getRemarks() {
		return super.getRemark();
	}
}
