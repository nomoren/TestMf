package cn.ssq.ticket.system.OrderBatchImportSchedule;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.importBean.Datai;
import cn.ssq.ticket.system.entity.importBean.ListRespon;
import cn.ssq.ticket.system.entity.importBean.Row;
import cn.ssq.ticket.system.entity.importBean.TicketLockItem;
import cn.ssq.ticket.system.service.OrderImport.OrderImportService;
import cn.ssq.ticket.system.service.OrderImport.impl.TuniuOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.*;



/**
 * 途牛订单调度任务
 * @author Administrator
 */
@Component
public class TuNiuSchedule {

	private static Logger log = LoggerFactory.getLogger(TuNiuSchedule.class);
		
	@Autowired
	private OrderService orderService;
	
	@Autowired
	@Qualifier("tuniuOrderService")
	private OrderImportService tuniuOrderService;
	
	
	/**
	 * 跟新本地订单票号
	 */
	@Scheduled(cron="0 0/11 * * * ?")
	public void updateTicketNo(){
		try {
			String secretKey=DictUtils.getDictCode("openTN", "isOpen");
			if("1".equals(secretKey)){
				Calendar calendar=Calendar.getInstance();
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
				calendar.add(Calendar.DAY_OF_MONTH, -3);
				String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
				QueryWrapper<Order> query=new QueryWrapper<Order>();
				query.eq("order_source", InterfaceConstant.ORDER_SOURCE_TN);
				query.eq("status", WebConstant.ORDER_NO_TICKET);
				query.eq("Order_shop", "2");
				query.between("c_add_date", bDate, eDate);
				List<Order> orderList = orderService.getOrderList(query);
				TuniuOrderService.updateTickNo(orderList);
			}
			//log.info("---获取途牛订单票号单结束.....");
		} catch (Exception e) {
			log.error("获取途牛订单票号异常！！！ " + e.getMessage());
		}
	}
	

