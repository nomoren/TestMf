package cn.ssq.ticket.system.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.ssq.ticket.system.entity.Purchase;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface PurchaseMapper extends BaseMapper<Purchase>{

	
	List<String> getPassengerNames(String orderNo);
	
	List<Purchase> getPurchData(@Param("startDate")String startDate,@Param("endDate")String endDate,
								@Param("orderSorce")String orderSorce,@Param("orderShop")String orderShop);
	
	int getPurchCountByOrderNo(String orderNo);
	
	List<String> getB2BtciketCountByRemark(@Param("startDate")String startDate,@Param("endDate")String endDate,@Param("remark")String remark);
	

	List<Purchase> getPurchDataByState(@Param("startDate")String startDate,@Param("endDate")String endDate,@Param("state")String state);

}
