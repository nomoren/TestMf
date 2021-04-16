package cn.ssq.ticket.system.OrderBatchImportSchedule;


import cn.ssq.ticket.system.entity.Dict;
import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.service.OrderImport.impl.TcOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.ssq.ticket.system.util.InterfaceOrderImportConfigVO;
import cn.ssq.ticket.system.util.WebConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 同程自动调度
 */
@Component
public class TongChengSchednule {

	private static Logger log = LoggerFactory.getLogger(TongChengSchednule.class);
	
	public static String token="";

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private TcOrderService tcOrderService;
	

	
    //@PostConstruct
	private void init(){
        token=tcOrderService.createToken();
        Dict param=new Dict();
		param.setDictId(1234741151693348866l);
		param.setCode(token);
		param.setUpdateTime(new Date());
		param.setUpdateUser(1l);
		DictUtils.updataDict(param);
	}
	
	@Scheduled(cron="0 0/60 * * * ?")
	public void updateToken(){
		try {
			token = tcOrderService.createToken();
			if(StringUtils.isEmpty(token)){
			    Thread.sleep(10000);
                token = tcOrderService.createToken();
            }
			Dict param=new Dict();
			param.setDictId(1234741151693348866l);
			param.setCode(token);
			param.setUpdateTime(new Date());
			param.setUpdateUser(1l);
			DictUtils.updataDict(param);
			
		} catch (Exception e) {
			log.error("更新token异常"+e.getMessage());
		}
	}
	
	/**
	 * 导入待出票单
	 */
	@Scheduled(cron="0/37 * * * * ?")
	public void importTcOrder(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info("---开始导入同程订单.....");
			String status ="0"; // 待出票  
			InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_TC);
			config.setShopName("1");
			procBatchImportOrdersTC(config,status);
			//log.info("---导入同程订单结束.....");
		} catch (Exception e) {
			log.error("同程导入订单异常！！！ ",e);
		}
	}
		
	/**
	 * 同步退票订单
	 */
	@Scheduled(cron="0 0/1 * * * ?")
	public void syncRefundOrder(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始同步TC退票单.....");
			Thread.sleep(1700);
			tcOrderService.sysncRefundOrder(1);//自愿退票
			Thread.sleep(2000);
			tcOrderService.sysncRefundOrder(2);//非自愿
			Thread.sleep(2000);
			tcOrderService.sysncRefundOrder(0);//特殊退票
			//log.info(threadname+"---同步TC退票单结束.....");
		} catch (Exception e) {
			log.error("同步TC退票单异常！！！ ",e);
		}
	}
	
	/**
	 * 同步改签订单
	 */
	@Scheduled(cron="0 0/3 * * * ?")
	public void syncChangeOrder(){
		try {
			Thread.sleep(1700);
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始同步TC改签单.....");
			tcOrderService.sysncChangeOrder(0);
			//log.info(threadname+"---同步TC改签单结束.....");
		} catch (Exception e) {
			log.error("同步TC改期异常！！！ ",e);
		}
	}
	
	/**
	 * 跟新本地订单票号,
	 */
	@Scheduled(cron="0 0/1 * * * ?")
	public void updateTicketNo(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---同步TC订单票号.....");
			Thread.sleep(9300);
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.DAY_OF_MONTH, -3);
			String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			QueryWrapper<Order> query=new QueryWrapper<Order>();
			query.eq("order_source", InterfaceConstant.ORDER_SOURCE_TC);
			query.eq("status", WebConstant.ORDER_NO_TICKET);
			query.between("c_add_date", bDate, eDate);
			List<Order> orderList = orderService.getOrderList(query);
			if(orderList.size()==0){
				return;
			}
			tcOrderService.updateTickNo(orderList);
			//log.info(threadname+"---同步TC订单票号结束.....");
		} catch (Exception e) {
			log.error("同步TC订单票号异常！！！ " + e.getMessage());
		}
	}
	
	private void procBatchImportOrdersTC(InterfaceOrderImportConfigVO config,String status){
		List<String> errorList = new ArrayList<String>();
		try {
			List<OrderVO> orderVoList=tcOrderService.batchImportOrders(config.getSource(), config.getShopName(), status);
			if(orderVoList.size()>0){
				for(OrderVO orderVo:orderVoList){
					String orderNo=orderVo.getOrderNo();
					boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_TC,orderNo);
					if(exist){
						errorList.add(orderNo);
					}else{
						int count = orderService.saveOrderVO(orderVo);//保存订单
						if(count!=0){									
							TcOrderService.status1.offer(orderNo);
						}
					}
				}
			}
			/*int totalCount=orderVoList==null?0:orderVoList.size();
			int successCount=orderVoList==null?0:orderVoList.size()-errorList.size();
			String errorIds=Arrays.toString(errorList.toArray(new String[errorList.size()]));*/
			
			/*StringBuilder sb=new StringBuilder();
			sb.append("[system]同程批量导入订单成功。");
			sb.append(", status : ").append(status);
			sb.append(", totalCount : ").append(totalCount);
			sb.append(", successCount : ").append(successCount);
			sb.append(", errorIds(db exists) : ").append(errorIds);
			log.info(sb.toString());*/
			
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("[system]同程批量导入订单异常！！！ ");
			sb.append(", status : ").append(status);
			sb.append(", errorMsg : ").append(e.getMessage());
			log.error(sb.toString(),e);
		}
	}
}
