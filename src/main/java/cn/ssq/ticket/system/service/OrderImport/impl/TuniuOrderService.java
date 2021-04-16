package cn.ssq.ticket.system.service.OrderImport.impl;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.importBean.*;
import cn.ssq.ticket.system.service.ChangeService;
import cn.ssq.ticket.system.service.OrderImport.OrderImportService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.RefundService;
import cn.ssq.ticket.system.util.*;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 途牛平台服务类
 */
@Service("tuniuOrderService")
public class TuniuOrderService implements OrderImportService{
	
	private static int timeout=60000;
	
	private static Logger log = LoggerFactory.getLogger(TuniuOrderService.class);

	private static String apiurl = "http://flight.api.tuniu.cn/opi";

	private static String  ticketUrl="https://www.tuniu.cn/restful/common/api";

	/*private static CloseableHttpClient httpClient;

	private static CloseableHttpClient httpClient2;*/

	//待出票订单队列
	public static LimitQueue<String> status1 = new LimitQueue<String>(200);

	//出票成功订单队列
	public static LimitQueue<String> status4 = new LimitQueue<String>(200);

	//出票失败订单队列
	public static LimitQueue<String> status5 = new LimitQueue<String>(200);

	//private static Object o=new Object();

	private static Map<String, String> cookieMap=new HashMap<String, String>();

	private static MongoTemplate mongnTemplate;

	private static OrderService orderService;

	private static RefundService refundService;

	private static ChangeService changeService;

	@Autowired
	public  void setRefundService(RefundService refundService) {
		TuniuOrderService.refundService = refundService;
	}

	@Autowired
	public void setChangeService(ChangeService changeService) {
		TuniuOrderService.changeService = changeService;
	}

	@Autowired
	public void setOrderService(OrderService orderService) {
		TuniuOrderService.orderService = orderService;
	}

	@Autowired
	public void setMongnTemplate(MongoTemplate mongnTemplate) {
		TuniuOrderService.mongnTemplate = mongnTemplate;
	}


