package cn.ssq.ticket.system.OrderBatchImportSchedule;


import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.service.OrderImport.impl.TbOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.ssq.ticket.system.util.InterfaceOrderImportConfigVO;
import cn.ssq.ticket.system.util.WebConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 淘宝自动调度
 */
@Component
public class TaoBaoSchednule {

	private static Logger log = LoggerFactory.getLogger(TaoBaoSchednule.class);
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private TbOrderService tbOrderService;
	

	
	/**
	 * 导入待出票单
	 */
    @Scheduled(cron="0/30 * * * * ?")
	public void importTbOrder(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始导入同程订单.....");
			String status ="2"; // 待出票  
			InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_TB);
			config.setShopName("1");
			procBatchImportOrdersTB(config,status);
			//log.info(threadname+"---导入同程订单结束.....");
		} catch (Exception e) {
			log.error("淘宝导入订单异常！！！ " +e.getMessage());
		}
	}
	
	
	/**
	 * 导入待出票单,强制成功状态
	 */ 	
	@Scheduled(cron="0 0/8 * * * ?")
	public void importTbOrder2(){
		try {
			String status ="3"; // 强制成功 
			InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_TB);
			config.setShopName("1");
			procBatchImportOrdersTB(config,status);
		} catch (Exception e) {
			
		}
	}
	
	
	/**
	 * 跟新本地订单票号,
	 */
	@Scheduled(cron="0 0/11 * * * ?")
	public void updateTicketNo(){
		try {
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.HOUR_OF_DAY, -3);
			String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			
			QueryWrapper<Order> query=new QueryWrapper<Order>();
			query.eq("order_source", InterfaceConstant.ORDER_SOURCE_TB);
			query.eq("status", WebConstant.ORDER_NO_TICKET);
			query.between("c_add_date", bDate, eDate);
			List<Order> orderList = orderService.getOrderList(query);
			if(orderList.size()==0){
				return;
			}
			tbOrderService.updateTickNo(orderList);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("同步TB订单票号异常！！！ " + e.getMessage());
		}
	}
	
	
	
	private void procBatchImportOrdersTB(InterfaceOrderImportConfigVO config,String status){
		List<String> errorList = new ArrayList<String>();
		try {
			List<OrderVO> orderVoList=tbOrderService.batchImportOrders(config.getSource(), config.getShopName(), status);
			if(orderVoList.size()>0){
				for(OrderVO orderVo:orderVoList){
					String orderNo=orderVo.getOrderNo();
					boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_TB,orderNo);
					if(exist){
						errorList.add(orderNo);
					}else{
						int count = orderService.saveOrderVO(orderVo);//保存订单
						if(count!=0){									
							TbOrderService.status1.offer(orderNo);	
						}
					}
				}
			}			
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("[system]淘宝批量导入订单异常！！！ ");
			sb.append(", status : ").append(status);
			sb.append(", errorMsg : ").append(e.getMessage());
			log.error(sb.toString(),e);
		}
	}
		
	/**
	 * 同步退票订单
	 */
	@Scheduled(cron="0 0/2 * * * ?")
	public void syncRefundOrder(){
		try {
			Thread.sleep(15000);
			tbOrderService.sysncRefundOrder();
		} catch (Exception e) {
			log.error("同步TB退票单异常！！！ ",e);
		}
	}
	
	
	/**
	 * 同步改签订单
	 */
	@Scheduled(cron="0 0/5 * * * ?")
	public void syncChangeOrder(){
		try {
			Thread.sleep(2000);
			tbOrderService.sysncChangeOrder();
		} catch (Exception e) {
			log.error("同步TB改签单异常！！！ ",e);
		}
	}
	

	//@Scheduled(cron="0 0/3 0,1,2,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * ?")
	public void syncChangeOrders(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始同步TC改签单.....");
		//	tbOrderService.syncChangeOrder();
			//log.info(threadname+"---同步TC改签单结束.....");
		} catch (Exception e) {
		
		}
	}
	

	
	
	//@Scheduled(cron="0 0/7 * * * ?")
	public void testKeepCookieLive(){
		try {
			tbOrderService.lockedOrder("926518946615", true);
			Thread.sleep(1000*60);
			tbOrderService.unlocked("926518946615", true);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
