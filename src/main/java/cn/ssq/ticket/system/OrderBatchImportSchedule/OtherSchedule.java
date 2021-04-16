package cn.ssq.ticket.system.OrderBatchImportSchedule;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.runnable.UpdateChangeFee;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.service.OrderImport.impl.CtripOrderService;
import cn.ssq.ticket.system.util.*;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 其他一些同步任务
 */
@Component
public class OtherSchedule {

    private static Logger log = LoggerFactory.getLogger(OtherSchedule.class);


    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static List<String> portsList = new ArrayList<>();
    @Autowired
    private ChangeService changeService;
    @Autowired
    private CtripOrderService ctripOrderService;
    @Resource
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private RefundService refundService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private LogService logService;
    @Autowired
    private PassengerService passengerService;
    @Autowired
    private FlightService flightService;

    public static void main(String[] args) throws Exception {
        Date realFlightTime = SDF.parse("2020-12-23 18:14:00");
        Date thisFlightTime = SDF.parse("2020-12-23 18:10:00");
        long datePoor = getDatePoor(realFlightTime, thisFlightTime);
        System.out.println(datePoor);
    }

    public static long getDatePoor(Date endDate, Date nowDate) {
        long nm = 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少分钟
        long min = diff / nm;
        return min;
    }

    /**
     * 同步改期单客户支付金额
     */
    @Scheduled(cron = "1 1 2 * * ?")
    public void updateChangePrice() {
        log.info("开始 同步改期单客户支付金额");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        String start = date + " 00:00:00";
        String end = date + " 23:59:59";
        QueryWrapper<Change> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_date", start, end);
        List<Change> changes = changeService.selectByQuery(queryWrapper);
        Map<String, List<Change>> map = new HashMap<>();
        for (Change change : changes) {
            String changeNo = change.getNewCOrderNo();
            List<Change> changeS = map.get(changeNo);
            if (changeS == null || changeS.size() < 1) {
                changeS = new ArrayList<>();
                map.put(changeNo, changeS);
            }
            changeS.add(change);
        }

        Collection<List<Change>> values = map.values();
        for (List<Change> changeList : values) {
            try {
                UpdateChangeFee updateChangeFee = new UpdateChangeFee(changeList);
                updateChangeFee.run();
            } catch (Exception e) {
                log.info(changeList.get(0).getOrderNo() + "修改改签费用异常", e);
                continue;
            }

        }

    }

