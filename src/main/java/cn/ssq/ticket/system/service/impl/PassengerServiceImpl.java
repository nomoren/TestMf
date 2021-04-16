package cn.ssq.ticket.system.service.impl;

import cn.ssq.ticket.system.entity.Change;
import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.entity.Refund;
import cn.ssq.ticket.system.mapper.ChangeMapper;
import cn.ssq.ticket.system.mapper.PassengreMapper;
import cn.ssq.ticket.system.service.PassengerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService{
	@Autowired
	private PassengreMapper passengerMapper;
	@Autowired
	private ChangeMapper changeMapper;
	

	/**
	 * 获取订单所有乘机人
	 */
	@Override
	public List<Passenger> getPassengersByOrderNo(String orderNo) {
		List<Passenger> pList = passengerMapper.getByOrderNo(orderNo);
		for(Passenger passenger : pList) {
            Refund refund = passenger.getRefund();
            if (refund != null) {
                refund.setXePnrStatus(StringUtils.isEmpty(refund.getXePnrStatus())?"0":refund.getXePnrStatus());
                refund.setRefundType(StringUtils.isEmpty(refund.getRefundType())?"0":refund.getRefundType());
            }
            QueryWrapper<Change> query=new QueryWrapper<Change>();
			query.eq("order_no", passenger.getOrderNo());
			query.eq("passenger_Name", passenger.getName());
			Change selectOne = changeMapper.selectOne(query);
			passenger.setChange(selectOne);
		}
		return pList;
	}


	/**
	 * 获取订单所有退票乘机人
	 */
	@Override
	public List<Passenger> getPassengersByRetNo(String retNo) {
		return passengerMapper.getByRetNo(retNo);
	}

	
	@Override
	public List<Passenger> find(String startDate, String endDate,
			String orderSorce, String orderShop) {
		startDate=startDate+" 00:00:00";
		endDate=endDate+" 23:59:59";
		QueryWrapper<Passenger> query=new QueryWrapper<Passenger>();
		query.between("print_ticket_date", startDate, endDate);
		query.eq("order_source", orderSorce);
		query.ne("ticket_no", "");
		if(!"0".equals(orderShop)){
			if("1".equals(orderSorce)||"06".equals(orderSorce)||"03".equals(orderSorce)||"02".equals(orderSorce)){
				query.eq("Order_shop", orderShop);
			}
		}
		return passengerMapper.selectList(query);
	}


	@Override
	public Passenger getById(String passengerId) {
		return passengerMapper.selectById(passengerId);
	}


	@Override
	public Passenger getByQuery(String orderNo,String name) {
		return passengerMapper.selectOne(new QueryWrapper<Passenger>().eq("order_no", orderNo).eq("name", name));
	}


	@Override
	public void updateTicketStatus(Passenger passenger) {
		passenger.setOrderNo(null);
		passengerMapper.updateById(passenger);
	}


	@Override
	public Integer updateById(Passenger passenger) {
		Integer count = passengerMapper.updateById(passenger);
		return count;
	}


	@Override
	public List<Passenger> findByQueryWapper(QueryWrapper<Passenger> query) {
		return passengerMapper.selectList(query);
	}


	@Override
	public void updateByWapper(Passenger p, UpdateWrapper<Passenger> update) {
		passengerMapper.update(p, update);
	}

	@Override
	public Passenger findOneByQueryWapper(LambdaQueryWrapper<Passenger> query) {
		return passengerMapper.selectOne(query);
	}


}
