package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.OrderBatchImportSchedule.CtripSchednule;
import cn.ssq.ticket.system.entity.AirLine;
import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.ExportBean.RefundTicketReport;
import cn.ssq.ticket.system.exception.OrderToMuchException;
import cn.ssq.ticket.system.queryEntity.RefundQuery;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.service.OrderImport.impl.*;
import cn.ssq.ticket.system.util.*;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/refund")
public class RefundMangerController {
    private Logger logg = LoggerFactory.getLogger(this.getClass());

    private String PREFIX = "/modular/system/refund/";

    @Autowired
    private RefundService refundService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ImportServcie importService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private TTsOrderService ttsOrderService;

    @Autowired
    private CtripOrderService ctripOrderService;

    @Autowired
    private TcOrderService tcOrderService;

    @Autowired
    private TbOrderService tbOrderService;

    @Autowired
    private JiuOrderService jiuOrderService;

    @Autowired
    private LogService logService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private  PassengerService passengerService;

    @Autowired
    private CtripSchednule ctripSchednule;

    @Autowired
    private PurchaseService purchService;

    //???????????????
    @RequestMapping("/toRefundIndex")
    public String orderIndex() {
        return PREFIX + "refund_index.html";
    }

    @RequestMapping("/toSearchOrder")
    public String toSearchOrder() {
        return PREFIX + "search_order.html";
    }

