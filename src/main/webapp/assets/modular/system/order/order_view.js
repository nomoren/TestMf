$(document).ready(function() {
	layui.use(['layer', 'form', 'table', 'admin', 'ax', 'laydate', 'element'], function() {
		var layer = layui.layer;
		var table = layui.table;
		var element = layui.element;

		/*var index;
		$('#orderxq').mouseover(
			function() {
				index = layer.tips(
						'航空公司：深圳航空<br/>里程：200公里  &nbsp;&nbsp;飞行时间：2小时30分钟',
						'#orderxq', {
							tips : [ 3, '#78BA32' ],
							area : [ '345px' ]
						});
			});
		$("#orderxq").mouseout(function() {
			layer.close(index);
		});*/
		var Log = {
			tableId: "logTable" //表格id
		};

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
						width: 480
					}
				]
			];
		};
		var orderNo = $("#orderNo").val();
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
							/* "<th>#{purchaseNo}</th>"+*/
							"<th>#{tradeNo}</th>" +
							"<th>#{supplier}</th>" +
							"<th>#{payAmount}</th>" +
							"<th>#{payWay}</th>" +
							"<td >#{remark}</td>" +
							"<td>#{passengerNames}</td>" +
							"<td>#{type}</td>" +
							"<td>-</td>" +
							"<tr>";
						/*	html = html.replace(/#{purchaseNo}/g, purchList[i]==null?"":purchList[i].purchaseNo);*/
						html = html.replace(/#{tradeNo}/g, purchList[i] == null ? "" : purchList[i].tradeNo);
						html = html.replace(/#{passengerNames}/g, purchList[i] == null ? "" : purchList[i].passengerNames);
						html = html.replace(/#{supplier}/g, purchList[i] == null ? "" : purchList[i].supplier);
						html = html.replace(/#{payAmount}/g, purchList[i] == null ? "" : purchList[i].payAmount);
						html = html.replace(/#{payWay}/g, purchList[i] == null ? "" : purchList[i].payWay);
						html = html.replace(/#{remark}/g, purchList[i] == null ? "" : purchList[i].remark);
						html = html.replace(/#{type}/g, purchList[i] == null ? "" : purchList[i].type);
						$("#purchList").append(html);
					}
					$("#purchInfo").show();
				}
			}
		});
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

		if("待出票" == status) {
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

	});

})
window.onfocus = function() {
	getRemark();
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
				skin: "layui-layer-rim", //皮肤类型，在skin文件夹中
				area: ["1000px", "600px"], //范围大小
				title: "查看政策",
				content: json.data,
				btn: ['关闭'],
				yes: function(index, layero) { //取消按钮后的处理函数
					layer.close(index);
				},
			});
		}
	});
});