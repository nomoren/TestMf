package cn.ssq.ticket.system.service.OrderImport.impl;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.importBean.TTsBean.QnrFlightVO;
import cn.ssq.ticket.system.entity.importBean.TTsBean.QnrOrderVO;
import cn.ssq.ticket.system.entity.importBean.TTsBean.QnrPassengerVO;
import cn.ssq.ticket.system.entity.pp.PPMSG;
import cn.ssq.ticket.system.service.ChangeService;
import cn.ssq.ticket.system.service.FlightService;
import cn.ssq.ticket.system.service.OrderImport.OrderImportService;
import cn.ssq.ticket.system.service.OrderService;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.ssq.ticket.system.util.LimitQueue;
import cn.ssq.ticket.system.util.WebConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 去哪儿平台服务类
 */
@Service("ttsOrderService")
public class TTsOrderService implements OrderImportService {

	// 待出票订单队列
	public static LimitQueue<String> status1 = new LimitQueue<String>(200);

    public static LimitQueue<String> status5 = new LimitQueue<String>(200);
	private static Logger log = LoggerFactory.getLogger(TTsOrderService.class);

	// private static CloseableHttpClient httpClient;

	// private static CloseableHttpClient httpClient2;
    private static int timeout = 60000;
	private static StringBuilder rnfCookie = new StringBuilder();
	private static StringBuilder rnbCookie = new StringBuilder();
	@Autowired
	private MongoTemplate mongnTemplate;

	@Autowired
	private OrderService orderService;
	
    @Autowired
    private ChangeService changeService;

    @Autowired
    private FlightService flightService;



