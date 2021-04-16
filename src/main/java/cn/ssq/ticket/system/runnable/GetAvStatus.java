package cn.ssq.ticket.system.runnable;

import cn.ssq.ticket.system.entity.Flight;
import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.mapper.OrderMapper;
import cn.ssq.ticket.system.util.CabinUtilA;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAvStatus implements  Runnable{

    private static OrderMapper orderMapper = SpringContextHolder.getBean(OrderMapper.class);

    private final static Logger logger = LoggerFactory.getLogger(GetAvStatus.class);

    private Flight flight;

    public GetAvStatus(Flight flight) {
        this.flight = flight;
    }

    @Override
    public void run() {
        try {
            String orderNo = flight.getOrderNo();
            String dep=flight.getDepCityCode();
            String arr=flight.getArrCityCode();
            String depDate=flight.getFlightDepDate();
            String depTime=flight.getDepTime();
            String flightNo=flight.getFlightNo();
            String cabin=flight.getCabin();
            String avStatus = CabinUtilA.getAvStatus(dep, arr, depDate, depTime, flightNo, cabin);
            if (StringUtils.isEmpty(avStatus)){
                Thread.sleep(3000);
                avStatus = CabinUtilA.getAvStatus(dep, arr, depDate, depTime, flightNo, cabin);
            }
            if (StringUtils.isNotEmpty(avStatus)){
                Order order = new Order();
                order.setAvStatus(avStatus);
                UpdateWrapper<Order> updateWrapper=new UpdateWrapper<>();
                updateWrapper.eq("order_no",orderNo);
                int update = orderMapper.update(order, updateWrapper);
                logger.info(orderNo+"保存av结果："+update+",av状态："+avStatus+",参数:"+dep+arr+depDate+depTime+cabin);
            }else {
                logger.info(orderNo+"获取av结果失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存av结果异常",e);
        }


    }
}
