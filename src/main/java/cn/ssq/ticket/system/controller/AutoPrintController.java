package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.SHSL.AskPriceDTO;
import cn.ssq.ticket.system.entity.woNiu.PriceInfo;
import cn.ssq.ticket.system.entity.woNiu.PriceSearch;
import cn.ssq.ticket.system.enums.TicketPlace;
import cn.ssq.ticket.system.service.AskPriceService;
import cn.ssq.ticket.system.service.LogService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.PPUtil;
import cn.ssq.ticket.system.util.WebConstant;
import cn.ssq.ticket.system.util.WoNiuUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 半自动出票控制层
 */
@Component
@RequestMapping("auto")
public class AutoPrintController {


    /**
     * 深航商旅报价微服务
     */
    @Autowired
    private AskPriceService askPriceService;

    @Autowired
    private OrderService orderService;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private LogService logService;


    /**
     * 获取鹏鹏报价
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/ppPrice")
    public ResponseResult<Map<String,String>> ppPrice(String orderNo){
        ResponseResult<Map<String,String>> responseResult=new ResponseResult<>();
        try {
            /*Object object = redisTemplate.opsForValue().get(orderNo+":pp");
            if(object!=null){
                JSONObject jsonObject = JSONObject.fromObject(object);
                Gson gson=new Gson();
                ResponseResult responseResult1 = gson.fromJson(jsonObject.toString(), ResponseResult.class);
                return  responseResult1;
            }*/
            OrderVO orderVO = orderService.selectOrderDetailsAuto(orderNo);
            double ticketPrice=Double.valueOf(orderVO.getPassengetList().get(0).getTicketPrice()).doubleValue() -3;
            if(ticketPrice<=0){
                ticketPrice=203;
            }
            Map<String, String> lowPriceNoTP = PPUtil.getLowPriceNoTP(orderVO, ticketPrice + "");
            if(StringUtils.isEmpty(lowPriceNoTP.get("sellPrice"))){
                lowPriceNoTP.put("sellPrice","没有获取到价格");
            }
            responseResult.setData(lowPriceNoTP);
            //setKV(orderNo+":pp",responseResult);
        } catch (Exception e) {
            Map<String,String> map=new HashMap<>();
            e.printStackTrace();
            responseResult.setData(map);
            responseResult.setCode(-1);
        }
        return responseResult;
    }


    /**
     * 获取深航商旅报价
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/shslPrice")
    public ResponseResult<Map<String,String>> shslPrice(String orderNo){
        ResponseResult<Map<String,String>> responseResult=new ResponseResult<>();
        Map<String,String> map=new HashMap<>();
        responseResult.setData(map);
        try {
            Gson gson=new Gson();
           /* Object object = redisTemplate.opsForValue().get(orderNo+":sh");
            if(object!=null){
                JSONObject jsonObject = JSONObject.fromObject(object);
                ResponseResult responseResult1 = gson.fromJson(jsonObject.toString(), ResponseResult.class);
                return  responseResult1;
            }*/
            OrderVO orderVO = orderService.selectOrderDetailsAuto(orderNo);
            for (Passenger passenger : orderVO.getPassengetList()) {
                if(!"0".equals(passenger.getCertType())){
                    responseResult.setCode(-1);
                    return responseResult;
                }
            }
            AskPriceDTO askPriceDTO=new AskPriceDTO();
            askPriceDTO.setDpt(orderVO.getDepCityCode());
            askPriceDTO.setArr(orderVO.getArrCityCode());
            askPriceDTO.setCabin(orderVO.getCabin());
            askPriceDTO.setDate(orderVO.getFlightDate());
            String flightNo = orderVO.getFlightNo();
            if(flightNo.startsWith("KY")) {
                String key=orderVO.getDepCityCode()+orderVO.getArrCityCode()+flightNo;
                String flightValue = PPUtil.KY_ZH.get(key);
                if(StringUtils.isNotEmpty(flightValue)){
                    flightNo = flightValue;
                }else{
                    //flightNo = flightNo.replace("KY", "ZH");
                }
            }
            askPriceDTO.setFlightNo(flightNo);
            String askPriceDTOJsonStr = gson.toJson(askPriceDTO);
            AskPriceDTO ss= askPriceService.askPrice(askPriceDTOJsonStr);
            if(ss!=null){
                double count = Double.valueOf(orderVO.getPassengerCount()).doubleValue();
                double tax=count*50;
                double allTicket = Double.valueOf(ss.getTicketPrice()).doubleValue()*count;
                double sellPrice=allTicket+tax;
                map.put("sellPrice",sellPrice+"");
                map.put("ticketPrice",ss.getTicketPrice());
                //setKV(orderNo+":sh",responseResult);
            }else{
                responseResult.setCode(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult.setCode(-1);
        }
        return responseResult;
    }


    /**
     * 获取蜗牛报价
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/wnPrice")
    public ResponseResult<Map<String,String>> wnPrice(String orderNo){
        ResponseResult<Map<String,String>> responseResult=new ResponseResult<>();
        Map<String,String> map=new HashMap<>();
        responseResult.setData(map);
        try {
            Gson gson=new Gson();
            OrderVO orderVO = orderService.selectOrderDetailsAuto(orderNo);
            for (Passenger passenger : orderVO.getPassengetList()) {
                if(!"0".equals(passenger.getCertType())){
                    responseResult.setCode(-1);
                    responseResult.setMsg("非身份证暂不可处理");
                    return responseResult;
                }
            }
            String flightNo = orderVO.getFlightNo();
            if(flightNo.startsWith("KY")) {
                String key=orderVO.getDepCityCode()+orderVO.getArrCityCode()+flightNo;
                String flightValue = PPUtil.KY_ZH.get(key);
                if(StringUtils.isNotEmpty(flightValue)){
                    flightNo = flightValue;
                }else{
                   // flightNo = flightNo.replace("KY", "ZH");
                }
            }
            orderVO.setFlightNo(flightNo);
            PriceSearch priceSearch =new PriceSearch();
            priceSearch.setDpt(orderVO.getDepCityCode());
            priceSearch.setArr(orderVO.getArrCityCode());
            priceSearch.setDate(orderVO.getFlightDate());
            priceSearch.setFlightNum(orderVO.getFlightNo());
            priceSearch.setEx_track("youxuan");
            priceSearch.setCabin(orderVO.getCabin());
            PriceInfo priceInfo = WoNiuUtil.searchPrice(priceSearch);
            if(priceSearch!=null){
                double count = Double.valueOf(orderVO.getPassengerCount()).doubleValue();
                double sellPrice = Double.valueOf(priceInfo.getSellPrice()).doubleValue()*count;
                map.put("sellPrice",sellPrice+"");
                map.put("ticketPrice",priceInfo.getPrice()+"");
            }else{
                responseResult.setCode(-1);
                responseResult.setMsg("无官网特价");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult.setMsg("无官网特价");
            responseResult.setCode(-1);
        }
        return responseResult;
    }


    /**
     * 下单
     * @param ticketPlace
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/autoPrint")
    public ResponseResult<Void> autoPrint(int ticketPlace, HttpServletRequest request){
        ResponseResult<Void> responseResult=new ResponseResult<>();
        OrderOperateLog log = new OrderOperateLog();
        try {
            String orderNo = request.getParameter("orderNo");
            log.setOrderNo(orderNo);
            log.setType("订单处理");
            log.setName("SYSTEM");
            Object o = redisTemplate.opsForValue().get(orderNo);
            if(o!=null){
                //防止处理中重复提交任务
                responseResult.setMsg(orderNo+"已在处理中...");
                responseResult.setCode(-1);
                return  responseResult;
            }
            if(ticketPlace== TicketPlace.PP.getValue()){
                //蜜蜂系统在鹏鹏下单，由自动出票系统负责出票
                String ticketPrice=request.getParameter("ppTicket");
                OrderVO orderVO = orderService.selectOrderDetailsAuto(orderNo);
                boolean isOk = PPUtil.payPPDJ(orderVO, 100, ticketPrice);
                if (isOk){
                    log.setContent("鹏朋采购成功，等待机器人票号回填!");
                    responseResult.setMsg(log.getContent());
                    Order order = new Order();
                    order.setIsAuto(ticketPlace+"");
                    order.setStatus(WebConstant.ORDER_PRINT);
                    UpdateWrapper<Order> updateWrapper=new UpdateWrapper<>();
                    updateWrapper.eq("order_no",orderNo);
                    orderService.updateByWrapper(order, updateWrapper);
                    redisTemplate.opsForValue().set(orderNo,"采购中",1,TimeUnit.DAYS);
                }else{
                    responseResult.setCode(-1);
                    log.setContent("鹏朋下单失败,手工处理!");
                    responseResult.setMsg(log.getContent());
                }
            }else if (ticketPlace== TicketPlace.WONIU.getValue()){
                //蜜蜂系统在蜗牛下单，由自动出票系统负责出票
                OrderVO orderVO = orderService.selectOrderDetailsAuto(orderNo);
                boolean isOk = WoNiuUtil.createAndPay(orderVO, 50);
                if(isOk){
                    log.setContent("蜗牛采购成功，等待机器人票号回填!");
                    responseResult.setMsg(log.getContent());
                    Order order = new Order();
                    order.setIsAuto(ticketPlace+"");
                    order.setStatus(WebConstant.ORDER_PRINT);
                    UpdateWrapper<Order> updateWrapper=new UpdateWrapper<>();
                    updateWrapper.eq("order_no",orderNo);
                    orderService.updateByWrapper(order, updateWrapper);
                    redisTemplate.opsForValue().set(orderNo,"采购中",1,TimeUnit.DAYS);
                }else{
                    responseResult.setCode(-1);
                    log.setContent("蜗牛下单失败,手工处理!");
                    responseResult.setMsg(log.getContent());
                }
            }else if(ticketPlace== TicketPlace.ZHSL.getValue()){
                //修改字段即可，由深航采购系统发现并处理
                Order order = new Order();
                order.setIsAuto(ticketPlace+"");
                UpdateWrapper<Order> updateWrapper=new UpdateWrapper<>();
                updateWrapper.eq("order_no",orderNo);
                int i = orderService.updateByWrapper(order, updateWrapper);
                if(i<1){
                    responseResult.setCode(-1);
                    log.setContent("深航商旅下单失败,手工处理");
                    responseResult.setMsg(log.getContent());
                }else{
                    log.setContent("等待机器人处理..");
                    responseResult.setMsg(log.getContent());
                    redisTemplate.opsForValue().set(orderNo,"采购中",1,TimeUnit.DAYS);
                }
            }
        } catch (Exception e) {
            responseResult.setCode(-1);
            log.setContent("下单失败,手工处理!");
            responseResult.setMsg(log.getContent());
        }
        logService.saveLog(log);
        return responseResult;
    }




    //保存缓存
    public int setKV(String key,Object value){
        try {
            redisTemplate.opsForValue().set(key, value,30, TimeUnit.MINUTES);
        } catch (Exception e) {
            return 0;
        }
        return 1;

    }

    //删除缓存
    public int delKV(String key){
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            return 0;
        }
        return 1;

    }




    @ResponseBody
    @RequestMapping(value = "orderNotic/tt")
    @HystrixCommand(fallbackMethod = "getDefaultUser")
    public Object TT(){
        AskPriceDTO askPriceDTO=new AskPriceDTO();
        askPriceDTO.setDpt("SHE");
        askPriceDTO.setArr("NNG");
        askPriceDTO.setCabin("E");
        askPriceDTO.setDate("2020-12-31");
        askPriceDTO.setFlightNo("ZH9370");
        Gson gson=new Gson();
        String s = gson.toJson(askPriceDTO);
        System.out.println(s);
        AskPriceDTO askPriceDTO1 = askPriceService.askPrice( s);
        System.out.println(askPriceDTO1);
        return  askPriceDTO1;
    }

    public String getDefaultUser() {
        System.out.println("默认回调函数");
        return "{\"id\":-1,\"name\":\"xxx\",\"password\":\"123456\"}";
    }

}
