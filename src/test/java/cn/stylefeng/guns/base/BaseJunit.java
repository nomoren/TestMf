package cn.stylefeng.guns.base;

import cn.GunsApplication;
import cn.ssq.ticket.system.OrderBatchImportSchedule.*;
import cn.ssq.ticket.system.entity.OrderVO;
import cn.ssq.ticket.system.entity.PPRefund;
import cn.ssq.ticket.system.entity.Refund;
import cn.ssq.ticket.system.entity.SHSL.AskPriceDTO;
import cn.ssq.ticket.system.entity.importBean.CtripBean.CtripOrderVO;
import cn.ssq.ticket.system.entity.woNiu.*;
import cn.ssq.ticket.system.mapper.*;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.service.OrderImport.impl.*;
import cn.ssq.ticket.system.util.*;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TopAuthTokenCreateRequest;
import com.taobao.api.response.TopAuthTokenCreateResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 基础测试类
 *
 * @author stylefeng
 * @Date 2017/5/21 16:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GunsApplication.class)
@WebAppConfiguration
//@Transactional //打开的话测试之后数据可自动回滚
public class BaseJunit {

	@Value("${uploadProxyIp}")
	String proxyServer;

	@Autowired
	WebApplicationContext webApplicationContext;

	/*@Autowired
	TuNiuSchedule importSchedule;*/
	/*@Autowired
	TTsSchednule ttsSchedule;
*/
	@Autowired
	ChangeService changes;

	@Autowired
	OrderService orderService;
	@Autowired
	PassengreMapper passengreMapper;
	@Autowired
	FlightMapper flightMapper;
	/*@Autowired
	TongChengSchednule tcSch;*/
	@Autowired
	TcOrderService tc;
	@Autowired
	OrderMapper orderMapper;



	@Autowired
	OtherSchedule otherSchedule;
	@Autowired
	UserMapper oMapper;

	@Autowired
	MongoTemplate mongnTemplate;

	@Autowired
	PurchaseMapper purchMapper;

	@Autowired
	TuniuOrderService tuniuOrderService;
	@Autowired
    TuNiuSchedule tuNiuSchedule;
	@Autowired
	JiuSchednule jius;
	@Autowired
	TuniuOrderService tuniu;

	@Autowired
	TTsOrderService tts;

	@Autowired
	TTsSchednule ttss;

	@Autowired
	PurchaseService purchService;
	@Autowired
	CtripOrderService ct;
	@Autowired
	CtripSchednule cs;
	@Autowired
	TbOrderService tb;
	@Autowired
	DictService dict;
	@Autowired
	JiuOrderService jiuOrder;
	@Autowired
	TuNiuSchedule tnS;

	@Autowired
	TongChengSchednule tcs;

	@Autowired
    TcOrderService tcOrderService;
	@Autowired
	PassengreMapper pm;
	protected MockMvc mockMvc;

	@Autowired
	private TaoBaoSchednule tbs;

	@Autowired
	private RefundMapper refundMapper;

/*
	@Autowired
    private RedisTemplate<Object,Object> redisTemplate;*/

