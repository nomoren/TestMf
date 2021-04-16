package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Date;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.FilesGetResponse;

/**
 * TOP API: taobao.files.get request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class FilesGetRequest extends BaseTaobaoRequest<FilesGetResponse> {
	
	

	/** 
	* 搜索结束时间
	 */
	private Date endDate;

	/** 
	* 搜索开始时间
	 */
	private Date startDate;

	/** 
	* 下载链接状态。1:未下载。2:已下载
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
		return "taobao.files.get";
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

	public Class<FilesGetResponse> getResponseClass() {
		return FilesGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(endDate, "endDate");
		RequestCheckUtils.checkNotEmpty(startDate, "startDate");
	}
	

}