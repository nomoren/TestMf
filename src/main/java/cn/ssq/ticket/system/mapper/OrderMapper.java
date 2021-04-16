package cn.ssq.ticket.system.mapper;


import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.queryEntity.OrderQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface OrderMapper extends BaseMapper<Order>{
	
	/**
	 * 根据查询条件查询订单列表
	 * @param query
	 * @return
	 */
	List<OrderVO> selectOrderVoList(OrderQuery query);
	
	
	List<Order> selectOrderList(OrderQuery query);
	
	
	Integer selectOrderVoCount(OrderQuery query);
	
	Integer selectOrderCount(OrderQuery query);
	
	/**
	 * 根据订单号查询订单
	 * @param orderNo
	 * @return
	 */
	List<OrderVO> selectOrderDetails(@Param("orderNo")String orderNo,@Param("orderSource")String orderSource,@Param("orderShop")String orderShop);
	
	/**
	 * 添加订单的处理人
	 * @param process
	 * @param orderNo
	 */
	int addProcess(@Param("process")String process,@Param("orderNo")String orderNo);
	
	void deleteProcess(String orderNo);
	
	String getStatus(String orderNo);
	
	/**
	 * 删除订单
	 * @param orderNoArray
	 */
	void deleteOrder(String[] orderNoArray);
	
	void updateStatus(@Param("status")String status,@Param("orderNo")String orderNo);
	
	void savePnr(@Param("pnr")String pnr,@Param("orderNo")String orderNo);
	
	String isHavePcocess(String orderNo);
	
	String isExist(@Param("orderSource")String orderSource,@Param("orderNo")String orderNo);
	
	String isExistcOrderNo(@Param("cOrderNo")String cOrderNo,@Param("orderSource")String orderSource);
	
	List<String> getOrderPassenger(String orderNo);
	
	/**
	 * 获取订单号
	 * @param orderSource 平台
	 * @param orderShop 店铺
	 * @param status 状态
	 * @return
	 */
	List<String> getOrderNoList(@Param("orderSource")String orderSource,@Param("orderShop")String orderShop,
								@Param("status")String status);
	
	List<String> getcOrderNoList(@Param("orderSource")String orderSource,@Param("orderShop")String orderShop,
			@Param("status")String status);
	
	List<OrderVO> getOrderVoList(@Param("startDate")String startDate, @Param("endDate")String endDate,
			@Param("orderSource")String orderSource, @Param("orderShop")String orderShop);
	
	//List<Order> getOrderList(QueryWrapper<Order> query);
	
	List<Integer> getB2BtciketCount(@Param("policyType")String policyType,@Param("startDate")String startDate,@Param("endDate")String endDate);
	
	Integer getLockCount(@Param("name")String name,@Param("startDate")String startDate,@Param("endDate")String endDate);
	
	Integer	getAutoPrintLocks(@Param("minDate")String minDate, @Param("name")String name,@Param("status")String status);

	Order getOurOrderNo(String orderNo);

    String getPnrByOrderNo(String orderNo);

    void updateBigPnr(@Param("bigPnr")String bigPnr,@Param("orderId")long orderId);

    void updateOutOrderNo(@Param("outOrderNo")String outOrderNo,@Param("status")String status,@Param("orderId")long orderId);
	
}
