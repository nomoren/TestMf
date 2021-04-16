package com.taobao.api.request;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.AlitripFlightchangeAddResponse;

/**
 * TOP API: taobao.alitrip.flightchange.add request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripFlightchangeAddRequest extends BaseTaobaoRequest<AlitripFlightchangeAddResponse> {
	
	

	/** 
	* 录入参数类
	 */
	private String flightChangeDataDo;

	public void setFlightChangeDataDo(String flightChangeDataDo) {
		this.flightChangeDataDo = flightChangeDataDo;
	}

	public void setFlightChangeDataDo(FlightChangeDataDO flightChangeDataDo) {
		this.flightChangeDataDo = new JSONWriter(false,true).write(flightChangeDataDo);
	}

	public String getFlightChangeDataDo() {
		return this.flightChangeDataDo;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.flightchange.add";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("flight_change_data_do", this.flightChangeDataDo);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripFlightchangeAddResponse> getResponseClass() {
		return AlitripFlightchangeAddResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	
	/**
 * 录入参数类
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class FlightChangeDataDO extends TaobaoObject {

	private static final long serialVersionUID = 8477225838498284571L;

	/**
		 * 业务类型,0:国内机票 1:国际机票
		 */
		@ApiField("biz_type")
		private Long bizType;
		/**
		 * 航变类型,1:航班取消 2:航班变更
		 */
		@ApiField("flight_change_type")
		private Long flightChangeType;
		/**
		 * 航班最新到达机场三字码, 字符长度3；仅当flightChangeType=2，该参数必填
		 */
		@ApiField("new_arr_airport")
		private String newArrAirport;
		/**
		 * 最新到达时间
		 */
		@ApiField("new_arr_time_str")
		private String newArrTimeStr;
		/**
		 * 航班最新出发机场三字码, 字符长度3；仅当flightChangeType=2，该参数必填
		 */
		@ApiField("new_dep_airport")
		private String newDepAirport;
		/**
		 * 航班最新计划起飞时间,仅当flightChangeType=2，该参数必填；录入格式:yyyy-MM-dd HH: mm
		 */
		@ApiField("new_dep_time_str")
		private String newDepTimeStr;
		/**
		 * 变更到的最新航班号,仅当flightChangeType=2,该参数必填
		 */
		@ApiField("new_flt_num")
		private String newFltNum;
		/**
		 * 原到达机场三字代码, 字符长度3
		 */
		@ApiField("old_arr_airport")
		private String oldArrAirport;
		/**
		 * 原出发机场三字代码, 字符长度3
		 */
		@ApiField("old_dep_airport")
		private String oldDepAirport;
		/**
		 * 原航变日期 ,格式:yyyy-MM-dd
		 */
		@ApiField("old_dep_time_str")
		private String oldDepTimeStr;
		/**
		 * 原航班号（如果是二次航变，该参数为第一航变后最新的航班号。eg:第一航变 航班号从CA123变更到CA456，那第二次航变的原航班应该为CA456）
		 */
		@ApiField("old_flt_num")
		private String oldFltNum;
		/**
		 * 飞猪机票订单号，如果输入了该参数，平台只会给该指定订单发送航变，如果不输入该参数，则会处理代理商的所有订单；正常的延误航变该参数一般不需要，如果是航班保护，大部分情况该参数应该都是必填的，因为航班保护基本每个订单保护的新航班可能都不一样。
		 */
		@ApiField("order_id")
		private Long orderId;
	

	public Long getBizType() {
			return this.bizType;
		}
		public void setBizType(Long bizType) {
			this.bizType = bizType;
		}
		public Long getFlightChangeType() {
			return this.flightChangeType;
		}
		public void setFlightChangeType(Long flightChangeType) {
			this.flightChangeType = flightChangeType;
		}
		public String getNewArrAirport() {
			return this.newArrAirport;
		}
		public void setNewArrAirport(String newArrAirport) {
			this.newArrAirport = newArrAirport;
		}
		public String getNewArrTimeStr() {
			return this.newArrTimeStr;
		}
		public void setNewArrTimeStr(String newArrTimeStr) {
			this.newArrTimeStr = newArrTimeStr;
		}
		public String getNewDepAirport() {
			return this.newDepAirport;
		}
		public void setNewDepAirport(String newDepAirport) {
			this.newDepAirport = newDepAirport;
		}
		public String getNewDepTimeStr() {
			return this.newDepTimeStr;
		}
		public void setNewDepTimeStr(String newDepTimeStr) {
			this.newDepTimeStr = newDepTimeStr;
		}
		public String getNewFltNum() {
			return this.newFltNum;
		}
		public void setNewFltNum(String newFltNum) {
			this.newFltNum = newFltNum;
		}
		public String getOldArrAirport() {
			return this.oldArrAirport;
		}
		public void setOldArrAirport(String oldArrAirport) {
			this.oldArrAirport = oldArrAirport;
		}
		public String getOldDepAirport() {
			return this.oldDepAirport;
		}
		public void setOldDepAirport(String oldDepAirport) {
			this.oldDepAirport = oldDepAirport;
		}
		public String getOldDepTimeStr() {
			return this.oldDepTimeStr;
		}
		public void setOldDepTimeStr(String oldDepTimeStr) {
			this.oldDepTimeStr = oldDepTimeStr;
		}
		public String getOldFltNum() {
			return this.oldFltNum;
		}
		public void setOldFltNum(String oldFltNum) {
			this.oldFltNum = oldFltNum;
		}
		public Long getOrderId() {
			return this.orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		@Override
		public String toString() {
			return "FlightChangeDataDO [bizType=" + bizType
					+ ", flightChangeType=" + flightChangeType
					+ ", newArrAirport=" + newArrAirport + ", newArrTimeStr="
					+ newArrTimeStr + ", newDepAirport=" + newDepAirport
					+ ", newDepTimeStr=" + newDepTimeStr + ", newFltNum="
					+ newFltNum + ", oldArrAirport=" + oldArrAirport
					+ ", oldDepAirport=" + oldDepAirport + ", oldDepTimeStr="
					+ oldDepTimeStr + ", oldFltNum=" + oldFltNum + ", orderId="
					+ orderId + "]";
		}

		
}


}