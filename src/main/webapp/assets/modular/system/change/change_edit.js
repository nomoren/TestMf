$(document).ready(function() {
	layui.use(['layer', 'table', 'laydate', 'element', 'form'], function() {
		var layer = layui.layer;
		var table = layui.table;
		var element = layui.element;
		var laydate = layui.laydate;
		var form = layui.form;
		//渲染日期
		$.each($(".changeDate"), function(index) {
			laydate.render({
				elem: this,
				format: 'yyyy-MM-dd HH:mm:ss'
			});
		});

		var Log = {
			tableId: "logTable" //表格id
		};

		Log.initColumn = function() {
			return [
				[{
						field: 'type',
						sort: false,
						title: '类型',
						align: 'center'
					},
					{
						field: 'date',
						sort: false,
						title: '记录时间',
						align: 'center'
					},
					{
						field: 'name',
						sort: false,
						title: '操作人',
						align: 'center'
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
		var newCOrderNo = $("#newCOrderNo").val();
		//日志
		table.render({
			elem: '#' + Log.tableId,
			height: 500,
			url: Feng.ctxPath + '/order/getLogByOrderNo?orderNo=' + orderNo, //数据接口

			method: "post",
			page: true //开启分页
				,
			even: true,
			limit: 10,
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
							"<th>#{tradeNo}</th>" +
							"<th>#{supplier}</th>" +
							"<th>#{payAmount}</th>" +
							"<th>#{payWay}</th>" +
							"<td>#{remark}</td>" +
							"<td>#{passengerNames}</td>" +
							"<th>#{type}</th>" +
							"<th>#{newTicketNo}</th>" +
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
						html = html.replace(/#{newTicketNo}/g, purchList[i] == null ? "" : purchList[i].newTicketNo);
						$("#purchList").append(html);
					}
				}
			}
		});

		//监听采购单提交
		form.on('submit(purchForm)', function(data) {
			var data = $('#purch').serialize();

			// 改签单号
			var changeNo = $("#changeNo").text();
			data += "&changeNo=" + changeNo;
			
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
			
			/*if($("#purchType").val() == 1) {
				if($("#returnMoney").val() == "") {
					layer.msg("差错报表要输入退款金额");
					return false;
				}
			}*/
			var url = Feng.ctxPath + '/purch/addChangePurch';
			$.ajax({
				"url": url,
				"type": "POST",
				"data": data,
				"dataType": "JSON",
				"success": function(json) {
					layer.msg(json.msg);
					$('#purch')[0].reset();
					reloadPurch();
					
					reloadChange(changeNo);
				}
			});
			return false;
		});

	});
})

// 采购单作废
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
					
					var changeNo = $("#changeNo").text();
					reloadChange(changeNo);
				} else {
					layer.msg("修改失败");
				}

			}
		});
		layer.close(index);

	});
}

// 重新加载采购单
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
					"<th>#{newTicketNo}</th>" +
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
				html = html.replace(/#{newTicketNo}/g, purchList[i] == null ? "" : purchList[i].newTicketNo);
				$("#purchList").append(html);
			}
		}
	});
}

// 重新加载改签单
function reloadChange(newCOrderNo) {
	$.ajax({
		"url": Feng.ctxPath+'/change/getChangeInfo',
		"type": "POST",
		"data": { newCOrderNo },
		"dataType": "JSON",
		"success": function(json) {
			if(json.code==-1){
				layer.msg("获取改签信息失败!");
			}else{
				var changePassenger = json.data;
				for (var i=0;i<changePassenger.length;i++) {
					var change = changePassenger[i];
					
					var changeId = change.changeId;
					var tktNo = change.tktNo;
					var state = '1';
					if (change.state == "改签完成") {
						state = '2';
					} 
					if (change.state == "拒绝改签"){
						state = '3';
					}
					
					$("#tktNo"+changeId).val(tktNo);
					$("#state"+changeId+" option[value='"+state+"']").attr("selected","selected");
					
				}
			}
		}
	});
}

$("#saveChanges").click(function() {
	var data = new Array();
	$.each($(".changeP"), function(index) {
		var refundPassenger = $(this).serializeObject();
		// 改签票号去空处理
		refundPassenger.tktNo = refundPassenger.tktNo.trim();
		data.push(refundPassenger);
	});
	var result = JSON.stringify(data);
	if(result == '[]') {
		return;
	}
	$.ajax({
		"url": Feng.ctxPath + '/change/updateChanges',
		"type": "POST",
		"data": result,
		"dataType": "JSON",
		"contentType": "application/json",
		"success": function(json) {
			layer.msg(json.msg);
		}
	});
});
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