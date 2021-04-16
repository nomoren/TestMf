package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;
import com.taobao.api.TaobaoObject;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.order.confirm response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerOrderConfirmResponse extends TaobaoResponse {

	private static final long serialVersionUID = 3319188511791337826L;

	/** 
	 * 返回对象
	 */
	@ApiField("result")
	private TripOrderConfirmResultVo result;


	public void setResult(TripOrderConfirmResultVo result) {
		this.result = result;
	}
	public TripOrderConfirmResultVo getResult( ) {
		return this.result;
	}
	
	/**
 * 返回对象
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class TripOrderConfirmResultVo extends TaobaoObject {

	private static final long serialVersionUID = 8681925581128271616L;

	/**
		 * 错误码
		 */
		@ApiField("error_code")
		private String errorCode;
		/**
		 * 错误描述
		 */
		@ApiField("error_msg")
		private String errorMsg;
		/**
		 * 订单是否成功
		 */
		@ApiField("is_order_success")
		private Boolean isOrderSuccess;
		/**
		 * 返回结果，此接口无值
		 */
		@ApiListField("results")
		@ApiField("json")
		private List<String> results;
	

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
		public Boolean getIsOrderSuccess() {
			return this.isOrderSuccess;
		}
		public void setIsOrderSuccess(Boolean isOrderSuccess) {
			this.isOrderSuccess = isOrderSuccess;
		}
		public List<String> getResults() {
			return this.results;
		}
		public void setResults(List<String> results) {
			this.results = results;
		}

}



}
