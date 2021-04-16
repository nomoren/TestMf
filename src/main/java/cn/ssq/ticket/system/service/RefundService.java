package cn.ssq.ticket.system.service;


import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.entity.Refund;
import cn.ssq.ticket.system.entity.RefundVO;
import cn.ssq.ticket.system.queryEntity.RefundQuery;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.util.List;


public interface RefundService {

	List<RefundVO> getRefundList(RefundQuery query);
	
	int getRefundListCount(RefundQuery query);
	
	List<Passenger> getRefundPassenger(String retNo);
	
	Refund getRefund(String retNo);
	
	Refund getRefundById(String refundId);
	
	void changeRemStatus(String status,String retNo);
	
	void changeRemStatusOk(String cRealPrice,String retNo);
	
	void saveRefunds(List<Refund> list);
	
	void updateRefunds(List<Refund> list);
	
	void deleteRefund(String[] retNoArray);
	
	void autoCreateRefund(Refund refund);
	
	boolean isExit(String retNo,String orderSource,String orderShop,String name);
	
	void updateById(Refund refund);
	
	List<Refund> getRefundsByRetNo(String retNo);
	
	List<RefundVO> getDataForReport(String startDate,String endDate,String orderSorce,String orderShop);

	List<Refund> getRefundsByQueryWapper(Wrapper<Refund> query);

	void addProcess(String process,String retNo);
	
	void addProcessById(String process,String refundId);
	
	void deleteProcess(String retNo);
	
	void deleteProcessById(String refundId);
	
	String isHavePcocess(String retNo);
	
	String isHavePcocessById(String refundId);

    List<RefundVO> getDataForReportList(RefundQuery query);

	int updateByWrapper(Refund refund, UpdateWrapper<Refund> updateWrapper);

	void initializedPassengerRefund(String orderNo,String name);
}
