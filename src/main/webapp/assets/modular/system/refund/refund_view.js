$(document).ready(function() {
	layui.use(['layer',  'table',   'element'], function() {
		var layer = layui.layer;
		var table = layui.table;
		var element = layui.element;
		
	/*	var index;
		$('#orderxq').mouseover(
			function() {
				index = layer.tips(
						'航空公司：深圳航空<br/>里程：xxx公里  &nbsp;&nbsp;飞行时间： xxx',
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
		var orderNo=$("#orderNo").val();
		var retNo=$("#retNo").val();
		//日志
		table.render({
	   	 	elem: '#' + Log.tableId
	   		,height: 500
	    	,url: Feng.ctxPath+'/refund/getLogByRetNo?retNo='+retNo //数据接口
	   	 	,method:"post"	
	    	,page: true //开启分页
	    	,even:true
	    	,limit:10
	    	,cols: Log.initColumn()
	  	});
	  	var refundId=$("#refundId").val();
	  	//获取退款信息
	  	$.ajax({
			"url": Feng.ctxPath+'/refund/getRefundInfo',
			"type": "POST",
			"data": {
				retNo:retNo,
				refundId:refundId
			},
			"dataType": "JSON",
			"success": function(json) {
				if(json.code==-1){
					layer.msg("获取退款信息失败!");
				}else{
					var refund=json.data.refund;
					var refundPassenger=json.data.refundPassenger;
					$("#cAppDate").val(refund.cAppDate);
					$("#cRemDate").val(refund.cRemDate);
					$("#cRealPrice").val(refund.cRealPrice);
					$("#passengers").val(refund.passengerName);
					$("#airAppDate").val(refund.airAppDate);
					$("#airRemDate").val(refund.airRemDate);	
					$("#refundPassengers").empty();
					for (var i=0;i<refundPassenger.length;i++) {
						var html="<tr>"+
						  	 "<th>#{passengerName}</th>"+
						  	 "<th>#{ticketNo}</th>"+
						  	 "<th>#{cRealPrice}</th>"+
						  	 "<th>#{airRealPrice}</th>"+
						  	 "<th>#{airEstimatePrice}</th>"+
						  	 "<th>#{remark}</th>"+
						  	 "<th>#{airRefundType}</th>"+
						  	 "<th>#{airStatus}</th>"+
						  	 "<tr>";
						html = html.replace(/#{passengerName}/g, refundPassenger[i]==null?"":refundPassenger[i].passengerName);
						html = html.replace(/#{ticketNo}/g, refundPassenger[i]==null?"":refundPassenger[i].ticketNo);
						html = html.replace(/#{airRefundType}/g, refundPassenger[i]==null?"":refundPassenger[i].airRefundType);
						html = html.replace(/#{airStatus}/g, refundPassenger[i]==null?"":refundPassenger[i].airRemState);
						html = html.replace(/#{cRealPrice}/g, refundPassenger[i]==null?"":refundPassenger[i].cRealPrice);
						html = html.replace(/#{airRealPrice}/g, refundPassenger[i]==null?"":refundPassenger[i].airRealPrice);
						html = html.replace(/#{airEstimatePrice}/g, refundPassenger[i]==null?"":refundPassenger[i].airEstimatePrice);
						html = html.replace(/#{remark}/g, refundPassenger[i]==null?"":refundPassenger[i].remark);
						$("#refundPassengers").append(html);
					}
				}
			}
		});
	  	
		//获取采购信息
	  	$.ajax({
			"url": Feng.ctxPath+'/purch/getPruchListByOrderNo',
			"type": "POST",
			"data": {
				orderNo:orderNo
			},
			"dataType": "JSON",
			"success": function(json) {
				var purchList=json.data;
				if(purchList.length>0){
					$("#purchList").empty();
					for(var i=0;i<purchList.length;i++){
						var html="<tr>"+
						  /*	 "<th>#{purchaseNo}</th>"+*/
						  	 "<th>#{tradeNo}</th>"+
						  	 "<th>#{supplier}</th>"+
						  	 "<th>#{payAmount}</th>"+
						  	 "<th>#{payWay}</th>"+
						  	 "<td >#{remark}</td>"+
						  	 "<td>#{passengerNames}</td>"+
						  	 "<td>#{type}</td>"+
						  	 "<td>-</td>"+
						  	 "<tr>";
						/*html = html.replace(/#{purchaseNo}/g, purchList[i]==null?"":purchList[i].purchaseNo);*/
						html = html.replace(/#{tradeNo}/g, purchList[i]==null?"":purchList[i].tradeNo);
						html = html.replace(/#{passengerNames}/g, purchList[i]==null?"":purchList[i].passengerNames);
						html = html.replace(/#{supplier}/g, purchList[i]==null?"":purchList[i].supplier);
						html = html.replace(/#{payAmount}/g, purchList[i]==null?"":purchList[i].payAmount);
						html = html.replace(/#{payWay}/g, purchList[i]==null?"":purchList[i].payWay);
						html = html.replace(/#{remark}/g, purchList[i]==null?"":purchList[i].remark);
						html = html.replace(/#{type}/g, purchList[i]==null?"":purchList[i].type);
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