layui.use(['layer', 'form', 'table', 'admin', 'ax', 'laydate', 'element'], function() {
	var $ = layui.$;
	var layer = layui.layer;
	var form = layui.form;
	var table = layui.table;
	var $ax = layui.ax;
	var admin = layui.admin;
	var laydate = layui.laydate;
	var element = layui.element;

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

	var PurchIndex = {
		condition: {
			RefundName: ""
		}
	};
	//监听搜索
	form.on('submit(purchAdd)', function(data) {
		var data = $('#purchSearchForm').serialize();
		var orderNo = $("#orderNo").val();
		if(orderNo != "") {
			var url = Feng.ctxPath + '/purch/toPurchAdd?' + data;
			$("#purchIframe").attr("src", url)
			return false;
		} else {
			var orderSource = $("input[type='radio']:checked").val();
			var startDate = $("#startDate").val();
			var endDate = $("#endDate").val();
			var date = startDate + ":" + endDate;
			var isChecked = $("#onlyMe").prop("checked");
			var remark=$("#remark").val();
			purchIframe.window.tableReload(orderSource, date, isChecked,remark);
			return false;
		}

	});

	//监听单选框
	form.on('radio(orderSource)', function(data) {
		PurchIndex.reloadtable(data.value);
	});

	PurchIndex.reloadtable = function(orderSource) {
		var startDate = $("#startDate").val();
		var endDate = $("#endDate").val();
		var date = startDate + ":" + endDate;
		var isChecked = $("#onlyMe").prop("checked");
		var remark=$("#remark").val();
		purchIframe.window.tableReload(orderSource, date, isChecked,remark);
	};
});