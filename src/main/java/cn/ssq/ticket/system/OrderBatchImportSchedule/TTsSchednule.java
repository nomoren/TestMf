package cn.ssq.ticket.system.OrderBatchImportSchedule;

import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderOperateLog;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.service.LogService;
import cn.ssq.ticket.system.service.OrderImport.impl.TTsOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.PurchaseService;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.ssq.ticket.system.util.InterfaceOrderImportConfigVO;
import cn.ssq.ticket.system.util.SendQQMsgUtil;
import cn.ssq.ticket.system.util.WebConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 去哪儿自动调度
 */
@Component
public class TTsSchednule {
	private static Logger log = LoggerFactory.getLogger(TTsSchednule.class);

	@Autowired
	private OrderService orderService;

	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	private LogService logService;

	@Autowired
	private TTsOrderService ttsOrderService;

	@Autowired
	MongoTemplate mongnTemplate;

	//@PostConstruct
	public void updateOrderStatus() {
		updateTicketNo();
	}

	/**
	 * 批量导入tts待出票订单(rnf)
	 */
	@Scheduled(cron = "0 0/4 * * * ?")
	public void importTTsOrderRnf() {
		try {
			// String threadname=Thread.currentThread().getName();
			// log.info(threadname+"---开始导入TTS rnf订单.....");
			String status = "1"; // 1、待出票 5、处理中
			InterfaceOrderImportConfigVO config = new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_QNR);
			config.setShopName("2");
			procBatchImportOrdersTTS(config, status);
			status = "5";
			procBatchImportOrdersTTS(config, status);
			// log.info(threadname+"---导入TTS rnf订单结束.....");
		} catch (Exception e) {
			log.error("TTS rnf 导入订单异常！！！ " + e.getMessage());
		}
	}

	/**
	 * 批量导入tts待出票订单(rnb)
	 */
	@Scheduled(cron = "0 0/5 * * * ?")
	public void importTTsOrderRnb() {
		try {
			// String threadname=Thread.currentThread().getName();
			// log.info(threadname+"---开始导入TTS rnb订单.....");
			String status = "1"; // 1、待出票 5、处理中
			InterfaceOrderImportConfigVO config = new InterfaceOrderImportConfigVO();
			config.setSource(InterfaceConstant.ORDER_SOURCE_QNR);
			config.setShopName("1");
			procBatchImportOrdersTTS(config, status);

			status = "5";
			procBatchImportOrdersTTS(config, status);
			// log.info(threadname+"---导入TTS rnb订单结束.....");
		} catch (Exception e) {
			log.error("TTS rnb导入订单异常！！！ " + e.getMessage());
		}
	}

	public void procBatchImportOrdersTTS(InterfaceOrderImportConfigVO config, String status) {
		try {
			List<OrderVO> orderVoList = ttsOrderService.batchImportOrders(config.getSource(), config.getShopName(),
					status);
			List<String> errorList = new ArrayList<String>();
			if (orderVoList.size() > 0) {
				for (OrderVO orderVo : orderVoList) {
                    String orderNo = orderVo.getOrderNo();
					boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo);
					if (exist) {
						errorList.add(orderNo);
					} else {
						int count = orderService.saveOrderVO(orderVo);// 保存订单
						if (count != 0) {
							TTsOrderService.status1.offer(orderNo);
						}
					}
				}
			}
			int totalCount = orderVoList == null ? 0 : orderVoList.size();
			int successCount = orderVoList == null ? 0 : orderVoList.size() - errorList.size();
			String errorIds = Arrays.toString(errorList.toArray(new String[errorList.size()]));
			StringBuilder sb = new StringBuilder();
			sb.append("[system]TTS" + config.getShopName() + "店批量导入订单成功。");
			sb.append(", status : ").append(status);
			// sb.append(", lastId :
			// ").append(ConfigUtils.getParam("ttsOrderStatus1LastId"));
			sb.append(", totalCount : ").append(totalCount);
			sb.append(", successCount : ").append(successCount);
			sb.append(", errorIds(db exists) : ").append(errorIds);
			// log.info(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append("[system]TTS批量导入订单异常！！！ ");
			sb.append(", status : ").append(status);
			sb.append(", errorMsg : ").append(e.getMessage());
			log.error(sb.toString());

		}
	}

	public void updateTicketNo() {
		try {
			QueryWrapper<Order> query = new QueryWrapper<Order>();
			query.eq("order_source", "02");
			List<String> status = new ArrayList<String>();
			status.add(WebConstant.ORDER_NO_TICKET);
			status.add(WebConstant.ORDER_PRINT);
			query.in("status", status);
			List<Order> orderList = orderService.getOrderList(query);
			if (orderList.size() == 0) {
				return;
			}
			ttsOrderService.updateTickNo(orderList);
			log.info("---更新tts订单票号结束.....");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("更新tts订单票号异常！！！ " + e.getMessage());
		}
	}

	@Scheduled(cron = "0 0/3 * * * ?")
	public void tt() {
		try {
			String eDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			/*
			 * calendar.add(Calendar.DAY_OF_MONTH, -10); String
			 * bDate=DateFormatUtils.format(calendar.getTime(),
			 * "yyyy-MM-dd HH:mm:ss"); UpdateWrapper<Passenger> uw1=new
			 * UpdateWrapper<Passenger>(); uw1.eq("IS_IM_RET", "0");
			 * uw1.between("create_date", bDate, eDate); Passenger p2=new
			 * Passenger(); p2.setStatus("3"); pm.update(p2, uw1);
			 */

			Calendar calendar2 = Calendar.getInstance();
			calendar2.add(Calendar.DAY_OF_MONTH, -1);
			String startDate = DateFormatUtils.format(calendar2.getTime(), "yyyy-MM-dd HH:mm:ss");
			QueryWrapper<Order> query = new QueryWrapper<Order>();
			query.eq("process_by", "机器人");
			query.eq("status", WebConstant.ORDER_NO_TICKET);
			query.between("create_date", startDate, eDate);
			List<Order> orderList = orderService.getOrderList(query);
			long nm = 1000 * 60;
			for (Order o : orderList) {
				Date create = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o.getCreateDate());
				long diff = new Date().getTime() - create.getTime();
				long minute = diff / nm;
				/*
				 * System.out.println("==="); System.out.println(minute);
				 */
				if (minute >= 8) {
					orderService.deleteProcess(o.getOrderNo());
					OrderOperateLog log = new OrderOperateLog();
					log.setOrderNo(o.getOrderNo());
					log.setContent("机器人超时解锁订单" + minute);
					log.setType("订单处理");
					log.setName("机器人");
					logService.saveLog(log);
				}
			}

			/*
			 * QueryWrapper<Order> query2=new QueryWrapper<Order>();
			 * query2.eq("process_by", "深航采购"); query2.eq("status",
			 * WebConstant.ORDER_NO_TICKET); query2.between("create_date",
			 * startDate, eDate); List<Order> orderList2 =
			 * orderService.getOrderList(query2); for(Order o:orderList2){ Date
			 * create = new
			 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o.getCreateDate());
			 * long diff = new Date().getTime() - create.getTime(); long minute
			 * = diff/nm; System.out.println("==="); System.out.println(minute);
			 * if(minute>=14){ orderService.deleteProcess(o.getOrderNo()); } }
			 */
		} catch (Exception e) {

		}
	}

