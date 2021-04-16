package cn.ssq.ticket.system.service.OrderImport.impl;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.service.ChangeService;
import cn.ssq.ticket.system.service.OrderImport.OrderImportService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.RefundService;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.LimitQueue;
import cn.ssq.ticket.system.util.SendQQMsgUtil;
import cn.ssq.ticket.system.util.WebConstant;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.TripBaseInfo;
import com.taobao.api.domain.TripFlightInfo;
import com.taobao.api.domain.TripFlightPassenger;
import com.taobao.api.domain.TripOrder;
import com.taobao.api.request.*;
import com.taobao.api.request.AlitripFlightchangeAddRequest.FlightChangeDataDO;
import com.taobao.api.response.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 淘宝订单服务类
 * 注：淘宝接口都是用阿里服务器作为代理，需要注意代理服务器正常运行
 * 目前使用的代理服务器：104.121.148.15，在淘宝开放平台聚石塔可查看
 */
@Service("tbOrderService")
public class TbOrderService implements OrderImportService{
	private static Logger log = LoggerFactory.getLogger(TbOrderService.class);

	//待出票订单队列
	public static LimitQueue<String> status1 = new LimitQueue<String>(200);

	//已取消订单队列
	public static LimitQueue<String> status5 = new LimitQueue<String>(200);
	
	public static String PROXY_SERVER="101.201.148.15";

	private static String tbCookie=new String();

	@Autowired
	private  MongoTemplate mongnTemplate;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private RefundService refundService;
	
	@Autowired
	private ChangeService changeService;

