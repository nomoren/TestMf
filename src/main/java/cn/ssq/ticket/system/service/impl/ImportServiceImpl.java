package cn.ssq.ticket.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.ssq.ticket.system.entity.Import;
import cn.ssq.ticket.system.mapper.ImportMapper;
import cn.ssq.ticket.system.service.ImportServcie;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


@Service
public class ImportServiceImpl implements ImportServcie{
	@Autowired
	private ImportMapper importMapper;

	@Override
	public void insert(Import im) {
		importMapper.insert(im);
	}

	@Override
	public Map<String, Object> getList(String type) {
		//String name=ShiroKit.getSubject().getPrincipal().toString();
		QueryWrapper<Import> query=new QueryWrapper<Import>();
		//query.eq("name", name);
		if("10".equals(type)) {
			List<String> list=new ArrayList<String>();
			list.add("10");
			list.add("11");
			list.add("12");
			query.in("type", list);
		}else {			
			query.eq("type", type);
		}
		query.orderByDesc("import_date");
		Integer count=importMapper.selectCount(query);
		Map<String, Object> map=new HashMap<String, Object>();
		List<Import> list = importMapper.selectList(query);
		map.put("count", count);
		map.put("data", list);
		return map;
	}

}
