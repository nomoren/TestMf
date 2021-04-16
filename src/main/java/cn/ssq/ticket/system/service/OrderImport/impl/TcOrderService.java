package cn.ssq.ticket.system.service.OrderImport.impl;

import cn.ssq.ticket.system.OrderBatchImportSchedule.TongChengSchednule;
import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.importBean.TcBean.TcChange;
import cn.ssq.ticket.system.entity.pp.PPMSG;
import cn.ssq.ticket.system.service.ChangeService;
import cn.ssq.ticket.system.service.FlightService;
import cn.ssq.ticket.system.service.OrderImport.OrderImportService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.RefundService;
import cn.ssq.ticket.system.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 同程平台服务类
 */
@Service("tcOrderService")
public class TcOrderService implements OrderImportService{

	private static Logger log = LoggerFactory.getLogger(TcOrderService.class);

	private static int timeout=40000;
	
	/*private static CloseableHttpClient httpClient;

	private static CloseableHttpClient httpClient2;*/

	//待出票订单队列
	public static LimitQueue<String> status1 = new LimitQueue<String>(200);

	//已取消订单队列
	public static LimitQueue<String> status5 = new LimitQueue<String>(200);

	private static StringBuilder tcCookie=new StringBuilder();

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
	 * 导入待出票订单
	 */
	@Override
	public List<OrderVO> batchImportOrders(String orderSource,
			String orderShop, String status) throws Exception {
		List<OrderVO> list=new ArrayList<OrderVO>();
		String user=DictUtils.getDictCode("order_import_cfgtc", "user");
		String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
		String access_token=getToken();
		Assert.notNull(access_token, "access_token不能为空");

		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.HOUR_OF_DAY, -2);
		String startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");

		JSONObject param=new JSONObject();
		param.accumulate("startCreateTime", startDate);
		param.accumulate("endCreateTime", endDate);
		param.accumulate("orderStatus", status);
		param.accumulate("orderType", -1);

