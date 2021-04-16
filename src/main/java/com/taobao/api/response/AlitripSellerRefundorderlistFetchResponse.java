package com.taobao.api.response;

import java.util.List;
import com.taobao.api.domain.ReturnApplyDo;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.alitrip.seller.refundorderlist.fetch response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class AlitripSellerRefundorderlistFetchResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6397637722294675188L;

	/** 
	 * 退票订单列表
	 */
	@ApiListField("result_list")
	@ApiField("return_apply_do")
	private List<ReturnApplyDo> resultList;


	public void setResultList(List<ReturnApplyDo> resultList) {
		this.resultList = resultList;
	}
	public List<ReturnApplyDo> getResultList( ) {
		return this.resultList;
	}
	


}
