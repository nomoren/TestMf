package cn.ssq.ticket.system.util;

import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.entity.woNiu.*;
import cn.ssq.ticket.system.service.OrderImport.impl.CtripOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.JiuOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TTsOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TcOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.service.PurchaseService;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 蜗牛采购工具类
 */
public class WoNiuUtil {

    private static  Logger logger = LoggerFactory.getLogger(WoNiuUtil.class);

    private static String searchFlightTag = "flight.national.supply.sl.searchflight";
    private static String bookingTag = "flight.national.supply.sl.bk";
    private static String searchPriceTag = "flight.national.supply.sl.searchprice";
    private static String orderTag = "flight.national.supply.sl.order";
    private static String payTag = "flight.national.supply.sl.pay";
    private static String payValidate = "flight.national.supply.sl.payValidate";
    private static String signTag = "flight.national.supply.sl.sign";
    private static String signQueryTag = "flight.national.supply.sl.SignQuery";
    private static String orderSearchTag = "flight.national.tts.order.info.detail.get";
    private static String token = "42d4573c3f019579a906d8f2c575bfe8";
    private static String key = "656cd1dea6068fe3fbb9ec61da247e50";
    public static String ppm="ssqly777@163.com";//147258//18820300483
    public static String regUrl="http://14.152.95.93:9111/pp/notic/payNotifyWoNiu";
    private static OrderService orderService = SpringContextHolder.getBean(OrderService.class);
    private static TTsOrderService ttsOrderService = SpringContextHolder.getBean(TTsOrderService.class);
    private static CtripOrderService ctripOrderService = SpringContextHolder.getBean(CtripOrderService.class);
    private static JiuOrderService jiuOrderService = SpringContextHolder.getBean(JiuOrderService.class);
    private static TcOrderService tcOrderService = SpringContextHolder.getBean(TcOrderService.class);
    private static PurchaseService purchaseService = SpringContextHolder.getBean(PurchaseService.class);
    public static Queue<String> queue=new LinkedList<String>();

    /**
     * 航变通知手机号
     */
    static {
        queue.offer("13265278717");
        queue.offer("17338146572");
        queue.offer("18145883071");
        queue.offer("18617245717");
    }


