package cn.ssq.ticket.system.util;

import cn.ssq.ticket.system.entity.OrderOperateLog;
import cn.ssq.ticket.system.entity.ResponseResult;
import cn.ssq.ticket.system.service.LogService;
import cn.ssq.ticket.system.service.OrderService;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;


public class LockOrderUtil {
	
	private static LogService logService = SpringContextHolder.getBean(LogService.class);
	
	private static OrderService orderService = SpringContextHolder.getBean(OrderService.class);

	
	/**
	 * 机器人锁单
	 * @param orderNo
	 * @return
	 */
	public static Object robotLock(String orderNo){
		ResponseResult<Boolean> result=new ResponseResult<Boolean>();
		String name="机器人";
		OrderOperateLog log=new OrderOperateLog();
		log.setOrderNo(orderNo);
		log.setContent("锁单");
		log.setType("订单处理");
		log.setName("机器人");
		try {
			String havePcocess = orderService.isHavePcocess(orderNo);
			if(StringUtils.isNotEmpty(havePcocess)){
				if(name.equals(havePcocess)){
					result.setCode(0);
					result.setMsg(havePcocess);
					result.setData(true);
					logService.saveLog(log);
				}else{
					result.setCode(-2);
					result.setMsg(havePcocess);
					result.setData(false);
				}
				return result;
			}
			orderService.addProcess(name, orderNo);//加锁定人
			logService.saveLog(log);
		} catch (Exception e) {
			result.setCode(-1);
			result.setMsg("锁定失败");
			result.setData(false);
		}
		System.out.println("MF锁定:"+result.toString());
		return result;
	}

	public static Object robotUnLock(String orderNo,String msg){
		ResponseResult<Boolean> result=new ResponseResult<Boolean>();
		OrderOperateLog log=new OrderOperateLog();
		log.setOrderNo(orderNo);
		log.setContent(msg);
		log.setType("订单处理");
		log.setName("SYSTEM");
        logService.saveLog(log);
      /*  try {
            String havePcocess = orderService.isHavePcocess(orderNo);
            if(StringUtils.isNotEmpty(havePcocess)){
                if(!"机器人".equals(havePcocess)){
                    result.setCode(-2);
                    result.setMsg(havePcocess);
                    result.setData(false);
                    return result;
                }
            }
            orderService.deleteProcess(orderNo);
		} catch (Exception e) {
			result.setCode(-1);
			result.setMsg("解锁失败");
			result.setData(false);
		}
		System.out.println("MF解锁:"+result.toString());*/
		return result;
	}

}
