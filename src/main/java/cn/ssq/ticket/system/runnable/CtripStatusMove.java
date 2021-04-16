package cn.ssq.ticket.system.runnable;

import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.service.OrderImport.impl.CtripOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.PassengerService;
import cn.ssq.ticket.system.util.WebConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class CtripStatusMove extends Thread{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private Order order;
	private List<Passenger> list;
	private String userName;
	private OrderService orderSerice;
	private CtripOrderService ctripOrderService;
	private PassengerService passengerService;
	
	public CtripStatusMove(Order order,List<Passenger> list,String userName,OrderService orderService,
			CtripOrderService ctrip,PassengerService passengerService) {
		this.order=order;
		this.list=list;
		this.userName=userName;
		this.orderSerice=orderService;
		this.ctripOrderService=ctrip;
		this.passengerService=passengerService;
	}
	public void run() {
		try {
			if(order==null){
				return;
			}
			String orderStatus="";
			for(int i=0;i<=40;i++){
				Thread.sleep(5000);
				orderStatus = ctripOrderService.getOrderStatus(order.getOrderNo(), order.getOrderShop()).split(":")[0];
				log.info("跟进携程订单状态:"+order.getOrderNo()+":"+orderStatus);
				if(StringUtils.isNotEmpty(orderStatus)){
					if(!"8".equals(orderStatus)){//非验证中
						break;
					}
				}
			}
			if("1".equals(orderStatus) || "2".equals(orderStatus)){//验证失败
				orderSerice.updateStatus(WebConstant.ORDER_NO_TICKET, order.getOrderNo());
				for(Passenger passenger:list){
					passenger.setTicketStatus(WebConstant.ERROR_TICKET);
					passenger.setStatus(WebConstant.ERROR_TICKET);
					passenger.setPrintTicketBy(userName);
					passenger.setPrintTicketCabin(passenger.getCabin());
					passengerService.updateTicketStatus(passenger);
				}
			}else{
				orderSerice.updateStatus("3", order.getOrderNo());
				for(Passenger passenger:list){
					passenger.setTicketStatus(WebConstant.OK_TICKET);
					passenger.setStatus(WebConstant.OK_TICKET);
					passenger.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					passenger.setPrintTicketCabin(passenger.getCabin());
					passenger.setPrintTicketBy(userName);
					passengerService.updateTicketStatus(passenger);
				}
			}
		} catch (Exception e) {
			log.info("跟进携程订单状态失败"+order.getOrderNo());
		}
	}
}