    public static boolean createAndPay(OrderVO orderVO){
        try {
            PriceSearch priceSearch =new PriceSearch();
            priceSearch.setDpt(orderVO.getDepCityCode());
            priceSearch.setArr(orderVO.getArrCityCode());
            priceSearch.setDate(orderVO.getFlightDate());
            priceSearch.setFlightNum(orderVO.getFlightNo());
            priceSearch.setEx_track("youxuan");
            priceSearch.setCabin(orderVO.getCabin());
            PriceInfo priceInfo = searchPrice(priceSearch);
            System.out.println(priceInfo);
            if(priceInfo==null){
                System.out.println("无报价");
                return false;
            }

            Booking booking=new Booking();
            booking.setTicketPrice(priceInfo.getVppr()+"");
            booking.setBarePrice(priceInfo.getBarePrice()+"");
            booking.setPrice(priceInfo.getPrice()+"");
            booking.setBasePrice(priceInfo.getBasePrice()+"");
            booking.setBusinessExt(priceInfo.getBusinessExt());
            booking.setTag(priceInfo.getPrtag());
            booking.setCarrier(priceInfo.getCarrier());
            booking.setFlightNum(priceInfo.getCode());
            booking.setCabin(priceInfo.getCabin());
            booking.setFrom(priceInfo.getDepCode());
            booking.setTo(priceInfo.getArrCode());
            booking.setPolicyType(priceInfo.getPolicyType());
            booking.setWrapperId(priceInfo.getWrapperId());
            booking.setStartTime(priceInfo.getDate());
            booking.setClient(priceInfo.getDomain());
            booking.setPolicyId(priceInfo.getPolicyId());
            booking.setDptTime(priceInfo.getBtime());
            BookInfo bookInfo = booking(booking);
            System.out.println(bookInfo);
            if(bookInfo==null){
                System.out.println("验价失败");
                return false;
            }

            OrderParam orderParam=new OrderParam();
            orderParam.setProductTag(bookInfo.getProductTag());
            orderParam.setBookingTag(bookInfo.getBookingTag());
            orderParam.setPrintPrice(bookInfo.getPrintPrice());
            orderParam.setQt(bookInfo.getQt());
            orderParam.setClientSite(bookInfo.getClientId());
            orderParam.setFlyFund(false);
            orderParam.setIsUseBonus(false);
            orderParam.setyPrice(bookInfo.getTicketPrice());
            orderParam.setContact("上上千");
            orderParam.setContactPreNum("86");
            orderParam.setContactMob("13265278717");
            orderParam.setInvoiceType(bookInfo.getInvoiceType());
            orderParam.setSjr("上上千");
            orderParam.setXcd("");
            orderParam.setXcdMethod("");
            orderParam.setBxInvoice("");
            JSONObject flightJson=new JSONObject();
            flightJson.put("flightNum",bookInfo.getFlightNum());
            flightJson.put("flightType",bookInfo.getFlightType());
            flightJson.put("stopInfo",bookInfo.getStops());
            flightJson.put("deptAirportCode",bookInfo.getDpt());
            flightJson.put("arriAirportCode",bookInfo.getArr());
            flightJson.put("deptCity",bookInfo.getDptCity());
            flightJson.put("arriCity",bookInfo.getArrCity());
            flightJson.put("deptDate",bookInfo.getDptDate());
            flightJson.put("deptTime",bookInfo.getDptTime());
            flightJson.put("arriTime",bookInfo.getArrTime());
            flightJson.put("cabin",bookInfo.getCabin());
            flightJson.put("childCabin",bookInfo.getChildCabin());
            orderParam.setFlightInfo(flightJson);

            List<JSONObject> passengerList=new ArrayList<>();
            List<Passenger> passengers = orderVO.getPassengetList();
            for (Passenger passenger : passengers) {
                JSONObject passengerObj=new JSONObject();
                passengerObj.put("name",passenger.getName());
                passengerObj.put("ageType",0);
                passengerObj.put("cardType","NI");
                String cardNo=passenger.getCertNo();
                passengerObj.put("cardNo",cardNo);
                int substring = Integer.valueOf(cardNo.substring(16, 17)).intValue();
                passengerObj.put("sex",substring%2 == 0?0:1);
                String bir=cardNo.substring(6,10)+"-"+cardNo.substring(10,12)+"-"+ cardNo.substring(12,14);
                passengerObj.put("birthday",bir);
                passengerObj.put("passengerPriceTag",bookInfo.getAduTag());
                passengerObj.put("bx",false);
                passengerObj.put("flightDelayBx",false);
                passengerObj.put("tuipiaoBx",false);
                passengerList.add(passengerObj);
            }
            orderParam.setPassengerCount(passengers.size());
            orderParam.setPassengers(passengerList);
            OrderInfo orderInfo = createOrder(orderParam);
            System.out.println(orderInfo);
            if(orderInfo==null){
                System.out.println("订单创建失败");
                return false;
            }

            PayParam payParam=new PayParam();
            payParam.setOrderNo(orderInfo.getOrderNo());
            payParam.setPmCode("DAIKOU");
            payParam.setBankCode("ALIPAY");
            payParam.setPaymentMerchantCode(ppm);
            payParam.setCurId("CNY");
            payParam.setBgRetUrl(regUrl);
           // pay(payParam);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }



    public static boolean createAndPay(OrderVO orderVO, int maxKui){
        try {
            PriceSearch priceSearch =new PriceSearch();
            priceSearch.setDpt(orderVO.getDepCityCode());
            priceSearch.setArr(orderVO.getArrCityCode());
            priceSearch.setDate(orderVO.getFlightDate());
            priceSearch.setFlightNum(orderVO.getFlightNo());
            priceSearch.setEx_track("youxuan");
            priceSearch.setCabin(orderVO.getCabin());
            PriceInfo priceInfo = searchPrice(priceSearch);
            if(priceInfo==null){
                LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "无官方特价");
                return false;
            }
            Double totalPrice = Double.valueOf(orderVO.getTotalPrice());
            int kui = Integer.valueOf(orderVO.getPassengerCount()).intValue() * maxKui;
            if (Double.valueOf(priceInfo.getSellPrice()).doubleValue() - totalPrice.doubleValue() > kui) {
                System.out.println(orderVO.getOrderNo() + "亏太多不出");
                LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "采购失败,超过亏损值,下单价格：" + priceInfo.getSellPrice());
                return false;
            }
            Booking booking=new Booking();
            booking.setTicketPrice(priceInfo.getVppr()+"");
            booking.setBarePrice(priceInfo.getBarePrice()+"");
            booking.setPrice(priceInfo.getPrice()+"");
            booking.setBasePrice(priceInfo.getBasePrice()+"");
            booking.setBusinessExt(priceInfo.getBusinessExt());
            booking.setTag(priceInfo.getPrtag());
            booking.setCarrier(priceInfo.getCarrier());
            booking.setFlightNum(priceInfo.getCode());
            booking.setCabin(priceInfo.getCabin());
            booking.setFrom(priceInfo.getDepCode());
            booking.setTo(priceInfo.getArrCode());
            booking.setPolicyType(priceInfo.getPolicyType());
            booking.setWrapperId(priceInfo.getWrapperId());
            booking.setStartTime(priceInfo.getDate());
            booking.setClient(priceInfo.getDomain());
            booking.setPolicyId(priceInfo.getPolicyId());
            booking.setDptTime(priceInfo.getBtime());
            BookInfo bookInfo = booking(booking);
            if(bookInfo==null){
                LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "验价失败");
                return false;
            }
            OrderParam orderParam=new OrderParam();
            orderParam.setProductTag(bookInfo.getProductTag());
            orderParam.setBookingTag(bookInfo.getBookingTag());
            orderParam.setPrintPrice(bookInfo.getPrintPrice());
            orderParam.setQt(bookInfo.getQt());
            orderParam.setClientSite(bookInfo.getClientId());
            orderParam.setFlyFund(false);
            orderParam.setIsUseBonus(false);
            orderParam.setyPrice(bookInfo.getTicketPrice());
            orderParam.setContact("上上千");
            orderParam.setContactPreNum("86");
            String lxrPhone = queue.poll();
            if(StringUtils.isEmpty(lxrPhone)) {
                lxrPhone = "13265278717";
            }
            queue.offer(lxrPhone);
            orderParam.setContactMob(lxrPhone);
            orderParam.setInvoiceType(bookInfo.getInvoiceType());
            orderParam.setSjr("上上千");
            orderParam.setXcd("");
            orderParam.setXcdMethod("");
            orderParam.setBxInvoice("");
            JSONObject flightJson=new JSONObject();
            flightJson.put("flightNum",bookInfo.getFlightNum());
            flightJson.put("flightType",bookInfo.getFlightType());
            flightJson.put("stopInfo",bookInfo.getStops());
            flightJson.put("deptAirportCode",bookInfo.getDpt());
            flightJson.put("arriAirportCode",bookInfo.getArr());
            flightJson.put("deptCity",bookInfo.getDptCity());
            flightJson.put("arriCity",bookInfo.getArrCity());
            flightJson.put("deptDate",bookInfo.getDptDate());
            flightJson.put("deptTime",bookInfo.getDptTime());
            flightJson.put("arriTime",bookInfo.getArrTime());
            flightJson.put("cabin",bookInfo.getCabin());
            flightJson.put("childCabin",bookInfo.getChildCabin());
            orderParam.setFlightInfo(flightJson);

            List<JSONObject> passengerList=new ArrayList<>();
            List<Passenger> passengers = orderVO.getPassengetList();
            for (Passenger passenger : passengers) {
                JSONObject passengerObj=new JSONObject();
                passengerObj.put("name",passenger.getName());
                passengerObj.put("ageType",0);
                passengerObj.put("cardType","NI");
                String cardNo=passenger.getCertNo();
                passengerObj.put("cardNo",cardNo);
                int substring = Integer.valueOf(cardNo.substring(16, 17)).intValue();
                passengerObj.put("sex",substring%2 == 0?0:1);
                String bir=cardNo.substring(6,10)+"-"+cardNo.substring(10,12)+"-"+ cardNo.substring(12,14);
                passengerObj.put("birthday",bir);
                passengerObj.put("passengerPriceTag",bookInfo.getAduTag());
                passengerObj.put("bx",false);
                passengerObj.put("flightDelayBx",false);
                passengerObj.put("tuipiaoBx",false);
                passengerList.add(passengerObj);
            }
            orderParam.setPassengerCount(passengers.size());
            orderParam.setPassengers(passengerList);
            OrderInfo orderInfo = createOrder(orderParam);
            if(orderInfo==null){
                LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "订单创建失败");
                return false;
            }

