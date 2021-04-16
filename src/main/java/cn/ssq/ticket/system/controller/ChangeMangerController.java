package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.runnable.UpdateChangeFee;
import cn.ssq.ticket.system.service.ChangeService;
import cn.ssq.ticket.system.service.FlightService;
import cn.ssq.ticket.system.service.LogService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.Util;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/change")
public class ChangeMangerController {

	private String PREFIX = "/modular/system/change/";

	private Logger logg = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ChangeService changeService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private FlightService flightService;

	@Autowired
	private LogService logService;

	@RequestMapping("/toChangeIndex")
	public String orderIndex(){
		return PREFIX + "change_index.html";
	}

	/**
	 * 改签单添加页面
	 * @param orderId
	 * @return
	 */
	@RequestMapping("/toChangeAdd")
	public String toRefundAdd(String orderId,Model model){
		try {
			Order order = orderService.getOrderById(orderId);
			List<Flight> flightList = flightService.selectFlightInfoByOrderId(orderId);
			model.addAttribute("data", order);
			model.addAttribute("flightList", flightList);
			return PREFIX + "change_add.html";
		} catch (Exception e) {
			return "/404.html";
		}
	}

	/**
	 * 新增改签单
	 * @param
	 * @return
	 */
	@ResponseBody
	@PostMapping("/saveChanges")
	public Object saveRefunds(@RequestBody List<Change> changes){
		ResponseResult<Void> result=new ResponseResult<Void>();
		try {
			changeService.saveChangesByShouG(changes);
			result.setMsg("保存成功");
		} catch (Exception e) {
			logg.error("保存改签单失败",e);
			e.printStackTrace();
			result.setMsg("保存失败");
		}
		return result;
	}


	/**
	 * 改签单列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getChangeList")
	public ResponseResult<List<Change>> getChangeList(HttpServletRequest request){
		ResponseResult<List<Change>> result=new ResponseResult<List<Change>>();
		try {
			QueryWrapper<Change> query=this.createChangeQuery(request);
			Integer page=StringUtils.isEmpty(request.getParameter("page"))?0:Integer.parseInt(request.getParameter("page"));
			Integer limit=StringUtils.isEmpty(request.getParameter("limit"))?10:Integer.parseInt(request.getParameter("limit"));
			Page<Change> ipage=new Page<Change>(page, limit);
			IPage<Change> selectChangeList = changeService.selectChangeList(query,ipage);
			result.setData(selectChangeList.getRecords());
			result.setCount((int)selectChangeList.getTotal());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg("查无数据");
		}
		return result;
	}

	/**
	 * 改签查看页面
	 * @param orderNo
	 * @param model
	 * @param requext
	 * @return
	 */
	@RequestMapping("/toChangeView")
	public String orderView(String orderNo,Model model,HttpServletRequest requext){		
		if(StringUtils.isEmpty(orderNo)){
			return "/404.html";
		}
		try {
			OrderVO orderVo=orderService.selectOrderDetails(orderNo,null,null);
			List<Flight> flightList=flightService.selectFlightInfoByOrderId(orderVo.getOrderId().toString());
			orderVo.setFlightList(flightList);
			model.addAttribute("data", orderVo);
			model.addAttribute("newCOrderNo", requext.getParameter("newCOrderNo"));

			String process = changeService.isHavePcocess(requext.getParameter("newCOrderNo"));
			if(!StringUtils.isEmpty(process)){
				String username=ShiroKit.getSubject().getPrincipal().toString().trim();
				if(process.trim().equals(username)){
					QueryWrapper<Change> query=new QueryWrapper<Change>();
					query.eq("NEW_C_ORDER_NO", requext.getParameter("newCOrderNo"));
					List<Change> changeList = changeService.selectByQuery(query);
					model.addAttribute("changeList", changeList);
					return PREFIX + "change_edit.html";
				}
			}
		} catch (Exception e) {

		}
		OrderOperateLog log=new OrderOperateLog();
		log.setChangeNo(requext.getParameter("newCOrderNo"));
		log.setContent("点击了订单详情页");
		log.setType("订单详情");
		log.setName(ShiroKit.getLocalName());
		logService.saveLog(log);
		return PREFIX + "change_view.html";
	}