	/**
	 * 导入待出票订单
	 */
	@Override
	public List<OrderVO> batchImportOrders(String orderSource,
			String orderShop, String status)
					throws Exception {

		String apiKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "apiKey");
		String secretKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "secretKey");
		String validVendorIds=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "validVendorIds");
		Assert.notNull(apiKey, "apiKey不能为空");

		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.HOUR_OF_DAY, -2);
		String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");

		String data=getMetadata(apiKey, secretKey, bDate, eDate, status, 1, 100, getHttpClient2(), validVendorIds);
		//System.out.println(data);
		List<OrderVO> orderVoList = processMetadata(data);
		return orderVoList;
	}

	/**
	 * 获取平台出票失败的订单
	 */
	public static void getFailOrder(String orderShop){
		String apiKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "apiKey");
		String secretKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "secretKey");
		String validVendorIds=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "validVendorIds");
		Assert.notNull(apiKey, "apiKey不能为空");

		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		String data=getMetadata(apiKey, secretKey, bDate, eDate, "5", 1, 100, getHttpClient2(), validVendorIds);
		Gson gson=new Gson();
		ListRespon listRespon=gson.fromJson(data, ListRespon.class);
		if(listRespon==null){
			return;
		}
		if(listRespon.getSuccess().equals("false")){
			return ;
		}else{
			Datai datai=listRespon.getData();
			for(Row row:datai.getRows()){
				if(status5.contains(row.getId())){
					continue;
				}
				orderService.updateStatus("4", row.getOrderId());
				status5.offer(row.getId());	
			}
		}
	}

	/**
	 * 获取平台客票异常的订单
	 */
	public static void getExceptionOrder(){
		String apiKey=DictUtils.getDictCode("order_import_cfgtun", "apiKey");
		String secretKey=DictUtils.getDictCode("order_import_cfgtun", "secretKey");
		String validVendorIds=DictUtils.getDictCode("order_import_cfgtun", "validVendorIds");
		Assert.notNull(apiKey, "apiKey不能为空");

		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		String bDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		String data=getMetadata(apiKey, secretKey, bDate, eDate, "14", 1, 100, getHttpClient2(), validVendorIds);
		Gson gson=new Gson();
		ListRespon listRespon=gson.fromJson(data, ListRespon.class);
		if(listRespon==null){
			return;
		}
		if(listRespon.getSuccess().equals("false")){
			return ;
		}else{
			Datai datai=listRespon.getData();
			for(Row row:datai.getRows()){
				orderService.updateStatus("5", row.getId());
			}
		}
	}

	/**
	 * 获取途牛订单
	 * @param addTimeFrom
	 * @param addTimeTo
	 * @param status  0、全部  3、待出票 4、出票成功 5、出票失败 6、处理中
	 * @param start 返回结果的页码
	 * @param limit 单页返回的记录条数
	 * @param httpClient
	 * @return
	 */
	public static String getMetadata(String apiKey,String secretKey,String addTimeFrom,String addTimeTo,String status,int start,int limit,CloseableHttpClient httpClient,String validVendorIds){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		HttpPost httpPost = new HttpPost(apiurl+"/v1/order/ticket/list");
		httpPost.setHeader("Content-Type","application/json");	

		//参数
		Map<String, String> obj = new TreeMap<String, String>();
		obj.put("apiKey", apiKey);
		obj.put("timestamp", time);
		obj.put("addTimeFrom", addTimeFrom);
		obj.put("addTimeTo", addTimeTo);
		obj.put("validVendorIds", validVendorIds);
		obj.put("status", status);
		obj.put("start", start+"");
		obj.put("limit", limit+"");

		List<Map.Entry<String, String>> mappingList = new ArrayList<Map.Entry<String, String>>(obj.entrySet());
		String str = secretKey;
		for (Map.Entry<String, String> mapping : mappingList) {
			str += mapping.getKey() + mapping.getValue();
		}
		str += secretKey;
		String sign = null;
		try {
			sign = Md5Util.Md5Encode(str);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		obj.put("sign", sign);

		JSONObject jsonObject = JSONObject.fromObject(obj);
	
		CloseableHttpResponse response=null;
		// 设置参数到请求对象中，发送请求，获取数据
		try {
			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			//InputStream instreams = entity.getContent();
			String jsonBase64 = EntityUtils.toString(entity);
			String json = Base64Util.getFromBase64(jsonBase64);
			/*System.out.println("=========================途牛平台订单数据=======================");
			System.out.println(json.substring(0, 35));
			System.out.println("============================================================");*/
			response.close();
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {  
			try {
				httpClient.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			httpPost.abort();
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
	 * 验证票号
	 * @param passengerList
	 * @throws Exception
	 */
	public static String verifyTicketNo(List<Passenger> passengerList,String orderShop,String orderNo)
			throws Exception {
		String apiKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "apiKey");
		String secretKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "secretKey");
		String validVendorIds=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "validVendorIds");
		Assert.notNull(apiKey, "apiKey不能为空");
		List<TickSubConfirmParam> paramList=new ArrayList<TickSubConfirmParam>();
		for(Passenger passenger:passengerList){
			TickSubConfirmParam param=new TickSubConfirmParam();
			param.setId(Integer.valueOf(passenger.getcChildrenId()));
			param.setPnrCode(passenger.getPnr());
			param.setTicketNo(passenger.getTicketNo().trim());
			paramList.add(param);
		}
		String confirm = confirm(apiKey, secretKey, getHttpClient2(), validVendorIds, paramList,orderNo);
		return Base64Util.getFromBase64(confirm);
	}

	/**
	 * 出票单确认
	 * @param apiKey
	 * @param secretKey
	 * @param httpClient
	 * @param validVendorIds
	 * @param param
	 * @param orderNo
	 * @return
	 */
	public static String confirm(String apiKey,String secretKey,CloseableHttpClient httpClient,String validVendorIds,List<TickSubConfirmParam> param,String orderNo){
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sf.format(new Date());
		HttpPost httpPost = new HttpPost(apiurl+"/v1/order/ticket/"+orderNo+"/confirm/");
		httpPost.setHeader("Content-Type","application/json");	
		Gson gson = new Gson();
		Map<String, Object> obj = new TreeMap<String, Object>();
		obj.put("apiKey", apiKey);
		obj.put("timestamp", time);	
		obj.put("operatorId", "69269");
		obj.put("operatorName", "易飞商旅");
		obj.put("validVendorIds",validVendorIds);
		obj.put("ticketSubConfirmParams", param);
		List<Map.Entry<String, Object>> mappingList = new ArrayList<Map.Entry<String, Object>>(obj.entrySet());
		String str = secretKey;
		for (Map.Entry<String, Object> mapping : mappingList) {
			str += mapping.getKey() + mapping.getValue();
		}
		str += secretKey;
		String sign = null;
		try {
			sign = Md5Util.Md5Encode(str);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		obj.put("sign", sign);
		String json = gson.toJson(obj);
		CloseableHttpResponse response=null;
		try {
			httpPost.setEntity(new StringEntity(json));
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity);
			response.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				httpClient.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			httpPost.abort();
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
	 * 获取平台退票单
	 * @throws Exception 
	 */
	public static void  getRefundOrder(String orderShop) throws Exception{
		String apiKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "apiKey");
		String secretKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "secretKey");
		String validVendorIds=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "validVendorIds");
		Assert.notNull(apiKey, "apiKey不能为空");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String addTimeTo=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.HOUR_OF_DAY, -3);
		String addTimeFrom=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		//参数
		Map<String, String> obj = new TreeMap<String, String>();
		obj.put("apiKey", apiKey);
		obj.put("timestamp", time);
		obj.put("addTimeFrom", addTimeFrom);
		obj.put("addTimeTo", addTimeTo);
		obj.put("validVendorIds", validVendorIds);
		obj.put("status", "3");
		obj.put("start", "1");
		obj.put("limit", "100");

		List<Map.Entry<String, String>> mappingList = new ArrayList<Map.Entry<String, String>>(obj.entrySet());
		String str = secretKey;
		for (Map.Entry<String, String> mapping : mappingList) {
			str += mapping.getKey() + mapping.getValue();
		}
		str += secretKey;
		String sign = null;
		sign = Md5Util.Md5Encode(str);

		obj.put("sign", sign);
		JSONObject jsonObject = JSONObject.fromObject(obj);
		HttpPost httpPost = new HttpPost(apiurl+"/v1/order/refund/list");
		httpPost.setHeader("Content-Type","application/json");	
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient2 = getHttpClient();
		try {
			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			response= httpClient2.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String jsonBase64 = EntityUtils.toString(entity);
			String jsonData = Base64Util.getFromBase64(jsonBase64);
		/*	System.out.println("=========================途牛平台退票单数据=======================");
			System.out.println(jsonData);
			System.out.println("================================================================");*/
			JSONObject jsonObj=JSONObject.fromObject(jsonData);
			if(jsonObj.getBoolean("success")){
				JSONObject data=jsonObj.getJSONObject("data");
				JSONArray refundArr=data.getJSONArray("rows");
				for(int i=0;i<refundArr.size();i++){
					JSONObject refundJson=refundArr.getJSONObject(i);
					String cOrderNo=refundJson.getString("ticketId");
					String retNo=refundJson.getString("id");
					if(status5.contains(retNo)){
						continue;
					}
					Order order = orderService.getOrderBycOrderNo(cOrderNo);
					if(order==null){
						continue;
					}
					JSONArray passengerArr = refundJson.getJSONArray("refundInfoList");
					for(int j=0;j<passengerArr.size();j++){
						JSONObject  passengerJson=passengerArr.getJSONObject(j);
						Refund refund=new Refund();
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
						refund.setcRealPrice(new BigDecimal(refundJson.getString("totalAmount")).doubleValue());
						refund.setAirRemState("0");
						refund.setcAppDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						refund.setPassengerName(passengerJson.getJSONObject("person").getString("passengerName"));
						refund.setTicketNo(passengerJson.getString("ticketNo"));
						refund.setPnr(order.getPnr());
						String refundItemType = refundJson.getString("refundType");
						if ("1".equals(refundItemType)){
							//自愿
							refund.setRefundType(WebConstant.REFUND_TYPE_ZIYUAN);
						}else{
							refund.setRefundType(WebConstant.REFUND_TYPE_NOZIYUAN);
						}
						refundService.autoCreateRefund(refund);
					}
					status5.offer(retNo);	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			httpClient2.close();
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
	public static void  getChangeOrder(String orderShop) throws Exception{
		String apiKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "apiKey");
		String secretKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "secretKey");
		String validVendorIds=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "validVendorIds");
		Assert.notNull(apiKey, "apiKey不能为空");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String addTimeTo=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.HOUR_OF_DAY, -2);
		String addTimeFrom=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		//参数
		Map<String, String> obj = new TreeMap<String, String>();
		obj.put("apiKey", apiKey);
		obj.put("timestamp", time);
		obj.put("addTimeFrom", addTimeFrom);
		obj.put("addTimeTo", addTimeTo);
		obj.put("validVendorIds", validVendorIds);
		obj.put("status", "0");
		obj.put("start", "1");
		obj.put("limit", "100");

		List<Map.Entry<String, String>> mappingList = new ArrayList<Map.Entry<String, String>>(obj.entrySet());
		String str = secretKey;
		for (Map.Entry<String, String> mapping : mappingList) {
			str += mapping.getKey() + mapping.getValue();
		}
		str += secretKey;
		String sign = null;
		sign = Md5Util.Md5Encode(str);

		obj.put("sign", sign);
		JSONObject jsonObject = JSONObject.fromObject(obj);
		HttpPost httpPost = new HttpPost(apiurl+"/v1/order/change/list");
		httpPost.setHeader("Content-Type","application/json");	
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient22 = getHttpClient();
		try {
			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			response= httpClient22.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String jsonBase64 = EntityUtils.toString(entity);
			String jsonData = Base64Util.getFromBase64(jsonBase64);
		/*	System.out.println("=========================途牛平台改签单数据=======================");
			System.out.println(jsonData);
			System.out.println("============================================================");*/
			JSONObject jsonObj=JSONObject.fromObject(jsonData);
			if(jsonObj.getBoolean("success")){
				JSONObject data=jsonObj.getJSONObject("data");
				JSONArray refundArr=data.getJSONArray("rows");
				for(int i=0;i<refundArr.size();i++){
					JSONObject row=refundArr.getJSONObject(i);
					JSONArray changeInfoArr=row.getJSONArray("changeInfoList");
					if(status5.contains(row.getString("id"))){
						continue;
					}
					JSONObject ticketOrder=row.getJSONObject("ticketOrder");
					String cOrderNo=ticketOrder.getString("id");
					Order order = orderService.getOrderBycOrderNo(cOrderNo);
					if(order==null){
						continue;
					}
					List<Change> list=new ArrayList<Change>();
					for(int j=0;j<changeInfoArr.size();j++){
						JSONObject changeInfo=changeInfoArr.getJSONObject(j);
						JSONObject newPerson=changeInfo.getJSONObject("newPerson");
						JSONObject orgResInfoVo=changeInfo.getJSONObject("orgResInfoVo");
						JSONObject newResInfoVo=changeInfo.getJSONObject("newResInfoVo");

						Change change=new Change();
						change.setOrderNo(order.getOrderNo());
						change.setOrderId(order.getOrderId());
						change.setOrderShop(order.getOrderShop());
						change.setOrderSource(order.getOrderSource());
						change.setNewCOrderNo(row.getString("id"));
						change.setPassengerName(newPerson.getString("passengerName"));
						change.setTktNo(changeInfo.getString("ticketNo"));
						change.setRevenuePrice(new BigDecimal(changeInfo.getDouble("changeFee")).add(new BigDecimal(changeInfo.getDouble("upgradeFee"))).toString());
						change.setsPnr(changeInfo.getString("orgPnrCode"));
						change.setsAirlineCode(orgResInfoVo.getString("flightNo").substring(0,2));
						change.setsFlightNo(orgResInfoVo.getString("flightNo"));
						change.setsArrCityCode(orgResInfoVo.getString("arriveAirportCode"));
						change.setsDepCityCode(orgResInfoVo.getString("departAirportCode"));
						change.setsFlightDate(orgResInfoVo.getString("departureDate"));
						change.setsCabin(orgResInfoVo.getString("seatCode"));
						change.setCabin(newResInfoVo.getString("seatCode"));
						change.setPnr(changeInfo.getString("pnrCode"));
						change.setFlightNo(newResInfoVo.getString("flightNo"));
						change.setFlightDate(newResInfoVo.getString("departureDate"));
						change.setChangeDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						change.setCreateBy("SYSTEM");
						if(4==row.getInt("status")){
							change.setState(WebConstant.CHANGE_ALREADY);
						}else if(5==row.getInt("status")){
							change.setState(WebConstant.CHANGE_REFUSE);
						}else{	
							change.setState(WebConstant.CHANGE_UNTREATED);
						}
						list.add(change);
					}
					changeService.saveChanges(list);
					status5.offer(row.getString("id"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status5.clear();
			throw e;
		}finally{
			httpClient22.close();
			httpPost.abort();
			if(response!=null){
				response.close();
			}
		}
	}

	/**
	 * 订单状态查询
	 * @param orderNo
	 * @return
	 */
	public static Integer checkOrderStatus(String orderNo,String orderShop){
		String apiKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "apiKey");
		String secretKey=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "secretKey");
		String validVendorIds=DictUtils.getDictCode("order_import_cfgtun"+orderShop, "validVendorIds");
		Assert.notNull(apiKey, "apiKey不能为空");
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sf.format(new Date());
		HttpPost httpPost = new HttpPost(apiurl+"/v1/order/ticket/"+orderNo+"/status");
		httpPost.setHeader("Content-Type","application/json");	
		Map<String, Object> obj = new TreeMap<String, Object>();
		obj.put("apiKey", apiKey);
		obj.put("timestamp", time);	
		obj.put("validVendorIds",validVendorIds);
		List<Map.Entry<String, Object>> mappingList = new ArrayList<Map.Entry<String, Object>>(obj.entrySet());
		String str = secretKey;
		for (Map.Entry<String, Object> mapping : mappingList) {
			str += mapping.getKey() + mapping.getValue();
		}
		str += secretKey;
		String sign = null;
		try {
			sign = Md5Util.Md5Encode(str);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		obj.put("sign", sign);
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		CloseableHttpResponse response=null;
		 CloseableHttpClient httpClient22 = getHttpClient2();
		try {
			httpPost.setEntity(new StringEntity(json));
			response =httpClient22.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity);
			JSONObject jsonResult=JSONObject.fromObject(Base64Util.getFromBase64(content));
			if(!jsonResult.getBoolean("success")){
				return null;
			}
			JSONObject statusInfo=jsonResult.getJSONObject("data");
			Integer status=statusInfo.getInt("status");
			return status;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				httpClient22.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			httpPost.abort();
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
	 * 处理获取的元数据，封装orderVo
	 * @param data
	 * @param
	 * @return
	 */
	public static List<OrderVO> processMetadata(String data){
		try {
			Gson gson=new Gson();
			ListRespon listRespon=gson.fromJson(data, ListRespon.class);
			if(listRespon==null){
				return null;
			}
			if(listRespon.getSuccess().equals("false")){
				return null;
			}else{
				List<OrderVO> orderList =  new LinkedList<OrderVO>();
				Datai datai=listRespon.getData();
				for(Row row:datai.getRows()){
					if(status1.contains(row.getOrderId())){
						continue;
					}
					Cabin cabin = row.getCabinList().get(0);
					OrderVO orderVo=new OrderVO();
					orderVo.setOrderNo(row.getOrderId());
					orderVo.setcOrderNo(row.getId());
					Order order=new Order();
					order.setcOrderNo(row.getId());
					order.setOrderNo(row.getOrderId());
					order.setTotalPrice(row.getTotalAmount().toString());
					order.setPolicyId(row.getPolicyId()+"");
					order.setPolicyType(row.getPolicyDes());
					order.setPolicyRemark(row.getPolicyRemark());
					order.setFlightNo(cabin.getFlightNo());
					order.setAirlineCode(cabin.getFlightNo().substring(0, 2));
					order.setDepCityCode(cabin.getDepartAirportCode());
					order.setArrCityCode(cabin.getArriveAirportCode());
					order.setFlightDate(cabin.getDepartureDate());
					order.setCabin(cabin.getSeatCode());
					order.setcAddDate(row.getAddTime());
					Calendar calendar=Calendar.getInstance();
					calendar.add(Calendar.MINUTE, 10);
					String eDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
					if(StringUtils.isNotEmpty(order.getPolicyRemark()) && order.getPolicyRemark().contains("计时")) {
						order.setLastPrintTicketTime(eDate);
					}
					if(StringUtils.isEmpty(order.getLastPrintTicketTime())) {
						String productName = row.getProductName();
						if(StringUtils.isEmpty(productName) || productName.contains("无")) {
							order.setLastPrintTicketTime(eDate);
						}
						
					}
					orderVo.setOrder(order);
					Travel travel=new Travel();
					travel.setFlightNo(cabin.getFlightNo());
					travel.setAirlineCode(cabin.getFlightNo().substring(0, 2));
					travel.setDepCityCode(cabin.getDepartAirportCode());
					travel.setArrCityCode(cabin.getArriveAirportCode());
					travel.setFlightDepDate(cabin.getDepartureDate());
					travel.setFlightArrDate(cabin.getArrivalDate());
					travel.setDepTime(cabin.getDepartureTime());
					travel.setArrTime(cabin.getArrivalTime());
					travel.setCabin(cabin.getSeatCode());
					travel.setOrderNo(order.getOrderNo());
					orderVo.setTravel(travel);

					List<Passenger> passengerList=new ArrayList<Passenger>();
					for(PnrInfo pnr:row.getPnrInfoList()){
						Person p=pnr.getPerson();
						Passenger passenger=new Passenger();
						passenger.setOrderNo(order.getOrderNo());
						passenger.setName(p.getPassengerName());
						if(p.getIdentityType()==1)
							passenger.setCertType("0");
						else if(p.getIdentityType()==2)
							passenger.setCertType("1");
						else if(p.getIdentityType()==3)
							passenger.setCertType("3");
						else if(p.getIdentityType()==4)
							passenger.setCertType("6");
						else if(p.getIdentityType()==7)
							passenger.setCertType("5");
						else if(p.getIdentityType()==8)
							passenger.setCertType("4");
						else 
							passenger.setCertType("9");
						passenger.setCertNo(p.getIdentityNo());
						if (1==p.getPersonType()) {
							passenger.setPassengerType(WebConstant.PASSENGER_TYPE_ADULT);
							passenger.setCabin(cabin.getSeatCode());
						} else if (2==p.getPersonType()) {
							passenger.setPassengerType(WebConstant.PASSENGER_TYPE_CHILD);
						} else {
							passenger.setPassengerType(WebConstant.PASSENGER_TYPE_BABY);
						}
						passenger.setBirthday(p.getBirthday());
						passenger.setFlightNo(cabin.getFlightNo());
						passenger.setAirlineCode(cabin.getFlightNo().substring(0, 2));
						passenger.setDepCityCode(cabin.getDepartAirportCode());
						passenger.setArrCityCode(cabin.getArriveAirportCode());
						passenger.setFlightDepDate(cabin.getDepartureDate());
						passenger.setFlightArrDate(cabin.getArrivalDate());
						passenger.setDepTime(cabin.getDepartureTime());
						passenger.setArrTime(cabin.getArrivalTime());
						passenger.setTax(pnr.getTaxFee());
						passenger.setFee("0");
						passenger.setTicketPrice(pnr.getTicketPrice().toString());
						passenger.setSellPrice(pnr.getSalePrice().toString());
						passenger.setPnr(pnr.getPnrCode());
						passenger.setcChildrenId(pnr.getId()+"");
						passenger.setActualPrice(pnr.getCostPrice().toString());
						passenger.setTicketStatus(WebConstant.NO_TICKET);
						passenger.setStatus(WebConstant.NO_TICKET);
						if(StringUtils.isBlank(order.getPnr())){
							order.setPnr(pnr.getPnrCode());
						}
						if(StringUtils.isBlank(order.getTotalTax())){
							order.setTotalTax(pnr.getTaxFee());
						}
						passengerList.add(passenger);
					}
					orderVo.setPassengetList(passengerList);

					List<Flight> flightList=new ArrayList<Flight>();
					for(Cabin cabi:row.getCabinList()){
						Flight flight=new Flight();
						flight.setOrderNo(order.getOrderNo());
						flight.setFlightNo(cabi.getFlightNo());
						if(StringUtils.isBlank(order.getFlightNo())){
							order.setFlightNo(cabi.getFlightNo());
						}
						flight.setAirlineCode(cabi.getFlightNo().substring(0, 2));
						flight.setDepCityCode(cabi.getDepartAirportCode());
						flight.setArrCityCode(cabi.getArriveAirportCode());
						flight.setFlightDepDate(cabi.getDepartureDate());
						flight.setFlightArrDate(cabi.getArrivalDate());
						flight.setDepTime(cabi.getDepartureTime());
						flight.setArrTime(cabi.getArrivalTime());
						flight.setCabin(cabi.getSeatCode());
						flightList.add(flight);
					}
					orderVo.setFlightList(flightList);
					if(flightList.size()>1){
						order.setTripType(WebConstant.FLIGHT_TYPE_GOBACK);
					}else{
						order.setTripType(WebConstant.FLIGHT_TYPE_ONEWAY);
					}
					orderList.add(orderVo);
					
				}
				return orderList;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("封装订单元数据失败...");
		}
		return null;
	}

	/**
	 * 更新本地订单票号
	 * @param
	 */
	public static void updateTickNo(List<Order> orderList){
		if(orderList.size()==0){
			return;
		}
		for(Order order:orderList){
			JSONObject jsonObject = getTicketNoByOid(order.getcOrderNo(),order.getOrderShop(),order.getOrderNo(),true);
			if(jsonObject==null){
				continue;
			}
			Gson gson=new Gson();
			JSONArray jsonArray = jsonObject.getJSONArray("items");
			if(jsonArray.size()>0){
				List<Passenger> passengerList=new ArrayList<Passenger>();
				for(int i=0;i<jsonArray.size();i++){
					Item item=gson.fromJson(jsonArray.getString(i), Item.class);
					Person person=item.getPerson();
					String ticketNo=item.getTicketNo();
					String printTicketCabin=jsonObject.getJSONArray("flights").getJSONObject(0).getString("seatCode");
					Passenger passanger=new Passenger();
					passanger.setTicketNo(ticketNo);
					passanger.setOrderNo(order.getOrderNo());
					passanger.setPrintTicketCabin(printTicketCabin);
					passanger.setCertNo(person.getIdentityNo());
					passengerList.add(passanger);
				}
				orderService.updateTicketNo(order.getOrderNo(),passengerList);
			}
		}		
	}

	/**
	 * 根据订单号获取票号
	 * @param Oid
	 */
	public static JSONObject getTicketNoByOid(String Oid,String orderShop,String orderNo,boolean isAgain){
		Query query=new Query();
		query.addCriteria(Criteria.where("source").is("TN"+orderShop));
		if(StringUtils.isBlank(cookieMap.get(orderShop))){
			CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
			JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
			for(int i=0;i<jsonArray.size();i++){
				JSONObject json=jsonArray.getJSONObject(i);
				if("JSESSIONIDNB".equals(json.getString("name"))){
					String cookieValue=json.getString("value");
					if(StringUtils.isBlank(cookieValue)){
						log.info("途牛获取票号cookie为空。");
						return null;
					}
					cookieMap.put(orderShop, cookieValue);
					break;
				}
			}
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String jsonParam=String.format("{\"r\":0.2434188312002692,\"id\":%s,\"type\":1,\"method\":\"POST\",\"sysFlag\":\"AOP\",\"url\":\"/aop/nb/order/front/ticket/detail\"}", Oid);
		String base64Str=Base64Util.generateBase64Str(jsonParam);
		HttpGet get = new HttpGet(ticketUrl+"?"+base64Str);
		try {
			get.addHeader("accept-encoding", "gzip, deflate");
			get.addHeader("accept-language", "zh-CN,zh");
			get.addHeader("cache-control", "no-cache");
			get.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			get.addHeader("Accept", "text/plain, */*");
			get.addHeader("cookie", "version_page=20191101105833; JSESSIONIDNB="+cookieMap.get(orderShop)+"; JSESSIONIDBB="+cookieMap.get("cookie")+"; lang=zh-CN; _pk_id.3.2a8b=1971e0e37c810423.1573197836.9.1573533902.15; _pk_ses.3.2a8b=*");
			get.addHeader("pragma", "no-cache");
			get.addHeader("x-requested-with", "XMLHttpRequest");
			get.addHeader("referer", "https://www.tuniu.cn/nbooking/main.html");
			get.addHeader("Connection", "Keep-Alive");
			get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");
			response = httpClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK){				
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity,"UTF-8");
				JSONObject data = JSONObject.fromObject(Base64Util.getFromBase64(result));
				if("未登录".equals(data.getString("msg"))){//登录过期，更换cookie
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
					for(int i=0;i<jsonArray.size();i++){
						JSONObject json=jsonArray.getJSONObject(i);
						if("JSESSIONIDNB".equals(json.getString("name"))){
							String cookieValue=json.getString("value");
							cookieMap.put(orderShop, cookieValue);
							break;
						}
					}
					if(isAgain){
						return getTicketNoByOid(Oid, orderShop,orderNo,false);
					}
				}
				if(!data.getBoolean("success")){
					return null;
				}
				JSONObject jsonObject=data.getJSONObject("data");
				int status=jsonObject.getInt("status");
				if(status==5){//出票失败的
					orderService.updateStatus(WebConstant.ORDER_CANCEL, orderNo);
					return null;
				}
				if(status!=4){//非出票完成状态暂不处理
					return null;
				}
				return jsonObject;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				httpClient.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				get.abort();
				if(response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	/**
	 * 根据订单状态
	 * @param Oid
	 */
	public static String getStatusByOid(String Oid,String orderShop,boolean isAgain){
		Query query=new Query();
		query.addCriteria(Criteria.where("source").is("TN"+orderShop));
		if(StringUtils.isBlank(cookieMap.get(orderShop))){
			CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
			JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
			for(int i=0;i<jsonArray.size();i++){
				JSONObject json=jsonArray.getJSONObject(i);
				if("JSESSIONIDNB".equals(json.getString("name"))){
					String cookieValue=json.getString("value");
					if(StringUtils.isBlank(cookieValue)){
						log.info("途牛获取票号cookie为空。");
						return null;
					}
					cookieMap.put(orderShop, cookieValue);
					break;
				}
			}
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String jsonParam=String.format("{\"r\":0.2434188312002692,\"id\":%s,\"type\":1,\"method\":\"POST\",\"sysFlag\":\"AOP\",\"url\":\"/aop/nb/order/front/ticket/detail\"}", Oid);
		String base64Str=Base64Util.generateBase64Str(jsonParam);
		HttpGet get = new HttpGet(ticketUrl+"?"+base64Str);
		try {
			get.addHeader("accept-encoding", "gzip, deflate");
			get.addHeader("accept-language", "zh-CN,zh");
			get.addHeader("cache-control", "no-cache");
			get.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			get.addHeader("Accept", "text/plain, */*");
			get.addHeader("cookie", "version_page=20191101105833; JSESSIONIDNB="+cookieMap.get(orderShop)+"; JSESSIONIDBB="+cookieMap.get("cookie")+"; lang=zh-CN; _pk_id.3.2a8b=1971e0e37c810423.1573197836.9.1573533902.15; _pk_ses.3.2a8b=*");
			get.addHeader("pragma", "no-cache");
			get.addHeader("x-requested-with", "XMLHttpRequest");
			get.addHeader("referer", "https://www.tuniu.cn/nbooking/main.html");
			get.addHeader("Connection", "Keep-Alive");
			get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");
			response = httpClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK){				
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity,"UTF-8");
				JSONObject data = JSONObject.fromObject(Base64Util.getFromBase64(result));
				if("未登录".equals(data.getString("msg"))){//登录过期，更换cookie
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
					for(int i=0;i<jsonArray.size();i++){
						JSONObject json=jsonArray.getJSONObject(i);
						if("JSESSIONIDNB".equals(json.getString("name"))){
							String cookieValue=json.getString("value");
							cookieMap.put(orderShop, cookieValue);
							break;
						}
					}
					if(isAgain){
						return getStatusByOid(Oid, orderShop, false);
					}
				}
				if(!data.getBoolean("success")){
					return null;
				}
				JSONObject jsonObject=data.getJSONObject("data");
				String status=jsonObject.getString("status");
				return status;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				httpClient.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				get.abort();
				if(response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public static JSONObject lock(String cOrderNo,String orderShop,boolean isAgain){
		Query query=new Query();
		query.addCriteria(Criteria.where("source").is("TN"+orderShop));
		if(StringUtils.isBlank(cookieMap.get(orderShop))){
			CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
			JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
			for(int i=0;i<jsonArray.size();i++){
				JSONObject json=jsonArray.getJSONObject(i);
				if("JSESSIONIDNB".equals(json.getString("name"))){
					String cookieValue=json.getString("value");
					if(StringUtils.isBlank(cookieValue)){
						return null;
					}
					cookieMap.put(orderShop, cookieValue);
					break;
				}
			}
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String jsonParam=String.format("{\"r\":0.2434188312002692,\"id\":%s,\"type\":1,\"method\":\"POST\",\"sysFlag\":\"AOP\",\"url\":\"/aop/nb/order/front/ticket/lock\"}", cOrderNo);
		String base64Str=Base64Util.generateBase64Str(jsonParam);
		HttpGet get = new HttpGet(ticketUrl+"?"+base64Str);
		try {
			get.addHeader("accept-encoding", "gzip, deflate");
			get.addHeader("accept-language", "zh-CN,zh");
			get.addHeader("cache-control", "no-cache");
			get.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			get.addHeader("Accept", "text/plain, */*");
			get.addHeader("cookie", "version_page=20191101105833; JSESSIONIDNB="+cookieMap.get(orderShop)+"; JSESSIONIDBB="+cookieMap.get("cookie")+"; lang=zh-CN; _pk_id.3.2a8b=1971e0e37c810423.1573197836.9.1573533902.15; _pk_ses.3.2a8b=*");
			get.addHeader("pragma", "no-cache");
			get.addHeader("x-requested-with", "XMLHttpRequest");
			get.addHeader("referer", "https://www.tuniu.cn/nbooking/main.html");
			get.addHeader("Connection", "Keep-Alive");
			get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");
			response = httpClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK){				
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity,"UTF-8");
				JSONObject data = JSONObject.fromObject(Base64Util.getFromBase64(result));
				if("用户尚未登录".equals(data.getString("msg"))){//登录过期，更换cookie
					cookieMap.put(orderShop, "");
					try {
						SendQQMsgUtil.send("途牛的cookie过期了");
					} catch (Exception e) {
						
					}
					if(isAgain){
						lock(cOrderNo, orderShop, false);
					}
				}
				return data;
			}
		} catch (Exception e) {
			e.printStackTrace();
			cookieMap.put(orderShop, "");
			if(isAgain){
				lock(cOrderNo, orderShop, false);
			}
		}finally {
			try {
				httpClient.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			try {
				get.abort();
				if(response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}



	public static JSONObject unlock(String cOrderNo,String orderShop,boolean isAgain){
		synchronized (log) {	
			if(StringUtils.isBlank(cookieMap.get(orderShop))){
				Query query=new Query();
				query.addCriteria(Criteria.where("source").is("TN"+orderShop));
				CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
				JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
				for(int i=0;i<jsonArray.size();i++){
					JSONObject json=jsonArray.getJSONObject(i);
					if("JSESSIONIDNB".equals(json.getString("name"))){
						String cookieValue=json.getString("value");
						if(StringUtils.isBlank(cookieValue)){
							return null;
						}
						cookieMap.put(orderShop, cookieValue);
						break;
					}
				}
			}
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String jsonParam=String.format("{\"r\":0.2434188312002692,\"id\":%s,\"type\":1,\"method\":\"POST\",\"sysFlag\":\"AOP\",\"url\":\"/aop/nb/order/front/ticket/unlock\"}", cOrderNo);
		String base64Str=Base64Util.generateBase64Str(jsonParam);
		HttpGet get = new HttpGet(ticketUrl+"?"+base64Str);
		try {
			get.addHeader("accept-encoding", "gzip, deflate");
			get.addHeader("accept-language", "zh-CN,zh");
			get.addHeader("cache-control", "no-cache");
			get.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			get.addHeader("Accept", "text/plain, */*");
			get.addHeader("cookie", "version_page=20191101105833; JSESSIONIDNB="+cookieMap.get(orderShop)+"; JSESSIONIDBB="+cookieMap.get("cookie")+"; lang=zh-CN; _pk_id.3.2a8b=1971e0e37c810423.1573197836.9.1573533902.15; _pk_ses.3.2a8b=*");
			get.addHeader("pragma", "no-cache");
			get.addHeader("x-requested-with", "XMLHttpRequest");
			get.addHeader("referer", "https://www.tuniu.cn/nbooking/main.html");
			get.addHeader("Connection", "Keep-Alive");
			get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");
			response = httpClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK){				
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity,"UTF-8");
				JSONObject data = JSONObject.fromObject(Base64Util.getFromBase64(result));
				if("用户尚未登录".equals(data.getString("msg"))){//登录过期，更换cookie
					cookieMap.put(orderShop, "");
					if(isAgain){
						unlock(cOrderNo, orderShop, false);
					}
				}
				return data;
			}
		} catch (Exception e) {
			e.printStackTrace();
			cookieMap.put(orderShop, "");
			if(isAgain){
				unlock(cOrderNo, orderShop, false);
			}
		}finally {
			try {
				httpClient.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			try {
				get.abort();
				if(response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	/**
	 * 确认退款
	 * @throws NoSuchAlgorithmException 
	 */
	public static JSONObject confirmRefund(String retNo,String orderShop) throws Exception{
		String apiKey=DictUtils.getDictCode("order_import_cfgtun", "apiKey");
		String secretKey=DictUtils.getDictCode("order_import_cfgtun", "secretKey");
		String validVendorIds=DictUtils.getDictCode("order_import_cfgtun", "validVendorIds");
		Assert.notNull(apiKey, "apiKey不能为空");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		//参数
		Map<String, String> obj = new TreeMap<String, String>();
		obj.put("apiKey", apiKey);
		obj.put("timestamp", time);
		obj.put("validVendorIds", validVendorIds);
		String str = secretKey;
		List<Map.Entry<String, String>> mappingList = new ArrayList<Map.Entry<String, String>>(obj.entrySet());
		for (Map.Entry<String, String> mapping : mappingList) {
			str += mapping.getKey() + mapping.getValue();
		}
		str += secretKey;
		String sign = null;
		sign = Md5Util.Md5Encode(str);
		obj.put("sign", sign);
		JSONObject jsonObject = JSONObject.fromObject(obj);
		HttpPost httpPost = new HttpPost(apiurl+"/v1/order/refund/"+retNo+"/confirm");
		httpPost.setHeader("Content-Type","application/json");	
		try {
			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			CloseableHttpResponse response= getHttpClient2().execute(httpPost);
			HttpEntity entity = response.getEntity();
			String jsonBase64 = EntityUtils.toString(entity);
			String jsonData = Base64Util.getFromBase64(jsonBase64);
			JSONObject jsonObj=JSONObject.fromObject(jsonData);
			response.close();
			return jsonObj;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}	
	}





	/**
	 * 设置代理
	 * @return
	 */public static CloseableHttpClient getHttpClient(){

		String proxyServer="14.152.95.93";
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyServer, 30000),new UsernamePasswordCredentials("ff53719", "ff53719"));
		HttpHost proxy = new HttpHost(proxyServer, 30000 );
		RequestConfig globalConfig = RequestConfig.custom() 
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout).setProxy(proxy)
				.build();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCredentialsProvider(credsProvider)
				.build();
	
		return httpClient;	 
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


}
