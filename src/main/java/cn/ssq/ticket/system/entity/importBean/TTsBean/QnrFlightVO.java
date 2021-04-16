package cn.ssq.ticket.system.entity.importBean.TTsBean;

/** 
 * 去哪儿航段VO
 */
public class QnrFlightVO{
	
	/**
	 * 航班号
	 */
	private String code;
	
	/**
	 * 舱位
	 */
	private String cabin;
	
	/**
	 * 儿童舱位
	 */
	private String ccabin;
	
	/**
	 * 出发机场
	 */
	private String dep;
	
	/**
	 * 到达机场
	 */
	private String arr;
	
	/**
	 * 出发日期
	 */
	private String depDay;
	
	/**
	 * 出发时间
	 */
	private String depTime;
	
	/**
	 * 到达时间
	 */
	private String arrTime;
	
	/**
	 * 真实航班号,若不为空则是共享航班
	 */
	private String realCode;


	/**
	 * 获取航班号<p>
	 * @return  code  航班号<br>
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * 设置航班号<p>
	 * @param  code  航班号<br>
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

	/**
	 * 获取舱位<p>
	 * @return  cabin  舱位<br>
	 */
	public String getCabin()
	{
		return cabin;
	}

	/**
	 * 设置舱位<p>
	 * @param  cabin  舱位<br>
	 */
	public void setCabin(String cabin)
	{
		this.cabin = cabin;
	}

	/**
	 * 获取出发机场<p>
	 * @return  dep  出发机场<br>
	 */
	public String getDep()
	{
		return dep;
	}

	/**
	 * 设置出发机场<p>
	 * @param  dep  出发机场<br>
	 */
	public void setDep(String dep)
	{
		this.dep = dep;
	}

	/**
	 * 获取到达机场<p>
	 * @return  arr  到达机场<br>
	 */
	public String getArr()
	{
		return arr;
	}

	/**
	 * 设置到达机场<p>
	 * @param  arr  到达机场<br>
	 */
	public void setArr(String arr)
	{
		this.arr = arr;
	}

	/**
	 * 获取出发日期<p>
	 * @return  depDay  出发日期<br>
	 */
	public String getDepDay()
	{
		return depDay;
	}

	/**
	 * 设置出发日期<p>
	 * @param  depDay  出发日期<br>
	 */
	public void setDepDay(String depDay)
	{
		this.depDay = depDay;
	}

	/**
	 * 获取出发时间<p>
	 * @return  depTime  出发时间<br>
	 */
	public String getDepTime()
	{
		return depTime;
	}

	/**
	 * 设置出发时间<p>
	 * @param  depTime  出发时间<br>
	 */
	public void setDepTime(String depTime)
	{
		this.depTime = depTime;
	}

	public String getArrTime() {
		return arrTime;
	}

	public void setArrTime(String arrTime) {
		this.arrTime = arrTime;
	}

	/**
	 * 获取真实航班号若不为空则是共享航班<p>
	 * @return  realCode  真实航班号若不为空则是共享航班<br>
	 */
	public String getRealCode()
	{
		return realCode;
	}

	/**
	 * 设置真实航班号若不为空则是共享航班<p>
	 * @param  realCode  真实航班号若不为空则是共享航班<br>
	 */
	public void setRealCode(String realCode)
	{
		this.realCode = realCode;
	}

	public String getCcabin() {
		return ccabin;
	}

	public void setCcabin(String ccabin) {
		this.ccabin = ccabin;
	}

	@Override
	public String toString() {
		return "QnrFlightVO [code=" + code + ", cabin=" + cabin + ", ccabin="
				+ ccabin + ", dep=" + dep + ", arr=" + arr + ", depDay="
				+ depDay + ", depTime=" + depTime + ", arrTime=" + arrTime
				+ ", realCode=" + realCode + "]";
	}




}
