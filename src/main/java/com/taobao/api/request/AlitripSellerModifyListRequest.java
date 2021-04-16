package com.taobao.api.request;

import java.util.Date;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSellerModifyListResponse;

/**
 * TOP API: taobao.alitrip.seller.modify.list request
 * 
 * @author top auto create
 * @since 1.0, 2018.07.25
 */
public class AlitripSellerModifyListRequest extends BaseTaobaoRequest<AlitripSellerModifyListResponse> {
	
	

	/** 
	* 改签发起时间的查询结束日期 和 更新时间必选其一
	 */
	private Date applyDateEnd;

	/** 
	* 改签发起时间的查询开始日期 和 更新时间必选其一
	 */
	private Date applyDateStart;

	/** 
	* 申请单ID
	 */
	private Long applyId;

	/** 
	* 页码
	 */
	private Long currentPage;

	/** 
	* 乘客起飞时间的查询结束日期
	 */
	private Date flyDateEnd;

	/** 
	* 乘客起飞时间的查询开始日期
	 */
	private Date flyDateStart;

	/** 
	* 记录修改结束时间  和 改签发起时间必选其一
	 */
	private Date modifyDateEnd;

	/** 
	* 记录修改起始时间 和 改签发起时间必选其一
	 */
	private Date modifyDateStart;

	/** 
	* 淘宝订单号
	 */
	private Long orderId;

	/** 
	* 每页记录数
	 */
	private Long pageSize;

	/** 
	* 1：初始状态，2：已改签成功，3：已拒绝，4：未付款（已回填退票费），5：已付款
	 */
	private Long status;

	public void setApplyDateEnd(Date applyDateEnd) {
		this.applyDateEnd = applyDateEnd;
	}

	public Date getApplyDateEnd() {
		return this.applyDateEnd;
	}

	public void setApplyDateStart(Date applyDateStart) {
		this.applyDateStart = applyDateStart;
	}

	public Date getApplyDateStart() {
		return this.applyDateStart;
	}

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public Long getApplyId() {
		return this.applyId;
	}

	public void setCurrentPage(Long currentPage) {
		this.currentPage = currentPage;
	}

	public Long getCurrentPage() {
		return this.currentPage;
	}

	public void setFlyDateEnd(Date flyDateEnd) {
		this.flyDateEnd = flyDateEnd;
	}

	public Date getFlyDateEnd() {
		return this.flyDateEnd;
	}

	public void setFlyDateStart(Date flyDateStart) {
		this.flyDateStart = flyDateStart;
	}

	public Date getFlyDateStart() {
		return this.flyDateStart;
	}

	public void setModifyDateEnd(Date modifyDateEnd) {
		this.modifyDateEnd = modifyDateEnd;
	}

	public Date getModifyDateEnd() {
		return this.modifyDateEnd;
	}

	public void setModifyDateStart(Date modifyDateStart) {
		this.modifyDateStart = modifyDateStart;
	}

	public Date getModifyDateStart() {
		return this.modifyDateStart;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOrderId() {
		return this.orderId;
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
		return "taobao.alitrip.seller.modify.list";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("apply_date_end", this.applyDateEnd);
		txtParams.put("apply_date_start", this.applyDateStart);
		txtParams.put("apply_id", this.applyId);
		txtParams.put("current_page", this.currentPage);
		txtParams.put("fly_date_end", this.flyDateEnd);
		txtParams.put("fly_date_start", this.flyDateStart);
		txtParams.put("modify_date_end", this.modifyDateEnd);
		txtParams.put("modify_date_start", this.modifyDateStart);
		txtParams.put("order_id", this.orderId);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("status", this.status);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerModifyListResponse> getResponseClass() {
		return AlitripSellerModifyListResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}