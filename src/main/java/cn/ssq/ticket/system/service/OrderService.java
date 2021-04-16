package cn.ssq.ticket.system.service;

import cn.ssq.ticket.system.entity.Flight;
import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.exception.OrderToMuchException;
import cn.ssq.ticket.system.queryEntity.OrderQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface OrderService {

	/**
	 * 根据所有查询条件查询订单
	 * @return
	 */
	Object selectOrderList(OrderQuery query);
	
	Order getOrderByOrderNo(String orderNo);
	
	Order getOrderBycOrderNo(String corderNo);
	
	Order getOrderById(String orderId);
	/**
	 * 根据订单号查询订单
	 * @param orderNo
	 * @return
	 */
	OrderVO selectOrderDetails(String orderNo,String orderSource,String orderShop) throws OrderToMuchException;


    OrderVO selectOrderDetailsAuto(String orderNo) ;

	/**
	 * 新增订单
	 * @param order
	 * @param pList
	 * @param fList
	 */
	void addOrder(Order order,List<Passenger> pList,List<Flight> fList);

	
	/**
	 * 修改订单
	 * @param order
	 * @param pList
	 * @param fList
	 */
	void editOrder(Order order,List<Passenger> pList,List<Flight> fList);
	
	/**
	 * 添加订单的处理人
	 * @param process
	 * @param orderNo
	 */
	int addProcess(String process,String orderNo);
	
	int savePnr(String pnr,String orderNo);
	
	void deleteProcess(String orderNo);
	
	String isHavePcocess(String orderNo);

	/**
	 * 删除订单
	 * @param orderNoArray
	 */
	void deleteOrder(String[] orderNoArray);
	
	void updateStatus(String status,String orderNo);
	
	boolean isExist(String orderSource,String orderNo);
		
	boolean isExistcOrderNo(String cOrderNo,String orderSource);
	
	List<String> getOrderPassenger(String orderNo);
	
	int saveOrderVO(OrderVO orderVO);
	
	/**
	 * 跟新票号
	 * @param orderNo
	 * @param
	 */
	void updateTicketNo(String orderNo,List<Passenger> list);
	
	/**
	 * 获取订单号
	 * @param orderSource 平台
	 * @param orderShop 店铺
	 * @param status 状态
	 * @return
	 */
	List<String> getOrderNoList(String orderSource,String orderShop,String status);
	
	List<String> getcOrderNoList(String orderSource,String orderShop,String status);
	
	
	List<OrderVO> getOrderVoList(String startDate,String endDate,String orderSource,
			String orderShop);
	
	void updateOrder(Order order);
	
	void updateOrderByID(Order order);
	
	List<Order> getOrderList(QueryWrapper<Order> query);
	
	IPage<Order> getOrderList2(QueryWrapper<Order> query,Page<Order> orderPag);
	
	Integer getLockCount(String name);
	
	String getStatus(String orderNo);
	
	List<Integer> getB2BtciketCount(String policyType,String startDate,String endDate);
	
	Integer getAutoPrintLocks(String minDate,String name,String status);

	Order getOutOrderNo(String orderNo);

    String getPnrByOrderNo(String orderNo);

    void updateBigPnr(String bigPnr,long orderId);

    void updateOutOrderNo(String outOrderNo,String status,long orderId);

    int updateByWrapper(Order order, UpdateWrapper<Order> updateWrapper);

}
