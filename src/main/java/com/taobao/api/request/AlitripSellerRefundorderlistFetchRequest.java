package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Date;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSellerRefundorderlistFetchResponse;

/**
 * TOP API: taobao.alitrip.seller.refundorderlist.fetch request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSellerRefundorderlistFetchRequest extends BaseTaobaoRequest<AlitripSellerRefundorderlistFetchResponse> {
	
	

	/** 
	* 提取数据的结束时间
	 */
	private Date endDate;

	/** 
	* 提取数据的开始时间
	 */
	private Date startDate;

	/** 
	* 1：初始，2：接受，3：确认，4：失败，5：买家处理，6：卖家处理，7：等待小二回填，8：退款成功
	 */
	private Long status;

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getStatus() {
		return this.status;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.seller.refundorderlist.fetch";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("end_date", this.endDate);
		txtParams.put("start_date", this.startDate);
		txtParams.put("status", this.status);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerRefundorderlistFetchResponse> getResponseClass() {
		return AlitripSellerRefundorderlistFetchResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(endDate, "endDate");
		RequestCheckUtils.checkNotEmpty(startDate, "startDate");
	}
	

}