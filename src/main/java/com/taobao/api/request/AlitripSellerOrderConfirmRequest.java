package com.taobao.api.request;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.AlitripSellerOrderConfirmResponse;

/**
 * TOP API: taobao.alitrip.seller.order.confirm request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSellerOrderConfirmRequest extends BaseTaobaoRequest<AlitripSellerOrderConfirmResponse> {
	
	

	/** 
	* 请求参数
	 */
	private String tripConfirmOrderParam;

	public void setTripConfirmOrderParam(String tripConfirmOrderParam) {
		this.tripConfirmOrderParam = tripConfirmOrderParam;
	}

	public void setTripConfirmOrderParam(TripConfirmOrderParam tripConfirmOrderParam) {
		this.tripConfirmOrderParam = new JSONWriter(false,true).write(tripConfirmOrderParam);
	}

	public String getTripConfirmOrderParam() {
		return this.tripConfirmOrderParam;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.seller.order.confirm";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("trip_confirm_order_param", this.tripConfirmOrderParam);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerOrderConfirmResponse> getResponseClass() {
		return AlitripSellerOrderConfirmResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	
	/**
 * 请求参数
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class TripConfirmOrderParam extends TaobaoObject {

	private static final long serialVersionUID = 1739941797764452436L;

	/**
		 * 确认项：官网投放卖家确认订单参数为orderConfirmHk
		 */
		@ApiField("confirm")
		private String confirm;
		/**
		 * 对于此订单的描述。
		 */
		@ApiField("info")
		private String info;
		/**
		 * 订单号
		 */
		@ApiField("order_id")
		private Long orderId;
	

	public String getConfirm() {
			return this.confirm;
		}
		public void setConfirm(String confirm) {
			this.confirm = confirm;
		}
		public String getInfo() {
			return this.info;
		}
		public void setInfo(String info) {
			this.info = info;
		}
		public Long getOrderId() {
			return this.orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}

}


}