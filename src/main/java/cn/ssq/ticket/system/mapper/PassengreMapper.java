package cn.ssq.ticket.system.mapper;

import java.util.List;
import cn.ssq.ticket.system.entity.Passenger;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface PassengreMapper extends BaseMapper<Passenger>{

	
	List<Passenger> getByOrderNo(String orderNo);
	
	/*void updateIsImRet(@Param("cStatus")String cStatus,@Param("aStatus")String aStatus,@Param("passengerId")Long passengerId);*/
	
	List<Passenger> getByRetNo(String retNo);
	
	//void updateTicketNo(@Param("ticketNo")String ticketNo,@Param("certNo")String certNo,@Param("date")Date date);
	List<String> getRemark();
}
