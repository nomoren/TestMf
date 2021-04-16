package cn.ssq.ticket.system.entity.ExportBean;

import java.util.List;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.entity.Passenger;
import cn.stylefeng.guns.core.common.annotion.ExcelField;


/**
 * 收款报表
 * @author Administrator
 *
 */
public class ReceiptReport extends OrderVO{

	private static final long serialVersionUID = 1L;

	@ExcelField(title="订单创建日期", align=1, sort=10)
	public String getPrinticketDate(){
		//return DateFormatUtils.format(super.getCreateDate(), "yyyy-MM-dd");
		return super.getcAddDate();
	}
	
	@ExcelField(title="销售订单号   ", align=1, sort=20)
	public String getOrderNo(){
		return super.getOrderNo();
	}
	
	@ExcelField(title="乘机人      ", align=1, sort=30)
	public String getName(){
		List<Passenger> passengetList = super.getPassengetList();
		StringBuilder sb=new StringBuilder();
		for(Passenger p:passengetList){
			sb.append(p.getName()).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	@ExcelField(title="航班号", align=1, sort=40)
	public String getFlightNo(){
		return super.getFlightNo();
	}
	
	@ExcelField(title="航段", align=1, sort=50)
	public String getFlightCourse(){
		return (super.getPassengetList().get(0).getDepCityCode()+"-"+super.getPassengetList().get(0).getArrCityCode());
	}
	@ExcelField(title="舱位", align=1, sort=60)
	public String getCabin(){
		return super.getPassengetList().get(0).getCabin();
	}

	@ExcelField(title="出票舱位", align=1, sort=70)
	public String getPrintTicketCabin(){
		return super.getPassengetList().get(0).getPrintTicketCabin();
	}
	
	@ExcelField(title="销售总价", align=1, sort=80)
	public String getActualPrice(){
		return super.getTotalPrice();
	}
	
	@ExcelField(title="航班日期", align=1, sort=110)
	public String getFlightDepDate(){
		return super.getFlightDate();
	}
	@ExcelField(title="票号     ", align=1, sort=150)
	public String getTicketNo(){
		List<Passenger> passengetList = super.getPassengetList();
		StringBuilder sb=new StringBuilder();
		for(Passenger p:passengetList){
			sb.append(p.getTicketNo()==null?"":p.getTicketNo()).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	@ExcelField(title="政策渠道", align=1, sort=170)
	public String getPolicyType(){
		return super.getPolicyType();
	}
	
	@ExcelField(title="备注", align=1, sort=190)
	public String getRemarks() {
		return super.getTicketRemark();
	}
	
	
}
