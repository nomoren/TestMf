package cn.ssq.ticket.system.service;

import cn.ssq.ticket.system.entity.Flight;
import cn.ssq.ticket.system.entity.FlightVO;
import cn.ssq.ticket.system.queryEntity.OrderQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.util.List;


public interface FlightService {

	/**
	 * 根据订单号查询航段信息
	 * @param orderNo
	 * @return
	 */
	List<Flight> selectFlightInfo(String orderNo);
	
	/**
	 * 根据订单id查询航段信息
	 * @param
	 * @return
	 */
	List<Flight> selectFlightInfoByOrderId(String orderId);
	
	/**
	 * 获取符合条件的航变
	 * @param query
	 * @return
	 */
	List<FlightVO> getFlightChangneInfo(OrderQuery query);
	
	/**
	 * 已录入的航变
	 * @return
	 */
	List<FlightVO> getFlightChangneList(int jump,int limit);
	
	Flight getById(String flightId);
	
	int getFlightChangneCount();
	
	int getFlightChangneInfoCount(OrderQuery query);
	
	void updateByQuery(Flight f,UpdateWrapper<Flight> updateWrapper);
	
	void updateById(Flight flight);

    List<Flight> getByQueryW(QueryWrapper<Flight> query);
}
