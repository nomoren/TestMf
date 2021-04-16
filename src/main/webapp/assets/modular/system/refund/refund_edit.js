var tableResult;
$(document).ready(function() {
	layui.use(['layer', 'table', 'laydate', 'element', 'form'], function() {
		var layer = layui.layer;
		var table = layui.table;
		var element = layui.element;
		var laydate = layui.laydate;
		var form = layui.form;

		//渲染日期
		laydate.render({
			elem: '#pAppDate',
			type: 'datetime'
		});
		laydate.render({
			elem: '#pRemDate',
			type: 'datetime'
		});
		laydate.render({
			elem: '#aAppDate',
			type: 'datetime'
		});
		laydate.render({
			elem: '#aRemDate',
			type: 'datetime'
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
						
					}
				]
			];
		};
		var orderNo = $("#orderNo").val();
		var retNo = $("#retNo").val();
		//日志
		tableResult=table.render({
			elem: '#' + Log.tableId,
			height: 500,
			url: Feng.ctxPath + '/refund/getLogByRetNo?retNo=' + retNo //数据接口
				,
			method: "post",
			page: true //开启分页
				,
			even: true,
			limit: 10,
			cols: Log.initColumn()
		});
		var refundId = $("#refundId").val();

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
							 "<th>#{tradeNo}</th>"+
							"<th>#{supplier}</th>" +
							"<th>#{payAmount}</th>" +
							"<th>#{payWay}</th>" +
							"<td >#{remark}</td>" +
							"<td>#{passengerNames}</td>" +
							"<td>-</td>" +
							"<tr>";
						html = html.replace(/#{tradeNo}/g, purchList[i] == null ? "" : purchList[i].tradeNo);
						html = html.replace(/#{passengerNames}/g, purchList[i] == null ? "" : purchList[i].passengerNames);
						html = html.replace(/#{supplier}/g, purchList[i] == null ? "" : purchList[i].supplier);
						html = html.replace(/#{payAmount}/g, purchList[i] == null ? "" : purchList[i].payAmount);
						html = html.replace(/#{payWay}/g, purchList[i] == null ? "" : purchList[i].payWay);
						html = html.replace(/#{remark}/g, purchList[i] == null ? "" : purchList[i].remark);
						$("#purchList").append(html);
					}
					$("#purchInfo").show();
				}
			}
		});
		
		//获取销售规则
		$.ajax({
			"url": Feng.ctxPath + '/notic/sellRole',
			"type": "POST",
			"data": {
				orderNo: orderNo
			},
			"dataType": "JSON",
			"success": function(json) {
				$("#sellRole").empty();
				$("#sellRole").append(json.data)
			}
		});


	});
})

$("#confirmRefund").click(function() {
	var retNo=$("#retNoText").text();
	$.ajax({
		"url": Feng.ctxPath + '/refund/confirmRefund',
		"type": "POST",
		"data": {
			retNo:retNo
		},
		"dataType": "JSON",
		"success": function(json) {
			layer.msg(json.msg);
			tableResult.reload();
		},
		"error":function(){
			layer.msg("出错了，请到平台上操作！");
			tableResult.reload();
		}
	});

});
/*$("#savePassengerRe").click(function() {
	$.each($(".cAppDate"), function(index) {
		$(this).val($("#pAppDate").val());
	});
	$.each($(".cRemDate"), function(index) {
		$(this).val($("#pRemDate").val());
	});
	$.each($(".airAppDate"), function(index) {
		$(this).val($("#aAppDate").val());
	});
	$.each($(".airRemDate"), function(index) {
		$(this).val($("#aRemDate").val());
	});
	var data = new Array();
	$.each($(".refundP"), function(index) {
		var refundPassenger = $(this).serializeObject();
		data.push(refundPassenger);
	});
	var result = JSON.stringify(data);
	if(result == '[]') {
		return;
	}
	$.ajax({
		"url": Feng.ctxPath + '/refund/updateRefunds',
		"type": "POST",
		"data": result,
		"dataType": "JSON",
		"contentType":"application/json",
		"success": function(json) {
			layer.msg(json.msg);
			tableResult.reload();
		}
	});
});*/

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


function updateRefunds(type) {
	$.each($(".cAppDate"), function(index) {
		$(this).val($("#pAppDate").val());
	});
	$.each($(".cRemDate"), function(index) {
		$(this).val($("#pRemDate").val());
	});
	$.each($(".airAppDate"), function(index) {
		$(this).val($("#aAppDate").val());
	});
	$.each($(".airRemDate"), function(index) {
		$(this).val($("#aRemDate").val());
	});
	if("checking" == type) {
		$.each($(".airRemStateC"), function(index) {
			$(this).val("7");
		});
	}
	var data = new Array();
	$.each($(".refundP"), function(index) {
		var refundPassenger = $(this).serializeObject();
		data.push(refundPassenger);
	});
	var result = JSON.stringify(data);
	if(result == '[]') {
		return;
	}
	$.ajax({
		"url": Feng.ctxPath + '/refund/updateRefunds',
		"type": "POST",
		"data": result,
		"dataType": "JSON",
		"contentType": "application/json",
		"success": function(json) {
			layer.msg(json.msg);
			tableResult.reload();
		}
	});

}

