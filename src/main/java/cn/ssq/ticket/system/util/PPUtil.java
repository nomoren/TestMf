package cn.ssq.ticket.system.util;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.pp.*;
import cn.ssq.ticket.system.service.FlightService;
import cn.ssq.ticket.system.service.OrderImport.impl.CtripOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.JiuOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TTsOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TcOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.PurchaseService;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 鹏鹏接口调用工具类
 */
public class PPUtil {

    private static Logger logg = LoggerFactory.getLogger(PPUtil.class);

    private static String userId = "DGSSQADMIN";
    private static String businessNo = "DGSSQ";
    private static String systemId = "VETECH";
    private static String url = "http://api.airpp.net/openapi/buyer.shtml";
    private static String key = "94d1f8af88b330cb96d1cfd538ef235e";
    public  static final String lxrPhone="17338146572";

    private static FlightService flightService = SpringContextHolder.getBean(FlightService.class);
    private static OrderService orderService = SpringContextHolder.getBean(OrderService.class);
    private static TTsOrderService ttsOrderService = SpringContextHolder.getBean(TTsOrderService.class);
    private static CtripOrderService ctripOrderService = SpringContextHolder.getBean(CtripOrderService.class);
    private static JiuOrderService jiuOrderService = SpringContextHolder.getBean(JiuOrderService.class);
    private static TcOrderService tcOrderService = SpringContextHolder.getBean(TcOrderService.class);
    private static PurchaseService purchaseService = SpringContextHolder.getBean(PurchaseService.class);
    public static  Map<String,String> KY_ZH = getKYZH();
    public static  Map<String,String> getKYZH(){
        Map<String,String> KY_ZH=new HashMap<>();
        try {
            List<String> list = FileUtils.readLines(new File( "D:\\KY-ZH.txt"), "UTF-8");
            for (String s : list) {
                if(StringUtils.isNotEmpty(s)){
                    String[] split = s.split(",");
                    KY_ZH.put(split[0],split[1]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return KY_ZH;

    }


    public static String postFile(String url,String xml){
        CloseableHttpClient httpClient = getHttpClientL();
        CloseableHttpResponse response = null;

        HttpPost post = new HttpPost(url);
        try{

            post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

            List<BasicNameValuePair> pairList = new ArrayList<>(8);
            pairList.add(new BasicNameValuePair("data", xml));

            post.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));

            response = httpClient.execute(post);
            HttpEntity resEntity = response.getEntity();
            return EntityUtils.toString(resEntity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                response.close();
                post.abort();
                httpClient.close();
            } catch (IOException e) {

            }
        }
        return null;
    }


    public static String refundOrder(PPRefund refund){
        String str = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 0);
            String now = DateFormatUtils.format(calendar.getTime(),"yyyy-MM-dd HH:mm:ss");
            String operateTime = now;
            String service = "refundApply";
            String sign = DigestUtils.md5Hex(systemId+businessNo+userId+operateTime+key);
            refund.setUserId(userId);
            refund.setBusinessNo(businessNo);
            refund.setSystemId(systemId);
            refund.setOperateTime(operateTime);
            refund.setSign(sign);
            refund.setService(service);
            String s = beanToXml(refund, PPRefund.class);
            str = postFile(url, s);
        } catch (Exception e) {
            logg.error(refund.getMfOrderNo()+"退票申请失败",e);
        }
        return str;
    }

