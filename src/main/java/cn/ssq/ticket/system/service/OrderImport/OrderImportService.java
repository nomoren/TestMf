package cn.ssq.ticket.system.service.OrderImport;

import java.util.List;

import cn.ssq.ticket.system.entity.OrderVO;


public interface OrderImportService {
	/**
	 * 订单批量导入
	 * @param orderSource
	 * @param orderShop
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public List<OrderVO> batchImportOrders(String orderSource,
			String orderShop, String status) throws Exception;
	

}