	@Before
	public void setupMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}



	@Autowired
    AskPriceService askPriceService;

	@Autowired
    RestTemplate restTemplate;

	@Autowired
    RefundService refundService;

	@Autowired
    PassengerService passengerService;


    @Test
    public void  ttCc(){
        //qianyue:{"code":0,"message":"SUCCESS","createTime":1616497480837,"result":{"bankCode":"ALIPAY_SN","charSet":"UTF-8","errCode":0,"errMsg":"请求成功","finishSignTime":"","retHtml":"PCFET0NUWVBFIGh0bWwgUFVCTElDICItLy9XM0MvL0RURCBIVE1MIDQuMDEgVHJhbnNpdGlvbmFsLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL1RSL2h0bWw0L2xvb3NlLmR0ZCI+CjxodG1sPgo8aGVhZD4KPG1ldGEgaHR0cC1lcXVpdj0iQ29udGVudC1UeXBlIiBjb250ZW50PSJ0ZXh0L2h0bWw7IGNoYXJzZXQ9VVRGLTgiIC8+Cjx0aXRsZT5iYW5rPC90aXRsZT4KPC9oZWFkPgo8Ym9keT4KPGZvcm0gaWQgPSAicWZvcm0iIG5hbWU9InFmb3JtIiBtZXRob2Q9InBvc3QiIGFjdGlvbj0iaHR0cHM6Ly9tYXBpLmFsaXBheS5jb20vZ2F0ZXdheS5kbyI+CjxpbnB1dCB0eXBlPSJoaWRkZW4iIG5hbWU9InNlcnZpY2UiIGlkPSJzZXJ2aWNlIiB2YWx1ZT0ic2lnbl9wcm90b2NvbF93aXRoX3BhcnRuZXIiLz4KPGlucHV0IHR5cGU9ImhpZGRlbiIgbmFtZT0ic2lnbiIgaWQ9InNpZ24iIHZhbHVlPSI4MWNkYTFkMDZkMWI1MjEwMDlhOGIxMDU2YzFmYTQ3MSIvPgo8aW5wdXQgdHlwZT0iaGlkZGVuIiBuYW1lPSJzaWduX3R5cGUiIGlkPSJzaWduX3R5cGUiIHZhbHVlPSJNRDUiLz4KPGlucHV0IHR5cGU9ImhpZGRlbiIgbmFtZT0iZW1haWwiIGlkPSJlbWFpbCIgdmFsdWU9InNzcWx5Nzc3QDE2My5jb20iLz4KPGlucHV0IHR5cGU9ImhpZGRlbiIgbmFtZT0iX2lucHV0X2NoYXJzZXQiIGlkPSJfaW5wdXRfY2hhcnNldCIgdmFsdWU9IlVURi04Ii8+CjxpbnB1dCB0eXBlPSJoaWRkZW4iIG5hbWU9InBhcnRuZXIiIGlkPSJwYXJ0bmVyIiB2YWx1ZT0iMjA4ODIwMTM0NjQ1NzUyMSIvPgo8L2Zvcm0+CjxzY3JpcHQ+ZG9jdW1lbnQucWZvcm0uc3VibWl0KCk7PC9zY3JpcHQ+CjwvYm9keT4KPC9odG1sPgo=","signStatus":"ACCEPT","signedAccount":"ssqly777@163.com"}}
//        SingQuery sign=new SingQuery();
//        sign.setBankCode("ALIPAY_SN");
//        sign.setSignedAccount("ssqly777@163.com");
//        WoNiuUtil.signq(sign);
        OrderVO orderVO = orderService.selectOrderDetailsAuto("1110586024615");
        WoNiuUtil.createAndPay(orderVO,100);

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	@Test
	public void  ttC(){

        try {

            PayValidate validate=new PayValidate();
            validate.setBankCode("ALIPAY");
            validate.setPmCode("OUTDAIKOU");
            validate.setOrderNo("sub210323213952816");
            WoNiuUtil.payValidate(validate);

            PayParam payParam=new PayParam();
            payParam.setOrderNo("sub210323213952816");
            payParam.setBankCode("ALIPAY");
            payParam.setPmCode("OUTDAIKOU");
            payParam.setPaymentMerchantCode(WoNiuUtil.ppm);
            payParam.setCurId("CNY");
            payParam.setBgRetUrl(WoNiuUtil.regUrl);
            PayInfo payInfo = WoNiuUtil.pay(payParam);


	        //otherSchedule.syncFlightStatus();

            //String avStatus = CabinUtilA.getAvStatus("XIY", "HFE","2021-03-19", "2220", "KY9281","L");
           // System.out.println(avStatus);

            //tuNiuSchedule.importTuniuOrder2();
            //otherSchedule.syncFlightStatus();

            //JSONArray zh9322 = FCZutil.getFlightDataByFlightNo("ZH8358", "2021-01-13","CGQ ","CGO");
            //System.out.println(zh9322);

            //JSONArray flightData = FCZutil.getFlightData("KHN", "SZX", "2021-01-18");
            //System.out.println(flightData);
            //otherSchedule.syncFlightStatus();
	       //  Refund refund = refundMapper.selectById("116070");
         //  refundService.autoCreateRefund(refund);

	      /*
           JSONObject jsonObject = tts.submitInfoChecked(refund);
            System.out.println(jsonObject);
            JSONObject s = tts.confirmRefund(refund.getRetNo(), refund.getcRealPrice().toString().split("\\.")[0],refund.getOrderNo().substring(0, 3));
            System.out.println(s);*/
            //{"ret":true,"errorMessage":null,"throwable":null,"data":null}


          /*  JSONObject rx20210102155649RERX5J9 = tcOrderService.confirmRefund("RX20210102155649RERX5J9", 1);
            System.out.println(rx20210102155649RERX5J9);*/
//            {"isSuccess":true,"errorCode":0,"errorMessage":null}
           /* JSONObject str = ct.confirmRefund("100150622", "1");
            System.out.println(str);*/
            // otherSchedule.commitRefundTicket();
	        //tts.getByOrderNo("rnf210104170015048001","1","2",false);
           // ttss.importTTsOrderRnf();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

        //拼接完整的请求url
        /*String result = restTemplate.getForObject("http://ASK-PRICE/askPrice", String.class);
        System.out.println(result);*/

    }

	@Test
    public void ttredis(){
	    try {
            //tts.getByOrderNo2("rnf210302120747626001", "127", "rnf", true);
            // redisTemplate.opsForValue().decrement("aaaaa");
	        //otherSchedule.commitRefundTicket();
	        //otherSchedule.getRefundDatail();




            // otherSchedule.syncFlightStatus();
           /* OrderVO orderVO = orderService.selectOrderDetailsAuto("rnf201226124838472001");
            double d=Double.valueOf(orderVO.getPassengetList().get(0).getTicketPrice()).doubleValue() -3;
            Map<String, String> lowPriceNo = PPUtil.getLowPriceNoTP(orderVO, d + "");
            System.out.println(lowPriceNo.get("ticketPrice"));
            System.out.println(lowPriceNo.get("sellPrice"));*/
            //cs.updatePrice();
            //Set<Object> keys = redisTemplate.keys("*");
            //redisTemplate.delete(keys);

           /* redisTemplate.opsForValue().set("abc", "dddd",1, TimeUnit.HOURS);
            Object a = redisTemplate.opsForValue().get("abc");
            System.out.println(a.toString());*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//@Test
public void tcloud(){
    AskPriceDTO askPriceDTO=new AskPriceDTO();
    askPriceDTO.setDpt("PVG");
    askPriceDTO.setArr("CAN");
    askPriceDTO.setCabin("E");
    askPriceDTO.setDate("2021-01-13");
    String flightNo = "KY9532";
    if(!flightNo.startsWith("ZH")) {
        flightNo= flightNo.replace("KY", "ZH");
    }
    Gson gson=new Gson();
    askPriceDTO.setFlightNo(flightNo);
    String askPriceDTOJsonStr = gson.toJson(askPriceDTO);
    AskPriceDTO ss= askPriceService.askPrice(askPriceDTOJsonStr);
    System.out.println(ss);
}

	//@Test
	public void textTTS(){
		try {
            //boolean b = RefundServiceImpl.deletePnr(refund);
            //System.out.println(b);

            //tcs.importTcOrder();
//			String orderStatus = jiuOrder.getOrderStatus("5223681583724");
//			System.out.println(orderStatus);
			//ttss.getB2BtciketCount();
			/*while(true){
				Thread.sleep(30*1000);
				//ttss.updateTicketNo();
			}*/
			//tnS.updateTicketNo();
			//tnS.importTuniuOrder2();
			//tc.getOrderByOrderNo("OPSDJ0XDE108LQ008613", "dgssqzhh", "dgssqzhh@tcjp", null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	/*	try {
			List<OrderVO> batchImportOrders = tts.batchImportOrders(InterfaceConstant.ORDER_SOURCE_QNR, "2", status);
			for(OrderVO vo:batchImportOrders){
				System.out.println(vo.getOrderNo());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	//@Test
	public void aa() throws Exception {
		try {
			//js.importOrder();
			/*String orderStatus = jiuOrder.getOrderStatus("5223731631744");
			System.out.println(orderStatus);*/
			OrderVO byOrderNo = jiuOrder.getByOrderNo("5225515020954", "07", "1");
            //OrderVO byOrderNo = tts.getByOrderNo("rnb210105082659243001", "02", "1");
			byOrderNo.getOrder().setStatus("3");
			byOrderNo.getOrder().setProcessBy("");
			System.out.println(byOrderNo);
			orderService.saveOrderVO(byOrderNo);
			/*List<String> orderNoList = jiuOrder.getOrderNoList();
			orderNoList.forEach((str)->System.out.println(str));*/
			/*
			System.out.println(byOrderNo);*/
			/*QueryWrapper<Passenger> query=new QueryWrapper<Passenger>();
			query.eq("order_no", "rnf200629140402058001");
			List<Passenger> list = passengreMapper.selectList(query);
			for(Passenger p:list){
				System.out.println(p);
			}*/
			/*OrderVO byOrderNo = tts.getByOrderNo("rnf200807193027554001", "02", "2", true);
			System.out.println(byOrderNo);
			System.out.println(byOrderNo.getOrder());*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*String orderRemark = tts.getOrderRemark("rnf200407161058619001", "rnf", true);
		System.out.println(orderRemark);*/
		try {
		/*	OrderVO orderByOrderNo = tb.getOrderByOrderNo("929847613615", null, null);
			System.out.println(orderByOrderNo);*/
			//tb.sysncRefundOrder();
			//tb.sysncRefundOrder();
			//tts.getByOrderNo("rnf200410181226118001", null, "2", true);
			//tb.batchImportOrders(null, null, null);
			//{"result":{"data":"上上千航服","orderId":923815442615},"errMsg":"","code":"0","success":true}
			//{"result":{"data":"","orderId":923603068615},"errMsg":"","code":"0","success":true}
		/*	String lockedOrder = tb.unlocked("923603068615", true);
			System.out.println(lockedOrder);*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	/*	try {
			OrderVO vo = tts.getByOrderNo("rnf200106091036229001", "02", "2");
			System.out.println(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//importSchedule.importTuniuOrder();
		//cs.updateTicketNo();
		//tuniu.getTicketNoByOid("1861375422");

		/*Query query=new Query();
		query.addCriteria(Criteria.where("source").is("RNB"));
		CookieSource cookie = mongnTemplate.findOne(query, CookieSource.class);
		JSONArray jsonArray = JSONArray.fromObject(cookie.getCookie());
		System.out.println(jsonArray);
		for(int i=0;i<jsonArray.size();i++){
			JSONObject json=jsonArray.getJSONObject(i);
			if("JSESSIONIDNB".equals(json.getString("name"))){
				String cookieValue=json.getString("value");
				System.out.println(cookieValue);
				break;
			}
		}
*/

	}

	@Test
	public void tw1() throws Exception {


        //ct.syncChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "1","Unhandle");
		//ct.assignOrder("645272414","1","0",true);
		//ttss.importTTsOrderRnf();
		//jius.importOrder();
		//ttss.updateTicketNo();
		//OrderVO byOrderNo = tts.getByOrderNo("rnf201219150106282001", null, "2", true);
        //System.out.println(byOrderNo.getOrder().getRemarkStr());
        //System.out.println(byOrderNo);
//		System.out.println(byOrderNo.getOrder().getOrderNo());
//		System.out.println(byOrderNo.getOrder().getRemarkStr());
	/*	List<String> orderNoList = jiuOrder.getOrderNoList();
		for(String s:orderNoList){
			System.out.println(s);
		}*/
		/*OrderVO byOrderNo = jiuOrder.getByOrderNo("5222983180524", InterfaceConstant.ORDER_SOURCE_JIU, "1");
		System.out.println(byOrderNo);
		orderService.saveOrderVO(byOrderNo);*/
		//js.importOrder();
		//
	/*	while(true){
			Thread.sleep(1000*6);
			ttss.importTTsOrderRnb();
			ttss.importTTsOrderRnf();
			cs.importCtripOrder1();
			cs.importCtripOrder2();
			cs.importCtripOrder3();
			//tb.batchImportOrders(InterfaceConstant., orderShop, status)
		}*/
		//ttss.tt();
		/*
		for(Passenger p:byOrderNo.getPassengetList()){
			System.out.println(p.getTicketPrice()+p.getCabin());
		}*/
		//jiuOrder.getByOrderNo("5222837035094", null, null);
//		String policyLink = tts.getPolicyLink("rnf200609180339598001", "rnf", true);
//		System.out.println(policyLink);
		/*System.out.println(orderService.addProcess("admin", "j"));*/
		//tts.getByOrderNo("rnf200519093705623001", null, "2", true);
		//System.out.println(tb.getRefundDetail("55435170"));
//		OrderVO orderByOrderNo = tb.getOrderByOrderNo("941734920615", null, null);
//		System.out.println(orderByOrderNo);
		/*List<String> dictNameList = dict.getDictNameList("badPeople");
		System.out.println(dictNameList);*/

	/*	List<OrderVO> batchImportOrders = ct.batchImportOrders("1", "1", null);
		System.out.println("===================");
		System.out.println(batchImportOrders);
*/
	}

	//@Test
	public void tw() throws Exception {
		try {
		//	tb.getOrderByOrderNo("945684979615", "", "");
			/*OrderVO orderByOrderNo = tc.getOrderByOrderNo("O1915EFAE10PKG103349", "dgssqzhh", "dgssqzhh@tcjp", null, null);
			for(Passenger p:orderByOrderNo.getPassengetList()){
				System.out.println(p.getTicketPrice()+p.getCabin());
			}*/
			//cs.updateTicketNo();
			/*OrderVO byOrderNo = jiuOrder.getByOrderNo("5222721156954", InterfaceConstant.ORDER_SOURCE_JIU, "1");
			/*Refund r=new Refund();
			r.setOrderNo("5222710769934");
			r.setRetNo("303586");
			String confirmRefund = jiuOrder.confirmRefund(r);
			System.out.println(confirmRefund);*/
			//jiuOrder.sysncRefundOrder();
			/*JSONObject locked = jiuOrder.locked("5222620630044", true);
			System.out.println(locked);*/
			//System.out.println(jiuOrder.getOrderStatus("5222614845894"));
			/*JSONObject locked = jiuOrder.locked("5223483990164", true);
			System.out.println(locked);*/
			/*List<Passenger> passengetList = byOrderNo.getPassengetList();
			for(Passenger p:passengetList){
				System.out.println(p);
			}*/
			//.saveOrderVO(byOrderNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*try {
			cs.syncChangeOrder();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		//tts.getChangeOrders("rnf191024171048947001", "rnf");
		//tts.getByOrderNo2("rnf191231134204495001", "06", "2");
		/*OrderVO order=orderService.selectOrderDetails("rnf191212143856733001", "02", null);
		tts.verifyTicketNo(order);*/
		//List<OrderVO> batchImportOrders = tuniu.batchImportOrders("06", "2", 0);
		//ttss.importTTsOrderRnf5();
		//ttss.importTTsOrderRnb();
	}
	//@Test
	public void a() {
		try {

			//ct.syncRefundOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "1");
			//ct.syncChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "1");
			tb.sysncChangeOrder();
			//tnS.importTuniuOrder2();
			/*List<Order> byOrderNo2 = jiuOrder.getByOrderNo2("", true);
			for(Order o:byOrderNo2){
				System.out.println(o.getOrderNo());
			}*/
//			String orderStatus = ct.getOrderStatus("575133235", "3");
//			System.out.println(orderStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*try {
			List<Order> byOrderNo2 = tts.getByOrderNo2(null, "127", "rnb");
			for(Order o:byOrderNo2){
				System.out.println(o.getOrderNo());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		//tc.getOrderByOrderNo("", user, pass, orderSource, orderShop)

		/*try {
			tc.syncChangeOrder();
		} catch (Exception e) {

			e.printStackTrace();
		}*/
	}

	//@Test
	public void t2() throws Exception {
		try {
            PPRefund ppRefund=new PPRefund();
            ppRefund.setOrderNo("JP2012150C8D");
            ppRefund.setTravelRange("CTUKHN");
            ppRefund.setTicketNo("479-2167856554");
            ppRefund.setPassenger("徐浦钦");
            ppRefund.setRefundReason("10260201");
            String xmlResult = PPUtil.refundOrder(ppRefund);
            System.out.println(xmlResult);

			//TuniuOrderService.getRefundOrder("2");
			//otherSchedule.updateChangePrice();
			//jiuOrder.sysncChangeOrder();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//tc.sysncChangeOrder(1);

	/*	ttss.importTTsOrderRnb();
		ttss.importTTsOrderRnf();
		js.importTTsOrderRnf();
		ttss.updateOrderStatus();*/

		/*List<OrderVO> batchImportOrders = tc.batchImportOrders("05", "1", "N");
		System.out.println(batchImportOrders);*/
		//OrderVO orderByOrderNo = tc.getOrderByOrderNo("OVAMKQ5WE10SNH104808", "dgssqzhh", "dgssqzhh@tcjp","","");
		//OrderVO orderByOrderNo2 = tc.getOrderByOrderNo2("OLAF9EVQD10TTK002299", "", "");
		//System.out.println(orderByOrderNo2);
		//tc.syncFailOrder();

		/*Date d=new Date();
		d.setTime(1577329251777l);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));*/
		/*Date d1=new Date();
		d1.setTime(1577329255777l);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d1));*/
		//tc.sysncRefundOrder();
	}

	//@Test
	public void t1() throws Exception {

		/*String base64Decode = Base64Util.getFromBase64("eyJyIjowLjE0MjQ5ODg2NTM4Mjk4NzczLCJpZCI6MTMzMzM3NzM0NSwidHlwZSI6MSwibWV0aG9kIjoiUE9TVCIsInN5c0ZsYWciOiJBT1AiLCJ1cmwiOiIvYW9wL25iL29yZGVyL2Zyb250L3RpY2tldC9sb2NrIn0=");
		System.out.println(base64Decode);*/
		/*JSONObject lock = tuniu.lock("1912194493", "2",true);
		System.out.println(lock);*/
	 //{"errorCode":7140008,"data":{},"success":false,"msg":"出票单状态不为待处理状态或客票异常状态"}
									  //{"errorCode":230000,"data":true,"success":true,"msg":""}
										//{"errorCode":7110002,"data":{},"success":false,"msg":"订单已被:67731 加锁"}



	/*	JSONObject unlock = tuniu.unlock("577515884", "2",true);
		System.out.println(unlock);*/
		//{"errorCode":7110002,"data":{},"success":false,"msg":"订单未锁定"}
		//{"errorCode":230000,"data":true,"success":true,"msg":""}




		QueryWrapper<Refund> query3=new QueryWrapper<Refund>();
		query3.between("create_date", "2020-10-15 00:00:00", "2020-10-15 11:26:49");
		List<Refund> selectList = refundMapper.selectList(query3);
		for(Refund r:selectList) {
			String createBy = r.getCreateBy();
			if(createBy.contains("SYSTEM") || StringUtils.isEmpty(createBy)) {
				continue;
			}
			r.setProcessBy(createBy);
			refundMapper.updateById(r);
			//System.out.println();
		}

	   //ct.syncRefundOrder("1", "2");

		//ctrip.batchImportOrders("1", "1", 1);

		/*orderVo.getOrder().setStatus("3");
		orderService.saveOrderVO(orderVo);*/
	}


	//@Test
	public void t() {
		try {
			String user=DictUtils.getDictCode("order_import_cfgxc1", "user");
			String pass=DictUtils.getDictCode("order_import_cfgxc1", "pass");
			String passWord=Md5Util.md5(user+"#"+pass).toLowerCase();
			CtripOrderVO ctripOrdcerVO = ct.getOrderDetailBycOrderNo("661038953", user, passWord);
			OrderVO byOrderNo = ct.procCtripOrderToOrderVO(ctripOrdcerVO, "1", "3");
            byOrderNo.getOrder().setStatus("3");
            byOrderNo.getOrder().setProcessBy("");
            System.out.println(byOrderNo);
            orderService.saveOrderVO(byOrderNo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		//Integer s = TuniuOrderService.checkOrderStatus("710718825");
		//System.out.println( s);
		//TuniuOrderService.getRefundOrder();
		//tuniu.getChangeOrder();

	}
	//@Test
	public void initDatabase() {
		try {
			JSONObject json = tts.getChangeOrders("rnb201113161617813001", "rnb201113161617813001".substring(0,3),"2020-12-05");
			JSONObject changeJson=json.getJSONArray("orderList").getJSONObject(0);
			JSONArray passengerArr=changeJson.getJSONArray("ttsPassengerList");
			System.out.println(changeJson.getString("allPrices"));
			String revenuePrice = new BigDecimal(changeJson.getString("allPrices")).divide(new BigDecimal(passengerArr.size())).toString();
			System.out.println(revenuePrice);
			/*List<OrderVO> batchImportOrders = tuniu.batchImportOrders("06", "2", "4");
			for(OrderVO vo:batchImportOrders){
				System.out.println(vo.getOrder().getPolicyRemark());
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			//importSchedule.importTuniuOrder();
	}

	//@Test
	public void initDatabase2() {
		try {
            JSONObject json = tts.updateOrderStatus("rnf210105164844323001", "rnf210105164844323001".substring(0, 3), true);
            System.out.println(json);
            ct.updateChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "1","All");
		   // ct.syncChangeOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "1","All");
			//ct.syncRefundOrder(InterfaceConstant.ORDER_SOURCE_CTRIP, "1");

		} catch (Exception e) {
		    e.printStackTrace();
		}


	}
	//@Test
	public void initDatabase1() {
		try {
			//System.out.println(tb.getRefundDetail("61940415"));
			//jiuOrder.sysncRefundOrder();
		/*	String ticketNoByOid = tuniu.getStatusByOid("786793125", "2",true);
			System.out.println(ticketNoByOid);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		//importSchedule.updateTuNiuFailOrder();

	}
	//@Test
	public void initDatabase3() throws InterruptedException {
		try {
			Map<String, String> map = tts.getRefundMoney("rnb201112235209192001", "rnb201112235209192001".substring(0, 3));
			System.out.println(map.get("refundMoney"));
			System.out.println(map.get("refundType"));
			//tuniuOrderService.batchImportOrders("", "2", "0");
			//TuniuOrderService.getChangeOrder("2");
			//cs.updatePrice();
		} catch (Exception e) {
		    e.printStackTrace();
		}

		/*String lockedOrder = tc.lockedOrder("OPR9GALFE107D2308088", true);
		System.out.println(lockedOrder);*/
		/*while(true){
			UpdateWrapper<Passenger> uw=new UpdateWrapper<Passenger>();
			uw.eq("remarks", "已退票");
			uw.eq("order_source", "02");
			Passenger p1=new Passenger();
			p1.setStatus("3");
			pm.update(p1, uw);
			Thread.sleep(1000*60);
		}*/
		//importSchedule.updateTuNiuExceptionOrder();
	}

	//淘宝拿session
	public static void main(String[] args) {
		TaobaoClient client = new DefaultTaobaoClient("https://eco.taobao.com/router/rest", "29181677", "d72fc6df319718c3e17770fa577ff593");
		TopAuthTokenCreateRequest req = new TopAuthTokenCreateRequest();
		req.setCode("ibLuRkSuHqULhPo2kVnV4IgC27538432");
	//	req.setUuid("abc");
		TopAuthTokenCreateResponse rsp;
		try {
			rsp = client.execute(req);
			System.out.println(rsp.getBody());
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
    public  void  tsrsa(){

    }




	public static void writeFile(String content,String fileName,boolean append,String charset) {
		File file = new File(fileName);

		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;

		try {
			out = new FileOutputStream(file,append);
			writer = new OutputStreamWriter(out,charset);
			bw = new BufferedWriter(writer);

			bw.write(content);
			//bw.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
