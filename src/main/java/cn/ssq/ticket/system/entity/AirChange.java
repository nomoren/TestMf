package cn.ssq.ticket.system.entity;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class AirChange implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 航变类型 1 变更 2 取消,有保护航班 3 取消,无保护航班
	 */
	private String typeKey;
	/**
	 * 其实这是原航班号 必须由两位英文字母+3/4 位数据组成
	 */
	private String flightNum;
	/**
	 * 变更后 航班日期即起飞日期 格式 yyyy-MM-dd
	 */
	private String flightDate="";
	/**
	 * 变更后 起飞时间 格式：aabb/aa:bb/aa；bb
	 */
	private String dptTime="";
	/**
	 * 变更后 到达时间 格式:aabb/aa:bb/aa：bb/空
	 */
	private String arrTime="";
	/***
	 * 保护航班号，只有 typeKey=2 的时候传，其他情况设置为空指针 null
	 */
	private String flightNumProtect="";
	/***
	 * 变更后 起飞机场
	 */
	private String depAirport="";
	/***
	 * 变更后 到达机场
	 */
	private String arrAirport="";
	/**
	 * 变更后到达日期
	 */
	private String arrDate="";
	/**
	 * 座位确认字段，该字段不需要填写，设置为空指针 null 即可
	 */
	private String ensureKey="";
	/**
	 * 变更前 起飞时间 格式：aabb/aa:bb
	 */
	private String preDptTime;
	/**
	 * 变更前 到达时间 格式：aabb/aa:bb
	 */
	private String preArrTime;
	/**
	 * 变更前 起飞日期 格式：yyyy-MM-dd
	 */
	private String preDptDate;
	/**
	 * 变更前 起飞机场三字码
	 */
	private String preDptAirPort;
	/**
	 * 变更前 到达机场三字码
	 */
	private String preArrAirPort;


	public String getTypeKey() {
		return typeKey;
	}
	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}
	public String getFlightNum() {
		return flightNum;
	}
	public void setFlightNum(String flightNum) {
		if (StringUtils.isNotEmpty(flightNum)) {
			this.flightNum = flightNum.toUpperCase();
		}
	}
	public String getFlightDate() {
		return flightDate;
	}
	public void setFlightDate(String flightDate) {
		this.flightDate = flightDate;
	}
	public String getDptTime() {
		return dptTime;
	}
	public void setDptTime(String dptTime) {
		this.dptTime = dptTime;
	}
	public String getArrTime() {
		return arrTime;
	}
	public void setArrTime(String arrTime) {
		this.arrTime = arrTime;
	}
	public String getFlightNumProtect() {
		return flightNumProtect;
	}
	public void setFlightNumProtect(String flightNumProtect) {
		this.flightNumProtect = flightNumProtect;
	}
	public String getArrAirport() {
		return arrAirport;
	}
	public void setArrAirport(String arrAirport) {
		this.arrAirport = arrAirport;
	}
	public String getDepAirport() {
		return depAirport;
	}
	public void setDepAirport(String depAirport) {
		this.depAirport = depAirport;
	}
	public String getArrDate() {
		return arrDate;
	}
	public void setArrDate(String arrDate) {
		this.arrDate = arrDate;
	}
	public String getEnsureKey() {
		return ensureKey;
	}
	public void setEnsureKey(String ensureKey) {
		this.ensureKey = ensureKey;
	}
	public String getPreDptTime() {
		return preDptTime;
	}
	public void setPreDptTime(String preDptTime) {
		this.preDptTime = preDptTime;
	}
	public String getPreArrTime() {
		return preArrTime;
	}
	public void setPreArrTime(String preArrTime) {
		this.preArrTime = preArrTime;
	}
	public String getPreDptDate() {
		return preDptDate;
	}
	public void setPreDptDate(String preDptDate) {
		this.preDptDate = preDptDate;
	}
	public String getPreDptAirPort() {
		return preDptAirPort;
	}
	public void setPreDptAirPort(String preDptAirPort) {
		this.preDptAirPort = preDptAirPort;
	}
	public String getPreArrAirPort() {
		return preArrAirPort;
	}
	public void setPreArrAirPort(String preArrAirPort) {
		this.preArrAirPort = preArrAirPort;
	}
	@Override
	public String toString() {
		return "AirChange{" +
				"typeKey='" + typeKey + '\'' +
				", flightNum='" + flightNum + '\'' +
				", flightDate='" + flightDate + '\'' +
				", dptTime='" + dptTime + '\'' +
				", arrTime='" + arrTime + '\'' +
				", flightNumProtect='" + flightNumProtect + '\'' +
				", depAirport='" + depAirport + '\'' +
				", arrAirport='" + arrAirport + '\'' +
				", arrDate='" + arrDate + '\'' +
				", ensureKey='" + ensureKey + '\'' +
				", preDptTime='" + preDptTime + '\'' +
				", preArrTime='" + preArrTime + '\'' +
				", preDptDate='" + preDptDate + '\'' +
				", preDptAirPort='" + preDptAirPort + '\'' +
				", preArrAirPort='" + preArrAirPort + '\'' +
				'}';
	}
}
