package cn.ssq.ticket.system.service;

import cn.ssq.ticket.system.entity.Change;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface ChangeService{

	
	void saveChanges(List<Change> list);
	 
	List<Change> selectByQuery(QueryWrapper<Change> query);
	
	IPage<Change> selectChangeList(QueryWrapper<Change> query,Page<Change> page);
	
	void updateById(Change change);
	
	void deleteChanges(String[] changeIds);
	
	void initializedPassengerChange(List<Change> list);
	
	void addProcess(String process,String changeNo);
	
	void deleteProcess(String changeNo);
	
	
	void updateStatus(String state,String orderNo);
	
	String isHavePcocess(String changeNo);
	
	void saveChangesByShouG(List<Change> list);

	// 修改
	int updateByChangeNo(Change change);

	void updateByticketNo(Change change);

	void updateByWrapper(Change change, UpdateWrapper<Change> updateWrapper);


}

