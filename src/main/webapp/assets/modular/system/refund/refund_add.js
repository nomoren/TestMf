$(document).ready(function() {
	var element;
	var table;
	var layer;
	layui.use(['layer', 'form', 'table', 'laydate', 'element'], function() {
		layer = layui.layer;
		table = layui.table;
		element = layui.element;
	});
	var url = Feng.ctxPath + '/passenger/getPassengersByOrderNo';
	var orderNo = $("#orderNo").text();
	$.ajax({
		"url": url,
		"type": "POST",
		"data": {
			orderNo: orderNo,
		},
		"dataType": "JSON",
		"success": function(json) {
			if(json.code == -1) {
				layer.msg(json.msg);
			} else {
				var passengerList = json.data;
				$("#passengerList").empty();
				for(var i = 0; i < passengerList.length; i++) {
					var passenger = passengerList[i];
					var html = "<form class='layui-form passenger'>" +
						"<div><table class='layui-table'><colgroup><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'></colgroup><thead><tr>" +
						"<th>姓名：#{name}</th>" +
						"<th>身份证：#{certNo}</th>" +
						"<th>PNR：#{pnr}</th>" +
						"<th>票号：#{ticketNo}</th>" +
						"<th>实收金额：￥#{actualPrice}</th>" +
						"</tr></thead></table><table class='layui-table'><colgroup><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'></colgroup><thead><tr>" +
						"<th>退票状态(乘)</th><th>退票单号</th><th>退款日期(乘)</th><th>实退(乘)</th><th>编码状态</th><th>申请类型</th></tr></thead><tbody>" +
						"<tr><td ><select name='cRemState' id='cRemState#{id}'>" +
						"<option value='-1'>未退票</option><option value='0'>申请中</option><option value='1'>已退款</option><option value='2'>已拒绝</option><option value='3'>等航变</option><option value='4'>取消作废</option></select>" +
						"</td><td><input type='text' name='retNo' id='retNo#{id}' required lay-verify='required' class='layui-input'></td>" +
						"<th><input type='text' name='cRemDate' id='cRemDate#{id}' class='layui-input refundDate'></th><th><input type='text' name='cRealPrice' id='cRealPrice#{id}' required lay-verify='required' class='layui-input'></th>"+
						"<th><input type='radio' name='xePnrStatus' value='0' title='未取消' class='xePnrStatus#{id}'>"+
      					"<input type='radio' name='xePnrStatus' value='1' title='已取消' class='xePnrStatus#{id}' checked='checked'></th>"+
      					"<th><input type='radio' name='refundType' value='0' title='自愿' class='refundType#{id}' checked='checked'>"+
      					"<input type='radio' name='refundType' value='1' title='非自愿' class='refundType#{id}' ></th>"+
      					"</tr>" +
						"</tbody></table><table class='layui-table'><colgroup><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'><col width='150'></colgroup>" +
						"<thead><tr><th>退票状态(供)</th><th>申请日期(供)</th><th>退款日期(供)</th><th>供应预退</th><th>供应实退</th><th>退票备注</th></tr></thead><tbody>" +
						"<tr><th class='layui-form'><select name='airRemState' id='airRemState#{id}'>" +
						"<option value='-1'>未退票</option><option value='0'>申请中</option><option value='1'>已退款</option><option value='2'>已拒绝</option><option value='3'>等航变</option><option value='4'>取消作废</option></select>" +
						"</th><th><input type='text' name='airAppDate' id='airAppDate#{id}' class='layui-input refundDate'></th>" +
						"<th><input type='text' name='airRemDate' id='airRemDate#{id}' class='layui-input refundDate'></th><th><input type='text' name='airEstimatePrice' id='airEstimatePrice#{id}' class='layui-input'></th><th><input type='text' name='airRealPrice' id='airRealPrice#{id}' class='layui-input'></th>" +
						"<th><input type='text' name='remark' id='remark#{id}' class='layui-input'></th></tr>" +
						"</tbody></table>" +
						"<input type='hidden' name='orderId' value='#{orderId}'/><input type='hidden' name='orderNo' value='#{orderNo}'/>" +
						"<input type='hidden' name='orderSource' value='#{orderSource}'/><input type='hidden' name='orderShop' value='#{orderShop}'/>" +
						"<input type='hidden' name='passengerId' value='#{passengerId}'/><input type='hidden' name='airlineCode' value='#{airlineCode}'/>" +
						"<input type='hidden' name='flightNo' value='#{flightNo}'/><input type='hidden' name='depCityCode' value='#{depCityCode}'/>" +
						"<input type='hidden' name='arrCityCode' value='#{arrCityCode}'/><input type='hidden' name='flightDate' value='#{flightDate}'/>" +
						"<input type='hidden' name='printTicketCabin' value='#{printTicketCabin}'/><input type='hidden' name='passengerName' value='#{passengerName}'/>" +
						"<input type='hidden' name='ticketNo' value='#{ticketNo}'/><input type='hidden' name='purchPalse' value='#{purchPalse}'/>" +
						"<input type='hidden' name='refundId' id='refundId#{id}' value=''/>" +
						"</div></from>" +
						"<hr class='layui-bg-black'>";
					html = html.replace(/#{name}/g, passenger.name);
					html = html.replace(/#{certNo}/g, passenger.certNo);
					html = html.replace(/#{pnr}/g, passenger.pnr);
					html = html.replace(/#{ticketNo}/g, passenger.ticketNo);
					html = html.replace(/#{actualPrice}/g, passenger.actualPrice);
					html = html.replace(/#{purchPalse}/g, passenger.purchPalse);
					html = html.replace(/#{orderId}/g, passenger.orderId);
					html = html.replace(/#{orderNo}/g, passenger.orderNo);
					html = html.replace(/#{orderSource}/g, passenger.orderSource);
					html = html.replace(/#{orderShop}/g, passenger.orderShop);
					html = html.replace(/#{passengerId}/g, passenger.passengerId);
					html = html.replace(/#{airlineCode}/g, passenger.airlineCode);
					html = html.replace(/#{flightNo}/g, passenger.flightNo);
					html = html.replace(/#{depCityCode}/g, passenger.depCityCode);
					html = html.replace(/#{arrCityCode}/g, passenger.arrCityCode);
					html = html.replace(/#{flightDate}/g, passenger.flightDepDate);
					html = html.replace(/#{printTicketCabin}/g, passenger.printTicketCabin);
					html = html.replace(/#{passengerName}/g, passenger.name);
					html = html.replace(/#{id}/g, passenger.passengerId);
					$("#passengerList").append(html);
					var id = passenger.passengerId;
					var refund = passenger.refund;
					if(refund != "") {
						$("#cRemState" + id + " option[value='" + refund.cRemState + "']").attr("selected", "selected");
						$("#airRemState" + id + " option[value='" + refund.airRemState + "']").attr("selected", "selected");
						$("input[type=radio][class=xePnrStatus"+id+"][value="+refund.xePnrStatus+"]").prop("checked", true);
						$("input[type=radio][class=refundType"+id+"][value="+refund.refundType+"]").prop("checked", true);
						$("#retNo" + id).val(refund.retNo);
						$("#cRemDate" + id).val(refund.cRemDate.substring(0, 10));
						$("#cRealPrice" + id).val(refund.cRealPrice);
						$("#airAppDate" + id).val(refund.airAppDate.substring(0, 10));
						$("#airEstimatePrice" + id).val(refund.airEstimatePrice);
						$("#airRealPrice" + id).val(refund.airRealPrice);
						$("#airRemDate" + id).val(refund.airRemDate.substring(0, 10));
						$("#remark" + id).val(refund.remark);
						$("#refundId" + id).val(refund.refundId);
					}
				}
				layui.form.render();
				$(".layui-bg-black").last().remove();
				$('.refundDate').each(function(index) {
					var _this = this;
					layui.laydate.render({
						elem: _this,
						format: 'yyyy-MM-dd'
					});
				});
			}
		}
	});
});
$("#saveRefund").click(function() {
	var data = new Array();
	var isAll = false; //退票单信息是否完善
	$.each($(".passenger"), function(index) {
		var refundInfo = $(this).serializeObject();
		if(refundInfo.retNo != "") {
			isAll = true;
			data.push(refundInfo);
		}
	});
	if(!isAll) {
		layer.msg('请输入退票单号', {
			icon: 5
		});
	}
	var result = JSON.stringify(data);
	if(result == '[]') {
		return;
	}
	/*console.log(result);*/
	$.ajax({
		"url": Feng.ctxPath + '/refund/saveRefunds',
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