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

	var Change = {
		tableId: "changeTable", //表格id
		condition: {
			ChangeName: ""
		}
	};

	/**
	 * 初始化表格的列
	 */
	Change.initColumn = function() {
		return [
			[{
					type: 'checkbox'
				},
				{
					sort: false,
					title: '订单号<hr/>改签单号',
					align: 'center',
					width: 210,
					templet: function(d) {
						if(d.retNo == "") {
							return d.orderNo;
						} else {
							return d.orderNo + "<hr/>" + d.newCOrderNo;
						}
					}
				},
				{
					sort: false,
					title: '航班日期/航班号',
					align: 'center',
					templet: function(d) {
						return d.flightDate + '<br/>' + d.flightNo;
					}
				},
				{
					sort: false,
					title: '改签乘客',
					align: 'center',
					templet: function(d) {
						return d.passengerName;
					}
				},
				{
					sort: false,
					title: '改签状态(乘)',
					align: 'center',
					templet: function(d) {
						if(d.state == 1) {
							return '改签申请中';
						} else if(d.state == 2) {
							return '改签完成';
						} else if(d.state == 3) {
							return '拒绝改签';
						}
					}
				},
				{
					field: 'changeDate',
					sort: false,
					title: '改签日期',
					align: 'center'
					/*templet: function(d) {
						return layui.util.toDateString(d.changeDate, 'yyyy-MM-dd')
					}*/
				},
				{
					field: 'revenuePrice',
					sort: false,
					title: '改签费(乘)',
					align: 'center'
				},
				{
					field: 'payPrice',
					sort: false,
					title: '改签费(供)',
					align: 'center'
				},
				{
					field: 'createBy',
					sort: false,
					title: '录入人',
					align: 'center'
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
					title: '操作',
					minWidth: 200
				}
			]
		];
	};
	laydate.render({
		elem: '#flightEndDate',
		format: 'yyyy-MM-dd'
	});
	laydate.render({
		elem: '#flightStartDate',
		format: 'yyyy-MM-dd'
	});
	var d = new Date();
	laydate.render({
		elem: '#changeDateStart',
		format: 'yyyy-MM-dd',
		value: d.getFullYear() + '-' + lay.digit(d.getMonth()) + '-' + lay.digit(d.getDate())
	});

	laydate.render({
		elem: '#changeDateEnd',
		format: 'yyyy-MM-dd',
		value: d
	});

	// 渲染表格
	tableResult = table.render({
		elem: '#' + Change.tableId,
		url: Feng.ctxPath + '/change/getChangeList',
		page: true,
		even: true,
		height: AudioContext,
		cellMinWidth: 100,
		cols: Change.initColumn(),
		parseData: function(res) { //res 即为原始返回的数据
			return {
				"code": 0, //解析接口状态
				"msg": res.msg, //解析提示文本
				"count": res.count, //解析数据长度
				"data": res.data //解析数据列表
			};
		}
	});

	// 搜索按钮点击事件
	$('#btnSearch').click(function() {
		Change.search();
	});
	/*$('#add').click(function() {
		$.each(parent.$(".layui-tab-item"), function() {
							$(this).attr("class","layui-tab-item")
						});
		Change.addNewTab("/change/toSearchOrder","订单搜索");
	});*/
	$("#delete").click(function() {
		var checkRows = table.checkStatus('changeTable');
		if(checkRows.data.length === 0) {
			Feng.error("请选择要删除的数据");
		} else {
			var checkData = checkRows.data;
			var noArr = new Array();
			for(var i = 0; i < checkData.length; i++) {
				noArr[i] = checkData[i].changeId;
			}
			layer.confirm('确认删除吗', function(index) {
				$.ajax({
					"url": Feng.ctxPath + '/change/deleteChange',
					"type": "POST",
					"data": {
						changeIds: noArr.join(',')
					},
					"dataType": "JSON",
					"success": function(json) {
						table.reload('changeTable');
						layer.msg(json.msg);
					}
				});
				layer.close(index);
			});
		}
	});
	// 工具条点击事件
	table.on('tool(changeTable)', function(obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if(layEvent === 'show') {
			Change.onShowChange(data);
		}
		if(layEvent === 'edit') {
			var changeId = data.ChangeId;
			var orderNo = data.orderNo;
			var newCOrderNo = data.newCOrderNo;
			var url = '/change/toChangeEdit?&orderNo=' + orderNo + '&newCOrderNo=' + newCOrderNo + '&changeId=' + changeId;
			$.ajax({
				"url": Feng.ctxPath + '/change/isHavePcocess',
				"type": "POST",
				"data": {
					changeNo: newCOrderNo
				},
				"dataType": "JSON",
				"success": function(json) {
					if(json.data) {
						Change.addNewTab(changeId, url, "编辑");
						Change.reloadTable();
					} else {
						layer.msg(json.msg, {
							icon: 5,
							time: 2000
						});
					}
				},
				"error": function() {
					Change.addNewTab(changeId, url, "编辑");
					Change.reloadTable();
				}
			});

			Change.addNewTab(newCOrderNo, url, "编辑");
		}
		if(layEvent == 'unlock') {
			$.ajax({
				"url": Feng.ctxPath + '/change/deleteProcess',
				"type": "POST",
				"data": {
					changeNo: data.newCOrderNo
				},
				"dataType": "JSON",
				"success": function(json) {
					if(json.code == 0) {
						layer.msg("已解锁");
						Change.reloadTable();
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
	Change.search = function() {
		var sourceText = $("input[type='radio']:checked").val();
		var str = sourceText.split("-");
		var orderSource = str[0];
		var orderShop = str[1];
		var ticketNo = $("#ticketNo").val();
		var flightNo = $("#flight").val();
		var newCOrderNo = $("#newCOrderNo").val();
		var orderNo = $("#orderNo").val();
		var state = $("#state").val();
		var passengerName = $("#passengerName").val();
		var changeDateStart = $("#changeDateStart").val();
		var changeDateEnd = $("#changeDateEnd").val();
		var flightStartDate = $("#flightStartDate").val();
		var flightEndDate = $("#flightEndDate").val();
		tableResult.reload({
			where: {
				'orderSource': orderSource,
				'orderShop': orderShop,
				'ticketNo': ticketNo,
				'passengerName': passengerName,
				'flightNo': flightNo,
				'flightStartDate': flightStartDate,
				'flightEndDate': flightEndDate,
				'newCOrderNo': newCOrderNo,
				'orderNo': orderNo,
				'state': state,
				'changeDateStart': changeDateStart,
				'changeDateEnd': changeDateEnd,
			},
			page: {
				curr: 1 //重新从第 1 页开始
			}
		});
	};
	
	$('#add').click(function() {
		$.each(parent.$(".layui-tab-item"), function() {
			$(this).attr("class", "layui-tab-item")
		});
		Change.addNewTab("search", "/refund/toSearchOrder", "订单搜索");
	});
	
	/**
	 * 点击查看
	 * @param data 点击按钮时候的行数据
	 */
	Change.onShowChange = function(data) {
		var orderNo = data.orderNo;
		var newCOrderNo = data.newCOrderNo;
		/*$.each(parent.$(".layui-tab-item"), function() {
							$(this).attr("class","layui-tab-item")
						});*/
		Change.addNewTab(":" + newCOrderNo, '/change/toChangeView?orderNo=' + orderNo + "&newCOrderNo=" + newCOrderNo, "查看");
	};
	/**
	 * 在父页面新增一个tab
	 * @param  url
	 * @param  name
	 */
	Change.addNewTab = function(id, url, name) {
		parent.loadTab(id, url, name);
	};

	Change.reloadTable = function() {
		var sourceText = $("input[type='radio']:checked").val();
		var str = sourceText.split("-");
		var orderSource = str[0];
		var orderShop = str[1];
		var ticketNo = $("#ticketNo").val();
		var flightNo = $("#flight").val();
		var newCOrderNo = $("#newCOrderNo").val();
		var orderNo = $("#orderNo").val();
		var state = $("#state").val();
		var passengerName = $("#passengerName").val();
		var changeDateStart = $("#changeDateStart").val();
		var changeDateEnd = $("#changeDateEnd").val();
		var flightStartDate = $("#flightStartDate").val();
		var flightEndDate = $("#flightEndDate").val();
		tableResult.reload({
			where: {
				'orderSource': orderSource,
				'orderShop': orderShop,
				'ticketNo': ticketNo,
				'passengerName': passengerName,
				'flightNo': flightNo,
				'flightStartDate': flightStartDate,
				'flightEndDate': flightEndDate,
				'newCOrderNo': newCOrderNo,
				'orderNo': orderNo,
				'state': state,
				'changeDateStart': changeDateStart,
				'changeDateEnd': changeDateEnd,
			},
			page: {
				curr: 1 //重新从第 1 页开始
			}
		});
	}

});