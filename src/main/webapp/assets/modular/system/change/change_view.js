$(document).ready(function() {
	layui.use(['layer',  'table',   'element'], function() {
		var layer = layui.layer;
		var table = layui.table;
		var element = layui.element;
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
		var orderNo=$("#orderNo").val();
		//日志
		table.render({
	   	 	elem: '#' + Log.tableId
	   		,height: 500
	    	,url: Feng.ctxPath + '/order/getLogByOrderNo?orderNo='+orderNo //数据接口
	   	 	,method:"post"	
	    	,page: true //开启分页
	    	,even:true
	    	,limit:10
	    	,cols: Log.initColumn()
	  });
	  	var newCOrderNo=$("#newCOrderNo").val();
	  	//获取改签信息
	  	$.ajax({
			"url": Feng.ctxPath+'/change/getChangeInfo',
			"type": "POST",
			"data": {
				newCOrderNo
			},
			"dataType": "JSON",
			"success": function(json) {
				if(json.code==-1){
					layer.msg("获取改签信息失败!");
				}else{
					var changePassenger=json.data;
					$("#changePassengers").empty();
					for (var i=0;i<changePassenger.length;i++) {
						var html="<tr>"+
						  	 "<th>#{passengerName}</th>"+
						  	 "<th>#{cabin}</th>"+
						  	 "<th>#{revenuePrice}</th>"+
						  	 "<th>#{payPrice}</th>"+
						  	 "<th>#{state}</th>"+
						  	 "<th>#{ticketNo}</th>"+
						  	 "<th>#{changeDate}</th>"+
						  	 "<th>#{bussinsNo}</th>"+
						  	 "<tr>";
						html = html.replace(/#{passengerName}/g, changePassenger[i]==null?"":changePassenger[i].passengerName);
						html = html.replace(/#{cabin}/g, changePassenger[i]==null?"":changePassenger[i].cabin);
						html = html.replace(/#{revenuePrice}/g, changePassenger[i]==null?"":changePassenger[i].revenuePrice);
						html = html.replace(/#{payPrice}/g, changePassenger[i]==null?"":changePassenger[i].payPrice);
						html = html.replace(/#{state}/g, changePassenger[i]==null?"":changePassenger[i].state);
						html = html.replace(/#{ticketNo}/g, changePassenger[i]==null?"":changePassenger[i].tktNo);
						html = html.replace(/#{changeDate}/g, changePassenger[i]==null?"":changePassenger[i].changeDate);
						html = html.replace(/#{bussinsNo}/g, changePassenger[i]==null?"":changePassenger[i].bussinsNo);
						$("#changePassengers").append(html);
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
						  	 "<th>#{tradeNo}</th>"+
						  	 "<th>#{supplier}</th>"+
						  	 "<th>#{payAmount}</th>"+
						  	 "<th>#{payWay}</th>"+
						  	 "<td >#{remark}</td>"+
						  	 "<td>#{passengerNames}</td>"+
						  	 "<td>#{type}</td>" +
						  	 "<th>#{newTicketNo}</th>" +
						  	 "<td>-</td>"+
						  	 "<tr>";
						html = html.replace(/#{tradeNo}/g, purchList[i]==null?"":purchList[i].tradeNo);
						html = html.replace(/#{passengerNames}/g, purchList[i]==null?"":purchList[i].passengerNames);
						html = html.replace(/#{supplier}/g, purchList[i]==null?"":purchList[i].supplier);
						html = html.replace(/#{payAmount}/g, purchList[i]==null?"":purchList[i].payAmount);
						html = html.replace(/#{payWay}/g, purchList[i]==null?"":purchList[i].payWay);
						html = html.replace(/#{remark}/g, purchList[i]==null?"":purchList[i].remark);
						html = html.replace(/#{type}/g, purchList[i] == null ? "" : purchList[i].type);
						html = html.replace(/#{newTicketNo}/g, purchList[i] == null ? "" : purchList[i].newTicketNo);
						$("#purchList").append(html);
					}
					$("#purchInfo").show();
				}
			}
		});
		
	
	});
})