package cn.ssq.ticket.system.service.impl;


import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.enums.Dagree;
import cn.ssq.ticket.system.enums.TicketPlace;
import cn.ssq.ticket.system.exception.OrderToMuchException;
import cn.ssq.ticket.system.mapper.FlightMapper;
import cn.ssq.ticket.system.mapper.OrderMapper;
import cn.ssq.ticket.system.mapper.PassengreMapper;
import cn.ssq.ticket.system.mapper.TravelMapper;
import cn.ssq.ticket.system.queryEntity.OrderQuery;
import cn.ssq.ticket.system.runnable.GetAvStatus;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 订单服务层
 */
@Service
public class OrderServiceImpl implements OrderService {

    private Logger logg = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private FlightMapper flightMapper;

    @Autowired
    private PassengreMapper passangerMapper;

    @Autowired
    private TravelMapper travelMapper;

    private static SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;


    @Override
    public Object selectOrderList(OrderQuery orderQuery) {
        Map<String, Object> map = new HashMap<String, Object>();
        Integer count;
        if (StringUtils.isNoneBlank(orderQuery.getName()) || StringUtils.isNoneBlank(orderQuery.getPnr()) || StringUtils.isNoneBlank(orderQuery.getTicketNo())) {
            //多表关联查询
            List<OrderVO> orderVoList = orderMapper.selectOrderVoList(orderQuery);
            count = orderMapper.selectOrderVoCount(orderQuery);
            map.put("list", orderVoList);
        } else {
            //单表查询，多数是执行这里
            List<Order> orderList = orderMapper.selectOrderList(orderQuery);
            count = orderMapper.selectOrderCount(orderQuery);
            map.put("list", orderList);
        }
        map.put("count", count);
        return map;

    }


    /**
     * 获取订单详情
     * @param orderNo
     * @param orderSource
     * @param orderShop
     * @return
     * @throws OrderToMuchException
     */
    @Override
    public OrderVO selectOrderDetails(String orderNo, String orderSource, String orderShop) throws OrderToMuchException {
        if ("0".equals(orderSource)) {
            orderSource = null;
        }
        if ("0".equals(orderShop)) {
            orderShop = null;
        }
        List<OrderVO> list = orderMapper.selectOrderDetails(orderNo, orderSource, orderShop);
        if (list.size() == 0) {
            return null;
        }
        OrderVO orderVO = list.get(0);
        // 就旅行 订单来源 特殊处理
//        if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderVO.getOrderSource())) {
//            // 就旅行（agent……/去哪儿/蜗牛）  ||  A去哪儿/蜗牛 B携程，D智行/马蜂窝
//            StringBuffer str = new StringBuffer();
//            str.append(DictUtils.getDictName("order_source", orderVO.getOrderSource()));
//            if (StringUtils.isNoneBlank(orderVO.getWebsiteOrderSource())) {
//                str.append(" （");
//                String first = orderVO.getWebsiteOrderSource().substring(0,1);
//                if ("a".equals(first) || "A".equals(first)) {
//                    str.append("/去哪儿/蜗牛");
//                } else if ("b".equals(first) || "B".equals(first)) {
//                    str.append("/携程");
//                } else if ("d".equals(first) || "D".equals(first)) {
//                    str.append("/智行/马蜂窝");
//                }
//                str.append("）");
//            }
//            orderVO.setOrderSource(str.toString());
//        } else {
//        }

        orderVO.setOrderSource(DictUtils.getDictName("order_source", orderVO.getOrderSource()));
        orderVO.setTripType(DictUtils.getDictName("trip_type", orderVO.getTripType()));



        orderVO.setStatus(DictUtils.getDictName("order_status", orderVO.getStatus()));
        for (Passenger passenger : orderVO.getPassengetList()) {
            passenger.setPassengerType(DictUtils.getDictName("passenger_type", passenger.getPassengerType()));
            passenger.setCertType(DictUtils.getDictName("cert_type", passenger.getCertType()));
            passenger.setStatus(DictUtils.getDictName("ticket_status", passenger.getStatus()));
        }
        return orderVO;
    }


