package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.Passenger;
import cn.ssq.ticket.system.entity.ResponseResult;
import cn.ssq.ticket.system.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@RequestMapping("/passenger")
@Controller
public class PassengerController {

	@Autowired
	private PassengerService passengerService;

	/**
	 * 查询订单关联的所有乘机人
	 * @param orderNo
	 * @return
	 */
	@PostMapping("/getPassengersByOrderNo")
	@ResponseBody
	public Object getByOrderNo(String orderNo){
		ResponseResult<List<Passenger>> result=new ResponseResult<List<Passenger>>();
		if(StringUtils.isEmpty(orderNo)){
			result.setCode(-1);
			result.setMsg("查询乘机人数据失败！");
			return result;
		}
		try {
			List<Passenger> list=passengerService.getPassengersByOrderNo(orderNo);
			result.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg("查询乘机人数据失败！");
		}
		return result;
	}

	/**
	 * 查询退票单关联的所有乘机人
	 * @param
	 * @return
	 */
	@PostMapping("/getPassengersByRetNo")
	@ResponseBody
	public Object getByCertNo(String retNo){
		ResponseResult<List<Passenger>> result=new ResponseResult<List<Passenger>>();
		if(StringUtils.isEmpty(retNo)){
			result.setCode(-1);
			result.setMsg("查询乘机人数据失败！");
			return result;
		}
		try {
			List<Passenger> list=passengerService.getPassengersByRetNo(retNo);
			result.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg("查询乘机人数据失败！");
		}
		return result;
	}

	@PostMapping("/saveTicketNo")
	@ResponseBody
	public Object saveTicketNo(Passenger passenger){
		ResponseResult<Void> result=new ResponseResult<Void>();
		try {
			passenger.setTicketStatus("1");
			Integer count = passengerService.updateById(passenger);
			if(count!=1){
				result.setCode(-1);
			}
		} catch (Exception e) {
			result.setCode(-1);
		}
		return result;
	}
}
