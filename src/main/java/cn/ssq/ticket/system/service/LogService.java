package cn.ssq.ticket.system.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import cn.ssq.ticket.system.entity.OrderOperateLog;


@Service
public class LogService {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired 
	private OrderService orderService;
	
	public void saveLog(OrderOperateLog log){
		try {
			HandelLog handelLog=new HandelLog(log);
			Thread thread=new Thread(handelLog);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<OrderOperateLog> getLogByOrderNo(String orderNo,long page,int limit){
		 Query query=new Query();
		 query.addCriteria(Criteria.where("orderNo").is(orderNo));
		 query.with(new Sort(Sort.Direction.DESC, "date"));
		 query.skip((page-1)*limit);
		 query.limit(limit);
		 List<OrderOperateLog> data = mongoTemplate.find(query, OrderOperateLog.class);
		 return data;
	}
	
	public List<OrderOperateLog> getLogByRetNo(String retNo,long page,int limit){
		 Query query=new Query();
		 query.addCriteria(Criteria.where("retNo").is(retNo));
		 query.with(new Sort(Sort.Direction.DESC, "date"));
		 query.skip((page-1)*limit);
		 query.limit(limit);
		 List<OrderOperateLog> data = mongoTemplate.find(query, OrderOperateLog.class);
		 return data;
	}
	
	
	class HandelLog implements Runnable{
		private OrderOperateLog log;
		public HandelLog(OrderOperateLog log) {
			this.log=log;
		}
		@Override
		public void run() {
			if("订单详情".equals(log.getType())){
				String username=log.getName();
				Query query=new Query();
				query.addCriteria(Criteria.where("orderNo").is(log.getOrderNo()));
				query.with(new Sort(Sort.Direction.DESC, "date"));
				OrderOperateLog orderLog = mongoTemplate.findOne(query, OrderOperateLog.class);
				if(orderLog!=null){
					if(username.equals(orderLog.getName())){
						return;
					}
				}
				String process=orderService.isHavePcocess(log.getOrderNo());
				if(username.equals(process)){
					if(orderLog!=null){
						if(orderLog.getOrderNo().equals(log.getOrderNo())){
							return;					
						}						
					}
				}
			}
			log.setDate(new Date());
			mongoTemplate.save(log);
		}
		
	}
	
}
