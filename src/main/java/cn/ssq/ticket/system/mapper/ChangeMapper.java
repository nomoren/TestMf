package cn.ssq.ticket.system.mapper;

import cn.ssq.ticket.system.entity.Change;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 改签mapper
 * 
 * @author Administrator
 */
public interface ChangeMapper extends BaseMapper<Change> {

	void deleteChange(String[] changeIds);

	void initializedPassengerChange(@Param("orderNo") String orderNo, @Param("name") String name);

	void addProcess(@Param("process") String process, @Param("changeNo") String changeNo);

	void deleteProcess(String changeNo);

	void updateStatus(@Param("state") String state, @Param("orderNo") String orderNo);

	List<String> isHavePcocess(String changeNo);

	// 根据改签单号修改
	int updateByChangeNo(Change change);

	void updateByticketNo(Change change);
}
