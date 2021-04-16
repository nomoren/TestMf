package cn.ssq.ticket.system.service.OrderImport.impl;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.pp.PPMSG;
import cn.ssq.ticket.system.service.ChangeService;
import cn.ssq.ticket.system.service.FlightService;
import cn.ssq.ticket.system.service.OrderImport.OrderImportService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.RefundService;
import cn.ssq.ticket.system.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 就旅行平台服务类
 */
@Service("jiuOrderService")
public class JiuOrderService implements OrderImportService{

	private static Logger log = LoggerFactory.getLogger(JiuOrderService.class);

	private static int timeout=60000;
	
	//private static CloseableHttpClient httpClient;

	//private static CloseableHttpClient httpClient2;

	private static StringBuilder jiuCookie=new StringBuilder();

	//待出票订单队列
	public static LimitQueue<String> status1 = new LimitQueue<String>(200);

	//
	public static LimitQueue<String> status5 = new LimitQueue<String>(200);

	@Autowired
	private  MongoTemplate mongnTemplate;

	@Autowired
	private OrderService orderService;

	@Autowired
	private RefundService refundService;

	@Autowired
	private ChangeService changeService;

    @Autowired
    private FlightService flightService;

    /**
	 * 导单
	 */
	@Override
	public List<OrderVO> batchImportOrders(String orderSource,
			String orderShop, String status) throws Exception {
		List<OrderVO> list=new ArrayList<OrderVO>();
		try {
			List<String> orderNoList = this.getOrderNoList();
			if(orderNoList!=null){
				for(String orderNo:orderNoList){
                    if(status1.contains(orderNo)){
                        continue;
                    }
                    Thread.sleep(1000);
                    OrderVO  orderVo = getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, orderShop);
                    if(orderVo!=null){
                        list.add(orderVo);
					}
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return list;
	}



	/**
	 * 回填票号
	 * @throws Exception 
	 */
	public String verifyTicketNo(OrderVO orderVo) throws Exception{
		String user=DictUtils.getDictCode("order_import_jiu1", "user");
		String pass=DictUtils.getDictCode("order_import_jiu1", "pass");
		Assert.notNull(user, "user不能为空");

		List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
		paramsList.add(new BasicNameValuePair("domain", "ssx"));
		paramsList.add(new BasicNameValuePair("user", user));
		paramsList.add(new BasicNameValuePair("pass", pass));
		String xmlParam=generateTicketNoXmlParam(orderVo);
		if(StringUtils.isEmpty(xmlParam)){
			throw new RuntimeException();
		}
		paramsList.add(new BasicNameValuePair("orderdata", xmlParam));
		UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(paramsList,"utf-8");
		HttpPost httpPost = new HttpPost("http://fuwu.jiulvxing.com/autoOta/orderUpdate");
		//先解锁订单，如果需要

		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClientL();
		try {
			httpPost.setEntity(uefEntity);			
			response = httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity,"UTF-8");
				return metadata;
			}
		} catch (Exception e) {
			throw e;
		}finally{
			httpClient.close();
			httpPost.abort();
			if(response!=null){
				response.close();
			}
		}
		return null;
	}



	/**
	 * 更新票号
	 * @param orderList
	 */
	public void updateTickNo(List<Order> orderList){
		for(Order order:orderList){
			String orderShop=order.getOrderShop();
			String orderSource=order.getOrderSource();
			String orderNo=order.getOrderNo();
			OrderVO byOrderNo = getByOrderNo(orderNo, orderSource, orderShop);	
			if(byOrderNo!=null){
				String status=byOrderNo.getStatus();
				if("TICKET_OK".equals(status)){
					List<Passenger> passengetList = byOrderNo.getPassengetList();
					List<Passenger> list2=new ArrayList<Passenger>();
					for(Passenger p:passengetList){
						Passenger passenger=new Passenger();
						passenger.setTicketNo(p.getTicketNo());
						passenger.setOrderNo(orderNo);
						passenger.setPrintTicketCabin(p.getCabin());
						passenger.setCertNo(p.getCertNo());
						list2.add(passenger);
					}
					orderService.updateTicketNo(orderNo,list2);
				}else if("REFUND_NO_TICKET".equals(status)){
					orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
				}else if("CANCEL_OK".equals(status)){
					orderService.updateStatus(WebConstant.ORDER_CANCEL, orderNo);
				}
			}
		}


	}