    @RequestMapping("/importIndex")
    public String index() {
        return PREFIX + "refund_import_index.html";
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getRefundList")
    public Object getRefundList(HttpServletRequest request) {
        ResponseResult<List<RefundVO>> result = new ResponseResult<List<RefundVO>>();
        try {
            RefundQuery query = this.createRefundQuery(request);
            List<RefundVO> list = refundService.getRefundList(query);
            Integer count = refundService.getRefundListCount(query);
            result.setCount(count);
            result.setData(list);
            //??????????????????????????????????????????
            if (result.getCount() < 1) {
                if (!StringUtils.isEmpty(query.getRetNo()) && !"0".equals(query.getOrderSource())) {
                    getRefundIsNotExit(query);
                    List<RefundVO> list2 = refundService.getRefundList(query);
                    Integer count2 = refundService.getRefundListCount(query);
                    result.setCount(count2);
                    result.setData(list2);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(-1);
            result.setMsg("????????????");
        }
        return result;
    }

    /**
     * ?????????????????????
     *
     * @param orderId
     * @return
     */
    @RequestMapping("/toRefundAdd")
    public String toRefundAdd(String orderId, Model model) {
        try {
            Order order = orderService.getOrderById(orderId);
            List<Flight> flightList = flightService.selectFlightInfoByOrderId(orderId);
            model.addAttribute("data", order);
            model.addAttribute("flightList", flightList);
            return PREFIX + "refund_add.html";
        } catch (Exception e) {
            return "/404.html";
        }
    }

    /**
     * ?????????????????????
     *
     * @param orderNo
     * @param model
     * @param requext
     * @return
     */
    @RequestMapping("/toRefundView")
    public String orderView(String orderNo, Model model, HttpServletRequest requext) {
        if (StringUtils.isEmpty(orderNo)) {
            return "/404.html";
        }
        try {
            model.addAttribute("refundId", requext.getParameter("refundId"));
            model.addAttribute("retNo", requext.getParameter("retNo"));
            OrderVO orderVo = orderService.selectOrderDetails(orderNo, null, null);
            List<Flight> flightList = flightService.selectFlightInfoByOrderId(orderVo.getOrderId().toString());
            orderVo.setFlightList(flightList);
            model.addAttribute("data", orderVo);
            String process = refundService.isHavePcocess(requext.getParameter("retNo"));
            if (!StringUtils.isEmpty(process)) {
                String username = ShiroKit.getSubject().getPrincipal().toString().trim();
                if (process.trim().equals(username)) {
                    //?????????(??????????????????)
                    List<Refund> refundPassenger = refundService.getRefundsByRetNo(requext.getParameter("retNo"));
                    model.addAttribute("refundPassenger", refundPassenger);
                    RefundInfo info = new RefundInfo();
                    StringBuilder sb = new StringBuilder();
                    BigDecimal price = new BigDecimal("0");
                    for (Refund r : refundPassenger) {
                        r.setXePnrStatus(StringUtils.isEmpty(r.getXePnrStatus())?"0":r.getXePnrStatus());
                        if (StringUtils.isEmpty(info.getcAppDate())) {
                            info.setcAppDate(r.getcAppDate());
                            info.setcRemDate(r.getcRemDate());
                            info.setAirAppDate(r.getAirAppDate());
                            info.setAirRemDate(r.getAirRemDate());
                            info.setIsRet(r.getIsRefund());
                            info.setXePnrStatus(StringUtils.isEmpty(r.getXePnrStatus())?"0":r.getXePnrStatus());
                        }
                        sb.append(r.getPassengerName()).append(",");
                        price = price.add(r.getcRealPrice() == null ? new BigDecimal(0) : new BigDecimal(r.getcRealPrice()));
                    }
                    info.setcRealPrice(price);
                    info.setPassengerName(sb.deleteCharAt(sb.length() - 1).toString());
                    model.addAttribute("refund", info);
                    return PREFIX + "refund_edit.html";
                }
            }
        } catch (Exception e) {
            //return "/404.html";
        }
        OrderOperateLog log = new OrderOperateLog();
        log.setRetNo(requext.getParameter("retNo"));
        log.setContent("????????????????????????");
        log.setType("????????????");
        log.setName(ShiroKit.getLocalName());
        logService.saveLog(log);
        return PREFIX + "refund_view.html";
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @RequestMapping("/toRefundEdit")
    public String toRefundEdit(String orderNo, HttpServletRequest request, Model model) {
        if (StringUtils.isEmpty(orderNo)) {
            return "/404.html";
        }
        OrderVO orderVo;
        try {
            orderVo = orderService.selectOrderDetails(orderNo, null, null);
            List<Flight> flightList = flightService.selectFlightInfoByOrderId(orderVo.getOrderId().toString());
            orderVo.setFlightList(flightList);
            model.addAttribute("data", orderVo);
            model.addAttribute("retNo", request.getParameter("retNo"));
            model.addAttribute("refundId", request.getParameter("refundId"));

            //?????????(??????????????????)
            List<Refund> refundPassenger = refundService.getRefundsByRetNo(request.getParameter("retNo"));
            model.addAttribute("refundPassenger", refundPassenger);

            RefundInfo info = new RefundInfo();
            StringBuilder sb = new StringBuilder();
            BigDecimal price = new BigDecimal("0");
            for (Refund r : refundPassenger) {
                r.setXePnrStatus(StringUtils.isEmpty(r.getXePnrStatus())?"0":r.getXePnrStatus());
                if (StringUtils.isEmpty(info.getcAppDate())) {
                    info.setcAppDate(r.getcAppDate());
                    info.setcRemDate(r.getcRemDate());
                    info.setAirAppDate(r.getAirAppDate());
                    info.setAirRemDate(r.getAirRemDate());
                    info.setIsRet(r.getIsRefund());
                }
                sb.append(r.getPassengerName()).append(",");
                price = price.add(r.getcRealPrice() == null ? new BigDecimal(0) : new BigDecimal(r.getcRealPrice()));
            }
            info.setcRealPrice(price);
            info.setPassengerName(sb.deleteCharAt(sb.length() - 1).toString());
            model.addAttribute("refund", info);
        } catch (OrderToMuchException e) {
            return "/404.html";
        }
        return PREFIX + "refund_edit.html";

    }


    /**
     * ?????????????????????
     *
     * @param
     * @param retNo
     * @param
     * @param requext
     * @return
     */
    @RequestMapping("/getRefundInfo")
    @ResponseBody
    public Object getRefundInfo(String refundId, String retNo, HttpServletRequest requext) {
        ResponseResult<Map<String, Object>> result = new ResponseResult<Map<String, Object>>();
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            List<Refund> refundPassenger = refundService.getRefundsByRetNo(retNo);
            RefundInfo info = new RefundInfo();
            StringBuilder sb = new StringBuilder();
            BigDecimal price = new BigDecimal("0");
            for (Refund r : refundPassenger) {
                r.setAirRefundType(DictUtils.getDictName("refund_type_air", r.getAirRefundType()));
                r.setAirRemState(DictUtils.getDictName("refund_status", r.getAirRemState()));
                if (StringUtils.isEmpty(info.getcAppDate())) {
                    info.setcAppDate(r.getcAppDate());
                    info.setcRemDate(r.getcRemDate());
                    info.setAirAppDate(r.getAirAppDate());
                    info.setAirRemDate(r.getAirRemDate());
                }
                sb.append(r.getPassengerName()).append(",");
                price = price.add(r.getcRealPrice() == null ? new BigDecimal(0) : new BigDecimal(r.getcRealPrice()));
            }
            info.setcRealPrice(price);
            info.setPassengerName(sb.deleteCharAt(sb.length() - 1).toString());
            map.put("refundPassenger", refundPassenger);
            map.put("refund", info);
            result.setData(map);
        } catch (Exception e) {
            result.setCode(-1);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ???????????????
     *
     * @param refunds
     * @return
     */
    @ResponseBody
    @PostMapping("/updateRefunds")
    public Object updateRefundStatus(@RequestBody List<Refund> refunds) {
        ResponseResult<Void> result = new ResponseResult<Void>();
        try {
            refundService.updateRefunds(refunds);
            result.setMsg("????????????");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("????????????");
        }
        return result;
    }

    /**
     * ????????????,???????????????????????????????????????????????????????????????
     *
     * @param retNo
     * @return
     */
    @ResponseBody
    @PostMapping("/confirmRefund")
    public Object confirmRefund(String retNo) {
        ResponseResult<Void> result = new ResponseResult<Void>();
        QueryWrapper<Refund> query = new QueryWrapper<Refund>();
        query.eq("RET_NO", retNo);
        List<Refund> refunds = refundService.getRefundsByQueryWapper(query);
        OrderOperateLog log = new OrderOperateLog();
        log.setRetNo(retNo);
        log.setType("????????????");
        String userName = ShiroKit.getLocalName();
        if (StringUtils.isEmpty(userName)) {
            userName = refundService.isHavePcocess(retNo);
        }
        try {
            log.setName(userName);
            Refund refund = refunds.get(0);
            String orderSource = refund.getOrderSource();
            String orderShop = refund.getOrderShop();
            if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)) {
                orderShop = "01".equals(orderShop) ? "rnb" : "rnf";
                JSONObject fromObject = ttsOrderService.confirmRefund(retNo, null,orderShop);
                if (fromObject.getBoolean("ret")) {
                    result.setMsg(StringUtils.isEmpty(fromObject.getString("errmsg")) ? "????????????" : fromObject.getString("errmsg"));
                } else {
                    result.setCode(1);
                    result.setMsg(StringUtils.isEmpty(fromObject.getString("errmsg")) ? "????????????" : fromObject.getString("errmsg"));
                }
            } else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)) {
                JSONObject fromObject = tcOrderService.confirmRefund(retNo, 1);
                if (fromObject.getBoolean("isSuccess")) {
                    result.setMsg(StringUtils.isEmpty(fromObject.getString("errorMessage")) ? "????????????" : fromObject.getString("errorMessage"));
                } else {
                    result.setCode(1);
                    result.setMsg(StringUtils.isEmpty(fromObject.getString("errorMessage")) ? "????????????" : fromObject.getString("errorMessage"));
                }
            } else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)) {
                JSONObject json = ctripOrderService.confirmRefund(retNo, orderShop);
                JSONObject fromObject = json.getJSONObject("Header");
                //{"Header":{"ShouldRecordPerformanceTime":false,"RequestID":"c80ab171-4efe-4205-8ae5-f85e985f9108","ResultCode":1,"ResultMsg":"????????????85091619????????????","AssemblyVersion":"2.9.8.0","RequestBodySize":0,"SerializeMode":0,"RouteStep":1,"Environment":"PRO"}}
                result.setMsg(fromObject.getString("ResultMsg"));
            } else if (InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)) {
                String confirmRefund = tbOrderService.confirmRefund(retNo);
                JSONObject json = JSONObject.fromObject(confirmRefund);
                if (confirmRefund.contains("error_response")) {
                    String string = json.getJSONObject("error_response").getString("sub_msg");
                    result.setCode(1);
                    result.setMsg(StringUtils.isEmpty(string) ? "????????????" : string);
                } else {
                    if (json.getJSONObject("alitrip_seller_refund_confirmreturn_response").getBoolean("result")) {
                        result.setMsg("????????????");
                    } else {
                        result.setCode(1);
                        result.setMsg("????????????");
                    }
                }
            } else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource)) {
                JSONObject fromObject = jiuOrderService.confirmRefund(refund);
                if (fromObject.getBoolean("ret")) {
                    result.setMsg(StringUtils.isEmpty(fromObject.getString("errorMessage")) ? "????????????" : fromObject.getString("errorMessage"));
                } else {
                    result.setCode(1);
                    result.setMsg(StringUtils.isEmpty(fromObject.getString("errorMessage")) ? "????????????" : fromObject.getString("errorMessage"));
                }
            }
        } catch (Exception e) {
            result.setCode(1);
            result.setMsg("????????????????????????????????????");
        }
        try {
            for (Refund r : refunds) {
                r.setIsRefund("1");
                r.setCreateBy(userName);
                refundService.updateById(r);
            }
        } catch (Exception e) {

        }
        log.setContent(result.getMsg());
        logService.saveLog(log);
        return result;
    }

    /**
     * ???????????????
     *
     * @param refunds
     * @return
     */
    @ResponseBody
    @PostMapping("/saveRefunds")
    public Object saveRefunds(@RequestBody List<Refund> refunds) {
        ResponseResult<Void> result = new ResponseResult<Void>();
        try {
            refundService.saveRefunds(refunds);
            result.setMsg("????????????");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("????????????");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping("/isHavePcocess")
    public synchronized Object isHavePcocess(String retNo, String refundId) {
        ResponseResult<Boolean> result = new ResponseResult<Boolean>();
        try {
            if (StringUtils.isEmpty(retNo)) {
                result.setData(false);
                result.setMsg("???????????????");
                return result;
            }
            String pcocess = refundService.isHavePcocessById(refundId);
            if (!StringUtils.isEmpty(pcocess)) {
                result.setMsg("???????????????" + pcocess + "??????!");
                result.setData(false);
                return result;
            }
        } catch (Exception e) {
            logg.error("????????????????????????" + retNo, e);
        }
        try {
            String username = ShiroKit.getSubject().getPrincipal().toString();
            refundService.addProcessById(username, refundId);//????????????
        } catch (Exception e) {
            result.setData(false);
            result.setMsg("Token?????????????????????????????????");
            return result;
        }
        result.setData(true);
        OrderOperateLog log = new OrderOperateLog();
        log.setRetNo(retNo);
        log.setContent("??????");
        log.setType("????????????");
        log.setName(ShiroKit.getLocalName());
        logService.saveLog(log);
        return result;
    }


    /**
     * ??????
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/deleteProcess")
    public Object deleteProcess(String retNo, String refundId) {
        ResponseResult<Void> result = new ResponseResult<Void>();
        try {
            String process = refundService.isHavePcocessById(refundId);
            if (!StringUtils.isEmpty(process)) {
                String username = ShiroKit.getSubject().getPrincipal().toString().trim();
                if (!process.trim().equals(username)) {
                    if (!ShiroKit.hasPermission("/unlock")) {
                        result.setCode(-1);
                        result.setMsg("???????????????!");
                        return result;
                    }
                }
            }
            refundService.deleteProcessById(refundId);
            result.setCode(0);
            OrderOperateLog log = new OrderOperateLog();
            log.setRetNo(retNo);
            log.setContent("??????");
            log.setType("????????????");
            log.setName(ShiroKit.getLocalName());
            logService.saveLog(log);

        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(-1);
            result.setMsg("token??????????????????????????????");
        }
        return result;
    }


    /**
     * ??????
     *
     * @param retNos
     * @return
     */
    @ResponseBody
    @PostMapping("/deleteRefund")
    public Object deleteRefund(String[] retNos) {
        ResponseResult<Void> result = new ResponseResult<Void>();
        try {
            refundService.deleteRefund(retNos);
            result.setMsg("????????????");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("????????????");
        }
        return result;
    }


    public void getRefundIsNotExit(RefundQuery query) {
        String orderSource = query.getOrderSource();
        String orderShop = query.getOrderShop();
        //String orderNo=query.getOrderNo();
        String retNo = query.getRetNo();
        try {
            boolean exist = orderService.isExist(orderSource, retNo);
            if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)) {
                if (exist) {
                    List<Refund> list = new TTsOrderNoticController().createTTSRefund(retNo, orderShop, null);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                } else {//????????????????????????????????????
                    OrderVO orderVo = ttsOrderService.getByOrderNo(retNo, InterfaceConstant.ORDER_SOURCE_QNR, orderShop);
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                    List<Refund> list = new TTsOrderNoticController().createTTSRefund(retNo, orderShop, orderVo);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                }
            } else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)) {
                ctripSchednule.syncRefundOrder();
            } else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)) {
                String user = DictUtils.getDictCode("order_import_cfgtc", "user");
                String pass = DictUtils.getDictCode("order_import_cfgtc", "pass");
                JSONObject refundDetail = tcOrderService.getRefundDetail(retNo, user, pass, true);
                if (refundDetail == null || !refundDetail.getBoolean("isSuccess")) {
                    return;
                }
                JSONObject data = refundDetail.getJSONObject("data");
                Order order = orderService.getOrderBycOrderNo(data.getString("orderSerialNo"));
                if (order == null) {
                    return;
                }
                JSONArray passengerArr = data.getJSONArray("refundTicketList");
                for (int j = 0; j < passengerArr.size(); j++) {
                    Refund refund = new Refund();
                    JSONObject passenger = passengerArr.getJSONObject(j);
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
                    refund.setcRemState("0");//?????????
                    refund.setAirRemState("0");
                    refund.setcAppDate(data.getString("createTime"));
                    refund.setPassengerName(passenger.getString("passName"));
                    refund.setTicketNo(passenger.getString("eticketNo"));
                    refund.setcRealPrice(passenger.getDouble("refundAmount"));
                    refund.setcRetPrice(passenger.getString("refundFee"));
                    refundService.autoCreateRefund(refund);
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getLogByRetNo")
    public Object getLogByOrderNo(String retNo, long page, int limit) {
        ResponseResult<List<OrderOperateLog>> result = new ResponseResult<List<OrderOperateLog>>();
        try {
            List<OrderOperateLog> list = logService.getLogByRetNo(retNo, page, limit);
            result.setCode(0);
            result.setData(list);
            Query query = new Query();
            query.addCriteria(Criteria.where("retNo").is(retNo));
            long count = mongoTemplate.count(query, OrderOperateLog.class);
            result.setCount((int) count);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(-1);
            result.setMsg("????????????");
        }
        return result;
    }

    public RefundQuery createRefundQuery(HttpServletRequest request) {
        RefundQuery query = new RefundQuery();
        //????????????????????????
        Calendar calendar = Calendar.getInstance();
        String defaultEndDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        calendar.add(Calendar.MONTH, -1);
        String defaultStartDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        String startDate = request.getParameter("cAppStartDate");
        String endDate = request.getParameter("cAppEndDate");
        query.setcAppStartDate(StringUtils.isEmpty(startDate) ? defaultStartDate + " 00:00:00" : startDate.trim() + " 00:00:00");
        query.setcAppEndDate(StringUtils.isEmpty(endDate) ? defaultEndDate + " 23:59:59" : endDate.trim() + " 23:59:59");
        String orderSource = request.getParameter("orderSource");
        if (!StringUtils.isEmpty(orderSource)) {
            if (!"0".equals(orderSource)) {
                query.setOrderSource(orderSource);
            }
            String orderShop = request.getParameter("orderShop");
            if (!StringUtils.isEmpty(orderShop)) {
                if (!"0".equals(orderShop)) {
                    query.setOrderShop(orderShop);
                }
            }
        }
        String theType = request.getParameter("theType");
        if(!StringUtils.isEmpty(theType)){
            if(!"0".equals(theType)){
                if ("NoPNR".equals(theType)){
                    //???????????????
                    //query.setXepnrStatus("1");
                    query.setAirRemState(WebConstant.REFUND_APPLY);
                    query.setIsAuto("0");
                }else if("NoPNRSYS".equals(theType)){
                    //????????????????????????
                    query.setAirRemState(WebConstant.REFUND_APPLY);
                    query.setIsAuto("1");
                    //query.setXepnrStatus("1");
                }else if("commitFail".equals(theType)){
                    //??????????????????
                    query.setAirRemState(WebConstant.REFUND_APPLY_FAIL);
                    //query.setIsAuto("1");
                }else if("isChecking".equals(theType)){
                    //???????????????????????????
                    query.setAirRemState(WebConstant.REFUND_CHECKING);
                    //query.setIsAuto("1");
                }else if (("checkFail").equals(theType)){
                    //????????????
                    query.setAirRemState(WebConstant.REFUND_CHECKING_FAIL);
                   // query.setIsAuto("1");
                }else if("complete".equals(theType)){
                    query.setAirRemState(WebConstant.REFUND_ALREADY_RETURN);
                }
            }
        }

        if (!StringUtils.isEmpty(request.getParameter("retNo"))) {
            query.setRetNo(request.getParameter("retNo").trim());
        }
        if (!StringUtils.isEmpty(request.getParameter("orderNo"))) {
            query.setOrderNo(request.getParameter("orderNo").trim());
        }
        if (!StringUtils.isEmpty(request.getParameter("flightNo"))) {
            query.setFlightNo(request.getParameter("flightNo").trim());
        }
        if (!StringUtils.isEmpty(request.getParameter("name"))) {
            query.setName(request.getParameter("name").trim());
        }

        if (!StringUtils.isEmpty(request.getParameter("cRemState"))) {
            query.setcRemState(request.getParameter("cRemState"));
        }
        if (!StringUtils.isEmpty(request.getParameter("ticketNo"))) {
            query.setTicketNo(request.getParameter("ticketNo").trim());
        }
        String isCheck = request.getParameter("isChecked");
        if (!StringUtils.isEmpty(isCheck)) {
            if ("true".equals(isCheck)) {
                String localName = ShiroKit.getLocalName();
                if (!StringUtils.isEmpty(localName)) {
                    query.setProcessBy(localName);
                }
            }
        }
        String flightStartDate = request.getParameter("flightStartDate");
        String flightEndDate = request.getParameter("flightEndDate");
        if (!StringUtils.isEmpty(flightStartDate)) {
            query.setFlightStartDate(flightStartDate+" 00:00:00");
            query.setFlightEndDate(StringUtils.isEmpty(flightEndDate) ? flightStartDate + " 23:59:59" : flightEndDate + " 23:59:59");
        }

        Integer page = StringUtils.isEmpty(request.getParameter("page")) ? 0 : Integer.parseInt(request.getParameter("page"));
        Integer limit = StringUtils.isEmpty(request.getParameter("limit")) ? 10 : Integer.parseInt(request.getParameter("limit"));
        query.setPage(limit);
        query.setJump((page - 1) * limit);
        return query;
    }


    /**
     * ?????????????????????
     *
     * @param file
     * @param
     * @return
     */
    @PostMapping("/uploadRefundReport")
    @ResponseBody
    public Object importExcel(@RequestParam("file") MultipartFile file) {
        ResponseResult<Void> result = new ResponseResult<>(0, "");
        Import im = new Import();//??????????????????
        Integer success = 0;//???????????????
        Integer fail = 0;//???????????????
        List<PPRefund> reportListA = new ArrayList<>();
        try {
            ImportExcel ei = new ImportExcel(file, 0, 0);
            reportListA = ei.getDataList(PPRefund.class, null);
            List<PPRefund> reportList = Lists.newArrayList();
            for (PPRefund refund : reportListA) {
                if (!StringUtils.isEmpty(refund.getMfOrderNo())) {
                    reportList.add(refund);
                }
            }
            int size = reportList.size();
            for (PPRefund refund : reportList) {
                String mfOrderNo = refund.getMfOrderNo();
                Order order = orderService.getOutOrderNo(mfOrderNo);
                String outOrderNo = refund.getOrderNo();
                if (order != null && !StringUtils.isEmpty(outOrderNo)) {
                    order.setOutOrderNo(outOrderNo);
                }
                if (order == null || StringUtils.isEmpty(order.getOutOrderNo())) {
                    refund.setRemark("?????????????????????????????????????????????");
                    fail++;
                    continue;
                }
                String ppOrderNo = order.getOutOrderNo();
                refund.setOrderNo(ppOrderNo);
                String travelRange = order.getDepCityCode() + order.getArrCityCode();
                refund.setTravelRange(travelRange);
                String ticketNo = refund.getTicketNo();
                if (StringUtils.isEmpty(ticketNo)) {
                    refund.setRemark("????????????,????????????");
                    fail++;
                    continue;
                }
                ticketNo = ticketNo.replace("???", ",");
                refund.setTicketNo(ticketNo);
                String[] ticketNoSplit = ticketNo.split(",");
                boolean noName=false;
                StringBuilder sb=new StringBuilder();
                for (String no : ticketNoSplit) {
                    LambdaQueryWrapper<Passenger> wrapper = new LambdaQueryWrapper();
                    wrapper.eq(Passenger::getOrderNo, mfOrderNo)
                            .and(Wrapper -> Wrapper.eq(Passenger::getTicketNo, no).or().eq(Passenger::getTicketNo, no.replace("-", "")))
                            .select(Passenger::getName);
                    Passenger passenger = passengerService.findOneByQueryWapper(wrapper);
                    if (passenger == null) {
                        noName=true;
                        break;
                    }
                    sb.append(passenger.getName()).append("|");
                }
                if (noName) {
                    refund.setRemark("??????????????????,????????????");
                    fail++;
                    continue;
                }
                String passenger = sb.deleteCharAt(sb.length()-1).toString();
                refund.setPassenger(passenger);
                refund.setRefundReason("10260102");//??????
                String xmlResult = PPUtil.refundOrder(refund);
                if (xmlResult == null) {
                    refund.setRemark("????????????");
                    fail++;
                    continue;
                }
                Document document = DocumentHelper.parseText(xmlResult);
                Element rootElement = document.getRootElement();
                String status = rootElement.elementText("status");
                if ("0".equals(status)) {
                    refund.setRemark("????????????");
                    String refundNo = rootElement.elementText("refundNo");
                    refund.setRefundNo(refundNo);
                    success++;
                } else {
                    String errorMessage = rootElement.elementText("errorMessage");
                    refund.setRemark(errorMessage + ",????????????");
                    fail++;
                    continue;
                }
            }
            im.setCount(size + "");
            im.setFail(fail + "");
            im.setSuccess(success + "");
            String name = ShiroKit.getLocalName();
            im.setName(name);
            im.setType("2");
            im.setImportDate(new Date());
            importService.insert(im);
            Long importId = im.getImportId();
            for (PPRefund ppRefund : reportList) {
                ppRefund.setImportId(importId);
            }
            this.importRecord(reportList, PPRefund.class);
        } catch (Exception e) {
            im.setName(ShiroKit.getLocalName());
            im.setType("2");
            im.setImportDate(new Date());
            im.setFail(fail + "");
            im.setSuccess(success + "");
            im.setRemark("???????????????????????????????????????");
            importService.insert(im);
            logg.error("??????????????????", e);
        }
        return result;
    }


    /**
     * B2B???????????????
     *
     * @param file
     * @param
     * @return
     */
    @PostMapping("/uploadRefundReportB2B")
    @ResponseBody
    public Object importExcelB2B(@RequestParam("file") MultipartFile file) {
        ResponseResult<Void> result = new ResponseResult<>(0, "");
        Import im = new Import();//??????????????????
        Integer success = 0;//???????????????
        Integer fail = 0;//???????????????
        List<B2BRefund> reportListA = new ArrayList<>();
        try {
            ImportExcel ei = new ImportExcel(file, 0, 0);
            reportListA = ei.getDataList(B2BRefund.class, null);
            List<B2BRefund> reportList = Lists.newArrayList();
            for (B2BRefund refund : reportListA) {
                if (!StringUtils.isEmpty(refund.getMfOrderNo())) {
                    reportList.add(refund);
                }
            }
            int size = reportList.size();
            for (B2BRefund refund : reportList) {
                String mfOrderNo = refund.getMfOrderNo();
                Order order = orderService.getOutOrderNo(mfOrderNo);
                String outOrderNo = refund.getOrderid();
                if (order != null && !StringUtils.isEmpty(outOrderNo)) {
                    order.setOutOrderNo(outOrderNo);
                }
                if (order == null || StringUtils.isEmpty(order.getOutOrderNo())) {
                    refund.setRemark("???????????????B2B????????????????????????");
                    fail++;
                    continue;
                }
                String ticketNo = refund.getTicketno();
                ticketNo = ticketNo.replace("???", ",").replace(",", "|");
                refund.setTicketno(ticketNo);
                refund.setOrderid(order.getOutOrderNo());
                refund.setPnr(order.getBigPnr());
                refund.setAirCode("AIR_ZH");
                refund.setReason("????????????");
                refund.setPolicyType(order.getPolicyType());
                //System.out.println(refund);
                String jsonResult = B2BUtil.refundOrder(refund);
                if (jsonResult == null) {
                    refund.setRemark("????????????");
                    fail++;
                    continue;
                }
                JSONObject jsonObject = JSONObject.fromObject(jsonResult);
                if (!jsonObject.getString("executeStatus").contains("SUCCESS")) {
                    Thread.sleep(5 * 1000);
                    System.out.println(mfOrderNo + "????????????????????????????????????");
                    jsonResult = B2BUtil.refundOrder(refund);
                    jsonObject = JSONObject.fromObject(jsonResult);
                }

                System.out.println(jsonObject);
                if (jsonObject.getString("executeStatus").contains("SUCCESS")) {
                    refund.setRemark("????????????");
                    success++;
                } else {
                    refund.setRemark(jsonObject.getString("executeMsg") + ",????????????");
                    fail++;
                    continue;
                }

            }
            im.setCount(size + "");
            im.setFail(fail + "");
            im.setSuccess(success + "");
            String name = ShiroKit.getLocalName();
            im.setName(name);
            im.setType("2");
            im.setImportDate(new Date());
            importService.insert(im);
            Long importId = im.getImportId();
            for (B2BRefund refund : reportList) {
                refund.setImportId(importId);
            }
            this.importRecord(reportList, B2BRefund.class);
        } catch (Exception e) {
            im.setName(ShiroKit.getLocalName());
            im.setType("2");
            im.setImportDate(new Date());
            im.setFail(fail + "");
            im.setSuccess(success + "");
            im.setRemark("???????????????????????????????????????");
            importService.insert(im);
            logg.error("??????????????????", e);
        }
        return result;

    }


    /**
     * ??????????????????????????????????????????
     *
     * @param list
     * @param
     */
    private void importRecord(List list, Class className) {
        try {
            mongoTemplate.insert(list, "importResult");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ???????????????????????????
     * @param importId
     * @param response
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/downloadRefundRecord")
    public void downloadImportRecord(@RequestParam(required = true) String importId, HttpServletResponse response) {
        try {
            String fileName = "importRecord.xlsx";
            Query query = new Query();
            query.addCriteria(Criteria.where("importId").is(Integer.valueOf(importId)));
            List<importResult> list = mongoTemplate.find(query, importResult.class);
            ExportExcel exportExcel = new ExportExcel("", importResult.class, 1);
            exportExcel.setDataList(list).write(response, fileName).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ?????????????????????????????????
     */
    @RequestMapping("/getDataForReportList")
    @ResponseBody
    public void exportRefund(HttpServletResponse response,HttpServletRequest request){
        RefundQuery refundQuery = this.createRefundQuery(request);
        List<RefundVO> refundList = refundService.getDataForReportList(refundQuery);
        List<AirLine> airList=mongoTemplate.findAll(AirLine.class);
        Map<String, AirLine> map=new HashMap<String, AirLine>();
        for(AirLine air:airList){
            map.put(air.getAirlineCode(), air);
        }
        List<RefundTicketReport> list=new ArrayList<RefundTicketReport>();
        String fileName = "????????????"+ org.apache.commons.lang.time.DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")+".xlsx";
        for(RefundVO refund:refundList){
            RefundTicketReport report=new RefundTicketReport();
            List<Purchase> purchaseList = purchService.getPurchaseList(refund.getOrderSource(), refund.getOrderShop(), refund.getOrderNo());
            if(!purchaseList.isEmpty()){
                if(purchaseList.size()>1){
                    for(Purchase purch:purchaseList) {
                        List<String> nameList = Arrays.asList(purch.getPassengerNames().split(","));
                        if(nameList.contains(refund.getPassengerName())) {
                            String purchPalseName=DictUtils.getDictName("order_purch_place", purch.getSupplier());
                            if(map.containsKey(refund.getAirlineCode())){
                                purch.setSupplier(map.get(refund.getAirlineCode()).getShortName()+purchPalseName);
                            }else{
                                purch.setSupplier(refund.getAirlineCode()+purchPalseName);
                            }
                            refund.setPurchase(purch);
                            break;
                        }
                    }
                }else{
                    Purchase purchase = purchaseList.get(0);
                    String purchPalseName=DictUtils.getDictName("order_purch_place", purchase.getSupplier());
                    if(map.containsKey(refund.getAirlineCode())){
                        purchase.setSupplier(map.get(refund.getAirlineCode()).getShortName()+purchPalseName);
                    }else{
                        purchase.setSupplier(refund.getAirlineCode()+purchPalseName);
                    }
                    refund.setPurchase(purchaseList.get(0));
                }
            }
            BeanUtils.copyProperties(refund, report);
            list.add(report);
        }
        ExportExcel exportExcel=new ExportExcel(null, RefundTicketReport.class);
        exportExcel.setDataList(list);
        try {
            exportExcel.write(response, fileName,request).dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
