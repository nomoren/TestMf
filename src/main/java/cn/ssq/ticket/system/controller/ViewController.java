package cn.ssq.ticket.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class ViewController {

	private String PREFIX = "/modular/system/";

	//同程订单驳回页
	@RequestMapping("/refuseOrder")
	public String refuseOrder(){
		return PREFIX + "other/refuseOrder.html";
	}

}
