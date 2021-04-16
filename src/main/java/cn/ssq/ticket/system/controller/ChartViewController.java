package cn.ssq.ticket.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;

@RequestMapping("/chart")
@Controller
public class ChartViewController {
	

	private String PREFIX = "/modular/system/charts/";

	//首页
	@RequestMapping("/toChartIndex")
	public String orderIndex(){
		return PREFIX + "chart_index.html";
	}
	
	@PostMapping("/getPrintTicketDate")
	@ResponseBody
	public Object getPrintTicketDate(){
		Calendar calendar=Calendar.getInstance();


		
		
		
		
		
		
		
		
		
		return null;
	}

}
