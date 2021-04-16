package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenuidGetBymixnickResponse;

/**
 * TOP API: taobao.openuid.get.bymixnick request
 * 
 * @author top auto create
 * @since 1.0, 2019.10.21
 */
public class OpenuidGetBymixnickRequest extends BaseTaobaoRequest<OpenuidGetBymixnickResponse> {
	
	

	/** 
	* 无线类应用获取到的混淆的nick
	 */
	private String mixNick;

	public void setMixNick(String mixNick) {
		this.mixNick = mixNick;
	}

	public String getMixNick() {
		return this.mixNick;
	}

	public String getApiMethodName() {
		return "taobao.openuid.get.bymixnick";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("mix_nick", this.mixNick);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenuidGetBymixnickResponse> getResponseClass() {
		return OpenuidGetBymixnickResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(mixNick, "mixNick");
	}
	

}