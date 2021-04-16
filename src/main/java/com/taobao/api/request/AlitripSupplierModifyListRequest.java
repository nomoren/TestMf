package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Date;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSupplierModifyListResponse;

/**
 * TOP API: taobao.alitrip.supplier.modify.list request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSupplierModifyListRequest extends BaseTaobaoRequest<AlitripSupplierModifyListResponse> {
	
	

	/** 
	* 页码
	 */
	private Long currentPage;

	/** 
	* 乘客出发时间查询结束日期
	 */
	private Date depEnd;

	/** 
	* 乘客出发时间查询开始日期
	 */
	private Date depStart;

	/** 
	* 申请单创建时间查询结束日期
	 */
	private Date gmtCreateEnd;

	/** 
	* 申请单创建时间查询开始日期
	 */
	private Date gmtCreateStart;

	/** 
	* 每页记录数
	 */
	private Long pageSize;

	/** 
	* 1：改签申请列表，2：改签已支付列表，3：改签成功列表
	 */
	private Long status;

	public void setCurrentPage(Long currentPage) {
		this.currentPage = currentPage;
	}

	public Long getCurrentPage() {
		return this.currentPage;
	}

	public void setDepEnd(Date depEnd) {
		this.depEnd = depEnd;
	}

	public Date getDepEnd() {
		return this.depEnd;
	}

	public void setDepStart(Date depStart) {
		this.depStart = depStart;
	}

	public Date getDepStart() {
		return this.depStart;
	}

	public void setGmtCreateEnd(Date gmtCreateEnd) {
		this.gmtCreateEnd = gmtCreateEnd;
	}

	public Date getGmtCreateEnd() {
		return this.gmtCreateEnd;
	}

	public void setGmtCreateStart(Date gmtCreateStart) {
		this.gmtCreateStart = gmtCreateStart;
	}

	public Date getGmtCreateStart() {
		return this.gmtCreateStart;
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	public Long getPageSize() {
		return this.pageSize;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getStatus() {
		return this.status;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.supplier.modify.list";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("current_page", this.currentPage);
		txtParams.put("dep_end", this.depEnd);
		txtParams.put("dep_start", this.depStart);
		txtParams.put("gmt_create_end", this.gmtCreateEnd);
		txtParams.put("gmt_create_start", this.gmtCreateStart);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("status", this.status);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSupplierModifyListResponse> getResponseClass() {
		return AlitripSupplierModifyListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(gmtCreateEnd, "gmtCreateEnd");
		RequestCheckUtils.checkNotEmpty(gmtCreateStart, "gmtCreateStart");
	}
	

}