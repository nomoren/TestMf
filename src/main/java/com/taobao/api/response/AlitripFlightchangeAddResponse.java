package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.flightchange.add response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripFlightchangeAddResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4793517331423484386L;

	/** 
	 * result
	 */
	@ApiField("result")
	private ResultDo result;


	public void setResult(ResultDo result) {
		this.result = result;
	}
	public ResultDo getResult( ) {
		return this.result;
	}
	
	/**
 * result
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class ResultDo extends TaobaoObject {

	private static final long serialVersionUID = 5288483875571582493L;

	/**
		 * 错误码
		 */
		@ApiField("err_code")
		private String errCode;
		/**
		 * 错误信息
		 */
		@ApiField("err_msg")
		private String errMsg;
		/**
		 * 是否成功
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
		public Boolean getSuccess() {
			return this.success;
		}
		public void setSuccess(Boolean success) {
			this.success = success;
		}

}



}
