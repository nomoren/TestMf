package cn.ssq.ticket.system.service.OrderImport.impl;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.importBean.CtripBean.CtripFlightVO;
import cn.ssq.ticket.system.entity.importBean.CtripBean.CtripOrderVO;
import cn.ssq.ticket.system.entity.importBean.CtripBean.CtripPassengerVO;
import cn.ssq.ticket.system.entity.pp.PPMSG;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.service.OrderImport.OrderImportService;
import cn.ssq.ticket.system.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * 携程平台服务类
 */
@Service("ctripOrderService")
public class CtripOrderService implements OrderImportService {

    static final int BUFFER = 10240;

    //private static CloseableHttpClient httpClient;
    //待出票订单队列
    public static LimitQueue<String> statusHas = new LimitQueue<String>(200);
    //其他状态订单队列
    public static LimitQueue<String> status5 = new LimitQueue<String>(200);
    private static Logger log = LoggerFactory.getLogger(CtripOrderService.class);
    private static int timeout = 30000;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PassengerService passengerService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private ChangeService changeService;
    @Autowired
    private FlightService flightService;
    @Autowired
    private LogService logService;
    @Resource
    private TOrderCtripService orderCtripService;

    public static CloseableHttpClient getHttpClient() {
        //String proxyServer="14.152.95.93";
        //CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //credsProvider.setCredentials(new AuthScope(proxyServer, 30000),new UsernamePasswordCredentials("ff53719", "ff53719"));
        //HttpHost proxy = new HttpHost(proxyServer, 30000 );
        RequestConfig globalConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();
        CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .build();
        return build;
    }

    public static CloseableHttpClient getHttpClientL() {
        //String proxyServer="14.152.95.93";
        //CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //credsProvider.setCredentials(new AuthScope(proxyServer, 30000),new UsernamePasswordCredentials("ff53719", "ff53719"));
        //HttpHost proxy = new HttpHost(proxyServer, 30000 );
        RequestConfig globalConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .build();
        CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .build();
        return build;
    }

