package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.exception.OrderToMuchException;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.Util;
import cn.ssq.ticket.system.util.WebConstant;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 采购单controller
 * 
 * @author Administrator
 *
 */
@RequestMapping("/purch")
@Controller
public class PurchController {

	private String PREFIX = "/modular/system/purch/";

	private Logger logg = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private ChangeService changeService;

	@Autowired
	private FlightService flightService;

	@Autowired
	private LogService logService;

	@RequestMapping("/toPurchIndex")
	public String toPurchIndex() {
		return PREFIX + "purch_index.html";
	}

	@RequestMapping("/toPurchList")
	public String toPurchList() {
		return PREFIX + "purch_list.html";
	}

	@RequestMapping("/toPurchAdd")
	public String toRefundAdd(String orderNo, HttpServletRequest request, Model model) {
		if (StringUtils.isEmpty(orderNo)) {
			return "/404.html";
		}
		return PREFIX + "purch_add.html";
	}

	/**
	 * 查列表
	 * 
	 * @param request
	 * @param page
	 * @param limit
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPurchList")
	public Object getpurchList(HttpServletRequest request, Long page, Long limit) {
		ResponseResult<List<Purchase>> result = new ResponseResult<List<Purchase>>();
		try {
			// String orderNo=request.getParameter("orderNo");
			String orderSource = request.getParameter("orderSource");
			orderSource = orderSource == null ? "0" : orderSource;
			String orderShop = request.getParameter("orderShop");
			orderShop = orderShop == null ? "0" : orderShop;

			QueryWrapper<Purchase> query = new QueryWrapper<Purchase>();
			query.ne("flag", 1);
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			startDate = StringUtils.isEmpty(startDate) ? Util.getToday()[0] : startDate.trim() + " 00:00:00";
			endDate = StringUtils.isEmpty(endDate) ? Util.getToday()[1] : endDate.trim() + " 23:59:59";
			query.between("c_add_date", startDate, endDate);

			String ischeck = request.getParameter("isCheck");
			if (!StringUtils.isEmpty(ischeck)) {
				if ("true".equals(ischeck)) {
					String localName = ShiroKit.getLocalName();
					if (!StringUtils.isEmpty(localName)) {
						query.eq("employee_name", localName);
					}
				}
			}
			String remark = request.getParameter("remark");
			if (!StringUtils.isEmpty(remark)) {
				query.like("remark", remark);
			}

			if (!"0".equals(orderSource)) {
				query.eq("order_source", orderSource);
				if (!"0".equals(orderShop)) {
					query.eq("order_shop", orderShop);
				}
			}
			query.orderByDesc("c_add_date");
			IPage<Purchase> data = purchaseService.getList(query, page, limit);
			List<Purchase> list = data.getRecords();
			if (list != null && list.size() > 0) {
				for (Purchase p : list) {
					p.setPayWay(DictUtils.getDictName("order_recipt_way", p.getPayWay()));
					p.setSupplier(DictUtils.getDictName("order_purch_place", p.getSupplier()));
					p.setType(DictUtils.getDictName("purchType", p.getType()));
				}
			}
			result.setData(list);
			result.setCount((int) data.getTotal());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg("查无数据");
		}
		return result;
	}

	/**
	 * 获取订单和其采购信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getPurch")
	@ResponseBody
	public Object getPurch(HttpServletRequest request, String orderNo) {
		ResponseResult<Map<String, Object>> result = new ResponseResult<Map<String, Object>>();

		String[] str = request.getParameter("orderSource").split("-");
		String orderSource = str[0];
		String orderShop = str[1];
		OrderVO orderVo = null;
		try {
			orderVo = orderService.selectOrderDetails(orderNo, orderSource, orderShop);
		} catch (OrderToMuchException e) {
			result.setCode(-1);
			result.setMsg("匹配到多个订单，请选择订单来源精确搜索！");
			return result;
		}
		if (orderVo == null) {
			result.setCode(-1);
			result.setMsg("没有查到订单信息！");
			return result;
		}
		/*
		 * if(orderVo==null){ //查平台 }
		 */
		List<Flight> flightList = flightService.selectFlightInfoByOrderId(orderVo.getOrderId().toString());
		List<Purchase> purchList = purchaseService.getPurchaseList(orderSource, orderShop, orderNo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderVo", orderVo);
		map.put("flightList", flightList);
		map.put("purchList", purchList);
		result.setData(map);
		return result;
	}

