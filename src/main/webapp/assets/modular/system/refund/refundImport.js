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
					title: '退票成功数',
					align: 'center',
					width: 130
				},
				{
					field: 'fail',
					sort: false,
					title: '退票失败数',
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
						if(d.type == '2') {
							return "待退订单导入";
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
						return "<a class='layui-btn layui-btn-primary layui-btn-xs' href='" + Feng.ctxPath + "/refund/downloadRefundRecord?importId=" + d.importId + "'>下载导入结果</a>";
					}
				}
			]
		];
	};

	// 渲染表格
	var tableResult = table.render({
		elem: '#' + Import.tableId,
		url: Feng.ctxPath + '/import/getImportHistory?type=2',
		page: true,
		height: "full-158",
		cellMinWidth: 100,
		even: true,
		cols: Import.initColumn()
	});
	var closeIndex = null;

	upload.render({
		elem: '#uploadOrderReport',
		url: Feng.ctxPath + '/refund/uploadRefundReport',
		accept: 'file', //允许上传的文件类型
		size: 10240, //最大允许上传的文件大小,10M
		exts: 'xls|xlsx|xlsm',
		acceptMime: 'file/xls,file/xlsx,file/xlsm',
		before: function(obj) {
			closeIndex = layer.msg('鹏朋退票中...', {
				icon: 16,
				time: false,
				shade: 0.8
			});
		},
		done: function(res, index, upload) { //上传后的回调
			if(res.code != 0) {
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
	
	
	upload.render({
		elem: '#uploadOrderReportB2B',
		url: Feng.ctxPath + '/refund/uploadRefundReportB2B',
		accept: 'file', //允许上传的文件类型
		size: 10240, //最大允许上传的文件大小,10M
		exts: 'xls|xlsx|xlsm',
		acceptMime: 'file/xls,file/xlsx,file/xlsm',
		before: function(obj) {
			closeIndex = layer.msg('B2B退票中...', {
				icon: 16,
				time: false,
				shade: 0.8
			});
		},
		done: function(res, index, upload) { //上传后的回调
			if(res.code != 0) {
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
	
	
	
});