package cn.ssq.ticket.system.service.impl;

import cn.ssq.ticket.system.entity.Flight;
import cn.ssq.ticket.system.entity.FlightVO;
import cn.ssq.ticket.system.mapper.FlightMapper;
import cn.ssq.ticket.system.queryEntity.OrderQuery;
import cn.ssq.ticket.system.service.FlightService;
import cn.ssq.ticket.system.util.DictUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FlightServiceImpl implements FlightService{

	@Autowired
	private FlightMapper flightMapper;
	
	@Override
	public List<Flight> selectFlightInfo(String orderNo) {
		List<Flight> selectFlightInfo = flightMapper.selectFlightInfo(orderNo);
		for(Flight flight:selectFlightInfo){
			String depTime=flight.getDepTime();
			String arrTimes=flight.getArrTimes();
			if(StringUtils.isBlank(arrTimes)){
				arrTimes=flight.getArrTime();
			}
			if(depTime.split(":").length<3){
				flight.setDepTimes(flight.getDepTimes()+":00");
				if(StringUtils.isNoneBlank(arrTimes)){
					flight.setArrTimes(arrTimes+":00");
				}
			}
		}
		return selectFlightInfo;
	}

	@Override
	public List<Flight> selectFlightInfoByOrderId(String orderId) {
		List<Flight> selectFlightInfo = flightMapper.selectFlightInfoByOrderId(orderId);
		for(Flight flight:selectFlightInfo){
			String depTime=flight.getDepTime();
			String arrTimes=flight.getArrTimes();
			if(StringUtils.isBlank(arrTimes)){
				arrTimes=flight.getArrTime();
			}
			if(depTime.split(":").length<3){
				flight.setDepTimes(flight.getDepTimes()+":00");
				if(StringUtils.isNoneBlank(arrTimes)){
					flight.setArrTimes(arrTimes+":00");
				}
			}
		}
		return selectFlightInfo;
	}

	@Override
	public List<FlightVO> getFlightChangneInfo(OrderQuery query) {
		List<FlightVO> flightChangneInfo = flightMapper.getFlightChangneInfo(query);
		for(FlightVO f:flightChangneInfo){
			String dictName = DictUtils.getDictName("order_source", f.getOrderSource());
			f.setRemark(dictName+f.getOrderShop());
		}
		return flightChangneInfo;
	}

	@Override
	public List<FlightVO> getFlightChangneList(int jump,int limit) {
		List<FlightVO> flightChangneList = flightMapper.getFlightChangneList(jump,limit);
		for(FlightVO f:flightChangneList){
			String dictName = DictUtils.getDictName("order_source", f.getOrderSource());
			f.setRemark(dictName+f.getOrderShop());
		}
		return flightChangneList;
	}

	@Override
	public int getFlightChangneCount() {
		return flightMapper.getFlightChangneCount();
	}

	@Override
	public int getFlightChangneInfoCount(OrderQuery query) {
		return flightMapper.getFlightChangneInfoCount(query);
	}

	@Override
	public Flight getById(String flightId) {
		return flightMapper.selectById(flightId);
	}

	@Override
	public void updateByQuery(Flight f,UpdateWrapper<Flight> updateWrapper) {
		flightMapper.update(f, updateWrapper);
	}

	@Override
	public void updateById(Flight flight) {
		flightMapper.updateById(flight);
		
	}


    @Override
    public List<Flight> getByQueryW(QueryWrapper<Flight> query) {
        return flightMapper.selectList(query);
    }
	
	
	

}
