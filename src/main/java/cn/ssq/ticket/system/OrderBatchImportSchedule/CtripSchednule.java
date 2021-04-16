package cn.ssq.ticket.system.OrderBatchImportSchedule;

import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.entity.importBean.CtripBean.CtripOrderVO;
import cn.ssq.ticket.system.service.OrderImport.impl.CtripOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.PurchaseService;
import cn.ssq.ticket.system.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 携程订单自动调度
 */
@Component
public class CtripSchednule {

	private static Logger log = LoggerFactory.getLogger(CtripSchednule.class);

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CtripOrderService ctripOrderService;
	
	@Autowired
	private PurchaseService purchaseService;


	
	/**
	 * 导单,1店
	 */
	@Scheduled(cron="0/33 * * * * ?")
	public void importCtripOrder1(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始导入Ctrip 1店订单.....");
			Integer status =1; //  1、待出票  
			InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_CTRIP);
			config.setShopName("1");
			procBatchImportOrdersCtrip(config,status);
			//log.info(threadname+"---导入Ctrip 1店订单结束.....");
		} catch (Exception e) {
			log.error("Ctrip 1店导入订单异常！！！ " + e.getMessage());
		}
	}
	
	/**
	 * 2店
	 */
	@Scheduled(cron="0/31 * * * * ?")
	public void importCtripOrder2(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始导入Ctrip 2店订单.....");
			Integer status =1; //  1、待出票  
			InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_CTRIP);
			config.setShopName("2");
			procBatchImportOrdersCtrip(config,status);
			//log.info(threadname+"---导入Ctrip 2店订单结束.....");
		} catch (Exception e) {
			log.error("Ctrip 2店导入订单异常！！！ " + e.getMessage());
		}
	}
	
	/**
	 * 3店
	 */
	@Scheduled(cron="0/35 * * * * ?")
	public void importCtripOrder3(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始导入Ctrip 3店订单.....");
			Integer status =1; //  1、待出票  
			InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_CTRIP);
			config.setShopName("3");
			procBatchImportOrdersCtrip(config,status);
			//log.info(threadname+"---导入Ctrip 3店订单结束.....");
		} catch (Exception e) {
			log.error("Ctrip 3店导入订单异常！！！ " + e.getMessage());
		}
	}
	
	/**
	 * 同步已取消订单
	 */
	/*@Scheduled(cron="0 0/41 * * * ?")
	public void updateFailOrder(){
		try {
			String threadname=Thread.currentThread().getName();
			log.info(threadname+"---开始同步Ctrip已取消的订单.....");
			ctripOrderService.syncFailOrder();
			log.info(threadname+"---同步Ctrip已取消订单结束.....");
		} catch (Exception e) {
			log.error("同步Ctrip已取消订单异常！！！ " + e.getMessage(), e);
		}
	}*/
	
	/**
	 * 同步退票单
	 */
    @Scheduled(cron="0/40 * * * * ?")
	public void syncRefundOrder(){
		try {
			Thread.sleep(3000);
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始同步Ctrip退票单.....");
			ctripOrderService.syncRefundOrder(InterfaceConstant.ORDER_SOURCE_TC, "1");
			Thread.sleep(5000);
			ctripOrderService.syncRefundOrder(InterfaceConstant.ORDER_SOURCE_TC, "2");
			Thread.sleep(5000);
			ctripOrderService.syncRefundOrder(InterfaceConstant.ORDER_SOURCE_TC, "3");
			//log.info(threadname+"---同步Ctrip已退票单结束.....");
		} catch (Exception e) {
			log.error("同步Ctrip退票单异常！！！ ",e);
		}
	}
	
	/**
	 * 同步改签单
	 */
	@Scheduled(cron="0 0/3 * * * ?")
	public void syncChangeOrder(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始同步Ctrip改签单.....");
            ctripOrderService.syncChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "1","Unhandle");
            Thread.sleep(10000);
            ctripOrderService.syncChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "2","Unhandle");
			Thread.sleep(10000);
			ctripOrderService.syncChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "3","Unhandle");
			//log.info(threadname+"---同步Ctrip改签单结束.....");
		} catch (Exception e) {
			log.error("同步Ctrip改签单异常！！！ ",e);
		}
	}
	
	
	
	
	/**
	 * 同步订单金额
	 */
	@Scheduled(cron="1 1 1 * * ?")
	public void updatePrice() {
		try {
			log.info("开始同步携程订单价格");
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String date=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
            String start=date+" 00:00:00";
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
            String end=date+" 23:59:59";
			QueryWrapper<Order> query=new QueryWrapper<Order>();
			query.eq("order_source", InterfaceConstant.ORDER_SOURCE_CTRIP);
			query.between("c_add_date", start, end);
			List<Order> orderList = orderService.getOrderList(query);
			int i=0;
			for(Order order:orderList) {
				try {
					String orderNo = order.getOrderNo();
					String user = DictUtils.getDictCode("order_import_cfgxc" + order.getOrderShop(), "user");
					String pass = DictUtils.getDictCode("order_import_cfgxc" + order.getOrderShop(), "pass");
					String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
					CtripOrderVO cOrderVO = ctripOrderService.getOrderDetailBycOrderNo(order.getOrderNo(), user, passWord);
					if (cOrderVO == null) {
						continue;
					}
					OrderVO orderVo = ctripOrderService.procCtripOrderToOrderVO(cOrderVO, order.getOrderSource(), order.getOrderShop());
					double totalPirce = Double.valueOf(order.getTotalPrice()).doubleValue();
					double newPirce = Double.valueOf(orderVo.getOrder().getTotalPrice()).doubleValue();
					System.out.println(orderNo+"."+totalPirce+","+newPirce);
					if (totalPirce != newPirce) {
                        i++;
                        Order o = new Order();
                        o.setOrderId(order.getOrderId());
						log.info(orderNo);
                        System.out.println(newPirce);
                        o.setTotalPrice(newPirce + "");
                        orderService.updateOrderByID(o);

                        UpdateWrapper<Purchase> uw=new UpdateWrapper<>();
                        uw.eq("order_id",order.getOrderId());

                        QueryWrapper<Purchase> queryWrapper =new QueryWrapper<>();
                        queryWrapper.eq("order_no",orderNo);
                        queryWrapper.eq("flag","0");
                        queryWrapper.select("order_no");
                        List<Purchase> byQueryWrapper = purchaseService.getByQueryWapper(queryWrapper);
                        int count=byQueryWrapper.size();
                        Purchase p=new Purchase();
                       // int count = Integer.valueOf(orderVo.getOrder().getPassengerCount()).intValue();
                        p.setCustomerAmount(newPirce/count);
                        purchaseService.updatePurchase(p,uw);
                    }
					Thread.sleep(3000);
				} catch (Exception e) {
					log.info("同步金额,取"+ order.getOrderNo()+"xml异常",e.getMessage());
					continue;
				}
			}
			
			log.info("同步携程订单价格结束，共"+orderList.size()+"条，更新"+i+"条");
			
		} catch (Exception e) {
			log.error("同步Ctrip金额异常！！！ " + e.getMessage());
		}
		
		
	}
	
	
	/**
	 * 跟新本地订单票号
	 */
	@Scheduled(cron="0 0/2 * * * ?")
	public void updateTicketNo(){
		try {
			/*String threadname=Thread.currentThread().getName();
			log.info(threadname+"---同步Ctrip订单票号.....");*/
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.DAY_OF_MONTH, -2);
			String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			List<String> status=new ArrayList<String>();
			status.add(WebConstant.ORDER_NO_TICKET);
			status.add(WebConstant.APPLY_ORDER_CANCEL);
			
			QueryWrapper<Order> query2=new QueryWrapper<>();
			query2.eq("order_source", InterfaceConstant.ORDER_SOURCE_CTRIP);
			query2.eq("Order_shop", "1");
			query2.in("status", status);
			query2.between("c_add_date", bDate, eDate);
			List<Order> ctOrder = orderService.getOrderList(query2);
			ctripOrderService.updateTickNo(ctOrder);
			Thread.sleep(2000);
			
			QueryWrapper<Order> query3=new QueryWrapper<>();
			query3.eq("order_source", InterfaceConstant.ORDER_SOURCE_CTRIP);
			query3.eq("Order_shop", "2");
			query3.in("status", status);
			query3.between("c_add_date", bDate, eDate);
			List<Order> ctOrder2 = orderService.getOrderList(query3);
			ctripOrderService.updateTickNo(ctOrder2);
			Thread.sleep(2000);
			
			QueryWrapper<Order> query4=new QueryWrapper<>();
			query4.eq("order_source", InterfaceConstant.ORDER_SOURCE_CTRIP);
			query4.eq("Order_shop", "3");                                                                      
			query4.in("status", status);
			query4.between("c_add_date", bDate, eDate);
			List<Order> orderList3 = orderService.getOrderList(query4);
			ctripOrderService.updateTickNo(orderList3);
		
			/*log.info(threadname+"---同步Ctrip订单票号结束.....");*/
		} catch (Exception e) {
			log.error("同步Ctrip订单票号异常！！！ " + e.getMessage());
		}
	}
	
	private void procBatchImportOrdersCtrip(InterfaceOrderImportConfigVO config,Integer status){
		List<String> errorList = new ArrayList<String>();
		try {
			List<OrderVO> orderVoList=ctripOrderService.batchImportOrders(config.getSource(),config.getShopName(),"1");
			if(orderVoList!=null && orderVoList.size()>0){
				for(OrderVO orderVo:orderVoList){
					String orderNo=orderVo.getOrderNo();
					boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_CTRIP,orderNo);
					if(exist){
						errorList.add(orderNo);
						CtripOrderService.statusHas.offer(orderNo);
					}else{
						int count = orderService.saveOrderVO(orderVo);//保存订单
						if(count!=0){
							CtripOrderService.statusHas.offer(orderNo);
						}
					}
				}
			}
			int totalCount=orderVoList==null?0:orderVoList.size();
			int successCount=orderVoList==null?0:orderVoList.size()-errorList.size();
			String errorIds=Arrays.toString(errorList.toArray(new String[errorList.size()]));
			
			StringBuilder sb=new StringBuilder();
			sb.append("[system]Ctrip"+config.getShopName()+"店批量导入订单成功。");
			sb.append(", status : ").append(status);
			sb.append(", totalCount : ").append(totalCount);
			sb.append(", successCount : ").append(successCount);
			sb.append(", errorIds(db exists) : ").append(errorIds);
			//log.info(sb.toString());
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("[system]Ctrip批量导入订单异常！！！ ");
			sb.append(", status : ").append(status);
			sb.append(", errorMsg : ").append(e.getMessage());
			log.error(sb.toString(),e);
		}
	}


    public static void main(String[] args) {
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        String start=date+" 00:00:00";
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        String end=date+" 23:59:59";
        System.out.println(start);
        System.out.println(end);

    }
}
