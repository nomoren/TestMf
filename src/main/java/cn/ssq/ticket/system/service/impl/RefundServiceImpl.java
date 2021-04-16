package cn.ssq.ticket.system.service.impl;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.mapper.OrderMapper;
import cn.ssq.ticket.system.mapper.PassengreMapper;
import cn.ssq.ticket.system.mapper.PurchaseMapper;
import cn.ssq.ticket.system.mapper.RefundMapper;
import cn.ssq.ticket.system.queryEntity.RefundQuery;
import cn.ssq.ticket.system.service.LogService;
import cn.ssq.ticket.system.service.OrderImport.impl.CtripOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.JiuOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TTsOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TcOrderService;
import cn.ssq.ticket.system.service.RefundService;
import cn.ssq.ticket.system.util.*;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
public class RefundServiceImpl implements RefundService{

	private static Logger log = LoggerFactory.getLogger(RefundServiceImpl.class);
	
	@Autowired
	private RefundMapper refundMapper;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private PassengreMapper passengerMapper;
	@Autowired
	private PurchaseMapper purchMapper;
	@Autowired
	private LogService logService;
	@Autowired
    private CtripOrderService ctripOrderService;
	@Autowired
    private TcOrderService tcOrderService;
	@Autowired
    private JiuOrderService jiuOrderService;
    @Autowired
    private TTsOrderService ttsOrderService;

    private static SimpleDateFormat SDF =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
	 * 获取refund列表
	 */
	@Override
	public List<RefundVO> getRefundList(RefundQuery query) {
        List<RefundVO> refundList = refundMapper.getRefundList(query);
        for (RefundVO refundVO : refundList) {
            if(org.springframework.util.StringUtils.isEmpty(refundVO.getRefundType())){
                refundVO.setRefundType("2");
            }
            refundVO.setRefundType(DictUtils.getDictName("refund_type_passenger",refundVO.getRefundType()));
            refundVO.setcRemState(DictUtils.getDictName("refund_status",refundVO.getcRemState()));
            refundVO.setAirRemState(DictUtils.getDictName("refund_status",refundVO.getAirRemState()));
            refundVO.setAirRefundType(DictUtils.getDictName("refund_type_air",refundVO.getAirRefundType()));
        }
        return refundList;
    }

	@Override
	public int getRefundListCount(RefundQuery query) {
		return refundMapper.getRefundListCount(query);
	}

	/**
	 * 获取退票单关联的乘机人
	 */
	@Override
	public List<Passenger> getRefundPassenger(String retNo) {
		List<Passenger> refundPassenger = refundMapper.getRefundPassenger(retNo);
		for(Passenger passenger:refundPassenger){
			passenger.setPassengerType(DictUtils.getDictName("passenger_type", passenger.getPassengerType()));
			passenger.setIsImRet(DictUtils.getDictName("refund_status", passenger.getIsImRet()));
		}
		return refundPassenger;
	}

	/**
	 * 根据退票单号获取退票单实例
	 */
	@Override
	public Refund getRefund(String retNo) {
		return refundMapper.getRefund(retNo).get(0);
	}

	/**
	 *修改退款状态 
	 */
	@Override
	public void changeRemStatus(String status, String retNo) {
		refundMapper.changeRemStatus(status, retNo);
	}
	/**
	 * 修改退款状态为完成
	 */
	@Override
	public void changeRemStatusOk(String cRealPrice, String retNo) {
		refundMapper.changeRemStatusOk(cRealPrice, retNo);
	}

