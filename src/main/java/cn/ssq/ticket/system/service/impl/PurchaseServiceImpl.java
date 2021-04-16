package cn.ssq.ticket.system.service.impl;

import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.mapper.PurchaseMapper;
import cn.ssq.ticket.system.service.PurchaseService;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.Util;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PurchaseServiceImpl implements PurchaseService{

	@Autowired
	private PurchaseMapper purchaseMapper;
		
	@Override
	public void insertPurchase(Purchase purchase) {
		purchaseMapper.insert(purchase);
	}

	/**
	 * 获取采购单中乘机人姓名
	 */
	@Override
	public List<List<String>> getPassengerNames(String orderNo) {
		List<String> nameList=purchaseMapper.getPassengerNames(orderNo);
		if(nameList==null||nameList.size()<1){
			return null;
		}
		List<List<String>> lists=new ArrayList<List<String>>();
		for(String str:nameList){
			String[] names=str.split(",");
			List<String> list=new ArrayList<String>();
			for(String name:names){
				list.add(name.trim());
			}
			lists.add(list);
		}
		return lists;
	}

	@Override
	public List<Purchase> getPurchData(String startDate, String endDate,
			String orderSorce, String orderShop) {
		if("0".equals(orderShop)){
			orderShop=null;
		}
		if(!"0".equals(orderShop)){
			if(!Util.orderSorceList.contains(orderSorce)){
				orderShop=null;
			}
		}
		if("0".equals(orderSorce)){
			orderSorce=null;
			orderShop=null;
		}
		return purchaseMapper.getPurchData(startDate, endDate, orderSorce, orderShop);
	}

	@Override
	public IPage<Purchase> getList(QueryWrapper<Purchase> query, Long page, Long limit) {
		
		Page<Purchase> p=new Page<Purchase>(page, limit);
		IPage<Purchase> selectPage = purchaseMapper.selectPage(p, query);
		return selectPage;
	}

	@Override
	public int savePurch(Purchase purchase) {
		int insert = purchaseMapper.insert(purchase);
		return insert;
	}

	@Override
	public List<Purchase> getPurchaseList(String orderSorce, String orderShop,
			String orderNo) {
		QueryWrapper<Purchase> query=new QueryWrapper<Purchase>();
		query.eq("order_no", orderNo);
		if(!"0".equals(orderSorce)){
			query.eq("order_source", orderSorce);
			if(!"0".equals(orderShop)){
				query.eq("order_shop", orderShop);
			}
		}
		query.ne("flag", "1");
		List<Purchase> purchList = purchaseMapper.selectList(query);
		if(purchList.size()>0){
			for(Purchase p:purchList){
				p.setPayWay(DictUtils.getDictName("order_recipt_way", p.getPayWay()));
				p.setSupplier(DictUtils.getDictName("order_purch_place", p.getSupplier()));
				p.setType(DictUtils.getDictName("purchType", p.getType()));
			}
		}
		return purchList;
	}

	@Override
	public void updatePurchaseFlag(Long purchaseId) {
		Purchase purchase=new Purchase();
		purchase.setPurchaseId(purchaseId);
		purchase.setFlag("1");//作废
		purchaseMapper.updateById(purchase);
	}

	@Override
	public void updatePurchase(Purchase purchase) {
		purchaseMapper.updateById(purchase);
	}

	/**
	 * 根据流水号查询采购单
	 */
	@Override
	public Purchase getPurchsByTradeNo(String tradeNo, String startDate, String endDate) {


		QueryWrapper<Purchase> query=new QueryWrapper<Purchase>();
		query.eq("trade_no", tradeNo);
		query.eq("flag", 0);
		if(StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)) {
			query.between("print_ticket_date", startDate, endDate);
		}
		Purchase selectOne = purchaseMapper.selectOne(query);
		if(selectOne!=null) {
			Purchase purchase=new Purchase();
			purchase.setPurchaseId(selectOne.getPurchaseId());
			purchase.setPayAmount(selectOne.getPayAmount());
			purchase.setCustomerAmount(selectOne.getCustomerAmount());
			purchase.setOrderId(selectOne.getOrderId());
			purchase.setPassengerNames(selectOne.getPassengerNames());
			return purchase;
		}
		return null;
	}

	@Override
	public List<Purchase> getByQueryWapper(QueryWrapper<Purchase> query) {
		return purchaseMapper.selectList(query);
	}

	@Override
	public int getPurchCountByOrderNo(String orderNo) {
		return purchaseMapper.getPurchCountByOrderNo(orderNo);
	}

	@Override
	public List<String> getB2BtciketCountByRemark(String startDate,
			String endDate, String remark) {
		return purchaseMapper.getB2BtciketCountByRemark(startDate, endDate, remark);
	}

	@Override
	public List<Purchase> getPurchDataByState(String startDate, String endDate, String state) {
		return purchaseMapper.getPurchDataByState(startDate, endDate, state);
	}

	@Override
	public List<Purchase> getByLambdaQueryWrapper(LambdaQueryWrapper<Purchase> wrapper) {
		return purchaseMapper.selectList(wrapper);
	}

	@Override
	public int updatePurchase(Purchase p, UpdateWrapper<Purchase> wrapper) {
		return purchaseMapper.update(p,wrapper);
	}

	@Override
	public Purchase getById(Long purchaseId) {
		return purchaseMapper.selectById(purchaseId);
	}
}
