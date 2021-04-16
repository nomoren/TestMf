
layui.use(['layer', 'element', 'upload', 'table'], function() {
	var upload = layui.upload;
	var layer = layui.layer;
	var element = layui.element;
	var table = layui.table;

	var Import = {
		tableId: "importTable", //表格id
		condition: {
			OrderName: ""
		}
	};

	/**
	 * 初始化表格的列
	 */
	 Import.initColumn = function() {
		return [
			[{
					type: 'checkbox'
				},
				{
					field: 'name',
					sort: false,
					title: '导入人',
					align: 'center',
					width: 170
				},
				{
					field: 'count',
					sort: false,
					title: '处理总数',
					align: 'center',
					width: 130
				},
				{
					field: 'success',
					sort: false,
					title: '对账成功数',
					align: 'center',
					width: 130
				},
				{
					field: 'fail',
					sort: false,
					title: '对账失败数',
					align: 'center',
					width: 130
				},
				{
					field: 'type',
					sort: false,
					title: '类型',
					align: 'center',
					width: 140,
					templet: function(d) {
						if(d.type == '10') {
							return "支付宝账单对账";
						}else{
							return "易宝账单对账";
						}
					}
				},
				{
					field: 'remark',
					sort: false,
					title: '备注',
					align: 'center',
					width: 150
				},
				{
					field: 'importDate',
					sort: false,
					title: '导入时间',
					align: 'center'
				},
				{
					align: 'center',
					sort: '#tableBar',
					title: '操作',
					minWidth: 200,
					templet: function(d) {
						if(d.fail > 0) {
							return 	"<a class='layui-btn layui-btn-primary layui-btn-xs' href='"+Feng.ctxPath+"/import/downloadAliPayCheckingRecord?importId="+d.importId+"&type="+d.type+"'>下载对账结果</a>";
						}else{
							return "";
						}
					}
				}
			]
		];
	}; 

	// 渲染表格
	var tableResult = table.render({
		elem: '#' + Import.tableId,
		url: Feng.ctxPath + '/import/getImportHistory?type=10',
		page: true,
		height: "full-158",
		cellMinWidth: 100,
		even:true,
		cols: Import.initColumn()
	});
	var closeIndex=null;
	upload.render({
		elem: '#uploadAliPayChecking',
		url: Feng.ctxPath + '/import/uploadAliPayChecking',
		accept: 'file', //允许上传的文件类型
		size: 5120, //最大允许上传的文件大小,5M
		exts: 'xls|xlsx|csv',
		acceptMime: 'file/xls,file/xlsx,file/csv',
		before: function(obj) {
			closeIndex = layer.msg('文件已导入，正在处理...', {
					icon: 16,  
					time: false,
					shade: 0.8
			});
		},
		done: function(res, index, upload) { //上传后的回调
			if(res.code!=0){
				layer.msg(res.msg);	
			}
			table.reload('importTable');
			layer.close(closeIndex);
		},
		error: function(index, upload) {
			table.reload('importTable');
			layer.close(closeIndex);
			layer.msg("上传失败！");
		}
	})
	var index2=null;
	upload.render({
		elem: '#uploadYiBaoOrderChecking',
		url: Feng.ctxPath + '/import/uploadYiBaoOrderChecking',
		accept: 'file', //允许上传的文件类型
		size: 5120, //最大允许上传的文件大小,5M
		exts: 'xls|xlsx',
		acceptMime: 'file/xls,file/xlsx',
		before: function(obj) {
				index2 = layer.msg('文件已导入，正在处理...', {
					icon: 16,  
					time: false,
					shade: 0.8
			});
		},
		done: function(res, index, upload) { //上传后的回调
			if(res.code!=0){
				layer.msg(res.msg);	
			}
			table.reload('importTable');
			layer.close(index2);
		},
		error: function(index, upload) {
			table.reload('importTable');
			layer.close(index2);
			layer.msg("上传失败！");
		}
	})
	var index3=null;
	upload.render({
		elem: '#uploadYiBaoRefundChecking',
		url: Feng.ctxPath + '/import/uploadYiBaoRefundChecking',
		accept: 'file', //允许上传的文件类型
		size: 5120, //最大允许上传的文件大小,5M
		exts: 'xls|xlsx',
		acceptMime: 'file/xls,file/xlsx',
		before: function(obj) {
				index3 = layer.msg('文件已导入，正在处理...', {
					icon: 16,  
					time: false,
					shade: 0.8
			});
		},
		done: function(res, index, upload) { //上传后的回调
			if(res.code!=0){
				layer.msg(res.msg);	
			}
			table.reload('importTable');
			layer.close(index3);
		},
		error: function(index, upload) {
			table.reload('importTable');
			layer.close(index3);
			layer.msg("上传失败！");
		}
	})
	
});