	/**
	 * 获取采购单
	 * 
	 * @param orderNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPruchListByOrderNo")
	public Object getPurchList(String orderNo, HttpServletRequest request) {
		ResponseResult<List<Purchase>> result = new ResponseResult<List<Purchase>>();
		String orderSource = "0";
		String orderShop = "0";
		if (!StringUtils.isEmpty(request.getParameter("orderSource"))) {
			String[] str = request.getParameter("orderSource").split("-");
			orderSource = str[0];
			orderShop = str[1];
		}
		try {
			List<Purchase> purchaseList = purchaseService.getPurchaseList(orderSource, orderShop, orderNo);
			result.setData(purchaseList);
		} catch (Exception e) {

		}
		return result;
	}

	/**
	 * 添加
	 * 
	 * @return
	 */
	@RequestMapping("/addPurch")
	@ResponseBody
	public Object addPurch(Purchase purch, String passengerId, HttpServletRequest request) {
		ResponseResult<Void> result = new ResponseResult<Void>();

		OrderOperateLog log = new OrderOperateLog();
		try {
			OrderVO orderVo = orderService.selectOrderDetails(purch.getOrderNo(), null, null);
			if (orderVo == null) {
				result.setMsg("没有这个订单，请检查订单号!");
				return result;
			}

			// 针对正常出票添加录入采购单规则
			result = purchaseRule(purch);
			if (result.getCode() == -1) {
				return result;
			}

			purch.setOrderShop(orderVo.getOrderShop());
			purch.setOrderSource(DictUtils.getDictCode("order_source", orderVo.getOrderSource()));
			purch.setcOrderNo(orderVo.getcOrderNo());
			purch.setcAddDate(orderVo.getcAddDate());
			purch.setFlightDate(orderVo.getFlightDate());
			purch.setFlag("0");
			if (!StringUtils.isEmpty(request.getParameter("hasChildren"))) {// 可能有兒童
				BigDecimal customerAmount = new BigDecimal(0);
				for (String name : purch.getPassengerNames().split(",")) {
					Passenger p = passengerService.getByQuery(purch.getOrderNo(), name);
					customerAmount = customerAmount.add(new BigDecimal(p.getActualPrice()));
				}
				purch.setCustomerAmount(customerAmount.doubleValue());
			} else {
				// 收款金额=总价/总人数*采购人数(不含兒童)
				int realPassengerSize = orderVo.getPassengetList().size();
				int passengerSize = purch.getPassengerNames().split(",").length;
				purch.setCustomerAmount(new BigDecimal(orderVo.getTotalPrice())
						.divide(new BigDecimal(realPassengerSize), 2, BigDecimal.ROUND_HALF_UP)
						.multiply(new BigDecimal(passengerSize)).doubleValue());
			}
			if (purch.getType().equals("1")) {// 差错报表
				if (purch.getReturnMoney() != null) {
					purch.setProfit(
							new BigDecimal(purch.getReturnMoney()).subtract(new BigDecimal(purch.getPayAmount()))
									.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				}
			} else {// 正常报表
				purch.setProfit(new BigDecimal(purch.getCustomerAmount()).subtract(new BigDecimal(purch.getPayAmount()))
						.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			Passenger p = passengerService.getById(passengerId);
			String username = ShiroKit.getLocalName();
			if (StringUtils.isEmpty(username)) {
				username = orderService.isHavePcocess(orderVo.getcOrderNo());
			}
			purch.setPrintTicketDate(new Date());
			if (p != null) {
				if (StringUtils.isEmpty(p.getPrintTicketBy())) {
					purch.setEmployeeName(username);
					Passenger nP = new Passenger();
					nP.setPassengerId(p.getPassengerId());
					nP.setPrintTicketBy(purch.getEmployeeName());
					nP.setPrintTicketDate(
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purch.getPrintTicketDate()));
					passengerService.updateById(nP);
				} else {
					purch.setEmployeeName(StringUtils.isEmpty(ShiroKit.getLocalName()) ? p.getPrintTicketBy()
							: ShiroKit.getLocalName());
					 purch.setPrintTicketDate(StringUtils.isEmpty(p.getPrintTicketDate()) ? new Date() : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(p.getPrintTicketDate()));
				}
			} else {
				purch.setEmployeeName(username);
//				purch.setPrintTicketDate(new Date());
			}

			int savePurch = purchaseService.savePurch(purch);
			log.setName(username);
			log.setOrderNo(purch.getOrderNo());
			log.setType("订单处理");
			if (savePurch != 0) {
				result.setMsg("保存成功");
				log.setContent("录入采购单成功");
			} else {
				logg.error("保存采购单失败,数据库连接异常");
				result.setMsg("保存失败!!!,再试一下");
				log.setContent("录入采购单失败！！");
			}
		} catch (Exception e) {
			logg.error("保存采购单失败", e);
			result.setMsg("保存失败!!!");
			log.setContent("录入采购单失败！！");
		}
		logService.saveLog(log);
		return result;
	}

	/**
	 * 添加
	 * 
	 * @return
	 */
	@RequestMapping("/addChangePurch")
	@ResponseBody
	public Object addChangePurch(Purchase purch, HttpServletRequest request) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		OrderOperateLog log = new OrderOperateLog();
		try {
			String orderNo = purch.getOrderNo();
			OrderVO orderVo = orderService.selectOrderDetails(orderNo, null, null);
			if (orderVo == null) {
				result.setMsg("没有这个订单，请检查订单号!");
				return result;
			}

			// 校验改签单录入规则
			String changeNo = request.getParameter("changeNo");
			result = changeRule(purch, changeNo);
			if (result.getCode() == -1) {
				return result;
			}
			// 改签票号去空处理
			String ticketNo = purch.getNewTicketNo().trim();

			purch.setNewTicketNo(ticketNo);
			purch.setOrderShop(orderVo.getOrderShop());
			purch.setOrderSource(DictUtils.getDictCode("order_source", orderVo.getOrderSource()));
			purch.setcOrderNo(orderVo.getcOrderNo());
			purch.setcAddDate(orderVo.getcAddDate());
			purch.setFlightDate(orderVo.getFlightDate());
			purch.setFlag("0");
			purch.setPrintTicketDate(new Date());

			String username = ShiroKit.getLocalName();
			if (StringUtils.isEmpty(username)) {
				username = changeService.isHavePcocess(orderVo.getcOrderNo());
			}
			purch.setEmployeeName(username);
			int savePurch = purchaseService.savePurch(purch);
			log.setName(username);
			log.setOrderNo(purch.getOrderNo());
			log.setType("订单处理");
			if (savePurch != 0) {
				result.setMsg("保存成功");

				String passengerName = purch.getPassengerNames();

				// 根据改签单号与改签乘客姓名查询
				QueryWrapper<Change> query = new QueryWrapper<>();
				query.eq("passenger_Name", passengerName);
				query.eq("NEW_C_ORDER_NO", changeNo);
				query.eq("order_no", orderNo);
				List<Change> nameList = changeService.selectByQuery(query);
				if (nameList.size() < 1) {
					result.setMsg("【" + passengerName + "】改签票号同步失败，请在改签信息栏下修改改签状态及票号！！");
					return result;
				}

				// 根据改签单号与改签乘客姓名同步改签数据
				Change change = new Change();
				change.setState(WebConstant.CHANGE_ALREADY);
				change.setTktNo(ticketNo);
				change.setNewCOrderNo(changeNo);
				change.setPassengerName(passengerName);
                int i = changeService.updateByChangeNo(change);
                log.setContent("录入改签采购单成功，并根据票号【"+ticketNo+"】,姓名【"+passengerName+"】修改票号、订单状态成功，修改数量："+i);

				// changeService.updateStatus(WebConstant.CHANGE_ALREADY,
				// purch.getOrderNo());
			} else {
				logg.error("保存采购单失败,数据库连接异常");
				result.setMsg("保存失败!!!,再试一下");
				log.setContent("录入改签采购单失败！！");
			}
		} catch (Exception e) {
			logg.error("保存采购单失败", e);
			result.setMsg("保存失败!!!");
			log.setContent("录入改签采购单失败！！");
		}
		logService.saveLog(log);
		return result;
	}

	@ResponseBody
	@RequestMapping("/getPurchCount")
	public Object getPurchCount(String orderNo) {
		ResponseResult<Integer> result = new ResponseResult<Integer>();
		try {
			int purchCountByOrderNo = purchaseService.getPurchCountByOrderNo(orderNo);
			result.setData(purchCountByOrderNo);
		} catch (Exception e) {
			result.setCode(-1);
		}
		return result;
	}

	/**
	 * 采购单作废
	 * 
	 * @param purchaseId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePurchaseFlag")
	public Object updatePurchaseFlag(Long purchaseId) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		try {
			purchaseService.updatePurchaseFlag(purchaseId);
			
			// 改签单状态、改签票号清空
			Purchase purchase = purchaseService.getById(purchaseId);
			if ("2".equals(purchase.getType()) && !StringUtils.isEmpty(purchase.getNewTicketNo())) {
				Change change = new Change();
				change.setOrderNo(purchase.getOrderNo());
				change.setTktNo(purchase.getNewTicketNo());
				change.setState("1");
				changeService.updateByticketNo(change);
			}
			
			result.setMsg("修改成功");
		} catch (Exception e) {
			result.setCode(-1);
		}
		return result;
	}

	/**
	 * 添加录入采购单规则 同订单下，同一个人 ，出票类型，只可以出现1次
	 * 
	 * @param purch
	 * @return
	 */
	public ResponseResult<Void> purchaseRule(Purchase purch) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		String orderNo = purch.getOrderNo();
		LambdaQueryWrapper<Purchase> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Purchase::getOrderNo, orderNo);
		// 针对正常出票类型
		wrapper.eq(Purchase::getFlag, "0");
		wrapper.eq(Purchase::getType, "0");
		List<Purchase> list = purchaseService.getByLambdaQueryWrapper(wrapper);
		if (list.size() < 1) {
			return result;
		}
		// 已录入的乘机人 + 采购单类型
		List<String> names = new ArrayList<>();
		list.stream().forEach(l -> {
			if (l.getPassengerNames().contains(",")) {
				List<String> asList = Arrays.asList(l.getPassengerNames().split(","));
				for (String item : asList) {
					names.add(item + l.getType());
				}
			} else {
				names.add(l.getPassengerNames() + l.getType());
			}

		});

