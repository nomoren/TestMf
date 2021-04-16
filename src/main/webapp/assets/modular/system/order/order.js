var tableResult;
var util;
$(document).ready(function() {
	layui.use(['layer', 'form', 'table', 'admin', 'ax', 'laydate', 'element'], function() {
		var $ = layui.$;
		util = layui.util
		var layer = layui.layer;
		var form = layui.form;
		var table = layui.table;
		var $ax = layui.ax;
		var admin = layui.admin;
		var laydate = layui.laydate;
		var element = layui.element;

		var Order = {
			tableId: "orderTable", //表格id
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
						field: 'orderNo',
						sort: false,
						title: '订单号<hr/>出票单号',
						align: 'center',
						width: 210,
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
						width: 155,
						title: '订单创建日期',
						align: 'center'
					},
					{
						sort: false,
						title: '航班日期<hr/>航班号',
						align: 'center',
						templet: function(d) {
							return d.flightDate + '<br/>' + d.flightNo;
						}
					},
					{
						field: 'priceCount',
						sort: false,
						title: '总价/人数',
						align: 'center',
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
								return '<span style="color: #f60;">出票中</span>';
							} else if(d.status == 3) {
								return '已出票';
							} else if(d.status == 4) {
								return '已取消';
							} else if(d.status == 5) {
								return '<span style="color: #f60;">客票异常</span>';
							} else if(d.status == 6) {
								return '未出票申请退款';
							} else if(d.status == 98) {
								return '已驳回';
							} else if(d.status == 99) {
								return '申请取消';
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
						sort: false,
						title: '紧急程度',
						width: 130,
						align: 'center',
						templet: function(d) {
							if(d.orderSource == 04) {
								if(d.status == 1) { //淘宝订单执行倒计时
									setCountdown('a' + d.orderNo, d.lastPrintTicketTime);
								}
								return '<span style="color: #f60;" id="a' + d.orderNo + '"></span>';
							} else {
								if(d.status == 1 || d.status == 2) {
									var diff = TimeDifference(d.lastPrintTicketTime);
									if("" == diff) {
										return "-";
									} else if("out" == diff) {
										return '<span style="color: #f60;">已出票超时</span>';
									} else {
										if(!isNaN(diff)) {
											if(diff <= 60) {
												return '<span style="color: #f60;">' + diff + '(临近超时)</span>';
											} else {
												return "-";
											}
										}
									}
								} else {
									return "-";
								}

							}
						},
					},
					{
						sort: false,
						title: '最晚出票/提示',
						width: 155,
						align: 'center',
						templet: function(d) {
							return '<span style="color: #f60;">' + d.lastPrintTicketTime + '</span>';
						}
					},
					{
						field: 'processBy',
						sort: false,
						width: 115,
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
		tableResult = table.render({
			elem: '#orderTable',
			url: Feng.ctxPath + '/order/getOrder',
			height: AudioContext,
			method: "POST",
			even: true,
			page: true,
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
		//渲染日期
		laydate.render({
			elem: '#startDate',
			format: 'yyyy-MM-dd',
			value: new Date()
		});
		laydate.render({
			elem: '#endDate',
			format: 'yyyy-MM-dd',
			value: new Date()
		});
		laydate.render({
			elem: '#flightStartDate',
			format: 'yyyy-MM-dd'
		});
		laydate.render({
			elem: '#flightEndDate',
			format: 'yyyy-MM-dd'
		});

		$('#add').click(function() {
			Order.addNewTab('addNew', '/order/toOrderAdd', "新增订单");
		});
		$("#delete").click(function() {
			var checkRows = table.checkStatus('orderTable');
			if(checkRows.data.length === 0) {
				Feng.error("请选择要删除的数据");
			} else {
				var checkData = checkRows.data;
				var noArr = new Array();
				for(var i = 0; i < checkData.length; i++) {
					noArr[i] = checkData[i].orderNo;
				}
				layer.confirm('确认删除吗', function(index) {
					$.ajax({
						"url": Feng.ctxPath + '/order/deleteOrder',
						"type": "POST",
						"data": {
							orderNos: noArr.join(',')
						},
						"dataType": "JSON",
						"success": function(json) {
							Order.reLoadTable();
							layer.msg(json.msg);
						}
					});
					layer.close(index);
				});

			}
		});

		// 工具条点击事件
		table.on('tool(orderTyble)', function(obj) {
			var data = obj.data;
			var layEvent = obj.event;
			if(layEvent === 'show') {
				Order.onShowOrder(data);
			}else if(layEvent === 'edit') {
				$.ajax({
					"url": Feng.ctxPath + '/order/getLockCount',
					"type": "POST",
					"data": {
						orderNo: data.orderNo
					},
					"dataType": "JSON",
					"success": function(json) {
						if(json.data) {
							Order.addNewTab(data.orderNo, '/order/toOrderEdit/' + data.orderNo, "处理");
							Order.reLoadTable();
							/*obj.update({
								processBy: json.msg
							});*/
						} else {
							layer.msg(json.msg, {
								icon: 5,
								time: 2000
							});
						}
					},
					"error": function() {
						layer.msg("请求异常!", {
							icon: 5,
							time: 2000
						});
					}
				});
			}else if(layEvent == 'unlock') {
				$.ajax({
					"url": Feng.ctxPath + '/order/deleteProcess',
					"type": "POST",
					"data": {
						orderNo: data.orderNo
					},
					"dataType": "JSON",
					"success": function(json) {
						if(json.code == 0) {
							layer.msg("已解锁");
							Order.reLoadTable();
						} else {
							layer.msg(json.msg);
						}
					}
				});
			}else if(layEvent == 'refuse') {
				$.ajax({
					"url": Feng.ctxPath + '/order/canRefuseOrder',
					"type": "POST",
					"data": {
						orderNo: data.orderNo
					},
					"dataType": "JSON",
					"success": function(json) {
						if(json.data) {
							var index = layer.open({
								type: 2,
								skin: "layui-layer-rim", //皮肤类型，在skin文件夹中
								area: ["350px", "250px"], //范围大小
								title: "订单驳回",
								content: [Feng.ctxPath + '/view/refuseOrder', 'no'],
								btn: ['提交', '取消'],
								yes: function(index, layero) { //确定按钮的处理函数
									var body = layer.getChildFrame('body', index);
									var str = body.find("#reason").val();
									$.ajax({
										"url": Feng.ctxPath + '/order/tcRefuseOrder',
										"type": "POST",
										"data": {
											orderNo: data.orderNo,
											reason: str
										},
										"dataType": "JSON",
										"success": function(json) {
											layer.msg(json.msg);
											//Order.reLoadTable();
										}
									});
								},
								btn2: function(index, layero) { //取消按钮后的处理函数
									layer.close(index);
								},
							});
						} else {
							layer.msg(json.msg);
						}
					}
				});

			}
		});

		/**
		 * 点击查看
		 * @param data 点击按钮时候的行数据
		 */
		Order.onShowOrder = function(data) {
			var orderNo = data.orderNo;
			Order.addNewTab(":" + orderNo, '/order/toOrderView/' + orderNo, "查看");
		};
		/**
		 *在父页面新增一个tab 
		 * @param  url 页面地址
		 * @param  name tab标题
		 */
		Order.addNewTab = function(id, url, name) {
			parent.loadTab(id, url, name);
		};

		Order.reLoadTable = function() {
			var sourceText = $("input[type='radio']:checked").val();
			var str = sourceText.split("-");
			var orderSource = str[0];
			var orderShop = str[1];
			var name = $("#name").val();
			var pnr = $("#pnr").val();
			var ticketNo = $("#ticketNo").val();
			var startDate = $("#startDate").val();
			var endDate = $("#endDate").val();
			var orderNo = $("#orderNo").val();
			var flight = $("#flight").val();
			var orderStatus = $("#orderStatus").val();
			var flightStartDate = $("#flightStartDate").val();
			var flightEndDate = $("#flightEndDate").val();
			tableResult.reload('orderTable', {
				url: Feng.ctxPath + '/order/getOrder',
				where: {
					'orderSource': orderSource,
					'orderShop': orderShop,
					'name': name,
					'pnr': pnr,
					'ticketNo': ticketNo,
					'endDate': endDate,
					'startDate': startDate,
					'orderNo': orderNo,
					'flight': flight,
					'orderStatus': orderStatus,
					'flightStartDate': flightStartDate,
					'flightEndDate': flightEndDate
				}
			});
		}
	});
});
var i = 0;

// 搜索按钮点击事件
$('#btnSearch').click(function() {
	var sourceText = $("input[type='radio']:checked").val();
	var str = sourceText.split("-");
	var orderSource = str[0];
	var orderShop = str[1];
	var name = $("#name").val();
	var pnr = $("#pnr").val();
	var ticketNo = $("#ticketNo").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var orderNo = $("#orderNo").val();
	var flight = $("#flight").val();
	var orderStatus = $("#orderStatus").val();
	var flightStartDate = $("#flightStartDate").val();
	var flightEndDate = $("#flightEndDate").val();
	if(orderNo != "") {
		if(sourceText == "0-0") {
			layer.msg("若无数据尝试选择订单来源搜索");
		}
	}
	tableResult.reload({
		url: Feng.ctxPath + '/order/getOrder',
		where: {
			'orderSource': orderSource,
			'orderShop': orderShop,
			'name': name,
			'pnr': pnr,
			'ticketNo': ticketNo,
			'endDate': endDate,
			'startDate': startDate,
			'orderNo': orderNo,
			'flight': flight,
			'orderStatus': orderStatus,
			'flightStartDate': flightStartDate,
			'flightEndDate': flightEndDate
		},
		page: {
			curr: 1 //重新从第 1 页开始
		}
	});
});

//倒计时
var thisTimer, setCountdown = function(doc, direct_time) {
	setTimeout(function() {
		if(direct_time != '' || direct_time != null) {
			var dateArr = direct_time.split(" ");
			var arr1 = dateArr[0].split("-")
			var arr2 = dateArr[1].split(":")
			var endTime = new Date(arr1[0], arr1[1] - 1, arr1[2], arr2[0], arr2[1], arr2[2]);
			var serverTime = new Date(); //假设为当前服务器时间，这里采用的是本地时间，实际使用一般是取服务端的
			clearTimeout(thisTimer);
			util.countdown(endTime, serverTime, function(date, serverTime, timer) {
				var str = date[0] + '天' + date[1] + '时' + date[2] + '分' + date[3] + '秒';
				lay('#' + doc + '').html(str);
				thisTimer = timer;
			});
		}
	}, 3000);

};

function time_dis(direct_time) {
	// direct_time格式为yyyy-mm-dd hh:mm:ss 指定时间
	var now_time = Date.parse(new Date()); //当前时间的时间戳
	var end_time = Date.parse(new Date(direct_time)); //指定时间的时间戳
	//计算相差分钟数
	var leave2 = leave1 % (3600 * 1000); //计算小时数后剩余的毫秒数
	var minutes = Math.floor(leave2 / (60 * 1000));
	return minutes;
}
//计算两时间相差几分钟
function TimeDifference(direct_time) {
	if(direct_time == '') {
		return "";
	}
	var last = Date.parse(new Date(direct_time)); //指定时间的时间戳
	if(isNaN(last)) {
		return "";
	}
	var now = Date.parse(new Date());
	if(now > last) {
		return "out";
	}
	var diff = (last - now) / 1000 / 60;
	return diff.toFixed(0);
}


