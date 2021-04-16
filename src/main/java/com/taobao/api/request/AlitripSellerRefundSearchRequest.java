package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Date;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSellerRefundSearchResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.search request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSellerRefundSearchRequest extends BaseTaobaoRequest<AlitripSellerRefundSearchResponse> {
	
	

	/** 
	* 结束时间
	 */
	private Date endTime;

	/** 
	* 开始时间
	 */
	private Date startTime;

	/** 
	* 申请单状态（如果为空查询所有状态，1初始 2接受 3确认 4失败 5买家处理 6卖家处理 7等待小二回填 8退款成功）
	 */
	private Long status;

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getStatus() {
		return this.status;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.seller.refund.search";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("end_time", this.endTime);
		txtParams.put("start_time", this.startTime);
		txtParams.put("status", this.status);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerRefundSearchResponse> getResponseClass() {
		return AlitripSellerRefundSearchResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(endTime, "endTime");
		RequestCheckUtils.checkNotEmpty(startTime, "startTime");
	}
	

}