	/**
	 * 新增保存退票单
	 */
	@Override
	@Transactional
	public void saveRefunds(List<Refund> list) {
		if(list==null||list.size()<1){
			throw new RuntimeException();
		}
		String createName=ShiroKit.getLocalName();
		if(StringUtils.isEmpty(createName)){
			createName=refundMapper.isHavePcocess(list.get(0).getRetNo()).get(0);
		}
		String createDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		for(Refund refund:list){
		    refund.setRetNo(refund.getRetNo().trim());
			boolean exist=this.isExit(refund.getOrderNo(), refund.getOrderSource(), refund.getOrderShop(),refund.getPassengerName());
			refund.setCreateBy(createName);
			refund.setCreateDate(createDate);
			refund.setProcessBy(createName);
			refund.setcAppDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			refund.setIsAuto("0");
			refund.setAirRefundType(refund.getRefundType());
			if("".equals(refund.getcRemDate())){
				refund.setcRemDate(null);
			}
			if("".equals(refund.getAirRemDate())){
				refund.setAirRemDate(null);
			}
			if("".equals(refund.getAirAppDate())){
				refund.setAirAppDate(null);
			}
			if(exist){
				refundMapper.updateRefund(refund);
				this.changePassenerTicketState(refund);
				continue;
			}
			try {
				QueryWrapper<Purchase> purchQuery=new QueryWrapper<Purchase>();
				purchQuery.eq("order_id", refund.getOrderId());
				purchQuery.eq("flag", 0);
				List<Purchase> selectList = purchMapper.selectList(purchQuery);
				for(Purchase purch:selectList) {
					List<String> nameList = Arrays.asList(purch.getPassengerNames().split(","));
					if(nameList.contains(refund.getPassengerName())) {			
						refund.setBusinessNo(purch.getBusinessNo()==null?purch.getTradeNo():purch.getBusinessNo());
						break;
					}
				}
			} catch (Exception e) {
				
			}
			refundMapper.insert(refund);
			refund.setcRemState("0");
			this.changePassenerTicketState(refund);
		}	
		OrderOperateLog log=new OrderOperateLog();
		log.setName(createName);
		log.setContent("点击了保存退票");
		log.setType("订单处理");
		log.setRetNo(list.get(0).getRetNo());
		logService.saveLog(log);
	}