	/**
	 * 批量导入途牛订单1店
	 */
	//@Scheduled(cron="0/16 * 0,1,2,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * ?")
	public void importTuniuOrder(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始导入途牛订单.....");
			String secretKey=DictUtils.getDictCode("openTN", "isOpen");
			if("1".equals(secretKey)){
				String status ="3"; //  0、全部  3、待出票 4、出票成功 5、出票失败 6、处理中
				InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
				config.setSource(InterfaceConstant.ORDER_SOURCE_TN);
				config.setShopName("1");
				procBatchImportOrdersTn(config,status);
			}
			//log.info(threadname+"---导入途牛订单结束.....");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("途牛1店批量导入订单异常！！！ ",e);
		}
	}
	
	

	/**
	 * 批量导入途牛订单2店
	 */
	@Scheduled(cron="0 0/1 * * * ?")
	public void importTuniuOrder2(){
		try {
		    Thread.sleep(2000);
			String secretKey=DictUtils.getDictCode("openTN", "isOpen");
			if("1".equals(secretKey)){	
				//log.info("---开始导入途牛订单.....");
				String status ="3"; //  0、全部  3、待出票 4、出票成功 5、出票失败 6、处理中
				InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
				config.setSource(InterfaceConstant.ORDER_SOURCE_TN);
				config.setShopName("2");
				procBatchImportOrdersTn(config,status);
			}
			//log.info(threadname+"---导入途牛订单结束.....");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("途牛2点批量导入订单异常！！！ " + e.getMessage());
		}
	}
	
	
	/**
	 * 批量导入途牛订单3店
	 */
	//@Scheduled(cron="0/15 * 0,1,2,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * ?")
	public void importTuniuOrder3(){
		try {
			String secretKey=DictUtils.getDictCode("openTN", "isOpen");
			if("1".equals(secretKey)){	
				String status ="3"; //  0、全部  3、待出票 4、出票成功 5、出票失败 6、处理中
				InterfaceOrderImportConfigVO config=new InterfaceOrderImportConfigVO();
				config.setSource(InterfaceConstant.ORDER_SOURCE_TN);
				config.setShopName("3");
				procBatchImportOrdersTn(config,status);
			}
			//log.info(threadname+"---导入途牛订单结束.....");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("途牛3点批量导入订单异常！！！ " + e.getMessage());
		}
	}
	
	
	
	
	/**
	 * 跟新出票失败订单
	 */
	//@Scheduled(cron="0 0/1 0,1,2,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * ?")
	public void updateTuNiuFailOrder(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始跟新途牛出票失败订单.....");
			String secretKey=DictUtils.getDictCode("openTN", "isOpen");
			if("1".equals(secretKey)){	
				
				//TuniuOrderService.getFailOrder("1");
				Thread.sleep(5000);
				TuniuOrderService.getFailOrder("2");
				Thread.sleep(5000);
				//TuniuOrderService.getFailOrder("3");
			}
			//log.info(threadname+"---跟新途牛出票失败订单结束.....");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("跟新途牛出票失败订单异常！！！ " + e.getMessage());
		}
	}
	
	/**
	 * 同步退票单
	 */
	@Scheduled(cron = "0 0/2 * * * ?")
	public void syncRefundOrder(){
		try {
			String secretKey=DictUtils.getDictCode("openTN", "isOpen");
			Thread.sleep(4000);
			TuniuOrderService.getRefundOrder("2");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("跟新途牛退票单异常！！！ " + e.getMessage());
		}
	}
	/**
	 * 同步改签单
	 */
	@Scheduled(cron="0 0/5 * * * ?")
	public void syncChangeOrder(){
		try {
			String secretKey=DictUtils.getDictCode("openTN", "isOpen");
			if("1".equals(secretKey)){	
				TuniuOrderService.getChangeOrder("2");
			}
		} catch (Exception e) {
			log.error("跟新途牛改签单异常！！！ ");
		}
	}

	/**
	 * 跟新出票异常订单
	 */
	//@Scheduled(cron="0 0/30 * * * ?")
	public void updateTuNiuExceptionOrder(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始跟新途牛客票异常订单.....");
			TuniuOrderService.getExceptionOrder();
			//log.info(threadname+"---跟新途牛客票异常订单结束.....");
		} catch (Exception e) {
			log.error("跟新途牛客票异常订单订单异常！！！ " + e.getMessage());
		}
	}
	
	
	
	/**
	 *获取待出票未锁定订单，用于QQ提示
	 */
	//@Scheduled(cron="0 0/30 * * * ?")
	public void getUnLcockOrder(){
		try {
			//String threadname=Thread.currentThread().getName();
			//log.info(threadname+"---开始获取途牛待出票未锁订单.....");
			String status = "3"; //  //0、全部  3、待出票 4、出票成功 5、出票失败 6、处理中
			String apiKey=DictUtils.getDictCode("order_import_cfgtun", "apiKey");
			String secretKey=DictUtils.getDictCode("order_import_cfgtun", "secretKey");
			String validVendorIds=DictUtils.getDictCode("order_import_cfgtun", "validVendorIds");
			Assert.notNull(apiKey, "apiKey不能为空");
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.HOUR_OF_DAY, -5);
			String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			String metadata = TuniuOrderService.getMetadata(apiKey, secretKey, bDate, eDate, status, 1, 100, TuniuOrderService.getHttpClient2(), validVendorIds);
			Gson gson=new Gson();
			ListRespon listRespon=gson.fromJson(metadata, ListRespon.class);
			
			if(listRespon==null) return;
			if(listRespon.getSuccess().equals("false")) return;
			
			Datai datai=listRespon.getData();
			Integer i=0;
			for(Row row:datai.getRows()){
				TicketLockItem lock=row.getTicketLockItem();
				if(!lock.isLocked()){
					i++;
				}
			}
			if(i!=0){
				String message="途牛N-Booking还有"+i+"条待出票订单......";
				SendQQMsgUtil.send(message);
			}
			//log.info(threadname+"---获取途牛待出票未锁订单结束，"+i+"条未锁定.....");
		} catch (Exception e) {
			log.error("获取途牛待出票未锁订单异常！！！ " + e.getMessage());
		}
	}
	
	/**
	 * 导入途牛订单
	 * @param config
	 * @param status
	 */
	private void procBatchImportOrdersTn(InterfaceOrderImportConfigVO config,String status){
		List<String> errorList = new ArrayList<String>();
		try {
			List<OrderVO> orderVoList=tuniuOrderService.batchImportOrders(config.getSource(), config.getShopName(), status);
			if(orderVoList!=null && orderVoList.size()>0){
			    int i=0;
				for(OrderVO orderVo:orderVoList){
				    i++;
					String orderNo=orderVo.getOrderNo();
					boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_TN,orderNo);
					if(exist){
						boolean exist1=orderService.isExistcOrderNo(orderVo.getcOrderNo(), InterfaceConstant.ORDER_SOURCE_TN);
						if(exist1){
							errorList.add(orderNo);							
						}else{//途牛往返单
							Order order=orderVo.getOrder();
							order.setOrderNo(order.getOrderNo()+"-"+i);
							order.setOrderSource(config.getSource());
							order.setOrderShop(config.getShopName());
							procInitBatchImportOrderVO(orderVo);
							order.setCreateBy("system");
							int count = orderService.saveOrderVO(orderVo);//保存订单
							if(count!=0){
								TuniuOrderService.status1.offer(order.getOrderNo());	
							}
						}
					}else{
						Order order=orderVo.getOrder();
						order.setOrderSource(config.getSource());
						order.setOrderShop(config.getShopName());
						procInitBatchImportOrderVO(orderVo);
						order.setCreateBy("system");
						int count=orderService.saveOrderVO(orderVo);//保存订单
						if(count!=0){
							TuniuOrderService.status1.offer(order.getOrderNo());	
						}
					}
				}
			}
			
			int totalCount=orderVoList==null?0:orderVoList.size();
			int successCount=orderVoList==null?0:orderVoList.size()-errorList.size();
			String errorIds=Arrays.toString(errorList.toArray(new String[errorList.size()]));
			
			StringBuilder sb=new StringBuilder();
			sb.append("[system]tuniu"+config.getShopName()+"店批量导入订单成功。");
			sb.append(", status : ").append(status);
			sb.append(", totalCount : ").append(totalCount);
			sb.append(", successCount : ").append(successCount);
			sb.append(", errorIds(db exists) : ").append(errorIds);
			//log.info(sb.toString());
			
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("[system]tuniu2批量导入订单异常！！！ ");
			sb.append(", status : ").append(status);
			sb.append(", errorMsg : ").append(e.getMessage());
			log.error(sb.toString());
		}
	}

	/**
	 * 封装order
	 * @param orderVO
	 */
	public static void procInitBatchImportOrderVO(OrderVO orderVO){
		if (null == orderVO){
			return;
		}
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Order order=orderVO.getOrder();
		order.setCreateDate(createDate);
		order.setStatus(WebConstant.ORDER_NO_TICKET); // 未出票 
		order.setPayStatus(WebConstant.PAYMENT_ALREADY); // 已支付
		order.setInterfaceImport(WebConstant.INTERFACE_IMPORT_YES);
		order.setUpdateTicketStatus(WebConstant.UPDATE_TICKET_NO_DISABLE);

		// 航班信息
		List<Flight> flightList = orderVO.getFlightList();
		Flight flight=null;
		if(flightList!=null && flightList.size()>0){
			for(Flight f:flightList){
				f.setOrderSource(order.getOrderSource());
				f.setOrderShop(order.getOrderShop());
				f.setOrderNo(order.getOrderNo());
				f.setCreateBy(order.getCreateBy());
				f.setCreateDate(createDate);
				if(flight==null){
					flight=f;
				}
			}
		}
		
		//乘机人信息
		List<Passenger> passengerList=orderVO.getPassengetList();
		Passenger passenger=null;
		if(passengerList!=null && passengerList.size()>0){
			for(Passenger p:passengerList){
				p.setOrderSource(order.getOrderSource());
				p.setOrderShop(order.getOrderShop());
				p.setOrderNo(order.getOrderNo());
				p.setPolicyType(order.getPolicyType());
				p.setCreateBy(order.getCreateBy());
				p.setCreateDate(createDate);
				if(passenger==null){
					passenger=p;
				}
			}
		}
		order.setPassengerCount(passengerList==null?"0":String.valueOf(passengerList.size()));
		
		if (null != flight){
			order.setAirlineCode(flight.getAirlineCode());
			order.setFlightNo(flight.getFlightNo());
			order.setFlightType(flight.getSegmentType());
			order.setDepCityCode(flight.getDepCityCode());
			order.setArrCityCode(flight.getArrCityCode());
			order.setFlightDate(flight.getFlightDepDate());
			order.setCabin(flight.getCabin());
		}
		
		// 行程单信息
		Travel travel=orderVO.getTravel();
		if(travel!=null){
			travel.setOrderNo(order.getOrderNo());
			travel.setOrderSource(order.getOrderSource());
			travel.setOrderShop(order.getOrderShop());
			if (null != passenger){
				travel.setPnr(passenger.getPnr());
			}
			if (null != flight){
				travel.setAirlineCode(flight.getAirlineCode());
				travel.setFlightNo(flight.getFlightNo());
				travel.setFlightType(flight.getSegmentType());
				travel.setDepCityCode(flight.getDepCityCode());
				travel.setArrCityCode(flight.getArrCityCode());
				travel.setFlightDepDate(flight.getFlightDepDate());
				travel.setFlightArrDate(flight.getFlightArrDate());
				travel.setDepTime(flight.getDepTime());
				travel.setArrTime(flight.getArrTime());
				travel.setCabin(flight.getCabin());
			}
		}
	}


}