    /**
     * 同步携程改签单部分字段
     */
    @Scheduled(cron = "1 1 5 * * ?")
    public void updateCtripChangePrice() {
        try {
            ctripOrderService.updateChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "1", "All");
            Thread.sleep(5000);
            ctripOrderService.updateChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "2", "All");
            Thread.sleep(5000);
            ctripOrderService.updateChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "3", "All");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 人工处理的退票单，获取非常准航班状态
     */
    @Scheduled(cron = "1 1 3 * * ?")
    public void syncFlightStatus() {
        log.info("开始获取非常准航班状态");
        //清除前一次使用的飞常准数据缓存
        Set<Object> allKey = redisTemplate.keys("*");
        redisTemplate.delete(allKey);

        QueryWrapper<Refund> queryWrapper = new QueryWrapper<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        // String start = date + " 00:00:00";
        // String end = date + " 23:59:59";
        // queryWrapper.between("Flight_date", start, end);
        queryWrapper.eq("Flight_date", date);
        // queryWrapper.eq("AIR_REM_STATE",WebConstant.REFUND_APPLY);//申请中
        queryWrapper.eq("is_auto", "0");//人工处理
        queryWrapper.eq("Refund_type", WebConstant.REFUND_TYPE_ZIYUAN);
        queryWrapper.select("refund_id", "order_no", "Refund_type", "order_source", "flight_no", "dep_city_code", "arr_city_code",
                "Flight_date", "flight_time", "RET_NO", "ticket_no", "order_id", "policy_type", "process_by");
        List<Refund> refundList = refundService.getRefundsByQueryWapper(queryWrapper);
        System.out.println(refundList.size());
        for (Refund refund : refundList) {
            try {
                String retNo = refund.getRetNo();
                Long refundId = refund.getRefundId();
                String flightNo = refund.getFlightNo();
                String flightDate = refund.getFlightDate();
                String fTime = refund.getFlightTime();
                if (StringUtils.isEmpty(fTime)) {
                    QueryWrapper<Flight> flightQueryWrapper = new QueryWrapper<>();
                    flightQueryWrapper.eq("order_no", refund.getOrderNo());
                    flightQueryWrapper.select("dep_time");
                    List<Flight> byQueryW = flightService.getByQueryW(flightQueryWrapper);
                    if (byQueryW.size() > 0) {
                        fTime = byQueryW.get(0).getDepTime();
                    }
                }
                String flightTime = refund.getFlightDate() + " " + fTime;
                String dep = refund.getDepCityCode();
                String arr = refund.getArrCityCode();
                JSONArray flightData = null;
                String cacheKey = flightNo + "-" + dep + "-" + arr + "-" + flightDate;
                Object obj = redisTemplate.opsForValue().get(cacheKey);
                if (obj == null) {
                    flightData = FCZutil.getFlightDataByFlightNo(flightNo, flightDate, dep, arr);
                } else {
                    System.out.println("you you you  cache");
                    flightData = JSONArray.fromObject(obj);
                }
                if (flightData == null) {
                    Refund r = new Refund();
                    r.setRefundId(refundId);
                    r.setOrderRemark("NO");
                    refundService.updateById(r);
                    continue;
                }
                //非常准航班状态
                String flightState = "";
                //非常准实际起飞时间
                String realFlightDate = "";
                for (int i = 0; i < flightData.size(); i++) {
                    JSONObject flightJson = flightData.getJSONObject(i);
                    String flightNoJ = flightJson.getString("FlightNo");
                    if (flightNo.equals(flightNoJ)) {
                        String FlightDepcode = flightJson.getString("FlightDepcode");
                        String FlightArrcode = flightJson.getString("FlightArrcode");
                        if (dep.equals(FlightDepcode) && arr.equals(FlightArrcode)) {
                            flightState = flightJson.getString("FlightState");
                            realFlightDate = flightJson.getString("FlightDeptimeDate");
                        }
                    }
                }
                if (flightState.contains("取消")) {
                    flightState = "<span style='color: #f60;'>" + flightState + "</span>";
                }
                if (StringUtils.isNotEmpty(realFlightDate)) {
                    Date realFlightTime = SDF.parse(realFlightDate);
                    Date thisFlightTime = SDF.parse(flightTime);
                    long datePoor = getDatePoor(realFlightTime, thisFlightTime);
                    if (datePoor > 15) {
                        realFlightDate = "<span style='color: #f60;'>" + "实飞:" + realFlightDate + ",可提非自愿</span>";
                    }
                }
                Refund r = new Refund();
                r.setRefundId(refundId);
                r.setFlightStatus(flightState);
                if (realFlightDate.contains("实飞")) {
                    r.setOrderRemark(realFlightDate);
                } else {
                    r.setOrderRemark("实飞:" + realFlightDate);
                }
                refundService.updateById(r);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

        }

        log.info("结束获取非常准航班状态");
    }

    /**
     * 每天扫描起飞时间为前一天，申请类型为自愿的票号，票号航班时间与非常准数据对比，
     * 判断是否符合非自愿政策 是-->提交非自愿  否-->提交自愿
     */
    @Scheduled(cron = "1 1 4 * * ?")
    public void commitRefundTicket() {
        log.info("开始退票");

        String ports = DictUtils.getDictCodeNoCache("city_code", "code");
        if (StringUtils.isNotEmpty(ports)) {
            ports = ports.replace("，", ",");
            portsList = Arrays.asList(ports.split(","));
        }
        QueryWrapper<Refund> queryWrapper = new QueryWrapper<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        String start = date + " 00:00:00";
        String end = date + " 23:59:59";
        queryWrapper.between("Flight_date", start, end);
        queryWrapper.eq("refund_type", WebConstant.REFUND_TYPE_ZIYUAN);//自愿
        queryWrapper.eq("AIR_REM_STATE", WebConstant.REFUND_APPLY);//申请中
        queryWrapper.eq("is_auto", "1");//系统处理
        queryWrapper.select("refund_id", "order_no", "Refund_type", "order_source", "flight_no", "dep_city_code", "arr_city_code",
                "Flight_date", "flight_time", "RET_NO", "ticket_no", "order_id", "policy_type", "process_by");
        List<Refund> refundList = refundService.getRefundsByQueryWapper(queryWrapper);
        for (Refund refund : refundList) {

            String retNo = refund.getRetNo();
            OrderOperateLog logg = new OrderOperateLog();
            logg.setRetNo(retNo);
            logg.setType("退票处理");
            logg.setName("SYSTEM");
            Long refundId = refund.getRefundId();
            String orderNo = refund.getOrderNo();
            String flightNo = refund.getFlightNo();
            String flightDate = refund.getFlightDate();
            String flightTime = refund.getFlightDate() + " " + refund.getFlightTime();
            String dep = refund.getDepCityCode();
            String arr = refund.getArrCityCode();
            Long orderId = refund.getOrderId();
            Order order = orderService.getOutOrderNo(orderNo);
            try {
                String outOrderNo = order.getOutOrderNo();
                String policyType = order.getPolicyType();
                String ticketNo = refund.getTicketNo();
                if (!ticketNo.contains("-")) {
                    ticketNo = ticketNo.substring(0, 3) + "-" + ticketNo.substring(3);
                    refund.setTicketNo(ticketNo);
                }
                JSONArray flightData = null;
                String cacheKey = flightNo + "-" + dep + "-" + arr + "-" + flightDate;
                Object obj = redisTemplate.opsForValue().get(cacheKey);
                if (obj == null) {
                    flightData = FCZutil.getFlightDataByFlightNo(flightNo, flightDate, dep, arr);
                } else {
                    flightData = JSONArray.fromObject(obj);
                }
                if (flightData == null) {
                    logg.setContent("没有航班数据，由人工处理");
                    log.info(orderNo + logg.getContent());
                    Refund r = new Refund();
                    r.setIsAuto("0");
                    r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    r.setRefundId(refundId);
                    r.setOrderRemark(logg.getContent());
                    refundService.updateById(r);
                    logService.saveLog(logg);
                    continue;
                }
                //非常准航班状态
                String flightState = "";
                //非常准实际起飞时间
                String realFlightDate = "";
                for (int i = 0; i < flightData.size(); i++) {
                    JSONObject flightJson = flightData.getJSONObject(i);
                    String flightNoJ = flightJson.getString("FlightNo");
                    if (flightNo.equals(flightNoJ)) {
                        String FlightDepcode = flightJson.getString("FlightDepcode");
                        String FlightArrcode = flightJson.getString("FlightArrcode");
                        if (dep.equals(FlightDepcode) && arr.equals(FlightArrcode)) {
                            flightState = flightJson.getString("FlightState");
                            realFlightDate = flightJson.getString("FlightDeptimeDate");
                        }
                    }
                }

                if (portsList.contains(dep) || portsList.contains(arr)) {
                    logg.setContent("包含不处理航线，人工处理");
                    log.info(orderNo + logg.getContent());
                    Refund r = new Refund();
                    r.setIsAuto("0");
                    r.setRefundId(refundId);
                    r.setFlightStatus(flightState);
                    r.setOrderRemark(logg.getContent());
                    r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    updateById(r);
                    logService.saveLog(logg);
                    continue;
                }

                if (StringUtils.isEmpty(flightState)) {
                    logg.setContent("无航班状态,由人工处理");
                    log.info(orderNo + logg.getContent());
                    Refund r = new Refund();
                    r.setIsAuto("0");
                    r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    r.setRefundId(refundId);
                    r.setOrderRemark(logg.getContent());
                    refundService.updateById(r);
                    logService.saveLog(logg);
                    continue;
                }
                if (StringUtils.isEmpty(outOrderNo)) {
                    logg.setContent("平台订单号没有找到,由人工处理");
                    log.info(orderNo + logg.getContent());
                    Refund r = new Refund();
                    r.setFlightStatus(flightState);
                    r.setRefundId(refundId);
                    r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    r.setOrderRemark(logg.getContent());
                    updateById(r);
                    logService.saveLog(logg);
                    continue;
                }
                LambdaQueryWrapper<Passenger> wrapper = new LambdaQueryWrapper();
                wrapper.eq(Passenger::getOrderNo, orderNo)
                        .and(Wrapper -> Wrapper.eq(Passenger::getTicketNo, refund.getTicketNo()).or().eq(Passenger::getTicketNo, refund.getTicketNo().replace("-", "")))
                        .select(Passenger::getName);
                Passenger passenger = passengerService.findOneByQueryWapper(wrapper);
                if (passenger == null) {
                    logg.setContent("退票失败,没有找到乘客名");
                    log.info(orderNo + logg.getContent());
                    Refund r = new Refund();
                    r.setIsAuto("0");
                    r.setFlightStatus(flightState);
                    r.setRefundId(refundId);
                    r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    r.setOrderRemark(logg.getContent());
                    updateById(r);
                    logService.saveLog(logg);
                    continue;
                }
                if (flightState.contains("取消")) {
                    //提交非自愿
                    flightState = "<span style='color: #f60;'>" + flightState + "</span>";
                    if (policyType.contains("FCPP")) {
                        PPRefund ppRefund = new PPRefund();
                        ppRefund.setOrderNo(outOrderNo);
                        ppRefund.setTravelRange(dep + arr);
                        ppRefund.setTicketNo(refund.getTicketNo());
                        ppRefund.setPassenger(passenger.getName());
                        ppRefund.setRefundReason("10260201");//非自愿
                        String xmlResult = PPUtil.refundOrder(ppRefund);
                        if (xmlResult == null) {
                            logg.setContent("提交非自愿失败");
                            log.info(orderNo + logg.getContent());
                            Refund r = new Refund();
                            r.setIsAuto("0");
                            r.setFlightStatus(flightState);
                            r.setRefundId(refundId);
                            r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                            r.setOrderRemark(logg.getContent());
                            updateById(r);
                            logService.saveLog(logg);
                            continue;
                        }
                        Document document = DocumentHelper.parseText(xmlResult);
                        Element rootElement = document.getRootElement();
                        String status = rootElement.elementText("status");
                        if ("0".equals(status)) {
                            logg.setContent("提交非自愿成功!");
                            log.info(orderNo + logg.getContent());
                            String refundNo = rootElement.elementText("refundNo");
                            Refund r = new Refund();
                            r.setFlightStatus(flightState);
                            r.setRefundId(refundId);
                            r.setAirRemState(WebConstant.REFUND_CHECKING);
                            r.setRemJobNo(refundNo);
                            r.setOrderRemark("已提交非自愿，等待回款");
                            refundService.updateById(r);
                            logService.saveLog(logg);
                            continue;
                        } else {
                            String errorMessage = rootElement.elementText("errorMessage");
                            logg.setContent("提交非自愿失败," + errorMessage);
                            log.info(orderNo + logg.getContent());
                            Refund r = new Refund();
                            r.setIsAuto("0");
                            r.setFlightStatus(flightState);
                            r.setRefundId(refundId);
                            r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                            r.setOrderRemark(logg.getContent());
                            updateById(r);
                            logService.saveLog(logg);
                            continue;
                        }
                    }
                    logg.setContent("<span style='color: #f60;'>可提交非自愿</span>");
                    log.info(orderNo + logg.getContent());
                    Refund r = new Refund();
                    r.setIsAuto("0");
                    r.setRefundId(refundId);
                    r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    r.setFlightStatus(flightState);
                    r.setOrderRemark(logg.getContent());
                    refundService.updateById(r);
                    logService.saveLog(logg);
                    continue;
                }
                if (StringUtils.isEmpty(realFlightDate)) {
                    logg.setContent("没有查到起飞时间，由人工处理");
                    log.info(orderNo + logg.getContent());
                    Refund r = new Refund();
                    r.setIsAuto("0");
                    r.setRefundId(refundId);
                    r.setFlightStatus(flightState);
                    r.setOrderRemark(logg.getContent());
                    r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    updateById(r);
                    logService.saveLog(logg);
                    continue;
                }


                Date realFlightTime = SDF.parse(realFlightDate);
                Date thisFlightTime = SDF.parse(flightTime);
                long datePoor = getDatePoor(realFlightTime, thisFlightTime);
                if (datePoor > 15) {
                    //可以提交非志愿
                    logg.setContent("<span style='color: #f60;'>实飞:" + realFlightDate + "，可提非自愿</span>");
                    log.info(orderNo + logg.getContent());
                    Refund r = new Refund();
                    r.setIsAuto("0");
                    r.setRefundId(refundId);
                    r.setFlightStatus(flightState);
                    r.setOrderRemark(logg.getContent());
                    r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    updateById(r);
                    logService.saveLog(logg);
                    continue;
                } else {
                    //申请自愿
                    if (policyType.contains("FCPP") || policyType.contains("B2B")) {
                        if (policyType.contains("FCPPDJ")) {
                            PPRefund ppRefund = new PPRefund();
                            ppRefund.setOrderNo(outOrderNo);
                            ppRefund.setTravelRange(dep + arr);
                            ppRefund.setTicketNo(refund.getTicketNo());
                            ppRefund.setPassenger(passenger.getName());
                            ppRefund.setRefundReason("10260102");//自愿
                            String xmlResult = PPUtil.refundOrder(ppRefund);
                            if (xmlResult == null) {
                                logg.setContent("提交自愿退票失败");
                                log.info(orderNo + logg.getContent());
                                Refund r = new Refund();
                                r.setIsAuto("0");
                                r.setFlightStatus(flightState);
                                r.setRefundId(refundId);
                                r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                                r.setOrderRemark(logg.getContent());
                                updateById(r);
                                logService.saveLog(logg);
                                continue;
                            }
                            Document document = DocumentHelper.parseText(xmlResult);
                            Element rootElement = document.getRootElement();
                            String status = rootElement.elementText("status");
                            if ("0".equals(status)) {
                                logg.setContent("提交自愿退票成功!");
                                log.info(orderNo + logg.getContent());
                                String refundNo = rootElement.elementText("refundNo");
                                Refund r = new Refund();
                                r.setFlightStatus(flightState);
                                r.setRefundId(refundId);
                                r.setAirRemState(WebConstant.REFUND_CHECKING);
                                r.setRemJobNo(refundNo);
                                r.setOrderRemark("已提交自愿，等待回款");
                                refundService.updateById(r);
                                logService.saveLog(logg);
                                continue;
                            } else {
                                String errorMessage = rootElement.elementText("errorMessage");
                                logg.setContent("提交自愿退票失败," + errorMessage);
                                log.info(orderNo + logg.getContent());
                                Refund r = new Refund();
                                r.setIsAuto("0");
                                r.setFlightStatus(flightState);
                                r.setRefundId(refundId);
                                r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                                r.setOrderRemark(logg.getContent());
                                updateById(r);
                                logService.saveLog(logg);
                                continue;
                            }
                        } else if (policyType.contains("B2B")) {
                            B2BRefund b2BRefund = new B2BRefund();
                            b2BRefund.setTicketno(refund.getTicketNo());
                            b2BRefund.setOrderid(order.getOutOrderNo());
                            b2BRefund.setPnr(order.getBigPnr());
                            b2BRefund.setAirCode("AIR_ZH");
                            b2BRefund.setReason("自愿退票");
                            b2BRefund.setRemark(";自愿");
                            b2BRefund.setPolicyType(order.getPolicyType());
                            String jsonResult = B2BUtil.refundOrder(b2BRefund);
                            if (jsonResult == null) {
                                Thread.sleep(5000);
                                jsonResult = B2BUtil.refundOrder(b2BRefund);
                            }
                            if (jsonResult == null) {
                                logg.setContent("提交自愿退票失败");
                                log.info(orderNo + logg.getContent());
                                Refund r = new Refund();
                                r.setIsAuto("0");
                                r.setFlightStatus(flightState);
                                r.setRefundId(refundId);
                                r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                                r.setOrderRemark(logg.getContent());
                                updateById(r);
                                logService.saveLog(logg);
                                continue;
                            }
                            JSONObject jsonObject = JSONObject.fromObject(jsonResult);
                            if (!jsonObject.getString("executeStatus").contains("SUCCESS")) {
                                Thread.sleep(5 * 1000);
                                jsonResult = B2BUtil.refundOrder(b2BRefund);
                                jsonObject = JSONObject.fromObject(jsonResult);
                            }
                            if (jsonObject.getString("executeStatus").contains("SUCCESS")) {
                                logg.setContent("提交自愿退票成功!");
                                log.info(orderNo + logg.getContent());
                                Refund r = new Refund();
                                r.setFlightStatus(flightState);
                                r.setRefundId(refundId);
                                r.setAirRemState(WebConstant.REFUND_CHECKING);
                                r.setRemJobNo(b2BRefund.getRequestid());
                                r.setOrderRemark("已提交自愿，等待回款");
                                refundService.updateById(r);
                                logService.saveLog(logg);
                                continue;
                            } else {
                                logg.setContent("提交自愿退票失败");
                                log.info(orderNo + logg.getContent());
                                Refund r = new Refund();
                                r.setIsAuto("0");
                                r.setFlightStatus(flightState);
                                r.setRefundId(refundId);
                                r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                                r.setOrderRemark(logg.getContent());
                                updateById(r);
                                logService.saveLog(logg);
                                continue;
                            }

                        }
                    } else {
                        //人工处理
                        logg.setContent("实飞:" + realFlightDate + ",人工退票");
                        log.info(orderNo + logg.getContent());
                        Refund r = new Refund();
                        r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                        r.setFlightStatus(flightState);
                        r.setIsAuto("0");
                        r.setRefundId(refundId);
                        r.setOrderRemark(logg.getContent());
                        updateById(r);
                        logService.saveLog(logg);
                        continue;
                    }
                }
            } catch (Exception e) {
                logg.setContent("退票失败,没有找到乘客名");
                log.info(orderNo + logg.getContent());
                Refund r = new Refund();
                r.setIsAuto("0");
                r.setFlightStatus("");
                r.setRefundId(refundId);
                r.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                r.setOrderRemark(logg.getContent());
                updateById(r);
                logService.saveLog(logg);
                continue;

            }
        }

        log.info("结束退票");
    }

    public void updateById(Refund refund) {
        String processBy = refund.getProcessBy();
        if ("SYSTEM".equals(processBy)) {
            refund.setProcessBy("");
        }
        refundService.updateById(refund);
    }

    /**
     * 申请退票后，查询退票状态，完善部分字段
     */
    @Scheduled(cron = "1 1 7 * * ?")
    public void getRefundDatail() {
        try {
            QueryWrapper<Refund> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("AIR_REM_STATE", WebConstant.REFUND_CHECKING);//审核中
            queryWrapper.eq("is_auto", "1");//系统处理
            queryWrapper.select("refund_id", "order_no", "Refund_type", "order_source", "flight_no", "dep_city_code", "arr_city_code",
                    "Flight_date", "flight_time", "RET_NO", "ticket_no", "order_id", "policy_type", "REM_JOB_NO");
            List<Refund> refundList = refundService.getRefundsByQueryWapper(queryWrapper);
            for (Refund refund : refundList) {
                Long refundId = refund.getRefundId();
                String remJobNo = refund.getRemJobNo();
                OrderOperateLog logg = new OrderOperateLog();
                logg.setRetNo(refund.getRetNo());
                logg.setType("退票处理");
                logg.setName(ShiroKit.getLocalName());
                if (StringUtils.isEmpty(remJobNo)) {
                    log.info(refund.getOrderNo() + "的退票单号为空");
                    continue;
                }
                String policyType = refund.getPolicyType();
                if (policyType.contains("FCPPDJ")) {
                    PPRefundDetail ppRefundDetail = new PPRefundDetail();
                    ppRefundDetail.setRefundNo(remJobNo);
                    String refundDetail = PPUtil.refundDetail(ppRefundDetail);
                    if (StringUtils.isEmpty(refundDetail)) {
                        log.info(refund.getOrderNo() + "查询退票异常，下次在查");
                        continue;
                    }
                    Document document = DocumentHelper.parseText(refundDetail);
                    Element rootElement = document.getRootElement();
                    String status = rootElement.elementText("status");
                    if ("0".equals(status)) {
                        String refundStatus = rootElement.elementText("refundStatus");
                        if ("241".equals(refundStatus)) {
                            //退款完成
                            logg.setContent("退款完成");
                            String actualRefund = rootElement.elementText("actualRefund");
                            Refund r = new Refund();
                            r.setRefundId(refundId);
                            r.setAirRemState(WebConstant.REFUND_ALREADY_RETURN);
                            r.setAirRealPrice(new BigDecimal(actualRefund));
                            r.setOrderRemark(logg.getContent());
                            refundService.updateById(r);
                            logService.saveLog(logg);
                            log.info(refund.getOrderNo() + logg.getContent());
                        } else if ("220".equals(refundStatus) || "230".equals(refundStatus)) {
                            //被拒绝
                            Refund r = new Refund();
                            logg.setContent("退款被拒绝");
                            r.setRefundId(refundId);
                            r.setAirRemState(WebConstant.REFUND_CHECKING_FAIL);
                            r.setOrderRemark(logg.getContent());
                            refundService.updateById(r);
                            logService.saveLog(logg);
                            log.info(refund.getOrderNo() + logg.getContent());
                        } else if ("240".equals(refundStatus)) {
                            //退款中
                            log.info(refund.getOrderNo() + "查询退票退款中，下次在查");
                        } else if ("244".equals(refundStatus) || "211".equals(refundStatus)) {
                            //退款审核中
                            log.info(refund.getOrderNo() + "查询退款审核中，下次在查");
                        }

                    } else {
                        log.info(refund.getOrderNo() + "查询退票异常，下次在查" + refundDetail);
                        continue;
                    }

                } else if (policyType.contains("B2B")) {
                    B2BRefundDatail b2BRefundDatail = new B2BRefundDatail();
                    b2BRefundDatail.setAirCode("AIR_ZH");
                    b2BRefundDatail.setMfOrderNo(refund.getOrderNo());
                    b2BRefundDatail.setPolicyType(refund.getPolicyType());
                    b2BRefundDatail.setRequestid(remJobNo);
                    String jsonDatail = B2BUtil.refundOrderDatail(b2BRefundDatail);
                    if (StringUtils.isEmpty(jsonDatail)) {
                        jsonDatail = B2BUtil.refundOrderDatail(b2BRefundDatail);
                    }
                    if (StringUtils.isEmpty(jsonDatail)) {
                        log.info(refund.getOrderNo() + "查询退票异常，下次在查");
                        continue;
                    }
                    JSONObject jsonObject = JSONObject.fromObject(jsonDatail);
                    String executeStatus = jsonObject.getString("executeStatus");
                    log.info(refund.getOrderNo() + "b2b退单详情" + jsonDatail);
                    if ("REFUNDTICKET_SUCCESS".equals(executeStatus)) {//REFUNDTICKET_INIT
                        //退票成功
                        String airAuditStatus = jsonObject.getString("airAuditStatus");
                        if ("审核通过".equals(airAuditStatus)) {
                            logg.setContent("审核通过");
                            JSONArray guestList = jsonObject.getJSONArray("guestList");
                            JSONObject passenger = guestList.getJSONObject(0);
                            String refundAmount = passenger.getString("refundAmount");
                            Refund r = new Refund();
                            r.setRefundId(refundId);
                            r.setAirRemState(WebConstant.REFUND_ALREADY_RETURN);
                            r.setAirRealPrice(new BigDecimal(refundAmount));
                            r.setOrderRemark(logg.getContent());
                            refundService.updateById(r);
                            logService.saveLog(logg);
                            log.info(refund.getOrderNo() + logg.getContent());
                        } else if ("审核中".equals(airAuditStatus)) {
                            log.info(refund.getOrderNo() + "查询退票审核中，下次在查");
                        } else {
                            //未知
                            logg.setContent("查询退款状态未知,需要人工查询");
                            Refund r = new Refund();
                            r.setRefundId(refundId);
                            r.setAirRemState(WebConstant.REFUND_CHECKING_FAIL);
                            r.setOrderRemark(logg.getContent());
                            refundService.updateById(r);
                            logService.saveLog(logg);
                            log.info(refund.getOrderNo() + logg.getContent());
                        }

                    }

                } else {
                    log.info(refund.getOrderNo() + "未知政策类型" + policyType);
                    continue;
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
