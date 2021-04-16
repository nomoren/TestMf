package cn.ssq.ticket.system.entity.ExportBean;

import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.util.DictUtils;
import cn.stylefeng.guns.core.common.annotion.ExcelField;
import cn.stylefeng.roses.core.util.ToolUtil;

public class ShouGReport extends Purchase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	private String ticketNos;
	


	@ExcelField(title="订单号           ", align=1, sort=10)
	public String getOrderNo() {
		return super.getOrderNo();
	}



	@ExcelField(title="票号           ", align=1, sort=20)
	public String getTicketNos() {
		return ticketNos;
	}

	public void setTicketNos(String ticketNos) {
		this.ticketNos = ticketNos;
	}

	@ExcelField(title="出票地  ", align=1, sort=30)
	public String getSupplier() {
		if(ToolUtil.isNum(super.getSupplier())){
			return DictUtils.getDictName("order_purch_place", super.getSupplier());
		}
		return super.getSupplier();
	}

	
	@ExcelField(title="支付金额", align=1, sort=40)
	public String getPayAmounts() {
		return super.getPayAmount().toString();
	}

	@ExcelField(title="支付方式   ", align=1, sort=50)
	public String getPayWay() {
		if(ToolUtil.isNum(super.getPayWay())){
			return DictUtils.getDictName("order_recipt_way", super.getPayWay());
		}
		return super.getPayWay();
	}


	@ExcelField(title="备注                            ", align=1, sort=60)
	public String getRemark() {
		return super.getRemark();
	}


	@Override
	public String toString() {
		return "ShouGReport [ticketNos=" + ticketNos + "]";
	}

}
