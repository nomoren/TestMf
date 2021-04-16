package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.importBean.CtripBean.CtripOrderVO;
import cn.ssq.ticket.system.exception.OrderToMuchException;
import cn.ssq.ticket.system.queryEntity.OrderQuery;
import cn.ssq.ticket.system.runnable.CtripStatusMove;
import cn.ssq.ticket.system.runnable.RedisRunnable;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.service.OrderImport.impl.*;
import cn.ssq.ticket.system.util.*;
import cn.stylefeng.guns.core.common.page.LayuiPageInfo;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单管理控制层
 */
@RequestMapping("/order")
@Controller
public class OrderManageController extends BaseController {

	private String PREFIX = "/modular/system/order/";

	private static OrderLcok orderLcok = new OrderLcok();

	private Logger logg = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OrderService orderService;

	@Autowired
	private FlightService fligthService;

	@Autowired
	private LogService logService;

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private TTsOrderService ttsOrderService;

	@Autowired
	private CtripOrderService ctripOrderService;

	@Autowired
	private TcOrderService tcOrderService;

	@Autowired
	private TbOrderService tbOrderService;

	@Autowired
	private JiuOrderService jiuOrderServer;

	@Autowired
	private PurchaseService purchaseService;

	// 订单列表页
	@RequestMapping("/toOrderIndex")
	public String orderIndex() {
		return PREFIX + "order_index.html";
	}

	//添加页
	@RequestMapping("/toOrderAdd")
	public String toOrderAdd() {
		return PREFIX + "order_add.html";
	}

	// 订单查看页
	@RequestMapping("/toOrderView/{orderNo}")
	public String orderView(@PathVariable("orderNo") String orderNo, Model model) {
		if (StringUtils.isEmpty(orderNo)) {
			return "/404.html";
		}
		try {
			OrderVO orderVo = orderService.selectOrderDetails(orderNo, null, null);



			if (orderVo == null) {
				return "/404.html";
			}
			List<Flight> flightList = fligthService.selectFlightInfoByOrderId(orderVo.getOrderId().toString());
			if (flightList.size() == 0) {
				Flight f = new Flight();
				flightList.add(f);
			}
			orderVo.setFlightList(flightList);
			model.addAttribute("data", orderVo);
			String process = orderService.isHavePcocess(orderNo);
			if (!StringUtils.isEmpty(process)) {
				String username = ShiroKit.getSubject().getPrincipal().toString().trim();
				if (process.trim().equals(username)) {
				    //是锁单人，直接到编辑页面
					return PREFIX + "order_edit.html";
				}
			}
		} catch (OrderToMuchException e) {
			return "/404.html";
		}
		OrderOperateLog log = new OrderOperateLog();
		log.setOrderNo(orderNo);
		log.setContent("点击了订单详情页");
		log.setType("订单详情");
		log.setName(ShiroKit.getLocalName());
		logService.saveLog(log);
		return PREFIX + "order_view.html";
	}

	// 订单处理页
	@RequestMapping("/toOrderEdit/{orderNo}")
	public String orderEdit(@PathVariable("orderNo") String orderNo, Model model) {
		if (StringUtils.isEmpty(orderNo)) {
			return "/404.html";
		}
		try {
			OrderVO orderVO = orderService.selectOrderDetails(orderNo, null, null);
            // 就旅行 订单来源 特殊处理
            if (orderVO.getOrderSource().contains("就旅行")) {
                // 就旅行（agent……/去哪儿/蜗牛）  ||  A去哪儿/蜗牛 B携程，D智行/马蜂窝
                StringBuffer str = new StringBuffer();
                str.append(DictUtils.getDictName("order_source", orderVO.getOrderSource()));
                if (org.apache.commons.lang3.StringUtils.isNoneBlank(orderVO.getWebsiteOrderSource())) {
                    str.append(" （");
                    String first = orderVO.getWebsiteOrderSource().substring(0,1);
                    if ("a".equals(first) || "A".equals(first)) {
                        str.append("/去哪儿/蜗牛");
                    } else if ("b".equals(first) || "B".equals(first)) {
                        str.append("/携程");
                    } else if ("d".equals(first) || "D".equals(first)) {
                        str.append("/智行/马蜂窝");
                    }
                    str.append("）");
                }
                orderVO.setOrderSource(str.toString());
            }

			if (orderVO == null) {
				return "/404.html";
			}
			List<Flight> flightList = fligthService.selectFlightInfoByOrderId(orderVO.getOrderId().toString());
			if (flightList.size() == 0) {
				Flight f = new Flight();
				flightList.add(f);
			}
            orderVO.setFlightList(flightList);
			model.addAttribute("data", orderVO);
		}  catch (Exception e) {
			return "/404.html";
		}
		return PREFIX + "order_edit.html";
	}

	/**
	 * 获取订单列表
	 * 
	 * @param requext
	 * @return
	 */
	@PostMapping("/getOrder")
	@ResponseBody
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getOrderByPage(HttpServletRequest requext) {
		LayuiPageInfo result = new LayuiPageInfo();
		try {
			OrderQuery query = this.createQueryData(requext);
			Object object = orderService.selectOrderList(query);
			Map<String, Object> map = (Map<String, Object>) object;
			result.setCount((int) map.get("count"));
			result.setData((List) map.get("list"));

			// 本地没有的话到对应平台查查看
			if (result.getCount() < 1) {
				if (!StringUtils.isEmpty(query.getOrderNo()) && !"0".equals(query.getOrderSource())) {
					this.goToGetOrder(query);
					Object object2 = orderService.selectOrderList(query);
					Map<String, Object> map2 = (Map<String, Object>) object2;
					result.setCount((int) map2.get("count"));
					result.setData((List) map2.get("list"));
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 票号验证
	 * 
	 * @param list
	 *            乘客及其票号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/verifyTicketNo")
	public Object verifyTicketNo(@RequestBody List<Passenger> list) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		String orderNo = list.get(0).getOrderNo();
		Order order = orderService.getOrderByOrderNo(orderNo);

		// 校验采购单相关问题
		result = verifyPurchase(list, order);
		if (result.getCode() == -1) {
			return result;
		}
		
		String orderSource = order.getOrderSource();
		if (InterfaceConstant.ORDER_SOURCE_TN.equals(orderSource)) {// 途牛
			result = verifyTuNiuTicketNo(list, order);
		} else if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)) {// 去哪儿
			result = verifyTTsTicketNo(list, order);
		} else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)) {// 携程
			result = verifyCtripTicketNo(list, order);
		} else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)) {// 同程
			result = verifyTcTicketNo(list, order);
		} else if (InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)) {// 淘宝
			result = verifyTbTicketNo(list, order);
		} else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource)) {// 就旅行
			result = verifyJiuTicketNo(list, order);
		} else {
			result.setCode(-2);
			result.setMsg("出票失败，请联系系统管理员！");
		}
		// String isDeletePnr = list.get(0).getStatus();
		//String policyType = order.getPolicyType();

        //出票完成，行程key自减1
        if (result.getCode() == 0) {
            String key=order.getFlightNo()+"-"+order.getFlightDate();
            ThreadPoolUtil.execute(new RedisRunnable(key));
        }

		// 是否删除编码
	/*	if (!StringUtils.isEmpty(policyType)) {
            if (result.getCode() == 0) {
                if(policyType.contains("FFMUGW")){
                    //ThreadPoolUtil.execute(new DeletePnr(order.getPnr(), orderNo,null));
                }else if(policyType.contains("FCQN")){
                    String userName = ShiroKit.getLocalName();
                    if (StringUtils.isEmpty(userName)){
                        userName=order.getProcessBy();
                    }
                    ThreadPoolUtil.execute(new UpdatePurchase(orderNo,userName));
                }
            }
		}*/
		return result;
	}

	/**
	 * 出票采购单校验
	 * 
	 * @param
	 * @param order
	 * @return
	 */
	public ResponseResult<Void> verifyPurchase(List<Passenger> list, Order order) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		String policyType = order.getPolicyType();// 政策代码
		String orderNo = list.get(0).getOrderNo();// 订单id

		// 政策代码包含GZHCX的订单是可以不用录入采购单信息
		if (policyType.contains("CX") || policyType.contains("123ZHTC")) {
			return result;
		}
		
		// 根据订单号查询采购信息
		LambdaQueryWrapper<Purchase> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Purchase::getOrderNo, orderNo);
		wrapper.eq(Purchase::getType, "0");
		wrapper.eq(Purchase::getFlag, "0");
		List<Purchase> purchaseList = purchaseService.getByLambdaQueryWrapper(wrapper);
		
		// 判断是否录入采购单信息
		if (purchaseList.size() < 1) {
			result.setCode(-1);
			result.setMsg("采购单未录入，请先录入采购单！！");
			return result;
		}
		
		// 姓名集合
		List<String> ticketNum = new ArrayList<>();
		list.stream().filter(p -> !"".equals(p.getTicketNo())).forEach(p -> ticketNum.add(p.getName()));
		// 录入的乘机人集合
		List<String> passenger = new ArrayList<>(); 
		purchaseList.stream().forEach(p -> {
			if (!StringUtils.isEmpty(p.getPassengerNames())) {
				String passengerNames = p.getPassengerNames();
				if (passengerNames.contains(",")) {
					Arrays.asList(passengerNames.split(",")).stream().forEach(n -> passenger.add(n));
				} else {
					passenger.add(passengerNames);
				}
				return;
			}
		});
		// 判断是录入的票号数量与录入的乘机人数量是否一致
