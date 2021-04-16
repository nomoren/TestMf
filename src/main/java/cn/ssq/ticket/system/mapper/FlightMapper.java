package cn.ssq.ticket.system.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.ssq.ticket.system.entity.Flight;
import cn.ssq.ticket.system.entity.FlightVO;
import cn.ssq.ticket.system.queryEntity.OrderQuery;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 航段mapper
 * @author Administrator
 *
 */
public interface FlightMapper extends BaseMapper<Flight>{
	
	/**
	 * 根据订单号查询航段信息
	 * @param orderNo
	 * @return  
	 */
	List<Flight> selectFlightInfo(String orderNo);
	
	/**
	 * 根据订单id查询航段信息
	 * @param orderNo
	 * @return 
	 */
	List<Flight> selectFlightInfoByOrderId(String orderId);
	
	/**
	 * 获取符合条件航变
	 * @param query
	 * @return
	 */
	List<FlightVO> getFlightChangneInfo(OrderQuery query);
	/**
	 * 已录入的航变
	 * @return
	 */
	List<FlightVO> getFlightChangneList(@Param("jump") int jump,@Param("limit") int limit);
	
	int getFlightChangneCount();
	
	int getFlightChangneInfoCount(OrderQuery query);	
}