		String uri="/SupplierOrder/SearchOrder?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		String url = "https://flightopenapi.17u.cn/SupplierOrder/SearchOrder?t="+timestamp;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient2();
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			List<String> orderList = toOrderNoList(content);
			for(String orderNo:orderList){
				if(status1.contains(orderNo)){
					continue;
				}
				/*if("D".equals(tcOrderVO.getFlightWay())){//往返单
					orderVo = getOrderByOrderNo2(tcOrderVO.getOrderNo(), orderSource, orderShop);
				}*/
				OrderVO orderVo=null;
				try {
				    Thread.sleep(1000);
					orderVo = getOrderByOrderNo(orderNo, user, pass, orderSource, orderShop);
				} catch (Exception e) {
					continue;
				}
				if(orderVo!=null){					
					list.add(orderVo);	
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
		return list;
	}

	/**
	 * 票号验证
	 * @param passengerList
	 * @return
	 * @throws Exception
	 */
	public String verifyTicketNo(List<Passenger> passengerList) throws Exception{
		String user=DictUtils.getDictCode("order_import_cfgtc", "user");
		String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
		String access_token=DictUtils.getDictCodeNoCache("order_import_cfgtc", "access_token");
		Assert.notNull(access_token, "access_token不能为空");
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);

		String orderNo=passengerList.get(0).getOrderNo();
		JSONObject param=new JSONObject();
		param.accumulate("orderSerialNo", orderNo);
		JSONArray passengerArr=new JSONArray();
		for(Passenger passenger:passengerList){
			JSONObject p=new JSONObject();
			p.accumulate("passName", passenger.getName().trim());
			p.accumulate("eticketNo", passenger.getTicketNo().trim());
			if(StringUtils.isEmpty(passenger.getPnr())){
				p.accumulate("pnr", "000000");
			}else{				
				p.accumulate("pnr", passenger.getPnr());
			}
			passengerArr.add(p);
		}
		param.accumulate("ticketList", passengerArr);

		String uri="/SupplierOrder/TicketNotify?t="+timestamp;
		String url = "https://flightopenapi.17u.cn/SupplierOrder/TicketNotify?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient2();
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			return content;
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
	public void sysncChangeOrder(int changeType) throws Exception{
		String user=DictUtils.getDictCode("order_import_cfgtc", "user");
		String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
		String access_token=getToken();
		Assert.notNull(access_token, "access_token不能为空");

		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.HOUR_OF_DAY, -10);
		String startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");

		JSONObject param=new JSONObject();
		param.accumulate("startCreateTime", startDate);
		param.accumulate("endCreateTime", endDate);
		param.accumulate("applyType", -1);
		param.accumulate("changeStatus", changeType);//0 未处理
		

		String uri="/ChangeOrder/SearchOrder?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		String url = "https://flightopenapi.17u.cn/ChangeOrder/SearchOrder?t="+timestamp;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response =httpClient2 .execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			List<String> orderList = toOrderNoList(content);
			List<Change> list=new ArrayList<Change>();
			for(String changeSerialNo:orderList){
				if(status5.contains(changeSerialNo)){
					continue;
				}
				JSONObject changeDetail = this.getChangeDetail(changeSerialNo, user, pass);
				if(changeDetail==null||!changeDetail.getBoolean("isSuccess")){
					continue;
				}
				JSONObject data=changeDetail.getJSONObject("data");
				Order order = orderService.getOrderBycOrderNo(data.getString("orderSerialNo"));
				if(order==null){
					continue;
				}
				JSONArray passengerArr=data.getJSONArray("changeTicketList");
				for(int j=0;j<passengerArr.size();j++){
					JSONObject passenger = passengerArr.getJSONObject(j);
					if(passenger.getInt("ticketType")==1) {
						Change change=new Change();
						change.setOrderNo(order.getOrderNo());
						change.setOrderId(order.getOrderId());
						change.setOrderShop(order.getOrderShop());
						change.setOrderSource(order.getOrderSource());
						change.setNewCOrderNo(changeSerialNo);
						change.setPassengerName(passenger.getString("passName"));
						change.setTktNo(passenger.getString("eticketNo"));
						double changePrice=passenger.getDouble("changeFee")+passenger.getDouble("upgradeFee");
						change.setRevenuePrice(changePrice+"");
						change.setUpgradeFee(passenger.getString("upgradeFee"));
						change.setsPnr(passenger.getString("pnr"));
						change.setsAirlineCode(order.getAirlineCode());
						change.setsFlightNo(order.getFlightNo());
						change.setsArrCityCode(order.getArrCityCode());
						change.setsDepCityCode(order.getDepCityCode());
						change.setsFlightDate(order.getFlightDate());
						change.setsCabin(order.getCabin());
						change.setCabin(passenger.getJSONArray("changeSegmentList").getJSONObject(0).getString("cabinCode"));
						change.setFlightNo(passenger.getJSONArray("changeSegmentList").getJSONObject(0).getString("flightNo"));
						change.setFlightDate(passenger.getJSONArray("changeSegmentList").getJSONObject(0).getString("depTime").split(" ")[0]);
						change.setChangeDate(data.getString("createTime"));
						change.setCreateBy("SYSTEM");
						change.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						change.setState(WebConstant.CHANGE_UNTREATED);
						list.add(change);
					}
					
				}
				status5.offer(changeSerialNo);	
			}
			changeService.saveChanges(list);
		} catch (Exception e) {
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
	 * 同步退票单
	 * @throws Exception 
	 */
	public void sysncRefundOrder(int refundType) throws Exception{
		String user=DictUtils.getDictCode("order_import_cfgtc", "user");
		String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
		String access_token=getToken();
		Assert.notNull(access_token, "access_token不能为空");

		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.HOUR_OF_DAY, -2);
		String startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");

		JSONObject param=new JSONObject();
		param.accumulate("startCreateTime", startDate);
		param.accumulate("endCreateTime", endDate);
		param.accumulate("refundType", refundType);
		param.accumulate("refundStatus", 0);//未处理

		String uri="/RefundOrder/SearchOrder?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		String url = "https://flightopenapi.17u.cn/RefundOrder/SearchOrder?t="+timestamp;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response =httpClient2 .execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			List<String> orderList = toOrderNoList(content);
			for(String retNo:orderList){
				if(status5.contains(retNo)){
					continue;
				}
				JSONObject refundDetail = this.getRefundDetail(retNo,user,pass,false);//退票详情
				if(refundDetail==null||!refundDetail.getBoolean("isSuccess")){
					continue;
				}
				JSONObject data=refundDetail.getJSONObject("data");
				Order order = orderService.getOrderBycOrderNo(data.getString("orderSerialNo"));
				if(order==null){
					continue;
				}
				JSONArray passengerArr=data.getJSONArray("refundTicketList");
				for(int j=0;j<passengerArr.size();j++){
					Refund refund=new Refund();
					JSONObject passenger=passengerArr.getJSONObject(j);
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
					refund.setcAppDate(data.getString("createTime"));
					refund.setPassengerName(passenger.getString("passName"));
					refund.setTicketNo(passenger.getString("eticketNo"));
					refund.setcRealPrice(new BigDecimal(passenger.getDouble("refundAmount")).doubleValue());
					refund.setcRetPrice(passenger.getString("refundFee"));
					refund.setPnr(order.getPnr());
					String type = data.getString("refundType");
					if ("1".equals(type)) {
						refund.setRefundType(WebConstant.REFUND_TYPE_ZIYUAN);
					}else{
						refund.setRefundType(WebConstant.REFUND_TYPE_NOZIYUAN);
					}
					refundService.autoCreateRefund(refund);
				}
				status5.offer(retNo);	
			}
			response.close();
		} catch (Exception e) {
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
	 * 获取改期详情
	 * @param 
	 */
	public JSONObject getChangeDetail(String changeSerialNo,String user,String pass){
		CloseableHttpClient httpclient = getHttpClient2();
		CloseableHttpResponse response = null;
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
		String access_token=getToken();
		Assert.notNull(access_token, "access_token不能为空");

		JSONObject param=new JSONObject();
		param.accumulate("changeSerialNo", changeSerialNo);

		String uri="/ChangeOrder/GetOrderDetail?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		String url = "https://flightopenapi.17u.cn/ChangeOrder/GetOrderDetail?t="+timestamp;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			JSONObject json=JSONObject.fromObject(content);
			return json;
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
		return null;
	}
	
	
	/**
	 * 获取退票详情
	 * @param refundSerialNo
	 */
	public JSONObject getRefundDetail(String refundSerialNo,String user,String pass,boolean isAgain){
		CloseableHttpClient httpclient = getHttpClient2();
		CloseableHttpResponse response = null;
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
		String access_token=getToken();
		Assert.notNull(access_token, "access_token不能为空");

		JSONObject param=new JSONObject();
		param.accumulate("refundSerialNo", refundSerialNo);

		String uri="/RefundOrder/GetOrderDetail?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		String url = "https://flightopenapi.17u.cn/RefundOrder/GetOrderDetail?t="+timestamp;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			JSONObject json=JSONObject.fromObject(content);
			return json;
		}  catch (Exception e) {

		} finally {
			try {
				httpclient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
		return null;
	}

	/**
	 * 退票审核
	 * @param retNo 
	 * @param type 0拒绝 1同意
	 * @return
	 * @throws Exception 
	 */
	public JSONObject confirmRefund(String retNo,int type) {
		CloseableHttpClient httpclient = getHttpClient2();
		CloseableHttpResponse response = null;
        String user=DictUtils.getDictCode("order_import_cfgtc", "user");
        String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
        String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
        String access_token=getToken();
        JSONObject param=new JSONObject();
        param.accumulate("refundSerialNo", retNo);
        param.accumulate("isAuditSuccess", type);
        String uri="/RefundOrder/Audit?t="+timestamp;
        String sign = createSign(uri+param.toString(), pass+access_token);
        String url = "https://flightopenapi.17u.cn/RefundOrder/Audit?t="+timestamp;
        HttpPost httpPost = new HttpPost(url);
		try {
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("x-tg-token", access_token);
            httpPost.setHeader("x-tg-sign", sign);
            httpPost.setHeader("x-appkey", user);
            StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSONObject.fromObject(content);
            return jsonObject;
		}  catch (Exception e) {
		    e.printStackTrace();
		} finally {
            try {
                httpclient.close();
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
		return null;
	}

	/**
	 * 获取改签详情
	 * @param
	 */
	public JSONObject getChangeDetailOld(String changeSerialNo,String user,String pass){
		CloseableHttpClient httpclient = getHttpClient2();
		CloseableHttpResponse response = null;
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
		String access_token=getToken();
		Assert.notNull(access_token, "access_token不能为空");

		JSONObject param=new JSONObject();
		param.accumulate("changeSerialNo", changeSerialNo);

		String uri="/ChangeOrder/GetOrderDetail?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		String url = "https://flightopenapi.17u.cn/ChangeOrder/GetOrderDetail?t="+timestamp;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			JSONObject json=JSONObject.fromObject(content);
			return json;
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
		return null;
	}


	/**
	 * 同步订单票号
	 * @param
	 */
	public void updateTickNo(List<Order> orderList){
		String user=DictUtils.getDictCode("order_import_cfgtc", "user");
		String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
		String access_token=getToken();
		Assert.notNull(access_token, "access_token不能为空");
		for(Order order:orderList){
			try {
				OrderVO orderVo = getOrderByOrderNo(order.getOrderNo(), user, pass, order.getOrderSource(), order.getOrderShop());
				if(orderVo!=null){
					if("1".equals(orderVo.getStatus())){
						List<Passenger> passengetList = orderVo.getPassengetList();
						List<Passenger> list2=new ArrayList<Passenger>();
						for(Passenger p:passengetList){
							Passenger passenger=new Passenger();
							passenger.setTicketNo(p.getTicketNo());
							passenger.setOrderNo(p.getOrderNo());
							passenger.setPrintTicketCabin(p.getCabin());
							passenger.setCertNo(p.getCertNo());
							list2.add(passenger);
						}
						orderService.updateTicketNo(order.getOrderNo(),list2);
					}else if("4".equals(orderVo.getStatus())){
						orderService.updateStatus(WebConstant.REFUSE_ORDER_CANCEL, order.getOrderNo());
					}else if("3".equals(orderVo.getStatus())){
						if(WebConstant.ORDER_NO_TICKET.equals(order.getStatus())||WebConstant.ORDER_PRINT.equals(order.getStatus())){
							orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, order.getOrderNo());
						}
					}
					Order o=new Order();
					o.setOrderNo(order.getOrderNo());
					StringBuilder sb=new StringBuilder();
					if(StringUtils.isNotEmpty(orderVo.getLastPrintTicketTime())){
						sb.append(orderVo.getLastPrintTicketTime());
					}
					if(StringUtils.isNotEmpty(orderVo.getTicketRemark())){
						try {
							int count=Integer.parseInt(orderVo.getTicketRemark());
							if(count>0){
								sb.append(" 催出票("+orderVo.getTicketRemark()+")");
							}
						} catch (Exception e) {

						}
					}
					o.setLastPrintTicketTime(sb.toString());
					orderService.updateOrder(o);
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

	}

	/**
	 * 提取orderNo
	 * @param jsonStr
	 * @return
	 */
	private List<String> toOrderNoList(String jsonStr){
		List<String> list=new ArrayList<String>();
		JSONObject metaJson;
		try {
			metaJson=JSONObject.fromObject(jsonStr);
		} catch (Exception e) {
			return list;
		}
		if(!metaJson.getBoolean("isSuccess")){
			return list;
		}
		JSONArray jsonArray = metaJson.getJSONArray("data");
		for(int i=0;i<jsonArray.size();i++){
			String orderNo=jsonArray.getString(i);
			list.add(orderNo);
		}
		return list;
	}

	/**
	 * 提取orderNo2
	 * @param jsonStr
	 * @return
	 */
	private List<TcChange> getOrderNoList(String jsonStr){
		List<TcChange> list=new ArrayList<TcChange>();
		try {
			JSONObject json=JSONObject.fromObject(jsonStr);
			if(json.getBoolean("success")){
				JSONObject data=json.getJSONObject("data");
				if(data.getInt("totalCount")>0){
					JSONArray dataArr=data.getJSONArray("data");
					for(int i=0;i<dataArr.size();i++){
						TcChange tcChange=new TcChange();
						JSONObject obj=dataArr.getJSONObject(i);
						tcChange.setChangeSerialNo(obj.getString("changeSerialNo"));
						tcChange.setOrderSerialNo(obj.getString("orderSerialNo"));
						tcChange.setChangeStatusDesc(obj.getString("changeStatusDesc"));
						list.add(tcChange);
					}
				}
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return list;
	}


	/**
	 * 获取订单详情(文档接口)
	 * @param orderNo
	 * @param user
	 * @param pass
	 * @param orderSource
	 * @param orderShop
	 * @return
	 * @throws Exception
	 */
	public  OrderVO getOrderByOrderNo(String orderNo,String user,String pass,String orderSource,String orderShop) throws Exception{
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
		String access_token=getToken();
		Assert.notNull(access_token, "access_token不能为空");

		JSONObject param=new JSONObject();
		param.accumulate("orderSerialNo", orderNo);

		String uri="/SupplierOrder/GetOrderDetail?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		String url = "https://flightopenapi.17u.cn/SupplierOrder/GetOrderDetail?t="+timestamp;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response =httpClient2.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");	
			//System.out.println(content);
			OrderVO orderVo = orderJsonToOrderVO(content, orderSource, orderShop);
			return orderVo;
		} catch (Exception e) {
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
	 * 驳回
	 * @param orderNo 订单号
	 * @param reason 理由
	 * @return
	 * @throws Exception 
	 */
	public String refuseOrder(String orderNo,String reason) throws Exception{
		String user=DictUtils.getDictCode("order_import_cfgtc", "user");
		String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
		String access_token=DictUtils.getDictCodeNoCache("order_import_cfgtc", "access_token");
		Assert.notNull(access_token, "access_token不能为空");
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);

		JSONObject param=new JSONObject();
		param.accumulate("orderSerialNo", orderNo);
		param.accumulate("rejectReason", reason);

		String uri="/SupplierOrder/ApplyReject?t="+timestamp;
		String url = "https://flightopenapi.17u.cn/SupplierOrder/ApplyReject?t="+timestamp;
		String sign = createSign(uri+param.toString(), pass+access_token);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = getHttpClient2();
		try {
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			response.close();
			return content;
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
	 * 航变录入
	 * @throws Exception 
	 */
	public String subFlightChange(JSONObject param) throws Exception{
		String user=DictUtils.getDictCode("order_import_cfgtc", "user");
		String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
		String access_token=DictUtils.getDictCodeNoCache("order_import_cfgtc", "access_token");
		Assert.notNull(access_token, "access_token不能为空");
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);

		JSONObject jsonParam=new JSONObject();
		jsonParam.accumulate("addType", 0);
		jsonParam.accumulate("changeType", param.getString("changeType"));
		jsonParam.accumulate("changeReason", param.getString("changeReason"));
		jsonParam.accumulate("orgFlightNo", param.getString("orgFlightNo"));
		jsonParam.accumulate("orgStartPort", param.getString("orgStartPort"));
		jsonParam.accumulate("orgEndPort", param.getString("orgEndPort"));
		jsonParam.accumulate("orgDepTime", param.getString("orgDepTime"));
		jsonParam.accumulate("orgArrTime", param.getString("orgArrTime"));
		if(StringUtils.isNotEmpty(param.getString("newFlightNo"))){
			jsonParam.accumulate("newFlightNo", param.getString("newFlightNo"));
		}
		if(StringUtils.isNotEmpty(param.getString("newStartPort"))){
			jsonParam.accumulate("newStartPort", param.getString("newStartPort"));
		}
		if(StringUtils.isNotEmpty(param.getString("newEndPort"))){
			jsonParam.accumulate("newEndPort", param.getString("newEndPort"));
		}
		if(StringUtils.isNotEmpty(param.getString("newDepTime"))){
			jsonParam.accumulate("newDepTime", param.getString("newDepTime"));
		}
		if(StringUtils.isNotEmpty(param.getString("newArrTime"))){
			jsonParam.accumulate("newArrTime", param.getString("newArrTime"));
		}
		String uri="/FlightChange/Add?t="+timestamp;
		String url = "https://flightopenapi.17u.cn/FlightChange/Add?t="+timestamp;
		String sign = createSign(uri+jsonParam.toString(), pass+access_token);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("x-tg-token", access_token);
		httpPost.setHeader("x-tg-sign", sign);
		httpPost.setHeader("x-appkey", user);
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			StringEntity se = new StringEntity(jsonParam.toString(), "utf-8");
			httpPost.setEntity(se);
			response =httpClient2.execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			log.info("同程航变录入返回"+content);
			response.close();
			return content;
		} catch (Exception e) {
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
	 * 根据订单号查询订单(平台上抓的接口)
	 */
	public OrderVO getOrderByOrderNo2(String orderNo,String orderSource,String orderShop) throws Exception{
		//获取同程cookie
		if(StringUtils.isEmpty(tcCookie.toString())){
			Query query=new Query();
			query.addCriteria(Criteria.where("source").is("TC"));
			CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
			JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
			for(int i=0;i<jsonArray.size();i++){
				JSONObject json=jsonArray.getJSONObject(i);
				tcCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
			}
		}
		CloseableHttpClient httpclient = HttpClients.custom().build();
		CloseableHttpResponse response = null;
		try {
			//创建post请求
			HttpPost httpPost = new HttpPost("http://jpebook.ly.com/suppliersharing/SupplierOrder/GetOrderDetail");
			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(10000).setConnectTimeout(10000).build();  
			httpPost.setConfig(requestConfig); 
			httpPost.addHeader("Accept-Encoding", "gzip, deflate");
			httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
			httpPost.addHeader("Accept", "application/json, text/plain, */*");
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpPost.addHeader("Connection", "keep-alive");
			httpPost.addHeader("Host", "jpebook.ly.com");
			httpPost.addHeader("Origin","http://jpebook.ly.com");
			httpPost.addHeader("cookie", tcCookie.toString());
			httpPost.addHeader("Pragma", "no-cache");
			httpPost.addHeader("Referer", "http://jpebook.ly.com/suppliersharing/SupplierOrder");
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");

			JSONObject param=new JSONObject();
			param.accumulate("orderSerialNo", orderNo);
			httpPost.setHeader("Content-Type", "application/json");
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			// 判断响应信息是否正确
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();	        
				String result = EntityUtils.toString(entity, "UTF-8");
				OrderVO orderVo = orderJsonToOrderVO2(result, orderSource, orderShop);
				return orderVo;
			}else{//登录过期，更换cookie
				tcCookie.setLength(0);
				log.info("TC的cookie过期了");
			}
		}  catch (Exception e) {
			e.printStackTrace();
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
	 * 锁单
	 * @param orderNo
	 */
	public String lockedOrder(String orderNo,boolean isAgain){
		CloseableHttpClient httpclient = HttpClients.custom().build();
		CloseableHttpResponse response = null;
		try {
			//获取同程cookie
			synchronized (this) {
				if(StringUtils.isEmpty(tcCookie.toString())){
					Query query=new Query();
					query.addCriteria(Criteria.where("source").is("TC"));
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
					for(int i=0;i<jsonArray.size();i++){
						JSONObject json=jsonArray.getJSONObject(i);
						tcCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
					}
				}
			}
			//创建post请求
			HttpPost httpPost = new HttpPost("http://jpebook.ly.com/suppliersharing/Locker/LockOrder");
			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(10000).setConnectTimeout(10000).build();  
			httpPost.setConfig(requestConfig); 
			httpPost.addHeader("Accept-Encoding", "gzip, deflate");
			httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
			httpPost.addHeader("Accept", "application/json, text/plain, */*");
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpPost.addHeader("Connection", "keep-alive");
			httpPost.addHeader("Host", "jpebook.ly.com");
			httpPost.addHeader("Origin","http://jpebook.ly.com");
			httpPost.addHeader("cookie", tcCookie.toString());
			httpPost.addHeader("Pragma", "no-cache");
			httpPost.addHeader("Referer", "http://jpebook.ly.com/suppliersharing/SupplierOrder");
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");

			JSONObject param=new JSONObject();
			param.accumulate("serialNo", orderNo);
			param.accumulate("businessType", 1);
			httpPost.setHeader("Content-Type", "application/json");
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			// 判断响应信息是否正确
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();	        
				String result = EntityUtils.toString(entity, "UTF-8");
				return result;
			}else{//登录过期
				tcCookie.setLength(0);
				log.info("TC的cookie过期了");
			}
		}  catch (Exception e) {
			e.printStackTrace();
			tcCookie.setLength(0);
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
	 * @param isAgain
	 * @return
	 */
	public String unlocked(String orderNo,boolean isAgain){
		CloseableHttpClient httpclient = HttpClients.custom().build();
		CloseableHttpResponse response = null;
		try {
			//获取同程cookie
			synchronized (this) {
				if(StringUtils.isEmpty(tcCookie.toString())){
					Query query=new Query();
					query.addCriteria(Criteria.where("source").is("TC"));
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
					for(int i=0;i<jsonArray.size();i++){
						JSONObject json=jsonArray.getJSONObject(i);
						tcCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
					}
				}
			}
			//创建post请求
			HttpPost httpPost = new HttpPost("http://jpebook.ly.com/suppliersharing/Locker/UnLockOrder");
			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(10000).setConnectTimeout(10000).build();  
			httpPost.setConfig(requestConfig); 
			httpPost.addHeader("Accept-Encoding", "gzip, deflate");
			httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
			httpPost.addHeader("Accept", "application/json, text/plain, */*");
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpPost.addHeader("Connection", "keep-alive");
			httpPost.addHeader("Host", "jpebook.ly.com");
			httpPost.addHeader("Origin","http://jpebook.ly.com");
			httpPost.addHeader("cookie", tcCookie.toString());
			httpPost.addHeader("Pragma", "no-cache");
			httpPost.addHeader("Referer", "http://jpebook.ly.com/suppliersharing/SupplierOrder");
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");

			JSONObject param=new JSONObject();
			param.accumulate("serialNo", orderNo);
			param.accumulate("businessType", 1);
			httpPost.setHeader("Content-Type", "application/json");
			StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
			response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			// 判断响应信息是否正确
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();	        
				String result = EntityUtils.toString(entity, "UTF-8");
				return result;
			}else{//登录过期
				tcCookie.setLength(0);
				log.info("TC的cookie过期了");
				if(isAgain){
					return lockedOrder(orderNo, false);
				}
			}
		}  catch (Exception e) {
			e.printStackTrace();
			tcCookie.setLength(0);
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
	 * 订单转换
	 * @param jsonObj
	 * @return
	 */
	private OrderVO orderJsonToOrderVO(String jsonObj,String orderSource,String orderShop){
		try {
			JSONObject json=JSONObject.fromObject(jsonObj);
			if(!json.getBoolean("isSuccess")){
				return null;
			}
			JSONObject orderJson=json.getJSONObject("data");
			OrderVO orderVo=new OrderVO();
			orderVo.setOrderNo(orderJson.getString("orderSerialNo"));
			orderVo.setcOrderNo(orderJson.getString("orderSerialNo"));
			orderVo.setStatus(orderJson.getString("orderStatus")); 
			orderVo.setLastPrintTicketTime(orderJson.getString("orderTip"));
			orderVo.setTicketRemark(orderJson.getString("urgeCount"));

			Order order=new Order();
			String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			order.setOrderNo(orderJson.getString("orderSerialNo"));
			order.setcOrderNo(orderJson.getString("orderSerialNo"));
			order.setOrderSource(orderSource);
			order.setOrderShop(orderShop);
			order.setCreateBy("system");
			order.setStatus(WebConstant.ORDER_NO_TICKET); // 未出票 
			order.setPayStatus(WebConstant.PAYMENT_ALREADY); // 已支付
			order.setInterfaceImport(WebConstant.INTERFACE_IMPORT_YES);
			order.setUpdateTicketStatus(WebConstant.UPDATE_TICKET_NO_DISABLE);
			order.setLastPrintTicketTime(orderJson.getString("orderTip"));
			order.setPolicyRemark(orderJson.getString("policyRemark"));
			order.setcAddDate(orderJson.getString("createTime"));
			order.setTripType("单程".equals(orderJson.getString("tripType"))?WebConstant.FLIGHT_TYPE_ONEWAY:WebConstant.FLIGHT_TYPE_GOBACK);
			order.setPolicyType(orderJson.getString("outPolicyId"));
			order.setPolicyTypeDes(orderJson.getString("policyTypeDes"));
			try {				
				order.setTicType(orderJson.getString("reimbursementTypeDes"));
			} catch (Exception e) {
				
			}
			orderVo.setOrder(order);

			JSONArray passengerArr = orderJson.getJSONArray("ticketList");
			JSONArray flightArr =passengerArr.getJSONObject(0).getJSONArray("segmentList");
			List<Flight> flightList=new ArrayList<Flight>();
			for(int i=0;i<flightArr.size();i++){
				Flight flight=new Flight();
				JSONObject flightJson=flightArr.getJSONObject(i);
				flight.setOrderNo(order.getOrderNo());
				flight.setFlightNo(flightJson.getString("flightNo"));
				flight.setAirlineCode(flightJson.getString("flightNo").substring(0, 2));
				flight.setDepCityCode(flightJson.getString("startPort"));
				flight.setArrCityCode(flightJson.getString("endPort"));
				flight.setCabin(flightJson.getString("cabinCode"));
				String[]  depTimes= flightJson.getString("depTime").split(" ");
				String[]  arrTimes= flightJson.getString("arrTime").split(" ");
				flight.setFlightDepDate(depTimes[0]);
				flight.setFlightArrDate(arrTimes[0]);
				flight.setDepTime(depTimes[1]);
				flight.setArrTime(arrTimes[1]);
				flight.setOrderSource(order.getOrderSource());
				flight.setOrderShop(order.getOrderShop());
				flight.setCreateBy(order.getCreateBy());
				flight.setCreateDate(createDate);
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

			BigDecimal allTax=new BigDecimal(0);
			BigDecimal allPrice=new BigDecimal(0);
			BigDecimal allAisinoPrice=new BigDecimal(0);
			List<Passenger> passengerList=new ArrayList<Passenger>();
			for(int i=0;i<passengerArr.size();i++){
				JSONObject person=passengerArr.getJSONObject(i);
				Passenger passenger=new Passenger();
				passenger.setOrderNo(order.getOrderNo());
				passenger.setName(person.getString("passName"));
				passenger.setCertNo(person.getString("certNo"));
				passenger.setBirthday(person.getString("birthDay"));
				passenger.setFlightNo(flight.getFlightNo());
				passenger.setAirlineCode(flight.getAirlineCode());
				passenger.setDepCityCode(flight.getDepCityCode());
				passenger.setArrCityCode(flight.getArrCityCode());
				passenger.setFlightDepDate(flight.getFlightDepDate());
				passenger.setFlightArrDate(flight.getFlightArrDate());
				passenger.setDepTime(flight.getDepTime());
				passenger.setArrTime(flight.getArrTime());
				if(WebConstant.FLIGHT_TYPE_ONEWAY.equals(order.getTripType())){
					JSONObject jsonObject = person.getJSONArray("segmentList").getJSONObject(0);
					passenger.setCabin(jsonObject.getString("cabinCode"));
				}else{
					JSONArray jsonArray = person.getJSONArray("segmentList");
					StringBuilder sb=new StringBuilder();
					for(int j=0;j<jsonArray.size();j++){
						JSONObject jsonObject = jsonArray.getJSONObject(j);
						sb.append(jsonObject.getString("cabinCode")).append("/");
					}
					passenger.setCabin(sb.deleteCharAt(sb.length()-1).toString());
				}
				passenger.setPnr(person.getString("pnr"));
				try {
					if(StringUtils.isEmpty(order.getBigPnr())){						
						order.setBigPnr(person.getString("bigPnr"));
					}
				} catch (Exception e) {
					
				}
				passenger.setTax(person.getString("buildPrice"));
				passenger.setTicketNo(person.getString("eticketNo"));
				passenger.setFee(person.getString("fuelPrice"));
				passenger.setTicketPrice(person.getString("aisinoPrice"));
				passenger.setSellPrice(person.getString("ticketPrice"));
				passenger.setActualPrice(new BigDecimal(passenger.getSellPrice()).add(new BigDecimal(passenger.getFee()).add(new BigDecimal(passenger.getTax()))).toString());
				passenger.setTicketStatus(WebConstant.NO_TICKET);
				passenger.setStatus(WebConstant.NO_TICKET);
				passenger.setOrderSource(order.getOrderSource());
				passenger.setOrderShop(order.getOrderShop());
				passenger.setOrderNo(order.getOrderNo());
				passenger.setPolicyType(order.getPolicyType());
				passenger.setCreateBy(order.getCreateBy());
				passenger.setCreateDate(createDate);
				if ("1".equals(person.getString("passType"))) {
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_ADULT);
					if(StringUtils.isEmpty(order.getCabin())){
						order.setCabin(passenger.getCabin());
						order.setPnr(passenger.getPnr());
					}
				} else if("2".equals(person.getString("passType"))){
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_CHILD);
				}else{
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_BABY);
				}
				String type=person.getString("certType");
				if ("0".equals(type)) {//身份证
					passenger.setCertType("0");
				} else if ("1".equals(type)) {
					passenger.setCertType("1");
				} else if ("2".equals(type)) {
					passenger.setCertType("3");
				} else if ("3".equals(type)) {
					passenger.setCertType("4");
				} else if ("5".equals(type)) {
					passenger.setCertType("5");
				} else {
					passenger.setCertType("9");
				}
				allTax=new BigDecimal(passenger.getTax()).add(allTax);
				allAisinoPrice=new BigDecimal(passenger.getTicketPrice()).add(allAisinoPrice);
				allPrice=new BigDecimal(passenger.getActualPrice()).add(allPrice);
				passengerList.add(passenger);
			}
			orderVo.setPassengetList(passengerList);
			order.setPassengerCount(passengerList.size()+"");
			order.setTotalPrice(allPrice.toString());
			order.setTotalTicketPrice(allAisinoPrice.toString());
			order.setTotalTax(allTax.toString());
			return orderVo;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 订单转换(平台页面接口数据)
	 * @param jsonStr
	 * @return
	 */
	private OrderVO orderJsonToOrderVO2(String jsonStr,String orderSource,String orderShop){
		try {
			JSONObject jsonObj=JSONObject.fromObject(jsonStr);
			JSONObject orderJson=jsonObj.getJSONObject("data").getJSONObject("ticketOrder");
			OrderVO orderVo=new OrderVO();
			orderVo.setOrderNo(orderJson.getString("orderSerialNo"));
			orderVo.setcOrderNo(orderJson.getString("orderSerialNo"));
			orderVo.setStatus(orderJson.getString("orderStatusDes"));
			Order order=new Order();
			String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			order.setOrderNo(orderJson.getString("orderSerialNo"));
			order.setcOrderNo(orderJson.getString("orderSerialNo"));
			order.setOrderSource(orderSource);
			order.setOrderShop(orderShop);
			order.setCreateDate(createDate);
			order.setCreateBy("system");
			order.setStatus(WebConstant.ORDER_NO_TICKET); // 未出票 
			order.setPayStatus(WebConstant.PAYMENT_ALREADY); // 已支付
			order.setInterfaceImport(WebConstant.INTERFACE_IMPORT_YES);
			order.setUpdateTicketStatus(WebConstant.UPDATE_TICKET_NO_DISABLE);
			order.setTripType(WebConstant.FLIGHT_TYPE_GOBACK);
			order.setTotalPrice(orderJson.getString("totalPrice"));
			order.setTotalTax("50");
			order.setTotalTicketPrice(orderJson.getString("ticketPrice"));
			order.setcAddDate(orderJson.getString("createTime"));
			JSONObject ticketPolicy=jsonObj.getJSONObject("data").getJSONObject("ticketPolicy");
			order.setPolicyType(ticketPolicy.getString("outPolicyId"));
			order.setPolicyId(ticketPolicy.getString("policyId"));
			orderVo.setOrder(order);

			List<Flight> flightList=new ArrayList<Flight>();

			JSONArray flightJsonArr = jsonObj.getJSONObject("data").getJSONArray("ticketSegmentList");
			JSONArray ticketPassengerArr = jsonObj.getJSONObject("data").getJSONArray("ticketPassengerList");

			for(int i=0;i<flightJsonArr.size();i++){
				Flight flight=new Flight();
				JSONObject flightJson=flightJsonArr.getJSONObject(i);
				JSONObject passengerJson=ticketPassengerArr.getJSONObject(i);
				flight.setOrderNo(order.getOrderNo());
				flight.setFlightNo(flightJson.getString("flightNo"));
				flight.setAirlineCode(flightJson.getString("flightNo").substring(0, 2));
				flight.setDepCityCode(flightJson.getString("startPort"));
				flight.setArrCityCode(flightJson.getString("endPort"));
				flight.setCabin(passengerJson.getString("cabinCode"));
				String[]  depTimes= flightJson.getString("depTime").split(" ");
				String[]  arrTimes= flightJson.getString("arrTime").split(" ");
				flight.setFlightDepDate(depTimes[0]);
				flight.setFlightArrDate(arrTimes[0]);
				flight.setDepTime(depTimes[1]);
				flight.setArrTime(arrTimes[1]);
				flight.setOrderSource(order.getOrderSource());
				flight.setOrderShop(order.getOrderShop());
				flight.setOrderNo(order.getOrderNo());
				flight.setCreateBy(order.getCreateBy());
				flight.setCreateDate(createDate);
				flightList.add(flight);
			}
			orderVo.setFlightList(flightList);

			Flight flight=flightList.get(0);
			order.setFlightNo(flight.getFlightNo());
			order.setAirlineCode(flight.getFlightNo().substring(0, 2));
			order.setDepCityCode(flight.getDepCityCode());
			order.setArrCityCode(flight.getArrCityCode());
			order.setFlightDate(flight.getFlightDepDate());
			order.setCabin(flight.getCabin());

			List<Passenger> passengerList=new ArrayList<Passenger>();
			Set<String> nameSet=new HashSet<String>();
			for(int i=0;i<ticketPassengerArr.size();i++){
				JSONObject person=ticketPassengerArr.getJSONObject(i);
				String pId=person.getString("guid");
				if(nameSet.contains(pId)){
					continue;
				}
				Passenger passenger=new Passenger();
				passenger.setOrderNo(order.getOrderNo());
				passenger.setName(person.getString("passName"));
				passenger.setCertNo(person.getString("certNo"));
				passenger.setBirthday(person.getString("birthDay"));
				passenger.setFlightNo(flight.getFlightNo());
				passenger.setAirlineCode(flight.getFlightNo().substring(0, 2));
				passenger.setDepCityCode(flight.getDepCityCode());
				passenger.setArrCityCode(flight.getArrCityCode());
				passenger.setFlightDepDate(flight.getFlightDepDate());
				passenger.setFlightArrDate(flight.getFlightArrDate());
				passenger.setDepTime(flight.getDepTime());
				passenger.setArrTime(flight.getArrTime());
				passenger.setCabin(person.getString("cabinCode"));
				passenger.setTicketPrice(person.getString("aisinoPrice"));
				passenger.setSellPrice(person.getString("ticketPrice"));
				passenger.setPnr(person.getString("pnr"));
				passenger.setTicketNo(person.getString("eticketNo"));
				passenger.setTicketStatus(WebConstant.NO_TICKET);
				passenger.setStatus(WebConstant.NO_TICKET);
				passenger.setOrderSource(order.getOrderSource());
				passenger.setOrderShop(order.getOrderShop());
				passenger.setOrderNo(order.getOrderNo());
				passenger.setPolicyType(order.getPolicyType());
				passenger.setCreateBy(order.getCreateBy());
				passenger.setCreateDate(createDate);
				if ("1".equals(person.getString("passType"))) {
					passenger.setTax("50");
					passenger.setFee("0");
					passenger.setActualPrice(new BigDecimal(passenger.getSellPrice()).add(new BigDecimal(passenger.getFee()).add(new BigDecimal(passenger.getTax()))).toString());
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_ADULT);
				} else if("2".equals(person.getString("passType"))){
					passenger.setTax("0");
					passenger.setFee("0");
					passenger.setActualPrice(passenger.getSellPrice());
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_CHILD);
				}else{
					passenger.setTax("0");
					passenger.setFee("0");
					passenger.setActualPrice(passenger.getSellPrice());
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_BABY);
				}
				String type=person.getString("certType");
				if ("0".equals(type)) {//身份证
					passenger.setCertType("0");
				} else if ("1".equals(type)) {
					passenger.setCertType("1");
				} else if ("2".equals(type)) {
					passenger.setCertType("3");
				} else if ("3".equals(type)) {
					passenger.setCertType("4");
				} else if ("5".equals(type)) {
					passenger.setCertType("5");
				} else {
					passenger.setCertType("9");
				}
				passengerList.add(passenger);
				nameSet.add(pId);
			}
			orderVo.setPassengetList(passengerList);
			order.setPassengerCount(passengerList.size()+"");

			Travel travel=new Travel();
			travel.setFlightNo(order.getFlightNo());
			travel.setAirlineCode(order.getFlightNo().substring(0, 2));
			travel.setDepCityCode(flight.getDepCityCode());
			travel.setArrCityCode(flight.getArrCityCode());
			travel.setFlightDepDate(flight.getFlightDepDate());
			travel.setFlightArrDate(flight.getFlightArrDate());
			travel.setDepTime(flight.getDepTime());
			travel.setArrTime(flight.getArrTime());
			travel.setCabin(order.getCabin());
			travel.setOrderNo(order.getOrderNo());
			orderVo.setTravel(travel);
			return orderVo;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 获取同程token
	 * @throws InterruptedException 
	 */
	public String createToken()  {
		String timestamp=String.valueOf(new Date().getTime()).substring(0, 10);
		String url="https://flightopenapi.17u.cn/openapi/create/accesstoken?t="+timestamp;
		String user=DictUtils.getDictCode("order_import_cfgtc", "user");
		String pass=DictUtils.getDictCode("order_import_cfgtc", "pass");
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient2 = getHttpClient2();
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject param = new JSONObject();
            param.accumulate("app_id", pass);
            param.accumulate("app_key", user);
            String uri = "/openapi/create/accesstoken?t=" + timestamp;
            String sign = createSign(uri + param.toString(), pass + timestamp);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("x-tg-token", String.valueOf(timestamp));
            httpPost.setHeader("x-tg-sign", sign);
            StringEntity se = new StringEntity(param.toString(), "utf-8");
			httpPost.setEntity(se);
            for (int i = 0; i < 3; i++) {
                response = httpClient2.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, "utf-8");
                JSONObject json = JSONObject.fromObject(content);
                String access_token = json.getString("access_token");
                if (StringUtils.isNotEmpty(access_token)){
                    return  access_token;
                }
                Thread.sleep(10000);
            }
		} catch (Exception e) {
			log.error("跟新TC token失败",e);
		}finally{
			try {
				httpClient2.close();
			} catch (IOException e1) {

			}
			httpPost.abort();
			if(response!=null){
				try {
					response.close();
				} catch (IOException e) {

				}
			}
		}
        return null;
	}

	public static CloseableHttpClient getHttpClientNotUse(){
		String proxyServer=ConfigUtils.getParam("uploadProxyIp1");
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
	
	public static CloseableHttpClient getHttpClientLNotUse(){
		String proxyServer=ConfigUtils.getParam("uploadProxyIp1");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();  
		credsProvider.setCredentials(new AuthScope(proxyServer, 30000),new UsernamePasswordCredentials("ff53719", "ff53719"));
		HttpHost proxy = new HttpHost(proxyServer, 30000 );
		RequestConfig globalConfig = RequestConfig.custom() 
				.setSocketTimeout(60000)
				.setConnectTimeout(60000)
				.setConnectionRequestTimeout(60000).setProxy(proxy)
				.build(); 
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCredentialsProvider(credsProvider)  
				.build();

		return build;	 
	}

	public static CloseableHttpClient getHttpClient2(){
		RequestConfig globalConfig = RequestConfig.custom() 
				.setSocketTimeout(60000)
				.setConnectTimeout(60000)
				.setConnectionRequestTimeout(60000)
				.build(); 
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig)
				.build();

		return build;	 
	}

	//同程签名
	private String createSign(String message, String secret) {
		String sign = "";
		byte[] keyByte = secret.getBytes(StandardCharsets.UTF_8);
		byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
		try {
			Mac hmacSha256 = Mac.getInstance("HmacSHA256");
			hmacSha256.init(new SecretKeySpec(keyByte, 0, keyByte.length, "HmacSHA256"));
			byte[] hmacSha256Bytes = hmacSha256.doFinal(messageBytes);
			sign = Md5Util.bytes2Hex(hmacSha256Bytes).replaceAll("-","").toLowerCase();
		} catch (Exception e) {}
		return sign;
	}


	private String getToken(){
		if(StringUtils.isEmpty(TongChengSchednule.token)){
			return DictUtils.getDictCodeNoCache("order_import_cfgtc", "access_token");
		}
		return TongChengSchednule.token;
	}





	public static void main(String[] args) {
		HttpPost httpPost = new HttpPost("https://www.mayilxcn.com:9100/api/Ticket/queryRefundDetail");
		httpPost.setHeader("Content-Type", "application/json");

		String param="{\"context\": {\"version\": \"1.0.0\",\"sign\": \"fcecf5124065af38cbdcd0870915e107\",\"timestamp\": 1589168855292,\"nonce\": \"13b6893b7a6945c5af2fc3e72f8026ac\",\"channel\": \"zhixing\"},\"businessOrderNo\": \"2695244447\",\"orderNo\": \"2695244447\"}";
		CloseableHttpResponse response=null;
		try {
			StringEntity se = new StringEntity(param, "utf-8");
			httpPost.setEntity(se);
			response = HttpClients.createDefault().execute(httpPost);
			HttpEntity entity = response.getEntity();			
			String content = EntityUtils.toString(entity, "utf-8");
			System.out.println(content);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			httpPost.abort();
			if(response!=null){
				try {
					response.close();
				} catch (IOException e) {
					
				}
			}
		}


	}

    public PPMSG getPPMSG(OrderVO orderVO,String ticketPirce){
        PPMSG p = new PPMSG();
        try{
            QueryWrapper<Flight> query=new QueryWrapper<Flight>();
            query.eq("order_id", orderVO.getOrderId());
            List<Flight> selectFlightInfo = flightService.getByQueryW(query);
            Flight flight = selectFlightInfo.get(0);
            p.setFlightDate(flight.getFlightDepDate());
            p.setFlightNo(flight.getFlightNo());
            p.setFromCity(flight.getDepCityCode());
            p.setFromDatetime(flight.getFlightDepDate()+" "+flight.getDepTime().substring(0,5));
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
            p.setTicketPrice(ticketPirce);
            p.setToCity(flight.getArrCityCode());
            p.setToDatetime(flight.getFlightDepDate()+" "+flight.getArrTime().substring(0,5));
            StringBuffer name = new StringBuffer();
            StringBuffer certNo = new StringBuffer();
            StringBuffer passengerType = new StringBuffer();
            StringBuffer cardType = new StringBuffer();
            for(Passenger qp:orderVO.getPassengetList()){
                if(!qp.getPassengerType().equals("0")){
                    return null;
                }
                name.append("|"+qp.getName());
                certNo.append("|"+qp.getCertNo());
                passengerType.append("|"+1);
                String type = qp.getCertType();
                if(type.equals("1")){
                    type = "PP";
                }else if(type.equals("0")){
                    type = "NI";
                }else {
                    type = "ID";
                }
                cardType.append("|"+type);
            }
            p.setPassengerName(name.toString().substring(1));
            p.setPassengerCard(certNo.toString().substring(1));
            p.setPassengerType(passengerType.toString().substring(1));
            p.setCardType(cardType.toString().substring(1));
            p.setSeatClass(flight.getCabin());
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return p;
    }





}
