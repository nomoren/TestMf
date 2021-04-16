package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenuidGetBytradeResponse;

/**
 * TOP API: taobao.openuid.get.bytrade request
 * 
 * @author top auto create
 * @since 1.0, 2018.10.17
 */
public class OpenuidGetBytradeRequest extends BaseTaobaoRequest<OpenuidGetBytradeResponse> {
	
	

	/** 
	* 订单ID
	 */
	private Long tid;

	public void setTid(Long tid) {
		this.tid = tid;
	}

	public Long getTid() {
		return this.tid;
	}

	public String getApiMethodName() {
		return "taobao.openuid.get.bytrade";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("tid", this.tid);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenuidGetBytradeResponse> getResponseClass() {
		return OpenuidGetBytradeResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(tid, "tid");
	}
	

}