	/**
	 * 自动创建退票单
	 */
	@Override
	public synchronized void autoCreateRefund(Refund refund) {
		if(refund==null){
            return;
        }
		boolean exist=this.isExit(refund.getOrderNo(), refund.getOrderSource(), refund.getOrderShop(),refund.getPassengerName());
		if(exist){
			return;
		}
        refund.setIsAuto("0");
        refund.setXePnrStatus("0");
        refund.setAirRefundType(refund.getRefundType());
		try {
			//从采购单同步商户订单号
			QueryWrapper<Purchase> purchQuery=new QueryWrapper<Purchase>();
			purchQuery.eq("order_id", refund.getOrderId());
			purchQuery.eq("flag", 0);
			List<Purchase> selectList = purchMapper.selectList(purchQuery);
			if(InterfaceConstant.ORDER_SOURCE_QNR.equals(refund.getOrderSource())){
				if(StringUtils.isNotEmpty(refund.getOrderRemark())){
					QueryWrapper<Purchase> purchQuery2=new QueryWrapper<Purchase>();
					purchQuery2.eq("order_no", refund.getRemark());
					purchQuery2.eq("flag", 0);
					selectList = purchMapper.selectList(purchQuery2);
				}
			}
			for(Purchase purch:selectList) {
				List<String> nameList = Arrays.asList(purch.getPassengerNames().split(","));
				if(nameList.contains(refund.getPassengerName())) {			
					refund.setBusinessNo(purch.getBusinessNo()==null?purch.getTradeNo():purch.getBusinessNo());
					break;
				}
			}
            QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
            orderQueryWrapper.eq("order_no", refund.getOrderNo());
            orderQueryWrapper.select("status","policy_type","order_no","order_id","flight_no","trip_type","Out_order_no");
            Order order=orderMapper.selectOne(orderQueryWrapper);
            if(WebConstant.ORDER_NO_TICKET.equals(order.getStatus())||WebConstant.ORDER_PRINT.equals(order.getStatus())){
                orderMapper.updateStatus(WebConstant.ORDER_NOTICK_REFUND, order.getOrderNo());
            }
            refund.setFlightType(order.getTripType());
            refund.setPolicyType(order.getPolicyType());
            Double aDouble = refund.getcRealPrice();
            if (aDouble != null) {
                refund.setAirEstimatePrice(new BigDecimal(aDouble));
            }

            Passenger p=passengerMapper.selectOne(new QueryWrapper<Passenger>().eq("name", refund.getPassengerName().trim()).eq("order_no", refund.getOrderNo().trim()));
            if(p!=null){
                refund.setPassengerId(p.getPassengerId());
                refund.setModifyDate(p.getPrintTicketDate());
                String depTime = p.getDepTime();
                if (depTime.length()<6){
                    depTime=depTime+":00";
                }
                refund.setFlightTime(depTime);
            }

            boolean isDelete = this.deletePnr(refund);
            if (isDelete) {
                refund.setXePnrStatus("1");
            } else {
                //取位失败，人工处理
                if (StringUtils.isEmpty(refund.getOrderRemark())) {
                    refund.setOrderRemark("取位失败");
                }
                refund.setAirEstimatePrice(null);
            }


            Date flightDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(refund.getFlightDate() + " "+refund.getFlightTime());
            boolean after = new Date().after(flightDate);
            if(after){
                refund.setFlightStatus("<span style='color: #f60;'>已起飞</span>");
            }else{
                refund.setFlightStatus("未起飞");
            }

            if (order.getFlightNo().contains("ZH") && WebConstant.REFUND_TYPE_ZIYUAN.equals(refund.getRefundType()) && "0".equals(order.getTripType()) && isDelete && !after && StringUtils.isNotEmpty(order.getOutOrderNo())) {
                String printTicketCabin = refund.getPrintTicketCabin();
                if (!"R".equals(printTicketCabin) && !"G".equals(printTicketCabin) && !"J".equals(printTicketCabin) && !"Y".equals(printTicketCabin)) {
                    if (refund.getPolicyType().contains("FCPP")) {
                        String orderSource = refund.getOrderSource();
                        boolean canAuto = false;
                        if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)) {
                            JSONObject confirmRefund = ctripOrderService.confirmRefund(refund.getRetNo(), refund.getOrderShop());
                            log.info(refund.getOrderNo() + "确认结果" + confirmRefund);
                            if (confirmRefund != null) {
                                if (confirmRefund.toString().contains("RefundConfirmResult")) {
                                    String string = confirmRefund.getJSONObject("ResponseBody").getString("RefundConfirmResult");
                                    if ("Success".equals(string)) {
                                        canAuto = true;
                                    }
                                } else {
                                    String string = confirmRefund.getJSONObject("Header").getString("ResultMsg");
                                    if (string.contains("已确认")) {
                                        canAuto = true;
                                    }
                                }
                            }
                        } else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)) {
                            JSONObject jsonObject = tcOrderService.confirmRefund(refund.getRetNo(), 1);
                            log.info(refund.getOrderNo() + "确认结果" + jsonObject);
                            if (jsonObject != null) {
                                boolean isSuccess = jsonObject.getBoolean("isSuccess");
                                if (isSuccess) {
                                    canAuto = true;
                                } else {
                                    String errorMessage = jsonObject.getString("errorMessage");
                                    if (errorMessage.contains("此退单已经处理")) {
                                        canAuto = true;
                                    }
                                }
                            }
                        } else if (InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource)) {
                            JSONObject jsonObject = jiuOrderService.confirmRefund(refund);
                            log.info(refund.getOrderNo() + "确认结果" + jsonObject);
                            if (jsonObject != null) {
                                boolean ret = jsonObject.getBoolean("ret");
                                if (ret) {
                                    canAuto = true;
                                }
                            }
                        } else if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)) {
                            JSONObject jsonObject = ttsOrderService.submitInfoChecked(refund);
                            log.info(refund.getOrderNo() + "确认结果" + jsonObject);
                            if (jsonObject != null) {
                                boolean ret = jsonObject.getBoolean("ret");
                                if (ret) {
                                    jsonObject = ttsOrderService.confirmRefund(refund.getRetNo(), refund.getcRealPrice().toString().split("\\.")[0], refund.getOrderNo().substring(0, 3));
                                    log.info(refund.getOrderNo() + "确认结果" + jsonObject);
                                    ret = jsonObject.getBoolean("ret");
                                    if (ret) {
                                        canAuto = true;
                                    }
                                }
                            }

                        }
                        if (canAuto) {
                            //可自动退
                            refund.setcRemDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                            refund.setIsAuto("1");
                            refund.setProcessBy("SYSTEM");
                        }
                    }
                }else{
                    refund.setOrderRemark("高舱");
                    refund.setAirEstimatePrice(null);
                }
            }

        } catch (Exception e) {
			log.error("退票单同步商户号出错"+refund.getRetNo(),e);
		}
        refund.setcRemState(WebConstant.REFUND_ALREADY_RETURN);
		refundMapper.insert(refund);
		refundMapper.initializedPassengerRefund(refund.getOrderNo().trim(), refund.getPassengerName().trim());
	}
	

	/**
	 * 修改退票单
	 */
	@Override
	@Transactional
	public void updateRefunds(List<Refund> list) {
		if(list.size()<1){
			throw new RuntimeException();
		}
		String user=ShiroKit.getLocalName();
		if(StringUtils.isEmpty(user)){
			user=refundMapper.isHavePcocessById(String.valueOf(list.get(0).getRefundId())).get(0);
		}
		for(Refund refund:list){
			if(StringUtils.isNotEmpty(user)){
				refund.setCreateBy(user);
				refund.setProcessBy(user);
			}
			String format = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			if(StringUtils.isEmpty(refund.getAirAppDate())) {
				refund.setAirAppDate(format);
			}
			refund.setCreateDate(format);
			refundMapper.updateRefund(refund);
			this.changePassenerTicketState(refund);
		}
		OrderOperateLog log=new OrderOperateLog();
		log.setContent("点击了保存退票");
		log.setType("订单处理");
		log.setRetNo(list.get(0).getRetNo());
		log.setName(user);
		logService.saveLog(log);
	}


	@Override
	public void updateById(Refund refund) {
		refundMapper.updateById(refund);
	}

	@Override
	public boolean isExit(String orderNo,String orderSource,String orderShop,String name) {
		QueryWrapper<Refund> query=new QueryWrapper<Refund>();
		query.eq("order_no", orderNo.trim());
		query.eq("order_source", orderSource);
	/*	query.eq("Order_shop", orderShop);*/
		query.eq("passenger_Name", name.trim());
		 List<Refund> selectList = refundMapper.selectList(query);
		if(selectList.size()>0){
			return true;
		}
		return false;
	}

	@Override
	public void deleteRefund(String[] retNoArray) {
		refundMapper.deleteRefund(retNoArray);

	}

	@Override
	public Refund getRefundById(String refundId) {
		return refundMapper.selectById(refundId);
	}

	@Override
	public List<Refund> getRefundsByRetNo(String retNo) {
		QueryWrapper<Refund> query=new QueryWrapper<Refund>();
		query.eq("RET_NO", retNo);
		return refundMapper.selectList(query);
	}

	@Override
	public List<RefundVO> getDataForReport(String startDate, String endDate,
			String orderSorce, String orderShop) {
		if("0".equals(orderShop)){
			orderShop=null;
		}
		if(!"0".equals(orderShop)){
			if(!Util.orderSorceList.contains(orderSorce)){
				orderShop=null;
			}
		}
		if("0".equals(orderSorce)){
			orderSorce=null;
			orderShop=null;
		}
		return refundMapper.getDataForReport(startDate, endDate, orderSorce, orderShop);
	}

    @Override
    public List<RefundVO> getDataForReportList(RefundQuery query) {
        return refundMapper.getDataForReportList(query);
    }

	@Override
	public List<Refund> getRefundsByQueryWapper(Wrapper<Refund> query) {
		return refundMapper.selectList(query);
	}


    public boolean deletePnr(Refund refund) {
	    boolean isDelete=true;
        OrderOperateLog logg = new OrderOperateLog();
        logg.setRetNo(refund.getRetNo());
        logg.setType("订单处理");
        logg.setName("SYSTEM");
	    if(InterfaceConstant.ORDER_SOURCE_CTRIP.equals(refund.getOrderSource()) && "1".equals(refund.getOrderShop())){
	        return isDelete;
        }
        if("1".equals(refund.getFlightType())){
            refund.setOrderRemark("往返不取位");
            return  false;
        }
        if (!refund.getFlightNo().contains("ZH")) {
            refund.setOrderRemark("外航无权限");
            return  false;
        }
        String uid="apiyjsl3";
        String param="";
        try {
            String pnr=refund.getPnr();
            if(StringUtils.isEmpty(pnr)){
                refund.setOrderRemark("pnr为空");
                logg.setContent("取消编码失败 pnr为空");
                logService.saveLog(logg);
                return  false;
            }
            String flightNo = refund.getFlightNo();
            if(flightNo.contains("MU") || flightNo.contains("HU")){
                uid="sz05";
            }
            pnr="RT"+pnr;
            String passengerName=refund.getPassengerName();
            param="cmd=raw&ins="+ ToolsUtil.bytes2Hex("LOCK01".getBytes("gbk"));
            String cmd = EtermHttpUtil.cmdChose(uid, param);
            Document document = DocumentHelper.parseText(cmd);
            Element rootElement = document.getRootElement();
            String ins = rootElement.getText();
            byte[] bytes = ToolsUtil.hex2Bytes(ins);
            String result=new String(bytes,"gbk");
            //已处于锁定状态
            if (!result.contains("SUCCESS") && !result.contains("已处于锁定状态")) {
                log.info(pnr+"locak结果"+result);
                logg.setContent("取消编码失败 LOCK01失败");
                refund.setOrderRemark("LOCK01失败");
                logService.saveLog(logg);
                param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                EtermHttpUtil.cmdChose(uid, param);
                return false;
            }
            param="cmd=raw&ins="+ToolsUtil.bytes2Hex(pnr.getBytes("gbk"));
            cmd = EtermHttpUtil.cmdChose(uid, param);
            document = DocumentHelper.parseText(cmd);
            rootElement = document.getRootElement();
            ins = rootElement.getText();
            bytes = ToolsUtil.hex2Bytes(ins);
            result = new String(bytes, "gbk");
            if (result.contains("THIS PNR WAS ENTIRELY CANCELLED")) {
                logg.setContent(passengerName + "取消编码成功,平台已取消");
                logService.saveLog(logg);
                param = "cmd=raw&ins=" + ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                EtermHttpUtil.cmdChose(uid, param);
                return true;
            }
            String modifyDate = refund.getModifyDate();
     /*       if (StringUtils.isNotEmpty(modifyDate) && WebConstant.REFUND_TYPE_NOZIYUAN.equals(refund.getRefundType())) {
                Date printDate = SDF.parse(modifyDate);
                Date now = SDF.parse("2021-01-27 00:00:00");
                if(printDate.before(now)){
                    String time=StringUtils.isEmpty(refund.getFlightTime())?"00:00:00":refund.getFlightTime();
                    String fTime= refund.getFlightDate() + " " +time;
                    Date flightTime = SDF.parse(refund.getFlightDate() + " " +time);
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTime(flightTime);
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    Date theTime = calendar.getTime();
                    boolean belongCalendar = Util.belongCalendar(fTime, "2021-02-04", "2021-03-08");
                    if(belongCalendar){
                        if (new Date().after(theTime)){
                            logg.setContent(passengerName + "7天后，不取消编码");
                            refund.setOrderRemark("7天后，不取消编码");
                            logService.saveLog(logg);
                            param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                            EtermHttpUtil.cmdChose(uid, param);
                            return false;
                        }
                    }
                }
            }*/
            if (WebConstant.REFUND_TYPE_NOZIYUAN.equals(refund.getRefundType())) {
                //非自愿 RR 不取消
                String[] split = result.split("\\r?\\n");
                for (String s : split) {
                    s = s.trim();
                    if (s.matches(".*RR[0-9].*")) {
                        logg.setContent(passengerName + "非自愿 RR 不取消编码");
                        refund.setOrderRemark("非自愿 RR 不取");
                        logService.saveLog(logg);
                        param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                        EtermHttpUtil.cmdChose(uid, param);
                        return false;
                    }
                }
                /*if (StringUtils.isNotEmpty(modifyDate)) {
                    Date printDate = SDF.parse(modifyDate);
                    Date now = SDF.parse("2021-01-27 00:00:00");
                    boolean after = printDate.after(now);
                    if (after) {
                        //非自愿 RR  27号之后 不取消
                        String[] split = result.split("\\r?\\n");
                        for (String s : split) {
                            s = s.trim();
                            if (s.matches(".*RR[0-9]*.*")) {
                                logg.setContent(passengerName + "非自愿 RR 不取消编码");
                                refund.setOrderRemark("非自愿 RR 不取(27号后出票)");
                                logService.saveLog(logg);
                                param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                                EtermHttpUtil.cmdChose(uid, param);
                                return false;
                            }
                        }
                    }
                } else {

                }*/

            }
            if (WebConstant.REFUND_TYPE_ZIYUAN.equals(refund.getRefundType())) {
                //自愿 UN 不取消
                String[] split = result.split("\\r?\\n");
                for (String s : split) {
                    s = s.trim();
                    if (s.matches(".*UN[0-9].*")) {
                        logg.setContent(passengerName + "自愿 UN状态 不取消编码");
                        refund.setOrderRemark("自愿 UN 不取");
                        logService.saveLog(logg);
                        param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                        EtermHttpUtil.cmdChose(uid, param);
                        return false;
                    }

                }
            }


            if (StringUtils.isEmpty(result)) {
                log.info(pnr+"rtpnr结果"+result);
                logg.setContent(passengerName + "取消编码失败,RTPNR失败");
                refund.setOrderRemark("RTPNR失败");
                logService.saveLog(logg);
                param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                EtermHttpUtil.cmdChose(uid, param);
                return  false;
            }

            if (result.equals("NO PNR")) {
                log.info(pnr+"rtpnr结果"+result);
                logg.setContent(passengerName + "无权限" + result);
                refund.setOrderRemark(result);
                logService.saveLog(logg);
                param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                EtermHttpUtil.cmdChose(uid, param);
                return  false;
            }

            String[] split = result.split("\\r?\\n");
            String nameStr=split[0].trim();
            if(!nameStr.startsWith("1")){
                nameStr=split[1].trim();
            }
            if(!nameStr.contains(passengerName)){
                log.info(pnr+"没有匹配到该乘客rtpnr结果"+nameStr+":"+result);
                logg.setContent(passengerName + "取消编码失败，没有匹配到该乘客，可能定的假编码");
                refund.setOrderRemark("没有匹配的乘客，假编码？");
                logService.saveLog(logg);
                param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                EtermHttpUtil.cmdChose(uid, param);
                return  false;
            }
            String[] nameArr = nameStr.split(" ");
            if (nameArr.length==2){
                //只有一个人乘机人
                param="cmd=raw&ins="+ToolsUtil.bytes2Hex("XEPNR\\".getBytes("gbk"));
                cmd = EtermHttpUtil.cmdChose(uid, param);
                document = DocumentHelper.parseText(cmd);
                rootElement = document.getRootElement();
                ins = rootElement.getText();
                bytes = ToolsUtil.hex2Bytes(ins);
                result = new String(bytes, "gbk");//PNR CANCELLED
                log.info(pnr+"XEPNR所有结果"+result);
                if(!result.contains("PNR CANCELLED")){
                    logg.setContent(passengerName+"取消编码失败，XEPNR指令失败");
                    refund.setOrderRemark("XEPNR失败");
                    logService.saveLog(logg);
                    param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                    EtermHttpUtil.cmdChose(uid, param);
                    return  false;
                }
            }else {
                for (String s : nameArr) {
                    if(s.contains(passengerName)){
                        String[] split1 = s.split("\\.");
                        String cParam="XEP"+split1[0];
                        param="cmd=raw&ins="+ToolsUtil.bytes2Hex(cParam.getBytes("gbk"));
                        cmd = EtermHttpUtil.cmdChose(uid, param);
                        document = DocumentHelper.parseText(cmd);
                        rootElement = document.getRootElement();
                        ins = rootElement.getText();
                        bytes = ToolsUtil.hex2Bytes(ins);
                        result = new String(bytes, "gbk");
                        log.info(pnr+"XE单人"+cParam+"结果:"+result);
                        if(!result.contains("OSI") && !result.contains("SSR")){
                            logg.setContent(passengerName+"取消编码失败，"+cParam+"指令失败");
                            logService.saveLog(logg);
                            refund.setOrderRemark(cParam + "指令失败");
                            param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                            EtermHttpUtil.cmdChose(uid, param);
                            return  false;
                        }
                        param="cmd=raw&ins="+ToolsUtil.bytes2Hex("\\".getBytes("gbk"));
                        cmd = EtermHttpUtil.cmdChose(uid, param);
                        document = DocumentHelper.parseText(cmd);
                        rootElement = document.getRootElement();
                        ins = rootElement.getText();
                        bytes = ToolsUtil.hex2Bytes(ins);
                        result = new String(bytes, "gbk");
                        if(!result.contains(refund.getPnr())){
                            log.info(pnr+"\\结果"+result);
                            logg.setContent(passengerName+"取消编码失败，\\指令失败");
                            logService.saveLog(logg);
                            param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
                            EtermHttpUtil.cmdChose(uid, param);
                            return  false;
                        }
                    }
                }
            }
            param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
            EtermHttpUtil.cmdChose(uid, param);
          /*  document = DocumentHelper.parseText(cmd);
            rootElement = document.getRootElement();
            ins = rootElement.getText();
            bytes = ToolsUtil.hex2Bytes(ins);
            result = new String(bytes, "gbk");*/
            logg.setContent(passengerName+"取消编码成功");
        } catch (Exception e) {
	        e.printStackTrace();
            isDelete=false;
            logg.setContent("取消编码失败，异常");
            try {
                param="cmd=raw&ins="+ToolsUtil.bytes2Hex("UNLOCK01".getBytes("gbk"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            EtermHttpUtil.cmdChose(uid, param);
	    }
	    logService.saveLog(logg);
        return  isDelete;

    }

    /**
	 * 变更乘客客票状态
	 * @param refund
	 */
	public void changePassenerTicketState(Refund refund){
		try {
			//更新乘客票号状态
			Passenger p=new Passenger();
			p.setIsImRet(refund.getcRemState());
			p.setStatus(WebConstant.APPLY_REFUND);
			switch (refund.getcRemState()) {
			case "0":
				p.setStatus(WebConstant.APPLY_REFUND);
				break;
			case "1":
				p.setStatus(WebConstant.YET_REFUND);
				break;
			case "2":
				p.setStatus(WebConstant.REFUCE_REFUND);
				break;
			case "3":
				p.setStatus(WebConstant.WAIT_FLIGHT);
				break;
			case "4":
				p.setStatus(WebConstant.CANCEL);
				break;
			default:
				p.setStatus(WebConstant.APPLY_REFUND);
				break;
			}
			passengerMapper.update(p, new UpdateWrapper<Passenger>().eq("name", refund.getPassengerName().trim()).eq("order_no", refund.getOrderNo().trim()));
		} catch (Exception e) {
			log.error("客票同步状态异常",e);
		}
	}

	@Override
	public void addProcess(String process, String retNo) {
		refundMapper.addProcess(process, retNo);
	}

	@Override
	public void deleteProcess(String retNo) {
        refundMapper.deleteProcess(retNo);

    }

	@Override
	public String isHavePcocess(String retNo) {
		List<String> havePcocess = refundMapper.isHavePcocess(retNo);
		if(havePcocess.size()<1){
			return null;
		}
		return havePcocess.get(0);
	}

	@Override
	public void initializedPassengerRefund(String orderNo, String name) {
		refundMapper.initializedPassengerRefund(orderNo, name);
	}

	@Override
	public String isHavePcocessById(String refundId) {
		List<String> havePcocess = refundMapper.isHavePcocessById(refundId);
		if(havePcocess.size()<1){
			return null;
		}
		return havePcocess.get(0);
	}



    @Override
    public int updateByWrapper(Refund refund, UpdateWrapper<Refund> updateWrapper) {
        return refundMapper.update(refund,updateWrapper);
    }

    @Override
	public void deleteProcessById(String refundId) {
		refundMapper.deleteProcessById(refundId);
		
	}

	@Override
	public void addProcessById(String process, String refundId) {
		refundMapper.addProcessById(process, refundId);
		
	}

}
