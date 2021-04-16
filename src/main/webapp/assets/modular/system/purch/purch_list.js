var table;
var tableResult;
$(document).ready(function() {
	layui.use(['layer', 'form', 'table', 'element'], function() {
		var layer = layui.layer;
		table = layui.table;
		var element = layui.element;

		var Purch = {
			tableId: "purchTable", //表格id
			condition: {
				RefundName: ""
			}
		};

		/**
		 * 初始化表格的列
		 */
		Purch.initColumn = function() {
			return [
				[
					{
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
						field: 'customerAmount',
						sort: false,
						title: '支付总价<hr/>采购总价',
						align: 'center',
						templet: function(d) {
							return d.customerAmount + "<hr/>" + d.payAmount;
						}
					},
					
					{
						field: 'tradeNo',
						sort: false,
						title: '交易流水号',
						align: 'center'
					},
					{
						field: 'profit',
						sort: false,
						title: '利润',
						align: 'center'
					},
					{
						field: 'payWay',
						sort: false,
						title: '支付方式<hr/>采购地',
						align: 'center',
						width: 130,
						templet: function(d) {
							return d.payWay + "<hr/>" + d.supplier;
						}
					},
					{
						field: 'remark',
						sort: false,
						title: '备注',
						align: 'center',
						width: 200
					},
					{
						field: 'passengerNames',
						sort: false,
						title: '乘机人',
						align: 'center'
					},
					{
						field: 'employeeName',
						sort: false,
						title: '出票员',
						align: 'center'
					},
					{
						field: 'printTicketDate',
						sort: false,
						title: '出票日期',
						align: 'center'
					},
					{
						field: 'type',
						sort: false,
						title: '报表类型',
						align: 'center'
					},
					{
						align: 'center',
						toolbar: '#tableBar',
						title: '操作',
						minWidth: 180
					}
				]
			];
		};
		// 渲染表格
		tableResult = table.render({
			elem: '#' + Purch.tableId,
			url: Feng.ctxPath + '/purch/getPurchList',
			height: AudioContext,
			page: true,
			even: true,
			cellMinWidth: 100,
			cols: Purch.initColumn()
			,parseData: function(res){ //res 即为原始返回的数据
    		return {
     		 "code": 0, //解析接口状态
     		 "msg": res.msg, //解析提示文本
     		 "count": res.count, //解析数据长度
    		  "data": res.data //解析数据列表
    		};
 		 }
		});

		// 工具条点击事件
		table.on('tool(purchTable)', function(obj) {
			var data = obj.data;
			var layEvent = obj.event;
			if(layEvent == 'delete') {
				layer.confirm('确认作废吗', function(index) {
					$.ajax({
						"url": Feng.ctxPath + '/purch/updatePurchaseFlag',
						"type": "POST",
						"data": {
							purchaseId: data.purchaseId
						},
						"dataType": "JSON",
						"success": function(json) {
							if(json.code == 0) {
								layer.msg(json.msg);
								obj.del();
							} else {
								layer.msg("修改失败");
							}

						}
					});
					layer.close(index);

				});
			}

		});

	});
});

function tableReload(orderSource,date,isCheck,remark) {
	var str = orderSource.split("-");
	var orderSource = str[0];
	var orderShop = str[1];
	var dates=date.split(":");
	var startDate=dates[0];
	var endDate=dates[1];
	tableResult.reload({
		where: {
			orderSource: orderSource,
			orderShop: orderShop,
			startDate: startDate,
			endDate: endDate,
			isCheck:isCheck,
			remark:remark
		},
		page: {
			curr: 1 //重新从第 1 页开始
		}
	});

}