package cn.ssq.ticket.system.mapper;

import cn.ssq.ticket.system.entity.Dict;
import cn.stylefeng.guns.core.common.node.ZTreeNode;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 基础字典 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-13
 */
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 获取ztree的节点列表
     */
    List<ZTreeNode> dictTree(@Param("dictTypeId") Long dictTypeId);

    /**
     * where parentIds like ''
     */
    List<Dict> likeParentIds(@Param("dictId") Long dictId);
    
    String getDictCode(@Param("type")String type,@Param("name")String name);
    
    String getDictName(@Param("type")String type,@Param("code")String code);
    
    List<String> getPrincipalAccount(@Param("description")String description,@Param("code")String code);
    
    List<String> getDictNameList(String type);
}
