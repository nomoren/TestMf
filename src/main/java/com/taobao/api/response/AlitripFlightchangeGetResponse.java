package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;
import com.taobao.api.TaobaoObject;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.flightchange.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripFlightchangeGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 2896554325567588672L;

	/** 
	 * result
	 */
	@ApiField("result_d_o")
	private ResultDo resultDO;


	public void setResultDO(ResultDo resultDO) {
		this.resultDO = resultDO;
	}
	public ResultDo getResultDO( ) {
		return this.resultDO;
	}
	
	/**
 * result
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class ResultDo extends TaobaoObject {

	private static final long serialVersionUID = 5631789774125515878L;

	/**
		 * errCode
		 */
		@ApiField("err_code")
		private String errCode;
		/**
		 * errMsg
		 */
		@ApiField("err_msg")
		private String errMsg;
		/**
		 * results
		 */
		@ApiListField("results")
		@ApiField("json")
		private List<String> results;
		/**
		 * success
		 */
		@ApiField("success")
		private Boolean success;
	

	public String getErrCode() {
			return this.errCode;
		}
		public void setErrCode(String errCode) {
			this.errCode = errCode;
		}
		public String getErrMsg() {
			return this.errMsg;
		}
		public void setErrMsg(String errMsg) {
			this.errMsg = errMsg;
		}
		public List<String> getResults() {
			return this.results;
		}
		public void setResults(List<String> results) {
			this.results = results;
		}
		public Boolean getSuccess() {
			return this.success;
		}
		public void setSuccess(Boolean success) {
			this.success = success;
		}

}



}