	/**
	 * 获取待出票订单列表(接口文档的url)
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public List<String> getOrderNoList(){
		String user=DictUtils.getDictCode("order_import_jiu1", "user");
		String pass=DictUtils.getDictCode("order_import_jiu1", "pass");
		Assert.notNull(user, "user不能为空");
		HttpPost httpPost = new HttpPost("http://fuwu.jiulvxing.com/autoOta/orderExport");
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient();
		try {
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.MINUTE, 1);
			String end=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.MINUTE, -29);
			String start=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");

			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("domain", "ssx"));
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			paramsList.add(new BasicNameValuePair("type", "incr"));
			paramsList.add(new BasicNameValuePair("status", "4"));
			paramsList.add(new BasicNameValuePair("start", start));
			paramsList.add(new BasicNameValuePair("end", end));
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(paramsList,"utf-8");
			httpPost.setEntity(uefEntity);

			response=httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
			//System.out.println(metadata);
			List<String> list=this.xmlToOrderNoList(metadata);
			return list;
		} catch (Exception e) {
		    log.error("获取就旅行订单号列表异常",e);
		}finally{
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e1) {

                }
            }
            httpPost.abort();
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		return null;
	}

	/**
	 * 解析xml
	 * @param xmlContent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked" })
	public List<String> xmlToOrderNoList(String xmlContent) throws Exception{
		List<String> list = new ArrayList<String>();
		try{
			Document doc = DocumentHelper.parseText(xmlContent);
			Element rootElement = doc.getRootElement();
			String resultStatus = rootElement.attributeValue("status");
			if ("ok".equals(resultStatus)){
				List<Element> orderElementList = rootElement.elements("order");
				if (null != orderElementList && orderElementList.size() > 0){
					for (Element orderElement : orderElementList){
						String attributeValue = orderElement.attributeValue("orderNo");
						list.add(attributeValue);
					}
				} 
			}
		} catch (Exception e){
            log.info("解析就旅行xml失败："+xmlContent);
		} 
		return list;
	}

	/**
	 * 根据订单号获取订单详情(接口文档的url)
	 * @param orderNo
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public OrderVO getByOrderNo(String orderNo,String orderSource,String orderShop){
		String user=DictUtils.getDictCode("order_import_jiu1", "user");
		String pass=DictUtils.getDictCode("order_import_jiu1", "pass");
		Assert.notNull(user, "user不能为空");
		HttpPost httpPost = new HttpPost("http://fuwu.jiulvxing.com/autoOta/orderExport");
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient();
		try {
			//ssx  	123456
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("domain", "ssx"));
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			paramsList.add(new BasicNameValuePair("type", "exact"));
			paramsList.add(new BasicNameValuePair("orderNo", orderNo));
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(paramsList,"utf-8");
			httpPost.setEntity(uefEntity);

			response=httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
			//System.out.println(metadata);
			List<OrderVO> list=this.xmlToOrderList(metadata, orderSource, orderShop);
			if(list.size()>0){
				return list.get(0);
			}
			return null;
		} catch (Exception e) {
			log.error("获取"+orderNo+"信息异常",e);
			return null;
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpPost.abort();
			if(response!=null){
				try {
					response.close();
				} catch (IOException e1) {

				}
			}
		}
	}

	/**
	 * 航变录入
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public boolean subFlightChange(JSONObject param) throws Exception{
		String user=DictUtils.getDictCode("order_import_jiu1", "user");
		String pass=DictUtils.getDictCode("order_import_jiu1", "pass");
		Assert.notNull(user, "user不能为空");
		//String url="http://fuwu.jiulvxing.com/autoOta/flightChange?domain=ssx&user="+user+"&pass="+pass+"&data="+string;
		String url="http://fuwu.jiulvxing.com/autoOta/flightChange";
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient();
		try {
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("domain", "ssx"));
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			paramsList.add(new BasicNameValuePair("data", param.toString()));
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(paramsList,"utf-8");
			httpPost.setEntity(uefEntity);
			response=httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
			log.info("就旅行航变录入返回"+metadata);
			//{"ret":true,"errorMessage":null,"throwable":null,"data":null}
			JSONObject json=JSONObject.fromObject(metadata);
			return json.getBoolean("ret");
		} catch (Exception e) {
			return false;
		}finally{
			httpClient.close();
			httpPost.abort();
			if(response!=null){
				response.close();
			}
		}
	}

    /**
     * 获取订单状态
     * @param orderNo
     * @return
     * @throws Exception
     */
	public String getOrderStatus(String orderNo) throws Exception{
		String user=DictUtils.getDictCode("order_import_jiu1", "user");
		String pass=DictUtils.getDictCode("order_import_jiu1", "pass");
		Assert.notNull(user, "user不能为空");
		HttpPost httpPost = new HttpPost("http://fuwu.jiulvxing.com/autoOta/orderExport");
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient();
		try {
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("domain", "ssx"));
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			paramsList.add(new BasicNameValuePair("type", "exact"));
			paramsList.add(new BasicNameValuePair("orderNo", orderNo));
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(paramsList,"utf-8");
			httpPost.setEntity(uefEntity);
			String attributeValue="";
			for(int i=0;i<3;i++){	
				response=httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				Document doc = DocumentHelper.parseText(metadata);
				Element rootElement = doc.getRootElement();
				Element element = rootElement.element("order");
				attributeValue = element.attributeValue("status");
				if(StringUtils.isNotEmpty(attributeValue)){
					break;
				}
				
			}
			return attributeValue;
		} catch (Exception e) {
			return "";
		}finally{
			httpClient.close();
			httpPost.abort();
			if(response!=null){
				response.close();
			}
		}
	}


	/**
	 *查询订单列表
	 * @param orderNo
	 * @param
	 * @throws Exception 
	 */
	public List<Order> getByOrderNo2(String orderNo,boolean isAgain) throws Exception{
		List<Order> list=new ArrayList<Order>();
		List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String orderEndDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
		calendar.add(Calendar.DAY_OF_MONTH, -7);
		String orderStartDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
		parameterList.add(new BasicNameValuePair("domain","ssx"));
		parameterList.add(new BasicNameValuePair("startDate",orderStartDate));
		parameterList.add(new BasicNameValuePair("endDate",orderEndDate));
		parameterList.add(new BasicNameValuePair("listType","innerOptMergencyOrder"));
		parameterList.add(new BasicNameValuePair("guanwang","0"));
		parameterList.add(new BasicNameValuePair("passengerName",""));
		if(StringUtils.isNotEmpty(orderNo)){			
			parameterList.add(new BasicNameValuePair("userOrderNo",orderNo));
		}
		parameterList.add(new BasicNameValuePair("status","4"));
		parameterList.add(new BasicNameValuePair("expressStatus","0"));
		parameterList.add(new BasicNameValuePair("flightType","0"));
		parameterList.add(new BasicNameValuePair("serviceType","0"));
		parameterList.add(new BasicNameValuePair("limit","30"));
		parameterList.add(new BasicNameValuePair("pageIndex","1"));
		parameterList.add(new BasicNameValuePair("start","0"));
		parameterList.add(new BasicNameValuePair("lastIndex","1"));
		UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(parameterList,"utf-8");
		HttpPost httpPost = new HttpPost("https://fuwu.jiulvxing.com/servOrderList/list/new");
		httpPost.setEntity(uefEntity);
		httpPost.setHeader("Accept","application/json");
		httpPost.setHeader("Accept-Encoding","gzip, deflate, br");
		httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.8");
		httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.8");
		httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
		synchronized (this) {				
			if(StringUtils.isEmpty(jiuCookie.toString())){
				Query query=new Query();
				query.addCriteria(Criteria.where("source").is("JIU"));
				CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
				JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
				for(int i=0;i<jsonArray.size();i++){
					JSONObject json=jsonArray.getJSONObject(i);
					jiuCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
				}
			}
		}
		httpPost.setHeader("Cookie",jiuCookie.toString());
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			httpPost.setEntity(uefEntity);			
			response =httpClient2.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				if(!metadata.startsWith("{")){//cookie过期，重新获取更新
					jiuCookie.setLength(0);
					log.info("JIU 的cookie过期了....");
					if(isAgain){
						return getByOrderNo2(orderNo,  false);
					}
				}
				JSONObject json = JSONObject.fromObject(metadata);
				//System.out.println(json);
				if(json.getBoolean("ret")){					
					JSONArray jsonArr = json.getJSONObject("data").getJSONArray("orderList");
					for(int i=0;i<jsonArr.size();i++){
						JSONObject orderJson=jsonArr.getJSONObject(i);
						Order order=new Order();
						order.setOrderNo(orderJson.getString("userOrderNo"));
						list.add(order);
					}
				}
				response.close();
				return list;
			}
		} catch (Exception e) {
			if(isAgain){
				return getByOrderNo2(orderNo, false);
			}
			
			throw e;
		}finally{
			httpClient2.close();
			httpPost.abort();
			if(response!=null){
				response.close();
			}
		}
		return list;
	}


	/**
	 * 同步退票单
	 * @throws Exception
	 */
	public void sysncRefundOrder() throws Exception{
		String user=DictUtils.getDictCode("order_import_jiu1", "user");
		String pass=DictUtils.getDictCode("order_import_jiu1", "pass");
		Assert.notNull(user, "user不能为空");
		HttpPost httpPost = new HttpPost("http://fuwu.jiulvxing.com/autoOta/servOrderExport");
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient();
		try {
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("domain", "ssx"));
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			paramsList.add(new BasicNameValuePair("type", "incr"));
			paramsList.add(new BasicNameValuePair("servType", "0"));
			//paramsList.add(new BasicNameValuePair("orderNo", "5222640959864"));
			//paramsList.add(new BasicNameValuePair("servOrderId", "302106"));
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			String endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.HOUR_OF_DAY, -3);
			String startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			paramsList.add(new BasicNameValuePair("start", startDate));
			paramsList.add(new BasicNameValuePair("end", endDate));

			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(paramsList,"utf-8");
			httpPost.setEntity(uefEntity);
			response=httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
			JSONObject fromObject = JSONObject.fromObject(metadata);
			if(fromObject.getBoolean("ret")){
				JSONArray jsonArray = fromObject.getJSONArray("data");
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if("退款申请中".equals(jsonObject.getString("servOrderStatus"))){
						String retNo=jsonObject.getString("servOrderNo");
						if(status5.contains(retNo)){
							continue;
						}
						Order order = orderService.getOrderBycOrderNo(jsonObject.getString("orderNo"));
						if(order==null){
							continue;
						}
						JSONArray passengerArr = jsonObject.getJSONArray("servOrders");
						for(int j=0;j<passengerArr.size();j++){
							Refund refund=new Refund();
							JSONObject passenger=passengerArr.getJSONObject(j);
							refund.setOrderId(order.getOrderId());
							refund.setOrderNo(order.getOrderNo());
							refund.setRetNo(order.getOrderNo());
							refund.setOutOrderNo(retNo);
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
							refund.setcAppDate(jsonObject.getString("applyTime"));
							refund.setPassengerName(passenger.getString("passenger"));
							refund.setTicketNo(passenger.getString("ticketNum"));
							refund.setcRealPrice(new BigDecimal(passenger.getDouble("price")).doubleValue());
							refund.setPnr(order.getPnr());
							String refundType = passenger.getString("refundType");
							if ("自愿".equals(refundType)) {
								refund.setRefundType(WebConstant.REFUND_TYPE_ZIYUAN);
							}else {
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
		}finally{
			httpClient.close();
			httpPost.abort();
			if(response!=null){
				response.close();
			}
		}
	}

	
	/**
	 * 同步改签单
	 * @throws Exception
	 */
	public void sysncChangeOrder() throws Exception{
		String user=DictUtils.getDictCode("order_import_jiu1", "user");
		String pass=DictUtils.getDictCode("order_import_jiu1", "pass");
		Assert.notNull(user, "user不能为空");
		HttpPost httpPost = new HttpPost("http://fuwu.jiulvxing.com/autoOta/servOrderExport");
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient();
		try {
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("domain", "ssx"));
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			paramsList.add(new BasicNameValuePair("type", "incr"));
			paramsList.add(new BasicNameValuePair("servType", "1"));
			//paramsList.add(new BasicNameValuePair("orderNo", "5222640959864"));
			//paramsList.add(new BasicNameValuePair("servOrderId", "302106"));
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			String endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			calendar.add(Calendar.HOUR_OF_DAY,-24);
			String startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			paramsList.add(new BasicNameValuePair("start", startDate));
			paramsList.add(new BasicNameValuePair("end", endDate));
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(paramsList,"utf-8");
			httpPost.setEntity(uefEntity);
			response=httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
			JSONObject fromObject = JSONObject.fromObject(metadata);
			if(fromObject.getBoolean("ret")){
				JSONArray jsonArray = fromObject.getJSONArray("data");
				List<Change> list=new ArrayList<Change>();
				for(int i=0;i<jsonArray.size();i++){					
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if("支付待改期".equals(jsonObject.getString("servOrderStatus"))){
						String changeNo=jsonObject.getString("servOrderNo");
						if(status5.contains(changeNo)){
							continue;
						}
						Order order = orderService.getOrderBycOrderNo(jsonObject.getString("orderNo"));
						if(order==null){
							continue;
						}
						JSONArray passengerArr = jsonObject.getJSONArray("servOrders");
						for(int j=0;j<passengerArr.size();j++){
							JSONObject  passenger= passengerArr.getJSONObject(j);
							Change change=new Change();
							change.setOrderNo(order.getOrderNo());
							change.setOrderId(order.getOrderId());
							change.setOrderShop(order.getOrderShop());
							change.setOrderSource(order.getOrderSource());
							change.setNewCOrderNo(changeNo);
							change.setPassengerName(passenger.getString("passenger"));
							change.setTktNo("");
							change.setRevenuePrice(passenger.getString("price"));
							change.setUpgradeFee(passenger.getString("upgradeCabinFee"));
							change.setsPnr(order.getPnr());
							change.setsAirlineCode(order.getAirlineCode());
							change.setsFlightNo(order.getFlightNo());
							change.setsArrCityCode(order.getArrCityCode());
							change.setsDepCityCode(order.getDepCityCode());
							change.setsFlightDate(order.getFlightDate());
							change.setsCabin(order.getCabin());
							change.setCabin(passenger.getString("cabin"));
							change.setPnr("");
							change.setFlightNo(passenger.getString("flightNum"));
							change.setFlightDate(passenger.getString("depTime").split(" ")[0]);
							change.setChangeDate(jsonObject.getString("applyTime"));
							change.setCreateBy("SYSTEM");
							change.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
							change.setState(WebConstant.CHANGE_UNTREATED);
							list.add(change);
						}
						status5.offer(changeNo);	
					}
				}
				changeService.saveChanges(list);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			httpClient.close();
			httpPost.abort();
			if(response!=null){
				response.close();
			}
		}
	}



	

	/**
	 * 确认退款
	 * @param
	 * @return
	 * @throws Exception
	 */
	public JSONObject confirmRefund(Refund refund) {
		String user=DictUtils.getDictCode("order_import_jiu1", "user");
		String pass=DictUtils.getDictCode("order_import_jiu1", "pass");
		HttpPost httpPost = new HttpPost("http://fuwu.jiulvxing.com/autoOta/refundUpdate");
		
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient();
		try {
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("domain", "ssx"));
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			JSONObject data=new JSONObject();
			data.accumulate("orderNo", refund.getOrderNo());
			data.accumulate("servOrderId", refund.getOutOrderNo());
			//退票单(所有退票乘客)
			//List<Refund> refundPassenger = refundService.getRefundsByRetNo(refund.getOrderNo());
			StringBuilder sb=new StringBuilder();
			BigDecimal allRefund=new BigDecimal(0);
			//for(Refund r:refundPassenger){
				sb.append(refund.getPassengerName()).append(",");
				allRefund=allRefund.add(new BigDecimal(refund.getcRealPrice().doubleValue()));
			//}
			JSONObject price=new JSONObject();
			price.accumulate(sb.deleteCharAt(sb.length()-1).toString(),allRefund.toString());
            data.accumulate("price", price);
			paramsList.add(new BasicNameValuePair("data", com.alibaba.fastjson.JSONObject.toJSONString(data)));
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(paramsList,"utf-8");
			httpPost.setEntity(uefEntity);
			response=httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
            JSONObject jsonObject = JSONObject.fromObject(metadata);
            return jsonObject;
		} catch (Exception e) {
		    e.printStackTrace();
		}finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
				try {
					httpPost.abort();
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return  null;
	}


	/**
	 * 解析xml
	 * @param xmlContent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked" })
	public List<OrderVO> xmlToOrderList(String xmlContent,String orderSource,String orderShop) throws Exception{
		List<OrderVO> list = new ArrayList<OrderVO>();
		try{
			Document doc = DocumentHelper.parseText(xmlContent);
			Element rootElement = doc.getRootElement();
			String resultStatus = rootElement.attributeValue("status");
			if ("ok".equals(resultStatus)){
				List<Element> orderElementList = rootElement.elements("order");
				if (null != orderElementList && orderElementList.size() > 0){
					for (Element orderElement : orderElementList){
						OrderVO orderVo = orderElementToVO(orderElement, orderSource, orderShop);
						if(orderVo!=null){
							list.add(orderVo);
						}
					}
				} 
			}
		} catch (Exception e){
			throw e;
		} 
		return list;
	}

	/**
	 * 平台加锁
	 * @param orderNo
	 * @param
	 * @param isAgain 遇到异常是否重试
	 * @return
	 */
	public JSONObject locked(String orderNo,boolean isAgain){
		HttpGet httpGet = new HttpGet("https://fuwu.jiulvxing.com/servOrderDetail/orderLock/lock?domain=ssx&domain=ssx&orderNo="+orderNo+"&bizType=1");
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			httpGet.setHeader("Accept","application/json");
			httpGet.setHeader("Accept-Encoding","gzip, deflate, br");
			httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8");
			httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8");
			httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			httpGet.setHeader("Content-Type","application/x-www-form-urlencoded");
			synchronized (this) {				
				if(StringUtils.isEmpty(jiuCookie.toString())){
					Query query=new Query();
					query.addCriteria(Criteria.where("source").is("JIU"));
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
					for(int i=0;i<jsonArray.size();i++){
						JSONObject json=jsonArray.getJSONObject(i);
						jiuCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
					}
				}
			}
			httpGet.setHeader("Cookie",jiuCookie.toString());	
			response =httpClient2.execute(httpGet);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				JSONObject json=JSONObject.fromObject(metadata);
				return json;
			}else{
				/*if(isAgain){
					jiuCookie.setLength(0);
					return locked(orderNo, false);
				}else{
					try {
						SendQQMsgUtil.send("JIU的cookie过期了");
					} catch (Exception e) {

					}
				}*/
			}
		} catch (Exception e) {
			jiuCookie.setLength(0);
		}finally{
			httpGet.abort();
			try {
				httpClient2.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(response!=null){
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 平台解锁
	 * @param orderNo
	 * @param
	 * @param isAgain 遇到异常是否重试
	 * @return
	 */
	public JSONObject unlocked(String orderNo,boolean isAgain){
		HttpGet httpGet = new HttpGet("https://fuwu.jiulvxing.com/servOrderDetail/orderLock/forceUnlock?me=false&domain=ssx&domain=ssx&orderNo="+orderNo+"&bizType=1");
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			httpGet.setHeader("Accept","application/json");
			httpGet.setHeader("Accept-Encoding","gzip, deflate, br");
			httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8");
			httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8");
			httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			httpGet.setHeader("Content-Type","application/x-www-form-urlencoded");
			synchronized (this) {				
				if(StringUtils.isEmpty(jiuCookie.toString())){
					Query query=new Query();
					query.addCriteria(Criteria.where("source").is("JIU"));
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
					for(int i=0;i<jsonArray.size();i++){
						JSONObject json=jsonArray.getJSONObject(i);
						jiuCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
					}
				}
			}
			httpGet.setHeader("Cookie",jiuCookie.toString());	
			response =httpClient2.execute(httpGet);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				JSONObject json=JSONObject.fromObject(metadata);
				return json;
			}else{
				if(isAgain){
					jiuCookie.setLength(0);
					return locked(orderNo, false);
				}else{
					try {
						SendQQMsgUtil.send("JIU的cookie过期了");
					} catch (Exception e) {

					}
				}
			}
		} catch (Exception e) {
			jiuCookie.setLength(0);
			if(isAgain){
				return locked(orderNo,  false);
			}else{
				try {
					SendQQMsgUtil.send("JIU的cookie过期了");
				} catch (Exception e1) {

				}
			}
		}finally{
			try {
				httpClient2.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			httpGet.abort();
			if(response!=null){
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}



	/**
	 * 订单标签封装成vo
	 * @param orderElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static OrderVO orderElementToVO(Element orderElement,String orderSource, String orderShop){
		if (null == orderElement){
			return null;
		}
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		OrderVO vo = new OrderVO();
		vo.setOrderNo(orderElement.attributeValue("orderNo"));
		vo.setStatus(orderElement.attributeValue("status"));
		vo.setReamrkStr(orderElement.attributeValue("ticketDeadLine"));

		Order order=new Order();
		vo.setOrder(order);
		order.setOrderNo(vo.getOrderNo());
		order.setcOrderNo(vo.getOrderNo());
		order.setLastPrintTicketTime(orderElement.attributeValue("ticketDeadLine"));
		order.setPnr(orderElement.attributeValue("pnr"));
		order.setBigPnr("");
		order.setTotalTax(new BigDecimal(orderElement.attributeValue("fuelTax")).add(new BigDecimal(orderElement.attributeValue("constructionFee"))).toString());
		order.setCreateBy("system");
		order.setStatus(WebConstant.ORDER_NO_TICKET);//未出票
		order.setPayStatus(WebConstant.PAYMENT_ALREADY); // 已支付
		order.setInterfaceImport(WebConstant.INTERFACE_IMPORT_YES);
		order.setUpdateTicketStatus(WebConstant.UPDATE_TICKET_NO_DISABLE);
		order.setPolicyRemark("");
		try {			
			order.setTicType(orderElement.attributeValue("jlxSource"));
			order.setWebsiteOrderSource(orderElement.attributeValue("source"));
		} catch (Exception e) {
		}
		order.setOrderSource(orderSource);
		order.setOrderShop(orderShop);
		order.setTotalPrice(orderElement.attributeValue("allPrice"));
		order.setRelationName(orderElement.attributeValue("contact"));
		order.setRelationMobile(orderElement.attributeValue("contactMob"));
		order.setcAddDate(orderElement.attributeValue("createTime"));
		order.setPolicyType(orderElement.attributeValue("policyCode"));
		order.setPolicyRemark(orderElement.attributeValue("cabinNote"));
		if(Boolean.valueOf(orderElement.attributeValue("needPS"))){
			order.setTraTotalPrice(orderElement.attributeValue("xcdPrice"));
			//order.setTraStatus("是");
		}else{
			//order.setTraStatus("否");
		}

		// 航班信息
		Flight firstFlightVO=null;
		List<Element> flightElements = orderElement.elements("flight");
		List<Flight> fList = new ArrayList<Flight>();
		if (null != flightElements && flightElements.size() > 0){
			for (Element flightElement : flightElements){
				Flight flight = new Flight();
				flight.setOrderNo(order.getOrderNo());
				flight.setOrderSource(order.getOrderSource());
				flight.setOrderShop(order.getOrderShop());
				flight.setSegmentType("0");
				flight.setAirlineCode(flightElement.attributeValue("code").substring(0,2));
				flight.setFlightNo(flightElement.attributeValue("code"));
				flight.setDepCityCode(flightElement.attributeValue("dep"));
				flight.setArrCityCode(flightElement.attributeValue("arr"));
				flight.setFlightDepDate(flightElement.attributeValue("depDay"));
				flight.setDepTime(flightElement.attributeValue("depTime"));
				flight.setArrTime(flightElement.attributeValue("arrTime"));
				flight.setCabin(flightElement.attributeValue("cabin"));
				flight.setPrintTicketCabin(flightElement.attributeValue("cabin"));
				flight.setCreateDate(createDate);
				if (null == firstFlightVO){
					firstFlightVO = flight;
				}
				fList.add(flight);
			}
			vo.setFlightList(fList);
			order.setTripType(1 == fList.size() ? WebConstant.FLIGHT_TYPE_ONEWAY:WebConstant.FLIGHT_TYPE_GOBACK);
		} 

		// 乘客信息
		List<Element> passengerElements = orderElement.elements("passenger");
		if (null != passengerElements && passengerElements.size() > 0){
			List<Passenger> pList = new ArrayList<Passenger>();
			for (Element passengerElement : passengerElements){
				Passenger passenger = new Passenger();
				passenger.setOrderNo(order.getOrderNo());
				passenger.setOrderSource(order.getOrderSource());
				passenger.setOrderShop(order.getOrderShop());
				passenger.setTicketStatus(WebConstant.NO_TICKET);
				passenger.setStatus(WebConstant.NO_TICKET);
				passenger.setCreateDate(createDate);
				passenger.setPolicyType(order.getPolicyType());
				passenger.setName(passengerElement.attributeValue("name"));
				String ticketNo=passengerElement.attributeValue("eticketNum");
				if(StringUtils.isNotBlank(ticketNo) && !ticketNo.contains("-")){
					ticketNo=ticketNo.substring(0, 3)+"-"+ticketNo.substring(3,ticketNo.length());
				}
				passenger.setTicketNo(ticketNo);
				passenger.setBirthday(passengerElement.attributeValue("birthday"));
				passenger.setCertNo(passengerElement.attributeValue("cardNum"));
				String type=passengerElement.attributeValue("cardType");
				if ("NI".equals(type)){
					passenger.setCertType("0");
				} else if ("PP".equals(type)) {
					passenger.setCertType("1");
				} else if ("ID".equals(type)){
					passenger.setCertType("9");
				} else if ("HX".equals(type)) {
					passenger.setCertType("4");
				} else if ("TB".equals(type)){
					passenger.setCertType("5");
				} else if ("GA".equals(type)) {
					passenger.setCertType("6");
				} else if ("HY".equals(type)){
					passenger.setCertType("7");
				} else{
					passenger.setCertType("9");
				}
				String ageType = passengerElement.attributeValue("ageType");
				if ("0".equals(ageType)){
					passenger.setTicketPrice(orderElement.attributeValue("viewPrice"));
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_ADULT);
					passenger.setPnr(orderElement.attributeValue("pnr"));
					if(WebConstant.FLIGHT_TYPE_ONEWAY.equals(order.getTripType())){
						passenger.setCabin(firstFlightVO.getCabin());
					}else{
						StringBuilder sb=new StringBuilder();
						for(Flight f:vo.getFlightList()){
							sb.append(f.getCabin()).append("/");
						}
						passenger.setCabin(sb.deleteCharAt(sb.length()-1).toString());
					}
				} else if ("1".equals(ageType)){
					passenger.setTicketPrice(orderElement.attributeValue("childPrice"));
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_CHILD);
					passenger.setPnr(orderElement.attributeValue("cpnr"));
					if(WebConstant.FLIGHT_TYPE_ONEWAY.equals(order.getTripType())){
						passenger.setCabin("是"+firstFlightVO.getCabin()+"吗");
					}else{
						StringBuilder sb=new StringBuilder();
						for(Flight f:vo.getFlightList()){
							sb.append(f.getCabin()).append("/");
						}
						passenger.setCabin("是"+sb.deleteCharAt(sb.length()-1).toString()+"吗");
					}
				} else{
					passenger.setTicketPrice(orderElement.attributeValue("childPrice"));
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_BABY);
					passenger.setPnr(orderElement.attributeValue("cpnr"));
					if(WebConstant.FLIGHT_TYPE_ONEWAY.equals(order.getTripType())){
						passenger.setCabin("是"+firstFlightVO.getCabin()+"吗");
					}else{
						StringBuilder sb=new StringBuilder();
						for(Flight f:vo.getFlightList()){
							sb.append(f.getCabin()).append("/");
						}
						passenger.setCabin("是"+sb.deleteCharAt(sb.length()-1).toString()+"吗");
					}
				}
				if (null != firstFlightVO){
					passenger.setAirlineCode(firstFlightVO.getAirlineCode());
					passenger.setFlightNo(firstFlightVO.getFlightNo());
					passenger.setDepCityCode(firstFlightVO.getDepCityCode());
					passenger.setArrCityCode(firstFlightVO.getArrCityCode());
					passenger.setFlightDepDate(firstFlightVO.getFlightDepDate());
					passenger.setDepTime(firstFlightVO.getDepTime());
					passenger.setArrTime(firstFlightVO.getArrTime());
					passenger.setPrintTicketCabin(firstFlightVO.getCabin());
					order.setFlightNo(firstFlightVO.getFlightNo());
					order.setFlightDate(firstFlightVO.getFlightDepDate());
					order.setAirlineCode(firstFlightVO.getAirlineCode());
					order.setDepCityCode(firstFlightVO.getDepCityCode());
					order.setArrCityCode(firstFlightVO.getArrCityCode());
					order.setCabin(firstFlightVO.getCabin());
				}
				if (WebConstant.PASSENGER_TYPE_ADULT.equals(passenger.getPassengerType())){
					passenger.setFee(new BigDecimal(orderElement.attributeValue("constructionFee")).divide(new BigDecimal(fList.size())).toString());
					passenger.setTax(new BigDecimal(orderElement.attributeValue("fuelTax")).divide(new BigDecimal(fList.size())).toString());
				} else{
					passenger.setFee("0");
					passenger.setTax(orderElement.attributeValue("childFuelTax"));
				}

				passenger.setSellPrice(passengerElement.attributeValue("price"));
				// 实收金额 = 销售价 + 机建 + 燃油 
				passenger.setActualPrice(new BigDecimal(passenger.getSellPrice()).add(new BigDecimal(passenger.getFee()).add(new BigDecimal(passenger.getTax()))).toString());
				pList.add(passenger);
			}
			order.setPassengerCount(String.valueOf(pList.size()));
			vo.setPassengetList(pList);
		}
		return vo;
	}



	public  String convertPayStatus(String payStatus){
		if("WLALIPAY".equals(payStatus) || "ALIPAY".equals(payStatus)){//支付宝
			return "12";
		}else if("BILLPAY".equals(payStatus)){//快钱
			return "11";
		}else if("TENPAY".equals(payStatus)){//财付通
			return "13";
		}else if("UMPAY".equals(payStatus)){//联动优势
			return "21";
		}else if("QUNARPAY".equals(payStatus)){//用户余额
			return "24";
		}else if("UPOPPAY".equals(payStatus)){//银联预授权
			return "25";
		}else if("UPOPCPAY".equals(payStatus)){//银联无卡
			return "26";
		}else if("OFFLINE".equals(payStatus)){//线下支付
			return "5";
		}else{
			return "";
		}
	}

	/**
	 * 生成回填票号需要的xml参数
	 * @param orderVo
	 * @return
	 */
	public String generateTicketNoXmlParam(OrderVO orderVo){
		if(orderVo==null){
			return null;
		}
		Document doc=DocumentHelper.createDocument();
		Element rootElement = doc.addElement("OrderList");
		Element orderElement = rootElement.addElement("OrderDetail");
		orderElement.addAttribute("status", "2");
		orderElement.addAttribute("no", orderVo.getOrderNo().trim());
		orderElement.addAttribute("errorCode", "");
		orderElement.addAttribute("errorMsg", "");
		List<Passenger> passengerList=orderVo.getPassengetList();
		for(Passenger passenger:passengerList){
			Element passengerElement = orderElement.addElement("passenger");
			passengerElement.addAttribute("name", passenger.getName().trim());
			passengerElement.addAttribute("no", passenger.getTicketNo().trim());
			passengerElement.addAttribute("cano", passenger.getCertNo().trim());
		}
		Element issueticket = orderElement.addElement("issueticket");
		issueticket.addAttribute("ticketType", "1");//手工出票
		issueticket.addAttribute("ticketPlatform", "");
		issueticket.addAttribute("purchasePrice", "");
		return doc.asXML();
	}

    /**
     * 14代理
     * @return
     */
	public static CloseableHttpClient getHttpClient(){
		String proxyServer="14.152.95.93";//ConfigUtils.getParam("uploadProxyIp1");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();  
		credsProvider.setCredentials(new AuthScope(proxyServer, 30000),new UsernamePasswordCredentials("ff53719", "ff53719"));
		HttpHost proxy = new HttpHost(proxyServer, 30000 );
		RequestConfig globalConfig = RequestConfig.custom() 
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout).setProxy(proxy)
				.build(); 
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCredentialsProvider(credsProvider)  
				.build();

		return build;	 
	}

    /**
     * 121代理
     * @return
     */
	public static CloseableHttpClient getHttpClientL(){
		String proxyServer="121.201.33.102";//ConfigUtils.getParam("uploadProxyIp1");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();  
		credsProvider.setCredentials(new AuthScope(proxyServer, 30000),new UsernamePasswordCredentials("ff53719", "ff53719"));
		HttpHost proxy = new HttpHost(proxyServer, 30000 );
		RequestConfig globalConfig = RequestConfig.custom() 
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout).setProxy(proxy)
				.build(); 
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCredentialsProvider(credsProvider)  
				.build();

		return build;	 
	}
	
	public static CloseableHttpClient getHttpClient2(){
		
		RequestConfig globalConfig = RequestConfig.custom() 
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.build(); 
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig) 
				.build();

		return build;	 
	}

	public static String signature(Map<String, String> params) {
		Map<String, String> map = Maps.newTreeMap();
		map.putAll(params);
		String joinParams = Joiner.on("&").withKeyValueSeparator("=").join(map);
		return DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(joinParams.getBytes()).getBytes());
	}


    /**
     * 鹏鹏下单接口参数配置
     * @param orderVO
     * @param ticketPrice
     * @return
     */
    public PPMSG getPPMSG(OrderVO orderVO, String ticketPrice) {
        PPMSG p = new PPMSG();
        try {
            QueryWrapper<Flight> query = new QueryWrapper<Flight>();
            query.eq("order_id", orderVO.getOrderId());
            List<Flight> selectFlightInfo = flightService.getByQueryW(query);
            Flight flight = selectFlightInfo.get(0);
            p.setFlightDate(flight.getFlightDepDate());
            p.setFlightNo(flight.getFlightNo());
            p.setFromCity(flight.getDepCityCode());
            p.setFromDatetime(flight.getFlightDepDate() + " " + flight.getDepTime());
            p.setPnr(orderVO.getPnr());
            String bigPnr = orderVO.getBigPnr();
            if (StringUtils.isEmpty(bigPnr)) {
                String param = "cmd=rt_parse&pnr=" + orderVO.getPnr();
                try {
                    String result = EtermHttpUtil.cmd("apiyjsl3", param);
                    org.dom4j.Document bills = DocumentHelper.parseText(result);
                    org.dom4j.Element root = bills.getRootElement();
                    bigPnr = root.elementText("b_pnr");
                    if (StringUtils.isEmpty(bigPnr)) {
                        bigPnr = "O12345";
                    }
                    orderService.updateBigPnr(bigPnr, orderVO.getOrderId());
                } catch (DocumentException e) {
                    bigPnr = "O12345";
                }
            }
            p.setBigPnr(bigPnr);
            p.setTicketPrice(ticketPrice);
            p.setToCity(flight.getArrCityCode());
            p.setToDatetime(flight.getFlightDepDate() + " " + flight.getArrTime());
            StringBuffer name = new StringBuffer();
            StringBuffer certNo = new StringBuffer();
            StringBuffer passengerType = new StringBuffer();
            StringBuffer cardType = new StringBuffer();
            for (Passenger qp : orderVO.getPassengetList()) {
                if (!qp.getPassengerType().equals("0")) {
                    return null;
                }
                name.append("|" + qp.getName());
                certNo.append("|" + qp.getCertNo());
                passengerType.append("|" + 1);
                String type = qp.getCertType();
                if(type.equals("1")){
                    type = "PP";
                }else if(type.equals("0")){
                    type = "NI";
                }else {
                    type = "ID";
                }
                cardType.append("|" + type);
            }
            p.setPassengerName(name.toString().substring(1));
            p.setPassengerCard(certNo.toString().substring(1));
            p.setPassengerType(passengerType.toString().substring(1));
            p.setCardType(cardType.toString().substring(1));
            p.setSeatClass(flight.getCabin());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return p;
    }
	


}
