package cn.ssq.ticket.system.service;

import cn.ssq.ticket.system.entity.Purchase;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;


public interface PurchaseService {
	
	void insertPurchase(Purchase purchase);
	
	List<List<String>> getPassengerNames(String orderNo);
	
	List<Purchase> getPurchData(String startDate,String endDate,String orderSorce,String orderShop);
	
	IPage<Purchase> getList(QueryWrapper<Purchase> query,Long page,Long limit);
	
	int savePurch(Purchase purchase);
	
	List<Purchase> getPurchaseList(String orderSorce,String orderShop,String orderNo);
	
	void updatePurchaseFlag(Long purchaseId);
	
	void updatePurchase(Purchase purchase);
	
	Purchase getPurchsByTradeNo(String tradeNo,String startDate,String endDate);
	
	List<Purchase> getByQueryWapper(QueryWrapper<Purchase> query);
	
	int getPurchCountByOrderNo(String orderNo);
	
	List<String> getB2BtciketCountByRemark(String startDate,String endDate,String remark);
	
	List<Purchase> getPurchDataByState(String startDate,String endDate,String state);
	
	List<Purchase> getByLambdaQueryWrapper(LambdaQueryWrapper<Purchase> wrapper);

	int updatePurchase(Purchase p, UpdateWrapper<Purchase> wrapper);

	Purchase getById(Long purchaseId);
}