	/**
	 * 改签编辑页面
	 * @param orderNo
	 * @param model
	 * @param requext
	 * @return
	 */
	@RequestMapping("/toChangeEdit")
	public String toChangeEdit(String orderNo,Model model,HttpServletRequest requext){		
		if(StringUtils.isEmpty(orderNo)){
			return "/404.html";
		}
		try {
			OrderVO orderVo=orderService.selectOrderDetails(orderNo,null,null);
			List<Flight> flightList=flightService.selectFlightInfoByOrderId(orderVo.getOrderId().toString());
			orderVo.setFlightList(flightList);
			model.addAttribute("data", orderVo);
			model.addAttribute("newCOrderNo", requext.getParameter("newCOrderNo"));

			QueryWrapper<Change> query=new QueryWrapper<Change>();
			query.eq("NEW_C_ORDER_NO", requext.getParameter("newCOrderNo"));
			List<Change> changeList = changeService.selectByQuery(query);
			model.addAttribute("changeList", changeList);
		} catch (Exception e) {
			e.printStackTrace();
			return "/404.html";
		}
		return PREFIX + "change_edit.html";
	}

	@ResponseBody
	@RequestMapping("/isHavePcocess")
	public synchronized Object isHavePcocess(String changeNo){
		ResponseResult<Boolean> result=new ResponseResult<Boolean>();
		try {
			if(StringUtils.isEmpty(changeNo)){
				result.setData(false);
				result.setMsg("处理失败！");
				return result;
			}
			String pcocess = changeService.isHavePcocess(changeNo);
			if(!StringUtils.isEmpty(pcocess)){
				result.setMsg("该订单已被"+pcocess+"锁定!");
				result.setData(false);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();		
			logg.error("点击处理退票异常"+changeNo,e);
		}
		try {
			String username=ShiroKit.getSubject().getPrincipal().toString();
			changeService.addProcess(username, changeNo);//加锁定人
		} catch (Exception e) {
			result.setData(false);
			result.setMsg("Token过期，先刷新当前页面！");
			return result;
		}
		result.setData(true);
		OrderOperateLog log=new OrderOperateLog();
		log.setChangeNo(changeNo);
		log.setContent("锁单");
		log.setType("订单处理");
		log.setName(ShiroKit.getLocalName());
		logService.saveLog(log);
		return result;
	}


	/**
	 * 解锁
	 * @param
	 * @return
	 */
	@ResponseBody
	@PostMapping("/deleteProcess")
	public Object deleteProcess(String changeNo){
		ResponseResult<Void> result=new ResponseResult<Void>();
		try {
			String process=changeService.isHavePcocess(changeNo);
			if(!StringUtils.isEmpty(process)){
				String username=ShiroKit.getSubject().getPrincipal().toString().trim();
				if(!process.trim().equals(username)){
					if(!ShiroKit.hasPermission("/unlock")){
						result.setCode(-1);
						result.setMsg("锁定人不符!");
						return result;
					}

				}
			}
			changeService.deleteProcess(changeNo);
			result.setCode(0);
			OrderOperateLog log=new OrderOperateLog();
			log.setChangeNo(changeNo);
			log.setContent("解锁");
			log.setType("订单处理");
			log.setName(ShiroKit.getLocalName());
			logService.saveLog(log);

		} catch (Exception e) {
			result.setCode(-1);
			result.setMsg("token过期，先刷新当前页面");
			e.printStackTrace();
		}
		return result;
	}




	/**
	 * 根据改签单号获取改签信息
	 * @param newCOrderNo
	 * @return
	 */
	@RequestMapping("/getChangeInfo")
	@ResponseBody
	public Object getChangeInfo(String newCOrderNo){
		ResponseResult<List<Change>> result=new ResponseResult<List<Change>>();
		try {
			QueryWrapper<Change> query=new QueryWrapper<Change>();
			query.eq("NEW_C_ORDER_NO", newCOrderNo);
			List<Change> changeList = changeService.selectByQuery(query);
			for(Change change:changeList){
				change.setState(DictUtils.getDictName("change_status", change.getState()));
			}
			result.setData(changeList);
		} catch (Exception e) {
			result.setCode(-1);
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 修改改签单
	 * @param changeList
	 * @return
	 */
	@ResponseBody
	@PostMapping("/updateChanges")
	@Transactional
	public Object updateChanges(@RequestBody List<Change> changeList){
		ResponseResult<Void> result=new ResponseResult<Void>();
		try {
			for(Change change:changeList){
				if(StringUtils.isEmpty(change.getRevenuePrice())){
					change.setRevenuePrice(null);
				}
				if(StringUtils.isEmpty(change.getPayPrice())){
					change.setPayPrice(null);
				}
				changeService.updateById(change);
			}
			Change change = changeList.get(0);
			String revenuePrice = change.getRevenuePrice();
			if(change.getState().equals("2")) {
				// 状态修改为改签完成后，在调用一次平台的接口，修改改签费用
				new Thread(new UpdateChangeFee(changeList)).start();
			}

			result.setMsg("保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("保存失败");
		}
		return result;
	}

	/**
	 * 删除改签单
	 * @param
	 * @return
	 */
	@ResponseBody
	@PostMapping("/deleteChange")
	public Object deleteChange(String[] changeIds){
		ResponseResult<Void> result=new ResponseResult<Void>();
		try {
			changeService.deleteChanges(changeIds);
			result.setMsg("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("删除失败");
		}
		return result;
	}

	/**
	 * 查询条件
	 * @param request
	 * @return
	 */
	private QueryWrapper<Change> createChangeQuery(HttpServletRequest request){
		QueryWrapper<Change> query=new QueryWrapper<Change>();
		//默认查询最近一月
		Calendar calendar=Calendar.getInstance();
		String defaultEndDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
		calendar.add(Calendar.MONTH, -1);
		String defaultStartDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
		String changeDateStart=request.getParameter("changeDateStart");
		String changeDateEnd=request.getParameter("changeDateEnd");
		query.between("change_date", StringUtils.isEmpty(changeDateStart)?defaultStartDate.trim()+" 00:00:00":changeDateStart.trim()+" 00:00:00",
				StringUtils.isEmpty(changeDateEnd)?defaultEndDate.trim()+" 23:59:59":changeDateEnd.trim()+" 23:59:59");

		if(!StringUtils.isEmpty(request.getParameter("flightStartDate"))){
			String flightStartDate=request.getParameter("flightStartDate");
			String flightEndDate=request.getParameter("flightEndDate")==null?Util.getToday()[1]:request.getParameter("flightEndDate");
			query.between("Flight_date", flightStartDate.trim()+" 00:00:00", flightEndDate.trim()+" 23:59:59");
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
		if(!StringUtils.isEmpty(request.getParameter("newCOrderNo"))){
			query.eq("NEW_C_ORDER_NO", request.getParameter("newCOrderNo"));
		}
		if(!StringUtils.isEmpty(request.getParameter("orderNo"))){
			query.eq("order_no", request.getParameter("orderNo"));
		}
		if(!StringUtils.isEmpty(request.getParameter("flightNo"))){
			query.eq("FLIGHT_NO", request.getParameter("flightNo"));
		}
		if(!StringUtils.isEmpty(request.getParameter("passengerName"))){
			query.eq("passenger_Name", request.getParameter("passengerName"));
		}
		if(!StringUtils.isEmpty(request.getParameter("state"))){
			if(-1!=Integer.valueOf(request.getParameter("state"))){
				query.eq("STATE", request.getParameter("state"));
			}
		}
		if(!StringUtils.isEmpty(request.getParameter("ticketNo"))){
			query.eq("TKT_NO", request.getParameter("ticketNo"));
		}
		query.orderByDesc("change_date");
		return query;
	}




}