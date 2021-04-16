var tableResult;
$(document).ready(function() {
	layui.use(['layer', 'form', 'table', 'admin', 'ax', 'laydate', 'element'], function() {
		var $ = layui.$;
		var layer = layui.layer;
		var form = layui.form;
		var table = layui.table;
		var $ax = layui.ax;
		var admin = layui.admin;
		var laydate = layui.laydate;
		var element = layui.element;

		var Order = {
			tableId: "flightTyble", //表格id
			condition: {
				OrderName: ""
			}
		};

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
						title: '订单号',
						align: 'center'
					},

					{
						sort: false,
						field: 'flightNo',
						title: '航班号',
						align: 'center'
					},

					{
						sort: false,
						title: '航班日期',
						align: 'center',
						templet: function(d) {
							return d.flightDepDate + " " + d.depTime;
						}
					},
					{
						sort: false,
						title: '票号',
						align: 'center',
						templet: function(d) {
							var list = d.passengers;
							var tickets = "";
							for(var i = 0; i < list.length; i++) {
								tickets = tickets + list[i].ticketNo + ",";
							}
							return tickets.substring(0, tickets.length - 1);
						}

					},
					{
						sort: false,
						title: '乘机人',
						align: 'center',
						templet: function(d) {
							var list = d.passengers;
							var names = "";
							for(var i = 0; i < list.length; i++) {
								names = names + list[i].name + ",";
							}
							return names.substring(0, names.length - 1);
						}
					},
					{
						field: 'remark',
						sort: false,
						title: '订单来源',
						align: 'center'
					},
					{
						field: 'flightChange',
						sort: false,
						title: '航变类型',
						align: 'center'
					},
					{
						field: 'modifyDate',
						sort: false,
						title: '录入时间',
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
		
		//渲染日期
		laydate.render({
			elem: '#startDate',
			format: 'yyyy-MM-dd',
			value: new Date()
		});
		laydate.render({
			elem: '#endDate',
			format: 'yyyy-MM-dd',
			value: getTimeByDay(30)
		});
		
		
		
		// 渲染表格
		tableResult = table.render({
			elem: '#flightTyble',
			url: Feng.ctxPath + '/flightChange/getFlightInfo',
			height: 520,
			method: "POST",
			even: true,
			page: true,
			limit: 10,
			limits: [10, 15, 20, 30, 40, 50],
			cellMinWidth: 100,
			cols: Order.initColumn(),
			parseData: function(res) { //res 即为原始返回的数据
				return {
					"code": 0, //解析接口状态
					"msg": res.msg, //解析提示文本
					"count": res.count, //解析数据长度
					"data": res.data //解析数据列表
				};
			}
		});

		laydate.render({
			elem: '#flightDate',
			format: 'yyyy-MM-dd'
		});

		// 工具条点击事件
		table.on('tool(flightTyble)', function(obj) {
			var data = obj.data;
			var url = Feng.ctxPath + '/flightChange/toflightChange?flightId=' + data.flightId+'&orderSource='+ data.orderSource;
			var layEvent = obj.event;
			if(layEvent == 'insertChange') {
				var index = layer.open({
					type: 2,
					skin: "layui-layer-rim", //皮肤类型，在skin文件夹中
					area: ["1000px", "600px"], //范围大小
					title: "航变录入",
					content: [url, 'no'],
					btn: ['取消'],
					yes: function(index, layero) { //取消按钮后的处理函数
						layer.close(index);
					},
				});

			}
		});
		/**
		 *在父页面新增一个tab 
		 * @param  url 页面地址
		 * @param  name tab标题
		 */
		Order.addNewTab = function(orderNo, url, name) {
			parent.loadTab(orderNo, url, name);
		};

	});
});

// 搜索按钮点击事件
$('#btnSearch').click(function() {
	var orderNo = $("#orderNo").val();
	var flightNo = $("#flightNo").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var ticketNo = $("#ticketNo").val();
	var name = $("#passengerName").val();
	tableResult.reload({
		url: Feng.ctxPath + '/flightChange/getFlightInfo',
		where: {
			'orderNo': orderNo,
			'flightNo': flightNo,
			'startDate': startDate,
			'endDate' : endDate,
			'ticketNo': ticketNo,
			'name': name
		},
		page: {
			curr: 1 //重新从第 1 页开始
		}
	});
});

function tableReload(orderSource) {
	var orderNo = $("#orderNo").val();
	var flightNo = $("#flightNo").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var ticketNo = $("#ticketNo").val();
	var name = $("#passengerName").val();
	tableResult.reload({
		url: Feng.ctxPath + '/flightChange/getFlightInfo',
		where: {
			'orderNo': orderNo,
			'flightNo': flightNo,
			'startDate': startDate,
			'endDate' : endDate,
			'ticketNo': ticketNo,
			'name': name
		},
		page: {
			curr: 1 //重新从第 1 页开始
		}
	});
}

function getTimeByDay(num) {
	var today=today = new Date().getTime();
    var time=today + 60 * 60 * 1000 * 24 * num;
    return new Date(time).toISOString().split('T')[0];
}