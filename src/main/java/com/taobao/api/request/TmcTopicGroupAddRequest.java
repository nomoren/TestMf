package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TmcTopicGroupAddResponse;

/**
 * TOP API: taobao.tmc.topic.group.add request
 * 
 * @author top auto create
 * @since 1.0, 2018.08.13
 */
public class TmcTopicGroupAddRequest extends BaseTaobaoRequest<TmcTopicGroupAddResponse> {
	
	

	/** 
	* 消息分组名，如果不存在，会自动创建
	 */
	private String groupName;

	/** 
	* 消息topic名称，多个以逗号(,)分割
	 */
	private String topics;

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getTopics() {
		return this.topics;
	}

	public String getApiMethodName() {
		return "taobao.tmc.topic.group.add";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("group_name", this.groupName);
		txtParams.put("topics", this.topics);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TmcTopicGroupAddResponse> getResponseClass() {
		return TmcTopicGroupAddResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(groupName, "groupName");
		RequestCheckUtils.checkNotEmpty(topics, "topics");
		RequestCheckUtils.checkMaxListSize(topics, 20, "topics");
	}
	

}