package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.Dict;
import cn.ssq.ticket.system.entity.DictType;
import cn.ssq.ticket.system.model.params.DictParam;
import cn.ssq.ticket.system.model.result.DictResult;
import cn.ssq.ticket.system.service.DictService;
import cn.ssq.ticket.system.service.DictTypeService;
import cn.stylefeng.guns.core.common.node.ZTreeNode;
import cn.stylefeng.guns.core.common.page.LayuiPageInfo;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.kernel.model.exception.RequestEmptyException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * 基础字典控制器
 */
@Controller
@RequestMapping("/dict")
public class DictController extends BaseController {

    private String PREFIX = "/modular/system/dict";

    @Autowired
    private DictService dictService;

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 跳转到主页面
     */
    @RequestMapping("")
    public String index(@RequestParam("dictTypeId") Long dictTypeId, Model model) {
        model.addAttribute("dictTypeId", dictTypeId);

        //获取type的名称
        DictType dictType = dictTypeService.getById(dictTypeId);
        if (dictType == null) {
            throw new RequestEmptyException();
        }
        model.addAttribute("dictTypeName", dictType.getName());

        return PREFIX + "/dict.html";
    }

    /**
     * 新增页面
     */
    @RequestMapping("/add")
    public String add(@RequestParam("dictTypeId") Long dictTypeId, Model model) {
        model.addAttribute("dictTypeId", dictTypeId);

        //获取type的名称
        DictType dictType = dictTypeService.getById(dictTypeId);
        if (dictType == null) {
            throw new RequestEmptyException();
        }

        model.addAttribute("dictTypeName", dictType.getName());
        return PREFIX + "/dict_add.html";
    }

    /**
     * 编辑页面
     */
    @RequestMapping("/edit")
    public String edit(@RequestParam("dictId") Long dictId, Model model) {

        //获取type的id
        Dict dict = dictService.getById(dictId);
        if (dict == null) {
            throw new RequestEmptyException();
        }

        //获取type的名称
        DictType dictType = dictTypeService.getById(dict.getDictTypeId());
        if (dictType == null) {
            throw new RequestEmptyException();
        }

        model.addAttribute("dictTypeId", dict.getDictTypeId());
        model.addAttribute("dictTypeName", dictType.getName());

        return PREFIX + "/dict_edit.html";
    }

    /**
     * 新增接口
     */
    @RequestMapping("/addItem")
    @ResponseBody
    public ResponseData addItem(DictParam dictParam) {
        this.dictService.add(dictParam);
        return ResponseData.success();
    }

    /**
     * 编辑接口
     */
    @RequestMapping("/editItem")
    @ResponseBody
    public ResponseData editItem(DictParam dictParam) {
        this.dictService.update(dictParam);
        return ResponseData.success();
    }

    /**
     * 删除接口
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseData delete(DictParam dictParam) {
        this.dictService.delete(dictParam);
        return ResponseData.success();
    }

    /**
     * 查看详情接口
     */
    @RequestMapping("/detail")
    @ResponseBody
    public ResponseData detail(DictParam dictParam) {
        DictResult dictResult = this.dictService.dictDetail(dictParam.getDictId());
        return ResponseData.success(dictResult);
    }

    /**
     * 查询列表
     *
     */
    @ResponseBody
    @RequestMapping("/list")
    public LayuiPageInfo list(DictParam dictParam) {
        return this.dictService.findPageBySpec(dictParam);
    }

    /**
     * 获取某个类型下字典树的列表，ztree格式
     */
    @RequestMapping(value = "/ztree")
    @ResponseBody
    public List<ZTreeNode> ztree(@RequestParam("dictTypeId") Long dictTypeId, @RequestParam(value = "dictId", required = false) Long dictId) {
        return this.dictService.dictTreeList(dictTypeId, dictId);
    }

}


