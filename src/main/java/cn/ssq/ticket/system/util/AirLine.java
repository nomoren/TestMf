package cn.ssq.ticket.system.util;

import java.io.Serializable;

import cn.stylefeng.guns.core.common.annotion.ExcelField;

public class AirLine implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String depCode;
	
	private String arrCode;

	@ExcelField(title="出发城市", type = 0, align = 2, sort = 1)
	public String getDepCode() {
		return depCode;
	}

	public void setDepCode(String depCode) {
		this.depCode = depCode;
	}

	@ExcelField(title="到达城市", type = 0, align = 2, sort = 1)
	public String getArrCode() {
		return arrCode;
	}

	public void setArrCode(String arrCode) {
		this.arrCode = arrCode;
	}
	
	

}