    /**
     * 查询需要自动出票的订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO selectOrderDetailsAuto(String orderNo) {

        List<OrderVO> list = orderMapper.selectOrderDetails(orderNo, null, null);
        if (list.size() == 0) {
            return null;
        }
        OrderVO orderVO = list.get(0);
        return orderVO;
    }

    /***
     * 新增一个订单
     */
    @Override
    @Transactional
    public void addOrder(Order order, List<Passenger> pList, List<Flight> fList) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        String date = DateFormatUtils.format(calendar.getTime(), "yyyyMMddHHmmss");
        // 公式：[a,b]  :  (int)(Math.random() * (b - a + 1) )+ a
        int random = (int) (Math.random() * 900 + 100);
        // 订单号生成规则：xmy + 当前年月日（yyyyMMdd） + 100-999随机整数
        String orderNo = "xmy" + date + random;

        order.setOrderNo(orderNo);
        orderMapper.insert(order);
        for (Passenger passenger : pList) {
            passenger.setOrderId(order.getOrderId());
            passenger.setOrderNo(orderNo);
            passangerMapper.insert(passenger);
        }
        for (Flight flight : fList) {
            flight.setOrderId(order.getOrderId());
            flight.setOrderNo(orderNo);
            flightMapper.insert(flight);
        }

    }

    /**
     * 修改订单
     */
    @Override
    @Transactional
    public void editOrder(Order order, List<Passenger> pList, List<Flight> fList) {
        orderMapper.update(order, new QueryWrapper<Order>().eq("order_no", order.getOrderNo()));
        for (Passenger passenger : pList) {
            passangerMapper.updateById(passenger);
        }
        for (Flight flight : fList) {
            flightMapper.updateById(flight);
        }
        orderMapper.deleteProcess(order.getOrderNo());
    }


    @Override
    public void deleteOrder(String[] orderNoArray) {
        orderMapper.deleteOrder(orderNoArray);
        for (String string : orderNoArray) {
            QueryWrapper<Passenger> p = new QueryWrapper<Passenger>();
            p.eq("order_no", string);
            passangerMapper.delete(p);
            QueryWrapper<Flight> p2 = new QueryWrapper<Flight>();
            p2.eq("order_no", string);
            flightMapper.delete(p2);
            QueryWrapper<Travel> p3 = new QueryWrapper<Travel>();
            p3.eq("order_no", string);
            travelMapper.delete(p3);
        }
    }


    @Override
    public Order getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        return order;
    }

    @Override
    public Order getOrderBycOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("C_order_no", orderNo));
        return order;
    }

    @Override
    public Order getOrderById(String orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public void updateStatus(String status, String orderNo) {
        orderMapper.updateStatus(status, orderNo);


    }


    @Override
    public int addProcess(String process, String orderNo) {
        return orderMapper.addProcess(process, orderNo);

    }

    @Override
    public void deleteProcess(String orderNo) {
        orderMapper.deleteProcess(orderNo);
    }

    @Override
    public String isHavePcocess(String orderNo) {
        String pcocess = orderMapper.isHavePcocess(orderNo);
        return pcocess;
    }

    /**
     * 是否存在订单
     */
    @Override
    public synchronized boolean isExist(String orderSource, String orderNo) {
        String no = orderMapper.isExist(orderSource, orderNo);
        if (StringUtils.isEmpty(no)) {
            return false;
        }
        return true;
    }


    /**
     * 是否已存在订单
     * @param cOrderNo
     * @param orderSource
     * @return
     */
    @Override
    public synchronized boolean isExistcOrderNo(String cOrderNo, String orderSource) {
        String no = orderMapper.isExistcOrderNo(cOrderNo, orderSource);
        if (StringUtils.isBlank(no)) {
            return false;
        }
        return true;
    }


    /**
     * 获取订单所有乘客姓名
     */
    @Override
    public List<String> getOrderPassenger(String orderNo) {
        return orderMapper.getOrderPassenger(orderNo);
    }


    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30)
    public int saveOrderVO(OrderVO orderVO) {
        //是否来单就锁
        boolean lockOrder = false;
        Order order = orderVO.getOrder();
        if (this.isExist(order.getOrderSource(), order.getOrderNo())) {
            return 1;
        }
        List<Passenger> passengerList = orderVO.getPassengetList();
        List<Flight> flightList = orderVO.getFlightList();
        String orderSource = order.getOrderSource();
        String policyType = order.getPolicyType();
        String flightNo = order.getFlightNo();
        String flightDate = order.getFlightDate();
        if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource) || InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)
                || InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource) || InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource)
                || InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)) {
            //b2b来单即锁
            if (StringUtils.isNotEmpty(policyType) && flightNo.startsWith("ZH")) {
                if (policyType.contains("ZHB2B") || policyType.contains("SHEB2B")) {
                    if ("T".equals(DictUtils.getDictCode("zh_lock", "isLockB2B"))) {
                        lockOrder = true;
                    }
                }
            }
        }
        if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource) || InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)
                || InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource) || InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)) {
            if (StringUtils.isNotEmpty(policyType)) {
                //鹏鹏来单即锁
                if (policyType.contains("FCPPDJ")) {
                    if ("Y".equals(DictUtils.getDictCode("zh_lock", "isLockPP"))) {
                        lockOrder = true;
                    }
                }
                //51BOOK来单即锁
                if (policyType.contains("51BOOK")) {
                    if ("G".equals(DictUtils.getDictCode("zh_lock", "isLock51"))) {
                        lockOrder = true;
                    }
                }
            }
        }
        for (Passenger p : passengerList) {
            //非成人暂无法自动出票
            if (!"0".equals(p.getPassengerType())) {
                lockOrder = false;
            }
        }
        if (flightList.size() > 1) {
            //往返暂无法自动出票
            lockOrder = false;
        }

