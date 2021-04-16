package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.OrderBatchImportSchedule.JiuSchednule;
import cn.ssq.ticket.system.OrderBatchImportSchedule.TTsSchednule;
import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.importBean.TTsBean.DataParam;
import cn.ssq.ticket.system.entity.importBean.TTsBean.RnfRequestParam;
import cn.ssq.ticket.system.entity.importBean.TTsBean.RnfResponse;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.service.OrderImport.impl.CtripOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.JiuOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TTsOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TuniuOrderService;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.ssq.ticket.system.util.WebConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 平台订单状态推送通知Controller
 *
 * @author Administrator
 */
@Controller
@RequestMapping("notic")
public class TTsOrderNoticController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderService orderService;

    @Autowired
    private DictService dictService;

    @Autowired
    private LogService logService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private ChangeService changeService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private TTsOrderService ttsService;

    @Autowired
    private JiuOrderService jiuOrderService;

    @Autowired
    private CtripOrderService ctripOrderService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private JiuSchednule js;

    @Autowired
    private TTsSchednule ttsS;



    /**
     * tts rnf订单状态变动消息通知接口
     *
     * @param
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "orderNotic/rnf", produces = "application/json;charset=UTF-8")
    public Object ttsRnfOrderStatusNotice(HttpServletRequest request, @RequestBody JSONObject jsonParam) {
        RnfResponse rnfResponse = new RnfResponse();
        if (jsonParam == null) {
            rnfResponse.setResult("fail");
            rnfResponse.setErrMsg("推送消息为空");
            return rnfResponse;
        }
        if (jsonParam.getJSONObject("data").getString("orderNo").contains("tes")) {
            //tts测试数据，忽略
            return rnfResponse;
        }
        Gson gson = new Gson();
        RnfRequestParam param = gson.fromJson(jsonParam.toString(), RnfRequestParam.class);
        DataParam data = param.getData();
        rnfResponse.setTransactionId(data.getTransactionId());
        String changeCode = data.getChangeCode();
        String orderNo = data.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            return rnfResponse;
        }
        orderNo = orderNo.trim();
        boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo);
        try {
            if ("0101".equals(changeCode)) {//订单状态从“订座成功等待支付”变成“支付成功等待出票”
                if (!exist) {//本地没有没有则添加这个订单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    for (int i = 0; i <= 5; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            }else if ("0104".equals(changeCode)) {//订单状态从“支付成功”变成“支付成功等待出票”
                if (!exist) {//本地没有没有则添加这个订单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    for (int i = 0; i <= 5; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            } else if ("0804".equals(changeCode)) {//订单状态由“支付成功等待出票”变成“出票中”
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_PRINT, orderNo);
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT);
                    orderService.saveOrderVO(orderVo);
                }
            } else if ("0201".equals(changeCode)) {//订单状态由“支付成功等待出票”变成“未出票申请退款”
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
                } else {//先导入订单，在创建退票单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                }
            } else if ("0203".equals(changeCode)) {//订单状态由“支付成功等待出票”变成“出票完成”
                Order order = orderService.getOrderByOrderNo(orderNo);
                if (order != null) {
                    if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
                        OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                        orderService.updateTicketNo(orderNo, orderVo.getPassengetList());
                    }
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                }
            } else if ("0204".equals(changeCode)) {//订单状态由“出票中”变为“出票完成
                Order order = orderService.getOrderByOrderNo(orderNo);
                if (order != null) {
                    if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
                        OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                        orderService.updateTicketNo(orderNo, orderVo.getPassengetList());
                    }
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                }
            } else if ("0202".equals(changeCode)) {//订单状态从“出票中”变为“未出票申请退款
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
                    List<Refund> list = this.createTTSRefund(orderNo, "2", null);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                } else {//先导入订单，在创建退票单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                    List<Refund> list = this.createTTSRefund(orderNo, "2", orderVo);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                }
            } else if ("0301".equals(changeCode)) {//：订单状态从“出票完成”变为“退票申请中”
                //添加退票单
                if (exist) {
                    List<Refund> list = this.createTTSRefund(orderNo, "2", null);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                } else {//先导入订单，在创建退票单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                    List<Refund> list = this.createTTSRefund(orderNo, "2", orderVo);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                }
            } else if ("0401".equals(changeCode)) {//订单状态由“出票完成”变成“改期申请中”
                //添加改期单
                if (exist) {
                    List<Change> list = this.createTTSChange(orderNo, "2", null);
                    changeService.initializedPassengerChange(list);
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                    List<Change> list = this.createTTSChange(orderNo, "2", orderVo);
                    changeService.saveChanges(list);
                }
            } else if ("0102".equals(changeCode)) {//未出票申请退款请求被驳回：订单状态从“未出票申请退款”变 为“支付成功等待出票”
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NO_TICKET, orderNo);
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    for (int i = 0; i <= 5; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            } else if ("0103".equals(changeCode)) {//订单状态从“取消”变 为“支付成功等待出票”
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NO_TICKET, orderNo);
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            } else if ("0402".equals(changeCode)) {
//				if(exist){
//					List<Change> list = this.createTTSChange(orderNo, "2",null);
//					changeService.initializedPassengerChange(list);
//				}else{
//					OrderVO  orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2",true);
//					orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
//					orderService.saveOrderVO(orderVo);
//					List<Change> list = this.createTTSChange(orderNo, "2",orderVo);
//					changeService.initializedPassengerChange(list);
//				}
            }
        } catch (Exception e) {
            log.error("tts rnf 订单推送异常" + changeCode + ":" + orderNo, e);
            e.printStackTrace();
            ttsS.importTTsOrderRnf();
            //rnfResponse.setResult("fail");
            //rnfResponse.setErrMsg("订单跟新失败..");
        }
        return rnfResponse;
    }

    /**
     * tts rnb订单状态变动消息通知接口
     *
     * @param
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "orderNotic/rnb", produces = "application/json;charset=UTF-8")
    public Object ttsRnbOrderStatusNotice(HttpServletRequest request, @RequestBody JSONObject jsonParam) {
        RnfResponse rnfResponse = new RnfResponse();
        if (jsonParam == null) {
            rnfResponse.setResult("fail");
            rnfResponse.setErrMsg("推送消息为空");
            return rnfResponse;
        }
        if (jsonParam.getJSONObject("data").getString("orderNo").contains("tes")) {
            //tts测试数据，忽略
            return rnfResponse;
        }
        Gson gson = new Gson();
        RnfRequestParam param = gson.fromJson(jsonParam.toString(), RnfRequestParam.class);
        DataParam data = param.getData();
        rnfResponse.setTransactionId(data.getTransactionId());
        String changeCode = data.getChangeCode();
        String orderNo = data.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            return rnfResponse;
        }
        boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo);
        try {
            if ("0101".equals(changeCode)) {//订单状态从“订座成功等待支付”变成“支付成功等待出票”
                if (!exist) {//本地没有没有则添加这个订单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    for (int i = 0; i <= 5; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            }else if ("0104".equals(changeCode)) {//订单状态从“订座成功等待支付”变成“支付成功等待出票”
                if (!exist) {//本地没有没有则添加这个订单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    for (int i = 0; i <= 5; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            } else if ("0804".equals(changeCode)) {//订单状态由“支付成功等待出票”变成“出票中”
                orderService.updateStatus(WebConstant.ORDER_PRINT, orderNo);
            } else if ("0201".equals(changeCode)) {//订单状态由“支付成功等待出票”变成“未出票申请退款”
                //添加退票单
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
                } else {//先导入订单，在创建退票单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                }
            } else if ("0203".equals(changeCode)) {//订单状态由“支付成功等待出票”变成“出票完成”
                Order order = orderService.getOrderByOrderNo(orderNo);
                if (order != null) {
                    if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
                        OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                        orderService.updateTicketNo(orderNo, orderVo.getPassengetList());
                    }
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                }
            } else if ("0204".equals(changeCode)) {//订单状态由“出票中”变为“出票完成
                Order order = orderService.getOrderByOrderNo(orderNo);
                if (order != null) {
                    if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
                        OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                        orderService.updateTicketNo(orderNo, orderVo.getPassengetList());
                    }
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                }
            } else if ("0202".equals(changeCode)) {//订单状态从“出票中”变为“未出票申请退款
                //添加退票单
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
                    List<Refund> list = this.createTTSRefund(orderNo, "1", null);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                } else {//先导入订单，在创建退票单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_NOTICK_REFUND);
                    orderService.saveOrderVO(orderVo);
                    List<Refund> list = this.createTTSRefund(orderNo, "1", orderVo);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                }
            } else if ("0301".equals(changeCode)) {//：订单状态从“出票完成”变为“退票申请中”
                //添加退票单
                if (exist) {
                    List<Refund> list = this.createTTSRefund(orderNo, "1", null);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                } else {//先导入订单，在创建退票单
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                    List<Refund> list = this.createTTSRefund(orderNo, "1", orderVo);
                    for (Refund refund : list) {
                        refundService.autoCreateRefund(refund);
                    }
                }
            } else if ("0401".equals(changeCode)) {//订单状态由“出票完成”变成“改期申请中”
                //添加改期单
                if (exist) {
                    List<Change> list = this.createTTSChange(orderNo, "1", null);
                    changeService.saveChanges(list);
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
                    orderService.saveOrderVO(orderVo);
                    List<Change> list = this.createTTSChange(orderNo, "1", orderVo);
                    changeService.saveChanges(list);
                }
            } else if ("0102".equals(changeCode)) {//未出票申请退款请求被驳回：订单状态从“未出票申请退款”变 为“支付成功等待出票”
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NO_TICKET, orderNo);
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            } else if ("0103".equals(changeCode)) {//订单状态从“取消”变 为“支付成功等待出票”
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NO_TICKET, orderNo);
                } else {
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                    for (int i = 0; i <= 5; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_QNR, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            } else if ("0402".equals(changeCode)) {
//				if(exist){
//					List<Change> list = this.createTTSChange(orderNo, "1",null);
//					changeService.initializedPassengerChange(list);
//				}else{
//					OrderVO  orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "1",true);
//					orderVo.getOrder().setStatus(WebConstant.ORDER_PRINT_TICKET);
//					orderService.saveOrderVO(orderVo);
//					List<Change> list = this.createTTSChange(orderNo, "1",orderVo);
//					changeService.initializedPassengerChange(list);
//				}
            }

        } catch (Exception e) {
            log.error("tts rnb 订单推送异常" + changeCode + ":" + orderNo);
            log.error(e.getMessage(), e);
            e.printStackTrace();
            ttsS.importTTsOrderRnb();
            //rnfResponse.setResult("fail");
            //rnfResponse.setErrMsg("订单跟新失败..");
        }
        return rnfResponse;
    }

    public List<Change> createTTSChange(String orderNo, String orderShop, OrderVO orderVo) throws Exception {
        Order order = orderService.getOrderBycOrderNo(orderNo);
        if (orderVo == null) {
            orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, orderShop);
        }
        String str = null;
        if (orderShop.equals("1")) {
            str = "rnb";
        } else {
            str = "rnf";
        }
        JSONObject json = ttsService.getChangeOrders(orderNo, str, null);
        List<Change> list = new ArrayList<Change>();
        if (json != null) {
            if (json.getInt("totalCount") > 0) {
                JSONObject changeJson = json.getJSONArray("orderList").getJSONObject(0);
                JSONArray passengerArr = changeJson.getJSONArray("ttsPassengerList");
                JSONObject flightJson = changeJson.getJSONArray("flightSegmentList").getJSONObject(0);
                for (int i = 0; i < passengerArr.size(); i++) {
                    JSONObject passengerJson = passengerArr.getJSONObject(i);
                    Change change = new Change();
                    change.setOrderNo(order.getOrderNo());
                    change.setRemark(orderVo.getOrder().getRemarkStr());
                    change.setOrderId(order.getOrderId());
                    change.setOrderShop(order.getOrderShop());
                    change.setOrderSource(order.getOrderSource());
                    change.setNewCOrderNo(changeJson.getString("gqId"));
                    change.setPassengerName(passengerJson.getString("name"));
                    //change.setTktNo(changeInfo.getString("ticketNo"));
                    change.setRevenuePrice(new BigDecimal(changeJson.getString("allPrices")).divide(new BigDecimal(passengerArr.size())).toString());
                    change.setState(WebConstant.CHANGE_UNTREATED);
                    //change.setsPnr(changeInfo.getString("orgPnrCode"));
                    change.setsAirlineCode(order.getFlightNo().substring(0, 2));
                    change.setsFlightNo(order.getFlightNo());
                    change.setsArrCityCode(order.getArrCityCode());
                    change.setsDepCityCode(order.getDepCityCode());
                    change.setsFlightDate(order.getFlightDate());
                    change.setsCabin(order.getCabin());
                    if (StringUtils.isNotEmpty(flightJson.getString("cabin")) && !"null".equals(flightJson.getString("cabin"))) {
                        change.setCabin(flightJson.getString("cabin"));
                    }
                    //change.setPnr(changeInfo.getString("pnrCode"));
                    change.setFlightNo(flightJson.getString("flightNum"));
                    change.setFlightDate(flightJson.getString("departureDay"));
                    change.setChangeDate(changeJson.getString("createTimeStr"));
                    change.setCreateBy("SYSTEM");
                    change.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    list.add(change);
                }
            }
        }
        return list;
    }

    public List<Refund> createTTSRefund(String orderNo, String orderShop, OrderVO orderVo) throws Exception {
        if (orderVo == null) {
            orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, orderShop);
        }
        Order order = orderService.getOrderBycOrderNo(orderNo);
        List<Passenger> passengetList = orderVo.getPassengetList();

        BigDecimal divide = null;
        String refundPrice = orderVo.getRefundPrice();
        String refundType = orderVo.getRefundReason();
        if(StringUtils.isNotEmpty(refundType)){
            if ("16".equals(refundType) || "17".equals(refundType)) {
                refundType = "自愿";
            } else if ("18".equals(refundType)){//18
                refundType = "非自愿";
            }else {
                refundType = "未知";
            }
        }

        if (StringUtils.isNotEmpty(refundPrice)) {
            divide = new BigDecimal(refundPrice);
        } else {
            Map<String, String> map = ttsService.getRefundMoney(orderNo, orderNo.substring(0, 3));
            refundPrice = map.get("refundMoney");
            if(StringUtils.isEmpty(refundType)){
                refundType = map.get("refundType");
            }
            if (StringUtils.isNotEmpty(refundPrice)) {
                divide = new BigDecimal(refundPrice).divide(new BigDecimal(passengetList.size()), 2, BigDecimal.ROUND_HALF_UP);
            }
        }

        List<Refund> list = new ArrayList<Refund>();
        for (Passenger passenger : passengetList) {
            Refund refund = new Refund();
            refund.setOrderId(order.getOrderId());
            if (StringUtils.isNotEmpty(orderVo.getOrder().getRemarkStr())) {
                refund.setOrderNo(orderVo.getOrder().getRemarkStr());
            } else {
                refund.setOrderNo(order.getOrderNo());
            }
            refund.setOtherRemark(orderVo.getOrder().getRemarkStr());
            refund.setRetNo(order.getOrderNo());
            refund.setOrderShop(order.getOrderShop());
            refund.setOrderSource(order.getOrderSource());
            refund.setAirlineCode(order.getAirlineCode());
            refund.setFlightNo(order.getFlightNo());
            refund.setDepCityCode(order.getDepCityCode());
            refund.setArrCityCode(order.getArrCityCode());
            refund.setFlightDate(order.getFlightDate());
            refund.setPrintTicketCabin(order.getCabin());
            refund.setPassengerName(passenger.getName());
            refund.setTicketNo(passenger.getTicketNo());
            refund.setPassengerId(passenger.getPassengerId());
            refund.setCreateBy("SYSTEM");
            refund.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            refund.setcRemState("0");
            refund.setAirRemState("0");
            refund.setPnr(order.getPnr());
            refund.setFlightType(order.getTripType());
            if (divide != null) {
                refund.setcRealPrice(divide.doubleValue());
            }
            if (StringUtils.isNotEmpty(orderVo.getReamrkStr())) {
                refund.setcAppDate(orderVo.getReamrkStr());
            } else {
                refund.setcAppDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }


            if (StringUtils.isNotEmpty(refundType)) {
                if (refundType.contains("非自愿")) {
                    refund.setRefundType(WebConstant.REFUND_TYPE_NOZIYUAN);
                } else if (refundType.contains("自愿")){
                    refund.setRefundType(WebConstant.REFUND_TYPE_ZIYUAN);
                } else {
                    refund.setRefundType("2");
                }
            }


            list.add(refund);
        }
        return list;
    }

    /**
     * 就旅行订单推送
     *
     * @param request
     * @param jsonParam
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "orderNotic/jiu", produces = "application/json;charset=UTF-8")
    public Object jiuOrderStatusNotice(HttpServletRequest request, @RequestBody JSONObject jsonParam) {
        RnfResponse rnfResponse = new RnfResponse();
        if (jsonParam == null) {
            rnfResponse.setResult("fail");
            rnfResponse.setErrMsg("推送消息为空");
            return rnfResponse;
        }
        if (jsonParam.getJSONObject("data").getString("orderNo").contains("tes")) {
            return rnfResponse;
        }
        Gson gson = new Gson();
        RnfRequestParam param = gson.fromJson(jsonParam.toString(), RnfRequestParam.class);
        DataParam data = param.getData();
        rnfResponse.setTransactionId(data.getTransactionId());
        String changeCode = data.getChangeCode();
        String orderNo = data.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            return rnfResponse;
        }
        boolean exist = orderService.isExist(InterfaceConstant.ORDER_SOURCE_JIU, orderNo);
        try {
            if ("0104".equals(changeCode)) {//订单状态从“订单待确认”变成“支付成功等待出票”
                Thread.sleep(5000);
                if (!exist) {//本地没有没有则添加这个订单
                    OrderVO orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                    for (int i = 0; i <= 5; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_JIU, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            } else if ("0304".equals(changeCode)) {//订单状态由“订单确认成功”变成“支付成功等待出票”
                Thread.sleep(5000);
                if (!exist) {
                    OrderVO orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                    for (int i = 0; i <= 6; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_JIU, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                }
            } else if ("0204".equals(changeCode)) {//订单状态由“订座成功等待支付”变成“支付成功等待出票”
                Thread.sleep(5000);
                if (!exist) {
                    OrderVO orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                    for (int i = 0; i <= 6; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                        } else {
                            break;
                        }
                    }
                    if (orderVo != null) {
                        if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_JIU, orderNo)) {
                            orderService.saveOrderVO(orderVo);
                        }
                    }
                }
            } else if ("0704".equals(changeCode)) {//订单状态由“订单取消”变成“支付成功等待出票”
                Thread.sleep(5000);
                if (!exist) {
                    OrderVO orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                    for (int i = 0; i <= 6; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                        } else {
                            break;
                        }
                    }
                    if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_JIU, orderNo)) {
                        orderService.saveOrderVO(orderVo);
                    }
                } else {
                    orderService.updateStatus(WebConstant.ORDER_NO_TICKET, orderNo);
                }
            } else if ("0201".equals(changeCode)) {//订单状态由“订座成功等待支付”变成“支付成功等待出票”
                Thread.sleep(5000);
                if (!exist) {
                    OrderVO orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                    for (int i = 0; i <= 6; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                        } else {
                            break;
                        }
                    }
                    if (orderVo != null) {
                        if (!orderService.isExist(InterfaceConstant.ORDER_SOURCE_JIU, orderNo)) {
                            orderService.saveOrderVO(orderVo);
                        }
                    }
                }
            } else if ("0405".equals(changeCode)) {//订单状态由“支付成功等待出票”变成“出票完成”
                Thread.sleep(10000);
                Order order = orderService.getOrderByOrderNo(orderNo);
                OrderVO orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                if (order != null) {
                    if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
                        orderService.updateTicketNo(orderNo, orderVo.getPassengetList());
                    }
                }
            } else if ("0413".equals(changeCode)) {//订单状态由“支付成功等待出票”变成“未出票申请退款”
                //添加退票单
                if (exist) {
                    orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
                } else {//先导入订单，在创建退票单
                    Thread.sleep(5000);
                    OrderVO orderVo = ttsService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                    for (int i = 0; i <= 6; i++) {
                        if (orderVo == null) {
                            Thread.sleep(5000);
                            orderVo = jiuOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, "1");
                        } else {
                            break;
                        }
                    }
                    orderVo.getOrder().setStatus(WebConstant.ORDER_NOTICK_REFUND);
                    orderService.saveOrderVO(orderVo);
                }
            }
        } catch (Exception e) {
            log.error("JIU订单推送异常" + changeCode + ":" + orderNo);
            log.error(e.getMessage(), e);
            e.printStackTrace();
            js.importOrder();
            //rnfResponse.setResult("fail");
            //rnfResponse.setErrMsg("订单跟新失败..");
        }
        return rnfResponse;
    }


    @RequestMapping(value = "/getSession", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void getsSession(JSONObject jsonParam) {
        System.out.println(jsonParam);
    }


    /**
     * 添加pnr
     *
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping("/orderNotic/savePnr")
    public Object savePnr(String pnr, String orderNo) {
        ResponseResult<Boolean> result = new ResponseResult<Boolean>();
        try {
            orderService.savePnr(pnr, orderNo);
        } catch (Exception e) {
            result.setCode(-1);
            result.setMsg("保存失败");
            result.setData(false);
        }
        return result;
    }


    /**
     * 机器人锁单 已没用
     *
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping("/orderNotic/robotLock")
    public Object robotLock(String orderNo) {
        ResponseResult<Boolean> result = new ResponseResult<Boolean>();
        String name = "机器人";
        OrderOperateLog log = new OrderOperateLog();
        log.setOrderNo(orderNo);
        log.setContent("锁单");
        log.setType("订单处理");
        log.setName("机器人");
        try {
            String havePcocess = orderService.isHavePcocess(orderNo);
            if (StringUtils.isNotEmpty(havePcocess)) {
                if (name.equals(havePcocess)) {
                    result.setCode(0);
                    result.setMsg(havePcocess);
                    result.setData(true);
                    logService.saveLog(log);
                } else {
                    result.setCode(-2);
                    result.setMsg(havePcocess);
                    result.setData(false);
                }
                return result;
            }
            orderService.addProcess(name, orderNo);//加锁定人
            logService.saveLog(log);
        } catch (Exception e) {
            result.setCode(-1);
            result.setMsg("锁定失败");
            result.setData(false);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/orderNotic/robotUnLock")
    public Object robotUnLock(String orderNo) {
        ResponseResult<Boolean> result = new ResponseResult<Boolean>();
        OrderOperateLog log = new OrderOperateLog();
        log.setOrderNo(orderNo);
        log.setContent("解锁订单");
        log.setType("订单处理");
        log.setName("机器人");
        try {
            String havePcocess = orderService.isHavePcocess(orderNo);
            if (StringUtils.isNotEmpty(havePcocess)) {
                if (!"机器人".equals(havePcocess)) {
                    result.setCode(-2);
                    result.setMsg(havePcocess);
                    result.setData(false);
                    return result;
                }
            }
            orderService.deleteProcess(orderNo);
            logService.saveLog(log);
        } catch (Exception e) {
            result.setCode(-1);
            result.setMsg("解锁失败");
            result.setData(false);
        }
        return result;
    }

    /**
     * 自动出票采购单，已没用
     *
     * @param jsonParam
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/orderNotic/robotPurch", produces = "application/json;charset=UTF-8")
    @Transactional
    public Object robotPurch(@RequestBody JSONObject jsonParam) {
        ResponseResult<Boolean> result = new ResponseResult<Boolean>();
        try {
            Gson gson = new Gson();
            Purchase purch = gson.fromJson(com.alibaba.fastjson.JSONObject.toJSONString(jsonParam), Purchase.class);
            OrderVO orderVo = orderService.selectOrderDetails(purch.getOrderNo(), null, null);
            if (orderVo == null) {
                result.setMsg("没有这个订单");
                return result;
            }
            purch.setOrderId(orderVo.getOrderId());
            purch.setOrderShop(orderVo.getOrderShop());
            purch.setOrderSource(DictUtils.getDictCode("order_source", orderVo.getOrderSource()));
            purch.setcOrderNo(orderVo.getcOrderNo());
            purch.setcAddDate(orderVo.getcAddDate());
            purch.setFlightDate(orderVo.getFlightDate());
            purch.setFlag("0");
            purch.setCustomerAmount(new BigDecimal(orderVo.getTotalPrice()).doubleValue());
            purch.setEmployeeName("SYSTEM");
            purch.setPrintTicketDate(new Date());
            purch.setProfit(new BigDecimal(purch.getCustomerAmount()).subtract(new BigDecimal(purch.getPayAmount())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            List<Passenger> passengetList = orderVo.getPassengetList();
            StringBuilder sb = new StringBuilder();
            for (Passenger p : passengetList) {
                sb.append(p.getName()).append(",");
                if (StringUtils.isEmpty(p.getPrintTicketBy())) {
                    Passenger nP = new Passenger();
                    nP.setPassengerId(p.getPassengerId());
                    nP.setPrintTicketBy(purch.getEmployeeName());
                    nP.setPrintTicketDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purch.getPrintTicketDate()));
                    passengerService.updateById(nP);
                }
            }
            purch.setPassengerNames(sb.deleteCharAt(sb.length() - 1).toString());
            purchaseService.savePurch(purch);
            result.setMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(-1);
            result.setMsg("添加失败");
            result.setData(false);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }


    /**
     * 获取去哪儿平台订单的销售规则
     *
     * @param orderNo
     * @return
     */
    @PostMapping("/sellRole")
    @ResponseBody
    public ResponseResult<String> sellRole(String orderNo) {
        ResponseResult<String> result = new ResponseResult<String>();
        result.setData("");
        try {
            if (orderNo.startsWith("rnf") || orderNo.startsWith("rnb")) {
                String sellRole = ttsService.getSellRole(orderNo, true);
                result.setData(sellRole);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取去哪儿平台政策连接
     *
     * @param orderNo
     * @return
     */
    @PostMapping("/getPolicyLink")
    @ResponseBody
    public ResponseResult<String> getPolicyLink(String orderNo) {
        ResponseResult<String> result = new ResponseResult<String>();
        result.setData("");
        try {
            if (orderNo.startsWith("rnf") || orderNo.startsWith("rnb")) {
                String remark = ttsService.getPolicyLink(orderNo, orderNo.substring(0, 3), true);
                result.setData(remark);
                return result;
            }
        } catch (Exception e) {

        }
        result.setCode(-1);
        return result;
    }

    /**
     * 获取去哪儿平台政策详情
     *
     * @param orderNo
     * @return
     */
    @RequestMapping("/getPolicyHtml")
    @ResponseBody
    public Object getPolicyHtml(String orderNo, String uri) {
        ResponseResult<String> result = new ResponseResult<String>();
        result.setData("");
        try {
            if (orderNo.startsWith("rnf") || orderNo.startsWith("rnb")) {
                String policyHtml = ttsService.getPolicyHtml(orderNo, uri, true);
                result.setData(policyHtml);
                return result;
            }
        } catch (Exception e) {

        }
        return result;
    }


    /**
     * 获取去哪儿平台的订单备注
     *
     * @param orderNo
     * @return
     */
    @PostMapping("/getOrderRemark")
    @ResponseBody
    public ResponseResult<String> getOrderRemark(String orderNo) {
        ResponseResult<String> result = new ResponseResult<String>();
        result.setData("");
        try {
            if (orderNo.startsWith("rnf") || orderNo.startsWith("rnb")) {
                String remark = ttsService.getOrderRemark(orderNo, orderNo.substring(0, 3), true);
                result.setData(remark);
                return result;
            }
        } catch (Exception e) {

        }
        result.setCode(-1);
        return result;
    }

    /**
     * 是否包含失信人
     *
     * @param orderNo
     * @return
     */
    @PostMapping("/isHaveBadPeopel")
    @ResponseBody
    public ResponseResult<String> isHaveBadPeopel(String orderNo) {
        ResponseResult<String> result = new ResponseResult<String>();
        try {
            List<String> dictCodeList = dictService.getDictNameList("badPeople");
            QueryWrapper<Passenger> query = new QueryWrapper<Passenger>();
            query.eq("order_no", orderNo);
            query.select("name","cert_no");
            List<Passenger> pList = passengerService.findByQueryWapper(query);
            StringBuilder sb = new StringBuilder();
            boolean hava = false;
            for (Passenger p : pList) {
                String name = p.getName();
                if (dictCodeList.contains(name)) {
                    sb.append(p.getName() + "(" + p.getCertNo() + "),");
                    hava = true;
                }
            }
            if (hava) {
                sb.append("在钓鱼名单中，请确认并联系平台拒单!");
                result.setCode(1);
                result.setData(sb.toString());
            }
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 订单状态是否改变
     *
     * @param orderNo
     * @return
     */
    @PostMapping("/getOrderStatus")
    @ResponseBody
    public ResponseResult<String> getOrderStatus(String orderNo, String orderSource) {
        ResponseResult<String> result = new ResponseResult<String>();
        try {
            if ("TTS".equals(orderSource)) {
                String orderStatus = ttsService.getOrderStatus(orderNo);
                if ("APPLY_4_RETURN_PAY".equals(orderStatus) || "REFUND_OK".equals(orderStatus)) {
                    orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
                    result.setCode(1);
                } else if ("CANCEL_OK".equals(orderStatus)) {
                    orderService.updateStatus(WebConstant.ORDER_CANCEL, orderNo);
                    result.setCode(2);
                }
            } else if ("携程".equals(orderSource)) {
                Order order = orderService.getOrderByOrderNo(orderNo);
                String status = order.getStatus();
                if ("99".equals(status) || "4".equals(status)) {
                    result.setCode(1);
                } else {
                    String orderStatus = ctripOrderService.getOrderStatus(orderNo, order.getOrderShop());
                    if ("1".equals(orderStatus.split(":")[1]) || "4".equals(orderStatus.split(":")[0])) {
                        orderService.updateStatus("99", orderNo);
                        result.setCode(1);
                    }
                }
            } else if ("就旅行".equals(orderSource)) {
                String orderStatus = jiuOrderService.getOrderStatus(orderNo);
                if (orderStatus.contains("REFUND")) {
                    //System.out.println(orderStatus);
                    orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
                    result.setCode(1);
                } else if ("CANCEL_OK".equals(orderStatus)) {
                    orderService.updateStatus(WebConstant.ORDER_CANCEL, orderNo);
                    result.setCode(2);
                }
            } else if ("淘宝".equals(orderSource)) {
                Order orderBycOrderNo = orderService.getOrderBycOrderNo(orderNo);
                if (WebConstant.ORDER_NOTICK_REFUND.equals(orderBycOrderNo.getStatus())) {
                    result.setCode(1);
                }
            } else if ("途牛".equals(orderSource)) {
                Order order = orderService.getOrderByOrderNo(orderNo);
                String status = TuniuOrderService.getStatusByOid(order.getcOrderNo(), "2", true);
                if ("5".equals(status)) {
                    result.setCode(1);
                    if (!WebConstant.ORDER_CANCEL.equals(order.getStatus())) {
                        orderService.updateStatus(WebConstant.ORDER_CANCEL, orderNo);
                    }
                }
            }
        } catch (Exception e) {

        }
        return result;
    }
}
