layui.use(['layer', 'element'], function() {
	//var $ = layui.jquery;
	var layer = layui.layer;
	var element = layui.element;


	// 渲染活动情况预测

	// 动态改变图表1数据
	/*   setInterval(function () {
            for (var i = 0; i < mData.length; i++) {
                mData[i] += (Math.random() * 50 - 25);
                if (mData[i] < 0) {
                    mData[i] = 0;
                }
            }
            myCharts.setOption({
                series: [{
                    data: mData
                }]
            });
        }, 1000);
*/

	// 渲染销售额图表
	var myCharts3 = echarts.init(document.getElementById('xse'), myEchartsTheme);
	var option3 = {
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			data: ['用户总数', '商品总数', '订单总数', '退货总数', '出货总量']
		},
		grid: {
			left: '3%',
			right: '4%',
			bottom: '3%',
			containLabel: true
		},
		toolbox: {
			feature: {
				saveAsImage: {}
			}
		},
		xAxis: {
			type: 'category',
			boundaryGap: false,
			data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
		},
		yAxis: {
			type: 'value'
		},
		series: [{
				name: '用户总数',
				type: 'line',
				stack: '总量',
				data: [200, 300, 400, 500, 300, 230, 210]
			},
			{
				name: '商品总数',
				type: 'line',
				stack: '总量',
				data: [220, 182, 191, 234, 290, 330, 310]
			},
			{
				name: '订单总数',
				type: 'line',
				stack: '总量',
				data: [150, 300, 520, 400, 190, 330, 410]
			},
			{
				name: '退货总数',
				type: 'line',
				stack: '总量',
				data: [320, 332, 301, 334, 390, 330, 320]
			},
			{
				name: '出货总量',
				type: 'line',
				stack: '总量',
				data: [700, 932, 901, 934, 1290, 1330, 1320]
			}
		]
	};
	myCharts3.setOption(option3);

	// 渲染访问量图表
	var myCharts4 = echarts.init(document.getElementById('fwl'), myEchartsTheme);
	var option4 = {
		title: {
			text: '访问量趋势',
			textStyle: {
				color: '#000',
				fontSize: 14
			}
		},
		tooltip: {},
		grid: {
			left: '0',
			right: '0',
			bottom: '0',
			containLabel: true
		},
		xAxis: {
			data: ['1月', '2月', '3月', '4月', '6月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
		},
		yAxis: {},
		series: [{
			type: 'bar',
			data: [558, 856, 880, 1325, 982, 856, 655, 546, 988, 985, 568, 302],
			barMaxWidth: 45
		}]
	};
	myCharts4.setOption(option4);

	// 切换选项卡重新渲染
	element.on('tab(tabZZT)', function(data) {
		if(data.index == 0) {
			myCharts3.resize();
		} else {
			myCharts4.resize();
		}
	});

	// 窗口大小改变事件
	window.onresize = function() {
		//myCharts.resize();
		myCharts3.resize();
		myCharts4.resize();
	};

});
/**
 * 获取指定天后的日期
 * @param {Object} day
 */
/*function getDay(day) {
	var today = new Date();	　　
	var targetday_milliseconds = today.getTime() + 1000 * 60 * 60 * 24 * day;	　　
	today.setTime(targetday_milliseconds);	　　
	var tYear = today.getFullYear();	　　
	var tMonth = today.getMonth();	　　
	var tDate = today.getDate();	　　
	tMonth = doHandleMonth(tMonth + 1);	　　
	tDate = doHandleMonth(tDate);
	return tYear + "-" + tMonth + "-" + tDate;
}
function doHandleMonth(month) {	　　
	var m = month;　　
	if(month.toString().length == 1) {　　　　
		m = "0" + month;		　　
	}　　
	return m;
}*/