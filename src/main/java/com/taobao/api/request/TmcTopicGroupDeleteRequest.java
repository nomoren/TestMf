package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TmcTopicGroupDeleteResponse;

/**
 * TOP API: taobao.tmc.topic.group.delete request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class TmcTopicGroupDeleteRequest extends BaseTaobaoRequest<TmcTopicGroupDeleteResponse> {
	
	

	/** 
	* 消息分组Id，一般不用填写，如果分组已经被删除，则根据问题排查工具返回的ID删除路由关系
	 */
	private Long groupId;

	/** 
	* 消息分组名
	 */
	private String groupName;

	/** 
	* 消息topic名称，多个以逗号(,)分割
	 */
	private String topics;

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getGroupId() {
		return this.groupId;
	}

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
		return "taobao.tmc.topic.group.delete";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("group_id", this.groupId);
		txtParams.put("group_name", this.groupName);
		txtParams.put("topics", this.topics);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TmcTopicGroupDeleteResponse> getResponseClass() {
		return TmcTopicGroupDeleteResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(groupName, "groupName");
		RequestCheckUtils.checkNotEmpty(topics, "topics");
		RequestCheckUtils.checkMaxListSize(topics, 20, "topics");
	}
	

}