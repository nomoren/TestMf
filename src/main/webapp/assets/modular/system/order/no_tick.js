var tableResult;
var util;
var layer;
$(document).ready(function() {
	layui.use(['layer', 'form', 'table', 'admin', 'ax', 'laydate', 'element'], function() {
		var $ = layui.$;
		util = layui.util;
		layer = layui.layer;
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
							}
						}
					},
					{
						field: 'tripType',
						sort: false,
						title: '类型',
						align: 'center',
						templet: function(d) {
							var sameOrder='';
							var str='';
							if(d.passengerCount>1){
								str="<span style='font-weight: bold;color: #f60;'>(多人)</span>";
							}
							if(d.sameCount>1) {
                                sameOrder='<span class="layui-icon layui-icon-tips"  style="color: #f60;"  onmouseover="haveSame(this)" onmouseout="delehaveSame()" lay-event="haveSame"></span>';
							}
							if(d.tripType == 0) {
								return sameOrder+'单程'+str;
							} else {
								return sameOrder+'往返'+str;
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
						sort: false,
						title: '总价/人数',
						align: 'center'.arguments,
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
						title: '紧急程度<hr/>AV状态',
						width: 130,
						align: 'center',
						templet: function(d) {
							//var diff = TimeDifference(d.lastPrintTicketTime);
							var str = d.degreeStr;
							var degree=d.degree;
                            str += '<br/>';
							//AV状态
                            var avstatus=d.avStatus;
                            if("A"!=avstatus){
                                str += '<span style="color: #f60;">' + avstatus + '</span>';
                            }else{
                                str += avstatus;
                            }
							return str;
						}
					},
					{
						field: 'lastPrintTicketTime',
						sort: true,
						title: '最晚出票',
						width: 155,
						align: 'center'
						/*templet: function(d) {
							return '<span style="color: #f60;">' + d.lastPrintTicketTime + '</span>';
						}*/
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
			url: Feng.ctxPath + '/noTick/getOrder',
			height: AudioContext,
			method: "POST",
			even: true,
			page: true,
			limit: 20,
			limits: [10, 20, 30, 40, 50, 60],
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
		/*	done: function (res, curr, count) {
				res.data.forEach(function (item, index) {
					if (item.sameCount > 1){
						$('div[lay-id="orderTable"]').
						find('tr[data-index="' + index + '"]').
						css('background-color', '#CCCCCC');
					}
				});
			}*/

		});

		//渲染日期
		var d = new Date();
		var m = d.getMonth();
		var y = d.getFullYear();
		if(m == 0) {
			m = 12;
			y = y - 1;
		}
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

		// 工具条点击事件
		table.on('tool(orderTyble)', function(obj) {
			var data = obj.data;
			var layEvent = obj.event;
			if(layEvent === 'show') {
				Order.onShowOrder(data);
			} else if(layEvent === 'edit') {
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
							/*obj.update({
								processBy: json.msg
							});*/
							Order.reLoadTable(false);
						} else {
							layer.msg(json.msg, {
								icon: 5,
								time: 2000
							});
						}
					},
					"error": function() {
						Order.addNewTab(data.orderNo, '/order/toOrderEdit/' + data.orderNo, "处理");
						Order.reLoadTable(false);
					}
				});
			} else if(layEvent == 'unlock') {
				$.ajax({
					"url": Feng.ctxPath + '/order/deleteProcess',
					"type": "POST",
					"data": {
						orderNo: data.orderNo
					},
					"dataType": "JSON",
					"success": function(json) {
						if(json.code == 0) {
							Order.reLoadTable(false);
							layer.msg("已解锁");
						} else {
							layer.msg(json.msg);
						}
					}
				});
			} else if(layEvent == 'refuse') {
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

			}else if(layEvent == 'haveSame'){
				var flightNo=data.flightNo;
				var flightDate=data.flightDate;
				$("#flight").val(flightNo);
				$("#flightStartDate").val(flightDate);
				$("#flightEndDate").val(flightDate);
				Order.reLoadTable(true);
			}
		});

		/**
		 * 点击查看
		 *
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
		Order.addNewTab = function(orderNo, url, name) {
			/*var title = parent.$("#layui-tab-title");
			$(title).append("<li lay-id='" + url + "' class='layui-this'>" + name + "</li>");
			var content = parent.$("#layui-tab-content");
			$(content).append("<div class='layui-tab-item layui-show'><iframe lay-id='" + url + "' style='height: 100%;' frameborder='0' src='" + Feng.ctxPath + url + "' class='admin-iframe'></iframe></div>");*/
			parent.loadTab(orderNo, url, name);
		};

		Order.reLoadTable = function(one) {
			tableReload(one);
		}

		//监听单选框
		form.on('radio(orderSource)', function(data) {
			tableReload(true);
		});

	});
	

});

// 搜索按钮点击事件
$('#btnSearch').click(function() {
	tableReload(true);
});

function tableReload(one) {
	var sourceText = $("input[type='radio']:checked").val();
	var str = sourceText.split("-");
	var orderSource = str[0];
	var orderShop = str[1];
	var pnr = $("#pnr").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var orderNo = $("#orderNo").val();
	var orderStatus = $("#orderStatus").val();
	var flight = $("#flight").val();
	var policyType = $("#policyType").val();
	var isChecked = $("#onlyMe").prop("checked");
	var flightStartDate = $("#flightStartDate").val();
	var flightEndDate = $("#flightEndDate").val();
	
	if(orderNo != "") {
		if(sourceText == "0-0") {
			layer.msg("若无数据尝试选择订单来源搜索");
		}
	}
	
	var dataurl=Feng.ctxPath + '/noTick/getOrder';
	
	var jsonData={
		url: dataurl,
		where: {
			'orderSource': orderSource,
			'orderShop': orderShop,
			'pnr': pnr,
			'endDate': endDate,
			'startDate': startDate,
			'orderNo': orderNo,
			'orderStatus': orderStatus,
			'flight': flight,
			'policyType': policyType,
			'flightStartDate': flightStartDate,
			'flightEndDate': flightEndDate,
			'isChecked':isChecked
		}
	};
	//是否从第一页开始
	if(one){
		jsonData.page={curr: 1 };
	}
	tableResult.reload(jsonData);
}
//倒计时
var thisTimer, setCountdown = function(doc, direct_time) {
	//var times=Math.floor(Math.random()*1000+3500)
	setTimeout(function() {
		if(direct_time != '' || direct_time != null) {
			var dateArr = direct_time.split(" ");
			var arr1 = dateArr[0].split("-")
			var arr2 = dateArr[1].split(":")
			var endTime = new Date(arr1[0], arr1[1] - 1, arr1[2], arr2[0], arr2[1], arr2[2]);
			var serverTime = new Date(); //为当前服务器时间，这里采用的是本地时间，实际使用一般是取服务端的
			clearTimeout(thisTimer);
			util.countdown(endTime, serverTime, function(date, serverTime, timer) {
				var str = date[0] + '天' + date[1] + '时' + date[2] + '分' + date[3] + '秒';
				lay('#' + doc + '').html(str);
				//thisTimer = timer;
			});
		}
	}, 5000);

};

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


var index;
function haveSame(even){
	index=layer.tips(
		'有相同行程订单,点击查询',
		even, {
			tips: [3, '#78BA32'],
			time: 2000
		});
}

function delehaveSame(){
	layer.close(index);
}