    public static String refundDetail(PPRefundDetail refund){
        String str = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 0);
            String now = DateFormatUtils.format(calendar.getTime(),"yyyy-MM-dd HH:mm:ss");
            String operateTime = now;
            String service = "getRefundOrderBuyInfo";
            String sign = DigestUtils.md5Hex(systemId+businessNo+userId+operateTime+key);
            refund.setUserId(userId);
            refund.setBusinessNo(businessNo);
            refund.setSystemId(systemId);
            refund.setOperateTime(operateTime);
            refund.setSign(sign);
            refund.setService(service);
            String s = beanToXml(refund, PPRefundDetail.class);
            str = postFile(url, s);
        } catch (Exception e) {
            logg.error(refund.getRefundNo()+"查询退票单详情失败",e);
        }
        return str;
    }


    public static String getRealtimePolicy(String flightDate,String fromCity,String toCity,String ticketPrice,String flightNo,String seatClass,String disCount){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 0);
        String now = DateFormatUtils.format(calendar.getTime(),"yyyy-MM-dd HH:mm:ss");
        String operateTime = now;
        String service = "getRealtimePolicy";
        String sign = DigestUtils.md5Hex(systemId+businessNo+userId+operateTime+key);
        RealtimePolicy policy = new RealtimePolicy();
        policy.setUserId(userId);
        policy.setBusinessNo(businessNo);
        policy.setService(service);
        policy.setSign(sign);
        policy.setSystemId(systemId);
        policy.setOperateTime(operateTime);
        policy.setFlightDate(flightDate);
        policy.setFromCity(fromCity);
        policy.setToCity(toCity);
        policy.setPassengerType("1");
        policy.setTravelType("1");
        policy.setAirways("ZH");
        policy.setTicketPrice(ticketPrice);
        policy.setFlightNo(flightNo);
        policy.setSeatClass(seatClass);
        policy.setDiscount(disCount);
        String str = null;
        try {
            str = beanToXml(policy, RealtimePolicy.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String msg = null;
        if(str!=null){
            msg = postFile(url,str);
        }else{
            System.out.println("航班数据存入失败");
        }
        return msg;
    }

    /**
     *获取鹏鹏低价，需要先获取票面价
     * @return
     */
    public static Map<String,String> getLowPriceNoTP(OrderVO orderVO,String ticketPrice){
        Map<String,String> map= Maps.newHashMap();
        try {
            if(StringUtils.isEmpty(orderVO.getPnr())){
                String pnr="";
                for(int i=0;i<4;i++){
                    Thread.sleep(10000);
                    pnr = orderService.getPnrByOrderNo(orderVO.getOrderNo());
                    if(StringUtils.isNotEmpty(pnr)){
                        break;
                    }
                }
                orderVO.setPnr(pnr);
            }

            if(StringUtils.isEmpty(orderVO.getPnr())){
                logg.info(orderVO.getOrderNo()+"pnr为空");
                return map;
            }
            if(StringUtils.isEmpty(orderVO.getBigPnr())){
                String param="cmd=rt_parse&pnr="+orderVO.getPnr();
                String result = EtermHttpUtil.cmd("apiyjsl3", param);
                org.dom4j.Document bills = null;
                try {
                    bills = DocumentHelper.parseText(result);
                    org.dom4j.Element root = bills.getRootElement();
                    String bigP = root.elementText("b_pnr");
                    orderVO.setBigPnr(bigP);
                    orderService.updateBigPnr(bigP, orderVO.getOrderId());
                } catch (DocumentException e) {
                    orderVO.setBigPnr("O12345");
                }
            }


            QueryWrapper<Flight> query=new QueryWrapper<Flight>();
            query.eq("order_id", orderVO.getOrderId());
            List<Flight> selectFlightInfo = flightService.getByQueryW(query);
            Flight flight = selectFlightInfo.get(0);
            String flightNo = flight.getFlightNo();
            if(flightNo.startsWith("KY")){
                String key=flight.getDepCityCode()+flight.getArrCityCode()+flightNo;
                String flightVaule = KY_ZH.get(key);
                if(StringUtils.isNotEmpty(flightVaule)){
                    flightNo=flightVaule;
                }else{
                    flightNo=flightNo.replace("KY","ZH");
                }
            }

            String policyId = "";
            String realtimePolicy = getRealtimePolicy(flight.getFlightDepDate(), flight.getDepCityCode(), flight.getArrCityCode(), ticketPrice, flightNo, flight.getCabin(), "0");
            String result = getJsonString(realtimePolicy);

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
            if(result.contains("\"status\":[\"0\"]")){
                JSONObject fromObject = JSONObject.fromObject(result);
                JSONObject jsonObject = fromObject.getJSONObject("response").getJSONArray("policys").getJSONObject(0);
                policyId = jsonObject.getJSONArray("policy").getJSONObject(0).getJSONArray("policyId").getString(0);

                CreateOrder c = new CreateOrder();
                c.setPnrNo(orderVO.getPnr());
                c.setPolicyId(policyId);
                c.setProductType("1");
                c.setTravelType("1");
                c.setTravelRange(flight.getDepCityCode()+flight.getArrCityCode());
                c.setFlightNo(flightNo);
                c.setRealFlightNo(flightNo);
                c.setSeatClass(flight.getCabin());
                c.setFromDatetime(flight.getFlightDepDate()+" "+flight.getDepTime().substring(0,5));
                c.setToDatetime(flight.getFlightDepDate()+" "+flight.getArrTime().substring(0,5));
                c.setTofromterminal("T2");
                c.setTerminal("A");
                c.setPassenger(name.toString().substring(1));
                c.setPassengerType(passengerType.toString().substring(1));
                c.setCardType(cardType.toString().substring(1));
                c.setCardId(certNo.toString().substring(1));
                c.setPassengerMobile(lxrPhone);
                Integer valueOf = Integer.valueOf(orderVO.getPassengetList().get(0).getTicketPrice().split("\\.")[0]);
                if(valueOf<=0){
                    valueOf=203;
                }
                int sub=valueOf-100;
                c.setScny(sub+"");
                c.setYq("0");
                c.setTax("50");
                c.setSettlementPrice(sub+"");
                c.setDisCount("0");
                c.setBigPnrNo(orderVO.getBigPnr());
                result = createOrder(c);
                System.out.println(orderVO.getOrderNo()+"获取到的低价result;"+result);
                if(StringUtils.isNotBlank(result)){
                    org.dom4j.Document bills= DocumentHelper.parseText(result);
                    org.dom4j.Element root = bills.getRootElement();
                    String elementText = root.elementText("errorMessage");
                    if(elementText.contains("解析账单价")){
                        String replace = elementText.replace("：", ":");
                        String string = replace.split(":")[1];
                        String replace2 = string.replace("，", ",");
                        String realPrice = replace2.split(",")[0];
                        System.out.println(orderVO.getOrderNo()+":获取到的低价:"+realPrice);
                        map.put("ticketPrice",realPrice);
                        //获取销售价
                        realtimePolicy = getRealtimePolicy(flight.getFlightDepDate(), flight.getDepCityCode(), flight.getArrCityCode(), realPrice, flight.getFlightNo(), flight.getCabin(), "0");
                        result = getJsonString(realtimePolicy);
                        Gson gson=new Gson();
                        String sellPrice = "";
                        String tPrice = "";
                        JsonObject jo = gson.fromJson(result, JsonObject.class);
                        if (result.contains("\"status\":[\"0\"]")) {
                            JsonArray datas = jo.getAsJsonObject("response").getAsJsonArray("policys");
                            for (JsonElement data : datas) {
                                JsonElement p = data.getAsJsonObject().get("policy");
                                JsonArray policys = p.getAsJsonArray();
                                for (JsonElement policy : policys) {
                                    Double buyPrice = Double.valueOf(policy.getAsJsonObject().get("buyPrice").getAsString());
                                    if (sellPrice.equals("")) {
                                        sellPrice = policy.getAsJsonObject().get("buyPrice").getAsString();
                                       // policyId = policy.getAsJsonObject().get("policyId").getAsString();
                                       // tPrice = policy.getAsJsonObject().get("ticketPrice").getAsString();
                                    } else if (buyPrice < Double.valueOf(sellPrice)) {
                                        sellPrice = policy.getAsJsonObject().get("buyPrice").getAsString();
                                        //policyId = policy.getAsJsonObject().get("policyId").getAsString();
                                        //tPrice = policy.getAsJsonObject().get("ticketPrice").getAsString();
                                    }
                                }
                            }
                            double p = Double.valueOf(sellPrice) * Double.valueOf(orderVO.getPassengerCount());
                            double sell = Double.valueOf(50) * Double.valueOf(orderVO.getPassengerCount());
                            double ppPrice = p + sell;
                            map.put("sellPrice",ppPrice+"");
                        }
                    }else {
                        logg.info(orderVO.getOrderNo()+":获取低价失败:"+result);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     *获取鹏鹏低价，真实票面价
     * @return
     */
    public static Map<String,String> getLowPriceNo(OrderVO orderVO,String ticketPrice){
        Map<String,String> map= Maps.newHashMap();
        try {
            if(StringUtils.isEmpty(orderVO.getPnr())){
                String pnr="";
                for(int i=0;i<4;i++){
                    Thread.sleep(10000);
                    pnr = orderService.getPnrByOrderNo(orderVO.getOrderNo());
                    if(StringUtils.isNotEmpty(pnr)){
                        break;
                    }
                }
                orderVO.setPnr(pnr);
            }

            if(StringUtils.isEmpty(orderVO.getPnr())){
                logg.info(orderVO.getOrderNo()+"pnr为空");
                return map;
            }
            if(StringUtils.isEmpty(orderVO.getBigPnr())){
                String param="cmd=rt_parse&pnr="+orderVO.getPnr();
                String result = EtermHttpUtil.cmd("apiyjsl3", param);
                org.dom4j.Document bills = null;
                try {
                    bills = DocumentHelper.parseText(result);
                    org.dom4j.Element root = bills.getRootElement();
                    String bigP = root.elementText("b_pnr");
                    orderVO.setBigPnr(bigP);
                    orderService.updateBigPnr(bigP, orderVO.getOrderId());
                } catch (DocumentException e) {
                    orderVO.setBigPnr("O12345");
                }
            }

            QueryWrapper<Flight> query=new QueryWrapper<Flight>();
            query.eq("order_id", orderVO.getOrderId());
            List<Flight> selectFlightInfo = flightService.getByQueryW(query);
            Flight flight = selectFlightInfo.get(0);
            String realtimePolicy = getRealtimePolicy(flight.getFlightDepDate(), flight.getDepCityCode(), flight.getArrCityCode(), ticketPrice, flight.getFlightNo(), flight.getCabin(), "0");
            String result = getJsonString(realtimePolicy);

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
                if(type.equals("NI")){

                }else if(type.equals("1")){
                    type = "P";
                }else{
                    type = "ID";
                }
                cardType.append("|"+type);
            }
            Gson gson=new Gson();
            String sellPrice = "";
            String tPrice = "";
            JsonObject jo = gson.fromJson(result, JsonObject.class);
            if(result.contains("\"status\":[\"0\"]")){
                JsonArray datas = jo.getAsJsonObject("response").getAsJsonArray("policys");
                for (JsonElement data : datas) {
                    JsonElement p = data.getAsJsonObject().get("policy");
                    JsonArray policys = p.getAsJsonArray();
                    for (JsonElement policy : policys) {
                        Double buyPrice = Double.valueOf(policy.getAsJsonObject().get("buyPrice").getAsString());
                        if (sellPrice.equals("")) {
                            sellPrice = policy.getAsJsonObject().get("buyPrice").getAsString();
                            // policyId = policy.getAsJsonObject().get("policyId").getAsString();
                            // tPrice = policy.getAsJsonObject().get("ticketPrice").getAsString();
                        } else if (buyPrice < Double.valueOf(sellPrice)) {
                            sellPrice = policy.getAsJsonObject().get("buyPrice").getAsString();
                            //policyId = policy.getAsJsonObject().get("policyId").getAsString();
                            //tPrice = policy.getAsJsonObject().get("ticketPrice").getAsString();
                        }
                    }
                }
                double p = Double.valueOf(sellPrice) * Double.valueOf(orderVO.getPassengerCount());
                double sell = Double.valueOf(50) * Double.valueOf(orderVO.getPassengerCount());
                double ppPrice = p + sell;
                map.put("sellPrice",ppPrice+"");
                map.put("ticketPrice",ticketPrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String createOrder(CreateOrder createOrder){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 0);
        String now = DateFormatUtils.format(calendar.getTime(),"yyyy-MM-dd HH:mm:ss");
        String operateTime = now;
        String service = "createOrder";
        String sign = DigestUtils.md5Hex(systemId+businessNo+userId+operateTime+key);
        createOrder.setUserId(userId);
        createOrder.setBusinessNo(businessNo);
        createOrder.setSystemId(systemId);
        createOrder.setOperateTime(operateTime);
        createOrder.setSign(sign);
        createOrder.setService(service);
        createOrder.setIfChangeOrder("0");
        createOrder.setIfNFDSpecial("1");
        createOrder.setIfMzcwChangOrder("0");
        String str = null;
        try {
            str = beanToXml(createOrder, CreateOrder.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String msg = null;
        if(str!=null){
            msg = postFile(url,str);
        }else{
            System.out.println("航班数据存入失败");
        }
        return msg;
    }

    public static String getPayLink(String orderNo,String orderPrice){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 0);
        String now = DateFormatUtils.format(calendar.getTime(),"yyyy-MM-dd HH:mm:ss");
        String operateTime = now;
        String service = "orderPayPTLinkService";
        String sign = DigestUtils.md5Hex(systemId+businessNo+userId+operateTime+key);
        PayPTLink policy = new PayPTLink();
        policy.setUserId(userId);
        policy.setBusinessNo(businessNo);
        policy.setService(service);
        policy.setSign(sign);
        policy.setSystemId(systemId);
        policy.setOperateTime(operateTime);
        policy.setOrderNo(orderNo);
        policy.setOrderAmt(orderPrice);
        policy.setHddxmc("gnjpZfCallBack");
        policy.setOrderType("1");
        policy.setYyfhcs(userId);
        //policy.setPnrContent(pnrContent);
        //policy.setPatContent(EtermHttpUtil.cmd("apiyjsl3", "cmd=PAT&pnr=JMJTPR"));
        String str = null;
        try {
            str = beanToXml(policy, PayPTLink.class);

            //str=B2Binterface.policyEntityToXml(policy);
            //System.out.println(str);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String msg = null;
        if(str!=null){
            msg = postFile(url,str);
        }else{
            System.out.println("航班数据存入失败");
        }
        return msg;
    }


    public static void main(String[] args) {
       /* PPRefundDetail refundDetail=new PPRefundDetail();
        refundDetail.setRefundNo("TF20120901EC");
        System.out.println(refundDetail(refundDetail));*/


    }


    private static String beanToXml(Object obj,Class<?> load) throws Exception {
        JAXBContext context = JAXBContext.newInstance(load);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		/*	marshaller.setProperty(CharacterEscapeHandler.class.getName(), new CharacterEscapeHandler() {
			public void escape(char[] ch, int start,int length, boolean isAttVal, Writer writer) throws IOException {
				writer.write(ch, start, length);
			}
		});*/
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj,writer);
        return writer.toString();
    }






    public static String getJsonString(String result){
        JSONObject obj = new net.sf.json.JSONObject();
        String ss = "";
        try{
            InputStream is = new ByteArrayInputStream(result.getBytes("utf-8"));
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(is);
            Element root = doc.getRootElement();
            obj.put(root.getName(), iterateElement(root));
            ss = obj.toString();
            return ss;
        }catch(Exception e){
            e.printStackTrace();
            return ss;
        }
    }

    public static Map iterateElement(Element element) {
        List jiedian = element.getChildren();
        Element et = null;
        Map obj = new HashMap();
        List list = null;
        for (int i = 0; i < jiedian.size(); i++) {
            list = new LinkedList();
            et = (Element) jiedian.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.getChildren().size() == 0)
                    continue;
                if (obj.containsKey(et.getName())) {
                    list = (List) obj.get(et.getName());
                }
                list.add(iterateElement(et));
                obj.put(et.getName(), list);
            } else {
                if (obj.containsKey(et.getName())) {
                    list = (List) obj.get(et.getName());
                }
                list.add(et.getTextTrim());
                obj.put(et.getName(), list);
            }
        }
        return obj;
    }


    public static CloseableHttpClient getHttpClientL(){
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

    /**
     * 进行支付鹏朋低价订单流程
     * @param
     */
    public static boolean payPPDJ(OrderVO orderVo,int maxKui,String ticketPrice) throws Exception{
        Gson gson = new Gson();
        String disCount = "0";
        PPMSG pp = null;
        //区分订单来源获取信息
        if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderVo.getOrderSource())) {
            pp = ttsOrderService.getPPMSG(orderVo,ticketPrice);
        } else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderVo.getOrderSource())) {
            pp = ctripOrderService.getPPMSG(orderVo,ticketPrice);
        } else if (InterfaceConstant.ORDER_SOURCE_TB.equals(orderVo.getOrderSource())) {

        } else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderVo.getOrderSource())) {
            pp=jiuOrderService.getPPMSG(orderVo,ticketPrice);
        } else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderVo.getOrderSource())) {
            pp = tcOrderService.getPPMSG(orderVo,ticketPrice);
        }
        if (pp == null) {
            LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "鹏朋采购失败，取订单信息失败");
            updateStatus(orderVo);
            return false;
        }
        String policyId = "";
        String sellPrice = "";
        String tPrice = "";
        String orderNo = "";
        String passengerMobile = "17338146572";
        String result = getRealtimePolicy(pp.getFlightDate(), pp.getFromCity(), pp.getToCity(), pp.getTicketPrice(), pp.getFlightNo(), pp.getSeatClass().substring(0, 1), disCount);
        result = getJsonString(result);
        JsonObject jo = gson.fromJson(result, JsonObject.class);
        if (result.contains("\"status\":[\"0\"]")) {
            JsonArray datas = jo.getAsJsonObject("response").getAsJsonArray("policys");
            for (JsonElement data : datas) {
                JsonElement p = data.getAsJsonObject().get("policy");
                JsonArray policys = p.getAsJsonArray();
                for (JsonElement policy : policys) {
                    Double buyPrice = Double.valueOf(policy.getAsJsonObject().get("buyPrice").getAsString());
                    if (sellPrice.equals("")) {
                        sellPrice = policy.getAsJsonObject().get("buyPrice").getAsString();
                        policyId = policy.getAsJsonObject().get("policyId").getAsString();
                        tPrice = policy.getAsJsonObject().get("ticketPrice").getAsString();
                    } else if (buyPrice < Double.valueOf(sellPrice)) {
                        sellPrice = policy.getAsJsonObject().get("buyPrice").getAsString();
                        policyId = policy.getAsJsonObject().get("policyId").getAsString();
                        tPrice = policy.getAsJsonObject().get("ticketPrice").getAsString();
                    }
                }
            }
        } else {
            logg.info(orderVo.getOrderNo() + "获取鹏朋政策失败." + result);
            LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "鹏朋采购失败，获取鹏朋政策失败");
            return false;
        }
        if ("".equals(sellPrice) || "".equals(policyId)) {
            if (result.contains("多窗口同时操作")) {
                logg.info(orderVo.getOrderNo() + "多窗口同时操作，进行重试");

            } else {
                logg.info(orderVo.getOrderNo() + "没有匹配的价格或政策");
            }
            LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "鹏朋采购失败，获取鹏朋政策失败");
            return false;
        }

        double p = Double.valueOf(sellPrice) * Double.valueOf(orderVo.getPassengerCount());
        double sell = Double.valueOf(50) * Double.valueOf(orderVo.getPassengerCount());
        double ppPrice = p + sell;
        //logg.info(orderVo.getOrderNo()+"的 sellPrice"+ppPrice);
        Double totalPrice = Double.valueOf(orderVo.getTotalPrice());
        if (Double.valueOf(ppPrice).doubleValue() < totalPrice.doubleValue()) {
           /* if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderVo.getOrderSource())) {
                String ticType = orderVo.getTicType();
                if ("行程单".equals(ticType) && orderVo.getPolicyType().contains("520")) {
                    logg.info(orderVo.getOrderNo() + "赚钱不出" + "===" + orderVo.getOrderNo() + "订单价格：" + totalPrice + "===采购价格：" + ppPrice);
                    LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "政策说赚钱不出，");
                    return;
                }
            }*/
        }
        int kui = Integer.valueOf(orderVo.getPassengerCount()).intValue() * maxKui;
        if (Double.valueOf(ppPrice).doubleValue() - totalPrice.doubleValue() > kui) {
            System.out.println(orderVo.getOrderNo() + "亏太多不出");
            LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "采购失败,超过亏损值,下单价格：" + ppPrice);
            return false;
        }

        CreateOrder c = new CreateOrder();
        c.setPnrNo(pp.getPnr());
        c.setPolicyId(policyId);
        c.setProductType("1");
        c.setTravelType("1");
        c.setTravelRange(pp.getFromCity() + pp.getToCity());
        c.setFlightNo(pp.getFlightNo());
        c.setRealFlightNo(pp.getFlightNo());
        c.setSeatClass(pp.getSeatClass());
        c.setFromDatetime(pp.getFromDatetime());
        c.setToDatetime(pp.getToDatetime());
        c.setTofromterminal("T2");
        c.setTerminal("A");
        c.setPassenger(pp.getPassengerName());
        c.setPassengerType(pp.getPassengerType());
        c.setCardType(pp.getCardType());
        c.setCardId(pp.getPassengerCard());
        c.setPassengerMobile(passengerMobile);
        c.setScny(pp.getTicketPrice());
        c.setYq("0");
        c.setTax("50");
        double sPrcie = Double.valueOf(sellPrice) * Double.valueOf(orderVo.getPassengerCount());
        System.out.println("PP销售价：" + sPrcie);
        BigDecimal bg = new BigDecimal(sPrcie).setScale(2, RoundingMode.UP);
        c.setSettlementPrice(bg.doubleValue() + "");
        c.setDisCount(disCount);
        c.setBigPnrNo(pp.getBigPnr());
        System.out.println(orderVo.getOrderNo() + "发送创建订单请求:" + c.toString());
        result = createOrder(c);
        result = getJsonString(result);

        jo = gson.fromJson(result, JsonObject.class);
        String paymentAmount = "";
        if (result.contains("\"status\":[\"0\"]")) {
            try {
                JsonObject response = jo.getAsJsonObject("response");
                orderNo = response.getAsJsonArray("orderNo").getAsString();
                paymentAmount = response.getAsJsonArray("paymentAmount").getAsString();
                logg.info(orderVo.getOrderNo() + ",创建订单成功:" + orderNo);
                Double totalPric = Double.valueOf(orderVo.getTotalPrice());
                System.out.println("===" + orderVo.getOrderNo() + "订单价格：" + totalPric + "===采购价格：" + paymentAmount);
            } catch (Exception e) {
                logg.info(orderVo.getOrderNo() + "需要查询支付结果");
                LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "创建订单异常。");
                return false;
            }
        } else {
            logg.info(orderVo.getOrderNo() + "创建订单失败:" + result);
            LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "创建订单失败，可能没有低价。");
            return false;
        }
        if ("".equals(orderNo)) {
            logg.info(orderVo.getOrderNo() + "获取订单号失败");
            LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "获取订单号失败,");
            return false;
        }

        //查询订单状态，锁单支付
        if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderVo.getOrderSource())) {
            String orderStatus = ttsOrderService.getOrderStatus(orderVo.getOrderNo());
            if ("TICKET_LOCK".equals(orderStatus)) {
                logg.info(orderVo.getOrderNo() + "修改为出票中成功...");
            } else {
                if (!"PAY_OK".equals(orderStatus)) {
                    LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "订单状态不可支付，");
                    return false;
                }
                JSONObject updateOrderStatus = ttsOrderService.updateOrderStatus(orderVo.getOrderNo(), orderVo.getOrderNo().substring(0, 3), true);
                if(updateOrderStatus==null){
                    Thread.sleep(10000);
                    updateOrderStatus = ttsOrderService.updateOrderStatus(orderVo.getOrderNo(), orderVo.getOrderNo().substring(0, 3), true);
                }
                if (updateOrderStatus != null) {
                    if (updateOrderStatus.getInt("ret") == 1) {
                        System.out.println(orderVo.getOrderNo() + "修改为出票中成功...");
                    } else {
                        System.out.println(orderVo.getOrderNo() + "修改为出票中失败");
                        LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "修改为出票中失败，");
                        return false;
                    }
                } else {
                    System.out.println(orderVo.getOrderNo() + "修改为出票中失败");
                    LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "修改为出票中失败，");
                    return false;
                }
            }

        } else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderVo.getOrderSource())) {
            String orderStatus = ctripOrderService.getOrderStatus(orderVo.getOrderNo(), orderVo.getOrderShop());
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(orderStatus)) {
                String[] split = orderStatus.split(":");
                if (!"1".equals(split[0]) || !"0".equals(split[1])) {
                    LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "订单状态不可支付，");
                    return false;
                }
            } else {
                LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "未获取到订单状态，");
                return false;
            }
            //ctripOrderService.assignOrder(orderVo.getOrderNo(), orderVo.getOrderShop(), "1", true);
        } else if (InterfaceConstant.ORDER_SOURCE_TB.equals(orderVo.getOrderSource())) {

        } else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderVo.getOrderSource())) {
            String orderStatus = jiuOrderService.getOrderStatus(orderVo.getOrderNo());
            //logg.info(orderVo.getOrderNo()+"订单状态："+orderStatus);
            if (!"PAY_OK".equals(orderStatus)) {
                logg.info(orderVo.getOrderNo() + "订单状态不可支付：" + orderStatus);
                LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "订单状态不可支付，");
                return false;
            }
            //jiuOrderService.locked(orderVo.getOrderNo(), true);
        } else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderVo.getOrderSource())) {
            //tcOrderService.lockedOrder(orderVo.getOrderNo(), true);
            //同程锁单{"success":true,"errorCode":null,"message":""}
        }


        boolean payOk = true;
        String tradeNo = "";
        String payType = DictUtils.getDictCode("pp_payType", "payType");
       // String payType = "0";
        try {
            if ("1".equals(payType)) {
                //汇付支付
                String payLink = getPayLink(orderNo, paymentAmount);
                payLink = getJsonString(payLink);
                JsonObject joo = gson.fromJson(payLink, JsonObject.class);
                JsonObject asJsonObject = joo.getAsJsonObject("response");
                String payUrl = asJsonObject.getAsJsonArray("url").get(0).getAsString();
                System.out.println(payUrl);
                if (org.apache.commons.lang3.StringUtils.isEmpty(payUrl)) {
                    logg.info(orderVo.getOrderNo(), "获取支付连接失败，");
                    payOk = false;
                }
                String nbJylsh = HuiFuUtil.getNbJylsh(payUrl);
                if (org.apache.commons.lang3.StringUtils.isEmpty(nbJylsh)) {
                    logg.info(orderVo.getOrderNo(), "获取支付订单编号失败，");
                    payOk = false;
                }
                HuiFuPay payFrom = HuiFuUtil.getPayFrom(nbJylsh);
                if (null == payFrom) {
                    logg.info(orderVo.getOrderNo(), "获取支付表单失败，");
                    payOk = false;
                }
                String payInfo = HuiFuUtil.getPayInfo(payFrom.getMerId(), payFrom.getOrdId(), payFrom.getOrdAmt());
                payFrom.setPayInfo(payInfo);
                String pay = HuiFuUtil.pay(payFrom);
                logg.info(orderVo.getOrderNo() + "汇付支付结果" + pay);
                org.dom4j.Document payText = DocumentHelper.parseText(pay);
                org.dom4j.Element root = payText.getRootElement();
                String respCode = root.elementText("RespCode");
                if (!"000000".equals(respCode)) {
                    String ErrorMsg = root.elementText("ErrorMsg");
                    LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "支付失败，" + ErrorMsg);
                    payOk = false;
                }
                if (payFrom != null) {
                    paymentAmount = payFrom.getOrdAmt();
                    tradeNo = payFrom.getOrdId();
                }
            } else {
                //支付宝支付
                PayWithholdingNew pwhn = new PayWithholdingNew();
                pwhn.setOrderNo(orderNo);
                pwhn.setOrderType("1");
                pwhn.setHddxmc("gnjpCallBack");
                pwhn.setCplx("0100");
                result = payWithholdingNew(pwhn);
                result = getJsonString(result);
                logg.info(orderVo.getOrderNo() + "支付宝支付结果:" + result);
                //jo = gson.fromJson(result, JsonObject.class);
                if (!result.contains("\"status\":[\"0\"]")) {
                    payOk = false;
                }
            }
        } catch (Exception e) {
            logg.error(orderVo.getOrderNo() + "支付异常", e);
            payOk = false;
        }

        if (payOk) {
            logg.info(orderVo.getOrderNo() + "支付成功,订单号为:" + orderNo + ",支付金额:" + paymentAmount);
            orderService.updateOutOrderNo(orderNo, WebConstant.ORDER_PRINT, orderVo.getOrderId());
            try {
                Purchase purch = new Purchase();
                purch.setTradeNo(tradeNo);
                purch.setPayWay("26");
                if ("0".equals(payType)) {
                    purch.setPayWay("24");
                }
                purch.setPayAmount(Double.valueOf(Double.valueOf(paymentAmount)));
                purch.setRemark("鹏鹏 低价:" + pp.getTicketPrice() + " " + orderNo);
                purch.setSupplierNo(orderNo);
                purch.setType("0");
                purch.setSupplier("17");
                purch.setOrderId(orderVo.getOrderId());
                purch.setOrderShop(orderVo.getOrderShop());
                purch.setOrderSource(orderVo.getOrderSource());
                purch.setOrderNo(orderVo.getOrderNo());
                purch.setcOrderNo(orderVo.getcOrderNo());
                purch.setcAddDate(orderVo.getcAddDate());
                purch.setFlightDate(orderVo.getFlightDate());
                purch.setFlag("0");
                purch.setCustomerAmount(new BigDecimal(orderVo.getTotalPrice()).doubleValue());
                purch.setEmployeeName(orderVo.getProcessBy());
                purch.setPrintTicketDate(new Date());
                purch.setProfit(new BigDecimal(purch.getCustomerAmount()).subtract(new BigDecimal(purch.getPayAmount())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                List<Passenger> passengetList = orderVo.getPassengetList();
                StringBuilder sb = new StringBuilder();
                for (Passenger pas : passengetList) {
                    sb.append(pas.getName()).append(",");
                }
                purch.setPassengerNames(sb.deleteCharAt(sb.length() - 1).toString());
                purchaseService.savePurch(purch);
            } catch (Exception e) {
                logg.info(orderVo.getOrderNo() + "采购单录入失败", e);
            }
            return  true;
        } else {
            LockOrderUtil.robotUnLock(orderVo.getOrderNo(), "支付失败");
            updateStatus(orderVo);
            return false;
        }

    }


    public static String payWithholdingNew(PayWithholdingNew payWithholdingNew){
        String msg = "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 0);
            String now = DateFormatUtils.format(calendar.getTime(),"yyyy-MM-dd HH:mm:ss");
            String operateTime = now;
            String service = "payWithholdingNew";
            String sign = DigestUtils.md5Hex(systemId+businessNo+userId+operateTime+key);
            payWithholdingNew.setUserId(userId);
            payWithholdingNew.setBusinessNo(businessNo);
            payWithholdingNew.setSystemId(systemId);
            payWithholdingNew.setOperateTime(operateTime);
            payWithholdingNew.setSign(sign);
            payWithholdingNew.setService(service);
            String str = null;
            str = beanToXml(payWithholdingNew, PayWithholdingNew.class);
            if(str!=null){
                msg = postFile(url,str);
            }else{
                logg.info("订单信息存入失败");
            }
        } catch (Exception e) {
            logg.info("鹏鹏支付未知异常",e);
        }

        return msg;
    }
    public static  void updateStatus(OrderVO orderVO) {
        try {
            if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderVO.getOrderSource())) {
                ttsOrderService.unLocked(orderVO.getOrderNo(), orderVO.getOrderNo().substring(0, 3), true);
            } else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderVO.getOrderSource())) {
               //jiuOrderService.unlocked(orderVO.getOrderNo(), true);
            } else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderVO.getOrderSource())) {
                //ctripOrderService.assignOrder(orderVO.getOrderNo(), orderVO.getOrderShop(), "0", true);
            } else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderVO.getOrderSource())) {
                //tcOrderService.unlocked(orderVO.getOrderNo(), true);
            }
        } catch (Exception e) {

        }

    }



}
