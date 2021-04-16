package cn.ssq.ticket.system.entity.importBean.TTsBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * 去哪儿订单VO
 */
public class QnrOrderVO implements Serializable{

	private static final long serialVersionUID = -5981211489057948958L;
	/**
	 * 唯一标识id
	 */
	private String id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 订单金额
	 */
	private String allPrice;
	
	/**
	 * 未支付金额
	 */
	private String noPay;
	
	/**
	 * 儿童票面价
	 */
	private String childPrintPrice;
	
	/**
	 * 儿童销售价
	 */
	private String childFaceValue;
	
	/**
	 * 订单来源
	 */
	private String source;
	
	/**
	 * 政策代码
	 */
	private String policySource;
	
	/**
	 * 出票方式
	 */
	private String ticketMode;
	
	/**
	 * 订单匹配政策号
	 */
	private String policyFriendly;
	
	/**
	 * 支付渠道
	 */
	private String payChannel;
	
	/**
	 * 支付状态
	 * 5:现金支付
	 * 11:快钱分账
	 * 12:支付宝分账
	 * 13:财付通分账
	 * 21:快捷支付
	 * 23:无线支付
	 */
	private String payStatus;
	
	private String lastPrintTicketTime;;
	
	/**
	 * 保单单价，一般是20或30
	 */
	private String insuranceUnitPrice;
	
	/**
	 * 退改签说明
	 */
	private String backnote;
	
	/**
	 * 成人保险让利价
	 */
	private String insuranceCuttingPrice;
	
	/**
	 * 舱位说明
	 */
	private String cabinnote;
	
	/**
	 * 快递实际价格
	 */
	private String xcdPrice;
	
	/**
	 * 行程单
	 */
	private String xcd;
	
	/**
	 * 收件人
	 */
	private String sjr;
	
	/**
	 * 联系人电话
	 */
	private String contactTel;
	
	/**
	 * 收件人地址
	 */
	private String address;
	
	/**
	 * 快递公司
	 */
	private String company;
	
	/**
	 * 普通/到付
	 */
	private String expType;
	
	/**
	 * 进单类型
	 */
	private String ticType;
	
	private String lastUpdateTime;
	
	private String bigPnr;
	

	private String rootOrderNo;
	
	
	private String XCDN;

	private String refundPrice;

	private String refundReason;


	public String getRefundPrice() {
		return refundPrice;
	}