	private static SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 批量导入
	 */
	@Override
	public List<OrderVO> batchImportOrders(String orderSource,
			String orderShop, String status) throws Exception {
		long sta = Long.valueOf(status);
		List<OrderVO> list=new ArrayList<OrderVO>();
		try {
			String url=DictUtils.getDictCode("order_import_cfgtb", "url");
			String appkey=DictUtils.getDictCode("order_import_cfgtb", "appkey");
			String secret=DictUtils.getDictCode("order_import_cfgtb", "secret");
			String sessionKey=DictUtils.getDictCode("order_import_cfgtb", "session");
			
			DefaultTaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			InetSocketAddress isa=new InetSocketAddress(PROXY_SERVER, 30000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
			client.setProxy(proxy);
			
			JipiaoAgentOrderSearchRequest req = new JipiaoAgentOrderSearchRequest();
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.HOUR_OF_DAY, -2);
			String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			req.setBeginTime(SDF.parse(bDate));
			req.setEndTime(SDF.parse(eDate));
			req.setStatus(sta);
			req.setHasItinerary(false);
			req.setPage(1L);
			JipiaoAgentOrderSearchResponse rsp = client.execute(req, sessionKey);
			if(rsp.isSuccess()){
				JSONObject fromObject = JSONObject.fromObject(rsp.getBody());
				JSONObject result= fromObject.getJSONObject("jipiao_agent_order_search_response").getJSONObject("search_result");
				if(result.toString().contains("order_ids")){	
					JSONArray jsonArray = result.getJSONObject("order_ids").getJSONArray("number");
					for(int i=0;i<jsonArray.size();i++){
						String orderNo=jsonArray.getString(i);
						if(status1.contains(orderNo)){
							continue;
						}
						OrderVO orderVo = this.getOrderByOrderNo(orderNo, orderSource, orderShop);
						if(orderVo!=null){
							list.add(orderVo);
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return list;
	}

	/**
	 * 获取订单详情
	 * @param orderNo
	 * @param orderSource
	 * @param orderShop
	 * @return
	 */
	public OrderVO getOrderByOrderNo(String orderNo,String orderSource,String orderShop){
		try {
			String url=DictUtils.getDictCode("order_import_cfgtb", "url");
			String appkey=DictUtils.getDictCode("order_import_cfgtb", "appkey");
			String secret=DictUtils.getDictCode("order_import_cfgtb", "secret");
			String sessionKey=DictUtils.getDictCode("order_import_cfgtb", "session");
			
			DefaultTaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			InetSocketAddress isa=new InetSocketAddress(PROXY_SERVER, 30000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
			client.setProxy(proxy);
			
			JipiaoAgentOrderDetailRequest req = new JipiaoAgentOrderDetailRequest();
			req.setOrderIds(orderNo);
			JipiaoAgentOrderDetailResponse rsp = client.execute(req, sessionKey);
			//System.out.println(rsp.getBody());
			if(rsp.getIsSuccess()){
				List<TripOrder> orders = rsp.getOrders();
				if(orders.size()>0){
					TripOrder tripOrder = orders.get(0);
					return this.orderJsonToOrderVO(tripOrder, orderSource, orderShop);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取淘宝订单详情失败",e);
		}
		return null;
	}

	/**
	 * 票号验证
	 * @param passengerList
	 * @return
	 * @throws Exception
	 */
	public String verifyTicketNo(List<Passenger> passengerList) {
		try {
			String url=DictUtils.getDictCode("order_import_cfgtb", "url");
			String appkey=DictUtils.getDictCode("order_import_cfgtb", "appkey");
			String secret=DictUtils.getDictCode("order_import_cfgtb", "secret");
			String sessionKey=DictUtils.getDictCode("order_import_cfgtb", "session");

			String orderNo=passengerList.get(0).getOrderNo();
			StringBuilder sb=new StringBuilder();
			for(Passenger passenger:passengerList){
				sb.append(passenger.getFlightNo()).append(";").append(passenger.getName()).append(";").append(passenger.getName()).append(";").append(passenger.getTicketNo().trim()).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
	
			DefaultTaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			InetSocketAddress isa=new InetSocketAddress(PROXY_SERVER, 30000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
			client.setProxy(proxy);
				
			JipiaoAgentOrderTicketRequest req = new JipiaoAgentOrderTicketRequest();
			req.setOrderId(Long.valueOf(orderNo));
			req.setSuccessInfo(sb.toString());
			JipiaoAgentOrderTicketResponse rsp = client.execute(req, sessionKey);
			if(rsp.isSuccess()){
				return rsp.getBody();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * 同步订单票号和状态
	 * @param
	 */
	public void updateTickNo(List<Order> orderList){
		for(Order order:orderList){
			try {
				OrderVO orderVo = getOrderByOrderNo(order.getOrderNo(),order.getOrderSource(), order.getOrderShop());
				if(orderVo!=null){
					if("3".equals(orderVo.getStatus())){//预定成功
						List<Passenger> passengetList = orderVo.getPassengetList();
						List<Passenger> list2=new ArrayList<Passenger>();
						boolean haveTicket=true;
						for(Passenger p:passengetList){
							if(StringUtils.isEmpty(p.getTicketNo())){
								haveTicket=false;
								break;
							}
							Passenger passenger=new Passenger();
							passenger.setTicketNo(p.getTicketNo());
							passenger.setOrderNo(p.getOrderNo());
							passenger.setPrintTicketCabin(p.getCabin());
							passenger.setCertNo(p.getCertNo());
							list2.add(passenger);
						}
						if(haveTicket){
							orderService.updateTicketNo(order.getOrderNo(),list2);
						}
					}else if("7".equals(orderVo.getStatus())){//买家取消
						orderService.updateStatus(WebConstant.ORDER_CANCEL, order.getOrderNo());
					}else if("4".equals(orderVo.getStatus())){//预定失败
						orderService.updateStatus(WebConstant.ORDER_CANCEL, order.getOrderNo());
					}else if("5".equals(orderVo.getStatus())){//:处理中超时失效
						orderService.updateStatus(WebConstant.ORDER_CANCEL, order.getOrderNo());
					}else if("6".equals(orderVo.getStatus())){//支付超时失效
						orderService.updateStatus(WebConstant.ORDER_CANCEL, order.getOrderNo());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}



	}

	
	/**
	 * 同步改签单
	 * @throws Exception 
	 */
	public void sysncChangeOrder() throws Exception{
		String url=DictUtils.getDictCode("order_import_cfgtb", "url");
		String appkey=DictUtils.getDictCode("order_import_cfgtb", "appkey");
		String secret=DictUtils.getDictCode("order_import_cfgtb", "secret");
		String sessionKey=DictUtils.getDictCode("order_import_cfgtb", "session");

		try {
			long currentTime = System.currentTimeMillis() - 30 * 60 * 1000;
			String startDate=DateFormatUtils.format(new Date(currentTime), "yyyy-MM-dd HH:mm:ss");
			String endDate=DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			
			DefaultTaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			InetSocketAddress isa=new InetSocketAddress(PROXY_SERVER, 30000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
			client.setProxy(proxy);
			
			AlitripSellerModifyListRequest req = new AlitripSellerModifyListRequest();
			req.setApplyDateEnd(SDF.parse(endDate));
			req.setApplyDateStart(SDF.parse(startDate));
			req.setCurrentPage(1L);
			req.setPageSize(50L);
			req.setStatus(5L);
			AlitripSellerModifyListResponse rsp = client.execute(req, sessionKey);
           // System.out.println(rsp.getBody());
			if(rsp.isSuccess()){
				JSONObject fromObject = JSONObject.fromObject(rsp.getBody());
				JSONObject result = fromObject.getJSONObject("alitrip_seller_modify_list_response").getJSONObject("order_list");
				JSONArray jsonArray = null;
				try {
					jsonArray = result.getJSONArray("sync_order_do");
				} catch (Exception e) {
					return;
				}
				List<Change> list=new ArrayList<Change>();
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					//JSONObject before=jsonObject.getJSONObject("modify_before_flight");
					JSONObject after=jsonObject.getJSONObject("modify_after_flight");
					JSONObject passenger=jsonObject.getJSONObject("passenger");
					String orderNo=jsonObject.getString("order_id");
					String changeSerialNo=jsonObject.getString("apply_id");
					if(status5.contains(changeSerialNo)){
						continue;
					}
					Order order = orderService.getOrderBycOrderNo(orderNo);
					if(order==null){
						continue;
					}
					Change change=new Change();
					change.setOrderNo(order.getOrderNo());
					change.setOrderId(order.getOrderId());
					change.setOrderShop(order.getOrderShop());
					change.setOrderSource(order.getOrderSource());
					change.setNewCOrderNo(changeSerialNo);
					change.setPassengerName(passenger.getString("passenger_name"));
					change.setTktNo("");
					double changePrice=new BigDecimal(jsonObject.getDouble("modify_fee")).divide(new BigDecimal(100)).doubleValue()  + new BigDecimal(jsonObject.getDouble("upgrade_fee")).divide(new BigDecimal(100)).doubleValue();
					change.setRevenuePrice(changePrice+"");
					change.setUpgradeFee(new BigDecimal(jsonObject.getDouble("upgrade_fee")).divide(new BigDecimal(100)).toString());
					change.setsPnr(passenger.getString("pnr"));
					change.setsAirlineCode(order.getAirlineCode());
					change.setsFlightNo(order.getFlightNo());
					change.setsArrCityCode(order.getArrCityCode());
					change.setsDepCityCode(order.getDepCityCode());
					change.setsFlightDate(order.getFlightDate());
					change.setsCabin(order.getCabin());
					change.setCabin("");
					change.setFlightNo(after.getString("flight_no"));
					change.setFlightDate(after.getString("dep_date").split(" ")[0]);
					change.setChangeDate(SDF.format(new Date()));
					change.setCreateBy("SYSTEM");
					change.setCreateDate(SDF.format(new Date()));
					change.setState(WebConstant.CHANGE_UNTREATED);
					list.add(change);
				}
				changeService.saveChanges(list);
			}
			
		} catch (Exception e) {
			throw e;
		}
		
	}
	

	/**
	 * 同步退票单
	 * @throws Exception 
	 */
	public void sysncRefundOrder() throws Exception{
		String url=DictUtils.getDictCode("order_import_cfgtb", "url");
		String appkey=DictUtils.getDictCode("order_import_cfgtb", "appkey");
		String secret=DictUtils.getDictCode("order_import_cfgtb", "secret");
		String sessionKey=DictUtils.getDictCode("order_import_cfgtb", "session");

		try {
			long currentTime = System.currentTimeMillis() - 30 * 60 * 1000;
			String startDate=DateFormatUtils.format(new Date(currentTime), "yyyy-MM-dd HH:mm:ss");
			String endDate=DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			
			DefaultTaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			InetSocketAddress isa=new InetSocketAddress(PROXY_SERVER, 30000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
			client.setProxy(proxy);
			
			AlitripSellerRefundSearchRequest req = new AlitripSellerRefundSearchRequest();
			req.setEndTime(SDF.parse(endDate));
			req.setStartTime(SDF.parse(startDate));
			//req.setStatus(1L);
			AlitripSellerRefundSearchResponse rsp = client.execute(req, sessionKey);
			if(rsp.isSuccess()){
				JSONObject fromObject = JSONObject.fromObject(rsp.getBody());
				JSONObject result = fromObject.getJSONObject("alitrip_seller_refund_search_response").getJSONObject("result");
				JSONObject results = result.getJSONObject("results");
				if(!results.toString().contains("return_ticket_do")){
					return;
				}
				if(results!=null){
					JSONArray jsonArray = results.getJSONArray("return_ticket_do");
					for(int i=0;i<jsonArray.size();i++){
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						String retNo=jsonObject.getString("apply_id");
						if(status5.contains(retNo)){
							continue;
						}
						String refundDetail = getRefundDetail(retNo);
						if(StringUtils.isEmpty(refundDetail)){
							continue;
						}
						JSONObject refundJson = JSONObject.fromObject(refundDetail);
						JSONObject refundData = refundJson.getJSONObject("alitrip_seller_refund_get_response").getJSONObject("result").getJSONObject("results");
						String orderNo=refundData.getString("order_id");
						Order order = orderService.getOrderBycOrderNo(orderNo);
						if(order==null){
							continue;
						}
						if(WebConstant.ORDER_NO_TICKET.equals(order.getStatus())){
							Order o=new Order();
							o.setStatus(WebConstant.ORDER_NOTICK_REFUND);
							o.setOrderNo(order.getOrderNo());
							orderService.updateOrder(o);
						}
						JSONArray refundArr = refundData.getJSONObject("return_apply_passenge").getJSONArray("return_apply_passenge");
						for(int j=0;j<refundArr.size();j++){
							Refund refund=new Refund();
							JSONObject passenger=refundArr.getJSONObject(j);
							refund.setOrderId(order.getOrderId());
							refund.setOrderNo(order.getOrderNo());
							refund.setRetNo(retNo);
							refund.setOrderShop(order.getOrderShop());
							refund.setOrderSource(order.getOrderSource());
							refund.setAirlineCode(order.getAirlineCode());
							refund.setFlightNo(order.getFlightNo());
							refund.setDepCityCode(order.getDepCityCode());
							refund.setArrCityCode(order.getArrCityCode());
							refund.setFlightDate(order.getFlightDate());
							refund.setPrintTicketCabin(order.getCabin());
							refund.setCreateBy("SYSTEM");
							refund.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
							refund.setcRemState("0");//申请中
							refund.setAirRemState("0");
							refund.setcAppDate(refundData.getString("apply_time"));
							refund.setPnr(order.getPnr());
							try {
								refund.setTicketNo(passenger.getJSONObject("return_ticket_segment").getJSONArray("return_ticket_segment").getJSONObject(0).getString("ticket_no"));
								refund.setcRealPrice(new BigDecimal(passenger.getString("refund_money")).divide(new BigDecimal(100)).doubleValue());
								refund.setcRetPrice(new BigDecimal(passenger.getString("refund_fee")).divide(new BigDecimal(100)).toString());
							} catch (Exception e) {
							}
							refund.setPassengerName(passenger.getString("passenger_name"));
							String apply_reason_type = refundData.getString("apply_reason_type");
							if("1".equals(apply_reason_type)){
								refund.setRefundType(WebConstant.REFUND_TYPE_ZIYUAN);
							}else{
								refund.setRefundType(WebConstant.REFUND_TYPE_NOZIYUAN);
							}
							refundService.autoCreateRefund(refund);
						}
						status5.offer(retNo);	
					}
				}
			}
			
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * 获取退票详情
	 * @param
	 * @return
	 */
	public String getRefundDetail(String retNo){
		try {
			String url=DictUtils.getDictCode("order_import_cfgtb", "url");
			String appkey=DictUtils.getDictCode("order_import_cfgtb", "appkey");
			String secret=DictUtils.getDictCode("order_import_cfgtb", "secret");
			String sessionKey=DictUtils.getDictCode("order_import_cfgtb", "session");
			
			DefaultTaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			InetSocketAddress isa=new InetSocketAddress(PROXY_SERVER, 30000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
			client.setProxy(proxy);
			
			AlitripSellerRefundGetRequest req = new AlitripSellerRefundGetRequest();
			req.setApplyId(Long.valueOf(retNo));
			AlitripSellerRefundGetResponse rsp = client.execute(req, sessionKey);
			return rsp.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取淘宝订单详情失败",e);
		}
		return null;
	}

    /**
     * 航变信息提及
     * @param param
     * @return
     */
	public boolean subFlightChange(JSONObject param){
		try {
			String url=DictUtils.getDictCode("order_import_cfgtb", "url");
			String appkey=DictUtils.getDictCode("order_import_cfgtb", "appkey");
			String secret=DictUtils.getDictCode("order_import_cfgtb", "secret");
			String sessionKey=DictUtils.getDictCode("order_import_cfgtb", "session");
			DefaultTaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			InetSocketAddress isa=new InetSocketAddress(PROXY_SERVER, 30000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
			client.setProxy(proxy);
			AlitripFlightchangeAddRequest req = new AlitripFlightchangeAddRequest();
			FlightChangeDataDO obj1 = new FlightChangeDataDO();
			String orgDepTime = param.getString("orgDepTime");
			String[] orgDepTimeArr = orgDepTime.split(" ");
			obj1.setOldArrAirport(param.getString("orgEndPort"));
			obj1.setOldDepAirport(param.getString("orgStartPort"));
			obj1.setOldDepTimeStr(orgDepTimeArr[0]);
			obj1.setOldFltNum(param.getString("orgFlightNo"));
			obj1.setFlightChangeType(param.getLong("changeType"));
			
			if(StringUtils.isNotEmpty(param.getString("newStartPort"))){				
				obj1.setNewDepAirport(param.getString("newStartPort"));
			}
			if(StringUtils.isNotEmpty(param.getString("newEndPort"))){
				obj1.setNewArrAirport(param.getString("newEndPort"));
			}
			if(StringUtils.isNotEmpty(param.getString("newFlightNo"))){
				obj1.setNewFltNum(param.getString("newFlightNo"));
			}
			String newDepTime = param.getString("newDepTime");
			if(StringUtils.isNotEmpty(newDepTime)){
				obj1.setNewDepTimeStr(newDepTime.substring(0, 16));
				
			}
			String newArrTime = param.getString("newArrTime");
			if(StringUtils.isNotEmpty(newArrTime)){
				obj1.setNewArrTimeStr(newArrTime.substring(0, 16));
			}
			obj1.setBizType(0L);
			obj1.setOrderId(param.getLong("orderNo"));
			req.setFlightChangeDataDo(obj1);
			AlitripFlightchangeAddResponse rsp = client.execute(req, sessionKey);
			log.info("淘宝航变录入返回:"+rsp.getBody());
			JSONObject fromObject = JSONObject.fromObject(rsp.getBody());
			JSONObject jsonObject = fromObject.getJSONObject("alitrip_flightchange_add_response").getJSONObject("result");
			return jsonObject.getBoolean("success");
		} catch (Exception e) {
			return false;
		}
	}
	
	
	
	/**
	 * 确认退票
	 * @return
	 */
	public String confirmRefund(String retNo){
		try {
			String url=DictUtils.getDictCode("order_import_cfgtb", "url");
			String appkey=DictUtils.getDictCode("order_import_cfgtb", "appkey");
			String secret=DictUtils.getDictCode("order_import_cfgtb", "secret");
			String sessionKey=DictUtils.getDictCode("order_import_cfgtb", "session");
			TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			AlitripSellerRefundConfirmreturnRequest req = new AlitripSellerRefundConfirmreturnRequest();
			req.setApplyId(Long.parseLong(retNo));
			AlitripSellerRefundConfirmreturnResponse rsp = client.execute(req, sessionKey);
			return rsp.getBody();
		} catch (Exception e) {
			log.error("确认退票失败",e);
		}
		return null;
	}
	


	/**
	 * 锁单
	 * @param orderNo
	 */
	public String lockedOrder(String orderNo,boolean isAgain){
		CloseableHttpClient httpclient = HttpClients.custom().build();
		CloseableHttpResponse response = null;
		try {
			//cookie
			synchronized (this) {
				if(StringUtils.isEmpty(tbCookie)){
					Query query=new Query();
					query.addCriteria(Criteria.where("source").is("TB"));
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					tbCookie=cookie.getCookie();
				}
			}
			HttpGet httpGet = new HttpGet("http://jipiao.fliggy.com/agent/mockAction.do?action=notice_ajax_action&event_submit_do_seller_lock_order=1&orderId="+orderNo+"&lockUser=");
			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(10000).setConnectTimeout(10000).build();  
			httpGet.setConfig(requestConfig); 
			httpGet.addHeader("Accept-Encoding", "gzip, deflate");
			httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
			httpGet.addHeader("Accept", "application/json, text/plain, */*");
			httpGet.addHeader("Cache-Control", "no-cache");
			httpGet.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpGet.addHeader("Connection", "keep-alive");
			httpGet.addHeader("Host", "jipiao.fliggy.com");
			httpGet.addHeader("Origin","http://jpebook.ly.com");
			httpGet.addHeader("cookie", tbCookie.toString());
			httpGet.addHeader("Pragma", "no-cache");
			httpGet.addHeader("Referer", "http://jipiao.fliggy.com/agent/tripSellerOrderList.htm?beginCreateDate=+2020-03-10+&endCreateDate=+2020-04-10+&beginDepDate=&endDepDate=&processLatestBeginDate=&processLatestEndDate=&vos=-1&passengerName=&tcOrderId=&orderId=923815442615&bookCode=&alipayTradeNo=&telephone=&ticketNo=&pnrCode=&stockType=-1&orderTypeCode=-1&page=1");
			httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");
			response = httpclient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			//System.out.println(EntityUtils.toString(response.getEntity(), "UTF-8"));
			// 判断响应信息是否正确
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();	        
				String result = EntityUtils.toString(entity, "UTF-8");
				return result;
			}else{//登录过期
				tbCookie=null;
				log.info("TB的cookie过期了");
				/*if(isAgain){
					return lockedOrder(orderNo, false);
				}else{
					try {
						SendQQMsgUtil.send("Tb的cookie过期了");
					} catch (Exception e) {
						
					}
				}*/
			}
		}  catch (Exception e) {
			e.printStackTrace();
			tbCookie=null;
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}


	/**
	 * 解锁
	 * @param orderNo
	 */
	public String unlocked(String orderNo,boolean isAgain){
		CloseableHttpClient httpclient = HttpClients.custom().build();
		CloseableHttpResponse response = null;
		try {
			//cookie
			synchronized (this) {
				if(StringUtils.isEmpty(tbCookie)){
					Query query=new Query();
					query.addCriteria(Criteria.where("source").is("TB"));
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					tbCookie=cookie.getCookie();
				}
			}
			HttpGet httpGet = new HttpGet("https://jipiao.fliggy.com/agent/mockAction.do?action=notice_ajax_action&event_submit_do_seller_lock_order=1&orderId="+orderNo+"&lockUser=%C9%CF%C9%CF%C7%A7%BA%BD%B7%FE");
			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(10000).setConnectTimeout(10000).build();  
			httpGet.setConfig(requestConfig); 
			httpGet.addHeader("Accept-Encoding", "gzip, deflate");
			httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
			httpGet.addHeader("Accept", "application/json, text/plain, */*");
			httpGet.addHeader("Cache-Control", "no-cache");
			httpGet.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpGet.addHeader("Connection", "keep-alive");
			httpGet.addHeader("Host", "jipiao.fliggy.com");
			httpGet.addHeader("Origin","http://jpebook.ly.com");
			httpGet.addHeader("cookie", tbCookie.toString());
			httpGet.addHeader("Pragma", "no-cache");
			httpGet.addHeader("Referer", "http://jipiao.fliggy.com/agent/tripSellerOrderList.htm?beginCreateDate=+2020-03-10+&endCreateDate=+2020-04-10+&beginDepDate=&endDepDate=&processLatestBeginDate=&processLatestEndDate=&vos=-1&passengerName=&tcOrderId=&orderId=923815442615&bookCode=&alipayTradeNo=&telephone=&ticketNo=&pnrCode=&stockType=-1&orderTypeCode=-1&page=1");
			httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");
			response = httpclient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			//System.out.println(EntityUtils.toString(response.getEntity(), "UTF-8"));
			// 判断响应信息是否正确
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();	        
				String result = EntityUtils.toString(entity, "UTF-8");
				return result;
			}else{//登录过期
				tbCookie=null;
				log.info("TB的cookie过期了");
				try {
					SendQQMsgUtil.send("TB的cookie过期了");
				} catch (Exception e) {

				}
				if(isAgain){
					return lockedOrder(orderNo, false);
				}
			}
		}  catch (Exception e) {
			e.printStackTrace();
			tbCookie=null;
			if(isAgain){
				return lockedOrder(orderNo, false);
			}
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}


	/**
	 * 订单装换
	 * @param
	 * @param orderSource
	 * @param orderShop
	 * @return
	 */
	private OrderVO orderJsonToOrderVO(TripOrder tripOrder,String orderSource,String orderShop){
		try {
			
				TripBaseInfo baseinfo = tripOrder.getBaseInfo();
				OrderVO orderVo=new OrderVO();
				orderVo.setOrderNo(String.valueOf(baseinfo.getOrderId()));
				orderVo.setStatus(String.valueOf(baseinfo.getStatus()));

				Order order=new Order();
				String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				order.setOrderNo(String.valueOf(baseinfo.getOrderId()));
				order.setcOrderNo(String.valueOf(baseinfo.getOrderId()));
				order.setOrderSource(orderSource);
				order.setOrderShop(orderShop);
				order.setCreateBy("system");
				order.setStatus(WebConstant.ORDER_NO_TICKET); // 未出票 
				order.setPayStatus(WebConstant.PAYMENT_ALREADY); // 已支付
				order.setInterfaceImport(WebConstant.INTERFACE_IMPORT_YES);
				order.setUpdateTicketStatus(WebConstant.UPDATE_TICKET_NO_DISABLE);
				order.setcAddDate(createDate);
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(SDF.parse(order.getcAddDate()));
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				order.setLastPrintTicketTime(DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
				order.setTripType(String.valueOf(baseinfo.getTripType()));
				order.setTotalPrice(new BigDecimal(baseinfo.getTotalPrice()).divide(new BigDecimal(100)).toString());
				orderVo.setOrder(order);
				//如果包含儿童，在单独的数组里，要区分
				List<TripFlightInfo> flightInfos = tripOrder.getFlightInfos();
				
				List<TripFlightInfo> havaPassengers=new ArrayList<TripFlightInfo>();
				Map<Long, TripFlightInfo> metaflightMap=new HashMap<Long, TripFlightInfo>();
				for(TripFlightInfo info:flightInfos){
					if(info.getSegmentType()==0){
						havaPassengers.add(info);
					}
					if(!metaflightMap.containsKey(info.getSegmentType())){
						metaflightMap.put(info.getSegmentType(), info);
					}
				}
				
				Collection<TripFlightInfo> values = metaflightMap.values();
				List<Flight> flightList=new ArrayList<Flight>();
				Iterator<TripFlightInfo> iterator = values.iterator();
				while(iterator.hasNext()){
					TripFlightInfo tripFlightInfo = iterator.next();
					Flight flight=new Flight();
					flight.setOrderNo(order.getOrderNo());
					flight.setFlightNo(tripFlightInfo.getFlightNo());
					flight.setAirlineCode(tripFlightInfo.getAirlineCode());
					flight.setDepCityCode(tripFlightInfo.getDepAirportCode());
					flight.setArrCityCode(tripFlightInfo.getArrAirportCode());
					String[]  depTimes=DateFormatUtils.format(SDF.parse(tripFlightInfo.getDepTime()), "yyyy-MM-dd HH:mm:ss").split(" ");
					String[]  arrTimes=DateFormatUtils.format(SDF.parse(tripFlightInfo.getArrTime()), "yyyy-MM-dd HH:mm:ss").split(" ");
					flight.setFlightDepDate(depTimes[0]);
					flight.setFlightArrDate(arrTimes[0]);
					flight.setDepTime(depTimes[1]);
					flight.setArrTime(arrTimes[1]);
					flight.setOrderSource(order.getOrderSource());
					flight.setOrderShop(order.getOrderShop());
					flight.setCreateBy(order.getCreateBy());
					flight.setCreateDate(createDate);
					flight.setCabin(tripFlightInfo.getPassengers().get(0).getCabinCode());
					flightList.add(flight);			
				}
				orderVo.setFlightList(flightList);
				Flight flight=flightList.get(0);
				order.setFlightNo(flight.getFlightNo());
				order.setAirlineCode(flight.getAirlineCode());
				order.setDepCityCode(flight.getDepCityCode());
				order.setArrCityCode(flight.getArrCityCode());
				order.setFlightDate(flight.getFlightDepDate());
				order.setCabin(flight.getCabin());

				List<Passenger> passengerList=new ArrayList<Passenger>();
				BigDecimal allTax=new BigDecimal(0);
				BigDecimal allAisinoPrice=new BigDecimal(0);
				for(TripFlightInfo fi:havaPassengers){
					List<TripFlightPassenger> passengers = fi.getPassengers();
					for(TripFlightPassenger person:passengers){
						Passenger passenger=new Passenger();
						passenger.setOrderNo(order.getOrderNo());
						passenger.setName(person.getName());
						passenger.setCertNo(person.getCertNo());
						passenger.setBirthday(person.getBirthday());
						passenger.setCabin(person.getCabinCode());
						passenger.setFlightNo(flight.getFlightNo());
						passenger.setAirlineCode(flight.getAirlineCode());
						passenger.setDepCityCode(flight.getDepCityCode());
						passenger.setArrCityCode(flight.getArrCityCode());
						passenger.setFlightDepDate(flight.getFlightDepDate());
						passenger.setFlightArrDate(flight.getFlightArrDate());
						passenger.setDepTime(flight.getDepTime());
						passenger.setArrTime(flight.getArrTime());
						try {
							passenger.setPnr(person.getPnr());
						} catch (Exception e1) {
							passenger.setPnr("");
						}
						passenger.setTax(person.getTax()/100+"");
						if(StringUtils.isEmpty(order.getPnr())){
							order.setPnr(passenger.getPnr());
						}
						if(StringUtils.isEmpty(order.getPolicyType())){
							order.setPolicyRemark(person.getMemo());
							try {
								String string = JSONObject.fromObject(person.getExtra()).getString("outId");
								order.setPolicyType(string);
							} catch (Exception e) {
								order.setPolicyType(person.getMemo());
							}
						}
						try {
							passenger.setTicketNo(person.getTicketNo());
						} catch (Exception e) {
							passenger.setTicketNo("");
						}
						passenger.setFee(person.getFee()/100+"");
						passenger.setTicketPrice(new BigDecimal(fi.getTicketPrice()).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
						passenger.setSellPrice(new BigDecimal(person.getPrice()).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
						passenger.setActualPrice(new BigDecimal(passenger.getSellPrice()).add(new BigDecimal(passenger.getFee()).add(new BigDecimal(passenger.getTax()))).toString());
						passenger.setTicketStatus(WebConstant.NO_TICKET);
						passenger.setStatus(WebConstant.NO_TICKET);
						passenger.setOrderSource(order.getOrderSource());
						passenger.setOrderShop(order.getOrderShop());
						passenger.setOrderNo(order.getOrderNo());
						passenger.setPolicyType(order.getPolicyType());
						passenger.setCreateBy(order.getCreateBy());
						passenger.setCreateDate(createDate);
						if ("0".equals(String.valueOf(person.getPassengerType()))) {
							passenger.setPassengerType(WebConstant.PASSENGER_TYPE_ADULT);
						} else if("1".equals(String.valueOf(person.getPassengerType()))){
							passenger.setPassengerType(WebConstant.PASSENGER_TYPE_CHILD);
						}else{
							passenger.setPassengerType(WebConstant.PASSENGER_TYPE_BABY);
						}
						String type=String.valueOf(person.getCertType());
						if ("0".equals(type)) {//身份证
							passenger.setCertType("0");
						} else if ("1".equals(type)) {
							passenger.setCertType("1");
						} else if ("3".equals(type)) {
							passenger.setCertType("3");
						} else if ("4".equals(type)) {
							passenger.setCertType("4");
						} else if ("5".equals(type)) {
							passenger.setCertType("5");
						} else if ("6".equals(type)) {
							passenger.setCertType("6");
						}else if ("10".equals(type)) {
							passenger.setCertType("10");
						}else if ("11".equals(type)) {
							passenger.setCertType("11");
						}else {
							passenger.setCertType("9");
						}
						allTax=new BigDecimal(passenger.getTax()).add(allTax);
						allAisinoPrice=new BigDecimal(passenger.getTicketPrice()).add(allAisinoPrice);
						passengerList.add(passenger);
					}
				}
				orderVo.setPassengetList(passengerList);
				order.setPassengerCount(passengerList.size()+"");
				order.setTotalTicketPrice(allAisinoPrice.toString());
				order.setTotalTax(allTax.toString());
				return orderVo;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
