package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.refusereturn response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerRefundRefusereturnResponse extends TaobaoResponse {

	private static final long serialVersionUID = 2244558196421345513L;

	/** 
	 * 返回结果
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
 * 返回结果
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class ResultDo extends TaobaoObject {

	private static final long serialVersionUID = 7381895521612687812L;

	/**
		 * 系统自动生成
		 */
		@ApiField("errorCode")
		private String errorCode;
		/**
		 * 系统自动生成
		 */
		@ApiField("errorMsg")
		private String errorMsg;
		/**
		 * 处理结果
		 */
		@ApiField("result")
		private Boolean result;
		/**
		 * 调用是否成功
		 */
		@ApiField("success")
		private Boolean success;
	

	public String getErrorCode() {
			return this.errorCode;
		}
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		public String getErrorMsg() {
			return this.errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public Boolean getResult() {
			return this.result;
		}
		public void setResult(Boolean result) {
			this.result = result;
		}
		public Boolean getSuccess() {
			return this.success;
		}
		public void setSuccess(Boolean success) {
			this.success = success;
		}

}



}
