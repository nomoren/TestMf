var form;
$(document).ready(function(){
	layui.use(['layer', 'form', 'laydate', 'element','table'], function() {
	var layer = layui.layer;
	form = layui.form;
	var table = layui.table;
	var element = layui.element;
	var laydate = layui.laydate;
	laydate.render({
		elem: '#flightDepDate',
		type: 'datetime'
	});
	laydate.render({
		elem: '#flightArrDate',
		type: 'datetime'
	});
	//监听提交
  form.on('submit(orderForm)', function(data) {
		/*layer.msg(JSON.stringify(data.field));*/
		var data = $('#order').serialize();
		var url=Feng.ctxPath+'/order/addOrder';
		$.ajax({
		"url":url,
		"type":"POST",
		"data":data,
		"dataType":"JSON",
		"success":function(json){
			layer.msg(json.msg);
			$('#order')[0].reset();
		}
	});
		return false;
	});
	//监听航程下拉选
	form.on('select(trip)', function(data) {
		if(data.value == '1') {
			$("#goBack").remove();
			var html = "<div id='goBack'>"+
				"<div class='layui-inline' id='gobackdep' style='margin-left: 14px;'>" +
				"<label class='layui-form-label'>返程日期</label>" +
				"<div class='layui-input-inline'>" +
				"<input  type='text' id='gobackDate' name='gobackDepDate'  autocomplete='off' class='layui-input'>" +
				"</div>" +
				"</div>" +
				"<div class='layui-inline' class='goback' id='gobackarr'>" +
				"<label class='layui-form-label'>到达日期</label>" +
				"<div class='layui-input-inline'>" +
				"<input  type='text' id='gobackArrDate' name='gobackArrDepDate'  autocomplete='off' class='layui-input'>" +
				"</div>" +
				"</div>"+
				"<div class='layui-inline' class='goback' id='gobackFlightNo'>" +
				"<label class='layui-form-label'>航班号</label>" +
				"<div class='layui-input-inline'>" +
				"<input  type='text' id='gobackFlightNo' name='gobackFlightNo'  autocomplete='off' class='layui-input'>" +
				"</div>" +
				"</div>"+
				"</div>";
			$("#orderInfo").append(html);
			laydate.render({
				elem: '#gobackDate',
				type: 'datetime'
			});
			laydate.render({
				elem: '#gobackArrDate',
				type: 'datetime'
			});
		} else {
			$("#goBack").remove();
		}
	});
	
  });
})
$("#addPassenger").click(function(){
		var html="<div class='newPassenger'><hr class='layui-bg-black'><div class='layui-inline'>"
				 +"<label class='layui-form-label'>姓名</label>"
				 +"<div class='layui-input-inline'><input type='text' name='name' required lay-verify='required' autocomplete='off' class='layui-input'></div></div>"
				 +"<div class='layui-inline'><label class='layui-form-label'>乘客类型</label><div class='layui-input-block'>"
				 +"<select name='passengerType' lay-verify='required'>"
				 +"<option value=''></option><option value='0'>成人</option><option value='1'>儿童</option><option value='2'>婴儿</option></select></div></div>"
			     +"<div class='layui-inline'><label class='layui-form-label'>证件号</label>"
			     +"<div class='layui-input-inline'><input type='text' name='certNo' required lay-verify='required' autocomplete='off' class='layui-input'></div></div>"
				 +"<div class='layui-inline'><label class='layui-form-label'>证件类型</label>"
				 +"<div class='layui-input-block'>"
				 +"<select name='certType' lay-verify='required'>"
				 +"<option value=''></option><option value='0'>身份证</option><option value='1'>护照</option><option value='2'>学生证</option>"
				 +"<option value='3'>军人证</option><option value='4'>回乡证</option><option value='5'>台胞证</option><option value='6'>港澳台胞</option><option value='7'>国际海员</option>"
				 +"<option value='8'>外国人永久居留证</option><option value='9'>其他证件</option>"
			     +"</select></div></div><br /><br /><div class='layui-inline'>"
		         +"<label class='layui-form-label'>票号</label>"
		         +"<div class='layui-input-inline'> <input type='text' name='ticketNo' required lay-verify='required' autocomplete='off' class='layui-input'>"
				 +"</div> </div>"
				 +"<button type='button' class='layui-btn layui-btn-primary' style='margin-left: 110px;margin-bottom: 10px;' onclick='deletePassenger()'>删除</button>"
			     +"</div>";
			$("#passengerInfo").append(html);
			form.render('select')
	});
	

function deletePassenger(){	
	var dom=event.srcElement;
	$(dom).parent().remove();
	
}
$.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};