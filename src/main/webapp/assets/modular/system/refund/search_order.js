layui.use(['layer', 'form', 'table', 'laydate', 'element'], function() {
	var layer = layui.layer;
	var form = layui.form;
	var table = layui.table;
	var laydate = layui.laydate;
	var element = layui.element;

	var Order = {
		tableId: "orderTable", //表格id
		condition: {
			OrderName: ""
		}
	};
	/*templet: function(d) {
						return "<a  onclick=\"toOrderView('" + d.orderNo + "')\">" + d.orderNo + "</a>";
					}*/

	/**
	 * 初始化表格的列
	 */
	Order.initColumn = function() {
		return [
			[{
					type: 'checkbox'
				},
				{
					field: 'orderNo',
					sort: false,
					title: '订单号<hr/>出票单号',
					align: 'center',
					width: 180,
					templet: function(d) {
						if(d.cOrderNo == "") {
							return d.orderNo;
						} else {
							return d.orderNo + "<hr/>" + d.cOrderNo;
							//return "<a onclick=\"toOrderView('"+d.orderNo+"')\">"+d.orderNo+"</a>";
						}
					}
				},
				{
					field: 'tripType',
					sort: false,
					title: '类型',
					align: 'center',
					templet: function(d) {
						if(d.tripType == 0) {
							return '单程';
						} else {
							return '往返';
						}
					}
				},
				{
					field: 'cAddDate',
					sort: false,
					title: '订单创建日期',
					align: 'center',
					width: 170,
				},
				{
					sort: false,
					title: '航班日期/航班号',
					align: 'center',
					width: 170,
					templet: function(d) {
						return d.flightDate + '<br/>' + d.flightNo;
					}
				},
				{
					sort: false,
					title: '总价/人数',
					align: 'center',
					width: 110,
					templet: function(d) {
						return d.totalPrice + '/' + d.passengerCount;
					}
				},
				{
					field: 'status',
					sort: false,
					title: '订单状态',
					align: 'center',
					templet: function(d) {
						if(d.status == 1) {
							return '<span style="color: #f60;">待出票</span>';
						} else if(d.status == 2) {
							return '出票中';
						} else if(d.status == 3) {
							return '已出票';
						} else if(d.status == 4) {
							return '已取消';
						} else if(d.status == 5) {
							return '<span style="color: #f60;">客票异常</span>';
						}
					}
				},
				{
					field: 'pnr',
					sort: false,
					title: 'PNR',
					align: 'center'
				},
				{
					field: 'policyType',
					sort: false,
					title: '政策代码',
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
	// 渲染表格
	var tableResult = table.render({
		elem: '#' + Order.tableId,
		url: Feng.ctxPath + '/order/getOrder',
		page: true,
		method: "POST",
		height: "full-158",
		even: true,
		cellMinWidth: 100,
		cols: Order.initColumn()
	});

	var d = new Date();
	var m = d.getMonth();
	var y = d.getFullYear();
	if(m == 0) {
		m = 12;
		y = y - 1;
	}
	//渲染日期
	laydate.render({
		elem: '#startDate',
		format: 'yyyy-MM-dd',
		value: y + '-' + lay.digit(m) + '-' + lay.digit(d.getDate())
	});
	laydate.render({
		elem: '#endDate',
		format: 'yyyy-MM-dd',
		value: d
	});

	// 搜索按钮点击事件
	$('#btnSearch').click(function() {
		Order.search();
	});

	// 工具条点击事件
	table.on('tool(orderTyble)', function(obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if(layEvent === 'showR') {
			$.ajax({
				"url": Feng.ctxPath + '/order/isHavePcocess',
				"type": "POST",
				"data": {
					orderNo: data.orderNo
				},
				"dataType": "JSON",
				"success": function(json) {
					if(true) {
						var url = "/refund/toRefundAdd/?orderId=" + data.orderId;
						var id = data.orderId;
						var name = "添加退票单";
						parent.loadTab(id, url, name);
					} else {
						layer.msg(json.msg);
					}
				}
			});

		} else {
			$.ajax({
				"url": Feng.ctxPath + '/order/isHavePcocess',
				"type": "POST",
				"data": {
					orderNo: data.orderNo
				},
				"dataType": "JSON",
				"success": function(json) {
					if(true) {
						var url = "/change/toChangeAdd/?orderId=" + data.orderId;
						var id = data.orderId;
						var name = "添加改签单";
						parent.loadTab(id, url, name);
					} else {
						layer.msg(json.msg);
					}
				}
			});
		}
	});
	Order.search = function() {
		var startDate = $("#startDate").val();
		var endDate = $("#endDate").val();
		var orderNo = $("#orderNo").val();
		tableResult.reload({
			where: {
				'endDate': endDate,
				'startDate': startDate,
				'orderNo': orderNo
			},
			page: {
				curr: 1 //重新从第 1 页开始
			}
		});
	};

});