package com.taobao.api.request;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.AlitripBuyerGetResponse;

/**
 * TOP API: taobao.alitrip.buyer.get request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripBuyerGetRequest extends BaseTaobaoRequest<AlitripBuyerGetResponse> {
	
	

	/** 
	* 敏感信息查询请求参数
	 */
	private String requestAxb;

	public void setRequestAxb(String requestAxb) {
		this.requestAxb = requestAxb;
	}

	public void setRequestAxb(RequestAxbDo requestAxb) {
		this.requestAxb = new JSONWriter(false,true).write(requestAxb);
	}

	public String getRequestAxb() {
		return this.requestAxb;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.buyer.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("request_axb", this.requestAxb);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripBuyerGetResponse> getResponseClass() {
		return AlitripBuyerGetResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	
	/**
 * 敏感信息查询请求参数
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class RequestAxbDo extends TaobaoObject {

	private static final long serialVersionUID = 2651387669467587288L;

	/**
		 * 业务类型：0国内机票,1国际机票
		 */
		@ApiField("biz_type")
		private Long bizType;
		/**
		 * 商家联系号码,多个号码以“,”分割；传证件号时为乘机人姓名
		 */
		@ApiField("contact_no")
		private String contactNo;
		/**
		 * 订单号
		 */
		@ApiField("order_id")
		private Long orderId;
		/**
		 * 用途
		 */
		@ApiField("purpose")
		private String purpose;
		/**
		 * 请求内容：0手机号
		 */
		@ApiField("req_content")
		private Long reqContent;
	

	public Long getBizType() {
			return this.bizType;
		}
		public void setBizType(Long bizType) {
			this.bizType = bizType;
		}
		public String getContactNo() {
			return this.contactNo;
		}
		public void setContactNo(String contactNo) {
			this.contactNo = contactNo;
		}
		public Long getOrderId() {
			return this.orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		public String getPurpose() {
			return this.purpose;
		}
		public void setPurpose(String purpose) {
			this.purpose = purpose;
		}
		public Long getReqContent() {
			return this.reqContent;
		}
		public void setReqContent(Long reqContent) {
			this.reqContent = reqContent;
		}

}


}