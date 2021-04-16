package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.Flight;
import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.entity.importBean.CtripBean.CtripOrderVO;
import cn.ssq.ticket.system.service.OrderImport.impl.CtripOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TTsOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TbOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TcOrderService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.ssq.ticket.system.util.WebConstant;
import cn.stylefeng.guns.core.common.page.LayuiPageInfo;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/noTick")
public class NoTickController {
	private String PREFIX = "/modular/system/order/";

	@Autowired
	private OrderService orderService;

	@Autowired
	private TTsOrderService ttsOrderService;

	@Autowired
	private CtripOrderService ctripOrderService;

	@Autowired
	private TcOrderService tcOrderService;
	
	@Autowired
	private TbOrderService tbOrderService;

	
	
	@RequestMapping("/toOrderIndex")
	public String orderIndex(){
		return PREFIX + "no_tick_index.html";
	}
	
	/**
	 * 获取订单列表
	 * @param
	 * @return
	 */
	@PostMapping("/getOrder")
	@ResponseBody
	public Object getOrderByPage(HttpServletRequest request){
		LayuiPageInfo result = new LayuiPageInfo();
		try {
			QueryWrapper<Order> query=new QueryWrapper<Order>();
			String startDate=request.getParameter("startDate");
			String endDate=request.getParameter("endDate");
			if(StringUtils.isEmpty(endDate)){
				endDate=DateFormatUtils.format(new Date(), "yyyy-MM-dd")+" 23:59:59";
			}else{
				endDate=request.getParameter("endDate") + " 23:59:59";
			}
			if(StringUtils.isEmpty(startDate)){
				Calendar calendar=Calendar.getInstance();
				calendar.add(Calendar.MONTH, -1);
				startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd")+" 00:00:00";
			}else{
				startDate=request.getParameter("startDate") + " 00:00:00";
			}
			query.between("c_add_date", startDate, endDate);

            String flightStartDate = request.getParameter("flightStartDate");
            if (!StringUtils.isEmpty(flightStartDate)){
                String flightEndDate = request.getParameter("flightEndDate");
                flightStartDate=flightStartDate.trim()+" 00:00:00";
                if(StringUtils.isEmpty(flightEndDate)){
                    flightEndDate =flightStartDate;
                }
                flightEndDate=flightEndDate.trim()+" 23:59:59";
                query.between("Flight_date", flightStartDate, flightEndDate);
            }

            String orderSource=request.getParameter("orderSource");
			if(!StringUtils.isEmpty(orderSource)){
				if(!"0".equals(orderSource)){
					query.eq("order_source", orderSource);
				}
				String orderShop=request.getParameter("orderShop");
				if(!StringUtils.isEmpty(orderShop)){
					if(!"0".equals(orderShop)){
						query.eq("Order_shop", orderShop);
					}
				}
			}
			if(!StringUtils.isEmpty(request.getParameter("pnr"))){
				query.eq("pnr_txt", request.getParameter("pnr").trim());
			}
			if(!StringUtils.isEmpty(request.getParameter("orderNo"))){
				query.eq("order_no", request.getParameter("orderNo").trim());
			}
            String isCheck = request.getParameter("isChecked");
            if (!StringUtils.isEmpty(isCheck)) {
                if ("true".equals(isCheck)) {
                    String localName = ShiroKit.getLocalName();
                    if (!StringUtils.isEmpty(localName)) {
                        query.eq("process_by", localName);
                    }
                }
            }
			if(!StringUtils.isEmpty(request.getParameter("flight"))){
				query.like("flight_no", request.getParameter("flight").trim());
			}
			if(!StringUtils.isEmpty(request.getParameter("policyType"))){
				query.like("policy_type", request.getParameter("policyType").trim());
			}
			List<String> list=new ArrayList<String>();
			list.add(WebConstant.ORDER_NO_TICKET);
			list.add(WebConstant.ORDER_PRINT);
			list.add(WebConstant.APPLY_ORDER_CANCEL);
			if(!StringUtils.isEmpty(request.getParameter("orderStatus"))){
				if(!"0".equals(request.getParameter("orderStatus"))){
					query.eq("status", request.getParameter("orderStatus"));
				}else{
					query.in("status", list);
				}
			}else{
				query.in("status", list);
			}
			query.select("order_id","order_no","order_source","trip_Type","status","policy_type","create_date","total_price",
                        "Passenger_count","process_by","Flight_date","flight_no","pnr_txt as pnr","C_order_no","c_add_date","tic_type",
                        "last_print_ticket_time","av_status");
			query.orderByDesc("create_date");
            Long limit = Long.valueOf(request.getParameter("limit"));
            Page<Order> pageH=new Page<Order>(Long.valueOf(request.getParameter("page")), limit);
			IPage<Order> orderList = orderService.getOrderList2(query, pageH);
            long total = orderList.getTotal();
            result.setCount(total);
			result.setData(orderList.getRecords());
			//本地没有的话到对应平台查查看
			if(result.getCount()<1){
				if(!StringUtils.isEmpty(request.getParameter("orderNo"))&&!"0".equals(request.getParameter("orderSource"))){
					this.goToGetOrder(request);
					IPage<Order> orderList2 = orderService.getOrderList2(query, pageH);
					result.setCount(orderList2.getTotal());
					result.setData(orderList2.getRecords());
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	/**
	 * 平台查询订单
	 * @param
	 */
	private void goToGetOrder(HttpServletRequest request){
		String orderSource=request.getParameter("orderSource");
		String orderShop=request.getParameter("orderShop");
		String orderNo=request.getParameter("orderNo").trim();
		boolean exist = orderService.isExist(orderSource,orderNo);
		try {
			if(!exist){//本地没有没有则添加这个订单
				if(InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)){
					OrderVO orderVo = ttsOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
					if(orderVo!=null){
						orderService.saveOrderVO(orderVo);						
					}
				}else if(InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)){
					String user=DictUtils.getDictCode("order_import_cfgxc"+orderShop, "user");
					String passWord=DictUtils.getDictCode("order_import_cfgxc"+orderShop, "md5");
					CtripOrderVO ctripOrdcerVO = ctripOrderService.getOrderDetailBycOrderNo(orderNo, user, passWord);
					OrderVO orderVo = ctripOrderService.procCtripOrderToOrderVO(ctripOrdcerVO, orderSource, orderShop);
					if(orderVo!=null){
						orderService.saveOrderVO(orderVo);	
					}
				}else if(InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)){
					OrderVO orderVo = tcOrderService.getOrderByOrderNo2(orderNo, orderSource, orderShop);
					if(orderVo!=null){
						List<Flight> list = orderVo.getFlightList();
						if(list.size()==1){
							orderVo.getOrder().setTripType(WebConstant.FLIGHT_TYPE_ONEWAY);
						}else{
							orderVo.getOrder().setTripType(WebConstant.FLIGHT_TYPE_GOBACK);
						}
						orderService.saveOrderVO(orderVo);	
					}
				}else if(InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)){
					OrderVO orderByOrderNo = tbOrderService.getOrderByOrderNo(orderNo, orderSource, orderShop);
					if(orderByOrderNo!=null){
						orderService.saveOrderVO(orderByOrderNo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