	public void setRefundPrice(String refundPrice) {
		this.refundPrice = refundPrice;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getXCDN() {
		return XCDN;
	}

	public void setXCDN(String xCDN) {
		XCDN = xCDN;
	}

	public String getRootOrderNo() {
		return rootOrderNo;
	}

	public void setRootOrderNo(String rootOrderNo) {
		this.rootOrderNo = rootOrderNo;
	}

	public String getTicType() {
		return ticType;
	}

	public void setTicType(String ticType) {
		this.ticType = ticType;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getBigPnr() {
		return bigPnr;
	}

	public void setBigPnr(String bigPnr) {
		this.bigPnr = bigPnr;
	}

	@Override
	public String toString() {
		return "QnrOrderVO{" +
				"id='" + id + '\'' +
				", orderNo='" + orderNo + '\'' +
				", allPrice='" + allPrice + '\'' +
				", noPay='" + noPay + '\'' +
				", childPrintPrice='" + childPrintPrice + '\'' +
				", childFaceValue='" + childFaceValue + '\'' +
				", source='" + source + '\'' +
				", policySource='" + policySource + '\'' +
				", ticketMode='" + ticketMode + '\'' +
				", policyFriendly='" + policyFriendly + '\'' +
				", payChannel='" + payChannel + '\'' +
				", payStatus='" + payStatus + '\'' +
				", lastPrintTicketTime='" + lastPrintTicketTime + '\'' +
				", insuranceUnitPrice='" + insuranceUnitPrice + '\'' +
				", backnote='" + backnote + '\'' +
				", insuranceCuttingPrice='" + insuranceCuttingPrice + '\'' +
				", cabinnote='" + cabinnote + '\'' +
				", xcdPrice='" + xcdPrice + '\'' +
				", xcd='" + xcd + '\'' +
				", sjr='" + sjr + '\'' +
				", contactTel='" + contactTel + '\'' +
				", address='" + address + '\'' +
				", company='" + company + '\'' +
				", expType='" + expType + '\'' +
				", ticType='" + ticType + '\'' +
				", lastUpdateTime='" + lastUpdateTime + '\'' +
				", bigPnr='" + bigPnr + '\'' +
				", rootOrderNo='" + rootOrderNo + '\'' +
				", XCDN='" + XCDN + '\'' +
				", refundPrice='" + refundPrice + '\'' +
				", refundReason='" + refundReason + '\'' +
				", ordernumber='" + ordernumber + '\'' +
				", sendtime='" + sendtime + '\'' +
				", viewPrice='" + viewPrice + '\'' +
				", price='" + price + '\'' +
				", pnr='" + pnr + '\'' +
				", cpnr='" + cpnr + '\'' +
				", constructionFee='" + constructionFee + '\'' +
				", fuelTax='" + fuelTax + '\'' +
				", childFuelTax='" + childFuelTax + '\'' +
				", policyType='" + policyType + '\'' +
				", status='" + status + '\'' +
				", contact='" + contact + '\'' +
				", contactMob='" + contactMob + '\'' +
				", contactEmail='" + contactEmail + '\'' +
				", createTime='" + createTime + '\'' +
				", needPS='" + needPS + '\'' +
				", purchasePrice='" + purchasePrice + '\'' +
				", issue_ticket_type='" + issue_ticket_type + '\'' +
				", issue_ticket_platform_code='" + issue_ticket_platform_code + '\'' +
				", source_transaction_id='" + source_transaction_id + '\'' +
				", source_order_no='" + source_order_no + '\'' +
				", tpp_type='" + tpp_type + '\'' +
				", purchaseStatus='" + purchaseStatus + '\'' +
				", pay_commercial_no='" + pay_commercial_no + '\'' +
				", offline_pay_type='" + offline_pay_type + '\'' +
				", offline_pay_cardNo='" + offline_pay_cardNo + '\'' +
				", platform_mem_id='" + platform_mem_id + '\'' +
				", operator='" + operator + '\'' +
				", refund_price='" + refund_price + '\'' +
				", flightList=" + flightList +
				", passengerList=" + passengerList +
				", orderLogList=" + orderLogList +
				", policyCode='" + policyCode + '\'' +
				'}';
	}

	/**
	 * 快递号
	 */
	private String ordernumber;
	
	/**
	 * 投递日期
	 */
	private String sendtime;
	
	/**
	 * 票面价
	 */
	private String viewPrice;
	
	/**
	 * 销售价
	 */
	private String price;
	
	/**
	 * 成人PNR
	 */
	private String pnr;
	
	/**
	 * 儿童PNR
	 */
	private String cpnr;
	
	/**
	 * 基建
	 */
	private String constructionFee;
	
	/**
	 * 燃油
	 */
	private String fuelTax;
	
	/**
	 * 儿童燃油附加费
	 */
	private String childFuelTax;
	
	/**
	 * 产品类型
	 */
	private String policyType;
	
	/**
	 * 订单状态 
	 * NONE(100, ""),
	 * PPLY_4_RETURN_PAY(50, "未出票申请退款")
	 * ORDER_SUCCESS_WAIT_4_PRICE_CONFIRM(51, "订座成功等待价格确认")
	 * WAIT_CONFIRM(20,"等待座位确认")
	 * BOOK_OK(0,"订座成功等待支付")
	 * CANCEL_OK(12,"订单取消")
	 * PAY_OK(1,"支付成功等待出票")
	 * TICKET_LOCK(5,"出票中")
	 * TICKET_OK(2,"出票完成")
	 * APPLY_CHANGE(40,"改签申请中")
	 * CHANGE_OK(42,"改签完成")
	 * APPLY_REFUNDMENT(30,"退票申请中")
	 * WAIT_REFUNDMENT(31,"退票完成等待退款")
	 * REFUND_OK(39,"退款完成")
	 */
	private String status;
	
	/**
	 * 订单联系人
	 */
	private String contact;
	
	/**
	 * 订单联系人电话
	 */
	private String contactMob;
	
	/**
	 * 订单联系人邮箱
	 */
	private String contactEmail;
	
	/**
	 * 订单创建时间
	 */
	private String createTime;
	
	/**
	 * 是否需要配送
	 */
	private String needPS;
	
	//以下为采购单信息节点字段
	/**
	 * 采购金额
	 */
	private String purchasePrice;
	
	/**
	 * 出票方式
	 */
	private String issue_ticket_type;
	
	/**
	 * 出票平台
	 */
	private String issue_ticket_platform_code;
	
	/**
	 * 支付平台流水号
	 */
	private String source_transaction_id;
	
	/**
	 * 采购源订单号
	 */
	private String source_order_no;
	
	/**
	 * 支付方式
	 */
	private String tpp_type;
	
	/**
	 * 采购状态
	 */
	private String purchaseStatus;
	
	/**
	 * 支付商户订单号
	 */
	private String pay_commercial_no;
	
	/**
	 * 线下支付方式
	 */
	private String offline_pay_type;
	
	/**
	 * 线下支付卡号
	 */
	private String offline_pay_cardNo;
	
	/**
	 * 采购会员id
	 */
	private String platform_mem_id;
	
	/**
	 * 出票员
	 */
	private String operator;
	
	/**
	 * 采购退款金额
	 */
	private String refund_price;
	
	/**
	 * 航段信息
	 */
	private List<QnrFlightVO> flightList = new ArrayList<QnrFlightVO>();
	
	/**
	 * 乘机人信息
	 */
	private List<QnrPassengerVO> passengerList = new ArrayList<QnrPassengerVO>();
	
	/**
	 * 订单操作日志信息
	 */
	private List<QnrOrderLogVO> orderLogList = new ArrayList<QnrOrderLogVO>();

/*	*//**
	 * 快递地址
	 *//*
	private String kdAddress;
	
	*//**
	 * 快递费用
	 *//*
	private String kdPrice;
	
	*//**
	 * 快递方式
	 *//*
	private String kdMethod;
	
	*//**
	 * 收件人电话
	 *//*
	private String kdPhone;
	
	*//**
	 * 收件人
	 *//*
	private String kdReceiver;
	
	
	*//**
	 * 支付交易流水号
	 *//*
	private String payTransactionid;
	
	*//**
	 * 旅客姓名是否有拼音
	 *//*
	private String pnrHashPinyin;
	
	*//**
	 * 退款交易流水号
	 *//*
	private String refundTransactionid;
	
	*/
	/**
	 * 政策编号
	 */
	
	private String policyCode;
	
	/**//**
	 * 政策ID
	 *//*
	private String policyId;
	
	private String ticketDeadline;
	
	*//**
	 * 自动出票平台
	 *//*
	private String autoTicketPlatform;
	
	*//**
	 * 外部订单id
	 *//*
	private String autoOrderId;
*/
	
	/**
	 * 获取唯一标识id<p>
	 * @return  id  唯一标识id<br>
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * 设置唯一标识id<p>
	 * @param  id  唯一标识id<br>
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * 获取订单号<p>
	 * @return  orderNo  订单号<br>
	 */
	public String getOrderNo()
	{
		return orderNo;
	}

	/**
	 * 设置订单号<p>
	 * @param  orderNo  订单号<br>
	 */
	public void setOrderNo(String orderNo)
	{
		this.orderNo = orderNo;
	}

	/**
	 * 获取订单金额<p>
	 * @return  allPrice  订单金额<br>
	 */
	public String getAllPrice()
	{
		return allPrice;
	}

	/**
	 * 设置订单金额<p>
	 * @param  allPrice  订单金额<br>
	 */
	public void setAllPrice(String allPrice)
	{
		this.allPrice = allPrice;
	}

	/**
	 * 获取订单状态<p>
	 * @return  status  订单状态NONE(100"")PPLY_4_RETURN_PAY(50"未出票申请退款")ORDER_SUCCESS_WAIT_4_PRICE_CONFIRM(51"订座成功等待价格确认")WAIT_CONFIRM(20"等待座位确认")BOOK_OK(0"订座成功等待支付")CANCEL_OK(12"订单取消")PAY_OK(1"支付成功等待出票")TICKET_LOCK(5"出票中")TICKET_OK(2"出票完成")APPLY_CHANGE(40"改签申请中")CHANGE_OK(42"改签完成")APPLY_REFUNDMENT(30"退票申请中")WAIT_REFUNDMENT(31"退票完成等待退款")REFUND_OK(39"退款完成")<br>
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * 设置订单状态<p>
	 * @param  status  订单状态NONE(100"")PPLY_4_RETURN_PAY(50"未出票申请退款")ORDER_SUCCESS_WAIT_4_PRICE_CONFIRM(51"订座成功等待价格确认")WAIT_CONFIRM(20"等待座位确认")BOOK_OK(0"订座成功等待支付")CANCEL_OK(12"订单取消")PAY_OK(1"支付成功等待出票")TICKET_LOCK(5"出票中")TICKET_OK(2"出票完成")APPLY_CHANGE(40"改签申请中")CHANGE_OK(42"改签完成")APPLY_REFUNDMENT(30"退票申请中")WAIT_REFUNDMENT(31"退票完成等待退款")REFUND_OK(39"退款完成")<br>
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * 获取联系人姓名<p>
	 * @return  contact  联系人姓名<br>
	 */
	public String getContact()
	{
		return contact;
	}

	/**
	 * 设置联系人姓名<p>
	 * @param  contact  联系人姓名<br>
	 */
	public void setContact(String contact)
	{
		this.contact = contact;
	}

	/**
	 * 获取联系人电话<p>
	 * @return  contactMob  联系人电话<br>
	 */
	public String getContactMob()
	{
		return contactMob;
	}

	/**
	 * 设置联系人电话<p>
	 * @param  contactMob  联系人电话<br>
	 */
	public void setContactMob(String contactMob)
	{
		this.contactMob = contactMob;
	}

	/**
	 * 获取联系人邮箱<p>
	 * @return  contactEmail  联系人邮箱<br>
	 */
	public String getContactEmail()
	{
		return contactEmail;
	}

	/**
	 * 设置联系人邮箱<p>
	 * @param  contactEmail  联系人邮箱<br>
	 */
	public void setContactEmail(String contactEmail)
	{
		this.contactEmail = contactEmail;
	}

	/**
	 * 获取支付方式<p>
	 * @return  payStatus  支付方式5:现金支付11:快钱分账12:支付宝分账13:财付通分账21:快捷支付23:无线支付<br>
	 */
	public String getPayStatus()
	{
		return payStatus;
	}

	/**
	 * 设置支付方式<p>
	 * @param  payStatus  支付方式5:现金支付11:快钱分账12:支付宝分账13:财付通分账21:快捷支付23:无线支付<br>
	 */
	public void setPayStatus(String payStatus)
	{
		this.payStatus = payStatus;
	}

	/**
	 * 获取订单创建时间<p>
	 * @return  createTime  订单创建时间<br>
	 */
	public String getCreateTime()
	{
		return createTime;
	}

	/**
	 * 设置订单创建时间<p>
	 * @param  createTime  订单创建时间<br>
	 */
	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}

	
	
	public String getLastPrintTicketTime() {
		return lastPrintTicketTime;
	}

	public void setLastPrintTicketTime(String lastPrintTicketTime) {
		this.lastPrintTicketTime = lastPrintTicketTime;
	}

	/**
	 * 获取是否需要配送<p>
	 * @return  needPS  是否需要配送<br>
	 */
	public String getNeedPS()
	{
		return needPS;
	}

	/**
	 * 设置是否需要配送<p>
	 * @param  needPS  是否需要配送<br>
	 */
	public void setNeedPS(String needPS)
	{
		this.needPS = needPS;
	}

	

	/**
	 * 获取订单来源<p>
	 * @return  source  订单来源<br>
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * 设置订单来源<p>
	 * @param  source  订单来源<br>
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * 获取航段信息<p>
	 * @return  flightList  航段信息<br>
	 */
	public List<QnrFlightVO> getFlightList()
	{
		return flightList;
	}

	/**
	 * 设置航段信息<p>
	 * @param  flightList  航段信息<br>
	 */
	public void setFlightList(List<QnrFlightVO> flightList)
	{
		this.flightList = flightList;
	}

	/**
	 * 获取乘机人信息<p>
	 * @return  passengerList  乘机人信息<br>
	 */
	public List<QnrPassengerVO> getPassengerList()
	{
		return passengerList;
	}

	/**
	 * 设置乘机人信息<p>
	 * @param  passengerList  乘机人信息<br>
	 */
	public void setPassengerList(List<QnrPassengerVO> passengerList)
	{
		this.passengerList = passengerList;
	}

	/**
	 * 获取订单操作日志信息<p>
	 * @return  orderLogList  订单操作日志信息<br>
	 */
	public List<QnrOrderLogVO> getOrderLogList()
	{
		return orderLogList;
	}

	/**
	 * 设置订单操作日志信息<p>
	 * @param  orderLogList  订单操作日志信息<br>
	 */
	public void setOrderLogList(List<QnrOrderLogVO> orderLogList)
	{
		this.orderLogList = orderLogList;
	}

	public String getNoPay() {
		return noPay;
	}

	public void setNoPay(String noPay) {
		this.noPay = noPay;
	}

	public String getChildPrintPrice() {
		return childPrintPrice;
	}

	public void setChildPrintPrice(String childPrintPrice) {
		this.childPrintPrice = childPrintPrice;
	}

	public String getChildFaceValue() {
		return childFaceValue;
	}

	public void setChildFaceValue(String childFaceValue) {
		this.childFaceValue = childFaceValue;
	}

	public String getPolicySource() {
		return policySource;
	}

	public void setPolicySource(String policySource) {
		this.policySource = policySource;
	}

	public String getTicketMode() {
		return ticketMode;
	}

	public void setTicketMode(String ticketMode) {
		this.ticketMode = ticketMode;
	}

	public String getPolicyFriendly() {
		return policyFriendly;
	}

	public void setPolicyFriendly(String policyFriendly) {
		this.policyFriendly = policyFriendly;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getInsuranceUnitPrice() {
		return insuranceUnitPrice;
	}

	public void setInsuranceUnitPrice(String insuranceUnitPrice) {
		this.insuranceUnitPrice = insuranceUnitPrice;
	}

	public String getBacknote() {
		return backnote;
	}

	public void setBacknote(String backnote) {
		this.backnote = backnote;
	}

	public String getInsuranceCuttingPrice() {
		return insuranceCuttingPrice;
	}

	public void setInsuranceCuttingPrice(String insuranceCuttingPrice) {
		this.insuranceCuttingPrice = insuranceCuttingPrice;
	}

	public String getCabinnote() {
		return cabinnote;
	}

	public void setCabinnote(String cabinnote) {
		this.cabinnote = cabinnote;
	}

	public String getXcdPrice() {
		return xcdPrice;
	}

	public void setXcdPrice(String xcdPrice) {
		this.xcdPrice = xcdPrice;
	}

	public String getXcd() {
		return xcd;
	}

	public void setXcd(String xcd) {
		this.xcd = xcd;
	}

	public String getSjr() {
		return sjr;
	}

	public void setSjr(String sjr) {
		this.sjr = sjr;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
	}

	public String getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getViewPrice() {
		return viewPrice;
	}

	public void setViewPrice(String viewPrice) {
		this.viewPrice = viewPrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getCpnr() {
		return cpnr;
	}

	public void setCpnr(String cpnr) {
		this.cpnr = cpnr;
	}

	public String getConstructionFee() {
		return constructionFee;
	}

	public void setConstructionFee(String constructionFee) {
		this.constructionFee = constructionFee;
	}

	public String getFuelTax() {
		return fuelTax;
	}

	public void setFuelTax(String fuelTax) {
		this.fuelTax = fuelTax;
	}

	public String getChildFuelTax() {
		return childFuelTax;
	}

	public void setChildFuelTax(String childFuelTax) {
		this.childFuelTax = childFuelTax;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getIssue_ticket_type() {
		return issue_ticket_type;
	}

	public void setIssue_ticket_type(String issueTicketType) {
		issue_ticket_type = issueTicketType;
	}

	public String getIssue_ticket_platform_code() {
		return issue_ticket_platform_code;
	}

	public void setIssue_ticket_platform_code(String issueTicketPlatformCode) {
		issue_ticket_platform_code = issueTicketPlatformCode;
	}

	public String getSource_transaction_id() {
		return source_transaction_id;
	}

	public void setSource_transaction_id(String sourceTransactionId) {
		source_transaction_id = sourceTransactionId;
	}

	public String getSource_order_no() {
		return source_order_no;
	}

	public void setSource_order_no(String sourceOrderNo) {
		source_order_no = sourceOrderNo;
	}

	public String getTpp_type() {
		return tpp_type;
	}

	public void setTpp_type(String tppType) {
		tpp_type = tppType;
	}

	public String getPurchaseStatus() {
		return purchaseStatus;
	}

	public void setPurchaseStatus(String purchaseStatus) {
		this.purchaseStatus = purchaseStatus;
	}

	public String getPay_commercial_no() {
		return pay_commercial_no;
	}

	public void setPay_commercial_no(String payCommercialNo) {
		pay_commercial_no = payCommercialNo;
	}

	public String getOffline_pay_type() {
		return offline_pay_type;
	}

	public void setOffline_pay_type(String offlinePayType) {
		offline_pay_type = offlinePayType;
	}

	public String getOffline_pay_cardNo() {
		return offline_pay_cardNo;
	}

	public void setOffline_pay_cardNo(String offlinePayCardNo) {
		offline_pay_cardNo = offlinePayCardNo;
	}

	public String getPlatform_mem_id() {
		return platform_mem_id;
	}

	public void setPlatform_mem_id(String platformMemId) {
		platform_mem_id = platformMemId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRefund_price() {
		return refund_price;
	}

	public void setRefund_price(String refundPrice) {
		refund_price = refundPrice;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

}
