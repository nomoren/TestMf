package cn.ssq.ticket.system.runnable;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.entity.Refund;
import cn.ssq.ticket.system.service.RefundService;
import cn.stylefeng.roses.core.util.SpringContextHolder;

public class RefundSyncInfo implements Runnable{

	private Purchase purchase;
	
	public RefundSyncInfo(Purchase purchase) {
		this.purchase=purchase;
	}
	@Override
	public void run() {
		//从采购单同步商户订单号
		try {
			RefundService refundService = SpringContextHolder.getBean(RefundService.class);
			QueryWrapper<Refund> query=new QueryWrapper<Refund>();
			query.eq("order_id", purchase.getOrderId());
			List<Refund> refundsByQueryWapper = refundService.getRefundsByQueryWapper(query);
			List<String> nameList = Arrays.asList(purchase.getPassengerNames().split(","));
			for(Refund refund:refundsByQueryWapper) {
				//采购单乘客需包含退票乘客
				if(nameList.contains(refund.getPassengerName())) {			
					refund.setBusinessNo(purchase.getBusinessNo());
					refundService.updateById(refund);	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
