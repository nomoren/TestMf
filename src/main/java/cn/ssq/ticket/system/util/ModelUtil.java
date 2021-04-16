package cn.ssq.ticket.system.util;

import cn.ssq.ticket.system.entity.woNiu.PriceInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * @Author: Administrator on 2019/4/19 15:09
 * @param:
 * @return:
 * @Description:处理实体类对象数据
 **/
public class ModelUtil {


    //价格数据写入
    public static PriceInfo getPriceObj(JSONObject rltObj, String priceVal, String startTime, String btime, String carrier) {
        JSONObject itemObj = JSON.parseObject(priceVal);
        PriceInfo priceInfo = new PriceInfo();
        priceInfo.setDate(startTime);
        priceInfo.setBtime(btime);
        priceInfo.setCarrier(carrier);
        //公共数据
        priceInfo.setStatus(getValByKey(rltObj, "status"));
        priceInfo.setMsg(getValByKey(rltObj, "msg"));
        priceInfo.setDepCode(getValByKey(rltObj, "depCode"));
        priceInfo.setDepAirport(getValByKey(rltObj, "depAirport"));
        priceInfo.setDepTerminal(getValByKey(rltObj, "depTerminal"));
        priceInfo.setArrCode(getValByKey(rltObj, "arrCode"));
        priceInfo.setArrAirport(getValByKey(rltObj, "arrAirport"));
        priceInfo.setArrTerminal(getValByKey(rltObj, "arrTerminal"));
        priceInfo.setEtime(getValByKey(rltObj, "etime"));
        priceInfo.setCom(getValByKey(rltObj, "com"));
        priceInfo.setCode(getValByKey(rltObj, "code"));
        priceInfo.setMeal(Boolean.parseBoolean(getValByKey(rltObj, "meal")));
        priceInfo.setZhiji(Boolean.parseBoolean(getValByKey(rltObj, "zhiji")));
        priceInfo.setCorrect(getValByKey(rltObj, "correct"));
        priceInfo.setStop(Boolean.parseBoolean(getValByKey(rltObj, "stop")));
        priceInfo.setStopsNum(Integer.parseInt(getValByKey(rltObj, "stopsNum")));
        priceInfo.setStopCityCode(getValByKey(rltObj, "stopCityCode"));
        priceInfo.setStopCityName(getValByKey(rltObj, "stopCityName"));
        priceInfo.setStopAirportCode(getValByKey(rltObj, "stopAirportCode"));
        priceInfo.setStopAirportName(getValByKey(rltObj, "stopAirportName"));
        priceInfo.setStopAirportFullName(getValByKey(rltObj, "stopAirportFullName"));
        priceInfo.setCodeShare(Boolean.parseBoolean(getValByKey(rltObj, "codeShare")));
        priceInfo.setActCode(getValByKey(rltObj, "actCode"));
        priceInfo.setArf(Integer.parseInt(getValByKey(rltObj, "arf")));
        priceInfo.setTof(Integer.parseInt(getValByKey(rltObj, "tof")));
        priceInfo.setDistance(Integer.parseInt(getValByKey(rltObj, "distance")));
        priceInfo.setFlightType(getValByKey(rltObj, "flightType"));
        //价格数据
        priceInfo.setWrapperId(getValByKey(itemObj, "wrapperId"));
        priceInfo.setBusinessExt(getValByKey(itemObj, "businessExt"));
        priceInfo.setPrtag(getValByKey(itemObj, "prtag"));
        priceInfo.setGroupId(getValByKey(itemObj, "groupId"));
        priceInfo.setDiscount(getValByKey(itemObj, "discount"));
        priceInfo.setCabin(getValByKey(itemObj, "cabin"));
        priceInfo.setPtype(getValByKey(itemObj, "ptype"));
        priceInfo.setVppr(Integer.parseInt(getValByKey(itemObj, "vppr")));
        priceInfo.setCabinType(Integer.parseInt(getValByKey(itemObj, "cabinType")));
        priceInfo.setBusinessExtMap(getValByKey(itemObj, "businessExtMap"));
        priceInfo.setLuggage(getValByKey(itemObj, "luggage"));
        priceInfo.setPrice(Integer.parseInt(getValByKey(itemObj, "price")));
        priceInfo.setpType(getValByKey(itemObj, "pType"));
        priceInfo.setFuzzy(getValByKey(itemObj, "fuzzy"));
        priceInfo.setBasePrice(Integer.parseInt(getValByKey(itemObj, "basePrice")));
        priceInfo.setAfee(getValByKey(itemObj, "afee"));
        priceInfo.setCardType(Integer.parseInt(getValByKey(itemObj, "cardType")));
        priceInfo.setCabinCount(getValByKey(itemObj, "cabinCount"));
        priceInfo.setIt(getValByKey(itemObj, "it"));
        priceInfo.setBprtag(getValByKey(itemObj, "bprtag"));
        priceInfo.setPolicyId(getValByKey(itemObj, "policyId"));
        priceInfo.setBarePrice(Integer.parseInt(getValByKey(itemObj, "barePrice")));
        priceInfo.setDomain(getValByKey(itemObj, "domain"));
        priceInfo.setPolicyType(getValByKey(itemObj, "policyType"));
        priceInfo.setShareShowAct(getValByKey(itemObj, "shareShowAct"));
        return priceInfo;
    }

    //根据key拿到JSON的value
    private static String getValByKey(JSONObject obj, String key) {
        try {
            return obj.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
