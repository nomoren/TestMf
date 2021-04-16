package cn.ssq.ticket.system.service;

import cn.ssq.ticket.system.entity.Passenger;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.util.List;



public interface PassengerService {

	
	List<Passenger> getPassengersByOrderNo(String orderNo);
	
	List<Passenger> getPassengersByRetNo(String retNo);

	List<Passenger> find(String startDate,String endDate,String orderSorce,String orderShop);
	
	Passenger getById(String passengerId);
	
	Passenger getByQuery(String orderNo,String name);
	
	void updateTicketStatus(Passenger passenger);
	
	Integer updateById(Passenger passenger);
	
	List<Passenger> findByQueryWapper(QueryWrapper<Passenger> query);
	
	void updateByWapper(Passenger p,UpdateWrapper<Passenger> update);

	Passenger findOneByQueryWapper(LambdaQueryWrapper<Passenger> query);
}
