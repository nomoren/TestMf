package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenuidChangeResponse;

/**
 * TOP API: taobao.openuid.change request
 * 
 * @author top auto create
 * @since 1.0, 2018.10.26
 */
public class OpenuidChangeRequest extends BaseTaobaoRequest<OpenuidChangeResponse> {
	
	

	/** 
	* openUid
	 */
	private String openUid;

	/** 
	* 转换到的appkey
	 */
	private String targetAppKey;

	public void setOpenUid(String openUid) {
		this.openUid = openUid;
	}

	public String getOpenUid() {
		return this.openUid;
	}

	public void setTargetAppKey(String targetAppKey) {
		this.targetAppKey = targetAppKey;
	}

	public String getTargetAppKey() {
		return this.targetAppKey;
	}

	public String getApiMethodName() {
		return "taobao.openuid.change";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("open_uid", this.openUid);
		txtParams.put("target_app_key", this.targetAppKey);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenuidChangeResponse> getResponseClass() {
		return OpenuidChangeResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(openUid, "openUid");
		RequestCheckUtils.checkNotEmpty(targetAppKey, "targetAppKey");
	}
	

}