package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.DictType;
import cn.ssq.ticket.system.model.params.DictTypeParam;
import cn.ssq.ticket.system.service.DictTypeService;
import cn.stylefeng.guns.core.common.page.LayuiPageInfo;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 字典类型表控制器
 */
@Controller
@RequestMapping("/dictType")
public class DictTypeController extends BaseController {

    private String PREFIX = "/modular/system/dictType";

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 跳转到主页面
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "/dictType.html";
    }

    /**
     * 新增页面
     */
    @RequestMapping("/add")
    public String add() {
        return PREFIX + "/dictType_add.html";
    }

    /**
     * 编辑页面
     */
    @RequestMapping("/edit")
    public String edit() {
        return PREFIX + "/dictType_edit.html";
    }

    /**
     * 新增接口
     */
    @RequestMapping("/addItem")
    @ResponseBody
    public ResponseData addItem(DictTypeParam dictTypeParam) {
        this.dictTypeService.add(dictTypeParam);
        return ResponseData.success();
    }

    /**
     * 编辑接口
     */
    @RequestMapping("/editItem")
    @ResponseBody
    public ResponseData editItem(DictTypeParam dictTypeParam) {
        this.dictTypeService.update(dictTypeParam);
        return ResponseData.success();
    }

    /**
     * 删除接口
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseData delete(DictTypeParam dictTypeParam) {
        this.dictTypeService.delete(dictTypeParam);
        return ResponseData.success();
    }

    /**
     * 查看详情接口
     */
    @RequestMapping("/detail")
    @ResponseBody
    public ResponseData detail(DictTypeParam dictTypeParam) {
        DictType detail = this.dictTypeService.getById(dictTypeParam.getDictTypeId());
        return ResponseData.success(detail);
    }

    /**
     * 查询列表
     */
    @ResponseBody
    @RequestMapping("/list")
    public LayuiPageInfo list(DictTypeParam dictTypeParam) {
        return this.dictTypeService.findPageBySpec(dictTypeParam);
    }

}


