$(document).ready(function() {
	var element;
	var table;
	var layer;
	layui.use(['layer', 'form', 'table', 'laydate', 'element'], function() {
		layer = layui.layer;
		table = layui.table;
		element = layui.element;
	});
	var url = Feng.ctxPath + '/passenger/getPassengersByOrderNo';
	var orderNo = $("#orderNo").text();
	$.ajax({
		"url": url,
		"type": "POST",
		"data": {
			orderNo: orderNo,
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == -1) {
				layer.msg(json.msg);
			} else {
				var passengerList = json.data;
				$("#passengerList").empty();
				for(var i = 0; i < passengerList.length; i++) {
					var passenger = passengerList[i];
					var html = "<form class='layui-form passenger'>" +
						"<div><table class='layui-table'><colgroup><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'></colgroup>" +
						"<thead><tr>" +
						"<th>姓名：#{name}</th>" +
						"<th>身份证：#{certNo}</th>" +
						"<th>PNR：#{pnr}</th>" +
						"<th>票号：#{ticketNo}</th>" +
						"<th>实收金额：￥#{actualPrice}</th>" +
						"</tr></thead></table>" +
						"<table class='layui-table'><colgroup><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'></colgroup>" +
						"<thead><tr>" +
						"<th>改签状态(乘)</th><th>改签单号</th><th>改签申请日期(乘)</th><th>改签费用(乘)</th><th>改签仓位</th><th>改签日期</th><th>改签航班号</th>" +
						"</tr></thead>" +
						"<tbody>" +
						"<tr>" +
						"<td><select name='state' id='state#{id}'><option value='1'>改签申请中</option><option value='2'>改签完成</option><option value='3'>拒绝改签</option></select></td>" +
						"<td><input type='text' name='newCOrderNo' id='newCOrderNo#{id}' required lay-verify='required' class='layui-input'></td>" +
						"<td><input type='text' name='changeDate' id='changeDate#{id}' class='layui-input refundDate'></td>" +
						"<td><input type='text' name='revenuePrice' id='revenuePrice#{id}' required lay-verify='required' class='layui-input'></td>" +
						"<td><input type='text' name='cabin' id='cabin#{id}' class='layui-input'></td>" +
						"<td><input type='text' name='flightDate' id='flightDate#{id}' class='layui-input fDate'></td>" +
						"<td><input type='text' name='flightNo' id='flightNo#{id}' class='layui-input'></td>" +
						"</tr>" +
						"</tbody></table>" +

						"<input type='hidden' name='orderId' value='#{orderId}'/><input type='hidden' name='orderNo' value='#{orderNo}'/>" +
						"<input type='hidden' name='orderSource' value='#{orderSource}'/><input type='hidden' name='orderShop' value='#{orderShop}'/>" +
						"<input type='hidden' name='passengerId' value='#{passengerId}'/><input type='hidden' name='sAirlineCode' value='#{sAirlineCode}'/>" +
						"<input type='hidden' name='sFlightNo' value='#{sFlightNo}'/><input type='hidden' name='sDepCityCode' value='#{sDepCityCode}'/>" +
						"<input type='hidden' name='sArrCityCode' value='#{sArrCityCode}'/><input type='hidden' name='sFlightDate' value='#{sFlightDate}'/>" +
						"<input type='hidden' name='sCabin' value='#{sCabin}'/><input type='hidden' name='passengerName' value='#{passengerName}'/>" +
						"<input type='hidden' name='ticketNo' value='#{ticketNo}'/><input type='hidden' name='purchPalse' value='#{purchPalse}'/>" +
						"<input type='hidden' name='changeId' id='changeId#{id}' value=''/>" +
						"</div></from>" +
						"<hr class='layui-bg-black'>";
					html = html.replace(/#{name}/g, passenger.name);
					html = html.replace(/#{certNo}/g, passenger.certNo);
					html = html.replace(/#{pnr}/g, passenger.pnr);
					html = html.replace(/#{ticketNo}/g, passenger.ticketNo);
					html = html.replace(/#{actualPrice}/g, passenger.actualPrice);
					html = html.replace(/#{purchPalse}/g, passenger.purchPalse);
					html = html.replace(/#{orderId}/g, passenger.orderId);
					html = html.replace(/#{orderNo}/g, passenger.orderNo);
					html = html.replace(/#{orderSource}/g, passenger.orderSource);
					html = html.replace(/#{orderShop}/g, passenger.orderShop);
					html = html.replace(/#{passengerId}/g, passenger.passengerId);
					html = html.replace(/#{sAirlineCode}/g, passenger.airlineCode);
					html = html.replace(/#{sFlightNo}/g, passenger.flightNo);
					html = html.replace(/#{sDepCityCode}/g, passenger.depCityCode);
					html = html.replace(/#{sArrCityCode}/g, passenger.arrCityCode);
					html = html.replace(/#{sFlightDate}/g, passenger.flightDepDate);
					html = html.replace(/#{sCabin}/g, passenger.printTicketCabin);
					html = html.replace(/#{passengerName}/g, passenger.name);
					html = html.replace(/#{id}/g, passenger.passengerId);
					$("#passengerList").append(html);
					var id = passenger.passengerId;
					var change = passenger.change;
					if(change != "") {
						$("#state" + id + " option[value='" + change.state + "']").attr("selected", "selected");
						$("#newCOrderNo" + id).val(change.newCOrderNo);
						$("#changeDate" + id).val(change.changeDate);
						$("#revenuePrice" + id).val(change.revenuePrice);
						$("#cabin" + id).val(change.cabin);
						$("#flightDate" + id).val(change.flightDate);
						$("#flightNo" + id).val(change.flightNo);
						$("#changeId" + id).val(change.changeId);
					}
				}
				layui.form.render();
				$(".layui-bg-black").last().remove();
				$('.refundDate').each(function(index) {
					var _this = this;
					layui.laydate.render({
						elem: _this,
						format: 'yyyy-MM-dd HH:mm:ss'
					});
				});
					$('.fDate').each(function(index) {
					var _this = this;
					layui.laydate.render({
						elem: _this,
						format: 'yyyy-MM-dd'
					});
				});
			}
		}
	});
});
$("#saveChange").click(function() {
	var data = new Array();
	var isAll = false; //信息是否完善
	$.each($(".passenger"), function(index) {
		var changeInfo = $(this).serializeObject();
		if(changeInfo.newCOrderNo != "") {
			isAll = true;
			data.push(changeInfo);
		}
	});
	if(!isAll) {
		layer.msg('请输入改签单号', {
			icon: 5
		});
	}
	var result = JSON.stringify(data);
	if(result == '[]') {
		return;
	}
	$.ajax({
		"url": Feng.ctxPath + '/change/saveChanges',
		"type": "POST",
		"data": result,
		"dataType": "JSON",
		"contentType": "application/json",
		"success": function(json) {
			layer.msg(json.msg);
		}
	});
});
/*<table class='layui-table'><colgroup><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'></colgroup>" +
		"<thead><tr><th>出票地</th><th>交易流水号</th><th>采购价格</th><th>支付方式</th><th>新票号</th><th>备注</th></tr></thead><tbody>" +
		"<tr><th class='layui-form'><select name='supplier' id='supplier#{id}'>" +
		"<option value='5'>客服</option><option value='1'>官网</option><option value='18'>B2B</option><option value='17'>鹏朋</option><option value='13'>BSP</option><option value='99'>其他</option></select>" +
		"</th><th><input type='text' name='tradeNo' id='tradeNo#{id}' class='layui-input'></th>" +
		"<th><input type='number' name='payAmount' id='payAmount#{id}' class='layui-input'></th>" +
		"<select name='payWay' id='payWay#{id}'>" +
		"<option value='5'>易宝支付</option><option value='1'>罗137支付宝</option><option value='18'>凤凰卡</option><option value='17'>BSP</option><option value='13'>ssqly支付宝</option><option value='99'>其他</option></select>" +
		"</th><th><input type='text' name='newTicketNo' id='newTicketNo#{id}' class='layui-input'></th>" +
		"<th><input type='text' name='remark' id='remark#{id}' class='layui-input'></th></tr>" +
		"</tbody>*/
$.fn.serializeObject  =   function ()  {    
	var  o  =   {};    
	var  a  =  this.serializeArray();    
	$.each(a,  function ()  {        
		if (o[this.name]  !==  undefined)  {            
			if (!o[this.name].push)  {                
				o[this.name]  =   [o[this.name]];            
			}            
			o[this.name].push(this.value  ||  '');        
		} 
		else  {            
			o[this.name]  =  this.value  ||  '';        
		}    
	});    
	return  o;
};