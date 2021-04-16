layui.use(['layer', 'element', 'laydate', 'form'], function() {
	var layer = layui.layer;
	var element = layui.element;
	var form = layui.form;
	var laydate = layui.laydate;

	laydate.render({
		elem: '#endDate',
		type: 'datetime',
		value: new Date()
	});
	laydate.render({
		elem: '#startDate',
		type: 'datetime'
	});
	//下载出票明细
	form.on('submit(exportPrintTicket)', function(data) {		
		var data = $('#printTicket').serialize()
		var tokenValue=new Date().Format("yyyyMMddHHmmss");
		var downToken="down"+tokenValue;
		var url=Feng.ctxPath+'/export/exportChangeReport?'+data+"&downToken="+downToken+"&tokenValue="+tokenValue
		var index = layer.msg('正在下载数据，等一下', {
					icon: 16,  
					time: false,
					shade: 0.8
				});
		window.location.href=url;
		var timeID=setInterval(function(){
			var str= getCookie(downToken);
			if(tokenValue==str){
				layer.close(index);
				clearInterval(timeID);
			}
		},600);
		return false;
	});
	
});