//			if (passenger.size() != ticketNum.size()) {
//				result.setCode(-1);
//				result.setMsg("录入的票号数量与录入的乘机人数量不一致");
//				return result;
//			}
		
		// 判断票号对应的乘机人是否录入
		for (String name : ticketNum) {
			if (!passenger.contains(name)) {
				result.setCode(-1);
				result.setMsg("乘机人：【" + name + "】没有录入采购单！！");
				break;
			}
		}
			
		return result;
	}

	/**
	 * 同程票号验证
	 * 
	 * @param list
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult<Void> verifyTcTicketNo(List<Passenger> list, Order order) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		OrderOperateLog log = new OrderOperateLog();
		log.setName(ShiroKit.getLocalName());
		try {
			String orderNo = list.get(0).getOrderNo();
			String username = ShiroKit.getLocalName();
			if (StringUtils.isEmpty(username)) {
				username = orderService.isHavePcocess(orderNo);
			}
			log.setOrderNo(orderNo);
			log.setType("订单处理");
			String resultData = tcOrderService
					.verifyTicketNo(list)/* .replaceAll("\\\\", "") */;

			JSONObject json = JSONObject.fromObject(resultData);
			if (json.getBoolean("isSuccess")) {
				orderService.updateStatus(WebConstant.ORDER_PRINT_TICKET, orderNo);
				for (Passenger passenger : list) {
					passenger.setTicketStatus(WebConstant.OK_TICKET);
					passenger.setStatus(WebConstant.OK_TICKET);
					passenger.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					passenger.setPrintTicketCabin(passenger.getCabin());
					passenger.setPrintTicketBy(username);
					passengerService.updateTicketStatus(passenger);
				}
				result.setMsg("出票成功!");
				log.setContent(result.getMsg());
			} else {
				if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
					for (Passenger passenger : list) {
						passenger.setTicketStatus(WebConstant.ERROR_TICKET);
						passenger.setStatus(WebConstant.ERROR_TICKET);
						passenger.setPrintTicketBy(username);
						passenger.setPrintTicketCabin(passenger.getCabin());
						passengerService.updateTicketStatus(passenger);
					}
				}
				result.setCode(-1);
				result.setMsg(
						StringUtils.isEmpty(json.getString("errorMessage")) ? "验证失败!" : json.getString("errorMessage"));
				log.setContent(result.getMsg());
				logg.info("=============同程票号验证返回=============");
				logg.info(resultData);
				logg.info("=============同程票号验证返回=============");
			}
		} catch (Exception e) {
			logg.error("TC出票异常。。", e);
			result.setCode(-2);
			result.setMsg("出票失败！请联系系统管理员");
			log.setContent(result.getMsg());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		logService.saveLog(log);
		return result;
	}

	/**
	 * 携程验证票号
	 * 
	 * @param list
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult<Void> verifyCtripTicketNo(List<Passenger> list, Order order) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		OrderOperateLog log = new OrderOperateLog();
		try {
			String orderNo = list.get(0).getOrderNo();
			// OrderVO orderVo=orderService.selectOrderDetails(orderNo,
			// InterfaceConstant.ORDER_SOURCE_CTRIP, null);
			String username = ShiroKit.getLocalName();
			if (StringUtils.isEmpty(username)) {
				username = orderService.isHavePcocess(orderNo);
			}
			log.setOrderNo(orderNo);
			log.setType("订单处理");
			log.setName(username);
			String resultData = ctripOrderService.verifyTicketNo(list);
			Document doc = DocumentHelper.parseText(resultData);
			Element rootElement = doc.getRootElement();
			Element element = rootElement.element("OpenModifyOrderResponse");
			Integer code = Integer.valueOf(element.elementText("ResultCode"));
			if (code == 0) {
				String orderShop = passengerService.getById(list.get(0).getPassengerId().toString()).getOrderShop();
				Thread.sleep(3000);
				String orderStatus = ctripOrderService.getOrderStatus(orderNo, orderShop).split(":")[0];
				if ("3".equals(orderStatus)) {
					orderService.updateStatus("3", orderNo);
					for (Passenger passenger : list) {
						passenger.setTicketStatus(WebConstant.OK_TICKET);
						passenger.setStatus(WebConstant.OK_TICKET);
						passenger.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
						passenger.setPrintTicketCabin(passenger.getCabin());
						passenger.setPrintTicketBy(username);
						passengerService.updateTicketStatus(passenger);
					}
					result.setMsg("出票成功!");
					log.setContent(result.getMsg());
				} else {
					for (int i = 0; i <= 30; i++) {
						Thread.sleep(2000);
						orderStatus = ctripOrderService.getOrderStatus(orderNo, orderShop).split(":")[0];
						if(!StringUtils.isEmpty(orderStatus)){
							if (!"8".equals(orderStatus)) {// 非验证中
								break;
							}
						}
					}
					if ("1".equals(orderStatus) || "2".equals(orderStatus)) {// 验证失败
						result.setCode(-1);
						result.setMsg("票号验证失败");
						log.setContent(result.getMsg());
					} else {
						orderService.updateStatus("3", orderNo);
						for (Passenger passenger : list) {
							passenger.setTicketStatus(WebConstant.OK_TICKET);
							passenger.setStatus(WebConstant.OK_TICKET);
							passenger.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
							passenger.setPrintTicketCabin(passenger.getCabin());
							passenger.setPrintTicketBy(username);
							passengerService.updateTicketStatus(passenger);
						}
						result.setMsg("出票成功!");
						log.setContent(result.getMsg());
					}
					if ("8".equals(orderStatus)) {
						CtripStatusMove move = new CtripStatusMove(order, list, log.getName(), orderService,
								ctripOrderService, passengerService);
						move.start();
					}
				}

			} else {
				if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
					for (Passenger passenger : list) {
						passenger.setTicketStatus(WebConstant.ERROR_TICKET);
						passenger.setStatus(WebConstant.ERROR_TICKET);
						passenger.setPrintTicketBy(username);
						passenger.setPrintTicketCabin(passenger.getCabin());
						passengerService.updateTicketStatus(passenger);
					}
				}
				result.setCode(-1);
				result.setMsg(element.elementText("Message"));
				log.setContent(result.getMsg());
				logg.info("=======ctrip票号验证结果========");
				logg.info(resultData);
				logg.info("=======ctrip票号验证结果========");
			}
		} catch (Exception e) {
			logg.error("ctrip出票异常。。", e);
			result.setCode(-2);
			result.setMsg("出票失败！请联系系统管理员");
			log.setContent(result.getMsg());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		logService.saveLog(log);
		return result;
	}

	/**
	 * 途牛验证票号
	 * 
	 * @param list
	 *            要验证的订单所有乘客及其票号
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult<Void> verifyTuNiuTicketNo(List<Passenger> list, Order orderByOrderNo) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		OrderOperateLog log = new OrderOperateLog();
		try {
			String orderNo = orderByOrderNo.getcOrderNo();// 途牛票号验证需出票单号
			String username = ShiroKit.getLocalName();
			if (StringUtils.isEmpty(username)) {
				username = orderService.isHavePcocess(orderByOrderNo.getOrderNo());
			}
			log.setName(username);
			log.setOrderNo(orderByOrderNo.getOrderNo());
			log.setType("订单处理");
			String returnData = TuniuOrderService.verifyTicketNo(list, orderByOrderNo.getOrderShop(), orderNo);
			JSONObject json = JSONObject.fromObject(returnData);
			if (json.getBoolean("success")) {
				if (json.getBoolean("data")) {
					Thread.sleep(3000);// 平台验证需要几秒时间,2-3秒验证完毕
					Integer orderStatus = TuniuOrderService.checkOrderStatus(orderNo.trim(),
							orderByOrderNo.getOrderShop());
					if (15 == orderStatus) {// 如果状态还在处理中，1分钟内重复获取状态
						for (int i = 0; i <= 60; i++) {
							logg.info("状态还在处理中，1分钟内重复获取状态");
							Thread.sleep(1000);
							orderStatus = TuniuOrderService.checkOrderStatus(orderNo.trim(),
									orderByOrderNo.getOrderShop());
							if (orderStatus != 15) {
								break;
							}
						}
					}
					if (4 == orderStatus || 15 == orderStatus) {
						orderService.updateStatus("3", orderByOrderNo.getOrderNo());
						for (Passenger passenger : list) {
							passenger.setTicketStatus(WebConstant.OK_TICKET);
							passenger.setStatus(WebConstant.OK_TICKET);
							passenger.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
							passenger.setPrintTicketCabin(passenger.getCabin());
							passenger.setPrintTicketBy(username);
							passengerService.updateTicketStatus(passenger);
						}
						result.setMsg("出票成功!");
						log.setContent(result.getMsg());
					} else {
						if (!WebConstant.ORDER_PRINT_TICKET.equals(orderByOrderNo.getStatus())) {
							// orderService.updateStatus("5",
							// orderByOrderNo.getOrderNo());
							for (Passenger passenger : list) {
								passenger.setTicketStatus(WebConstant.ERROR_TICKET);
								passenger.setStatus(WebConstant.ERROR_TICKET);
								passenger.setPrintTicketBy(username);
								passenger.setPrintTicketCabin(passenger.getCabin());
								passengerService.updateTicketStatus(passenger);
							}
						}
						result.setCode(-1);
						result.setMsg(StringUtils.isEmpty(json.getString("errorMsg")) ? "票号验证失败！"
								: json.getString("errorMsg"));
						log.setContent(result.getMsg());
						logg.info("=================TuNiu票号验证返回结果===================");
						logg.info(returnData);
						logg.info("=====================================================");
					}
					if (15 == orderStatus) {
						CheckOrderStatus check = new CheckOrderStatus(orderByOrderNo, list);
						Thread thread = new Thread(check);
						thread.start();
					}

				} else {
					if (!WebConstant.ORDER_PRINT_TICKET.equals(orderByOrderNo.getStatus())) {
						// orderService.updateStatus("5",
						// orderByOrderNo.getOrderNo());
						for (Passenger passenger : list) {
							passenger.setTicketStatus("2");
							passenger.setPrintTicketBy(username);
							passengerService.updateTicketStatus(passenger);
						}
					}
					result.setCode(-1);
					result.setMsg(
							StringUtils.isEmpty(json.getString("errorMsg")) ? "票号验证失败！" : json.getString("errorMsg"));
					log.setContent(result.getMsg());
					logg.info("=================TuNiu票号验证返回结果===================");
					logg.info(returnData);
					logg.info("=====================================================");
				}
			} else {
				result.setCode(-2);
				result.setMsg("验证失败," + json.getString("errorMsg"));
				log.setContent(result.getMsg());
				logg.info("=================TuNiu票号验证返回结果===================");
				logg.info(returnData);
				logg.info("=====================================================");
			}
		} catch (Exception e) {
			logg.error("TuNiu出票异常。。", e);
			result.setCode(-2);
			result.setMsg("出票失败，请联系系统管理员!");
			log.setContent("出票失败");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		logService.saveLog(log);
		return result;
	}

	/**
	 * 淘宝票号验证
	 * 
	 * @param list
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult<Void> verifyTbTicketNo(List<Passenger> list, Order order) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		OrderOperateLog log = new OrderOperateLog();
		try {
			// {"jipiao_agent_order_ticket_response":{"is_order_success":true,"is_success":true,"is_ticket_success":true,"request_id":"5zmgutslnmxe"}}

			String orderNo = list.get(0).getOrderNo();
			String username = ShiroKit.getLocalName();
			if (StringUtils.isEmpty(username)) {
				username = orderService.isHavePcocess(orderNo);
			}
			log.setName(username);
			log.setOrderNo(orderNo);
			log.setType("订单处理");
			String resultData = tbOrderService.verifyTicketNo(list);
			if(StringUtils.isEmpty(resultData)){
                result.setCode(-2);
                result.setMsg("出票失败！");
                log.setContent(result.getMsg());
                return result;
            }
			JSONObject json = JSONObject.fromObject(resultData);
			JSONObject jsonObject = json.getJSONObject("jipiao_agent_order_ticket_response");
			if(jsonObject==null){
                result.setCode(-2);
                result.setMsg("出票失败！");
                log.setContent(result.getMsg());
                return result;
            }
			if (jsonObject.getBoolean("is_ticket_success")) {
				orderService.updateStatus(WebConstant.ORDER_PRINT_TICKET, orderNo);
				for (Passenger passenger : list) {
					passenger.setTicketStatus(WebConstant.OK_TICKET);
					passenger.setStatus(WebConstant.OK_TICKET);
					passenger.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					passenger.setPrintTicketCabin(passenger.getCabin());
					passenger.setPrintTicketBy(username);
					passengerService.updateTicketStatus(passenger);
				}
				result.setMsg("出票成功!");
				log.setContent(result.getMsg());
			} else {
				if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
					for (Passenger passenger : list) {
						passenger.setTicketStatus(WebConstant.ERROR_TICKET);
						passenger.setStatus(WebConstant.ERROR_TICKET);
						passenger.setPrintTicketBy(username);
						passenger.setPrintTicketCabin(passenger.getCabin());
						passengerService.updateTicketStatus(passenger);
					}
				}
				result.setCode(-1);
				result.setMsg("票号验证失败!");
				log.setContent(result.getMsg());
				logg.info("=============TB票号验证返回=============");
				logg.info(resultData);
				logg.info("=============TB票号验证返回=============");
			}
		} catch (Exception e) {
			logg.error("TB出票异常。。", e);
			result.setCode(-2);
			result.setMsg("出票失败！请联系系统管理员");
			log.setContent(result.getMsg());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		logService.saveLog(log);
		return result;
	}

	/**
	 * tts验证票号，支持多人订单时单张回填
	 * 
	 * @param
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult<Void> verifyTTsTicketNo(List<Passenger> orgList, Order order) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		OrderOperateLog log = new OrderOperateLog();
		if (WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
			result.setMsg("该订单已出票！");
			return result;
		}
		/*
		 * if(WebConstant.ORDER_NOTICK_REFUND.equals(order.getStatus())){
		 * result.setMsg("该订单已申请退款！"); return result; }
		 */
		try {
			List<Passenger> list = new ArrayList<Passenger>();
			for (Passenger p : orgList) {
				if (!StringUtils.isEmpty(p.getTicketNo())) {
					list.add(p);
				}
			}
			if (list.size() == 0) {
				result.setCode(-2);
				result.setMsg("请粘贴票号！");
				return result;
			}
			int allPS = Integer.parseInt(order.getPassengerCount());
			String orderNo = list.get(0).getOrderNo();
			OrderVO orderVo = orderService.selectOrderDetails(orderNo, "02", null);
			String username = ShiroKit.getLocalName();
			if (StringUtils.isEmpty(username)) {
				username = orderService.isHavePcocess(orderNo);
			}
			log.setOrderNo(orderVo.getOrderNo());
			log.setType("订单处理");
			log.setName(username);
			orderVo.setPassengetList(list);
			String resultData = ttsOrderService.verifyTicketNo(orderVo);
			if (resultData == null) {
				result.setCode(-2);
				result.setMsg("出票失败！");
				log.setContent(result.getMsg());
				ttsOrderService.locked(orderNo, orderNo.substring(0, 3), true);
				return result;
			}
			Document doc = DocumentHelper.parseText(resultData);
			Element rootElement = doc.getRootElement();
			Element element = rootElement.element("OrderDetail");
			String value = element.attributeValue("errorCode");
			if (StringUtils.isEmpty(value)) {
				for (Passenger passenger : list) {
					passenger.setTicketStatus(WebConstant.OK_TICKET);
					passenger.setStatus(WebConstant.OK_TICKET);
					passenger.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					passenger.setPrintTicketCabin(passenger.getCabin());
					passenger.setPrintTicketBy(username);
					passengerService.updateTicketStatus(passenger);
				}

				if (allPS == list.size()) {
					orderService.updateStatus("3", orderVo.getOrderNo());
				}
				result.setMsg("票号验证通过");
				log.setContent(result.getMsg());
			} else {
				// 一张张贴的，整体结果返回都是校验失败
				if (allPS != list.size()) {
					@SuppressWarnings("unchecked")
					List<Element> elements = element.elements("passenger");
					StringBuilder sb = new StringBuilder();
					for (Element e : elements) {
						String name = e.attributeValue("name");
						String status = e.attributeValue("status");
						String ticketNo = e.attributeValue("no");
						Passenger p = new Passenger();
						p.setName(name);
						p.setOrderNo(orderNo);
						p.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
						p.setPrintTicketBy(username);
						p.setTicketNo(ticketNo);
						UpdateWrapper<Passenger> update = new UpdateWrapper<Passenger>();
						update.eq("name", p.getName());
						update.eq("order_no", p.getOrderNo());
						if ("1".equals(status)) {// 通过
							sb.append(name + "(" + ticketNo + ")验证通过，");
							p.setTicketStatus(WebConstant.OK_TICKET);
							p.setStatus(WebConstant.OK_TICKET);
						} else {// 不通过
							sb.append(name + "(" + ticketNo + ")验证不通过，");
							p.setTicketStatus(WebConstant.ERROR_TICKET);
							p.setStatus(WebConstant.ERROR_TICKET);
						}
						passengerService.updateByWapper(p, update);
					}
					result.setMsg(sb.toString());
					log.setContent(result.getMsg());
					orderService.updateStatus(WebConstant.ORDER_PRINT, orderNo);
				} else {
					if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
						for (Passenger passenger : list) {
							passenger.setTicketStatus(WebConstant.ERROR_TICKET);
							passenger.setStatus(WebConstant.ERROR_TICKET);
							passenger.setPrintTicketBy(username);
							passengerService.updateTicketStatus(passenger);
						}
					}
					result.setCode(-1);
					result.setMsg(element.attributeValue("errorMsg"));
					log.setContent(result.getMsg());
				}
				logg.info("===============tts票号验证返回=================");
				logg.info(resultData);
				logg.info("===============tts票号验证返回=================");
				ttsOrderService.locked(orderNo, orderNo.substring(0, 3), true);
			}
		} catch (Exception e) {
			logg.error("TTS出票异常。。", e);
			result.setCode(-2);
			result.setMsg("出票失败！请联系系统管理员");
			log.setContent(result.getMsg());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		logService.saveLog(log);
		return result;
	}

	/**
	 * 就旅行验证票号,支持单张回填
	 * 
	 * @param
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult<Void> verifyJiuTicketNo(List<Passenger> orgList, Order order) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		OrderOperateLog log = new OrderOperateLog();
		if (WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
			result.setMsg("该订单已出票！");
			return result;
		}
		/*
		 * if(WebConstant.ORDER_NOTICK_REFUND.equals(order.getStatus())){
		 * result.setMsg("该订单已申请退款！"); return result; }
		 */
		try {
			List<Passenger> list = new ArrayList<Passenger>();
			for (Passenger p : orgList) {
				if (!StringUtils.isEmpty(p.getTicketNo())) {
					list.add(p);
				}
			}
			if (list.size() == 0) {
				result.setCode(-2);
				result.setMsg("请粘贴票号！");
				return result;
			}
			int allPS = Integer.parseInt(order.getPassengerCount());
			String orderNo = list.get(0).getOrderNo();
			OrderVO orderVo = orderService.selectOrderDetails(orderNo, InterfaceConstant.ORDER_SOURCE_JIU, null);
			String username = ShiroKit.getLocalName();
			if (StringUtils.isEmpty(username)) {
				username = orderService.isHavePcocess(orderNo);
			}
			log.setName(ShiroKit.getLocalName());
			log.setOrderNo(orderVo.getOrderNo());
			log.setType("订单处理");
			orderVo.setPassengetList(list);
			String resultData = jiuOrderServer.verifyTicketNo(orderVo);
			if (resultData == null) {
				result.setCode(-2);
				result.setMsg("出票失败！");
				log.setContent(result.getMsg());
				jiuOrderServer.locked(orderNo, true);
				return result;
			}
			Document doc = DocumentHelper.parseText(resultData);
			Element rootElement = doc.getRootElement();
			Element element = rootElement.element("OrderDetail");
			String statusV = element.attributeValue("status");
			if ("5".equals(statusV)) {// 出票完成
				for (Passenger passenger : list) {
					passenger.setTicketStatus(WebConstant.OK_TICKET);
					passenger.setStatus(WebConstant.OK_TICKET);
					passenger.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					passenger.setPrintTicketCabin(passenger.getCabin());
					passenger.setPrintTicketBy(username);
					passengerService.updateTicketStatus(passenger);
				}
				if (allPS == list.size()) {
					orderService.updateStatus("3", orderVo.getOrderNo());
				}
				result.setMsg("票号验证通过");
				log.setContent(result.getMsg());
			} else {
				// 一张张贴的，返回都是校验失败
				if (allPS != list.size()) {
					@SuppressWarnings("unchecked")
					List<Element> elements = element.elements("passenger");
					StringBuilder sb = new StringBuilder();
					for (Element e : elements) {
						String name = e.attributeValue("name");
						String status = e.attributeValue("status");
						String ticketNo = e.attributeValue("no");
						Passenger p = new Passenger();
						p.setName(name);
						p.setOrderNo(orderNo);
						p.setPrintTicketDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
						p.setPrintTicketBy(username);
						p.setTicketNo(ticketNo);
						UpdateWrapper<Passenger> update = new UpdateWrapper<Passenger>();
						update.eq("name", p.getName());
						update.eq("order_no", p.getOrderNo());
						if ("1".equals(status)) {// 通过
							sb.append(name + "(" + ticketNo + ")验证通过，");
							p.setTicketStatus(WebConstant.OK_TICKET);
							p.setStatus(WebConstant.OK_TICKET);
						} else {// 不通过
							sb.append(name + "(" + ticketNo + ")验证不通过，");
							p.setTicketStatus(WebConstant.ERROR_TICKET);
							p.setStatus(WebConstant.ERROR_TICKET);
						}
						passengerService.updateByWapper(p, update);
					}
					result.setMsg(sb.toString());
					log.setContent(result.getMsg());
					orderService.updateStatus(WebConstant.ORDER_PRINT, orderNo);
				} else {
					if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
						for (Passenger passenger : list) {
							passenger.setTicketStatus(WebConstant.ERROR_TICKET);
							passenger.setStatus(WebConstant.ERROR_TICKET);
							passenger.setPrintTicketBy(username);
							passengerService.updateTicketStatus(passenger);
						}
					}
					result.setCode(-1);
					result.setMsg(element.attributeValue("errorMsg"));
					log.setContent(result.getMsg());
				}
				logg.info("===============Jiu票号验证返回=================");
				logg.info(resultData);
				logg.info("===============Jiu票号验证返回=================");
				jiuOrderServer.locked(orderNo, true);
			}
		} catch (Exception e) {
			logg.error("就旅行出票异常。。", e);
			result.setCode(-2);
			result.setMsg("出票失败！请联系系统管理员");
			log.setContent(result.getMsg());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		logService.saveLog(log);
		return result;
	}

	/**
	 * 解锁
	 * 
	 * @param orderNo
	 * @return
	 */
	@ResponseBody
	@PostMapping("/deleteProcess")
	public Object deleteProcess(String orderNo) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		try {
			String process = orderService.isHavePcocess(orderNo);
			if (!StringUtils.isEmpty(process)) {
				String username = ShiroKit.getSubject().getPrincipal().toString().trim();
				if (!process.trim().equals(username)) {
					if (!ShiroKit.hasPermission("/unlock")) {
						result.setCode(-1);
						result.setMsg("锁定人不符!");
						return result;
					}
				}
			}
			orderService.deleteProcess(orderNo);
			result.setCode(0);
			OrderOperateLog log = new OrderOperateLog();
			log.setName(ShiroKit.getLocalName());
			log.setOrderNo(orderNo);
			log.setContent("解锁");
			log.setType("订单处理");
			logService.saveLog(log);
			//解锁平台
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						if (orderNo.startsWith("rnf") || orderNo.startsWith("rnb")) {
							String orderShop = orderNo.substring(0, 3);
							ttsOrderService.unLocked(orderNo, orderShop, true);
						} else {
							Order order = orderService.getOrderByOrderNo(orderNo);
							if (InterfaceConstant.ORDER_SOURCE_TC.equals(order.getOrderSource())) {
								tcOrderService.unlocked(orderNo, true);
							} else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(order.getOrderSource())) {
								ctripOrderService.assignOrder(orderNo, order.getOrderShop(), "0", true);
							} else if (InterfaceConstant.ORDER_SOURCE_TN.equals(order.getOrderSource())) {
								TuniuOrderService.unlock(order.getcOrderNo(), order.getOrderShop(), true);
							} else if (InterfaceConstant.ORDER_SOURCE_TB.equals(order.getOrderSource())) {
								tbOrderService.unlocked(orderNo, true);
							} else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(order.getOrderSource())) {
								jiuOrderServer.unlocked(orderNo, true);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			Thread t = new Thread(r);
			t.start();
		} catch (Exception e) {
			result.setCode(-1);
			result.setMsg("token过期，先刷新当前页面");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/isHavePcocess")
	public Object isHavePcocess(String orderNo) {
		ResponseResult<Boolean> result = new ResponseResult<Boolean>();
		if (StringUtils.isEmpty(orderNo)) {
			result.setData(true);
			result.setMsg("处理失败");
			return result;
		}
		String pcocess = orderService.isHavePcocess(orderNo);
		if (!StringUtils.isEmpty(pcocess)) {
			result.setMsg("该订单已被" + pcocess + "锁定!");
			result.setData(true);
		} else {
			result.setData(false);
		}
		return result;
	}

	/**
	 * 修改tts订单为出票中，只有去哪儿平台才有这个操作
	 */
	@ResponseBody
	@RequestMapping("/ttsPrinting")
	public Object ttsPrinting(String orderNo) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		OrderOperateLog log = new OrderOperateLog();
		log.setName(ShiroKit.getLocalName());
		log.setOrderNo(orderNo);
		log.setType("订单处理");
		try {
			JSONObject json = ttsOrderService.updateOrderStatus(orderNo, orderNo.substring(0, 3), true);
			// 返回实例：{"ret":1,"msg":"状态已经改为出票中，请贴票号，完成出票。","url":"http://fuwu.qunar.com/modules/proxy/order?id=111684&orderNo=rnb200321154617145001&domain=rnb.trade.qunar.com&action=manage"}
			if (json == null) {
				result.setCode(-1);
				result.setMsg("请求失败，请重新点击！");
				return result;
			}
			if (json.getInt("ret") == 1) {
				log.setContent("修改状态为出票中成功");
				result.setMsg(json.getString("msg"));
				orderService.updateStatus(WebConstant.ORDER_PRINT, orderNo);
			} else {
				// 订单状态已不是待出票--->出票中
				String status = ttsOrderService.getOrderStatus(orderNo);
				if ("TICKET_LOCK".equals(status)) {
					result.setMsg("已是出票中");
					log.setContent(result.getMsg());
					orderService.updateStatus(WebConstant.ORDER_PRINT, orderNo);
				} else if ("TICKET_OK".equals(status)) {
					result.setMsg("该单已出票完成");
					log.setContent("无法改为出票中，在平台已经出票完成");
					orderService.updateStatus(WebConstant.ORDER_PRINT_TICKET, orderNo);
				} else if ("APPLY_4_RETURN_PAY".equals(status) || "REFUND_OK".equals(status)) {
					result.setMsg("无法改为出票中,该单申请退款");
					log.setContent(result.getMsg());
					orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
				}else if("PAY_OK".equals(status)){
                    result.setMsg("无法改为出票中,订单状态待出票");
                    log.setContent(result.getMsg());
                } else {
					result.setMsg("无法改为出票中,该单已不是待出票状态");
					log.setContent(result.getMsg());
				}
				logg.info("tts订单状态更新出票中,订单状态已经改变," + result.getMsg() + orderNo + ":" + status);

			}
			logService.saveLog(log);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg("请求失败，重试一下！");
		}
		return result;
	}

	/**
	 * 判断当前用户是否可以操作出票
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getLockCount")
	public Object getLockCount(String orderNo, HttpServletRequest request) {
		ResponseResult<Boolean> result = new ResponseResult<Boolean>();
		String name = "";
		try {
			name = ShiroKit.getSubject().getPrincipal().toString();
		} catch (Exception e) {
			result.setData(false);
			result.setMsg("Token过期，先刷新当前页面！");
			return result;
		}
		synchronized (orderLcok) {
			try {
				String pcocess = orderService.isHavePcocess(orderNo);
				if (!StringUtils.isEmpty(pcocess) && !name.equals(pcocess)) {
					result.setMsg("该订单已被" + pcocess + "锁定!");
					result.setData(false);
					return result;
				}
				int maxCount = Integer.parseInt(DictUtils.getDictCode("max_processing", "max"));
				Integer lockCount = orderService.getLockCount(name) + 1;
				if (lockCount >= maxCount) {
					result.setData(false);
					result.setMsg("处理中的订单已达到最大数量限制:" + maxCount + "，请先完成处理中订单！");
					return result;
				}
				Order order = orderService.getOrderByOrderNo(orderNo);
				String orderSource = order.getOrderSource();
				String orderShop = order.getOrderShop();
				if (!WebConstant.ORDER_PRINT_TICKET.equals(order.getStatus())) {
					if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)) {
						String lockedOrder = tcOrderService.lockedOrder(orderNo, true);
						if (lockedOrder != null) {
							JSONObject fromObject = JSONObject.fromObject(lockedOrder);
							if (!fromObject.getBoolean("success")) {
								result.setData(false);
								result.setMsg(fromObject.getString("message"));
								return result;
							}
						}
					} else if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)) {
						JSONObject locked = ttsOrderService.locked(orderNo, orderNo.substring(0, 3), true);
						if (locked != null) {
							if (!locked.getBoolean("ret")) {
								result.setData(false);
								result.setMsg("该单在TTS上锁单了！");
								return result;
							}
						}
					} else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource)) {
						JSONObject locked = jiuOrderServer.locked(orderNo, true);
						if (locked != null) {
							if (!locked.getBoolean("ret")) {
								result.setData(false);
								result.setMsg("该单在就旅行上锁单了！");
								return result;
							}
						}
					} else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)) {
						String assignOrder = ctripOrderService.assignOrder(orderNo, orderShop, "1", true);
						if (assignOrder != null) {
							JSONObject josnO = JSONObject.fromObject(assignOrder).getJSONObject("Header");
							if (josnO.getInt("ResultCode") == 1) {
								result.setData(false);
								result.setMsg(josnO.getString("ResultMsg"));
								return result;
							}
						}
					} else if (InterfaceConstant.ORDER_SOURCE_TN.equals(orderSource)) {
						JSONObject lock = TuniuOrderService.lock(order.getcOrderNo(), orderShop, true);
						if (lock != null) {
							if (!lock.getBoolean("success")) {
								result.setData(false);
								result.setMsg(lock.getString("msg"));
								return result;
							}
						}
					} else if (InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)) {

						String lockedOrder = tbOrderService.lockedOrder(orderNo, false);
						if (!StringUtils.isEmpty(lockedOrder)) {
							JSONObject fromObject = JSONObject.fromObject(lockedOrder);
							JSONObject json = fromObject.getJSONObject("result");
							if (!"上上千航服".equals(json.getString("data"))) {
								result.setData(false);
								result.setMsg("该单在淘宝平台上锁单了!");
								return result;
							}
						} else {
							logg.error("tb锁单失败>>>" + orderNo);
						}
					}
				}
				int count = orderService.addProcess(name, orderNo);// 加锁定人
				if (count == 0) {
					result.setData(false);
					result.setMsg("锁单失败，再锁一次");
					return result;
				} else {
					result.setData(true);
					result.setMsg(name);
					OrderOperateLog log = new OrderOperateLog();
					log.setName(name);
					log.setOrderNo(orderNo);
					log.setContent("锁单");
					log.setType("订单处理");
					logService.saveLog(log);
					return result;
				}
			} catch (Exception e) {
				result.setData(false);
				result.setMsg("处理失败");
				logg.error("点击处理订单异常" + orderNo, e);
				return result;
			}
		}
	}

	/**
	 * 同程订单驳回
	 * 
	 * @param orderNo
	 * @param reason
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/tcRefuseOrder")
	public Object tcRefuseOrder(String orderNo, String reason) {
		ResponseResult<Boolean> result = new ResponseResult<Boolean>();
		try {
			String resultStr = tcOrderService.refuseOrder(orderNo, reason);
			JSONObject json = JSONObject.fromObject(resultStr);
			if (json.getBoolean("isSuccess")) {
				result.setMsg(
						StringUtils.isEmpty(json.getString("errorMessage")) ? "驳回成功" : json.getString("errorMessage"));
				result.setData(true);
				Order order = new Order();
				order.setOrderNo(orderNo);
				order.setStatus(WebConstant.REFUSE_ORDER_CANCEL);
				orderService.updateOrder(order);
				OrderOperateLog log = new OrderOperateLog();
				log.setName(ShiroKit.getLocalName());
				log.setOrderNo(orderNo);
				log.setContent("订单驳回");
				log.setType("订单处理");
				logService.saveLog(log);
				return result;
			} else {
				result.setMsg(
						StringUtils.isEmpty(json.getString("errorMessage")) ? "驳回失败" : json.getString("errorMessage"));
				result.setData(false);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setData(false);
			result.setMsg("处理失败");
			return result;
		}

	}

	/**
	 * 同程订单驳回操作判断
	 * 
	 * @param orderNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/canRefuseOrder")
	public Object canRefuseOrder(String orderNo) {
		ResponseResult<Boolean> result = new ResponseResult<Boolean>();
		String name = "";
		try {
			name = ShiroKit.getSubject().getPrincipal().toString();
		} catch (Exception e) {
			result.setData(false);
			result.setMsg("Token过期，先刷新当前页面！");
			return result;
		}
		try {
			synchronized (orderLcok) {
				String pcocess = orderService.isHavePcocess(orderNo);
				if (!StringUtils.isEmpty(pcocess) && !name.equals(pcocess)) {
					result.setMsg("该订单已被" + pcocess + "锁定!");
					result.setData(false);
					return result;
				}
				String lockedOrder = tcOrderService.lockedOrder(orderNo, true);
				JSONObject fromObject = JSONObject.fromObject(lockedOrder);
				if (!fromObject.getBoolean("success")) {
					result.setData(false);
					result.setMsg(fromObject.getString("message"));
					return result;
				}
				orderService.addProcess(name, orderNo);// 加锁定人
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setData(false);
			result.setMsg("处理失败");
			return result;
		}
		result.setData(true);
		return result;
	}

	/**
	 * 获取操作日志
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getLogByOrderNo")
	public Object getLogByOrderNo(String orderNo, long page, int limit) {
		ResponseResult<List<OrderOperateLog>> result = new ResponseResult<List<OrderOperateLog>>();
		try {
			List<OrderOperateLog> list = logService.getLogByOrderNo(orderNo, page, limit);
			result.setCode(0);
			result.setData(list);
			Query query = new Query();
			query.addCriteria(Criteria.where("orderNo").is(orderNo));
			long count = mongoTemplate.count(query, OrderOperateLog.class);
			result.setCount((int) count);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg("查无记录");
		}
		return result;
	}

	/**
	 * 封装订单的查询条件
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	private OrderQuery createQueryData(HttpServletRequest request) throws ParseException {
		OrderQuery query = new OrderQuery();

		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		query.setStartDate(StringUtils.isEmpty(startDate) ? Util.getToday()[0] : startDate.trim() + " 00:00:00");
		query.setEndDate(StringUtils.isEmpty(endDate) ? Util.getToday()[1] : endDate.trim() + " 23:59:59");

		if (!StringUtils.isEmpty(request.getParameter("flightStartDate"))) {
			String flightStartDate = request.getParameter("flightStartDate");
			String flightEndDate = request.getParameter("flightEndDate");
			query.setFlightStartDate(flightStartDate.trim() + " 00:00:00");
			query.setFlightEndDate(
					StringUtils.isEmpty(flightEndDate) ? Util.getToday()[1] : flightEndDate.trim() + " 23:59:59");
		}

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
		if (!StringUtils.isEmpty(request.getParameter("name"))) {
			query.setName(request.getParameter("name").trim());
		}
		if (!StringUtils.isEmpty(request.getParameter("pnr"))) {
			query.setPnr(request.getParameter("pnr").trim());
		}
		if (!StringUtils.isEmpty(request.getParameter("ticketNo"))) {
			query.setTicketNo(request.getParameter("ticketNo").trim());
		}
		if (!StringUtils.isEmpty(request.getParameter("orderNo"))) {
			query.setOrderNo(request.getParameter("orderNo").trim());
		}
		if (!StringUtils.isEmpty(request.getParameter("flight"))) {
			query.setFlight(request.getParameter("flight").trim());
		}
		if (!StringUtils.isEmpty(request.getParameter("orderStatus"))) {
			if (!"0".equals(request.getParameter("orderStatus"))) {
				query.setOrderStatus(Integer.parseInt(request.getParameter("orderStatus")));
			}
		}
		Integer page = StringUtils.isEmpty(request.getParameter("page")) ? 0
				: Integer.parseInt(request.getParameter("page"));
		Integer limit = StringUtils.isEmpty(request.getParameter("limit")) ? 10
				: Integer.parseInt(request.getParameter("limit"));
		query.setPage(page);
		query.setLimit(limit);
		query.setJump((page - 1) * limit);
		return query;
	}

	/**
	 * 平台查询订单
	 * 
	 * @param query
	 */
	private void goToGetOrder(OrderQuery query) {
		String orderSource = query.getOrderSource();
		String orderShop = query.getOrderShop();
		String orderNo = query.getOrderNo().trim();
		boolean exist = orderService.isExist(orderSource, orderNo);
		try {
			if (!exist) {// 本地没有没有则添加这个订单
				if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)) {
					OrderVO orderVo = ttsOrderService.getByOrderNo(orderNo, InterfaceConstant.ORDER_SOURCE_QNR, "2");
					if (orderVo != null) {
						orderService.saveOrderVO(orderVo);
					}
				} else if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)) {
                    String user=DictUtils.getDictCode("order_import_cfgxc"+orderShop, "user");
                    String pass=DictUtils.getDictCode("order_import_cfgxc"+orderShop, "pass");
                    String passWord= Md5Util.md5(user+"#"+pass).toLowerCase();
					CtripOrderVO ctripOrdcerVO = ctripOrderService.getOrderDetailBycOrderNo(orderNo, user, passWord);
					OrderVO orderVo = ctripOrderService.procCtripOrderToOrderVO(ctripOrdcerVO, orderSource, orderShop);
					if (orderVo != null) {
						orderService.saveOrderVO(orderVo);
					}
				} else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)) {
					OrderVO orderVo = tcOrderService.getOrderByOrderNo2(orderNo, orderSource, orderShop);
					if (orderVo != null) {
						List<Flight> list = orderVo.getFlightList();
						if (list.size() == 1) {
							orderVo.getOrder().setTripType(WebConstant.FLIGHT_TYPE_ONEWAY);
						} else {
							orderVo.getOrder().setTripType(WebConstant.FLIGHT_TYPE_GOBACK);
						}
						orderService.saveOrderVO(orderVo);
					}
				} else if (InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)) {
					OrderVO orderByOrderNo = tbOrderService.getOrderByOrderNo(orderNo, orderSource, orderShop);
					if (orderByOrderNo != null) {
						orderService.saveOrderVO(orderByOrderNo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 跟进途牛票号验证接口返回true后订单的状态是否异常
	class CheckOrderStatus implements Runnable {
		private Order order;
		private List<Passenger> list;

		public CheckOrderStatus(Order order, List<Passenger> list) {
			this.order = order;
			this.list = list;
		}

		@Override
		public void run() {
			try {
				if (order == null) {
					return;
				}
				String username = ShiroKit.getSubject().getPrincipal().toString();
				logg.info("开启线程跟进" + order.getOrderNo() + "的订单状态.");
				for (int i = 0; i <= 40; i++) {
					Thread.sleep(3000);
					Integer orderStatus = TuniuOrderService.checkOrderStatus(order.getcOrderNo(), order.getOrderShop());
					if (4 == orderStatus) {
						logg.info("开启线程跟进" + order.getOrderNo() + "的订单状态，状态变为出票完成");
						return;
					}
					if (15 != orderStatus) {
						if (4 != orderStatus) {
							logg.info("开启线程跟进" + order.getOrderNo() + "的订单状态，状态变为出票异常");
							orderService.updateStatus("5", order.getOrderNo());
							for (Passenger passenger : list) {
								passenger.setTicketStatus(WebConstant.ERROR_TICKET);
								passenger.setStatus(WebConstant.ERROR_TICKET);
								passenger.setPrintTicketBy(username);
								passengerService.updateTicketStatus(passenger);
							}
						}
						return;
					}
				}
			} catch (Exception e) {
				logg.error("线程跟进" + order.getOrderNo() + "的订单状态失败.");
				logg.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 生成订单的数据
	 * 
	 * @return
	 */
	private Map<String, Object> createOrderData(Order order, HttpServletRequest request) {
		List<Passenger> passengerList = new ArrayList<Passenger>();// 乘客信息
		List<Flight> flightList = new ArrayList<Flight>();// 航段信息
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		order.setCreateDate(createDate);
		order.setcAddDate(order.getCreateDate());
		order.setcOrderNo(order.getOrderNo());
		String[] names = request.getParameterValues("name");
		String[] passengerTypes = request.getParameterValues("passengerType");
		String[] certNos = request.getParameterValues("certNo");
		String[] certTypes = request.getParameterValues("certType");
		String[] ticketNos = request.getParameterValues("ticketNo");
		String[] passengerIds = request.getParameterValues("passengerId");

		String flightDepDate = request.getParameter("flightDepDate");
		String flightArrDate = request.getParameter("flightArrDate");

		Flight flight = new Flight();
		String[] flightDepDates = flightDepDate.split(" ");
		String[] flightArrDates = flightArrDate.split(" ");
		flight.setFlightDepDate(flightDepDates[0]);
		flight.setFlightArrDate(flightArrDates[0]);
		flight.setDepTime(flightDepDates[1]);
		flight.setArrTime(flightArrDates[1]);
		flight.setArrCityCode(order.getArrCityCode());
		flight.setDepCityCode(order.getDepCityCode());
		flight.setOrderNo(order.getOrderNo());
		flight.setFlightNo(order.getFlightNo());
		flight.setCreateDate(createDate);
		flight.setCabin(order.getCabin());
		flight.setFlightId(StringUtils.isEmpty(request.getParameter("flightId")) ? null
				: Integer.valueOf(request.getParameter("flightId")));
		flightList.add(flight);
		order.setFlightDate(flightDepDates[0]);
		order.setTotalTax("50");

		for (int i = 0; i < names.length; i++) {
			Passenger p = new Passenger();
			// 如果是编辑页面请求的数据，设置id属性，后面根据id修改数据
			p.setPassengerId(StringUtils.isEmpty(passengerIds) ? null : Long.valueOf(passengerIds[i]));
			p.setName(names[i]);
			p.setPassengerType(passengerTypes[i]);
			p.setCertNo(certNos[i]);
			p.setCertType(certTypes[i]);
			p.setTicketNo(ticketNos[i]);
			p.setOrderNo(order.getOrderNo());
			p.setFee("50");
			p.setTax("0");
			p.setFlightNo(order.getFlightNo());
			p.setDepCityCode(order.getDepCityCode());
			p.setArrCityCode(order.getArrCityCode());
			p.setFlightDepDate(order.getFlightDate());
			p.setTicketPrice("0");
			p.setDepTime(flight.getDepTime());
			p.setArrTime(flight.getArrTime());
			p.setCabin(order.getCabin());
			p.setCreateDate(createDate);
			passengerList.add(p);
		}
		order.setPassengerCount(passengerList.size() + "");

		if (!StringUtils.isEmpty(request.getParameter("gobackDepDate"))) {
			String gobackDepDate = request.getParameter("gobackDepDate");
			String gobackArrDepDate = request.getParameter("gobackArrDepDate");
			Flight backFlight = new Flight();
			String[] gobackDepDates = gobackDepDate.split(" ");
			String[] gobackArrDepDates = gobackArrDepDate.split(" ");
			backFlight.setFlightDepDate(gobackDepDates[0]);
			backFlight.setFlightArrDate(gobackArrDepDates[0]);
			backFlight.setDepTime(gobackDepDates[1]);
			backFlight.setArrTime(gobackArrDepDates[1]);
			backFlight.setDepCityCode(order.getArrCityCode());
			backFlight.setArrCityCode(order.getDepCityCode());
			backFlight.setOrderNo(order.getOrderNo());
			backFlight.setFlightNo(request.getParameter("gobackFlightNo"));
			backFlight.setCabin(order.getCabin());
			backFlight.setCreateDate(createDate);
			backFlight.setFlightId(StringUtils.isEmpty(request.getParameter("flightId2")) ? null
					: Integer.valueOf(request.getParameter("flightId2")));
			flightList.add(backFlight);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order", order);
		map.put("passengerList", passengerList);
		map.put("flightList", flightList);
		return map;
	}

	@PostMapping("/editOrder")
	@SuppressWarnings("unchecked")
	@ResponseBody
	public Object editOrder(Order order, HttpServletRequest request) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		try {
			Map<String, Object> map = this.createOrderData(order, request);
			orderService.editOrder((Order) map.get("order"), (List<Passenger>) map.get("passengerList"),
					(List<Flight>) map.get("flightList"));
			result.setMsg("修改成功");
			OrderOperateLog log = new OrderOperateLog();
			log.setContent("修改订单");
			log.setType("订单处理");
			logService.saveLog(log);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("修改失败");
		}
		return result;
	}

	@PostMapping("/addOrder")
	@SuppressWarnings("unchecked")
	@ResponseBody
	public Object addOrder(Order order, HttpServletRequest request) {
		ResponseResult<Void> result = new ResponseResult<Void>();
		try {
			Map<String, Object> map = this.createOrderData(order, request);
			orderService.addOrder((Order) map.get("order"), (List<Passenger>) map.get("passengerList"),
					(List<Flight>) map.get("flightList"));
			result.setMsg("添加成功");
			OrderOperateLog log = new OrderOperateLog();
			log.setContent("添加订单");
			log.setType("订单处理");
			logService.saveLog(log);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("添加失败");
		}
		return result;
	}

	/**
	 * @ResponseBody @PostMapping("/deleteOrder") public Object
	 *               deleteOrder(String[] orderNos){ ResponseResult<Void>
	 *               result=new ResponseResult<Void>(); try {
	 *               orderService.deleteOrder(orderNos); result.setMsg("删除成功");
	 *               } catch (Exception e) { e.printStackTrace();
	 *               result.setMsg("删除失败"); } return result; }
	 */
}
