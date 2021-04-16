package cn.ssq.ticket.system.util;

import cn.ssq.ticket.system.entity.Dict;
import cn.ssq.ticket.system.service.DictService;
import cn.stylefeng.roses.core.util.SpringContextHolder;

/**
 * 字典工具类
 * @author Administrator
 *
 */
public class DictUtils {

	private static DictService dictService = SpringContextHolder.getBean(DictService.class);
	
	public static String getDictCode(String type,String name){
		return dictService.getDictCode(type, name);
	}
	
	
	public static String getDictName(String type,String code){
		return dictService.getDictName(type, code);
	}
	
	public static void updataDict(Dict param){
		dictService.updateById(param);
	}
	
	
	public static String getDictCodeNoCache(String type,String name){
		return dictService.getDictCodeNoCache(type, name);
	}
}
