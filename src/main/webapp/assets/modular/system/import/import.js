
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
					title: '导入成功数',
					align: 'center',
					width: 130
				},
				{
					field: 'fail',
					sort: false,
					title: '导入失败数',
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
						if(d.type == '1') {
							return "采购单导入";
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
							return 	"<a class='layui-btn layui-btn-primary layui-btn-xs' href='"+Feng.ctxPath+"/import/downloadImportRecord?importId="+d.importId+"'>下载导入结果</a>";
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
		url: Feng.ctxPath + '/import/getImportHistory?type=1',
		page: true,
		height: "full-158",
		cellMinWidth: 100,
		even:true,
		cols: Import.initColumn()
	});
	var closeIndex=null;
	upload.render({
		elem: '#uploadOrderReport',
		url: Feng.ctxPath + '/import/uploadOrderReport',
		accept: 'file', //允许上传的文件类型
		size: 5120, //最大允许上传的文件大小,5M
		exts: 'xls|xlsx|',
		acceptMime: 'file/xls,file/xlsx',
		before: function(obj) {
			layer.msg("文件已导入，正在处理...", {
				time: 2000
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

//	// 工具条点击事件
//	table.on('tool(importTable)', function(obj) {
//		var data = obj.data;
//		var layEvent = obj.event;
//		if(layEvent === 'download') {
//			console.log(data.importId);
//			$.ajax({
//				"url": Feng.ctxPath + '/import/downloadImportRecord',
//				"type": "POST",
//				"data": {
//					importId: data.importId
//				},
//				"dataType": "JSON",
//				"success": function(json) {
//					if(json.code==-1){
//						layer.msg(json.msg);
//					}
//				}
//			});
//		}
//	});

});