            PayValidate validate=new PayValidate();
            validate.setBankCode("ALIPAY");
            validate.setPmCode("OUTDAIKOU");
            validate.setOrderNo(orderInfo.getOrderNo());
            ValidateInfo validateInfo = WoNiuUtil.payValidate(validate);
            if(!validateInfo.isOk()){
                logger.info(orderVO.getOrderNo() + validateInfo.getErrMsg());
                LockOrderUtil.robotUnLock(orderVO.getOrderNo(), validateInfo.getErrMsg());
                return false;
            }

            //查询订单状态，锁单支付
            if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderVO.getOrderSource())) {
                String orderStatus = ttsOrderService.getOrderStatus(orderVO.getOrderNo());
                if ("TICKET_LOCK".equals(orderStatus)) {
                    logger.info(orderVO.getOrderNo() + "修改为出票中成功...");
                } else {
                    if (!"PAY_OK".equals(orderStatus)) {
                        LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "订单状态不可支付，");
                        return false;
                    }
                    net.sf.json.JSONObject updateOrderStatus = ttsOrderService.updateOrderStatus(orderVO.getOrderNo(), orderVO.getOrderNo().substring(0, 3), true);
                    if(updateOrderStatus==null){
                        Thread.sleep(10000);
                        updateOrderStatus = ttsOrderService.updateOrderStatus(orderVO.getOrderNo(), orderVO.getOrderNo().substring(0, 3), true);
                    }
                    if (updateOrderStatus != null) {
                        if (updateOrderStatus.getInt("ret") == 1) {
                            System.out.println(orderVO.getOrderNo() + "修改为出票中成功...");
                        } else {
                            System.out.println(orderVO.getOrderNo() + "修改为出票中失败");
                            LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "修改为出票中失败，");
                            return false;
                        }
                    } else {
                        System.out.println(orderVO.getOrderNo() + "修改为出票中失败");
                        LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "修改为出票中失败，");
                        return false;
                    }
                }

            } else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderVO.getOrderSource())) {
                String orderStatus = ctripOrderService.getOrderStatus(orderVO.getOrderNo(), orderVO.getOrderShop());
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(orderStatus)) {
                    String[] split = orderStatus.split(":");
                    if (!"1".equals(split[0]) || !"0".equals(split[1])) {
                        LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "订单状态不可支付，");
                        return false;
                    }
                } else {
                    LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "未获取到订单状态，");
                    return false;
                }
            } else if (InterfaceConstant.ORDER_SOURCE_TB.equals(orderVO.getOrderSource())) {

            } else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderVO.getOrderSource())) {
                String orderStatus = jiuOrderService.getOrderStatus(orderVO.getOrderNo());
                if (!"PAY_OK".equals(orderStatus)) {
                    logger.info(orderVO.getOrderNo() + "订单状态不可支付：" + orderStatus);
                    LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "订单状态不可支付，");
                    return false;
                }
            } else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderVO.getOrderSource())) {

            }


            PayParam payParam=new PayParam();
            payParam.setOrderNo(orderInfo.getOrderNo());
            payParam.setPmCode("OUTDAIKOU");
            payParam.setBankCode("ALIPAY");
            payParam.setPaymentMerchantCode(ppm);
            payParam.setCurId("CNY");
            payParam.setBgRetUrl(regUrl);
            PayInfo payInfo = pay(payParam);
            if(payInfo.isOk()){
               logger.info(orderVO.getOrderNo() + "支付成功,订单号为:" + orderInfo.getOrderNo() + ",支付金额:" + payInfo.getPayAmount());
               orderService.updateOutOrderNo(orderInfo.getOrderNo(), WebConstant.ORDER_PRINT, orderVO.getOrderId());
                try {
                    Purchase purch = new Purchase();
                    String payId = payInfo.getPayId();
                    if(StringUtils.isNotEmpty(payId)){
                        String[] split = payId.split(",");
                        String str = split[1];
                        payId= str.substring(0,str.length()-1);
                    }
                    purch.setTradeNo(payId);
                    purch.setPayWay("28");
                    purch.setPayAmount(Double.valueOf(Double.valueOf(payInfo.getPayAmount())));
                    purch.setRemark(orderInfo.getOrderNo()+"  "+lxrPhone);
                    purch.setSupplierNo(orderInfo.getOrderNo());
                    purch.setType("0");
                    purch.setSupplier("15");
                    purch.setOrderId(orderVO.getOrderId());
                    purch.setOrderShop(orderVO.getOrderShop());
                    purch.setOrderSource(orderVO.getOrderSource());
                    purch.setOrderNo(orderVO.getOrderNo());
                    purch.setcOrderNo(orderVO.getcOrderNo());
                    purch.setcAddDate(orderVO.getcAddDate());
                    purch.setFlightDate(orderVO.getFlightDate());
                    purch.setFlag("0");
                    purch.setCustomerAmount(new BigDecimal(orderVO.getTotalPrice()).doubleValue());
                    purch.setEmployeeName(orderVO.getProcessBy());
                    purch.setPrintTicketDate(new Date());
                    purch.setProfit(new BigDecimal(purch.getCustomerAmount()).subtract(new BigDecimal(purch.getPayAmount())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    List<Passenger> passengetList = orderVO.getPassengetList();
                    StringBuilder sb = new StringBuilder();
                    for (Passenger pas : passengetList) {
                        sb.append(pas.getName()).append(",");
                    }
                    purch.setPassengerNames(sb.deleteCharAt(sb.length() - 1).toString());
                    purchaseService.savePurch(purch);
                } catch (Exception e) {
                    logger.info(orderVO.getOrderNo() + "采购单录入失败", e);
                }
                return  true;

            }else{
                LockOrderUtil.robotUnLock(orderVO.getOrderNo(), "支付失败");
                PPUtil.updateStatus(orderVO);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }




    public static PriceInfo searchPrice(PriceSearch priceSearch){
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("token", token);
            map.put("key", key);
            map.put("createTime", System.currentTimeMillis() + "");
            map.put("params", JSONObject.toJSONString(priceSearch));
            map.put("tag", searchPriceTag);
            List<NameValuePair> parameterList = parameterListProcess(map);
            String rltStr = doRouter(parameterList);
            System.out.println(rltStr);
            int priceAdj = 50;
            JSONObject obj = JSON.parseObject(rltStr);
            String cabin = priceSearch.getCabin();
            if ("SUCCESS".equals(obj.getString("message"))) {
                JSONObject rltObj = JSON.parseObject(obj.get("result").toString());
                String startTime = rltObj.get("date").toString();
                String btime = rltObj.get("btime").toString();
                String carrier = rltObj.get("carrier").toString();
                JSONArray vendors = JSON.parseArray(rltObj.get("vendors").toString());
                for (int i = 0; i < vendors.size(); i++) {
                    JSONObject jsonObject = vendors.getJSONObject(i);
                    String tagName = jsonObject.getString("tagName");
                    String cabinName = jsonObject.getString("cabin");
                    if(tagName.contains("会员特惠") && cabin.equals(cabinName)){
                        PriceInfo priceInfo = ModelUtil.getPriceObj(rltObj, vendors.get(i).toString(), startTime, btime, carrier);
                        //作出价格调整
                        priceInfo.setSellPrice(priceInfo.getPrice() + priceAdj);
                        return  priceInfo;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(priceSearch+"报价出错",e);
        }
        return  null;
    }

    public static BookInfo booking(Booking booking){
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("token", token);
            map.put("key", key);
            map.put("createTime", System.currentTimeMillis() + "");
            map.put("params", JSONObject.toJSONString(booking));
            map.put("tag", bookingTag);
            List<NameValuePair> parameterList = parameterListProcess(map);
            String rltStr = doRouter(parameterList);
            System.out.println(rltStr);
            JSONObject obj = JSON.parseObject(rltStr);
            if ("SUCCESS".equals(obj.getString("message"))) {
                JSONObject result = obj.getJSONObject("result");
                if("BOOKING_SUCCESS".equals(result.getString("bookingStatus"))){
                    JSONObject priceInfo = result.getJSONObject("priceInfo");
                    JSONObject aduObject = priceInfo.getJSONObject("priceTag").getJSONArray("ADU").getJSONObject(0);
                    JSONObject expressInfo = result.getJSONObject("expressInfo");
                    JSONObject invoiceType = expressInfo.getJSONObject("invoiceType");
                    JSONObject extInfo = result.getJSONObject("extInfo");
                    JSONObject flightInfo = result.getJSONArray("flightInfo").getJSONObject(0);
                    BookInfo bookInfo=new BookInfo();
                    bookInfo.setBookingTag(result.getString("bookingTag"));
                    bookInfo.setPrintPrice(aduObject.getIntValue("viewPrice"));
                    bookInfo.setTicketPrice(priceInfo.getIntValue("ticketPrice"));
                    bookInfo.setInvoiceType(invoiceType.containsKey("2")?2:1);
                    bookInfo.setQt(extInfo.getString("qt"));
                    bookInfo.setProductTag(extInfo.getString("tag"));
                    bookInfo.setClientId(extInfo.getString("clientId"));
                    bookInfo.setFlightType(extInfo.getIntValue("flightType"));
                    bookInfo.setStops(flightInfo.getIntValue("stops"));
                    bookInfo.setFlightNum(flightInfo.getString("flightNum"));
                    bookInfo.setCabin(flightInfo.getString("cabin"));
                    bookInfo.setArr(flightInfo.getString("arr"));
                    bookInfo.setDpt(flightInfo.getString("dpt"));
                    bookInfo.setArrCity(flightInfo.getString("arrCity"));
                    bookInfo.setDptCity(flightInfo.getString("dptCity"));
                    bookInfo.setDptDate(flightInfo.getString("dptDate"));
                    bookInfo.setDptTime(flightInfo.getString("dptTime"));
                    bookInfo.setArrTime(flightInfo.getString("arrTime"));
                    bookInfo.setChildCabin(StringUtils.isEmpty(flightInfo.getString("childCabin"))?"Y":flightInfo.getString("childCabin"));
                    bookInfo.setAduTag(aduObject.getString("tag"));
                    return  bookInfo;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(booking+"booking出错",e);
        }
        return null;

    }


    public static OrderInfo createOrder(OrderParam orderParam){
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("token", token);
            map.put("key", key);
            map.put("createTime", System.currentTimeMillis() + "");
            map.put("params", JSONObject.toJSONString(orderParam));
            map.put("tag", orderTag);
            List<NameValuePair> parameterList = parameterListProcess(map);
            String rltStr = doRouter(parameterList);
           // System.out.println(rltStr);
            JSONObject obj = JSON.parseObject(rltStr);
            if ("SUCCESS".equals(obj.getString("message"))) {
                JSONObject result = obj.getJSONObject("result");
                OrderInfo orderInfo=new OrderInfo();
                orderInfo.setOrderNo(result.getString("orderNo"));
                orderInfo.setNoPayAmount(result.getString("noPayAmount"));
                orderInfo.setStatus(result.getString("status"));
                return  orderInfo;
            }else{
                logger.info(orderParam+"创建订单失败:"+rltStr);
            }

        } catch (Exception e) {
            logger.error(orderParam+"创建订单出错",e);
        }
        return null;


    }


    public static ValidateInfo payValidate(PayValidate validate){
        ValidateInfo validateInfo = new ValidateInfo();
        validateInfo.setOk(false);
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("token", token);
            map.put("key", key);
            map.put("createTime", System.currentTimeMillis() + "");
            map.put("params", JSONObject.toJSONString(validate));
            map.put("tag", payValidate);
            List<NameValuePair> parameterList = parameterListProcess(map);
            String rltStr = doRouter(parameterList);
            System.out.println(rltStr);
            JSONObject obj = JSON.parseObject(rltStr);
            if ("SUCCESS".equals(obj.getString("message"))) {
                validateInfo.setOk(true);
            }else {
                validateInfo.setErrMsg(obj.getString("message"));
            }

        } catch (Exception e) {
            logger.error(payValidate+"支付异常",e);
        }
        return validateInfo;
    }


    public static boolean sign(Sign sign){
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("token", token);
            map.put("key", key);
            map.put("createTime", System.currentTimeMillis() + "");
            map.put("params", JSONObject.toJSONString(sign));
            map.put("tag", signTag);
            List<NameValuePair> parameterList = parameterListProcess(map);
            String rltStr = doRouter(parameterList);
            System.out.println("qianyue:"+rltStr);
            JSONObject obj = JSON.parseObject(rltStr);
            if ("SUCCESS".equals(obj.getString("message"))) {
                return true;

            }else {

            }


        } catch (Exception e) {
            logger.error(payValidate+"支付异常",e);
        }
        return false;
    }

    public static boolean signq(SingQuery sign){
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("token", token);
            map.put("key", key);
            map.put("createTime", System.currentTimeMillis() + "");
            map.put("params", JSONObject.toJSONString(sign));
            map.put("tag", signQueryTag);
            List<NameValuePair> parameterList = parameterListProcess(map);
            String rltStr = doRouter(parameterList);
            System.out.println("qianyue:"+rltStr);
            JSONObject obj = JSON.parseObject(rltStr);
            if ("SUCCESS".equals(obj.get("message"))) {
                return true;

            }else {

            }


        } catch (Exception e) {
            logger.error(payValidate+"支付异常",e);
        }
        return false;
    }


    public static PayInfo pay(PayParam payParam){
        PayInfo payInfo = new PayInfo();
        payInfo.setOk(false);
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("token", token);
            map.put("key", key);
            map.put("createTime", System.currentTimeMillis() + "");
            map.put("params", JSONObject.toJSONString(payParam));
            map.put("tag", payTag);
            List<NameValuePair> parameterList = parameterListProcess(map);
            System.out.println("支付"+parameterList);
            String rltStr = doRouter(parameterList);
            System.out.println(rltStr);
            JSONObject obj = JSON.parseObject(rltStr);
            if ("SUCCESS".equals(obj.getString("message"))) {
                JSONObject result = obj.getJSONObject("result");
                if(0==result.getIntValue("code")){
                    payInfo.setOk(true);
                    JSONObject orderResult = result.getJSONArray("results").getJSONObject(0);
                    payInfo.setOrderNo(orderResult.getString("orderNo"));
                    payInfo.setPayAmount(orderResult.getString("payAmount"));
                    payInfo.setPayId(orderResult.getString("payId"));
                }
            }else {
                payInfo.setErrMsg(obj.getString("message"));
            }

        } catch (Exception e) {
            logger.error(payParam+"支付异常",e);
        }
        return payInfo;
    }

    public static String getOrderDetail(String orderNo){
        try {
            Map<String, String> map = new TreeMap<>();
            map.put("token", token);
            map.put("key", key);
            map.put("createTime", System.currentTimeMillis() + "");
            map.put("params", JSONObject.toJSONString(new OrderSearch(orderNo)));
            map.put("tag", orderSearchTag);
            List<NameValuePair> parameterList = parameterListProcess(map);
            String rltStr = doRouter(parameterList);
            System.out.println(rltStr);
            JSONObject obj = JSON.parseObject(rltStr);
            if ("SUCCESS".equals(obj.getString("message"))) {

            }else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }




    /*
     *请求递交参数预处理，返回格式化parameterList数据
     */
    public static List<NameValuePair> parameterListProcess(Map<String, String> map) {
        List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
        String param = "";
        try {
            for (String key : map.keySet()) {
                param += key + "=" + map.get(key);
                parameterList.add(new BasicNameValuePair(key, map.get(key)));
            }
            String sign = ToolsUtil.MD5(param);
            parameterList.add(new BasicNameValuePair("sign", sign));
            return parameterList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String doRouter(List<NameValuePair> parameterList) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://qae.qunar.com/api/router");
        CloseableHttpResponse response=null;
        try {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(parameterList, HTTP.UTF_8));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream instreams = entity.getContent();
            String content = ToolsUtil.InputStreamToString(instreams);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }finally {
            if (response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
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





    public static void main(String[] args) {

        System.out.println(getOrderDetail("tik210323102146195"));


//        PriceSearch priceSearch =new PriceSearch();
//        priceSearch.setDpt("HRB");
//        priceSearch.setArr("NTG");
//        priceSearch.setDate("2021-04-01");
//        priceSearch.setFlightNum("ZH9626");
//        priceSearch.setEx_track("youxuan");
//        priceSearch.setCabin("K");
//        PriceInfo priceInfo = searchPrice(priceSearch);
//        System.out.println(priceInfo);
//
//
//        Booking booking=new Booking();
//        booking.setTicketPrice(priceInfo.getVppr()+"");
//        booking.setBarePrice(priceInfo.getBarePrice()+"");
//        booking.setPrice(priceInfo.getPrice()+"");
//        booking.setBasePrice(priceInfo.getBasePrice()+"");
//        booking.setBusinessExt(priceInfo.getBusinessExt());
//        booking.setTag(priceInfo.getPrtag());
//        booking.setCarrier(priceInfo.getCarrier());
//        booking.setFlightNum(priceInfo.getCode());
//        booking.setCabin(priceInfo.getCabin());
//        booking.setFrom(priceInfo.getDepCode());
//        booking.setTo(priceInfo.getArrCode());
//        booking.setPolicyType(priceInfo.getPolicyType());
//        booking.setWrapperId(priceInfo.getWrapperId());
//        booking.setStartTime(priceInfo.getDate());
//        booking.setClient(priceInfo.getDomain());
//        booking.setPolicyId(priceInfo.getPolicyId());
//        booking.setDptTime(priceInfo.getBtime());
//
//        BookInfo bookInfo = booking(booking);
//        System.out.println(bookInfo);
//
//
//        OrderParam orderParam=new OrderParam();
//        orderParam.setProductTag(bookInfo.getProductTag());
//        orderParam.setBookingTag(bookInfo.getBookingTag());
//        orderParam.setPrintPrice(bookInfo.getPrintPrice());
//        orderParam.setQt(bookInfo.getQt());
//        orderParam.setClientSite(bookInfo.getClientId());
//        orderParam.setFlyFund(false);
//        orderParam.setIsUseBonus(false);
//        orderParam.setyPrice(bookInfo.getTicketPrice());
//        orderParam.setContact("上上千");
//        orderParam.setContactPreNum("86");
//        orderParam.setContactMob("13265278717");
//        orderParam.setInvoiceType(bookInfo.getInvoiceType());
//        orderParam.setSjr("上上千");
//        orderParam.setXcd("");
//        orderParam.setXcdMethod("");
//        orderParam.setBxInvoice("");
//        JSONObject flightJson=new JSONObject();
//        flightJson.put("flightNum",bookInfo.getFlightNum());
//        flightJson.put("flightType",bookInfo.getFlightType());
//        flightJson.put("stopInfo",bookInfo.getStops());
//        flightJson.put("deptAirportCode",bookInfo.getDpt());
//        flightJson.put("arriAirportCode",bookInfo.getArr());
//        flightJson.put("deptCity",bookInfo.getDptCity());
//        flightJson.put("arriCity",bookInfo.getArrCity());
//        flightJson.put("deptDate",bookInfo.getDptDate());
//        flightJson.put("deptTime",bookInfo.getDptTime());
//        flightJson.put("arriTime",bookInfo.getArrTime());
//        flightJson.put("cabin",bookInfo.getCabin());
//        flightJson.put("childCabin",bookInfo.getChildCabin());
//        orderParam.setFlightInfo(flightJson);
//
//        List<JSONObject> passengerList=new ArrayList<>();
//        for (int i=0; i<1; i++){
//            JSONObject passengerObj=new JSONObject();
//            passengerObj.put("name","吴志永");
//            passengerObj.put("ageType",0);
//            passengerObj.put("cardType","NI");
//            String cardNo="440882199807113032";
//            passengerObj.put("cardNo",cardNo);
//            int substring = Integer.valueOf(cardNo.substring(16, 17)).intValue();
//            passengerObj.put("sex",substring%2 == 0?0:1);
//            String bir=cardNo.substring(6,10)+"-"+cardNo.substring(10,12)+"-"+ cardNo.substring(12,14);
//            passengerObj.put("birthday",bir);
//            passengerObj.put("passengerPriceTag",bookInfo.getAduTag());
//            passengerObj.put("bx",false);
//            passengerObj.put("flightDelayBx",false);
//            passengerObj.put("tuipiaoBx",false);
//            passengerList.add(passengerObj);
//        }
//        orderParam.setPassengerCount(1);
//        orderParam.setPassengers(passengerList);
//
//        OrderInfo orderInfo = createOrder(orderParam);
//        System.out.println(orderInfo);



    }


}
