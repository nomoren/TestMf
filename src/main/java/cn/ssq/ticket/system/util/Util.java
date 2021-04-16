package cn.ssq.ticket.system.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Util {

	public static List<String> orderSorceList=new ArrayList<String>();
	static{
		orderSorceList.add(InterfaceConstant.ORDER_SOURCE_CTRIP);
		orderSorceList.add(InterfaceConstant.ORDER_SOURCE_TN);
		orderSorceList.add(InterfaceConstant.ORDER_SOURCE_KX);
		orderSorceList.add(InterfaceConstant.ORDER_SOURCE_QNR);
		orderSorceList.add(InterfaceConstant.ORDER_SOURCE_TN);
	}
	public static String[] getToday(){
		String[] str=new String[2];
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c=Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);//00:00:00
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		str[0]=sdf.format(c.getTime());
		c.set(Calendar.HOUR_OF_DAY, 23);//00:00:00
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		str[1]=sdf.format(c.getTime());
		return str;
	}

	/**
	 * 判断两个list是否相同(元素要重写equals)
	 * @param l0
	 * @param l1
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean isListEqual(  List l0, List l1){
		if (l0 == l1)
			return true;
		if (l0 == null && l1 == null)
			return true;
		if (l0 == null || l1 == null)
			return false;
		if (l0.size() != l1.size())
			return false;
		if (l0.containsAll(l1) && l1.containsAll(l0)){
			return true;
		}
		return false;
	}

    public static boolean belongCalendar(String nowTime, String beginTime,String endTime) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar date = Calendar.getInstance();
            date.setTime(sdf.parse(nowTime+" 00:00:01"));

            Calendar begin = Calendar.getInstance();
            begin.setTime(sdf.parse(beginTime.split(" ")[0]+" 00:00:00"));

            Calendar end = Calendar.getInstance();
            end.setTime(sdf.parse(endTime.split(" ")[0]+" 23:59:59"));

            if (date.after(begin) && date.before(end)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }



}
