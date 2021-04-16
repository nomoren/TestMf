package cn.ssq.ticket.system.mapper;
import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.entity.Refund;
import cn.ssq.ticket.system.entity.RefundVO;
import cn.ssq.ticket.system.queryEntity.RefundQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RefundMapper extends BaseMapper<Refund>{

	List<RefundVO> getRefundList(RefundQuery query);
	
	int getRefundListCount(RefundQuery query);
	
	List<Passenger> getRefundPassenger(String retNo);
	
	List<Refund> getRefund(String retNo);
	
	void changeRemStatus(@Param("status")String status,@Param("retNo")String retNo);
	
	void changeRemStatusOk(@Param("cRealPrice")String cRealPrice,@Param("retNo")String retNo);
	
	void updateRefund(Refund refund);
	
	void deleteRefund(String[] retNoArray);
	
	List<RefundVO> getDataForReport(@Param("startDate")String startDate,@Param("endDate")String endDate,
			@Param("orderSorce")String orderSorce,@Param("orderShop")String orderShop);

    List<RefundVO> getDataForReportList(RefundQuery query);
	
	void addProcess(@Param("process")String process,@Param("retNo")String retNo);
	
	void addProcessById(@Param("process")String process,@Param("refundId")String refundId);
	
	void deleteProcess(String retNo);
	
	void deleteProcessById(String refundId);
	
	List<String> isHavePcocess(String retNo);
	
	List<String> isHavePcocessById(String refundId);
	
	void initializedPassengerRefund(@Param("orderNo")String orderNo,@Param("name")String name);
}
