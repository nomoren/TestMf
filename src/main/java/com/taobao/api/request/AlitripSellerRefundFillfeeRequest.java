package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import com.taobao.api.internal.util.json.JSONValidatingReader;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlitripSellerRefundFillfeeResponse;

/**
 * TOP API: taobao.alitrip.seller.refund.fillfee request
 * 
 * @author top auto create
 * @since 1.0, 2018.10.17
 */
public class AlitripSellerRefundFillfeeRequest extends BaseTaobaoRequest<AlitripSellerRefundFillfeeResponse> {
	
	

	/** 
	* 申请单ID
	 */
	private Long applyId;

	/** 
	* 费对于关系，格式：{apply_fee_id:123,value:费用,金额单位分}
	 */
	private String feePriceMap;

	/** 
	* 改签费用，格式：{detail_id:123,value:费用,金额单位分}
	 */
	private String modifyFee;

	/** 
	* 票价信息，格式：{apply_fee_id：123,value:费用,金额单位分}
	 */
	private String ticketPriceMap;

	/** 
	* 升舱费用，格式：{detail_id:123,value:费用,金额单位分}
	 */
	private String upgradeFee;

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public Long getApplyId() {
		return this.applyId;
	}

	public void setFeePriceMap(String feePriceMap) {
		this.feePriceMap = feePriceMap;
	}
	public void setFeePriceMapString(String feePriceMap) {
		this.feePriceMap = feePriceMap;
	}

	public String getFeePriceMap() {
		return this.feePriceMap;
	}

	public void setModifyFee(String modifyFee) {
		this.modifyFee = modifyFee;
	}
	public void setModifyFeeString(String modifyFee) {
		this.modifyFee = modifyFee;
	}

	public String getModifyFee() {
		return this.modifyFee;
	}

	public void setTicketPriceMap(String ticketPriceMap) {
		this.ticketPriceMap = ticketPriceMap;
	}
	public void setTicketPriceMapString(String ticketPriceMap) {
		this.ticketPriceMap = ticketPriceMap;
	}

	public String getTicketPriceMap() {
		return this.ticketPriceMap;
	}

	public void setUpgradeFee(String upgradeFee) {
		this.upgradeFee = upgradeFee;
	}
	public void setUpgradeFeeString(String upgradeFee) {
		this.upgradeFee = upgradeFee;
	}

	public String getUpgradeFee() {
		return this.upgradeFee;
	}

	public String getApiMethodName() {
		return "taobao.alitrip.seller.refund.fillfee";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("apply_id", this.applyId);
		txtParams.put("fee_price_map", this.feePriceMap);
		txtParams.put("modify_fee", this.modifyFee);
		txtParams.put("ticket_price_map", this.ticketPriceMap);
		txtParams.put("upgrade_fee", this.upgradeFee);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlitripSellerRefundFillfeeResponse> getResponseClass() {
		return AlitripSellerRefundFillfeeResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(applyId, "applyId");
		RequestCheckUtils.checkNotEmpty(feePriceMap, "feePriceMap");
		RequestCheckUtils.checkNotEmpty(ticketPriceMap, "ticketPriceMap");
	}
	

}