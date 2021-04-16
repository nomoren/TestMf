// 用common.js必须加上Feng.addCtx("${ctxPath}");
Feng.info = function (info) {
    top.layer.msg(info, {icon: 6});
};
Feng.success = function (info) {
    top.layer.msg(info, {icon: 1});
};
Feng.error = function (info) {
    top.layer.msg(info, {icon: 2});
};
Feng.confirm = function (tip, ensure) {
    top.layer.confirm(tip, {
        skin: 'layui-layer-admin'
    }, function () {
        ensure();
    });
};
Feng.currentDate = function () {
    // 获取当前日期
    var date = new Date();

    // 获取当前月份
    var nowMonth = date.getMonth() + 1;

    // 获取当前是几号
    var strDate = date.getDate();

    // 添加分隔符“-”
    var seperator = "-";

    // 对月份进行处理，1-9月在前面添加一个“0”
    if (nowMonth >= 1 && nowMonth <= 9) {
        nowMonth = "0" + nowMonth;
    }

    // 对月份进行处理，1-9号在前面添加一个“0”
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }

    // 最后拼接字符串，得到一个格式为(yyyy-MM-dd)的日期
    return date.getFullYear() + seperator + nowMonth + seperator + strDate;
};
Feng.getUrlParam = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    } else {
        return null;
    }
};
Feng.infoDetail = function (title, info) {
    var display = "";
    if (typeof info === "string") {
        display = info;
    } else {
        if (info instanceof Array) {
            for (var x in info) {
                display = display + info[x] + "<br/>";
            }
        } else {
            display = info;
        }
    }
    top.layer.open({
        title: title,
        type: 1,
        skin: 'layui-layer-rim', //加上边框
        area: ['950px', '600px'], //宽高
        content: '<div style="padding: 20px;">' + display + '</div>'
    });
};
Feng.zTreeCheckedNodes = function (zTreeId) {
    var zTree = $.fn.zTree.getZTreeObj(zTreeId);
    var nodes = zTree.getCheckedNodes();
    var ids = "";
    for (var i = 0, l = nodes.length; i < l; i++) {
        ids += "," + nodes[i].id;
    }
    return ids.substring(1);
};
Feng.closeAllLoading = function () {
    layer.closeAll('loading');
};

Feng.randomNum=function (minNum, maxNum) {
        switch (arguments.length) {
            case 1:
                return parseInt(Math.random() * minNum + 1, 10);
                break;
            case 2:
                return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10);
                break;
            default:
                return 0;
                break;
        }
  },
Feng.newCrontab = function (href, menuName) {
        var dataUrl = href;
        var needCreateCrontab = true;

        // 轮询已有的标签，判断是否已经存在标签
        parent.$('.J_menuTab').each(function () {
            if ($(this).data('id') == dataUrl) {
                if (!$(this).hasClass('active')) {
                    $(this).addClass('active').siblings('.J_menuTab').removeClass('active');
                    parent.MyCrontab.scrollToTab(this);
                    parent.MyCrontab.$('.J_mainContent .J_iframe').each(function () {
                        if ($(this).data('id') == dataUrl) {
                            $(this).show().siblings('.J_iframe').hide();
                            $(this).attr('src', $(this).attr('src'));
                            return false;
                        }
                    });
                }
                needCreateCrontab = false;
                return false;
            }
        });

        //创建标签
        if (needCreateCrontab) {
            var tabLink = '<a href="javascript:;" class="active J_menuTab" data-id="' + dataUrl + '">' + menuName + ' <i class="fa fa-times-circle"></i></a>';
            parent.$('.J_menuTab').removeClass('active');
            parent.$('.J_menuTabs .page-tabs-content').append(tabLink);

            var iframeContent = '<iframe class="J_iframe" name="iframe' + Feng.randomNum(100,999) + '" width="100%" height="100%" src="' + dataUrl + '" frameborder="0" data-id="' + dataUrl + '" seamless></iframe>';
            parent.$('.J_mainContent').find('iframe.J_iframe').hide().parents('.J_mainContent').append(iframeContent);
            parent.MyCrontab.scrollToTab($('.J_menuTab.active'));
        }
   };
 
// 以下代码是配置layui扩展模块的目录，每个页面都需要引入
layui.config({
    base: Feng.ctxPath + '/assets/common/module/'
}).extend({
    formSelects: 'formSelects/formSelects-v4',
    treetable: 'treetable-lay/treetable',
    dropdown: 'dropdown/dropdown',
    notice: 'notice/notice',
    step: 'step-lay/step',
    dtree: 'dtree/dtree',
    citypicker: 'city-picker/city-picker',
    tableSelect: 'tableSelect/tableSelect',
    ax: 'ax/ax',
    ztree: 'ztree/ztree-object'
}).use(['admin'], function () {
    var $ = layui.$;
    var admin = layui.admin;

    // 单标签模式需要根据子页面的地址联动侧边栏的选中，用于适配浏览器前进后退按钮
    if (window != top && top.layui && top.layui.index && !top.layui.index.pageTabs) {
        top.layui.admin.activeNav(location.href.substring(Feng.ctxPath.length));
    }

    // 移除loading动画
    setTimeout(function () {
        admin.removeLoading();
    }, window == top ? 300 : 150);

    //注册session超时的操作
    $.ajaxSetup({
        contentType: "application/x-www-form-urlencoded;charset=utf-8",
        complete: function (XMLHttpRequest, textStatus) {

            //通过XMLHttpRequest取得响应头，sessionstatus，
            var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
            if (sessionstatus === "timeout") {

                //如果超时就处理 ，指定要跳转的页面
                window.location = Feng.ctxPath + "/global/sessionError";
            }
        }
    });

});
Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "H+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
function getCookie(name){
    var arr,reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg)) {
        return unescape(arr[2]);
    } else {
        return null;
    }
}