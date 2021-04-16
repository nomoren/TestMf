var layer;
var orderNo = $("#orderNo").val();
layui.use(['layer', 'form', 'table', 'admin', 'ax', 'laydate', 'element'], function() {
	var $ = layui.$;
	layer = layui.layer;
	var form = layui.form;
	var table = layui.table;
	var $ax = layui.ax;
	var admin = layui.admin;
	var laydate = layui.laydate;
	var element = layui.element;
	var Log = {
		tableId: "logTable" //表格id
	};

	var storage = window.localStorage;
	$("#tradeNo").val(storage.tradeNo);
	$("#purchPayAmount").val(storage.purchPayAmount);
	$("#purchRemark").val(storage.purchRemark);
	$("#returnMoney").val(storage.returnMoney);
	$("#returnDate").val(storage.returnDate);
	storage.clear();

	Log.initColumn = function() {
		return [
			[{
					field: 'type',
					sort: false,
					title: '类型',
					align: 'center',
				},
				{
					field: 'date',
					sort: false,
					title: '记录时间',
					align: 'center',
				},
				{
					field: 'name',
					sort: false,
					title: '操作人',
					align: 'center',
				},
				{
					field: 'content',
					sort: false,
					title: '操作内容',
					align: 'center',
					width: 700
				}
			]
		];
	};

	var orderSource = $("#orderSourceText").text();
	//日志
	table.render({
		elem: '#' + Log.tableId,
		height: 500,
		url: Feng.ctxPath + '/order/getLogByOrderNo?orderNo=' + orderNo //数据接口
			,
		method: "post",
		page: true //开启分页
			,
		even: true,
		cols: Log.initColumn()
	});

	laydate.render({
		elem: '#returnDate',
		format: 'yyyy-MM-dd HH:mm:ss'
	});

	//获取采购信息
	$.ajax({
		"url": Feng.ctxPath + '/purch/getPruchListByOrderNo',
		"type": "POST",
		"data": {
			orderNo: orderNo
		},
		"dataType": "JSON",
		"success": function(json) {
			var purchList = json.data;
			if(purchList.length > 0) {
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
		}
	});

	//tt获取订单备注
	$.ajax({
		"url": Feng.ctxPath + '/notic/getOrderRemark',
		"type": "POST",
		"data": {
			orderNo: orderNo
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == 0) {
				$("#orderRemarkText").text(json.data);
				$("#orderRemark").show();
				if(json.data == "" || json.data == null) {
					getRemark();
				}
			}
		}
	});

	//tt获取政策连接
	$.ajax({
		"url": Feng.ctxPath + '/notic/getPolicyLink',
		"type": "POST",
		"data": {
			orderNo: orderNo
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == 0) {
				var ss = json.data;
				var str = ss.split("-");
				$("#policyLink").attr("href", str[1]);
				$("#policyLink").text(str[0]);
				if(str[0] != "") {
					$("#policyLinkT").show();
				}
				/*if(json.data == "" || json.data == null) {
					getPolicyLink();
				}*/
			}
		}
	});

	//订单是否包含失信人
	var status = $("#orderStatusText").text();
	if("待出票" == status) {
		$.ajax({
			"url": Feng.ctxPath + '/notic/isHaveBadPeopel',
			"type": "POST",
			"data": {
				orderNo: orderNo
			},
			"dataType": "JSON",
			"success": function(json) {
				if(json.code == 1) {
					layer.open({
						title: '钓鱼单',
						content: json.data
					});
				}
			}
		});
	}

	$.ajax({
		"url": Feng.ctxPath + '/notic/getOrderStatus',
		"type": "POST",
		"data": {
			orderNo: orderNo,
			orderSource: orderSource
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code != 0) {
				if(orderSource == 'TTS') {
					if(json.code == 1) {
						$("#orderStatusText").text("未出票申请退款");
					} else if(json.code == 2) {
						$("#orderStatusText").text("已取消");
					}
				} else if(orderSource == '携程') {
					$("#orderStatusText").text("申请取消");
				} else if(orderSource == '就旅行') {
					if(json.code == 1) {
						$("#orderStatusText").text("未出票申请退款");
					} else if(json.code == 2) {
						$("#orderStatusText").text("已取消");
					}
				} else if(orderSource == '淘宝') {
					$("#orderStatusText").text("未出票申请退款");
				} else if(orderSource == '途牛') {
					$("#orderStatusText").text("已取消");
				}
			}
		}
	});

	//监听出票完成提交
	$("#addTicket").click(function() {
		var data = new Array();
		var hasNoTicket = false;
		var isChecked = $("#deletePnr").prop("checked");
		var orderSource = $("#orderSourceText").text();
		$.each($(".ticketForm"), function(index) {
			var ticketInfo = $(this).serializeObject();
			ticketInfo.status = isChecked;
			if(orderSource != 'TTS' && orderSource != '就旅行') {
				if(ticketInfo.ticketNo == "") {
					layer.msg('请输入所有票号，或者先保存！', {
						icon: 5
					});
					hasNoTicket = true;
					return;
				}
			}
			data.push(ticketInfo);
		});
		var result = JSON.stringify(data);
		if(hasNoTicket) {
			return;
		}
		if(result == '[]') {
			return;
		}
		var index;
		var url = Feng.ctxPath + '/order/verifyTicketNo';
		$.ajax({
			"url": url,
			"type": "POST",
			"data": result,
			"dataType": "JSON",
			"contentType": "application/json",
			"beforeSend": function(XMLHttpRequest) {
				index = layer.msg('票号验证中...', {
					icon: 16,
					time: false,
					shade: 0.8
				});
			},
			"success": function(json) {
				storage.tradeNo = $("#tradeNo").val();
				storage.purchPayAmount = $("#purchPayAmount").val();
				storage.purchRemark = $("#purchRemark").val();
				storage.returnMoney = $("#returnMoney").val();
				storage.returnDate = $("#returnDate").val();
				layer.close(index);
				if(json.code == 0) { //出票成功
					var m = json.msg;
					layer.msg(json.msg, {
						icon: 6,
						time: 1500
					});
					setTimeout(function() {
						location.reload();
					}, 2000);
				} else if(json.code == -1) { //验证失败
					layer.alert(json.msg, {
						icon: 5
					});
				} else { //系统异常
					layer.alert(json.msg, {
						icon: 5
					});
				}
			}
		});

	});

	//监听采购单提交
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

	var orderStatusText = $("#orderStatusText").text();
	var chiCount = $("#chiCount").val();
	var policyType = $("#policyIdText").text();
	var orderSource = $("#orderSourceText").text();

	if(chiCount == 0 && orderStatusText == "待出票") {
		if(policyType.indexOf("SL") >= 0 || policyType.indexOf("CWN") >= 0) {
			if(orderSource == "TTS" || orderSource == "携程" || orderSource.indexOf("就旅行") >= 0 || orderSource == "同程" || orderSource == "淘宝") {
				$("#showPrintPlace").show();
				getppPrice(); //获取PP报价
				getshslPrice();	//获取SHSL报价
				getwnPrice(); //获取蜗牛报价
			}
		}
	}

	//监听一键出票
	form.on('submit(onePrintForm)', function(data) {
		var data = $('#onePrint').serialize();
		if(data.indexOf("ticketPlace") == -1) {
			layer.msg("选择一个渠道出票！");
			return false;
		}
		var ticketPlace = $("input[name='ticketPlace']:checked").val();
		var price="";
		if(1 == ticketPlace) {
			price = $("#ppPrice").val();
		}else if(2 == ticketPlace) {
			price = $("#zhslPrice").val();
		}else if(4==ticketPlace){
			price = $("#wnPrice").val();
		}
		console.log(price);
		if(price == "" || isNaN(price)) {
			layer.alert("没有获取到结算价！", {
				icon: 5
			});
			return false;
		}

		var url = Feng.ctxPath + '/auto/autoPrint';
		var closeIndex = null;
		$.ajax({
			"url": url,
			"type": "POST",
			"data": data,
			"dataType": "JSON",
			"beforeSend": function(XMLHttpRequest) {
				closeIndex = layer.msg('处理中...', {
					icon: 16,
					time: false,
					shade: 0.8
				});
			},
			"success": function(json) {
				layer.close(closeIndex);
				if(json.code == 0) {
					layer.msg(json.msg, {
						icon: 6,
						time: 3000
					});
					setTimeout(function() {
						location.reload();
					}, 3500);
				} else {
					layer.alert(json.msg, {
						icon: 5
					});
				}

			},
			"error": function(json) {
				layer.close(closeIndex);
			}
		});

		return false;
	});
});

