package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.Flight;
import cn.ssq.ticket.system.entity.FlightVO;
import cn.ssq.ticket.system.entity.ResponseResult;
import cn.ssq.ticket.system.queryEntity.OrderQuery;
import cn.ssq.ticket.system.service.FlightService;
import cn.ssq.ticket.system.service.OrderImport.impl.*;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.stylefeng.guns.core.common.page.LayuiPageInfo;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/flightChange")
public class FlightChangeController {

	private String PREFIX = "/modular/system/flightChange/";

	@Autowired
	private FlightService fligthService;

	@Autowired
	private TcOrderService tcOrderService;

	@Autowired
	private TTsOrderService ttsOrderService;

	@Autowired
	private TbOrderService tbOrderService;

	@Autowired
	private JiuOrderService jiuOrderServer;
	
	private Logger logg = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CtripOrderService ctripOrderService;

	@RequestMapping("/flightChangeIndex")
	public String orderIndex(){
		return PREFIX + "flightChangeIndex.html";
	}

	@RequestMapping("/toflightChange")
	public String toTcflightChange(String flightId,String orderSource,Model model) throws ParseException{
		Flight flight = fligthService.getById(flightId);
		if(StringUtils.isEmpty(flight.getChangeCode())){
			flight.setChangeCode("99");
		}
		String depTime=flight.getDepTime();
		if(depTime.split(":").length<3){
			flight.setDepTime(flight.getDepTime()+":00");
			flight.setArrTime(flight.getArrTime()+":00");
		}
		String arrDate=flight.getFlightArrDate();
		if(StringUtils.isEmpty(arrDate)){
			flight.setFlightArrDate(flight.getFlightDepDate());
			String arrTime=flight.getArrTime();
			if(arrTime.startsWith("00")){				
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(flight.getFlightDepDate()));
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				String format = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
				flight.setFlightArrDate(format);
			}
		}
		flight.setDepTimes(flight.getFlightDepDate()+" "+flight.getDepTime());
		flight.setArrTimes(flight.getFlightArrDate()+" "+flight.getArrTime());
		model.addAttribute("flight", flight);
		if(InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)){			
			return PREFIX + "tcFlightChange.html";
		}else if(InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)){
			return PREFIX + "xcFlightChange.html";
		}else if(InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource)){
			return PREFIX + "jiuFlightChange.html";
		}else if(InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)){
			return PREFIX + "qnrFlightChange.html";
		}else if(InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)){
			return PREFIX + "tbFlightChange.html";
		}
		return "";
	}


	/**
	 * 获取航变
	 * @param
	 * @return
	 */
	@PostMapping("/getFlightInfo") 
	@ResponseBody
	public Object getFlightInfo(HttpServletRequest request){
		LayuiPageInfo result = new LayuiPageInfo();
		try {
			OrderQuery query = this.createQueryData(request);
			List<FlightVO> flightChangneOrder = fligthService.getFlightChangneInfo(query);
			result.setCount(fligthService.getFlightChangneInfoCount(query));
			result.setData(flightChangneOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 航变提交
	 * @param jsonParam
	 * @return
	 */
	@PostMapping(value="/subFlightChange",produces = "application/json;charset=UTF-8") 
	@ResponseBody
	public Object subFlightChange(@RequestBody JSONObject jsonParam){
		ResponseResult<Void> result=new ResponseResult<Void>();
		try {
			String orderSource = jsonParam.getString("orderSource");
			if(InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)){
				String subFlightChange = tcOrderService.subFlightChange(jsonParam);
				JSONObject fromObject = JSONObject.fromObject(subFlightChange);
				if(fromObject.getBoolean("isSuccess")){
					result.setMsg("录入成功");
					Flight f=new Flight();
					switch (jsonParam.getInt("changeType")) {
					case 1:
						f.setFlightChange("航班延误");
						break;
					case 2:
						f.setFlightChange("航班提前");
						break;
					case 3:
						f.setFlightChange("航班取消");
						break;
					case 4:
						f.setFlightChange("航班恢复");
						break;
					case 5:
						f.setFlightChange("航班号变动");
						break;
					case 6:
						f.setFlightChange("机场变动");
						break;
					default:
						break;
					}
					if(jsonParam.getInt("changeType")!=3){
						f.setNewArrCode(jsonParam.getString("newEndPort"));
						f.setNewDepCode(jsonParam.getString("newStartPort"));
						f.setNewArrDate(jsonParam.getString("newArrTime"));
						f.setNewDepDate(jsonParam.getString("newDepTime"));
						f.setNewFlightNo(jsonParam.getString("newFlightNo"));
					}
					f.setChangeCode(jsonParam.getInt("changeType")+"");
					f.setFlightId(Integer.valueOf(jsonParam.getString("flightId")));
					f.setModifyDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					fligthService.updateById(f);
				}else{
					result.setCode(-1);
					result.setMsg(StringUtils.isEmpty(fromObject.getString("errorMessage"))?"录入失败":fromObject.getString("errorMessage"));					
				}
			}else if(InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)){
				String subFlightChange = ctripOrderService.subFlightChange(jsonParam);
				Document doc = DocumentHelper.parseText(subFlightChange);
				Element rootElement = doc.getRootElement();
				Element element = rootElement.element("Header");
				String attributeValue = element.attributeValue("ResultCode");
				result.setMsg(element.attributeValue("ResultMsg"));
				if("Success".equals(attributeValue)){
					result.setMsg("录入成功");
					Flight f=new Flight();
					f.setChangeCode(jsonParam.getString("FlightChangeType"));
					f.setFlightId(Integer.valueOf(jsonParam.getString("flightId")));
					f.setModifyDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					if(jsonParam.getString("FlightChangeType").equals("0")){
						f.setFlightChange("航班变化");
						f.setNewArrCode(jsonParam.getString("ProtectAPort"));
						f.setNewDepCode(jsonParam.getString("ProtectDPort"));
						f.setNewArrDate(jsonParam.getString("ProtectAdate"));
						f.setNewDepDate(jsonParam.getString("ProtectDdate"));
						f.setNewFlightNo(jsonParam.getString("ProtectFlight"));
					}else{
						f.setFlightChange("航班取消");
					}
					fligthService.updateById(f);
				}else{
					result.setCode(-1);
					result.setMsg("航变录入失败!");
				}
			}else if(InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)){
				String airChangeNotic = ttsOrderService.airChangeNotic(jsonParam);
				JSONObject fromObject = JSONObject.fromObject(airChangeNotic);
				if(fromObject.getInt("status")==0){
					result.setMsg("录入成功");
					Flight f=new Flight();
					f.setChangeCode(jsonParam.getString("changeType"));
					f.setFlightId(Integer.valueOf(jsonParam.getString("flightId")));
					f.setModifyDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					if(jsonParam.getInt("changeType")==1){
						f.setFlightChange("航班变化");
					}else{
						f.setFlightChange("航班取消");
					}
					f.setNewArrCode(jsonParam.getString("newEndPort"));
					f.setNewDepCode(jsonParam.getString("newStartPort"));
					f.setNewArrDate(jsonParam.getString("newArrTime"));
					f.setNewDepDate(jsonParam.getString("newDepTime"));
					f.setNewFlightNo(jsonParam.getString("newFlightNo"));
					fligthService.updateById(f);
				}else{
					result.setMsg("航变录入失败!");
					result.setCode(-1);
				}
			}else if(InterfaceConstant.ORDER_SOURCE_JIU.equals(orderSource)){
				JSONObject param=new JSONObject();
				int type = jsonParam.getInt("changeType");
				if(type==0){
					param.accumulate("flightChangeType", "CHANGE");
				}else if(type==1){
					param.accumulate("flightChangeType", "CANCEL");
				}else{
					param.accumulate("flightChangeType", "RECOVERY");
				}
				param.accumulate("preFlightNo", jsonParam.getString("orgFlightNo"));
				param.accumulate("preDptAirport", jsonParam.getString("orgStartPort"));
				param.accumulate("preArrAirport", jsonParam.getString("orgEndPort"));
				param.accumulate("preDptDate", jsonParam.getString("orgDepTime").split(" ")[0]);
				param.accumulate("preDptTime", jsonParam.getString("orgDepTime").split(" ")[1]);
				param.accumulate("preArrDate", jsonParam.getString("orgArrTime").split(" ")[0]);
				param.accumulate("preArrTime", jsonParam.getString("orgArrTime").split(" ")[1]);
				if(!StringUtils.isEmpty(jsonParam.getString("newArrTime"))){
					String[] orgArrTimeArr = jsonParam.getString("newArrTime").split(" ");
					param.accumulate("folArrTime", orgArrTimeArr[1]);
					param.accumulate("folArrDate", orgArrTimeArr[0]);
				}
				if(!StringUtils.isEmpty(jsonParam.getString("newDepTime"))){
					String[] newDepTimeArr = jsonParam.getString("newDepTime").split(" ");
					param.accumulate("folDptTime", newDepTimeArr[1]);
					param.accumulate("folDptDate", newDepTimeArr[0]);
				}
				if(!StringUtils.isEmpty(jsonParam.getString("newFlightNo"))){
					param.accumulate("folFlightNo", jsonParam.getString("newFlightNo"));
				}
				if(!StringUtils.isEmpty(jsonParam.getString("newStartPort"))){					
					param.accumulate("folDptAirport", jsonParam.getString("newStartPort"));
				}
				if(!StringUtils.isEmpty(jsonParam.getString("newEndPort"))){						
					param.accumulate("folArrAirport", jsonParam.getString("newEndPort"));
				}
				param.accumulate("orderNo", jsonParam.getString("orderNo"));
				boolean subFlightChange = jiuOrderServer.subFlightChange(param);
				if(subFlightChange){
					result.setMsg("录入成功");
					Flight f=new Flight();
					f.setChangeCode(jsonParam.getString("changeType"));
					f.setFlightId(Integer.valueOf(jsonParam.getString("flightId")));
					f.setModifyDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					if(type==0){
						f.setFlightChange("航班变化");
					}else if(type==1){
						f.setFlightChange("航班取消");
					}else{
						f.setFlightChange("航班恢复");
					}
					f.setNewArrCode(jsonParam.getString("newEndPort"));
					f.setNewDepCode(jsonParam.getString("newStartPort"));
					f.setNewArrDate(jsonParam.getString("newArrTime"));
					f.setNewDepDate(jsonParam.getString("newDepTime"));
					f.setNewFlightNo(jsonParam.getString("newFlightNo"));
					fligthService.updateById(f);
				}else{
					result.setMsg("航变录入失败!");
					result.setCode(-1);
				}

			}else if(InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)){
				boolean subFlightChange = tbOrderService.subFlightChange(jsonParam);
				if(subFlightChange){
					result.setMsg("录入成功");
					Flight f=new Flight();
					f.setChangeCode(jsonParam.getString("changeType"));
					f.setFlightId(Integer.valueOf(jsonParam.getString("flightId")));
					f.setModifyDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					int type = jsonParam.getInt("changeType");
					if(type==1){
						f.setFlightChange("航班变更");
					}else{
						f.setFlightChange("航班取消");
					}
					f.setNewArrCode(jsonParam.getString("newEndPort"));
					f.setNewDepCode(jsonParam.getString("newStartPort"));
					f.setNewArrDate(jsonParam.getString("newArrTime"));
					f.setNewDepDate(jsonParam.getString("newDepTime"));
					f.setNewFlightNo(jsonParam.getString("newFlightNo"));
					fligthService.updateById(f);
				}else{
					result.setMsg("航变录入失败!");
					result.setCode(-1);
				}
			}

		} catch (Exception e) {
			logg.error("航变录入失败",e);
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg("航变录入失败!");
		}
		return result;
	}





    @ResponseBody
    @RequestMapping("/changeFlightNo")
    public Object verifyTicketNo(String newFlightNo,String flightId){
        ResponseResult<Void> result = new ResponseResult<Void>();
        try {
            Flight flight = new Flight();
            flight.setFlightId(Integer.valueOf(flightId));
            flight.setFlightNo(newFlightNo);
            fligthService.updateById(flight);
        } catch (Exception e) {
            result.setCode(-1);
        }
        return  result;
    }






























	@PostMapping("/getFlightList") 
	@ResponseBody
	public Object getFlightList(HttpServletRequest request){
		LayuiPageInfo result = new LayuiPageInfo();
		try {
			Integer page=StringUtils.isEmpty(request.getParameter("page"))?0:Integer.parseInt(request.getParameter("page"));
			Integer limit=StringUtils.isEmpty(request.getParameter("limit"))?10:Integer.parseInt(request.getParameter("limit"));
			List<FlightVO> flightChangneList = fligthService.getFlightChangneList((page-1)*limit,limit);
			result.setCount(fligthService.getFlightChangneCount());
			result.setData(flightChangneList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	private OrderQuery createQueryData(HttpServletRequest request) throws ParseException{
		OrderQuery query=new OrderQuery();	
		if(!StringUtils.isEmpty(request.getParameter("name"))){
			query.setName(request.getParameter("name").trim());
		}
		if(!StringUtils.isEmpty(request.getParameter("ticketNo"))){
			query.setTicketNo(request.getParameter("ticketNo").trim());
		}
		if(!StringUtils.isEmpty(request.getParameter("orderNo"))){
			query.setOrderNo(request.getParameter("orderNo").trim());
		}
		if(!StringUtils.isEmpty(request.getParameter("flightNo"))){
			query.setFlight(request.getParameter("flightNo").trim());
		}
		String startDate=request.getParameter("startDate");
		String endDate=request.getParameter("endDate");
		if(StringUtils.isEmpty(startDate)){
			startDate=DateFormatUtils.format(new Date(), "yyyy-MM-dd");
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 30);
			endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
		}
		query.setStartDate(startDate);
		query.setEndDate(endDate);
		Integer page=StringUtils.isEmpty(request.getParameter("page"))?0:Integer.parseInt(request.getParameter("page"));
		Integer limit=StringUtils.isEmpty(request.getParameter("limit"))?10:Integer.parseInt(request.getParameter("limit"));
		query.setPage(page);
		query.setLimit(limit);
		query.setJump((page-1)*limit);
		return query;
	}


}
