package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

/**
 * @Author: Administrator on 2019/4/22 16:13
 * @param:
 * @return:
 * @Description:航班搜索条件实体类
 **/
public class FlightSearch implements Serializable{
    private static final long serialVersionUID = 2836843632035973180L;
    private String arr;
    private String dpt;
    private String date;
    private String ex_track;

    public String getArr() {
        return arr;
    }

    public void setArr(String arr) {
        this.arr = arr;
    }

    public String getDpt() {
        return dpt;
    }

    public void setDpt(String dpt) {
        this.dpt = dpt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEx_track() {
        return ex_track;
    }

    public void setEx_track(String ex_track) {
        this.ex_track = ex_track;
    }

    @Override
    public String toString() {
        return "flightSearch{" +
                "arr='" + arr + '\'' +
                ", dpt='" + dpt + '\'' +
                ", date='" + date + '\'' +
                ", ex_track='" + ex_track + '\'' +
                '}';
    }
}
