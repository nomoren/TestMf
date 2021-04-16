package cn.ssq.ticket.system.service;

import java.util.Map;

import cn.ssq.ticket.system.entity.Import;


public interface ImportServcie {

	
	void insert(Import im);
	
	Map<String, Object> getList(String type);
}