//	@Scheduled(cron = "0 0/16 * * * ?")
	public void getB2BtciketCount() {
		try {
			String format = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
			List<Integer> ACount = orderService.getB2BtciketCount("%SHEB2B%A%", format + " 00:00:00",
					format + " 23:59:59");
			int A = 0;
			for (Integer count : ACount) {
				A = A + count;
			}

			List<Integer> BCount = orderService.getB2BtciketCount("%SHEB2B%B%", format + " 00:00:00",
					format + " 23:59:59");
			int B = 0;
			for (Integer count : BCount) {
				B = B + count;
			}

			List<Integer> CCount = orderService.getB2BtciketCount("%SHEB2B%C%", format + " 00:00:00",
					format + " 23:59:59");
			int C = 0;
			for (Integer count : CCount) {
				C = C + count;
			}

			// List<Integer> DCount =
			// orderService.getB2BtciketCount("YZHSHEB2B-D", format+" 00:00:00",
			// format+" 23:59:59");
			int D = 0;
			/*
			 * for(Integer count:DCount){ D=D+count; }
			 */
			List<String> DCount2 = purchaseService.getB2BtciketCountByRemark(format + " 00:00:00", format + " 23:59:59",
					"%-D%");
			for (String str : DCount2) {
				D = D + str.split(",").length;
			}

			// List<Integer> ECount =
			// orderService.getB2BtciketCount("YZHSHEB2B-E", format+" 00:00:00",
			// format+" 23:59:59");
			int E = 0;
			/*
			 * for(Integer count:ECount){ E=E+count; }
			 */
			List<String> ECount2 = purchaseService.getB2BtciketCountByRemark(format + " 00:00:00", format + " 23:59:59",
					"%-E%");
			for (String str : ECount2) {
				E = E + str.split(",").length;
			}

			String message = "YZHSHEB2B-A:" + A + "张-----YZHSHEB2B-B:" + B + "张-----YZHSHEB2B-C:" + C
					+ "张-----YZHSHEB2B-D:" + D + "张-----YZHSHEB2B-E:" + E + "张";
			// System.out.println(message);
			SendQQMsgUtil.send2Qun(message, "912704517");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 同步改签订单
	 */
	@Scheduled(cron = "0 0/3 * * * ?")
	public void syncChangeOrder() {
		try {
			Thread.sleep(1700);
			Calendar calendar = Calendar.getInstance();
			//calendar.add(Calendar.HOUR_OF_DAY, 1);
			String date = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
			// rnf
			ttsOrderService.sysncChangeOrder("rnf", date);
			Thread.sleep(2000);
			// rnb
			ttsOrderService.sysncChangeOrder("rnb", date);
		} catch (Exception e) {
			log.error("同步TTS改期单异常！！！ ", e);
		}
	}

}