		String type = purch.getType();// 采购单类型
		String passengerNames = purch.getPassengerNames();
		if (passengerNames.contains(",")) {
			List<String> asList = Arrays.asList(passengerNames.split(","));
			for (String item : asList) {
				if (names.contains(item + type)) {// 如果存在
					result.setCode(-1);
					result.setMsg("乘机人：【" + item + "】重复录入");
					break;
				}
			}
		} else {
			if (names.contains(passengerNames + type)) {
				result.setCode(-1);
				result.setMsg("乘机人：【" + passengerNames + "】重复录入");
			}
		}
		return result;
	}

	/**
	 * 录入改签单规则
	 * 
	 * @param purch
	 * @return
	 */
	public ResponseResult<Void> changeRule(Purchase purch, String changeNo) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		String passengerNames = purch.getPassengerNames();
		if (passengerNames.contains(",")) {
			result.setCode(-1);
			result.setMsg("改签单乘客需分开录入采购记录");
			return result;
		}
		String orderNo = purch.getOrderNo();
		String ticketNo = purch.getNewTicketNo();
		// 判断同一改签单号下改签票号是否重复录入
		LambdaQueryWrapper<Purchase> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Purchase::getFlag, "0");
		wrapper.eq(Purchase::getType, "2");
		wrapper.eq(Purchase::getOrderNo, orderNo);
		wrapper.eq(Purchase::getNewTicketNo, ticketNo);
		List<Purchase> list = purchaseService.getByLambdaQueryWrapper(wrapper);
		if (list.size() > 0) {
			List<String> ticketNos = new ArrayList<>();
			list.stream().filter(l -> l.getNewTicketNo().equals(ticketNo))
					.forEach(l -> ticketNos.add(l.getNewTicketNo()));

			if (ticketNos.size() > 0) {
				result.setCode(-1);
				result.setMsg("改签单新票号【" + ticketNo + "】已经录入，请勿重复录入");
			}
		}

		return result;
	}

}
