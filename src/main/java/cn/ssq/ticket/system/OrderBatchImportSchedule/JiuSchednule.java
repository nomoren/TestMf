package cn.ssq.ticket.system.OrderBatchImportSchedule;

import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.service.OrderImport.impl.JiuOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.ssq.ticket.system.util.InterfaceOrderImportConfigVO;
import cn.ssq.ticket.system.util.WebConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 就旅行订单调度
 */
@Component
public class JiuSchednule {

	private static Logger log = LoggerFactory.getLogger(JiuSchednule.class);

	@Autowired
	private OrderService orderService;


	@Autowired
	MongoTemplate mongnTemplate;
	
	@Autowired
	JiuOrderService jiuOrderService;

    @Scheduled(cron="0/30 * * * * ?")
	public void importOrder(){
		try {
			InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_JIU);
			config.setShopName("1");
			procBatchImportOrdersJIU(config);
		} catch (Exception e) {
			log.error("JIU 导入订单异常！！！ "+e.getMessage());
		}
	}
	
	/**
	 * 跟新本地订单票号,
	 */
	//@Scheduled(cron="0/31 * 0,1,2,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * ?")
	public void updateTicketNo(){
		try {
		
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.DAY_OF_MONTH, -3);
			String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			QueryWrapper<Order> query=new QueryWrapper<Order>();
			query.eq("order_source", InterfaceConstant.ORDER_SOURCE_JIU);
			query.eq("status", WebConstant.ORDER_NO_TICKET);
			query.between("c_add_date", bDate, eDate);
			List<Order> orderList = orderService.getOrderList(query);
			if(orderList.size()==0){
				return;
			}
			jiuOrderService.updateTickNo(orderList);
			//log.info(threadname+"---同步TC订单票号结束.....");
		} catch (Exception e) {
			log.error("同步JIU订单票号异常！！！ " + e.getMessage());
		}
	}
	
	public void procBatchImportOrdersJIU(InterfaceOrderImportConfigVO config){
		try {
			List<OrderVO> orderVoList = jiuOrderService.batchImportOrders(config.getSource(), config.getShopName(),null);
			List<String> errorList = new ArrayList<String>();
			if(orderVoList.size()>0){
				for(OrderVO orderVo:orderVoList){
					String orderNo=orderVo.getOrderNo();
					boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_JIU,orderNo);
					if(exist){
						errorList.add(orderNo);
					}else{		
						int count = orderService.saveOrderVO(orderVo);//保存订单
						if(count!=0){							
							JiuOrderService.status1.offer(orderNo);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append("[system]就旅行批量导入订单异常！！！ ");
			sb.append(", errorMsg : ").append(e.getMessage());
			log.error(sb.toString());
			
		}
	}
	
	
	/**
	 * 同步退票订单
	 */
	@Scheduled(cron="0 0/2 * * * ?")
	public void syncRefundOrder(){
		try {
		    Thread.sleep(2500);
			jiuOrderService.sysncRefundOrder();
		} catch (Exception e) {
			log.error("同步JIU退票单异常！！！ ",e);
		}
	}
	
	 
	/**
	 * 同步改签订单
	 */
	@Scheduled(cron="0 0/3 * * * ?")
	public void syncChangneOrder(){
		try {
			jiuOrderService.sysncChangeOrder();
		} catch (Exception e) {
			log.error("同步JIU改签异常！！！ "+e.getMessage());
		}
	}
	
	
}
