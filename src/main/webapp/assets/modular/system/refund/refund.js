var tableResult;
layui.use(['layer', 'form', 'table', 'admin', 'ax', 'laydate', 'element'], function() {
	var $ = layui.$;
	var layer = layui.layer;
	var form = layui.form;
	var table = layui.table;
	var $ax = layui.ax;
	var admin = layui.admin;
	var laydate = layui.laydate;
	var element = layui.element;

	var Refund = {
		tableId: "refundTable", //表格id
		condition: {
			RefundName: ""
		}
	};

	/**
	 * 初始化表格的列
	 */
	Refund.initColumn = function() {
		return [
			[{
					type: 'checkbox'
				},
				{
					field: 'retNo',
					sort: false,
					title: '订单号<hr/>退票单号',
					align: 'center',
					width: 210,
					templet: function(d) {
						if(d.retNo == "") {
							return d.orderNo;
						} else {
							return d.orderNo + "<hr/>" + d.retNo;
						}
					}
				},
				{
					sort: false,
					title: '航班日期<hr/>航班号',
					align: 'center',
					width: 150,
					templet: function(d) {
						return d.flightDate + " " + d.flightTime + '<hr/>' + d.flightNo;
					}
				},
				{
					field: 'cAppDate',
					sort: false,
					title: '申请日期(乘)',
					width: 150,
					align: 'center'
					/*,
										templet:function(d){
											return layui.util.toDateString(d.cAppDate, 'yyyy-MM-dd')
										}*/
				},
				{
					sort: false,
					title: '申请类型(乘)<hr/>退票状态(乘)',
					align: 'center',
					width: 100,
					templet: function(d) {
						return d.refundType + '<hr/>' + d.cRemState;
					}
				},
				{
					sort: false,
					title: '退票乘客(实退)',
					align: 'center',
					width: 100,
					templet: function(d) {
						return d.passengerName + "(" + d.cRealPrice + ")";
					}
				},

				{
					sort: false,
					title: '申请类型(供)<hr/>退票状态(供)',
					align: 'center',
					width: 100,
					templet: function(d) {
						return d.airRefundType + '<hr/>' + d.airRemState;
					}
				},
				/*{
					sort: false,
					title: '供应预退<hr/>供应实退',
					align: 'center',
					templet: function(d) {
						return d.airEstimatePrice+ '<hr/>' +d.airRealPrice;
					}
				},*/
				{
					sort: false,
					title: 'PNR状态',
					align: 'center',
					width: 100,
					templet: function(d) {
						var status = d.xePnrStatus;
						if("1" == status) {
							return "已取位";
						} else {
							return "未取位";
						}
					}

				},
				{
					field: 'flightStatus',
					sort: false,
					title: '航班状态',
					width: 100,
					align: 'center'
				},
				{
					field: 'orderRemark',
					sort: false,
					title: '系统备注',
					align: 'center',
					width: 200,
				},
				{
					field: 'processBy',
					sort: false,
					title: '锁定人',
					align: 'center'
				},
				{
					align: 'center',
					toolbar: '#tableBar',
					title: '操作'
					/*minWidth: 200*/
				}
			]
		];
	};
	var d = new Date();
	var m = d.getMonth();
	var y = d.getFullYear();
	if(m == 0) {
		m = 12;
		y = y - 1;
	}

	laydate.render({
		elem: '#flightEndDate',
		format: 'yyyy-MM-dd'
	});
	laydate.render({
		elem: '#flightStartDate',
		format: 'yyyy-MM-dd'
	});
	laydate.render({
		elem: '#cAppStartDate',
		format: 'yyyy-MM-dd',
		value: y + '-' + lay.digit(m) + '-' + lay.digit(d.getDate())
	});
	laydate.render({
		elem: '#cAppEndDate',
		format: 'yyyy-MM-dd',
		value: d
	});

	// 渲染表格
	tableResult = table.render({
		elem: '#' + Refund.tableId,
		url: Feng.ctxPath + '/refund/getRefundList',
		page: true,
		even: true,
		height: AudioContext,
		cellMinWidth: 100,
		cols: Refund.initColumn(),
		parseData: function(res) { //res 即为原始返回的数据
			return {
				"code": 0, //解析接口状态
				"msg": res.msg, //解析提示文本
				"count": res.count, //解析数据长度
				"data": res.data //解析数据列表
			};
		}
	});

	$("#confirmRefund").click(function() {
		var retNo = $("#retNoText").text();
	});

	// 搜索按钮点击事件
	$('#btnSearch').click(function() {
		Refund.search();
	});

	$('#btnExport').click(function() {
		var theType = $("input[type='radio']:checked").val();
		var sourceText = $("#orderSource").val();
		var str = sourceText.split("-");
		var orderSource = str[0];
		var orderShop = str[1];
		var ticketNo = $("#ticketNo").val();
		var flightNo = $("#flight").val();
		var retNo = $("#retNo").val();
		var orderNo = $("#orderNo").val();
		var cRemState = $("#cRemState").val();
		var name = $("#name").val();
		var cAppStartDate = $("#cAppStartDate").val();
		var flightStartDate = $("#flightStartDate").val();
		var cAppEndDate = $("#cAppEndDate").val();
		var flightEndDate = $("#flightEndDate").val();
		var isChecked = $("#onlyMe").prop("checked");
		var tokenValue = new Date().Format("yyyyMMddHHmmss");
		var downToken = "down" + tokenValue;
		var url = Feng.ctxPath + "/refund/getDataForReportList?&downToken=" + downToken + "&tokenValue=" + tokenValue + "&orderSource=" + orderSource + "&orderShop=" + orderShop +
			"&ticketNo=" + ticketNo + "&name=" + name + "&flightNo=" + flightNo + "&flightStartDate=" + flightStartDate + "&flightEndDate=" + flightEndDate + "&retNo=" + retNo +
			"&orderNo=" + orderNo + "&cRemState=" + cRemState + "&cAppStartDate=" + cAppStartDate + "&cAppEndDate=" + cAppEndDate + "&theType=" + theType + "&isChecked=" + isChecked;
		var index = layer.msg('正在下载数据，等一下', {
			icon: 16,
			time: false,
			shade: 0.8
		});
		window.location.href = url;
		var timeID = setInterval(function() {
			var str = getCookie(downToken);
			if(tokenValue == str) {
				layer.close(index);
				clearInterval(timeID);
			}
		}, 600);

	});

	$('#add').click(function() {
		$.each(parent.$(".layui-tab-item"), function() {
			$(this).attr("class", "layui-tab-item")
		});
		Refund.addNewTab("search", "/refund/toSearchOrder", "订单搜索");
	});
	$("#delete").click(function() {
		var checkRows = table.checkStatus('refundTable');
		if(checkRows.data.length === 0) {
			Feng.error("请选择要删除的数据");
		} else {
			var checkData = checkRows.data;
			var noArr = new Array();
			for(var i = 0; i < checkData.length; i++) {
				noArr[i] = checkData[i].retNo;
			}
			layer.confirm('确认删除吗', function(index) {
				$.ajax({
					"url": Feng.ctxPath + '/refund/deleteRefund',
					"type": "POST",
					"data": {
						retNos: noArr.join(',')
					},
					"dataType": "JSON",
					"success": function(json) {
						Refund.reloadTable();
						layer.msg(json.msg);
					}
				});
				layer.close(index);
			});

		}
	});
	// 工具条点击事件
	table.on('tool(refundTable)', function(obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if(layEvent === 'show') {
			Refund.onShowRefund(data);
		}
		if(layEvent === 'edit') {
			var refundId = data.refundId;
			var orderNo = data.orderNo;
			var retNo = data.retNo;
			var url = '/refund/toRefundEdit?&orderNo=' + orderNo + '&retNo=' + retNo + '&refundId=' + refundId;
			$.ajax({
				"url": Feng.ctxPath + '/refund/isHavePcocess',
				"type": "POST",
				"data": {
					retNo: retNo,
					refundId: refundId
				},
				"dataType": "JSON",
				"success": function(json) {
					if(json.data) {
						Refund.addNewTab(refundId, url, "编辑");
						Refund.reloadTable();
					} else {
						layer.msg(json.msg, {
							icon: 5,
							time: 2000
						});
					}
				},
				"error": function() {
					Refund.addNewTab(refundId, url, "编辑");
					Refund.reloadTable();
				}
			});
		}
		if(layEvent == 'unlock') {
			$.ajax({
				"url": Feng.ctxPath + '/refund/deleteProcess',
				"type": "POST",
				"data": {
					retNo: data.retNo,
					refundId: data.refundId
				},
				"dataType": "JSON",
				"success": function(json) {
					if(json.code == 0) {
						layer.msg("已解锁");
						Refund.reloadTable();
					} else {
						layer.msg(json.msg);
					}
				}
			});
		}

	});

	/**
	 * 点击查询按钮
	 */
	Refund.search = function() {
		var retNo = $("#retNo").val();
		var sourceText = $("#orderSource").val();
		if(retNo != "") {
			if(sourceText == "0-0") {
				layer.msg("若无数据尝试选择订单来源搜索");
			}
		}
		Refund.reloadTable();
	};
	/**
	 * 点击查看
	 * @param data 点击按钮时候的行数据
	 */
	Refund.onShowRefund = function(data) {
		var orderNo = data.orderNo;
		var refundId = data.refundId;
		var retNo = data.retNo;
		//var viewUrl = Feng.ctxPath + '/refund/toRefundView?orderNo=' + orderNo +'&retNo='+retNo;
		/*$.each(parent.$(".layui-tab-item"), function() {
							$(this).attr("class","layui-tab-item")
						});*/
		Refund.addNewTab(":" + refundId, '/refund/toRefundView?orderNo=' + orderNo + '&refundId=' + refundId + '&retNo=' + retNo, "查看");
	};
	/**
	 * 在父页面新增一个tab
	 * @param  url
	 * @param  name
	 */
	Refund.addNewTab = function(id, url, name) {
		parent.loadTab(id, url, name);
	};

	//监听单选框
	form.on('radio(theType)', function(data) {
		Refund.reloadTable();
	});

	Refund.reloadTable = function() {
		var theType = $("input[type='radio']:checked").val();
		var sourceText = $("#orderSource").val();
		var str = sourceText.split("-");
		var orderSource = str[0];
		var orderShop = str[1];
		var ticketNo = $("#ticketNo").val();
		var flightNo = $("#flight").val();
		var retNo = $("#retNo").val();
		var orderNo = $("#orderNo").val();
		var cRemState = $("#cRemState").val();
		var name = $("#name").val();
		var cAppStartDate = $("#cAppStartDate").val();
		var flightStartDate = $("#flightStartDate").val();
		var cAppEndDate = $("#cAppEndDate").val();
		var flightEndDate = $("#flightEndDate").val();
		var isChecked = $("#onlyMe").prop("checked");
		tableResult.reload({
			where: {
				'orderSource': orderSource,
				'orderShop': orderShop,
				'ticketNo': ticketNo,
				'name': name,
				'flightNo': flightNo,
				'flightStartDate': flightStartDate,
				'flightEndDate': flightEndDate,
				'retNo': retNo,
				'orderNo': orderNo,
				'cRemState': cRemState,
				'cAppStartDate': cAppStartDate,
				'cAppEndDate': cAppEndDate,
				'theType': theType,
				'isChecked': isChecked
			}
		});
	}

});