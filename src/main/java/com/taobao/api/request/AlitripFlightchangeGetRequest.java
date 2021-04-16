package com.taobao.api.request;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.AlitripFlightchangeGetResponse;

/**
 * TOP API: taobao.alitrip.flightchange.get request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripFlightchangeGetRequest extends BaseTaobaoRequest<AlitripFlightchangeGetResponse> {
	
	

	/** 
	* 查询信息封装
	 */
	private String searchOption;

	public void setSearchOption(String searchOption) {
		this.searchOption = searchOption;
	}

	public void setSearchOption(FlightChangeDataQueryOption searchOption) {
		this.searchOption = new JSONWriter(false,true).write(searchOption);
	}

	public String getSearchOption() {
		return this.searchOption;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.flightchange.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("search_option", this.searchOption);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripFlightchangeGetResponse> getResponseClass() {
		return AlitripFlightchangeGetResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	
	/**
 * 查询信息封装
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class FlightChangeDataQueryOption extends TaobaoObject {

	private static final long serialVersionUID = 4461983215665714786L;

	/**
		 * 航变信息产生时间开始,格式yyyy-MM-dd
		 */
		@ApiField("begin_flight_change_time_str")
		private String beginFlightChangeTimeStr;
		/**
		 * 原航变旅行日期开始,格式yyyy-MM-dd
		 */
		@ApiField("begin_old_dep_time_str")
		private String beginOldDepTimeStr;
		/**
		 * 第几页
		 */
		@ApiField("current_page")
		private Long currentPage;
		/**
		 * 航变信息产生时间截至,格式yyyy-MM-dd
		 */
		@ApiField("end_flight_change_time_str")
		private String endFlightChangeTimeStr;
		/**
		 * 原航变旅行日期截至,格式yyyy-MM-dd
		 */
		@ApiField("end_old_dep_time_str")
		private String endOldDepTimeStr;
		/**
		 * 是否只查已确认的航变,1:是 2:否
		 */
		@ApiField("is_confirmed")
		private Long isConfirmed;
		/**
		 * 是否只查自己订单航变,1:是 2:否
		 */
		@ApiField("is_get_self_only")
		private Long isGetSelfOnly;
		/**
		 * 原到达机场三字代码
		 */
		@ApiField("old_arr_airport")
		private String oldArrAirport;
		/**
		 * 原出发机场三字代码
		 */
		@ApiField("old_dep_airport")
		private String oldDepAirport;
		/**
		 * 原航班号
		 */
		@ApiField("old_flt_num")
		private String oldFltNum;
		/**
		 * 排序,1:航变时间降序（默认） 2:航变时间升序 3:离港时间降序 4:离港时间升序
		 */
		@ApiField("qsort")
		private Long qsort;
	

	public String getBeginFlightChangeTimeStr() {
			return this.beginFlightChangeTimeStr;
		}
		public void setBeginFlightChangeTimeStr(String beginFlightChangeTimeStr) {
			this.beginFlightChangeTimeStr = beginFlightChangeTimeStr;
		}
		public String getBeginOldDepTimeStr() {
			return this.beginOldDepTimeStr;
		}
		public void setBeginOldDepTimeStr(String beginOldDepTimeStr) {
			this.beginOldDepTimeStr = beginOldDepTimeStr;
		}
		public Long getCurrentPage() {
			return this.currentPage;
		}
		public void setCurrentPage(Long currentPage) {
			this.currentPage = currentPage;
		}
		public String getEndFlightChangeTimeStr() {
			return this.endFlightChangeTimeStr;
		}
		public void setEndFlightChangeTimeStr(String endFlightChangeTimeStr) {
			this.endFlightChangeTimeStr = endFlightChangeTimeStr;
		}
		public String getEndOldDepTimeStr() {
			return this.endOldDepTimeStr;
		}
		public void setEndOldDepTimeStr(String endOldDepTimeStr) {
			this.endOldDepTimeStr = endOldDepTimeStr;
		}
		public Long getIsConfirmed() {
			return this.isConfirmed;
		}
		public void setIsConfirmed(Long isConfirmed) {
			this.isConfirmed = isConfirmed;
		}
		public Long getIsGetSelfOnly() {
			return this.isGetSelfOnly;
		}
		public void setIsGetSelfOnly(Long isGetSelfOnly) {
			this.isGetSelfOnly = isGetSelfOnly;
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
		public String getOldFltNum() {
			return this.oldFltNum;
		}
		public void setOldFltNum(String oldFltNum) {
			this.oldFltNum = oldFltNum;
		}
		public Long getQsort() {
			return this.qsort;
		}
		public void setQsort(Long qsort) {
			this.qsort = qsort;
		}

}


}