//获取PP报价（鹏鹏）
function getppPrice() {
	$("#ppTicket").val("获取中...");
	$("#ppPrice").val("获取中...");
	$.ajax({
		"url": Feng.ctxPath + '/auto/ppPrice?orderNo=' + orderNo,
		"type": "POST",
		"success": function(json) {
			if(json.code == 0) {
				var mapData = json.data;
				$("#ppTicket").val(mapData['ticketPrice']);
				$("#ppPrice").val(mapData['sellPrice']);
			} else {
				// $("#ppPrice").val(json.msg);
				$("#ppPrice").val("没有获取到价格");
			}
		}
	});
}

//获取SHSL报价
function getshslPrice() {
	$("#zhslTicket").val("获取中...");
	$("#zhslPrice").val("获取中...");
	$.ajax({
		"url": Feng.ctxPath + '/auto/shslPrice?orderNo=' + orderNo,
		"type": "POST",
		"success": function(json) {
			if(json.code == 0) {
				var mapData = json.data;
				$("#zhslTicket").val(mapData['ticketPrice']);
				$("#zhslPrice").val(mapData['sellPrice']);
			} else {
				$("#zhslTicket").val("");
				$("#zhslPrice").val("没有获取到价格");
			}
		}
	});
}

//获取蜗牛报价
function getwnPrice() {
	$("#wnTicket").val("获取中...");
	$("#wnPrice").val("获取中...");
	$.ajax({
		"url": Feng.ctxPath + '/auto/wnPrice?orderNo=' + orderNo,
		"type": "POST",
		"success": function(json) {
			if(json.code == 0) {
				var mapData = json.data;
				$("#wnTicket").val(mapData['ticketPrice']);
				$("#wnPrice").val(mapData['sellPrice']);
			} else {
				$("#wnTicket").val("");
				$("#wnPrice").val(json.msg);
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
//重新加载
function reloadPurch() {
	var orderNo = $("#orderNo").val();
	$.ajax({
		"url": Feng.ctxPath + '/purch/getPruchListByOrderNo',
		"type": "POST",
		"data": {
			orderNo: orderNo
		},
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
/**
 * 保存票號
 * @param {Object} id
 */
function saveTciketNo(id) {
	var ticketNo = $("#" + id).val();
	if(id == '') {
		layer.msg("保存失败", {
			icon: 5,
			time: 1000
		});
		return;
	}
	if(ticketNo == '') {
		layer.msg("请输入票号", {
			icon: 5,
			time: 1000
		});
		return;
	}
	ticketNo = ticketNo.trim();
	$.ajax({
		"url": Feng.ctxPath + '/passenger/saveTicketNo',
		"type": "POST",
		"data": {
			passengerId: id,
			ticketNo: ticketNo
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == 0) {
				layer.msg("保存成功", {
					icon: 6,
					time: 1500
				});
				setTimeout(function() {
					location.reload();
				}, 2000);
			} else {
				layer.msg("保存失败", {
					icon: 5,
					time: 1000
				});
			}

		}
	});
}

function changeFlightNo(flightId) {
	var newFlightNo = $("#changeFlightNo").val();
	$.ajax({
		"url": Feng.ctxPath + '/flightChange/changeFlightNo',
		"type": "POST",
		"data": {
			flightId: flightId,
			newFlightNo: newFlightNo
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == 0) {
				layer.msg("修改成功", {
					icon: 6,
					time: 2000
				});

			} else {
				layer.msg("修改失败", {
					icon: 5,
					time: 2000
				});
			}
		}
	});
}

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

$("#printing").click(function() {
	var orderNo = $("#orderNo").val();
	$.ajax({
		"url": Feng.ctxPath + '/order/ttsPrinting',
		"type": "POST",
		"data": {
			orderNo: orderNo
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == 0) {
				var storage = window.localStorage;
				storage.tradeNo = $("#tradeNo").val();
				storage.purchPayAmount = $("#purchPayAmount").val();
				storage.purchRemark = $("#purchRemark").val();
				storage.returnMoney = $("#returnMoney").val();
				storage.returnDate = $("#returnDate").val();
				layer.msg(json.msg, {
					icon: 6,
					time: 1500
				});
				setTimeout(function() {
					location.reload();
				}, 2000);
			} else {
				layer.msg(json.msg, {
					icon: 5,
					time: 1000
				});
			}

		}
	});
});

window.onfocus = function() {
	getRemark();
	getOrderStatus();
	getPolicyLink();
}

function getPolicyLink() {
	var orderNo = $("#orderNo").val();
	$.ajax({
		"url": Feng.ctxPath + '/notic/getPolicyLink',
		"type": "POST",
		"data": {
			orderNo: orderNo
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == 0) {
				var ss = json.data;
				var str = ss.split("-");
				$("#policyLink").attr("href", str[1]);
				$("#policyLink").text(str[0]);
				if(str[0] != "") {
					$("#policyLinkT").show();
				}

			}
		}
	});
}

function getRemark() {
	var orderNo = $("#orderNo").val();
	$.ajax({
		"url": Feng.ctxPath + '/notic/getOrderRemark',
		"type": "POST",
		"data": {
			orderNo: orderNo
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == 0) {
				$("#orderRemarkText").text(json.data);
				$("#orderRemark").show();
			}
		}
	});
}

function getOrderStatus() {
	var orderNo = $("#orderNo").val();
	var orderSource = $("#orderSourceText").text();
	$.ajax({
		"url": Feng.ctxPath + '/notic/getOrderStatus',
		"type": "POST",
		"data": {
			orderNo: orderNo,
			orderSource: orderSource
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code != 0) {
				if(orderSource == 'TTS') {
					if(json.code == 1) {
						$("#orderStatusText").text("未出票申请退款");
					} else if(json.code == 2) {
						$("#orderStatusText").text("已取消");
					}
				} else if(orderSource == '携程') {
					$("#orderStatusText").text("申请取消");
				} else if(orderSource == '就旅行') {
					if(json.code == 1) {
						$("#orderStatusText").text("未出票申请退款");
					} else if(json.code == 2) {
						$("#orderStatusText").text("已取消");
					}
				} else if(orderSource == '淘宝') {
					$("#orderStatusText").text("未出票申请退款");
				} else if(orderSource == '途牛') {
					$("#orderStatusText").text("已取消");
				}
			}
		}
	});
}

$("#policyLink").click(function() {
	var orderNo = $("#orderNo").val();
	var uri = $(this).attr("href");
	$.ajax({
		"url": Feng.ctxPath + '/notic/getPolicyHtml',
		"type": "POST",
		"data": {
			orderNo: orderNo,
			uri: uri
		},
		"dataType": "JSON",
		"success": function(json) {
			var index = layer.open({
				type: 1,
				skin: "layui-layer-rim",
				area: ["1000px", "600px"],
				title: "查看政策",
				content: json.data,
				btn: ['关闭'],
				yes: function(index, layero) {
					layer.close(index);
				},
			});
		}
	});
});