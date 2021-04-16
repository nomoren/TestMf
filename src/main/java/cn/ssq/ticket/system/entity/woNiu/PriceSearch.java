package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

/**
 * @Author: Administrator on 2019/4/22 16:14
 * @param:
 * @return:
 * @Description:价格搜索条件实体类
 **/
public class PriceSearch implements Serializable{
    private static final long serialVersionUID = -2777659000809781931L;
    private String arr;
    private String dpt;
    private String date;
    private String flightNum;
    private String ex_track;
    private String cabin;




    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

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

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public String getEx_track() {
        return ex_track;
    }

    public void setEx_track(String ex_track) {
        this.ex_track = ex_track;
    }


    @Override
    public String toString() {
        return "PriceSearch{" +
                "arr='" + arr + '\'' +
                ", dpt='" + dpt + '\'' +
                ", date='" + date + '\'' +
                ", flightNum='" + flightNum + '\'' +
                ", ex_track='" + ex_track + '\'' +
                ", cabin='" + cabin + '\'' +
                '}';
    }

}