	/**
	 * 解析xml
	 *
	 * @param xmlContent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked" })
	public static List<QnrOrderVO> xmlToOrderList(String xmlContent) throws Exception {
		List<QnrOrderVO> list = new ArrayList<QnrOrderVO>();
		try {
			Document doc = DocumentHelper.parseText(xmlContent);
			Element rootElement = doc.getRootElement();
			String resultStatus = rootElement.attributeValue("status");
			if ("ok".equals(resultStatus)) {
				List<Element> orderElementList = rootElement.elements("order");
				if (null != orderElementList && orderElementList.size() > 0) {
					for (Element orderElement : orderElementList) {
						QnrOrderVO orderVo = orderElementToVO(orderElement);
						if (orderVo != null) {
							list.add(orderVo);
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return list;
	}

	/**
	 * 订单标签封装成vo
	 *
	 * @param orderElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static QnrOrderVO orderElementToVO(final Element orderElement) {
		if (null == orderElement) {
			return null;
		}
		QnrOrderVO vo = new QnrOrderVO();
		vo.setBigPnr(orderElement.attributeValue("pnrBigCodeAdult"));
		vo.setId(orderElement.attributeValue("id"));
		vo.setOrderNo(orderElement.attributeValue("orderNo"));
		try {
			vo.setRootOrderNo(orderElement.attributeValue("rootOrderNo"));
		} catch (Exception e) {
		}
		vo.setAllPrice(orderElement.attributeValue("allPrice"));
		vo.setNoPay(orderElement.attributeValue("noPay"));
		vo.setChildPrintPrice(orderElement.attributeValue("childPrintPrice"));
		vo.setChildFaceValue(orderElement.attributeValue("childFaceValue"));
		vo.setSource(orderElement.attributeValue("source"));
		vo.setPolicySource(orderElement.attributeValue("policySource").replaceAll("null", ""));
		vo.setTicketMode(orderElement.attributeValue("ticketMode"));
		vo.setPolicyFriendly(orderElement.attributeValue("policyFriendly"));
		vo.setPayChannel(orderElement.attributeValue("payChannel"));
		vo.setPayStatus(orderElement.attributeValue("payStatus"));
		vo.setInsuranceUnitPrice(orderElement.attributeValue("insuranceUnitPrice"));
		vo.setBacknote(orderElement.attributeValue("backnote"));
		vo.setCabinnote(orderElement.attributeValue("cabinnote"));
		vo.setInsuranceCuttingPrice(orderElement.attributeValue("insuranceCuttingPrice"));
		vo.setViewPrice(orderElement.attributeValue("viewPrice"));
		vo.setPrice(orderElement.attributeValue("price"));
		vo.setPnr(orderElement.attributeValue("pnr"));
		vo.setCpnr(orderElement.attributeValue("cpnr"));
		vo.setBigPnr(orderElement.attributeValue("pnrBigCodeAdult"));
		vo.setTicType(orderElement.attributeValue("tic_type"));
		vo.setConstructionFee(orderElement.attributeValue("constructionFee"));
		vo.setFuelTax(orderElement.attributeValue("fuelTax"));
		vo.setChildFuelTax(orderElement.attributeValue("childFuelTax"));
		vo.setPolicyType(orderElement.attributeValue("policyType"));
		vo.setStatus(orderElement.attributeValue("status"));
		vo.setContact(orderElement.attributeValue("contact"));
		vo.setContactMob(orderElement.attributeValue("contactMob"));
		vo.setContactEmail(orderElement.attributeValue("contactEmail"));
		vo.setCreateTime(orderElement.attributeValue("createTime"));
		vo.setNeedPS(orderElement.attributeValue("needPS"));
		vo.setLastPrintTicketTime(orderElement.attributeValue("deadline"));
		vo.setLastUpdateTime(orderElement.attributeValue("lastUpdateTime"));
		vo.setContactTel(orderElement.attributeValue("contactTel"));
		vo.setAddress(orderElement.attributeValue("address"));
		vo.setCompany(orderElement.attributeValue("company"));
		vo.setExpType(orderElement.attributeValue("expType"));
		vo.setOrdernumber(orderElement.attributeValue("ordernumber"));
		vo.setXcdPrice(orderElement.attributeValue("xcdPrice"));
		vo.setXcd(orderElement.attributeValue("xcd"));
		vo.setXCDN(orderElement.attributeValue("XCDN"));
		vo.setSjr(orderElement.attributeValue("sjr"));
		vo.setSendtime(orderElement.attributeValue("sendtime"));
		vo.setPolicyCode(orderElement.attributeValue("policySource").replaceAll("null", ""));

		// 乘客信息
		List<Element> passengerElements = orderElement.elements("passenger");
		if (null != passengerElements && passengerElements.size() > 0) {
			List<QnrPassengerVO> pList = new ArrayList<QnrPassengerVO>();
			for (Element passengerElement : passengerElements) {
				QnrPassengerVO passenger = new QnrPassengerVO();
				passenger.setName(passengerElement.attributeValue("name"));
				passenger.setBirthday(passengerElement.attributeValue("birthday"));
				passenger.setAgeType(passengerElement.attributeValue("ageType"));
				passenger.setCardType(passengerElement.attributeValue("cardType"));
				passenger.setPrice(passengerElement.attributeValue("price"));
				passenger.setPriceType(passengerElement.attributeValue("priceType"));
				passenger.setCardNum(passengerElement.attributeValue("cardNum"));
				passenger.setEticketNum(passengerElement.attributeValue("eticketNum"));
				passenger.setInsuranceCount(passengerElement.attributeValue("insuranceCount"));
				passenger.setBxSource(passengerElement.attributeValue("bxSource"));
				passenger.setBxName(passengerElement.attributeValue("bxName"));
				Element insuranceElement = passengerElement.element("insurance");
				if (null != insuranceElement) {
					passenger.setInsuranceNo(insuranceElement.attributeValue("insuranceNo"));
					passenger.setBxFlight(insuranceElement.attributeValue("bxFlight"));
					passenger.setBxStatus(insuranceElement.attributeValue("bxStatus"));
				}
				pList.add(passenger);
			}
			vo.setPassengerList(pList);
		}
		// 航班信息
		List<Element> flightElements = orderElement.elements("flight");
		if (null != flightElements && flightElements.size() > 0) {
			List<QnrFlightVO> fList = new ArrayList<QnrFlightVO>();
			for (Element flightElement : flightElements) {
				QnrFlightVO flight = new QnrFlightVO();
				flight.setCode(flightElement.attributeValue("code"));
				flight.setCabin(flightElement.attributeValue("cabin"));
				flight.setCcabin(flightElement.attributeValue("ccabin"));
				flight.setDep(flightElement.attributeValue("dep"));
				flight.setArr(flightElement.attributeValue("arr"));
				flight.setDepDay(flightElement.attributeValue("depDay"));
				flight.setDepTime(flightElement.attributeValue("depTime"));
				flight.setArrTime(flightElement.attributeValue("arrTime"));
				flight.setRealCode(flightElement.attributeValue("realCode"));
				fList.add(flight);
			}
			vo.setFlightList(fList);
		}

		Element refund = orderElement.element("refund");
		if (refund != null) {
			String refundPrice = refund.attributeValue("refund_price");
			String refundReason = refund.attributeValue("refund_reason");
			vo.setRefundPrice("null".equals(refundPrice) ? "" : refundPrice);
			vo.setRefundReason("null".equals(refundReason) ? "" : refundReason);
		}

		return vo;
	}

    /**
     * 转换 qnrVO->OrderVO
     * @param qnrVO
     * @param orderSource
     * @param orderShop
     * @return
     */
	public static OrderVO procQnrOrderToOrderVO(QnrOrderVO qnrVO, String orderSource, String orderShop) {
		if (null == qnrVO) {
			return null;
		}
		OrderVO orderVo = new OrderVO();
		orderVo.setOrderNo(qnrVO.getOrderNo());
		orderVo.setOrderId(Long.valueOf(qnrVO.getId()));
		orderVo.setBigPnr(qnrVO.getId());
		orderVo.setStatus(qnrVO.getStatus());
		orderVo.setReamrkStr(qnrVO.getLastUpdateTime());
		orderVo.setRefundPrice(qnrVO.getRefundPrice());
		orderVo.setRefundReason(qnrVO.getRefundReason());
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Order order = new Order();
		order.setOrderNo(qnrVO.getOrderNo());
		order.setcOrderNo(qnrVO.getOrderNo());
		order.setRemarkStr(qnrVO.getRootOrderNo());
		order.setLastPrintTicketTime(qnrVO.getLastPrintTicketTime());
		order.setPnr(qnrVO.getPnr());
		order.setTotalTax(qnrVO.getConstructionFee());
		// order.setBigPnr(qnrVO.getId());
		order.setCreateBy("system");
		order.setStatus(WebConstant.ORDER_NO_TICKET);// 未出票
		order.setPayStatus(WebConstant.PAYMENT_ALREADY); // 已支付
		order.setInterfaceImport(WebConstant.INTERFACE_IMPORT_YES);
		order.setUpdateTicketStatus(WebConstant.UPDATE_TICKET_NO_DISABLE);
		order.setPolicyRemark(qnrVO.getCabinnote());
		order.setTicType(qnrVO.getTicType());
		order.setBigPnr(qnrVO.getBigPnr());
		order.setTraStatus(qnrVO.getXCDN());
		order.setBookWay("");
		/*
		 * if("自动出票".equals(qnrVO.getIssue_ticket_type())){ }else{
		 * order.setBookWay("手工出票"); }
		 */
		order.setOrderSource(orderSource);
		order.setOrderShop(orderShop);
		order.setTotalPrice(qnrVO.getAllPrice());
		order.setRelationName(qnrVO.getContact());
		order.setRelationMobile(qnrVO.getContactMob());
		order.setRelationEmail(qnrVO.getContactEmail());
		order.setcAddDate(qnrVO.getCreateTime());
		order.setPolicyType(qnrVO.getPolicySource());
		String needPS = qnrVO.getNeedPS();
		if ("true".equalsIgnoreCase(needPS)) {
			order.setTraTotalPrice(qnrVO.getXcdPrice());
			// order.setTraStatus("1");
		} else {
			// order.setTraStatus("0");
		}
		if (qnrVO.getSource_order_no() == null) {
			order.setOutOrderNo("");// 采购源订单号
		} else {
			order.setOutOrderNo(qnrVO.getSource_order_no());// 采购源订单号
		}

		List<QnrFlightVO> flightList = qnrVO.getFlightList();
		QnrFlightVO firstFlightVO = null;
		if (null != flightList && flightList.size() > 0) {
			order.setTripType(1 == flightList.size() ? WebConstant.FLIGHT_TYPE_ONEWAY : WebConstant.FLIGHT_TYPE_GOBACK);
			List<Flight> fList = new ArrayList<Flight>();
			for (QnrFlightVO flightVO : flightList) {
				Flight flight = new Flight();
				flight.setOrderNo(order.getOrderNo());
				flight.setOrderSource(order.getOrderSource());
				flight.setOrderShop(order.getOrderShop());
				flight.setSegmentType(order.getTripType());
				flight.setAirlineCode(flightVO.getCode().substring(0, 2));
				flight.setFlightNo(flightVO.getCode());
				flight.setDepCityCode(flightVO.getDep());
				flight.setArrCityCode(flightVO.getArr());
				flight.setFlightDepDate(flightVO.getDepDay());
				flight.setDepTime(flightVO.getDepTime());
				flight.setArrTime(flightVO.getArrTime());
				flight.setCabin(flightVO.getCabin());
				flight.setPrintTicketCabin(flightVO.getCabin());
				flight.setCreateDate(createDate);
				// flight.setRealCode(StringUtils.trimToEmpty(flightVO.getRealCode()));
				if (null == firstFlightVO) {
					firstFlightVO = flightVO;
				}
				fList.add(flight);
			}
			orderVo.setFlightList(fList);
		}

		/*
		 * Travel travel=new Travel(); travel.setFlightNo(order.getFlightNo());
		 * travel.setOrderNo(order.getOrderNo()); travel.setPnr(qnrVO.getPnr());
		 */

		List<QnrPassengerVO> passengerList = qnrVO.getPassengerList();
		if (null != passengerList && passengerList.size() > 0) {
			order.setPassengerCount(String.valueOf(passengerList.size()));
			List<Passenger> pList = new ArrayList<Passenger>();
			for (QnrPassengerVO passengerVO : passengerList) {
				Passenger passenger = new Passenger();
				passenger.setOrderNo(order.getOrderNo());
				passenger.setOrderSource(order.getOrderSource());
				passenger.setOrderShop(order.getOrderShop());
				passenger.setName(passengerVO.getName());
				passenger.setTicketStatus(WebConstant.NO_TICKET);
				passenger.setStatus(WebConstant.NO_TICKET);
				passenger.setCreateDate(createDate);
				passenger.setPolicyType(order.getPolicyType());
				passenger.setTicketNo(passengerVO.getEticketNum());
				String type = passengerVO.getCardType();
				if ("NI".equals(type)) {
					passenger.setCertType("0");
				} else if ("PP".equals(type)) {
					passenger.setCertType("1");
				} else if ("ID".equals(type)) {
					passenger.setCertType("9");
				} else if ("HX".equals(type)) {
					passenger.setCertType("4");
				} else if ("TB".equals(type)) {
					passenger.setCertType("5");
				} else if ("GA".equals(type)) {
					passenger.setCertType("6");
				} else if ("HY".equals(type)) {
					passenger.setCertType("7");
				} else {
					passenger.setCertType("9");
				}
				passenger.setBirthday(passengerVO.getBirthday());
				passenger.setCertNo(passengerVO.getCardNum());
				String ageType = passengerVO.getAgeType();
				if ("0".equals(ageType)) {
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_ADULT);
					passenger.setTicketPrice(qnrVO.getViewPrice());
					passenger.setPnr(qnrVO.getPnr());
					if (WebConstant.FLIGHT_TYPE_ONEWAY.equals(order.getTripType())) {
						passenger.setCabin(firstFlightVO.getCabin());
					} else {
						StringBuilder sb = new StringBuilder();
						for (QnrFlightVO f : flightList) {
							sb.append(f.getCabin()).append("/");
						}
						passenger.setCabin(sb.deleteCharAt(sb.length() - 1).toString());
					}
				} else if ("1".equals(ageType)) {
					passenger.setTicketPrice(qnrVO.getChildPrintPrice());
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_CHILD);
					passenger.setPnr(qnrVO.getCpnr());
					if (WebConstant.FLIGHT_TYPE_ONEWAY.equals(order.getTripType())) {
						passenger.setCabin(firstFlightVO.getCcabin());
					} else {
						StringBuilder sb = new StringBuilder();
						for (QnrFlightVO f : flightList) {
							sb.append(f.getCcabin()).append("/");
						}
						passenger.setCabin(sb.deleteCharAt(sb.length() - 1).toString());
					}
				} else {
					passenger.setTicketPrice(qnrVO.getChildPrintPrice());
					passenger.setPassengerType(WebConstant.PASSENGER_TYPE_BABY);
					passenger.setPnr(qnrVO.getCpnr());
					if (WebConstant.FLIGHT_TYPE_ONEWAY.equals(order.getTripType())) {
						passenger.setCabin(firstFlightVO.getCcabin());
					} else {
						StringBuilder sb = new StringBuilder();
						for (QnrFlightVO f : flightList) {
							sb.append(f.getCcabin()).append("/");
						}
						passenger.setCabin(sb.deleteCharAt(sb.length() - 1).toString());
					}
				}
				if (null != firstFlightVO) {
					passenger.setAirlineCode(firstFlightVO.getCode().substring(0, 2));
					passenger.setFlightNo(firstFlightVO.getCode());
					passenger.setDepCityCode(firstFlightVO.getDep());
					passenger.setArrCityCode(firstFlightVO.getArr());
					passenger.setFlightDepDate(firstFlightVO.getDepDay());
					passenger.setDepTime(firstFlightVO.getDepTime());
					passenger.setArrTime(firstFlightVO.getArrTime());
					passenger.setPrintTicketCabin(firstFlightVO.getCabin());
					order.setFlightNo(firstFlightVO.getCode());
					order.setFlightDate(firstFlightVO.getDepDay());
					order.setAirlineCode(passenger.getAirlineCode());
					order.setDepCityCode(firstFlightVO.getDep());
					order.setArrCityCode(firstFlightVO.getArr());
					order.setCabin(firstFlightVO.getCabin());
				}
				if (WebConstant.PASSENGER_TYPE_ADULT.equals(passenger.getPassengerType())) {
					passenger.setFee(qnrVO.getConstructionFee());
					passenger.setTax(qnrVO.getFuelTax());
				} else {
					passenger.setFee("0");
					passenger.setTax(qnrVO.getChildFuelTax());
				}
				passenger.setSellPrice(passengerVO.getPrice());
				passenger.setPolicyType(StringUtils.trimToEmpty(qnrVO.getPolicyCode()));
				if ("100".equals(qnrVO.getPayStatus())) {// 100表示升级后支付渠道有效
					passenger.setReciptWay(convertPayStatus(qnrVO.getPayChannel()));
				} else {
					passenger.setReciptWay(qnrVO.getPayStatus());// 支付方式（收款方式）
				}
				// 实收金额 = 销售价 + 机建 + 燃油
				passenger.setActualPrice(new BigDecimal(passenger.getSellPrice())
						.add(new BigDecimal(passenger.getFee()).add(new BigDecimal(passenger.getTax()))).toString());
				passenger.setInsureCount(passengerVO.getInsuranceCount());
				pList.add(passenger);
			}
			orderVo.setPassengetList(pList);
			// orderVo.setTotalTicketPrice(totalTicketPrice);
			// orderVo.setTotalTax(totalTax);
			// orderVo.setTravel(travel);

