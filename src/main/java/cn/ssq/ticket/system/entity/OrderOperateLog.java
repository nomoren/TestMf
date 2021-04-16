package cn.ssq.ticket.system.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单操作日志
 * @author Administrator
 *
 */
public class OrderOperateLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;//操作人
	
	private String type;//类型
	
	private String content;//内容
	
	private String orderNo;//订单号
	
	private Date date;//操作时间
	
	private String retNo;//退票单号

	private String changeNo;//改签单号
	
	
	
	public String getChangeNo() {
		return changeNo;
	}

	public void setChangeNo(String changeNo) {
		this.changeNo = changeNo;
	}

	public String getRetNo() {
		return retNo;
	}

	public void setRetNo(String retNo) {
		this.retNo = retNo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		return "OrderOperateLog [name=" + name + ", type=" + type
				+ ", content=" + content + ", orderNo=" + orderNo + ", date="
				+ date + ", retNo=" + retNo + ", changeNo=" + changeNo + "]";
	}
	
	
	
}