    //携程订单接口返回的是对报文GZIP压缩后的base64位格式的文本编码
    public static String parse(String result) {
        byte[] values;
        try {
            values = result.getBytes("iso8859-1");
            values = decompress(values);
            return new String(values, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decompress(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 解压缩
        try {
            decompress(bais, baos);
            data = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static void decompress(InputStream is, OutputStream os) {
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                os.write(data, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gis != null) {
                try {
                    gis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	/*@SuppressWarnings("unchecked")
	private List<String> processXmlToStrList(String xmlContent) throws Exception{
		List<String> list=new ArrayList<String>();
		try {
			org.dom4j.Document doc = DocumentHelper.parseText(xmlContent);
			Element rootElement = doc.getRootElement().element("Header");//头信息
			String resultStatus = rootElement.attributeValue("ResultCode");
			if ("Success".equals(resultStatus)){
				Element eOrderRoot = doc.getRootElement().element("OpenSearchIssueBillResponse");//订单列表根节点
				Element eOrderInfoList = eOrderRoot.element("OpenIssueBillSearchResultList");//订单列表次节点
				List<Element> orderElementList = eOrderInfoList.elements("OpenIssueBillSearchResult");
				if (null != orderElementList && orderElementList.size() > 0){
					for (Element orderElement : orderElementList){
						list.add(orderElement.elementText("IssueBillID"));
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return list;
	}*/

    /**
     * 导入待出票
     */
    @Override
    public List<OrderVO> batchImportOrders(String orderSource,
                                           String orderShop, String status) throws Exception {
        List<OrderVO> list = new ArrayList<OrderVO>();
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        String endDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        String startDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("<Request UserName=\"" + user + "\" UserPassword=\"" + passWord + "\">");
        sb.append("<OpenSearchIssueBillRequest>");
        sb.append("<OrderBeginDateTime>" + startDate + "</OrderBeginDateTime>");
        sb.append("<OrderEndDateTime>" + endDate + "</OrderEndDateTime>");
        sb.append("</OpenSearchIssueBillRequest>");
        sb.append("</Request>");
        String xmlBody = sb.toString();
        String xmla = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Handle xmlns=\"http://tempuri.org/\"><requestXML>"
                + xmlBody.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</requestXML></Handle></soap:Body></soap:Envelope>";
        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/OpenIssueOrderList.asmx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.setEntity(new StringEntity(xmla, "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Document doc = Jsoup.parse(content);
            if (doc.getElementsByTag("HandleResult").first() == null) {
                return list;
            }
            String base = doc.getElementsByTag("HandleResult").first().text();
            String data = Base64Util.base64Decode(base);
            String xmlStr = parse(data);
            List<CtripOrderVO> ctripOrderVOList = processXml(xmlStr);
            for (CtripOrderVO ctripOrderVO : ctripOrderVOList) {
                String cOrderNo = ctripOrderVO.getIssueBillID();
                if (statusHas.contains(cOrderNo)) {
                    continue;
                }
                Thread.sleep(500);
                CtripOrderVO cOrderVO = getOrderDetailBycOrderNo(cOrderNo, user, passWord);
                if (cOrderVO != null) {
                    OrderVO orderVo = procCtripOrderToOrderVO(cOrderVO, orderSource, orderShop);
                    list.add(orderVo);
                }
            }

        } catch (Exception e) {
            throw e;
        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
        return list;
    }

    /**
     * 同步退票单
     *
     * @throws Exception
     */
    public void syncRefundOrder(String orderSource, String orderShop) throws Exception {
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        String endDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss").replace(" ", "T");
        calendar.add(Calendar.HOUR_OF_DAY, -3);
        String startDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss").replace(" ", "T");
        StringBuilder sb = new StringBuilder();
        sb.append("<Request UserName=\"" + user + "\" UserPassword=\"" + passWord + "\">");
        sb.append("<GetCoopRefundConfirmListRequest>");
        sb.append("<StartDate>" + startDate + "</StartDate>");
        sb.append("<EndDate>" + endDate + "</EndDate>");
        sb.append("<FeeType>2</FeeType>");
        sb.append("<RefundType>全部</RefundType>");
        sb.append("<IsInternational>0</IsInternational>");
        sb.append("<OrderBy>1</OrderBy>");
        sb.append("<OperateUser>" + user + "</OperateUser>");
        sb.append("<BeginRow>1</BeginRow>");
        sb.append("<EndRow>10</EndRow>");
        sb.append("</GetCoopRefundConfirmListRequest>");
        sb.append("</Request>");
        String xmlBody = sb.toString();
        String xmla = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Handle xmlns=\"http://tempuri.org/\"><requestXML>"
                + xmlBody.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</requestXML></Handle></soap:Body></soap:Envelope>";
        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/GetCoopRefundConfirmListService.asmx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.setEntity(new StringEntity(xmla, "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Document doc = Jsoup.parse(content);
            String base = doc.getElementsByTag("HandleResult").first().text();
            String data = Base64Util.base64Decode(base);
            String xmlStr = parse(data);
            //System.out.println(xmlStr);
            processRefundXml(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 同步改签单
     *
     * @param orderSource
     * @param orderShop
     * @throws Exception
     */
    public void syncChangeOrder(String orderSource, String orderShop, String status) throws Exception {
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        String endDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss").replace(" ", "T");
        calendar.add(Calendar.HOUR_OF_DAY, -3);
        String startDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss").replace(" ", "T");
        StringBuilder sb = new StringBuilder();
        sb.append("<Request UserName=\"" + user + "\" UserPassword=\"" + passWord + "\">");
        sb.append("<OpenQueryExchangeRequest>");
        sb.append("<FromDateTime>" + startDate + "</FromDateTime>");
        sb.append("<ToDateTime>" + endDate + "</ToDateTime>");
        sb.append("<RebookQueryOptionType>" + status + "</RebookQueryOptionType>");
        sb.append("</OpenQueryExchangeRequest>");
        sb.append("</Request>");
        String xmlBody = sb.toString();
        String xmla = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Handle xmlns=\"http://tempuri.org/\"><requestXML>"
                + xmlBody.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</requestXML></Handle></soap:Body></soap:Envelope>";
        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/SearchExchangeList.asmx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.setEntity(new StringEntity(xmla, "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Document doc = Jsoup.parse(content);
            if (doc.getElementsByTag("HandleResult") == null) {
                return;
            }
            String base = doc.getElementsByTag("HandleResult").first().text();
            String data = Base64Util.base64Decode(base);
            String xmlStr = parse(data);
            //System.out.println(xmlStr);
            processChangeXml(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<CtripOrderVO> processXml(String xmlContent) throws Exception {
        List<CtripOrderVO> list = new ArrayList<CtripOrderVO>();
        try {
            org.dom4j.Document doc = DocumentHelper.parseText(xmlContent);
            Element rootElement = doc.getRootElement().element("Header");//头信息
            String resultStatus = rootElement.attributeValue("ResultCode");
            if ("Success".equals(resultStatus)) {
                Element eOrderRoot = doc.getRootElement().element("OpenIssueOrderListResponse");//订单列表根节点
                Element eOrderInfoList = eOrderRoot.element("IssueOrderList");//订单列表次节点
                List<Element> orderElementList = eOrderInfoList.elements("OpenIssueBillResponse");
                if (null != orderElementList && orderElementList.size() > 0) {
                    for (Element orderElement : orderElementList) {
                        CtripOrderVO vo = new CtripOrderVO();
                        vo.setIssueBillID(orderElement.elementText("IssueBillID"));
                        list.add(vo);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }

        return list;
    }

    /**
     * 解析改签单
     *
     * @param xmlContent
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void processChangeXml(String xmlContent) throws Exception {
        try {
            org.dom4j.Document doc = DocumentHelper.parseText(xmlContent);
            Element rootElement = doc.getRootElement().element("Header");//头信息
            String resultStatus = rootElement.attributeValue("ResultCode");
            if ("Success".equals(resultStatus)) {
                Element eOrderRoot = doc.getRootElement().element("OpenQueryExchangeResponse");
                if (eOrderRoot == null) {
                    return;
                }
                Element eOrderInfoList = eOrderRoot.element("OpenApiQueryExchangeItems");
                if (eOrderInfoList == null) {
                    return;
                }
                List<Element> changeElementList = eOrderInfoList.elements("OpenQueryExchangeItem");
                List<Change> list = new ArrayList<Change>();
                for (Element changeEle : changeElementList) {
                    String RbkId = changeEle.elementText("RbkId");
                    String passengerName = changeEle.elementTextTrim("PassengerName");
                    if (status5.contains(RbkId + passengerName)) {
                        continue;
                    }
                    String oldPnr = changeEle.elementTextTrim("OldRecordNo");
                    String flightDate = changeEle.elementTextTrim("OldTakeOffTime").split("T")[0];// 原来航班日期
                    String depCityCode = changeEle.elementTextTrim("OldDPort");// 原来航班出发城市三字码
                    String arrCityCode = changeEle.elementTextTrim("OldAPort");// 原来航班到达城市三字码
                    //  根据pnr查询
                    QueryWrapper<Passenger> query = new QueryWrapper<Passenger>();
                    query.eq("order_source", InterfaceConstant.ORDER_SOURCE_CTRIP);
                    query.eq("pnr", oldPnr);
                    List<Passenger> passengerList = passengerService.findByQueryWapper(query);
                    if (passengerList.size() < 1) {
                        log.info(passengerName + "：根据PNR：" + oldPnr + "未查询到乘客信息");
                        // 根据姓名+原航班起飞日期+起始城市查询
                        query = new QueryWrapper<Passenger>();
                        query.eq("order_source", InterfaceConstant.ORDER_SOURCE_CTRIP);
                        query.eq("name", passengerName);
                        query.eq("Flight_dep_date", flightDate);
                        query.eq("dep_city_code", depCityCode);
                        query.eq("arr_city_code", arrCityCode);
                        passengerList = passengerService.findByQueryWapper(query);
                    }
                    if (passengerList.size() < 1) {
                        continue;
                    }
                    Passenger passenger = passengerList.get(0);
                    Change change = new Change();
                    change.setOrderNo(passenger.getOrderNo());
                    change.setOrderId(passenger.getOrderId());
                    change.setOrderShop(passenger.getOrderShop());
                    change.setOrderSource(passenger.getOrderSource());
                    change.setNewCOrderNo(changeEle.elementTextTrim("RbkId"));
                    change.setPassengerName(changeEle.elementTextTrim("PassengerName"));
                    //change.setTktNo(changeEle.elementText("PassengerName"));RebookingQueryFee
                    change.setRevenuePrice(new BigDecimal(changeEle.elementTextTrim("RebookingQueryFee")).toString());
                    change.setState(WebConstant.CHANGE_UNTREATED);
                    change.setsPnr(changeEle.elementText("OldRecordNo"));
                    change.setsAirlineCode(changeEle.elementTextTrim("OldFlight").substring(0, 2));
                    change.setsFlightNo(changeEle.elementTextTrim("OldFlight"));
                    change.setsArrCityCode(changeEle.elementTextTrim("OldAPort"));
                    change.setsDepCityCode(changeEle.elementTextTrim("OldDPort"));
                    change.setsFlightDate(changeEle.elementTextTrim("OldTakeOffTime").split("T")[0]);
                    change.setsCabin(changeEle.elementTextTrim("OldSubClass"));
                    change.setCabin(changeEle.elementTextTrim("SubClass"));
                    change.setPnr(changeEle.elementTextTrim("RecordNo"));
                    change.setFlightNo(changeEle.elementTextTrim("Flight"));
                    change.setFlightDate(changeEle.elementTextTrim("TakeOffTime").split("T")[0]);
                    String changeDate = changeEle.elementTextTrim("RebookingDate").replace("T", " ");
                    change.setChangeDate(changeDate);
                    change.setCreateBy("SYSTEM");
                    change.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                    String newPnr = changeEle.elementTextTrim("RecordNo");
                    if (StringUtils.isNotEmpty(newPnr)) {
                        Passenger p = new Passenger();
                        p.setPassengerId(passenger.getPassengerId());
                        p.setPnr(newPnr);
                        passengerService.updateById(p);
                    }

                    list.add(change);
                    status5.offer(change.getNewCOrderNo() + passengerName);
                }
                changeService.saveChanges(list);
            }
        } catch (Exception e) {
            status5.clear();
            throw e;
        }
    }

    /**
     * 解析退票单
     *
     * @param xmlContent
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void processRefundXml(String xmlContent) throws Exception {
        org.dom4j.Document doc = DocumentHelper.parseText(xmlContent);
        Element rootElement = doc.getRootElement().element("Header");//头信息
        String resultStatus = rootElement.attributeValue("ResultCode");
        if ("Success".equals(resultStatus)) {
            Element eOrderRoot = doc.getRootElement().element("GetCoopRefundConfirmListResponse");
            if (eOrderRoot == null) {
                return;
            }
            Element eOrderInfoList = eOrderRoot.element("GetCoopRefundConfirmListItems");
            if (eOrderInfoList == null) {
                return;
            }
            List<Element> refundElementList = eOrderInfoList.elements("GetCoopRefundConfirmItem");
            if (null != refundElementList && refundElementList.size() > 0) {
                for (Element refundElement : refundElementList) {
                    String retNo = refundElement.elementText("Prid");

                    Element detailItems = refundElement.element("GetCoopRefundConfirmDetailItems");
                    List<Element> detailItem = detailItems.elements("GetCoopRefundConfirmDetailItem");
                    String cOrderNo = detailItem.get(0).elementText("IssueBillID");
                    Order order = orderService.getOrderBycOrderNo(cOrderNo);
                    if (order == null) {
                        continue;
                    }
                    //StringBuilder names=new StringBuilder();
                    //StringBuilder ticketNos=new StringBuilder();
                    for (Element element : detailItem) {
                        String passengerName = element.elementText("PassengerName");
                        if (status5.contains(retNo + passengerName)) {
                            continue;
                        }
                        Refund refund = new Refund();
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
                        refund.setPassengerName(passengerName);
                        refund.setTicketNo(element.elementText("AirLineCode") + (element.elementText("TicketNO")));
                        refund.setcRealPrice(new BigDecimal("0").subtract(new BigDecimal(element.elementText("PayFlightAgency"))).doubleValue());
                        refund.setcAppDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        refund.setPnr(order.getPnr());
                        String refundType = element.elementText("RefundType");
                        if ("自愿".equals(refundType)) {
                            refund.setRefundType(WebConstant.REFUND_TYPE_ZIYUAN);
                        } else {
                            refund.setRefundType(WebConstant.REFUND_TYPE_NOZIYUAN);
                        }
                        refundService.autoCreateRefund(refund);
                        status5.offer(retNo + passengerName);
                    }
                    // 订单携程表添加数据
                    String ctripId = refundElement.elementText("OrderId");
                    TOrderCtrip orderCtrip = new TOrderCtrip();
                    orderCtrip.setOrderId(order.getOrderId());
                    orderCtrip.setCtripId(ctripId);
                    orderCtripService.add(orderCtrip);


                }
            }
        }

    }

    /**
     * 根据出票单查询订单详情
     *
     * @param cOrderNo
     * @param user
     * @param pass
     * @return
     * @throws Exception
     */
    public CtripOrderVO getOrderDetailBycOrderNo(String cOrderNo, String user, String pass) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<Request UserName=\"" + user + "\" UserPassword=\"" + pass + "\">");
        sb.append("<OpenIssueBillInfoRequest>");
        sb.append("<IssueBillID>" + cOrderNo + "</IssueBillID>");
        sb.append("</OpenIssueBillInfoRequest>");
        sb.append("</Request>");
        String xmlBody = sb.toString();
        String xmla = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Handle xmlns=\"http://tempuri.org/\"><requestXML>"
                + xmlBody.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</requestXML></Handle></soap:Body></soap:Envelope>";

        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/IssueBillInfo.asmx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.setEntity(new StringEntity(xmla, "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Document doc = Jsoup.parse(content);
            String base = doc.getElementsByTag("HandleResult").first().text();
            String data = Base64Util.base64Decode(base);
            String xmlStr = parse(data);
            //System.out.println(xmlStr);
            CtripOrderVO ctripOrderVO = xmlToOrderVO(xmlStr);
            return ctripOrderVO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 获取订单状态
     * @param cOrderNo
     * @param orderShop
     * @return
     * @throws Exception
     */
    public String getOrderStatus(String cOrderNo, String orderShop) throws Exception {
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");

        StringBuilder sb = new StringBuilder();
        sb.append("<Request UserName=\"" + user + "\" UserPassword=\"" + passWord + "\">");
        sb.append("<OpenIssueBillInfoRequest>");
        sb.append("<IssueBillID>" + cOrderNo + "</IssueBillID>");
        sb.append("</OpenIssueBillInfoRequest>");
        sb.append("</Request>");
        String xmlBody = sb.toString();
        String xmla = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Handle xmlns=\"http://tempuri.org/\"><requestXML>"
                + xmlBody.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</requestXML></Handle></soap:Body></soap:Envelope>";

        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/IssueBillInfo.asmx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.setEntity(new StringEntity(xmla, "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Document doc = Jsoup.parse(content);
            String base = doc.getElementsByTag("HandleResult").first().text();
            String data = Base64Util.base64Decode(base);
            String xmlStr = parse(data);
            org.dom4j.Document docData = DocumentHelper.parseText(xmlStr);
            Element rootElement = docData.getRootElement().element("Header");//头信息
            String resultStatus = rootElement.attributeValue("ResultCode");
            if ("Success".equals(resultStatus)) {
                Element orderElement = docData.getRootElement().element("OpenIssueBillInfoResponse");
                if (null == orderElement) {
                    return null;
                }
                String status = orderElement.elementText("IssueStatus");
                String issueStatus = orderElement.elementText("CancelIssueStatus");
                return status + ":" + issueStatus;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 同步票号
     *
     * @param list
     * @throws NoSuchAlgorithmException
     */
    public void updateTickNo(List<Order> list) throws NoSuchAlgorithmException {
        if (list.size() < 1) {
            return;
        }
        String orderShop = list.get(0).getOrderShop();
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");
        for (Order order : list) {
            try {
                CtripOrderVO ctripOrderVO = getOrderDetailBycOrderNo(order.getcOrderNo(), user, passWord);
                if ("1".equals(ctripOrderVO.getCancelIssueStatus()) || "2".equals(ctripOrderVO.getCancelIssueStatus())) {
                    order.setStatus(WebConstant.APPLY_ORDER_CANCEL);
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date date = df.parse(ctripOrderVO.getLastPrintTicketTime());
                        SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                        Date date1 = df1.parse(date.toString());
                        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        order.setLastPrintTicketTime(df2.format(date1));
                    } catch (Exception e) {
                        order.setLastPrintTicketTime(ctripOrderVO.getLastPrintTicketTime());
                    }
                    orderService.updateOrder(order);
                }
                if ("3".equals(ctripOrderVO.getOrderStatus())) {
                    //已出票
                    List<Passenger> passengerList = new ArrayList<Passenger>();
                    List<CtripPassengerVO> passengerList2 = ctripOrderVO.getPassengerList();
                    for (CtripPassengerVO p : passengerList2) {
                        Passenger passenger = new Passenger();
                        passenger.setTicketNo(p.getTicketNO());
                        passenger.setOrderNo(order.getOrderNo());
                        passenger.setPrintTicketCabin(p.getSubClass());
                        passenger.setCertNo(p.getCardNO());
                        passengerList.add(passenger);
                    }
                    orderService.updateTicketNo(order.getOrderNo(), passengerList);
                } else if ("4".equals(ctripOrderVO.getOrderStatus())) {
                    orderService.updateStatus(WebConstant.ORDER_CANCEL, order.getOrderNo());
                } else if ("5".equals(ctripOrderVO.getOrderStatus())) {
                    orderService.updateStatus(WebConstant.ORDER_CANCEL, order.getOrderNo());
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

    }

    /**
     * 认领订单
     *
     * @param
     * @throws Exception
     */
    public String assignOrder(String orderNo, String orderShop, String type, boolean isAgain) throws Exception {
        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/Api/IssueBillAssignTo.ashx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
            String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
            String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
            Assert.notNull(user, "user不能为空");
            JSONObject param = new JSONObject();
            param.accumulate("UserName", user);
            param.accumulate("UserPassword", passWord);
            JSONObject body = new JSONObject();
            body.accumulate("OperatorType", type);
            body.accumulate("IssueBillID", orderNo);
            param.accumulate("RequestBody", body);
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity se = new StringEntity(param.toString(), "utf-8");
            httpPost.setEntity(se);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            String s = JSONObject.fromObject(content).getString("result");
            String data = Base64Util.base64Decode(s);
            String xmlStr = parse(data);
            response.close();
            return xmlStr;
        } catch (Exception e) {
            if (isAgain) {
                return assignOrder(orderNo, orderShop, type, false);
            }
        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 票号回填
     *
     * @param list
     * @return
     * @throws Exception
     */
    public String verifyTicketNo(List<Passenger> list) throws Exception {
        Passenger passenger = passengerService.getById(list.get(0).getPassengerId().toString());
        String orderShop = passenger.getOrderShop();
        String orderNo = passenger.getOrderNo();
        List<Flight> flightList = flightService.selectFlightInfo(orderNo);
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");
        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/SearchModifyOrderInfo.asmx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClientL();
        try {
            StringBuilder requestXML = new StringBuilder();
            requestXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            requestXML.append("<Request UserName=\"" + user);
            requestXML.append("\" UserPassword=\"" + passWord + "\">");
            requestXML.append("<OpenModifyOrderRequest>");
            requestXML.append("<BookingChannel/>");
            requestXML.append("<IssueBillID>" + passenger.getOrderNo() + "</IssueBillID>");
            requestXML.append("<PNRLists>");
            for (int i = 0; i < flightList.size(); i++) {
                int index = i + 1;
                Flight flight = flightList.get(i);
                requestXML.append("<PNRList>");
                requestXML.append("<PNR>" + passenger.getPnr() + "</PNR>");
                requestXML.append("<PNRResultStatus>1</PNRResultStatus>");
                requestXML.append("<Sequence>" + index + "</Sequence>");
                requestXML.append("<FlightNo>" + flight.getFlightNo() + "</FlightNo>");
                requestXML.append("<DPort>" + flight.getDepCityCode() + "</DPort>");
                requestXML.append("<APort>" + flight.getArrCityCode() + "</APort>");
                requestXML.append("<TicketLists>");
                for (Passenger p : list) {
                    requestXML.append("<TicketList>");
                    requestXML.append("<PassengerName>" + p.getName() + "</PassengerName>");
                    if (p.getTicketNo().indexOf("-") != -1) {
                        requestXML.append("<AirLineCode>" + p.getTicketNo().trim().substring(0, p.getTicketNo().indexOf("-")) + "</AirLineCode>");
                        requestXML.append("<TicketNo>" + p.getTicketNo().trim().substring(p.getTicketNo().indexOf("-") + 1, p.getTicketNo().trim().length()) + "</TicketNo>");
                    } else {
                        requestXML.append("<AirLineCode>" + p.getTicketNo().trim().substring(0, 3) + "</AirLineCode>");
                        requestXML.append("<TicketNo>" + p.getTicketNo().trim().substring(3, p.getTicketNo().trim().length()) + "</TicketNo>");
                    }
                    requestXML.append("<TicketResultStatus>1</TicketResultStatus>");
                    requestXML.append("</TicketList>");
                }
                requestXML.append("</TicketLists>");
                requestXML.append("</PNRList>");
            }
            requestXML.append("</PNRLists>");
            requestXML.append("</OpenModifyOrderRequest>");
            requestXML.append("</Request>");
            String xmlBody = requestXML.toString();
            String xmla = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Handle xmlns=\"http://tempuri.org/\"><requestXML>"
                    + xmlBody.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</requestXML></Handle></soap:Body></soap:Envelope>";

            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.setEntity(new StringEntity(xmla, "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Document doc = Jsoup.parse(content);
            String base = doc.getElementsByTag("HandleResult").first().text();
            String data = Base64Util.base64Decode(base);
            String xmlStr = parse(data);
            return xmlStr;
        } catch (Exception e) {
            throw e;
        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 航变提交
     * @param param
     * @return
     * @throws Exception
     */
    public String subFlightChange(JSONObject param) throws Exception {
        String orderShop = param.getString("orderShop");
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");

        StringBuilder requestXML = new StringBuilder();
        requestXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        requestXML.append("<Request UserName=\"" + user);
        requestXML.append("\" UserPassword=\"" + passWord + "\">");
        requestXML.append("<OpenFlightChangeRequest>");
        requestXML.append("<Content></Content>");
        requestXML.append("<Pnr></Pnr>");
		/*requestXML.append("<NotifyNumber>123</NotifyNumber>");
		requestXML.append("<Source>9</Source>");
		requestXML.append("<Format>S</Format> ");*/
        requestXML.append("<FlightChangeType>" + param.getString("FlightChangeType") + "</FlightChangeType>");
        requestXML.append("<FlightClass>" + param.getString("FlightClass") + "</FlightClass>");
        requestXML.append("<OriginFlight>" + param.getString("OriginFlight") + "</OriginFlight>");
        String[] OriginDdate = param.getString("OriginDdate").split(" ");
        requestXML.append("<OriginDdate>" + OriginDdate[0] + "T" + OriginDdate[1] + "</OriginDdate>");
        String[] OriginAdate = param.getString("OriginAdate").split(" ");
        requestXML.append("<OriginAdate>" + OriginAdate[0] + "T" + OriginAdate[1] + "</OriginAdate>");
        requestXML.append("<OriginDPort>" + param.getString("OriginDPort") + "</OriginDPort>");
        requestXML.append("<OriginAPort>" + param.getString("OriginAPort") + "</OriginAPort>");
        requestXML.append("<OriginDBuilding></OriginDBuilding>");
        requestXML.append("<OriginABuilding></OriginABuilding>");
        requestXML.append("<OriginCraftType></OriginCraftType>");

        if (param.getString("FlightChangeType").equals("0")) {
            requestXML.append("<ProtectFlight>" + param.getString("ProtectFlight") + "</ProtectFlight>");
            requestXML.append("<ProtectSubClass></ProtectSubClass>");
            String[] ProtectDdate = param.getString("ProtectDdate").split(" ");
            requestXML.append("<ProtectDdate>" + ProtectDdate[0] + "T" + ProtectDdate[1] + "</ProtectDdate>");
            String[] ProtectAdate = param.getString("ProtectAdate").split(" ");
            requestXML.append("<ProtectAdate>" + ProtectAdate[0] + "T" + ProtectAdate[1] + "</ProtectAdate>");
            requestXML.append("<ProtectDPort>" + param.getString("ProtectDPort") + "</ProtectDPort>");
            requestXML.append("<ProtectAPort>" + param.getString("ProtectAPort") + "</ProtectAPort>");
            requestXML.append("<ProtectDBuilding></ProtectDBuilding>");
            requestXML.append("<ProtectABuilding></ProtectABuilding>");
            requestXML.append("<ProtectCraftType></ProtectCraftType>");
        }

        requestXML.append("</OpenFlightChangeRequest>");
        requestXML.append("</Request>");
        String xmlBody = requestXML.toString();
        String xmla = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Handle xmlns=\"http://tempuri.org/\"><requestXML>"
                + xmlBody.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</requestXML></Handle></soap:Body></soap:Envelope>";
        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/FlightChangeService.asmx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.setEntity(new StringEntity(xmla, "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Document doc = Jsoup.parse(content);
            String base = doc.getElementsByTag("HandleResult").first().text();
            String data = Base64Util.base64Decode(base);
            String xmlStr = parse(data);
            log.info("携程航变录入返回" + xmlStr);
            return xmlStr;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 确认退款
     *
     * @param retNo
     * @return
     * @throws Exception
     */
    public JSONObject confirmRefund(String retNo, String orderShop) {
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");
        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/Api/RefundConfirm.ashx";
        HttpPost httpPost = new HttpPost(url);
        //{ "RequestBody": { "OpenRefundConfirmItems": [ { "Prid": "***" } ]}, "UserName":"***", "UserPassword":"***" }
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            JSONObject param = new JSONObject();
            param.accumulate("UserName", user);
            param.accumulate("UserPassword", passWord);
            JSONObject item = new JSONObject();
            JSONArray retArr = new JSONArray();
            JSONObject retStr = new JSONObject();
            retStr.accumulate("Prid", retNo);
            retArr.add(retStr);
            item.accumulate("OpenRefundConfirmItems", retArr);
            param.accumulate("RequestBody", item);
            StringEntity se = new StringEntity(param.toString(), "utf-8");
            httpPost.setEntity(se);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            String data = Base64Util.base64Decode(JSONObject.fromObject(content).getString("result"));
            String str = parse(data);
            JSONObject jsonObject = JSONObject.fromObject(str);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpPost.abort();
        }
        return null;
    }

    /**
     * 转换订单
     *
     * @param ctripOrderVO
     * @param
     * @param orderShop
     * @return
     */
    public OrderVO procCtripOrderToOrderVO(CtripOrderVO ctripOrderVO, String orderSource, String orderShop) {

        OrderVO orderVo = new OrderVO();
        orderVo.setOrderNo(ctripOrderVO.getIssueBillID());
        String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Order order = new Order();
        //order.setCreateDate(createDate);
        order.setStatus(WebConstant.ORDER_NO_TICKET); // 未出票
        order.setPayStatus(WebConstant.PAYMENT_ALREADY); // 已支付
        order.setInterfaceImport(WebConstant.INTERFACE_IMPORT_YES);
        order.setUpdateTicketStatus(WebConstant.UPDATE_TICKET_NO_DISABLE);
        order.setOrderNo(ctripOrderVO.getIssueBillID());
        order.setcOrderNo(ctripOrderVO.getIssueBillID());
        order.setBigPnr(ctripOrderVO.getAirlineRecodeNo());
        order.setTicType(ctripOrderVO.getTricType());
        order.setOrderShop(orderShop);
        order.setOrderSource(orderSource);
        try {
            String policyRemark = ctripOrderVO.getPolicyRemark();
            if (StringUtils.isNotEmpty(policyRemark) && policyRemark.startsWith("[")) {
                policyRemark = policyRemark.substring(0, policyRemark.lastIndexOf("]") + 1);
                JSONObject jsonObject = JSONArray.fromObject(policyRemark).getJSONObject(0);
                order.setPolicyRemark(jsonObject.getString("domesticProcessRemark"));
            } else {
                order.setPolicyRemark(policyRemark);
            }
        } catch (Exception e) {
            e.printStackTrace();
            order.setPolicyRemark("获取备注异常。");
        }

        //order.setPolicyRemark(ctripOrderVO.getPolicyRemark());
        order.setRelationName(ctripOrderVO.getContactName());// 联系人
        order.setRelationMobile(ctripOrderVO.getContactTel());
        order.setPolicyType(ctripOrderVO.getPolicyCode());//政策类型
        if ("1".equals(ctripOrderVO.getFlightWay())) {
            order.setTripType(WebConstant.FLIGHT_TYPE_ONEWAY);// 单程
        } else {
            order.setTripType(WebConstant.FLIGHT_TYPE_GOBACK);
        }
        order.setcAddDate(createDate);
        order.setCreateBy("system");
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = df.parse(ctripOrderVO.getLastPrintTicketTime());
            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            Date date1 = df1.parse(date.toString());
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            order.setLastPrintTicketTime(df2.format(date1));
        } catch (Exception e) {
            order.setLastPrintTicketTime(ctripOrderVO.getLastPrintTicketTime());
        }
        orderVo.setOrder(order);

        List<Flight> flightList = new ArrayList<Flight>();
        for (CtripFlightVO ctripFlightVO : ctripOrderVO.getFlightList()) {
            Flight flight = new Flight();
            flight.setAirlineCode(ctripFlightVO.getFlight().trim().substring(0, 2));
            flight.setOrderNo(order.getOrderNo());
            flight.setOrderSource(order.getOrderSource());
            flight.setOrderShop(order.getOrderShop());
            flight.setSegmentType(order.getTripType());
            flight.setFlightNo(ctripFlightVO.getFlight().trim());// 航班号
            flight.setFlightType(ctripOrderVO.getFlightAgency());
            flight.setDepCityCode(ctripFlightVO.getDport());
            flight.setArrCityCode(ctripFlightVO.getAport());
            flight.setFlightDepDate(ctripFlightVO.getTakeOffTime().substring(0, 10));// 航班起飞日期
            flight.setDepTime(ctripFlightVO.getTakeOffTime().substring(11, 16));// 航班起飞时间
            flight.setArrTime(ctripFlightVO.getArrivalTime().substring(11, 16));// 航班到达时间
            flight.setCabin(ctripFlightVO.getSubClass());// 航班舱位代码
            flight.setPrintTicketCabin(flight.getCabin());
            flightList.add(flight);
        }
        orderVo.setFlightList(flightList);
        order.setFlightNo(flightList.get(0).getFlightNo());
        order.setDepCityCode(flightList.get(0).getDepCityCode());
        order.setArrCityCode(flightList.get(0).getArrCityCode());
        order.setFlightDate(flightList.get(0).getFlightDepDate());
        order.setAirlineCode(flightList.get(0).getAirlineCode());

        List<CtripPassengerVO> ctripPassengerList = ctripOrderVO.getPassengerList();
        List<Passenger> passengerList = new ArrayList<Passenger>();
        BigDecimal totalPrice = new BigDecimal(0);
        for (CtripPassengerVO ctripPassengerVO : ctripPassengerList) {
            Passenger passenger = new Passenger();
            passenger.setOrderNo(order.getOrderNo());
            passenger.setOrderSource(order.getOrderSource());
            passenger.setOrderShop(order.getOrderShop());
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = df.parse(ctripPassengerVO.getBirthDay());
                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                Date date1 = df1.parse(date.toString());
                DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                passenger.setBirthday(df2.format(date1));
            } catch (Exception e) {
                passenger.setBirthday(ctripPassengerVO.getBirthDay());
            }
            passenger.setName(ctripPassengerVO.getPassengerName());
            passenger.setCertType(convertCertificateType(ctripPassengerVO.getCardType()));// 乘机人证件类型
            passenger.setCertNo(ctripPassengerVO.getCardNO());// 乘机人证件号码
            passenger.setTicketNo(ctripPassengerVO.getTicketNO());
            if ("ADU".equals(ctripPassengerVO.getPassengerType())) {
                passenger.setPassengerType(WebConstant.PASSENGER_TYPE_ADULT);
            } else if ("CHD".equals(ctripPassengerVO.getPassengerType())) {
                passenger.setPassengerType(WebConstant.PASSENGER_TYPE_CHILD);
            } else {
                passenger.setPassengerType(WebConstant.PASSENGER_TYPE_BABY);
            }
            passenger.setAirlineCode(ctripOrderVO.getFlightList().get(0).getFlight().trim().substring(0, 2));// 航班航空公司二字码
            passenger.setFlightNo(ctripOrderVO.getFlightList().get(0).getFlight().trim());// 航班号
            if ("1".equals(ctripOrderVO.getFlightWay())) {
                passenger.setFlightType(WebConstant.FLIGHT_TYPE_ONEWAY);
            } else {
                passenger.setFlightType(WebConstant.FLIGHT_TYPE_GOBACK);
            }
            passenger.setDepCityCode(ctripOrderVO.getFlightList().get(0).getDport());
            passenger.setArrCityCode(ctripOrderVO.getFlightList().get(0).getAport());
            passenger.setFlightDepDate(ctripOrderVO.getFlightList().get(0).getTakeOffTime().substring(0, 10));
            passenger.setDepTime(ctripOrderVO.getFlightList().get(0).getTakeOffTime().substring(11, 16));
            passenger.setArrTime(ctripOrderVO.getFlightList().get(0).getArrivalTime().substring(11, 16));
            passenger.setCabin(ctripPassengerVO.getSubClass());
            passenger.setPrintTicketCabin(passenger.getCabin());
            passenger.setPnr(ctripPassengerVO.getRecordNo());
            passenger.setFee(ctripPassengerVO.getOilFee());
            passenger.setTax(ctripPassengerVO.getTax());
            passenger.setCreateDate(createDate);
            passenger.setPolicyType(order.getPolicyType());
            passenger.setCreateBy(order.getCreateBy());
            passenger.setTicketStatus(WebConstant.NO_TICKET);
            passenger.setStatus(WebConstant.NO_TICKET);
            if (StringUtils.isEmpty(order.getTotalTax())) {
                order.setTotalTax(passenger.getTax());
            }
            if (StringUtils.isEmpty(order.getPnr())) {
                order.setPnr(passenger.getPnr());
            }
            if (StringUtils.isEmpty(order.getCabin())) {
                order.setCabin(passenger.getCabin());
            }
            // 销售票面价格
            passenger.setTicketPrice(ctripPassengerVO.getPrintPrice());
            // 销售价格(携程写底价)
            passenger.setSellPrice(ctripPassengerVO.getCost());
            passenger.setPolicyType(order.getPolicyType());//政策类型
            passenger.setActualPrice(new BigDecimal(passenger.getSellPrice()).add(new BigDecimal(passenger.getFee()).add(new BigDecimal(passenger.getTax()))).toString());
            totalPrice = totalPrice.add(new BigDecimal(passenger.getActualPrice()));
            passengerList.add(passenger);
        }
        if ("1".equals(ctripOrderVO.getFlightWay())) {
            order.setTotalPrice(totalPrice.toString());// 总金额
        } else {
            order.setTotalPrice(totalPrice.multiply(new BigDecimal(2)).toString());// 总金额
        }
        order.setPassengerCount(passengerList.size() + "");
        orderVo.setPassengetList(passengerList);


		/*Travel travel=new Travel();
		travel.setOrderSource(order.getOrderSource());
		travel.setOrderShop(order.getOrderShop());
		travel.setOrderNo(order.getOrderNo());
		travel.setTraAddress(ctripOrderVO.getSendTicketAddr());// 邮寄地址
		travel.setReceiver(ctripOrderVO.getContactName());// 收件人
		travel.setTraPrice(ctripOrderVO.getSendTicketFee());// 行程单费
		travel.setContactPhone(ctripOrderVO.getContactTel());// 联系号码
		travel.setSendWay(WebConstant.SENDWAY_KD); // 设置默认配送方式,携程有具体方式，可选择是否使用
		travel.setAirlineCode(ctripFlightVO.getFlight().trim().substring(0, 2));
		travel.setPnr(passengerList.get(0).getPnr());
		travel.setFlightNo(ctripFlightVO.getFlight().trim());
		travel.setFlightType("0");
		travel.setDepCityCode(ctripFlightVO.getDport());
		travel.setArrCityCode(ctripFlightVO.getAport());
		travel.setFlightDepDate(ctripFlightVO.getTakeOffTime().substring(0, 10));
		travel.setFlightArrDate(travel.getFlightDepDate());
		travel.setDepTime(ctripFlightVO.getTakeOffTime().substring(11, 16));
		travel.setArrTime(ctripFlightVO.getArrivalTime().substring(11, 16));
		travel.setCabin(passengerList.get(0).getCabin());
		travel.setPrintTicketCabin("");
		orderVo.setTravel(travel);*/

        return orderVo;

    }

    /**
     * xml转换ctripOrderVo
     *
     * @param xmlContent
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private CtripOrderVO xmlToOrderVO(String xmlContent) throws Exception {
        try {
            org.dom4j.Document doc = DocumentHelper.parseText(xmlContent);
            Element rootElement = doc.getRootElement().element("Header");//头信息
            String resultStatus = rootElement.attributeValue("ResultCode");
            if ("Success".equals(resultStatus)) {
                Element orderElement = doc.getRootElement().element("OpenIssueBillInfoResponse");
                if (null == orderElement) {
                    return null;
                }
                CtripOrderVO vo = new CtripOrderVO();
                vo.setIssueBillID(orderElement.elementText("IssueBillID"));
                vo.setAirlineRecodeNo(orderElement.elementText("AirlineRecodeNo"));
                vo.setFlightClass(orderElement.elementText("FlightClass"));
                vo.setFlightWay(orderElement.elementText("FlightWay"));
                vo.setOrderDateTime(orderElement.elementText("TBookingDateTime"));
                vo.setFlightAgency(orderElement.elementText("FlightAgency"));
                vo.setPolicyRemark(orderElement.elementText("IssueRemark"));
                vo.setOrderStatus(orderElement.elementText("IssueStatus"));
                vo.setCancelIssueStatus(orderElement.elementText("CancelIssueStatus"));
                vo.setLastPrintTicketTime(orderElement.elementText("LastPrintTicketTime"));
                vo.setPolicyID(orderElement.elementText("PolicyID"));
                vo.setPolicyCode(orderElement.elementText("PolicyCode"));
                vo.setTricType(convertTictype(orderElement.elementText("PolicyType")));
                List<Element> IssueBillDetailInfo = orderElement.element("IssueBillDetailInfoList").elements("IssueBillDetailInfo");
                List<CtripPassengerVO> passengerList = new ArrayList<CtripPassengerVO>();
                List<CtripFlightVO> flightList = new ArrayList<CtripFlightVO>();
                String flightWay = orderElement.elementText("FlightWay");
                if ("1".equals(flightWay)) {
                    CtripFlightVO flight = new CtripFlightVO();
                    for (Element issueBillDetailInfo : IssueBillDetailInfo) {
                        CtripPassengerVO passenger = new CtripPassengerVO();
                        passenger.setPassengerIndex(issueBillDetailInfo.elementText("IssueBillDetailID"));
                        passenger.setPassengerName(issueBillDetailInfo.elementText("PassengerName"));
                        passenger.setGender(issueBillDetailInfo.elementText("Gender"));
                        passenger.setTicketNO(issueBillDetailInfo.elementText("TicketNO"));
                        passenger.setCardNO(issueBillDetailInfo.elementText("CardNo"));
                        passenger.setPassengerType(issueBillDetailInfo.elementText("PassengerType"));
                        passenger.setBirthDay(issueBillDetailInfo.elementText("BirthDate"));
                        passenger.setCardType(issueBillDetailInfo.elementText("CardType"));
                        passenger.setPrintPrice(issueBillDetailInfo.elementText("PrintPrice"));
                        passenger.setAgeType(issueBillDetailInfo.elementText("AgeType"));
                        passenger.setCost(issueBillDetailInfo.elementText("Cost"));
                        passenger.setOilFee(issueBillDetailInfo.elementText("OilFee"));
                        passenger.setTax(issueBillDetailInfo.elementText("Tax"));
                        passenger.setSubClass(issueBillDetailInfo.elementText("SubClass"));
                        passenger.setRecordNo(issueBillDetailInfo.elementText("AgencyRecordNO"));
                        if ((!StringUtils.isEmpty(issueBillDetailInfo.elementText("AirLineCode"))) && (!StringUtils.isEmpty(issueBillDetailInfo.elementText("TicketNO")))) {
                            passenger.setTicketNO(issueBillDetailInfo.elementText("AirLineCode") + "-" + issueBillDetailInfo.elementText("TicketNO"));
                        }
                        passengerList.add(passenger);
                        flight.setSequence(issueBillDetailInfo.elementText("Sequence"));
                        flight.setFlight(issueBillDetailInfo.elementText("Flight"));
                        flight.setCclass(issueBillDetailInfo.elementText("Class"));
                        flight.setSubClass(issueBillDetailInfo.elementText("SubClass"));
                        flight.setDport(issueBillDetailInfo.elementText("DPort"));
                        flight.setAport(issueBillDetailInfo.elementText("APort"));
                        flight.setTakeOffTime(issueBillDetailInfo.elementText("TakeOffTime"));
                        flight.setArrivalTime(issueBillDetailInfo.elementText("ArrivalTime"));
                        flight.setOfficeNo(issueBillDetailInfo.elementText("AgencyOfficeNo"));
                        flight.setCostRate(issueBillDetailInfo.elementText("CostRate"));
                    }
                    flightList.add(flight);
                    vo.setPassengerList(passengerList);
                    vo.setFlightList(flightList);
                } else {
                    //往返
                    Set<String> passengerSet = new HashSet<String>();
                    for (Element issueBillDetailInfo : IssueBillDetailInfo) {
                        if (passengerSet.contains(issueBillDetailInfo.elementText("CardNo"))) {
                            continue;
                        }
                        CtripPassengerVO passenger = new CtripPassengerVO();
                        passenger.setPassengerIndex(issueBillDetailInfo.elementText("IssueBillDetailID"));
                        passenger.setPassengerName(issueBillDetailInfo.elementText("PassengerName"));
                        passenger.setGender(issueBillDetailInfo.elementText("Gender"));
                        passenger.setTicketNO(issueBillDetailInfo.elementText("TicketNO"));
                        passenger.setCardNO(issueBillDetailInfo.elementText("CardNo"));
                        passenger.setPassengerType(issueBillDetailInfo.elementText("PassengerType"));
                        passenger.setBirthDay(issueBillDetailInfo.elementText("BirthDate"));
                        passenger.setCardType(issueBillDetailInfo.elementText("CardType"));
                        passenger.setPrintPrice(issueBillDetailInfo.elementText("PrintPrice"));
                        passenger.setAgeType(issueBillDetailInfo.elementText("AgeType"));
                        passenger.setCost(issueBillDetailInfo.elementText("Cost"));
                        passenger.setOilFee(issueBillDetailInfo.elementText("OilFee"));
                        passenger.setTax(issueBillDetailInfo.elementText("Tax"));
                        passenger.setSubClass(issueBillDetailInfo.elementText("SubClass"));
                        passenger.setRecordNo(issueBillDetailInfo.elementText("AgencyRecordNO"));
                        if ((!StringUtils.isEmpty(issueBillDetailInfo.elementText("AirLineCode"))) && (!StringUtils.isEmpty(issueBillDetailInfo.elementText("TicketNO")))) {
                            passenger.setTicketNO(issueBillDetailInfo.elementText("AirLineCode") + "-" + issueBillDetailInfo.elementText("TicketNO"));
                        }
                        passengerList.add(passenger);
                        passengerSet.add(passenger.getCardNO());
                    }
                    Set<String> flightSet = new HashSet<String>();
                    for (Element issueBillDetailInfo : IssueBillDetailInfo) {
                        if (flightSet.contains(issueBillDetailInfo.elementText("Flight"))) {
                            continue;
                        }
                        CtripFlightVO flight = new CtripFlightVO();
                        flight.setSequence(issueBillDetailInfo.elementText("Sequence"));
                        flight.setFlight(issueBillDetailInfo.elementText("Flight"));
                        flight.setCclass(issueBillDetailInfo.elementText("Class"));
                        flight.setSubClass(issueBillDetailInfo.elementText("SubClass"));
                        flight.setDport(issueBillDetailInfo.elementText("DPort"));
                        flight.setAport(issueBillDetailInfo.elementText("APort"));
                        flight.setTakeOffTime(issueBillDetailInfo.elementText("TakeOffTime"));
                        flight.setArrivalTime(issueBillDetailInfo.elementText("ArrivalTime"));
                        flight.setOfficeNo(issueBillDetailInfo.elementText("AgencyOfficeNo"));
                        flight.setCostRate(issueBillDetailInfo.elementText("CostRate"));
                        flightList.add(flight);
                        flightSet.add(flight.getFlight());
                    }
                    vo.setPassengerList(passengerList);
                    vo.setFlightList(flightList);
                }
                return vo;
            }

        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * 证件类型 <br/>
     * 0：身份证， 1：护照， 2：学生证， 3：军人证， 4：回乡证， 5：台胞证， 6：港澳台胞， 7：国际海员， 8：外国人永久居留证， 9：其他
     *
     * @param type
     * @return String
     */
    private String convertCertificateType(String type) {
        /*
         * 去哪儿证件类型： NI=身份证 PP=护照 ID=其他 HX=回乡证 TB=台胞证 GA=港澳通行证
         */
        if ("身份证".equals(type) || "1".equals(type)) {//身份证
            return "0";
        } else if ("2".equals(type)) {//护照
            return "1";
        } else if ("99".equals(type)) {//其他
            return "9";
        } else if ("7".equals(type)) {//回乡证
            return "4";
        } else if ("8".equals(type)) {//台胞证
            return "5";
        } else if ("10".equals(type)) {//港澳通行证
            return "6";
        } else {
            return "9";
        }
    }

    private String convertTictype(String type) {
        if ("1".equals(type)) {
            return "NFD";
        } else if ("2".equals(type)) {
            return "清仓产品";
        } else if ("3".equals(type)) {
            return "商旅产品";
        } else if ("5".equals(type)) {
            return "中转产品";
        } else if ("6".equals(type)) {
            return "包机";
        } else if ("7".equals(type)) {
            return "切位";
        } else if ("8".equals(type)) {
            return "航司VIP卡";
        } else if ("10000".equals(type)) {
            return "普通";
        } else if ("10001".equals(type)) {
            return "规则运价";
        } else {
            return type;
        }
    }

    /**
     * 修改改签单金额
     *
     * @param orderSource
     * @param orderShop
     * @throws Exception
     */
    public void updateChangeOrder(String orderSource, String orderShop, String status) {
        String user = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "user");
        String pass = DictUtils.getDictCode("order_import_cfgxc" + orderShop, "pass");
        String passWord = Md5Util.md5(user + "#" + pass).toLowerCase();
        Assert.notNull(user, "user不能为空");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        String startDate = date + "T00:00:00";
        String endDate = date + "T23:59:59";
        StringBuilder sb = new StringBuilder();
        sb.append("<Request UserName=\"" + user + "\" UserPassword=\"" + passWord + "\">");
        sb.append("<OpenQueryExchangeRequest>");
        sb.append("<FromDateTime>" + startDate + "</FromDateTime>");
        sb.append("<ToDateTime>" + endDate + "</ToDateTime>");
        sb.append("<RebookQueryOptionType>" + status + "</RebookQueryOptionType>");
        sb.append("</OpenQueryExchangeRequest>");
        sb.append("</Request>");
        String xmlBody = sb.toString();
        String xmla = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Handle xmlns=\"http://tempuri.org/\"><requestXML>"
                + xmlBody.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</requestXML></Handle></soap:Body></soap:Envelope>";
        String url = "https://flights.ws.ctrip.com/Flight.Order.SupplierOpenAPI/SearchExchangeList.asmx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.setEntity(new StringEntity(xmla, "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Document docd = Jsoup.parse(content);
            if (docd.getElementsByTag("HandleResult") == null) {
                return;
            }
            String base = docd.getElementsByTag("HandleResult").first().text();
            String data = Base64Util.base64Decode(base);
            String xmlStr = parse(data);
            //System.out.println(xmlStr);

            org.dom4j.Document doc = DocumentHelper.parseText(xmlStr);
            Element rootElement = doc.getRootElement().element("Header");//头信息
            String resultStatus = rootElement.attributeValue("ResultCode");
            if ("Success".equals(resultStatus)) {
                Element eOrderRoot = doc.getRootElement().element("OpenQueryExchangeResponse");
                if (eOrderRoot == null) {
                    return;
                }
                Element eOrderInfoList = eOrderRoot.element("OpenApiQueryExchangeItems");
                if (eOrderInfoList == null) {
                    return;
                }
                List<Element> changeElementList = eOrderInfoList.elements("OpenQueryExchangeItem");
                for (Element changeEle : changeElementList) {
                    String RbkId = changeEle.elementText("RbkId");
                    String passengerName = changeEle.elementTextTrim("PassengerName");
                    String oldflightNo = changeEle.elementTextTrim("OldFlight");
                    String oldPnr = changeEle.elementTextTrim("OldRecordNo");
                    QueryWrapper<Passenger> query = new QueryWrapper<Passenger>();
                    query.eq("order_source", InterfaceConstant.ORDER_SOURCE_CTRIP);
                    query.eq("name", passengerName);
                    //query.eq("pnr", oldPnr);
                    List<Passenger> passengerList = passengerService.findByQueryWapper(query);
                    Passenger passenger = new Passenger();
                    if (passengerList.size() < 1) {
                        log.info("携程改签单" + RbkId + "没有匹配到乘客");
                        //continue;
                    } else {
                        passenger = passengerList.get(0);
                    }
                    Change change = new Change();
                    change.setRevenuePrice(new BigDecimal(changeEle.elementTextTrim("RebookingQueryFee")).toString());
                    UpdateWrapper<Change> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("NEW_C_ORDER_NO", RbkId);
                    updateWrapper.eq("passenger_Name", passengerName);
                    changeService.updateByWrapper(change, updateWrapper);
                    OrderOperateLog logg = new OrderOperateLog();
                    logg.setOrderNo(passenger.getOrderNo());
                    logg.setType("订单处理");
                    logg.setName("SYSTEM");
                    logg.setContent("修改改签费用-->" + change.getRevenuePrice());
                    logService.saveLog(logg);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpPost.abort();
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 鹏朋下单接口参数配置
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
            p.setBigPnr(orderVO.getBigPnr());
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
                if (type.equals("1")) {
                    type = "PP";
                } else if (type.equals("0")) {
                    type = "NI";
                } else {
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
