var param;
layui.use(['layer', 'form', 'table', 'laydate', 'element'], function() {
	var layer = layui.layer;
	var table = layui.table;
	var element = layui.element;
	var form = layui.form;
	var laydate = layui.laydate;

	var src = parent.$("#purchIframe").attr("src");
	param = src.split("?")[1];
	var url = Feng.ctxPath + '/purch/getPurch';


	laydate.render({
		elem: '#returnDate',
		format: 'yyyy-MM-dd HH:mm:ss'
	});

	$.ajax({
		"url": url,
		"type": "POST",
		"data": param,
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == -1) {
				layer.msg(json.msg);
			} else {
				var data = json.data;
				var order = data.orderVo;
				var flight = data.flightList;
				var passenger = order.passengetList;
				var purchList = data.purchList;

				$("#orderNoText").text(order.orderNo);
				$("#orderSourceText").text(order.orderSource);
				$("#policyIdText").text(order.policyType);
				$("#pnrText").text(order.pnr);
				$("#orderStatusText").text(order.status);

				for(var i = 0; i < flight.length; i++) {
					var html = "<tr>" +
						"<td rowspan='2'>#{type}</td>" +
						"<td>特惠</td>" +
						"<td>预付产品</td>" +
						"<td>#{flightNo}</td>" +
						"<td>#{cabin}</td>" +
						"<td>#{depCityCode} - #{arrCityCode}</td>" +
						"<td>#{depTimes} - #{arrTimes}</td>" +
						"<td><a href='javacript:void(0);' style='color: blue;' id='orderxq'>详情</a></td>"
					"</tr>";
					html = html.replace(/#{type}/g, order.tripType);
					html = html.replace(/#{flightNo}/g, flight[i] == null ? "" : flight[i].flightNo);
					html = html.replace(/#{depCityCode}/g, flight[i] == null ? "" : flight[i].depCityCode);
					html = html.replace(/#{arrCityCode}/g, flight[i] == null ? "" : flight[i].arrCityCode);
					html = html.replace(/#{depTimes}/g, flight[i] == null ? "" : flight[i].depTimes);
					html = html.replace(/#{arrTimes}/g, flight[i] == null ? "" : flight[i].arrTimes);
					html = html.replace(/#{cabin}/g, flight[i] == null ? "" : flight[i].cabin);
					$("#flightInfo").append(html);
					/*var index;
					$('#orderxq').mouseover(
						function() {
							index = layer.tips(
							'航空公司：深圳航空<br/>里程：xxx公里  &nbsp;&nbsp;飞行时间： xxx',
							'#orderxq', {
								tips : [ 3, '#78BA32' ],
								area : [ '345px' ]
							});
					 });
					$("#orderxq").mouseout(function() {
						layer.close(index);
					});*/
				}
				//统计乘客类型
				var adult = 0;
				var adultTax = 0;
				var adultTicketPrice = 0;
				var adultActualPrice = 0;
				var childen = 0;
				var childenTax = 0;
				var childenTicketPrice = 0;
				var childenActualPrice = 0;
				var baby = 0;
				var babyTax = 0;
				var babyTicketPrice = 0;
				var babyActualPrice = 0;
				for(var i = 0; i < passenger.length; i++) {
					if(passenger[i].passengerType == '成人') {   
						adult++;
						if(adultTax == 0) {
							adultTax = parseInt(passenger[i].tax)+parseInt(passenger[i].fee);
							adultTicketPrice = passenger[i].ticketPrice;
							adultActualPrice = passenger[i].actualPrice;
						}
					} else if(passenger[i].passengerType == '儿童') {
						childen++;
						if(childenTax == 0) {
							childenTax = parseInt(passenger[i].tax)+parseInt(passenger[i].fee);
							childenTicketPrice = passenger[i].ticketPrice;
							childenActualPrice = passenger[i].actualPrice;
						}
					} else if(passenger[i].passengerType == '婴儿') {
						baby++;
						if(babyTax == 0) {
							babyTax = parseInt(passenger[i].tax)+parseInt(passenger[i].fee);
							babyTicketPrice = passenger[i].ticketPrice;
							varbabyActualPrice = passenger[i].actualPrice;
						}
					}
				}
				if(adult != 0) {
					var html = "<tr>" +
						"<td>成人</td>" +
						"<td>#{passengerCount}</td>" +
						"<td>¥#{ticketPrice}</td>" +
						"<td>¥#{totalTax}</td>" +
						"<td>¥0(0份)</td>" +
						"<td>¥0(0份)</td>" +
						"<td>¥#{actualPrice}</td>" +
						"<td>-</td>" +
						"<td>-</td>" +
						"<td>-</td>" +
						"<td rowspan='3'>¥#{totalPrice}</td>" +
						"<tr>";
					html = html.replace(/#{passengerCount}/g, adult);
					html = html.replace(/#{ticketPrice}/g, adultTicketPrice);
					html = html.replace(/#{actualPrice}/g, adultActualPrice);
					html = html.replace(/#{totalTax}/g, adultTax);
					html = html.replace(/#{totalPrice}/g, order.totalPrice);
					$("#orderInfo").append(html);
				}
				if(childen != 0) {
					var html = "<tr>" +
						"<td>儿童</td>" +
						"<td>#{passengerCount}</td>" +
						"<td>¥#{ticketPrice}</td>" +
						"<td>¥#{totalTax}</td>" +
						"<td>¥0(0份)</td>" +
						"<td>¥0(0份)</td>" +
						"<td>¥#{actualPrice}</td>" +
						"<td>-</td>" +
						"<td>-</td>" +
						"<td>-</td>" +
						"#{td}" +
						"<tr>";
					html = html.replace(/#{passengerCount}/g, childen);
					html = html.replace(/#{ticketPrice}/g, childenTicketPrice);
					html = html.replace(/#{actualPrice}/g, childenActualPrice);
					html = html.replace(/#{totalTax}/g, childenTax);
					html = html.replace(/#{totalPrice}/g, order.totalPrice);
					if(adult == 0) {
						html = html.replace(/#{rowspan}/g, "<th rowspan='3'>¥" + order.totalPrice + "</th>");
					} else {
						html = html.replace(/#{rowspan}/g, "");
					}
					$("#orderInfo").append(html);
				}
				if(baby != 0) {
					var html = "<tr>" +
						"<td>婴儿</td>" +
						"<td>#{passengerCount}</td>" +
						"<td>¥#{ticketPrice}</td>" +
						"<td>¥#{totalTax}</td>" +
						"<td>¥0(0份)</td>" +
						"<td>¥0(0份)</td>" +
						"<td>¥#{actualPrice}</td>" +
						"<td>-</td>" +
						"<td>-</td>" +
						"<td>-</td>" +
						"<tr>";
					html = html.replace(/#{passengerCount}/g, baby);
					html = html.replace(/#{ticketPrice}/g, babyTicketPrice);
					html = html.replace(/#{actualPrice}/g, babyActualPrice);
					html = html.replace(/#{totalTax}/g, babyTax);
					$("#orderInfo").append(html);
				}

				var hasChildren = false;
				for(var i = 0; i < passenger.length; i++) {
					var html = "<tr>" +
						"<th>#{index}</th>" +
						"<th>#{passengerType}</th>" +
						"<th>#{passengerName}</th>" +
						"<th>#{certType}</th>" +
						"<th>#{certNo}</th>" +
						"<th>#{ticketNo}</th>" +
						"<tr>";
					html = html.replace(/#{index}/g, i + 1);
					html = html.replace(/#{passengerType}/g, passenger[i] == null ? "" : passenger[i].passengerType);
					html = html.replace(/#{passengerName}/g, passenger[i] == null ? "" : passenger[i].name);
					html = html.replace(/#{certType}/g, passenger[i] == null ? "" : passenger[i].certType);
					html = html.replace(/#{certNo}/g, passenger[i] == null ? "" : passenger[i].certNo);
					html = html.replace(/#{ticketNo}/g, passenger[i] == null ? "" : passenger[i].ticketNo);
					$("#passengerInfo").append(html);

					var html2 = "<input type='checkbox' name='passengerNames' title='#{passengerName}' value='#{passengerName}'  checked='checked' required lay-verify='required'><br/>";
					html2 = html2.replace(/#{passengerName}/g, passenger[i] == null ? "" : passenger[i].name);
					$("#passengerNames").append(html2);
					form.render();
					if(passenger[i].passengerType == '儿童' || passenger[i].passengerType == '婴儿') {
						hasChildren = true;
					}
				}
				if(hasChildren) {
					var childrenHtml = "<input type='hidden' name='hasChildren' value='1'/>";
					$("#save").append(childrenHtml);
				}
				for(var i = 0; i < purchList.length; i++) {
					var html = "<tr>" +
						"<th>#{tradeNo}</th>" +
						"<th>#{supplier}</th>" +
						"<th>#{payAmount}</th>" +
						"<th>#{payWay}</th>" +
						"<td>#{remark}</td>" +
						"<td>#{passengerNames}</td>" +
						"<th>#{type}</th>" +
						"<th>#{returnMoney}</th>" +
						"<th>#{returnDate}</th>" +
						"<td><button type='button' class='layui-btn layui-btn-xs layui-btn-normal' onclick='invalid(#{purchaseId})'>作废</button></td>" +
						"<tr>";
					html = html.replace(/#{tradeNo}/g, purchList[i] == null ? "" : purchList[i].tradeNo);
					html = html.replace(/#{purchaseId}/g, purchList[i] == null ? "" : purchList[i].purchaseId);
					html = html.replace(/#{passengerNames}/g, purchList[i] == null ? "" : purchList[i].passengerNames);
					html = html.replace(/#{supplier}/g, purchList[i] == null ? "" : purchList[i].supplier);
					html = html.replace(/#{payAmount}/g, purchList[i] == null ? "" : purchList[i].payAmount);
					html = html.replace(/#{payWay}/g, purchList[i] == null ? "" : purchList[i].payWay);
					html = html.replace(/#{remark}/g, purchList[i] == null ? "" : purchList[i].remark);
					html = html.replace(/#{type}/g, purchList[i] == null ? "" : purchList[i].type);
					html = html.replace(/#{returnMoney}/g, purchList[i] == null ? "" : purchList[i].returnMoney);
					html = html.replace(/#{returnDate}/g, purchList[i] == null ? "" : purchList[i].returnDate);
					$("#purchList").append(html);
				}
				$("#orderNo").val(order.orderNo);
				$("#orderId").val(order.orderId);
				$("#passengerId").val(passenger[0] == null ? "" : passenger[0].passengerId);
			}
		}
	});

	//监听提交
	form.on('submit(purchForm)', function(data) {
		var data = $('#purch').serialize();
		if(data.indexOf("passengerNames") == -1) {
			layer.msg("至少选择一名乘客！");
			return false;
		}
		if($("#supplier").val() == 0) {
			layer.msg("请选择出票地！");
			return false;
		}
		if($("#payWay").val() == 0) {
			layer.msg("请选择支付方式！");
			return false;
		}
		if($("#purchType").val()==1){
			if($("#returnMoney").val()==""){
				layer.msg("差错报表要输入退款金额");
				return false;
			}
		}

		var url = Feng.ctxPath + '/purch/addPurch';
		$.ajax({
			"url": url,
			"type": "POST",
			"data": data,
			"dataType": "JSON",
			"success": function(json) {
				layer.msg(json.msg);
				$('#purch')[0].reset();
				reloadPurch();
			}
		});
		return false;
	});

});

function reloadPurch() {
	$.ajax({
		"url": Feng.ctxPath + '/purch/getPruchListByOrderNo',
		"type": "POST",
		"data": param,
		"dataType": "JSON",
		"success": function(json) {
			var purchList = json.data;
			$("#purchList").empty();
			for(var i = 0; i < purchList.length; i++) {
				var html = "<tr>" +
					"<th>#{tradeNo}</th>" +
					"<th>#{supplier}</th>" +
					"<th>#{payAmount}</th>" +
					"<th>#{payWay}</th>" +
					"<td >#{remark}</td>" +
					"<td>#{passengerNames}</td>" +
					"<th>#{type}</th>" +
					"<th>#{returnMoney}</th>" +
					"<th>#{returnDate}</th>" +
					"<td><button type='button' class='layui-btn layui-btn-xs layui-btn-normal' onclick='invalid(#{purchaseId})'>作废</button></td>" +
					"<tr>";
				html = html.replace(/#{purchaseNo}/g, purchList[i] == null ? "" : purchList[i].purchaseNo);
				html = html.replace(/#{tradeNo}/g, purchList[i] == null ? "" : purchList[i].tradeNo);
				html = html.replace(/#{purchaseId}/g, purchList[i] == null ? "" : purchList[i].purchaseId);
				html = html.replace(/#{passengerNames}/g, purchList[i] == null ? "" : purchList[i].passengerNames);
				html = html.replace(/#{supplier}/g, purchList[i] == null ? "" : purchList[i].supplier);
				html = html.replace(/#{payAmount}/g, purchList[i] == null ? "" : purchList[i].payAmount);
				html = html.replace(/#{payWay}/g, purchList[i] == null ? "" : purchList[i].payWay);
				html = html.replace(/#{remark}/g, purchList[i] == null ? "" : purchList[i].remark);
				html = html.replace(/#{type}/g, purchList[i] == null ? "" : purchList[i].type);
				html = html.replace(/#{returnMoney}/g, purchList[i] == null ? "" : purchList[i].returnMoney);
				html = html.replace(/#{returnDate}/g, purchList[i] == null ? "" : purchList[i].returnDate);
				$("#purchList").append(html);
			}
		}
	});
}
//采购单作废
function invalid(purchaseId) {
	layer.confirm('确认作废吗', function(index) {
		$.ajax({
			"url": Feng.ctxPath + '/purch/updatePurchaseFlag',
			"type": "POST",
			"data": {
				purchaseId: purchaseId
			},
			"dataType": "JSON",
			"success": function(json) {
				if(json.code == 0) {
					layer.msg(json.msg);
					reloadPurch();
				} else {
					layer.msg("修改失败");
				}

			}
		});
		layer.close(index);
	});
}