			orderVo.setOrder(order);
		}
		return orderVo;
	}

    public static CloseableHttpClient getHttpClient14() {
        String proxyServer = "14.152.95.93";// ConfigUtils.getParam("uploadProxyIp1");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyServer, 30000),
				new UsernamePasswordCredentials("ff53719", "ff53719"));
		HttpHost proxy = new HttpHost(proxyServer, 30000);
		RequestConfig globalConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout).setProxy(proxy).build();
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig)
				.setDefaultCredentialsProvider(credsProvider).build();

		return build;
	}

    public static CloseableHttpClient getHttpClient12() {
		String proxyServer = "121.201.33.102";// ConfigUtils.getParam("uploadProxyIp1");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyServer, 30000),
				new UsernamePasswordCredentials("ff53719", "ff53719"));
		HttpHost proxy = new HttpHost(proxyServer, 30000);
		RequestConfig globalConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000)
				.setConnectionRequestTimeout(60000).setProxy(proxy).build();
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig)
				.setDefaultCredentialsProvider(credsProvider).build();

		return build;
	}

	public static CloseableHttpClient getHttpClient2() {
		RequestConfig globalConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout).build();
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();

		return build;
	}

	public static String convertPayStatus(String payStatus) {
		if ("WLALIPAY".equals(payStatus) || "ALIPAY".equals(payStatus)) {// 支付宝
			return "12";
		} else if ("BILLPAY".equals(payStatus)) {// 快钱
			return "11";
		} else if ("TENPAY".equals(payStatus)) {// 财付通
			return "13";
		} else if ("UMPAY".equals(payStatus)) {// 联动优势
			return "21";
		} else if ("QUNARPAY".equals(payStatus)) {// 用户余额
			return "24";
		} else if ("UPOPPAY".equals(payStatus)) {// 银联预授权
			return "25";
		} else if ("UPOPCPAY".equals(payStatus)) {// 银联无卡
			return "26";
		} else if ("OFFLINE".equals(payStatus)) {// 线下支付
			return "5";
		} else {
			return "";
		}
	}

	public static String signature(Map<String, String> params) {
		Map<String, String> map = Maps.newTreeMap();
		map.putAll(params);
		String joinParams = Joiner.on("&").withKeyValueSeparator("=").join(map);
		// String joinParams="airChange=AirChange{typeKey='1', flightNum=null,
		// flightDate='2020-07-02', dptTime='10:55', arrTime='13:45',
		// flightNumProtect='9C8810', depAirport='CGQ', arrAirport='CGQ',
		// arrDate='2020-07-02', ensureKey=null, preDptTime='10:55',
		// preArrTime='13:45', preDptDate='2020-07-01', preDptAirPort='CGQ',
		// preArrAirPort='CGQ'}&source=rnf.trade.qunar.com&token=0Ihzc9yRSz$JdNt)&orderNo=rnf200709001224571001";
		System.out.println(joinParams);
		return DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(joinParams.getBytes()).getBytes());
	}

	/**
	 * 导单
	 */
	@Override
	public List<OrderVO> batchImportOrders(String orderSource, String orderShop, String status) throws Exception {
		List<OrderVO> list = new ArrayList<OrderVO>();
		try {
			String source = null;
			if ("2".equals(orderShop)) {
				source = "rnf";
			} else {
				source = "rnb";
			}
			List<Order> orderNoList = getByOrderNo2(null, status, source, true);
			if (orderNoList.size() > 0) {
				for (Order order : orderNoList) {
					if (status1.contains(order.getOrderNo())) {
						continue;
					}
					Thread.sleep(1000);
					OrderVO orderVo = getByOrderNo(order.getOrderNo(), InterfaceConstant.ORDER_SOURCE_QNR, orderShop);

					list.add(orderVo);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return list;
	}

	/**
	 * 把订单状态改为出票中
	 *
	 * @param
	 * @throws Exception
	 */
	public JSONObject updateOrderStatus(String orderNo, String orderShop, boolean isAgain) throws Exception {
		Order order = this.getByOrderNo2(orderNo, "127", orderShop, true).get(0);
		if (order == null) {
			throw new RuntimeException();
		}
		List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
		parameterList.add(new BasicNameValuePair("domain", orderShop + ".trade.qunar.com"));
		parameterList.add(new BasicNameValuePair("ticketType", "1"));
		parameterList.add(new BasicNameValuePair("ticketPlatform_man", ""));
		parameterList.add(new BasicNameValuePair("purchaseManMoney", ""));
		parameterList.add(new BasicNameValuePair("operationNote", ""));
		parameterList.add(new BasicNameValuePair("submitbutton", "ticket-confirm"));
		parameterList.add(new BasicNameValuePair("status", "1"));
		parameterList.add(new BasicNameValuePair("orderNo", order.getOrderNo()));
		parameterList.add(new BasicNameValuePair("id", order.getOrderId().toString()));
		for (int i = 0; i < Integer.valueOf(order.getPassengerCount()); i++) {
			parameterList.add(new BasicNameValuePair("eticketNum", ""));
		}
		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "utf-8");
		HttpPost httpPost = new HttpPost("http://fuwu.qunar.com/flightorder/ordercenter/api/ticket/confirmTicket.json");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		if (StringUtils.isEmpty(rnfCookie.toString()) || rnfCookie.length()==0) {
			Query query = new Query();
            query.addCriteria(Criteria.where("source").is("RNF1"));
			CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
			JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				rnfCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
			}

			Query query2 = new Query();
            query2.addCriteria(Criteria.where("source").is("RNB1"));
			CookieSource cookie2 = mongnTemplate.findOne(query2, CookieSource.class);
			JSONArray jsonArray2 = JSONArray.fromObject(cookie2.getCookie());
			for (int i = 0; i < jsonArray2.size(); i++) {
				JSONObject json = jsonArray2.getJSONObject(i);
				rnbCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
			}
		}
		String qnrCookie = null;
		if ("rnb".equals(orderShop)) {
			qnrCookie = rnbCookie.toString();
		} else {
			qnrCookie = rnfCookie.toString();
		}
		httpPost.setHeader("Cookie", qnrCookie);
		// 先解锁订单，如果需要
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = getHttpClient2();
		try {
			httpPost.setEntity(uefEntity);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				if (!metadata.startsWith("{")) {// cookie过期，重新获取更新
					rnfCookie.setLength(0);
					rnbCookie.setLength(0);
					log.info("TTS 的cookie过期了....");
					if (isAgain) {
						return updateOrderStatus(orderNo, orderShop, false);
					}
				}
				/*
				 * String msg=""; msg =
				 * JSONObject.fromObject(metadata).getString("msg");
				 * log.info("tts订单状态更新出票中,"+msg+orderNo);
				 */
				response.close();
				return JSONObject.fromObject(metadata);
				/*
				 * Order o=orderService.getOrderByOrderNo(orderNo);
				 * if(WebConstant.ORDER_NO_TICKET.equals(o.getStatus())){
				 * orderService.updateStatus(WebConstant.ORDER_PRINT, orderNo);
				 * }
				 */
			}
		} catch (Exception e) {
			/*if (isAgain) {
				return updateOrderStatus(orderNo, orderShop, false);
			}*/
		} finally {
			httpClient.close();
			httpPost.abort();
			if (response != null) {
				response.close();
			}
		}
		return null;
	}

	/**
	 * 获取改签 (6 待处理)
	 *
	 * @param
	 * @throws Exception
	 */
	public JSONObject getChangeOrders(String orderNo, String orderShop, String changeDate) throws Exception {
		/*
		 * Calendar calendar=Calendar.getInstance(); String
		 * orderEndDate=DateFormatUtils.format(calendar.getTime(),
		 * "yyyy-MM-dd"); calendar.add(Calendar.DAY_OF_MONTH, -1); String
		 * orderStartDate=DateFormatUtils.format(calendar.getTime(),
		 * "yyyy-MM-dd");
		 */
		List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
		parameterList.add(new BasicNameValuePair("domain", orderShop + ".trade.qunar.com"));
		if (StringUtils.isNotEmpty(changeDate)) {
			parameterList.add(new BasicNameValuePair("orderStartDate", changeDate));
		}
		parameterList.add(new BasicNameValuePair("orderNo", orderNo));
		parameterList.add(new BasicNameValuePair("flightType", "[1,4, ]"));
		parameterList.add(new BasicNameValuePair("changeType", "[1,2,3]"));
		parameterList.add(new BasicNameValuePair("gqStatus", "[0,1,2,3,4,5,6,7,8,9,]"));
		parameterList.add(new BasicNameValuePair("name", "on"));
		parameterList.add(new BasicNameValuePair("ticketNoStatus", "-1"));
		parameterList.add(new BasicNameValuePair("childDomain", "all"));
		parameterList.add(new BasicNameValuePair("limit", "30"));
		parameterList.add(new BasicNameValuePair("pageIndex", "1"));
		parameterList.add(new BasicNameValuePair("start", "0"));
		parameterList.add(new BasicNameValuePair("lastIndex", "1"));

		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "utf-8");
		HttpPost httpPost = new HttpPost("http://fuwu.qunar.com/gaiqian/ajaxGQOrderList.json");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		if (StringUtils.isEmpty(rnfCookie.toString())) {
			Query query = new Query();
			query.addCriteria(Criteria.where("source").is("RNF1"));
			CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
			JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				rnfCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
			}

			Query query2 = new Query();
			query2.addCriteria(Criteria.where("source").is("RNB1"));
			CookieSource cookie2 = mongnTemplate.findOne(query2, CookieSource.class);
			JSONArray jsonArray2 = JSONArray.fromObject(cookie2.getCookie());
			for (int i = 0; i < jsonArray2.size(); i++) {
				JSONObject json = jsonArray2.getJSONObject(i);
				rnbCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
			}
		}
		String qnrCookie = null;
		if ("rnb".equals(orderShop)) {
			qnrCookie = rnbCookie.toString();
		} else {
			qnrCookie = rnfCookie.toString();
		}
		httpPost.setHeader("Cookie", qnrCookie);
		// 先解锁订单，如果需要
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = getHttpClient2();
		try {
			httpPost.setEntity(uefEntity);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				if (!metadata.startsWith("{")) {// cookie过期，重新获取更新
					rnfCookie.setLength(0);
					rnbCookie.setLength(0);
					log.info("TTS 的cookie过期了....");
					return null;
				}
				JSONObject jsonObj = JSONObject.fromObject(metadata);
				JSONObject dataObj = jsonObj.getJSONObject("data");
				response.close();
				return dataObj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.close();
			httpPost.abort();
			if (response != null) {
				response.close();
			}
		}
		return null;
	}

	/**
	 * 回填票号
	 *
	 * @throws Exception
	 */
	public String verifyTicketNo(OrderVO orderVo) throws Exception {
		String user = null;
		String pass = null;
		String url = null;
		if ("2".equals(orderVo.getOrderShop())) {
			user = DictUtils.getDictCode("order_import_cfgtts_rnf2", "user");
			pass = DictUtils.getDictCode("order_import_cfgtts_rnf2", "pass");
			url = DictUtils.getDictCode("order_import_cfgtts_rnf2", "url");
		} else {
			user = DictUtils.getDictCode("order_import_cfgtts_rnb1", "user");
			pass = DictUtils.getDictCode("order_import_cfgtts_rnb1", "pass");
			url = DictUtils.getDictCode("order_import_cfgtts_rnb1", "url");
		}
		Assert.notNull(user, "user不能为空");

		List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
		paramsList.add(new BasicNameValuePair("username", user));
		paramsList.add(new BasicNameValuePair("password", pass));
		String xmlParam = generateTicketNoXmlParam(orderVo);
		if (StringUtils.isEmpty(xmlParam)) {
			throw new RuntimeException();
		}
		paramsList.add(new BasicNameValuePair("orderdata", xmlParam));
		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(paramsList, "utf-8");
		HttpPost httpPost = new HttpPost(url + "/tts/interface/updateorder.jsp");
		// 先解锁订单，如果需要
		this.unLocked(orderVo.getOrderNo(), "1".equals(orderVo.getOrderShop()) ? "rnb" : "rnf", true);
		CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient12();
		try {
			httpPost.setEntity(uefEntity);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				return metadata;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			httpClient.close();
			httpPost.abort();
			if (response != null) {
				response.close();
			}
		}
		return null;
	}

	/**
	 * 更新票号
	 *
	 * @param orderList
	 */
	public void updateTickNo(List<Order> orderList) {
		for (Order order : orderList) {
			try {
				String orderShop = order.getOrderShop();
				String orderSource = order.getOrderSource();
				String orderNo = order.getOrderNo();
                OrderVO byOrderNo = getByOrderNo(orderNo, orderSource, orderShop);

				if (byOrderNo != null) {
					String status = byOrderNo.getStatus();
					if ("TICKET_OK".equals(status)) {
						List<Passenger> passengetList = byOrderNo.getPassengetList();
						List<Passenger> list2 = new ArrayList<Passenger>();
						for (Passenger p : passengetList) {
							Passenger passenger = new Passenger();
							passenger.setTicketNo(p.getTicketNo());
							passenger.setOrderNo(orderNo);
							passenger.setPrintTicketCabin(p.getCabin());
							passenger.setCertNo(p.getCertNo());
							list2.add(passenger);
						}
						orderService.updateTicketNo(orderNo, list2);
					} else if ("APPLY_4_RETURN_PAY".equals(status)) {
						orderService.updateStatus(WebConstant.ORDER_NOTICK_REFUND, orderNo);
					} else if ("CANCEL_OK".equals(status)) {
						orderService.updateStatus(WebConstant.ORDER_CANCEL, orderNo);
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

    /**
     * 获取订单状态
     * @param orderNo
     * @return
     */
	public String getOrderStatus(String orderNo) {
		String user = null;
		String pass = null;
		String url = null;
		if (orderNo.substring(0, 3).equals("rnf")) {
			user = DictUtils.getDictCode("order_import_cfgtts_rnf2", "user");
			pass = DictUtils.getDictCode("order_import_cfgtts_rnf2", "pass");
			url = DictUtils.getDictCode("order_import_cfgtts_rnf2", "url");
		} else {
			user = DictUtils.getDictCode("order_import_cfgtts_rnb1", "user");
			pass = DictUtils.getDictCode("order_import_cfgtts_rnb1", "pass");
			url = DictUtils.getDictCode("order_import_cfgtts_rnb1", "url");
		}
		Assert.notNull(user, "user不能为空");
		HttpPost httpPost = new HttpPost(url + "/tts/interface/new/orderExportNew");
		CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient12();
		try {
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			paramsList.add(new BasicNameValuePair("type", "exact"));
			paramsList.add(new BasicNameValuePair("orderNo", orderNo));
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(paramsList, "utf-8");
			httpPost.setEntity(uefEntity);
			String status = "";
			for (int i = 0; i < 3; i++) {
			    Thread.sleep(1000);
				response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				Document doc = DocumentHelper.parseText(metadata);
				Element rootElement = doc.getRootElement();
				Element element = rootElement.element("order");
				status = element.attributeValue("status");
				if (StringUtils.isNotEmpty(status)) {
					break;
				}
			}
			return status;
		} catch (Exception e) {
			log.error("获取" + orderNo + "信息异常");
			return null;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e1) {

			}
			httpPost.abort();
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * 获取政策连接
	 *
	 * @param orderNo
	 * @param orderShop
	 * @param isAgain
	 * @return
	 * @throws Exception
	 */
	public String getPolicyLink(String orderNo, String orderShop, boolean isAgain) throws Exception {
		String url = "http://fuwu.qunar.com/flightorder/ordercenter/api/baseorderAndPassenger?orderNo=" + orderNo
				+ "&domain=" + orderShop + ".trade.qunar.com";
		HttpGet http = new HttpGet(url);
		http.setHeader("Accept", "application/json");
		http.setHeader("Accept-Encoding", "gzip, deflate, br");
		http.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		http.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		http.setHeader("Content-Type", "application/x-www-form-urlencoded");
		StringBuilder cookieStr = new StringBuilder();
		Query query = new Query();
		query.addCriteria(Criteria.where("source").is(orderShop.toUpperCase()+"1"));
		CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
		JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			cookieStr.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
		}
		http.setHeader("Cookie", cookieStr.toString());
		CloseableHttpClient httpClient2 = getHttpClient2();
		CloseableHttpResponse response = null;
		try {
			response = httpClient2.execute(http);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				org.jsoup.nodes.Document parse = Jsoup.parse(metadata);

				Elements elementsByClass = parse.getElementsByAttributeValue("data-beacon", "check_rule");
				if (elementsByClass.size() > 0) {
					org.jsoup.nodes.Element element = elementsByClass.get(0);
					String href = element.attr("href");
					String text = element.text();
					return text + "-" + href;
				}
			} else {
				if (isAgain) {
					return getPolicyLink(orderNo, orderShop, false);
				}
			}
		} catch (Exception e) {
			/*if (isAgain) {
				return getPolicyLink(orderNo, orderShop, false);
			}*/

		} finally {
			httpClient2.close();
			http.abort();
			if (response != null) {
				response.close();
			}
		}
		return null;

	}

	public String getPolicyHtml(String orderNo, String url, boolean isAgain) throws Exception {
		HttpGet http = new HttpGet(url.trim());
		http.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		http.setHeader("Accept-Encoding", "gzip, deflate");
		http.setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		http.setHeader("Upgrade-Insecure-Requests", "1");
		http.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		// http.setHeader("Content-Type","application/x-www-form-urlencoded");
		StringBuilder cookieStr = new StringBuilder();
		Query query = new Query();
		query.addCriteria(Criteria.where("source").is(orderNo.substring(0, 3).toUpperCase()+"1"));
		CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
		JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			cookieStr.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
		}
		http.setHeader("Cookie", cookieStr.toString());
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			response = httpClient2.execute(http);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String metadata = EntityUtils.toString(entity);
				return metadata;
			} else {
				if (isAgain) {
					return getPolicyLink(orderNo, orderNo.substring(0, 3), false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (isAgain) {
				return getPolicyLink(orderNo, orderNo.substring(0, 3), false);
			}
		} finally {
			httpClient2.close();
			http.abort();
			if (response != null) {
				response.close();
			}
		}
		return null;

	}

	/**
	 * 获取订单备注
	 *
	 * @param orderNo
	 * @param orderShop
	 * @return
	 * @throws Exception
	 */
	public String getOrderRemark(String orderNo, String orderShop, boolean isAgain) throws Exception {
		String url = "http://fuwu.qunar.com/flightorder/ordercenter/api/baseorderAndPassenger?orderNo=" + orderNo
				+ "&domain=" + orderShop + ".trade.qunar.com";
		HttpGet http = new HttpGet(url);
		http.setHeader("Accept", "application/json");
		http.setHeader("Accept-Encoding", "gzip, deflate, br");
		http.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		http.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		http.setHeader("Content-Type", "application/x-www-form-urlencoded");
		StringBuilder cookieStr = new StringBuilder();
		Query query = new Query();
		query.addCriteria(Criteria.where("source").is(orderShop.toUpperCase()+"1"));
		CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
		JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			cookieStr.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
		}
		http.setHeader("Cookie", cookieStr.toString());
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			response = httpClient2.execute(http);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				org.jsoup.nodes.Document parse = Jsoup.parse(metadata);
				Elements elementsByClass = parse.getElementsByClass("b-remarkinfo red");
				if (elementsByClass.size() > 0) {
					org.jsoup.nodes.Element element = elementsByClass.get(0);
					String text = element.text();
					return text;
				}
			} else {
				if (isAgain) {
					return getOrderRemark(orderNo, orderShop, false);
				}
			}
		} catch (Exception e) {
			if (isAgain) {
				return getOrderRemark(orderNo, orderShop, false);
			}
		} finally {
			http.abort();
			if (response != null) {
				response.close();
			}
			httpClient2.close();
		}
		return null;
	}

	/**
	 * 根据订单号获取订单详情(接口文档的url)
	 *
	 * @param orderNo
	 * @return
	 * @throws UnsupportedEncodingException
	 */
    public OrderVO getByOrderNo(String orderNo, String orderSource, String orderShop)
			throws Exception {
		String user = null;
		String pass = null;
		String url = null;
		if ("2".equals(orderShop)) {
			user = DictUtils.getDictCode("order_import_cfgtts_rnf2", "user");
			pass = DictUtils.getDictCode("order_import_cfgtts_rnf2", "pass");
			url = DictUtils.getDictCode("order_import_cfgtts_rnf2", "url");
		} else {
			user = DictUtils.getDictCode("order_import_cfgtts_rnb1", "user");
			pass = DictUtils.getDictCode("order_import_cfgtts_rnb1", "pass");
			url = DictUtils.getDictCode("order_import_cfgtts_rnb1", "url");
		}
		Assert.notNull(user, "user不能为空");
		HttpPost httpPost = new HttpPost(url + "/tts/interface/new/orderExportNew");
		CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient12();
		try {
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("user", user));
			paramsList.add(new BasicNameValuePair("pass", pass));
			paramsList.add(new BasicNameValuePair("type", "exact"));
			paramsList.add(new BasicNameValuePair("orderNo", orderNo));
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(paramsList, "utf-8");
			httpPost.setEntity(uefEntity);
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                log.info("获取" + orderNo + "信息异常,换代理");
                httpClient.close();
                httpClient=getHttpClient14();
                response = httpClient.execute(httpPost);
            }
            HttpEntity entity = response.getEntity();
            String metadata = EntityUtils.toString(entity);
            //System.out.println(metadata);
            List<QnrOrderVO> qnrOrderVOList = TTsOrderService.xmlToOrderList(metadata);
            if (qnrOrderVOList.size() > 0) {
                QnrOrderVO qnrVo = qnrOrderVOList.get(0);
                OrderVO orderVo = procQnrOrderToOrderVO(qnrVo, orderSource, orderShop);
                return orderVo;
            }
        } catch (Exception e) {
            log.error("获取" + orderNo + "信息异常", e);
		} finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
        return null;

	}

	/**
	 * 航变通知
	 *
	 * @param
	 * @return
	 */
	public String airChangeNotic(JSONObject param) {
		AirChange airChange = new AirChange();
		airChange.setTypeKey(param.getString("changeType"));
		airChange.setFlightNum(param.getString("orgFlightNo"));
		String orgDepTime = param.getString("orgDepTime");
		String orgArrTime = param.getString("orgArrTime");
		String[] orgDepTimeArr = orgDepTime.split(" ");
		String[] orgArrTimeArr = orgArrTime.split(" ");
		airChange.setPreDptDate(orgDepTimeArr[0]);
		airChange.setPreDptTime(orgDepTimeArr[1].substring(0, 5));
		airChange.setPreArrTime(orgArrTimeArr[1].substring(0, 5));
		airChange.setPreArrAirPort(param.getString("orgEndPort"));
		airChange.setPreDptAirPort(param.getString("orgStartPort"));
		String newDepTime = param.getString("newDepTime");
		if (StringUtils.isNotEmpty(newDepTime)) {
			String[] newDepTimeArr = newDepTime.split(" ");
			airChange.setFlightDate(newDepTimeArr[0]);
			airChange.setDptTime(newDepTimeArr[1].substring(0, 5));
		}
		String newArrTime = param.getString("newArrTime");
		if (StringUtils.isNotEmpty(newArrTime)) {
			String[] newArrTimeArr = newArrTime.split(" ");
			airChange.setArrDate(newArrTimeArr[0]);
			airChange.setArrTime(newArrTimeArr[1].substring(0, 5));
		}
		String newFlightNo = param.getString("newFlightNo");
		if ("2".equals(param.getString("changeType"))) {
			if (StringUtils.isNotEmpty(newFlightNo)) {
				airChange.setFlightNumProtect(newFlightNo);
			}
		}
		if (StringUtils.isNotEmpty(param.getString("newStartPort"))) {
			airChange.setDepAirport(param.getString("newStartPort"));
		}
		if (StringUtils.isNotEmpty(param.getString("newEndPort"))) {
			airChange.setArrAirport(param.getString("newEndPort"));
		}
		String orderNo = param.getString("orderNo");

		String signature = signature(ImmutableMap.of("airChange", airChange.toString(), "source", "rnf.trade.qunar.com",
				"token", "0Ihzc9yRSz$JdNt)", "orderNo", orderNo));
		String requsetUrl = "http://ftchange.qunar.com/airchange?";

		String url = requsetUrl + Joiner.on("&").withKeyValueSeparator("=")
				.join(ImmutableMap.of("source", "rnf.trade.qunar.com", "signature", signature, "orderNo", orderNo));
        CloseableHttpClient httpClient2 = getHttpClient12();
		CloseableHttpResponse response = null;
		try {
			/*
			 * String result = HttpUtil4.postJsonWithWrapperReuslt(url,
			 * ObjectMapperUtil.writeObjectToJson(airChange)).result;
			 */
			HttpPost httpPost = new HttpPost(url);
			JSONObject json = JSONObject.fromObject(airChange);
			StringEntity se = new StringEntity(json.toString(), "utf-8");
			httpPost.setEntity(se);
			httpPost.setHeader("Content-Type", "application/json");
			response = httpClient2.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
			log.info("去哪儿航变录入返回" + metadata);
			return metadata;
		} catch (Exception e) {
			log.warn("parse exception {}", e);
		} finally {
			try {
				response.close();
				httpClient2.close();
			} catch (IOException e) {

			}
		}
		return null;
	}


	public JSONObject confirmRefund(String retNo, String refundPrice,String orderShop ) throws Exception {
        CloseableHttpClient httpClient2 = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost("http://fuwu.qunar.com/refund/waitrefund/refundpayok");
        try {
            List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
            parameterList.add(new BasicNameValuePair("domain", orderShop + ".trade.qunar.com"));
            parameterList.add(new BasicNameValuePair("orderNo", retNo));
            parameterList.add(new BasicNameValuePair("refundPrice", refundPrice));
            parameterList.add(new BasicNameValuePair("refundGoInsurance", "false"));
            parameterList.add(new BasicNameValuePair("refundBackInsurance", "false"));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "utf-8");
            httpPost.setEntity(uefEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpPost.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            String qnrCookie = null;
            if (StringUtils.isEmpty(rnfCookie.toString())) {
                Query query = new Query();
                query.addCriteria(Criteria.where("source").is("RNF1"));
                CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
                JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    rnfCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
                }

                Query query2 = new Query();
                query2.addCriteria(Criteria.where("source").is("RNB1"));
                CookieSource cookie2 = mongnTemplate.findOne(query2, CookieSource.class);
                JSONArray jsonArray2 = JSONArray.fromObject(cookie2.getCookie());

                for (int i = 0; i < jsonArray2.size(); i++) {
                    JSONObject json = jsonArray2.getJSONObject(i);
                    rnbCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
                }
            }
            if ("rnb".equals(orderShop)) {
                qnrCookie = rnbCookie.toString();
            } else {
                qnrCookie = rnfCookie.toString();
            }
            httpPost.setHeader("Cookie", qnrCookie);
            httpPost.setEntity(uefEntity);
            response = httpClient2.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String metadata = EntityUtils.toString(entity);
                return JSONObject.fromObject(metadata);
            } else {
               return  null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpPost.abort();
            if (response != null) {
                response.close();
            }
            httpClient2.close();
        }
        return  null;
    }

    /**
     *提交确认退款信息
     * @param refund
     * @return
     * @throws Exception
     */
    public JSONObject submitInfoChecked(Refund refund) throws Exception {
        CloseableHttpClient httpClient2 = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost("http://fuwu.qunar.com/refund/refundconfirm/submitInfoLocked");
        try {
            String orderNo = refund.getOrderNo();
            String orderShop=orderNo.substring(0,3);
            String cRealPrice = refund.getcRealPrice().toString();
            List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
            parameterList.add(new BasicNameValuePair("domain", orderShop + ".trade.qunar.com"));
            parameterList.add(new BasicNameValuePair("ticketNos", refund.getTicketNo()));
            parameterList.add(new BasicNameValuePair("ticketStatus", "2"));
            parameterList.add(new BasicNameValuePair("remark", ""));
            parameterList.add(new BasicNameValuePair("orderNo", refund.getRetNo()));
            parameterList.add(new BasicNameValuePair("confirmRefundAmount", cRealPrice.split("\\.")[0]));
            parameterList.add(new BasicNameValuePair("refundCauseType", "16"));
            parameterList.add(new BasicNameValuePair("goJourneyOptional", "false"));
            parameterList.add(new BasicNameValuePair("backJourneyOptional", "false"));
            parameterList.add(new BasicNameValuePair("confirmAmountType", "zhengce"));
            parameterList.add(new BasicNameValuePair("confirmAmountRemark", ""));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "utf-8");
            httpPost.setEntity(uefEntity);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate");
            httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpPost.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.setHeader("Host", "fuwu.qunar.com");
            httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.setHeader("Origin:", "http://fuwu.qunar.com");
            String qnrCookie = null;
            if (StringUtils.isEmpty(rnfCookie.toString())) {
                Query query = new Query();
                query.addCriteria(Criteria.where("source").is("RNF1"));
                CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
                JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    rnfCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
                }

                Query query2 = new Query();
                query2.addCriteria(Criteria.where("source").is("RNB1"));
                CookieSource cookie2 = mongnTemplate.findOne(query2, CookieSource.class);
                JSONArray jsonArray2 = JSONArray.fromObject(cookie2.getCookie());

                for (int i = 0; i < jsonArray2.size(); i++) {
                    JSONObject json = jsonArray2.getJSONObject(i);
                    rnbCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
                }
            }
            if ("rnb".equals(orderShop)) {
                qnrCookie = rnbCookie.toString();
            } else {
                qnrCookie = rnfCookie.toString();
            }
            httpPost.setHeader("Cookie", qnrCookie);
            httpPost.setEntity(uefEntity);
            response = httpClient2.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String metadata = EntityUtils.toString(entity);
                return JSONObject.fromObject(metadata);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpPost.abort();
            if (response != null) {
                response.close();
            }
            httpClient2.close();
        }
        return  null;
    }

	/**
	 * 根据订单号查询订单列表(平台上抓的接口)127
	 *
	 * @param orderNo
	 * @param status
	 * @throws Exception
	 */
	public List<Order> getByOrderNo2(String orderNo, String status, String orderShop, boolean isAgain)
			throws Exception {
		List<Order> list = new ArrayList<Order>();
		List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		String orderEndDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
		calendar.add(Calendar.DAY_OF_MONTH, -3);
		String orderStartDate = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
		parameterList.add(new BasicNameValuePair("domain", orderShop + ".trade.qunar.com"));
		parameterList.add(new BasicNameValuePair("menuUrl", "/orderadmin/order/ordermanage/query"));
		parameterList.add(new BasicNameValuePair("orderStartDate", orderStartDate));
		parameterList.add(new BasicNameValuePair("orderEndDate", orderEndDate));
		parameterList.add(new BasicNameValuePair("special", "0"));
		parameterList.add(new BasicNameValuePair("contactMobile", ""));
		parameterList.add(new BasicNameValuePair("passengerName", ""));
		if (StringUtils.isNotEmpty(orderNo)) {
			parameterList.add(new BasicNameValuePair("orderNo", orderNo));
		}
		parameterList.add(new BasicNameValuePair("ticketNo", ""));
		parameterList.add(new BasicNameValuePair("pnr", ""));
		parameterList.add(new BasicNameValuePair("status", status));
		parameterList.add(new BasicNameValuePair("flightStartDate", ""));
		parameterList.add(new BasicNameValuePair("flightEndDate", ""));
		parameterList.add(new BasicNameValuePair("flightNo", ""));
		parameterList.add(new BasicNameValuePair("orderUrgentLevel", "127"));
		parameterList.add(new BasicNameValuePair("flightSearchType", "2"));
		parameterList.add(new BasicNameValuePair("limit", "100"));
		parameterList.add(new BasicNameValuePair("pageIndex", "1"));
		parameterList.add(new BasicNameValuePair("start", "0"));
		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "utf-8");
		HttpPost httpPost = new HttpPost("http://fuwu.qunar.com/orderadmin/order/ordermanage/orderlist");
		httpPost.setEntity(uefEntity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		StringBuilder qnrCookie = new StringBuilder();

		Query query = new Query();
		query.addCriteria(Criteria.where("source").is(orderShop.toUpperCase() + "1"));
		CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
		JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			qnrCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
		}

		httpPost.setHeader("Cookie", qnrCookie.toString());
		CloseableHttpClient httpClient2 = getHttpClient2();
		CloseableHttpResponse response = null;
		try {
			httpPost.setEntity(uefEntity);
			response = httpClient2.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
                if (!metadata.startsWith("{")) {// cookie过期，重新获取更新
					rnfCookie.setLength(0);
					rnbCookie.setLength(0);
					log.info("TTS 的cookie过期了....");
					if (isAgain) {
						return getByOrderNo2(orderNo, status, orderShop, false);
					}
				}
				JSONObject json = JSONObject.fromObject(metadata);
				if (json.getBoolean("ret")) {
					JSONArray jsonArr = json.getJSONObject("data").getJSONArray("orderList");
					for (int i = 0; i < jsonArr.size(); i++) {
						JSONObject orderJson = jsonArr.getJSONObject(i);
						Order order = new Order();
						order.setOrderId(Long.valueOf(orderJson.getString("id")));
						order.setOrderNo(orderJson.getString("orderNo"));
						order.setPassengerCount(orderJson.getString("passengerCount"));
						order.setRemark(orderJson.getString("lockMan"));
						list.add(order);
					}
				}
				response.close();
				return list;
			}
		} catch (Exception e) {
			if (isAgain) {
				Thread.sleep(5000);
				return getByOrderNo2(orderNo, status, orderShop, false);
			}
			throw e;
		} finally {
			httpClient2.close();
			httpPost.abort();
			if (response != null) {
				response.close();
			}
		}
		return list;
	}

	/**
	 * 平台解锁
	 * @param orderNo
	 * @param orderShop
	 * @param isAgain
	 * @return
	 * @throws Exception
	 */
	public String unLocked(String orderNo, String orderShop, boolean isAgain) throws Exception {
		// http://fuwu.qunar.com/orderadmin/unlockorder/forceUnlock?orderNo="+orderNo+"&domain="+orderNo.substring(0,
		// 3)+".trade.qunar.com");
		HttpGet httpGet = new HttpGet("http://fuwu.qunar.com/orderadmin/unlockorder/forceUnlock?orderNo=" + orderNo
				+ "&domain=" + orderShop + ".trade.qunar.com");
		CloseableHttpClient httpClient2 = getHttpClient2();
		CloseableHttpResponse response = null;
		try {
			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
			String qnrCookie = null;
			synchronized (this) {
				if (StringUtils.isEmpty(rnfCookie.toString())) {
					Query query = new Query();
                    query.addCriteria(Criteria.where("source").is("RNF1"));
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject json = jsonArray.getJSONObject(i);
						rnfCookie.append(json.getString("name")).append("=").append(json.getString("value"))
								.append(";");
					}
					Query query2 = new Query();
                    query2.addCriteria(Criteria.where("source").is("RNB1"));
					CookieSource cookie2 = mongnTemplate.findOne(query2, CookieSource.class);
					JSONArray jsonArray2 = JSONArray.fromObject(cookie2.getCookie());
					for (int i = 0; i < jsonArray2.size(); i++) {
						JSONObject json = jsonArray2.getJSONObject(i);
						rnbCookie.append(json.getString("name")).append("=").append(json.getString("value"))
								.append(";");
					}
				}
			}
			if ("rnb".equals(orderShop)) {
				qnrCookie = rnbCookie.toString();

			} else {
				qnrCookie = rnfCookie.toString();
			}
			httpGet.setHeader("Cookie", qnrCookie.toString());
			response = httpClient2.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				return metadata;
			} else {
				if (isAgain) {
					rnfCookie.setLength(0);
					rnbCookie.setLength(0);
					return unLocked(orderNo, orderShop, false);
				}
			}
		} catch (Exception e) {
			rnfCookie.setLength(0);
			rnbCookie.setLength(0);
			if (isAgain) {
				return unLocked(orderNo, orderShop, false);
			}
		} finally {
			httpClient2.close();
			httpGet.abort();
			if (response != null) {
				response.close();
			}
		}
		return null;
	}

	/**
	 * 生成回填票号需要的xml参数
	 *
	 * @param orderVo
	 * @return
	 */
	public String generateTicketNoXmlParam(OrderVO orderVo) {
		if (orderVo == null) {
			return null;
		}
		Document doc = DocumentHelper.createDocument();
		Element rootElement = doc.addElement("OrderList");
		Element orderElement = rootElement.addElement("OrderDetail");
		orderElement.addAttribute("status", "2");
		orderElement.addAttribute("no", orderVo.getOrderNo().trim());
		orderElement.addAttribute("errorCode", "");
		orderElement.addAttribute("errorMsg", "");
		List<Passenger> passengerList = orderVo.getPassengetList();
		for (Passenger passenger : passengerList) {
			Element passengerElement = orderElement.addElement("passenger");
			passengerElement.addAttribute("name", passenger.getName().trim());
			passengerElement.addAttribute("no", passenger.getTicketNo().trim());
			passengerElement.addAttribute("cano", passenger.getCertNo().trim());
		}
		Element issueticket = orderElement.addElement("issueticket");
		issueticket.addAttribute("ticketType", "1");// 手工出票
		issueticket.addAttribute("ticketPlatform", "");
		issueticket.addAttribute("purchasePrice", "");
		return doc.asXML();
	}

	/**
	 * 平台加锁
	 *
	 * @param orderNo
	 * @param orderShop
	 * @param isAgain
	 *            遇到异常是否重试
	 * @return
	 */
	public JSONObject locked(String orderNo, String orderShop, boolean isAgain) {
		HttpPost httpPost = new HttpPost("http://fuwu.qunar.com/modules/lockcluster/lock");
		CloseableHttpResponse response = null;
		try {
			List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
			parameterList.add(new BasicNameValuePair("domain", orderShop + ".trade.qunar.com"));
			parameterList.add(new BasicNameValuePair("orderNo", orderNo));
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "utf-8");
			httpPost.setEntity(uefEntity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpPost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			String qnrCookie = null;
			synchronized (this) {
				if (StringUtils.isEmpty(rnfCookie.toString())) {
					Query query = new Query();
					query.addCriteria(Criteria.where("source").is("RNF1"));
					CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
					JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject json = jsonArray.getJSONObject(i);
						rnfCookie.append(json.getString("name")).append("=").append(json.getString("value"))
								.append(";");
					}
					Query query2 = new Query();
					query2.addCriteria(Criteria.where("source").is("RNB1"));
					CookieSource cookie2 = mongnTemplate.findOne(query2, CookieSource.class);
					JSONArray jsonArray2 = JSONArray.fromObject(cookie2.getCookie());
					for (int i = 0; i < jsonArray2.size(); i++) {
						JSONObject json = jsonArray2.getJSONObject(i);
						rnbCookie.append(json.getString("name")).append("=").append(json.getString("value"))
								.append(";");
					}
				}
			}
			if ("rnb".equals(orderShop)) {
				qnrCookie = rnbCookie.toString();

			} else {
				qnrCookie = rnfCookie.toString();
			}
			httpPost.setHeader("Cookie", qnrCookie.toString());
			response = getHttpClient2().execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				JSONObject json = JSONObject.fromObject(metadata);
				return json;
			} else {
                rnfCookie.setLength(0);
                rnbCookie.setLength(0);
			}
		} catch (Exception e) {
			rnfCookie.setLength(0);
			rnbCookie.setLength(0);
			/*if (isAgain) {
				return locked(orderNo, orderShop, false);
			} else {
				try {
					SendQQMsgUtil.send("TTS的cookie过期了");
				} catch (Exception e1) {

				}
			}*/
		} finally {
			httpPost.abort();
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 获取退款
	 * 
	 * @param orderNo
	 * @param orderShop
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getRefundMoney(String orderNo, String orderShop) throws Exception {
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		Map<String, String> map = new HashMap<>();
		HttpPost http = new HttpPost("http://fuwu.qunar.com/refund/router/refund");
		try {
			List<NameValuePair> parameterList = new ArrayList<>();
			parameterList.add(new BasicNameValuePair("domain", orderShop + ".trade.qunar.com"));
			parameterList.add(new BasicNameValuePair("orderNo", orderNo));
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "utf-8");
			http.setEntity(uefEntity);
			http.setHeader("Accept", "application/json");
			http.setHeader("Accept-Encoding", "gzip, deflate, br");
			http.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			http.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			http.setHeader("Content-Type", "application/x-www-form-urlencoded");
			StringBuilder cookieStr = new StringBuilder();
			Query query = new Query();
			query.addCriteria(Criteria.where("source").is(orderShop.toUpperCase()+"1"));
			CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
			JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				cookieStr.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
			}
			http.setHeader("Cookie", cookieStr.toString());
			http.setEntity(uefEntity);
            response = httpClient2.execute(http);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				// System.out.println(metadata);
				org.jsoup.nodes.Document parse = Jsoup.parse(metadata);
				org.jsoup.nodes.Element elementById = parse.getElementById("js_should_refund_fee");
				String text = elementById.text();
				Pattern p = Pattern.compile("[^\\d]+([\\d]+)[^\\d]+.*");
				Matcher m = p.matcher(text);
				boolean result = m.find();
				String find_result = null;
				if (result) {
					find_result = m.group(1);
				}
				map.put("refundMoney", find_result);
				org.jsoup.nodes.Element refundTypeE = parse.getElementById("js_refund_type");
				String refundType = refundTypeE.text();
				map.put("refundType", refundType);

			}
		} catch (Exception e) {
			log.error("tts获取退票费，申请类型异常" + orderNo);
		} finally {
			httpClient2.close();
			http.abort();
			if (response != null) {
				response.close();
			}
		}
		return map;
	}

	/**
	 * 获取销售规则
	 *
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	public String getSellRole(String orderNo, boolean isAgain) throws Exception {
		String url = "http://fuwu.qunar.com/refund/vm/servicestandard?orderNo=" + orderNo + "&domain="
				+ orderNo.substring(0, 3) + ".trade.qunar.com";
		HttpGet http = new HttpGet(url);
		http.setHeader("Accept", "application/json");
		http.setHeader("Accept-Encoding", "gzip, deflate, br");
		http.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		http.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		http.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		http.setHeader("Content-Type", "application/x-www-form-urlencoded");
		StringBuilder cookieStr = new StringBuilder();
		Query query = new Query();
		query.addCriteria(Criteria.where("source").is(orderNo.substring(0, 3).toUpperCase()+"1"));
		CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
		JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			cookieStr.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
		}
		http.setHeader("Cookie", cookieStr.toString());
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient2 = getHttpClient2();
		try {
			response = httpClient2.execute(http);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				response.close();
				return metadata;
			} else {
			}
		} catch (Exception e) {
		} finally {
			httpClient2.close();
			http.abort();
			if (response != null) {
				response.close();
			}
		}
		return null;
	}


	/**
	 * 同步改签单
	 * 
	 * @param platform
	 *            平台
	 * @param date
	 *            日期yyyy-MM-dd
	 * @throws Exception
	 */
	public void sysncChangeOrder(String platform, String date) throws Exception {
		List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
		parameterList.add(new BasicNameValuePair("domain", platform + ".trade.qunar.com"));
		parameterList.add(new BasicNameValuePair("orderStartDate", date));
		parameterList.add(new BasicNameValuePair("flightType", "[1,3,4]"));
		parameterList.add(new BasicNameValuePair("changeType", "[1,2,3]"));
		parameterList.add(new BasicNameValuePair("gqStatus", "[3,7]"));
		parameterList.add(new BasicNameValuePair("ticketNoStatus", "-1"));
		parameterList.add(new BasicNameValuePair("childDomain", "all"));
		parameterList.add(new BasicNameValuePair("limit", "30"));
		parameterList.add(new BasicNameValuePair("pageIndex", "1"));
		parameterList.add(new BasicNameValuePair("start", "0"));
		parameterList.add(new BasicNameValuePair("lastIndex", "1"));

		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "utf-8");
		HttpPost httpPost = new HttpPost("http://fuwu.qunar.com/gaiqian/ajaxGQOrderList.json");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		if (StringUtils.isEmpty(rnfCookie.toString())) {
			Query query = new Query();
			query.addCriteria(Criteria.where("source").is("RNF1"));
			CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
			JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				rnfCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
			}

			Query query2 = new Query();
			query2.addCriteria(Criteria.where("source").is("RNB1"));
			CookieSource cookie2 = mongnTemplate.findOne(query2, CookieSource.class);
			JSONArray jsonArray2 = JSONArray.fromObject(cookie2.getCookie());
			for (int i = 0; i < jsonArray2.size(); i++) {
				JSONObject json = jsonArray2.getJSONObject(i);
				rnbCookie.append(json.getString("name")).append("=").append(json.getString("value")).append(";");
			}
		}
		String qnrCookie = null;
		String source = "2";
		if ("rnb".equals(platform)) {
			qnrCookie = rnbCookie.toString();
			source = "1";
		} else {
			qnrCookie = rnfCookie.toString();
		}
		httpPost.setHeader("Cookie", qnrCookie);
		// 先解锁订单，如果需要
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = getHttpClient2();
		try {
			httpPost.setEntity(uefEntity);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String metadata = EntityUtils.toString(entity);
				if (!metadata.startsWith("{")) {// cookie过期，重新获取更新
					rnfCookie.setLength(0);
					rnbCookie.setLength(0);
					log.info("TTS 的cookie过期了....");
					throw new Exception("TTS 的cookie过期了....");
				}
				JSONObject jsonObj = JSONObject.fromObject(metadata);
				JSONObject data = jsonObj.getJSONObject("data");
				JSONArray orderList = data.getJSONArray("orderList");
				List<Change> list = new ArrayList<>();
				for (int i = 0; i < orderList.size(); i++) {
                    JSONObject obj = orderList.getJSONObject(i);
                    if (obj.getInt("gqStatus") == 3) {// 乘客已支付
                        String gqId = obj.getString("gqId");
                        if(status5.contains(gqId)){
                            continue;
                        }
                        JSONObject flight = obj.getJSONArray("flightSegmentList").getJSONObject(0);
                        JSONArray passengerList = obj.getJSONArray("ttsPassengerList");
                        String orderNo = obj.getString("orderNo");

                        // 获取订单信息
                        Order order = orderService.getOrderBycOrderNo(orderNo);
                        if (order == null) { // 根据父订单号查询订单信息
                            OrderVO orderVo = this.getByOrderNo(orderNo, null, source);
                        	orderNo = orderVo.getOrder().getRemarkStr();
                        	log.info("根据父订单号【"+orderNo+"】查询订单信息");
                        	order = orderService.getOrderBycOrderNo(orderNo);
                        	if (order == null) {
                        		log.info("根据父订单号【"+orderNo+"】查询订单信息为空..");
                        	}
                        }
                        log.info("根据订单号【"+orderNo+"】查询订单信息，改签ID【"+gqId+"】");
                        // 收入改签费
                        String revenuePrice = new BigDecimal(obj.getString("allPrices")).divide(new BigDecimal(passengerList.size())).toString();
                        for (int j = 0; j < passengerList.size(); j++) {
                            JSONObject passenger = passengerList.getJSONObject(j);
                            Change change = new Change();

                            change.setPassengerName(passenger.getString("name"));
                            change.setRevenuePrice(revenuePrice);
                            change.setState(WebConstant.CHANGE_UNTREATED);

                            change.setOrderNo(orderNo);
                            change.setNewCOrderNo(gqId);

                            change.setOrderId(order.getOrderId());
                            change.setOrderShop(order.getOrderShop());
                            change.setOrderSource(order.getOrderSource());
                            change.setsAirlineCode(order.getFlightNo().substring(0, 2));

                            change.setsFlightNo(order.getFlightNo());
                            change.setFlightNo(flight.getString("flightNum"));

                            change.setsArrCityCode(order.getArrCityCode());
                            change.setsDepCityCode(order.getDepCityCode());

                            change.setsFlightDate(order.getFlightDate());
                            change.setFlightDate(flight.getString("departureDay"));

                            change.setsCabin(order.getCabin());
                            if (StringUtils.isNotEmpty(flight.getString("cabin")) && !"null".equals(flight.getString("cabin"))) {
                                change.setCabin(flight.getString("cabin"));
                            }

                            change.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            change.setChangeDate(obj.getString("createTimeStr"));
                            change.setCreateBy("SYSTEM");

                            list.add(change);
                        }
                        status5.offer(gqId);
                    }
                }
				changeService.saveChanges(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.close();
			httpClient.close();
			httpPost.abort();
			if (response != null) {
				response.close();
			}
		}
	}

    public PPMSG getPPMSG(OrderVO orderVO,String ticketPrice){
        PPMSG p = new PPMSG();
        try{
            QueryWrapper<Flight> query=new QueryWrapper<Flight>();
            query.eq("order_id", orderVO.getOrderId());
            List<Flight> selectFlightInfo = flightService.getByQueryW(query);
            Flight flight = selectFlightInfo.get(0);
            p.setFlightDate(flight.getFlightDepDate());
            p.setFlightNo(flight.getFlightNo());
            p.setFromCity(flight.getDepCityCode());
            p.setFromDatetime(flight.getFlightDepDate()+" "+flight.getDepTime());
            p.setPnr(orderVO.getPnr());
            p.setBigPnr(orderVO.getBigPnr());
            p.setTicketPrice(ticketPrice);
            p.setToCity(flight.getArrCityCode());
            p.setToDatetime(flight.getFlightDepDate()+" "+flight.getArrTime());
            StringBuffer name = new StringBuffer();
            StringBuffer certNo = new StringBuffer();
            StringBuffer passengerType = new StringBuffer();
            StringBuffer cardType = new StringBuffer();
            for(Passenger qp:orderVO.getPassengetList()){
                if(!qp.getPassengerType().equals("0")){
                    return null;
                }
                name.append("|"+qp.getName());
                certNo.append("|"+qp.getCertNo());
                passengerType.append("|"+1);
                String type = qp.getCertType();
                if(type.equals("1")){
                    type = "PP";
                }else if(type.equals("0")){
                    type = "NI";
                }else {
                    type = "ID";
                }
                cardType.append("|"+type);
            }
            p.setPassengerName(name.toString().substring(1));
            p.setPassengerCard(certNo.toString().substring(1));
            p.setPassengerType(passengerType.toString().substring(1));
            p.setCardType(cardType.toString().substring(1));
            p.setSeatClass(flight.getCabin());
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return p;
    }


}
