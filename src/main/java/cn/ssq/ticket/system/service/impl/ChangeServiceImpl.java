package cn.ssq.ticket.system.service.impl;

import cn.ssq.ticket.system.entity.Change;
import cn.ssq.ticket.system.entity.OrderOperateLog;
import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.mapper.ChangeMapper;
import cn.ssq.ticket.system.mapper.PassengreMapper;
import cn.ssq.ticket.system.service.ChangeService;
import cn.ssq.ticket.system.service.LogService;
import cn.ssq.ticket.system.util.WebConstant;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ChangeServiceImpl implements ChangeService{

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ChangeMapper changeMapper;
	@Autowired
	private PassengreMapper passengerMapper;
	@Autowired
	private LogService logService;


	private static Object lock=new Object();
	
	/**
	 * 保存改签单
	 */
	@Override
	@Transactional
	public void saveChanges(List<Change> list) {
		for(Change change:list){
			synchronized (lock) {				
				if(isExit(change.getOrderNo(), change.getOrderSource(), change.getPassengerName(),change.getNewCOrderNo())) {
					continue;
				}
				change.setState(WebConstant.CHANGE_UNTREATED);
				Passenger p=passengerMapper.selectOne(new QueryWrapper<Passenger>().eq("name", change.getPassengerName().trim()).eq("order_no", change.getOrderNo().trim()));
				if(p!=null){
					change.setPassengerId(p.getPassengerId());
				}
				changeMapper.insert(change);
				this.changePassengerTicketStatus(change);
			}
		}
	}

	/**
	 * 手动添加改签单
	 */
	@Override
	public void saveChangesByShouG(List<Change> list) {
		if(list==null||list.size()<1){
			throw new RuntimeException();
		}
		String createName=ShiroKit.getLocalName();
		if(StringUtils.isEmpty(createName)){
			createName=changeMapper.isHavePcocess(list.get(0).getNewCOrderNo()).get(0);
		}
		for(Change change:list){
		    change.setNewCOrderNo(change.getNewCOrderNo().trim());
			if(isExit(change.getOrderNo(), change.getOrderSource(), change.getPassengerName(),change.getNewCOrderNo())) {
				//更新
				changeMapper.updateById(change);
				continue;
			}
			change.setCreateBy(createName);
			change.setProcessBy(createName);
			change.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			Passenger p=passengerMapper.selectOne(new QueryWrapper<Passenger>().eq("name", change.getPassengerName().trim()).eq("order_no", change.getOrderNo().trim()));
			if(p!=null){
				change.setPassengerId(p.getPassengerId());
			}
			changeMapper.insert(change);
			this.changePassengerTicketStatus(change);
		}

		OrderOperateLog log=new OrderOperateLog();
		log.setName(createName);
		log.setContent("点击了保存改签");
		log.setType("订单处理");
		log.setOrderNo(list.get(0).getOrderNo());
		logService.saveLog(log);

	}

	@Override
	public List<Change> selectByQuery(QueryWrapper<Change> query) {
		return changeMapper.selectList(query);
	}

	@Override
	public IPage<Change> selectChangeList(QueryWrapper<Change> query,
			Page<Change> page) {
		IPage<Change> selectPage = changeMapper.selectPage(page, query);
		return selectPage;
	}

	@Override
	public void updateById(Change change) {
		changeMapper.updateById(change);
		this.changePassengerTicketStatus(change);
	}

	@Override
	public void deleteChanges(String[] changeIds) {
		changeMapper.deleteChange(changeIds);
	}

	/**
	 * 变更乘客客票状态
	 * @param change
	 */
	private void changePassengerTicketStatus(Change change){
		if(StringUtils.isEmpty(change.getOrderNo())) {
			return;
		}
		try {
			//更新乘客票号状态
			Passenger p=new Passenger();
			p.setStatus(WebConstant.APPLY_CHANGE);
			switch (change.getState()) {
			case "1":
				p.setStatus(WebConstant.APPLY_CHANGE);
				break;
			case "2":
				p.setStatus(WebConstant.YET_CHANGE);
				break;
			case "3":
				p.setStatus(WebConstant.REFUCE_CHANGE);
				break;
			default:
				p.setStatus(WebConstant.APPLY_CHANGE);
				break;
			}
			passengerMapper.update(p, new UpdateWrapper<Passenger>().eq("name", change.getPassengerName().trim()).eq("order_no", change.getOrderNo().trim()));
		} catch (Exception e) {
			log.error("客票同步状态异常",e);
			e.printStackTrace();
		}

	}

	public synchronized void initializedPassengerChange(List<Change> list){
		for(Change change:list){
			if(isExit(change.getOrderNo(), change.getOrderSource(), change.getPassengerName(),change.getNewCOrderNo())) {
				continue;
			}
			changeMapper.insert(change);
			//修改乘客表状态
			changeMapper.initializedPassengerChange(change.getOrderNo().trim(), change.getPassengerName().trim());
			if(StringUtils.isNotEmpty(change.getRemark())){
				changeMapper.initializedPassengerChange(change.getRemark().trim(), change.getPassengerName().trim());
			}
		}
	}

	private boolean isExit(String orderNo,String orderSource,String name,String changeNo) {
		QueryWrapper<Change> query=new QueryWrapper<Change>();
		query.eq("order_no", orderNo.trim());
		query.eq("order_source", orderSource);
		/*	query.eq("Order_shop", orderShop);*/
		query.eq("passenger_Name", name.trim());
		query.eq("NEW_C_ORDER_NO", changeNo.trim());
		List<Change> selectList = changeMapper.selectList(query);
		if(selectList.size()>0){
			return true;
		}
		return false;
	}


	@Override
	public void addProcess(String process, String changeNo) {
		changeMapper.addProcess(process, changeNo);

	}

	@Override
	public void deleteProcess(String changeNo) {
		changeMapper.deleteProcess(changeNo);

	}

	@Override
	public String isHavePcocess(String changeNo) {
		List<String> havePcocess = changeMapper.isHavePcocess(changeNo);
		if(havePcocess.size()<1){
			return null;
		}
		return havePcocess.get(0);
	}

	@Override
	public void updateStatus(String state, String orderNo) {
		changeMapper.updateStatus(state, orderNo);

	}

	@Override
	public int updateByChangeNo(Change change) {
        int i = changeMapper.updateByChangeNo(change);
        return i;
    }
	
	@Override
	public void updateByticketNo(Change change) {
		changeMapper.updateByticketNo(change);
	}

	@Override
	public void updateByWrapper(Change change,UpdateWrapper<Change> updateWrapper) {
		changeMapper.update(change,updateWrapper);
	}


}
