package cn.ssq.ticket.system.entity.importBean.TTsBean;

/** 
 * 去哪儿订单操作日志VO
 */
public class QnrOrderLogVO {



	/**
	 * 操作人
	 */
	private String operator;

	/**
	 * 操作时间
	 */
	private String time;

	/**
	 * 行为
	 */
	private String action;

	/**
	 * 获取操作人<p>
	 * @return  operator  操作人<br>
	 */
	public String getOperator()
	{
		return operator;
	}

	/**
	 * 设置操作人<p>
	 * @param  operator  操作人<br>
	 */
	public void setOperator(String operator)
	{
		this.operator = operator;
	}

	/**
	 * 获取操作时间<p>
	 * @return  time  操作时间<br>
	 */
	public String getTime()
	{
		return time;
	}

	/**
	 * 设置操作时间<p>
	 * @param  time  操作时间<br>
	 */
	public void setTime(String time)
	{
		this.time = time;
	}

	/**
	 * 获取行为<p>
	 * @return  action  行为<br>
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * 设置行为<p>
	 * @param  action  行为<br>
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

}
