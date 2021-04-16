package cn.ssq.ticket.system.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TTsOrderService;
import cn.ssq.ticket.system.util.WebConstant;
import cn.stylefeng.roses.core.util.SpringContextHolder;

/**
 * 出票中
 * @author Administrator
 *
 */
public class UpdateStatus implements Runnable{
	private OrderVO orderVo;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	public UpdateStatus(OrderVO orderVo) {
		this.orderVo=orderVo;
	}
	@Override
	public void run() {
		String orderNo=orderVo.getOrderNo();
		String orderSource=orderVo.getOrderSource();
		try {
			OrderService orderService=SpringContextHolder.getBean(OrderService.class);
			Order o = orderService.getOrderByOrderNo(orderNo);
			if("TTS".equals(orderSource)){//去哪儿
				TTsOrderService tts=SpringContextHolder.getBean(TTsOrderService.class);
				if(WebConstant.ORDER_NO_TICKET.equals(o.getStatus())){
					if(orderNo.startsWith("rnf")){				
						tts.updateOrderStatus(orderNo,"rnf",true);
					}else{
						tts.updateOrderStatus(orderNo,"rnb",true);
					}
				}
			}/*else if("携程".equals(orderSource)){
				if(WebConstant.ORDER_NO_TICKET.equals(o.getStatus())){
					CtripOrderService ctri=SpringContextHolder.getBean(CtripOrderService.class);
					ctri.assignOrder(o.getOrderNo(),o.getOrderShop(),"1",true);
				}
			}*//*else if("同程".equals(orderSource)){
				TcOrderService tc=SpringContextHolder.getBean(TcOrderService.class);
				tc.lockedOrder(orderNo,true);
			}*/
			
			
			
			
		} catch (Exception e) {
			log.info("点击处理修改订单状态失败:"+orderNo);
			e.printStackTrace();
		}
	}

}