//        if (dontLock(orderVO)) {
//            // 指定情况机器人不锁单。   true: 不锁单， false：保持现状
//            lockOrder = false;
//        }

        if (lockOrder) {
            order.setProcessBy("机器人");
        }

        String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try {
            if (flightNo.startsWith("ZH") && !"机器人".equals(order.getProcessBy())) {
                if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource) || InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource) || InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)
                || InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource) || InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)) {
                    //商旅。反踩来单即锁
                    if(policyType.contains("ZH") && policyType.contains("SL")){
                        String code = DictUtils.getDictCode("zh_lock", "isLock");
                        if ("1".equals(code) ) {
                            order.setcAddDate(createDate);
                            order.setProcessBy("深航采购");
                            order.setIsAuto(TicketPlace.ZHSL.getValue()+"");
                        }
                    }else if(policyType.contains("FCQN")){
                        String code = DictUtils.getDictCode("zh_lock", "isLockQN");
                        if ("Q".equals(code)) {
                            order.setcAddDate(createDate);
                            order.setProcessBy("深航采购");
                            order.setIsAuto(TicketPlace.FCQN.getValue()+"");
                        }
                    }
                }
            }

            //行程KEY自增
            String key=flightNo+"-"+flightDate;
            if (redisTemplate.hasKey(key)){
                redisTemplate.opsForValue().increment(key);
            }else {
                redisTemplate.opsForValue().set(key,1,1,TimeUnit.DAYS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        order.setCreateDate(createDate);
        int count = orderMapper.insert(order);
        for (Passenger passenger : passengerList) {
            passenger.setOrderId(order.getOrderId());
            passangerMapper.insert(passenger);
        }
        for (Flight flight : flightList) {
            flight.setOrderId(order.getOrderId());
            flightMapper.insert(flight);
        }
        OrderThreadPoolUtil.execute(new GetAvStatus(flightList.get(0)));
        return count;
    }

    /**
     * 指定情况机器人不锁单
     * @param orderVO
     * @return true：不锁单
     */
    public boolean dontLock(OrderVO orderVO){
        try {
            Order order = orderVO.getOrder();
            String orderSource = order.getOrderSource();    //订单来源
            String orderShop = order.getOrderShop();    //店铺
            String ticType = order.getTicType();    //产品类型
            String policyRemark = order.getPolicyRemark();  //政策备注
            String airlineCode = order.getAirlineCode();    //航班航空公司二字码
            String cabin = order.getCabin();    //舱位
            int passengerCount = Integer.parseInt(order.getPassengerCount());  //乘机人数

            if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource) && StringUtils.isNoneBlank(ticType) && ticType.contains("包机")) {
                return  true;

            } else if (!(InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource) && "2".equals(orderShop) && StringUtils.isNoneBlank(policyRemark) && policyRemark.contains("500"))) {
                // 去哪儿主店
                return  true;

            } else if (!(InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource) && "1".equals(orderShop) && StringUtils.isNoneBlank(policyRemark) && policyRemark.contains("666"))) {
                // 去哪儿分销
                return  true;

            } else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource) && StringUtils.isNoneBlank(ticType) && ticType.contains("服务发票")) {
                return  true;

            } else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource) && StringUtils.isNoneBlank(ticType) && ticType.contains("特价")) {
                return  true;

            } else if (InterfaceConstant.ORDER_SOURCE_TN.equals(orderSource)) {
                return  true;

            } else if ("ZH".equals(airlineCode)) {
                if (("G".equals(cabin) || "Y".equals(cabin)) && passengerCount>1) {
                    return  true;
                } else if (("B".equals(cabin) || "M".equals(cabin) || "M1".equals(cabin) || "MU".equals(cabin)) && passengerCount>=2) {
                    return  true;
                } else if (("H".equals(cabin) || "Q".equals(cabin) || "Q1".equals(cabin) || "V".equals(cabin) || "V1".equals(cabin)) && passengerCount>=3) {
                    return  true;
                } else if (("W".equals(cabin) || "S".equals(cabin) || "E".equals(cabin)) && passengerCount>=4) {
                    return  true;
                }
            }
            return  false;
        } catch (Exception e) {
            logg.error("不锁单票判断出错：",e);
            return  false;
        }
    }

    /**
     * 跟新票号
     */
    @Override
    @Transactional
    public void updateTicketNo(String orderNo, List<Passenger> list) {
        String status = orderMapper.getStatus(orderNo);
        if (!WebConstant.ORDER_PRINT_TICKET.equals(status)) {
            for (Passenger passenger : list) {
                Passenger newP = new Passenger();
                newP.setTicketNo(passenger.getTicketNo());
                newP.setTicketStatus(WebConstant.OK_TICKET);
                //passenger.setPrintTicketBy("SYSTEM");
                newP.setStatus(WebConstant.OK_TICKET);
                newP.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                passangerMapper.update(newP, new UpdateWrapper<Passenger>().eq("cert_no", passenger.getCertNo()).eq("order_no", passenger.getOrderNo()));
            }
            orderMapper.updateStatus(WebConstant.ORDER_PRINT_TICKET, orderNo);    //跟新已出票
        }
    }


    @Override
    public List<String> getOrderNoList(String orderSource, String orderShop,
                                       String status) {
        return orderMapper.getOrderNoList(orderSource, orderShop, status);
    }

    @Override
    public List<String> getcOrderNoList(String orderSource, String orderShop,
                                        String status) {
        return orderMapper.getcOrderNoList(orderSource, orderShop, status);
    }

    @Override
    public List<OrderVO> getOrderVoList(String startDate, String endDate,
                                        String orderSource, String orderShop) {
        if ("0".equals(orderShop)) {
            orderShop = null;
        }
        if (!"0".equals(orderShop)) {
            if (!Util.orderSorceList.contains(orderSource)) {
                orderShop = null;
            }
        }
        if ("0".equals(orderSource)) {
            orderSource = null;
            orderShop = null;
        }
        return orderMapper.getOrderVoList(startDate, endDate, orderSource, orderShop);
    }

    /**
     * 更新订单
     */
    @Override
    public void updateOrder(Order order) {
        orderMapper.update(order, new UpdateWrapper<Order>().eq("order_no", order.getOrderNo()));
    }

    @Override
    public List<Order> getOrderList(QueryWrapper<Order> query) {
        return orderMapper.selectList(query);
    }

    @Override
    public Integer getLockCount(String name) {
        return orderMapper.getLockCount(name, DateFormatUtils.format(new Date(), "yyyy-MM-dd") + " 00:00:00", DateFormatUtils.format(new Date(), "yyyy-MM-dd") + " 23:59:59");
    }


    @Override
    public IPage<Order> getOrderList2(QueryWrapper<Order> query,
                                      Page<Order> orderPag) {
        IPage<Order> selectPage = orderMapper.selectPage(orderPag, query);
        List<Order> orderList = selectPage.getRecords();
        Date now =new Date();
        try {
            for (Order order : orderList) {
                String orderSource = order.getOrderSource();
                String key = order.getFlightNo() + "-" + order.getFlightDate();
                Object sc = redisTemplate.opsForValue().get(key);
                if (sc != null) {
                    order.setSameCount(Integer.valueOf(sc.toString()));
                }
                order.setDegree(Dagree.DEFAULT.getValue());
                order.setDegreeStr(Dagree.DEFAULT.getRemark());
//            if(Integer.valueOf(order.getPassengerCount())>1){
//                order.setDegree(Dagree.ORANGE.getValue());
//            }
                String avStatus = order.getAvStatus();
                if (StringUtils.isNotEmpty(avStatus)) {
                    boolean matches = avStatus.matches("\\d+");
                    if (matches) {
                        int seatValue = Integer.valueOf(avStatus).intValue();
                        if (seatValue <= 3) {
                            order.setDegree(Dagree.YELLOW1.getValue());
                            order.setDegreeStr(Dagree.YELLOW1.getRemark());
                        }
                    } else {
                        if (!"A".equals(avStatus)) {
                            order.setDegree(Dagree.YELLOW1.getValue());
                            order.setDegreeStr(Dagree.YELLOW1.getRemark());
                        }
                    }
                }
                String lastPrintTicketTime = order.getLastPrintTicketTime();
                if (StringUtils.isNotEmpty(lastPrintTicketTime)) {
                    String dateReg = "^(\\d{4})-([0-1]\\d)-([0-3]\\d)\\s([0-5]\\d):([0-5]\\d):([0-5]\\d)$";
                    if (lastPrintTicketTime.matches(dateReg)) {
                        Date lastTime = SDF.parse(lastPrintTicketTime);
                        long diff = lastTime.getTime() - now.getTime();
                        long minute = diff / 60 / 1000;
                        if (minute > 0 && minute <= 10) {
                            order.setDegree(Dagree.RED.getValue());
                            order.setDegreeStr(String.format(Dagree.RED.getRemark(), minute));
                        } else if (minute > 0 && minute <= 30) {
                            order.setDegree(Dagree.YELLOW.getValue());
                            order.setDegreeStr(String.format(Dagree.YELLOW.getRemark(), minute));
                        } else if (minute <= 0) {
                            order.setDegree(Dagree.RED1.getValue());
                            order.setDegreeStr(Dagree.RED1.getRemark());
                        }

                    } else {
                        //同城催出票，超时
                        order.setDegree(Dagree.YELLOW.getValue());
                        order.setDegreeStr(Dagree.YELLOW.getRemark());
                        if (lastPrintTicketTime.contains("超时")) {
                            order.setDegree(Dagree.RED1.getValue());
                            order.setDegreeStr(Dagree.RED1.getRemark());
                        }
                    }

                }
            }
            Collections.sort(orderList, (o1, o2) -> o2.getDegree() - o1.getDegree());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectPage;
    }


    @Override
    public String getStatus(String orderNo) {
        String status = orderMapper.getStatus(orderNo);
        return status;
    }


    @Override
    public List<Integer> getB2BtciketCount(String policyType, String startDate,
                                           String endDate) {
        return orderMapper.getB2BtciketCount(policyType, startDate, endDate);
    }


    @Override
    public int savePnr(String pnr, String orderNo) {
        orderMapper.savePnr(pnr, orderNo);
        return 1;
    }


    @Override
    public void updateOrderByID(Order order) {
        orderMapper.updateById(order);

    }


    private int getShenHangLock() {
        int i = 0;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -20);
            String maxTime = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
            Integer autoPrintLocks = orderMapper.getAutoPrintLocks(maxTime, "深航采购", WebConstant.ORDER_NO_TICKET);
            return autoPrintLocks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }


    @Override
    public Integer getAutoPrintLocks(String minDate, String name, String status) {
        return orderMapper.getAutoPrintLocks(minDate, name, status);
    }

    @Override
    public Order getOutOrderNo(String orderNo) {
        return orderMapper.getOurOrderNo(orderNo);
    }

    @Override
    public String getPnrByOrderNo(String orderNo) {
        return orderMapper.getPnrByOrderNo(orderNo);
    }


    @Override
    public void updateBigPnr(String bigPnr, long orderId) {
        orderMapper.updateBigPnr(bigPnr, orderId);
    }

    @Override
    public void updateOutOrderNo(String outOrderNo, String status, long orderId) {
        orderMapper.updateOutOrderNo(outOrderNo,status, orderId);
    }


    @Override
    public int updateByWrapper(Order order, UpdateWrapper<Order> updateWrapper) {
        return orderMapper.update(order, updateWrapper);
    }
}
