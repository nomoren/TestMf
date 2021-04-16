package cn.ssq.ticket.system.entity.woNiu;

/**
 * @Author: Administrator on 2019/4/19 14:14
 * @param:
 * @return:
 * @Description:卖方票价信息
 **/
public class PriceInfo {
    //公共信息部分
    private String status;
    private String msg;
    private String depCode;
    private String depAirport;
    private String depTerminal;
    private String arrCode;
    private String arrAirport;
    private String arrTerminal;
    private String etime;
    private String com;
    private String code;
    private boolean meal;
    private boolean zhiji;
    private String correct;
    private boolean stop;
    private int stopsNum;
    private String stopCityCode;
    private String stopCityName;
    private String stopAirportCode;
    private String stopAirportName;
    private String stopAirportFullName;
    private boolean codeShare;
    private String actCode;
    private int arf;
    private int tof;
    private int distance;
    private String flightType;
    //价位信息部分
    private String carrier;
    private String btime;
    private String date;
    private String wrapperId;
    private String businessExt;
    private String prtag;
    private String groupId;
    private String discount;
    private String cabin;
    private String ptype;
    private int vppr;
    private int cabinType;
    private Object businessExtMap;
    private String luggage;
    private int price;
    private String pType;
    private String fuzzy;
    private int basePrice;
    private String afee;
    private int cardType;
    private String cabinCount;
    private String it;
    private String bprtag;
    private String policyId;
    private int barePrice;
    private String domain;
    private String policyType;
    private String shareShowAct;
    private int sellPrice;


    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public void setWrapperId(String wrapperId) {
        this.wrapperId = wrapperId;
    }

    public String getBusinessExt() {
        return businessExt;
    }

    public void setBusinessExt(String businessExt) {
        this.businessExt = businessExt;
    }

    public String getPrtag() {
        return prtag;
    }

    public void setPrtag(String prtag) {
        this.prtag = prtag;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public int getVppr() {
        return vppr;
    }

    public void setVppr(int vppr) {
        this.vppr = vppr;
    }

    public int getCabinType() {
        return cabinType;
    }

    public void setCabinType(int cabinType) {
        this.cabinType = cabinType;
    }

    public Object getBusinessExtMap() {
        return businessExtMap;
    }

    public void setBusinessExtMap(Object businessExtMap) {
        this.businessExtMap = businessExtMap;
    }

    public String getLuggage() {
        return luggage;
    }

    public void setLuggage(String luggage) {
        this.luggage = luggage;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public String getFuzzy() {
        return fuzzy;
    }

    public void setFuzzy(String fuzzy) {
        this.fuzzy = fuzzy;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public String getAfee() {
        return afee;
    }

    public void setAfee(String afee) {
        this.afee = afee;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCabinCount() {
        return cabinCount;
    }

    public void setCabinCount(String cabinCount) {
        this.cabinCount = cabinCount;
    }

    public String getIt() {
        return it;
    }

    public void setIt(String it) {
        this.it = it;
    }

    public String getBprtag() {
        return bprtag;
    }

    public void setBprtag(String bprtag) {
        this.bprtag = bprtag;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public int getBarePrice() {
        return barePrice;
    }

    public void setBarePrice(int barePrice) {
        this.barePrice = barePrice;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getShareShowAct() {
        return shareShowAct;
    }

    public void setShareShowAct(String shareShowAct) {
        this.shareShowAct = shareShowAct;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBtime() {
        return btime;
    }

    public void setBtime(String btime) {
        this.btime = btime;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getDepAirport() {
        return depAirport;
    }

    public void setDepAirport(String depAirport) {
        this.depAirport = depAirport;
    }

    public String getDepTerminal() {
        return depTerminal;
    }

    public void setDepTerminal(String depTerminal) {
        this.depTerminal = depTerminal;
    }

    public String getArrCode() {
        return arrCode;
    }

    public void setArrCode(String arrCode) {
        this.arrCode = arrCode;
    }

    public String getArrAirport() {
        return arrAirport;
    }

    public void setArrAirport(String arrAirport) {
        this.arrAirport = arrAirport;
    }

    public String getArrTerminal() {
        return arrTerminal;
    }

    public void setArrTerminal(String arrTerminal) {
        this.arrTerminal = arrTerminal;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isMeal() {
        return meal;
    }

    public void setMeal(boolean meal) {
        this.meal = meal;
    }

    public boolean isZhiji() {
        return zhiji;
    }

    public void setZhiji(boolean zhiji) {
        this.zhiji = zhiji;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public int getStopsNum() {
        return stopsNum;
    }

    public void setStopsNum(int stopsNum) {
        this.stopsNum = stopsNum;
    }

    public String getStopCityCode() {
        return stopCityCode;
    }

    public void setStopCityCode(String stopCityCode) {
        this.stopCityCode = stopCityCode;
    }

    public String getStopCityName() {
        return stopCityName;
    }

    public void setStopCityName(String stopCityName) {
        this.stopCityName = stopCityName;
    }

    public String getStopAirportCode() {
        return stopAirportCode;
    }

    public void setStopAirportCode(String stopAirportCode) {
        this.stopAirportCode = stopAirportCode;
    }

    public String getStopAirportName() {
        return stopAirportName;
    }

    public void setStopAirportName(String stopAirportName) {
        this.stopAirportName = stopAirportName;
    }

    public String getStopAirportFullName() {
        return stopAirportFullName;
    }

    public void setStopAirportFullName(String stopAirportFullName) {
        this.stopAirportFullName = stopAirportFullName;
    }

    public boolean isCodeShare() {
        return codeShare;
    }

    public void setCodeShare(boolean codeShare) {
        this.codeShare = codeShare;
    }

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public int getArf() {
        return arf;
    }

    public void setArf(int arf) {
        this.arf = arf;
    }

    public int getTof() {
        return tof;
    }

    public void setTof(int tof) {
        this.tof = tof;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    @Override
    public String toString() {
        return "PriceInfo{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", depCode='" + depCode + '\'' +
                ", depAirport='" + depAirport + '\'' +
                ", depTerminal='" + depTerminal + '\'' +
                ", arrCode='" + arrCode + '\'' +
                ", arrAirport='" + arrAirport + '\'' +
                ", arrTerminal='" + arrTerminal + '\'' +
                ", etime='" + etime + '\'' +
                ", com='" + com + '\'' +
                ", code='" + code + '\'' +
                ", meal=" + meal +
                ", zhiji=" + zhiji +
                ", correct='" + correct + '\'' +
                ", stop=" + stop +
                ", stopsNum=" + stopsNum +
                ", stopCityCode='" + stopCityCode + '\'' +
                ", stopCityName='" + stopCityName + '\'' +
                ", stopAirportCode='" + stopAirportCode + '\'' +
                ", stopAirportName='" + stopAirportName + '\'' +
                ", stopAirportFullName='" + stopAirportFullName + '\'' +
                ", codeShare=" + codeShare +
                ", actCode='" + actCode + '\'' +
                ", arf=" + arf +
                ", tof=" + tof +
                ", distance=" + distance +
                ", flightType='" + flightType + '\'' +
                ", carrier='" + carrier + '\'' +
                ", btime='" + btime + '\'' +
                ", date='" + date + '\'' +
                ", wrapperId='" + wrapperId + '\'' +
                ", businessExt='" + businessExt + '\'' +
                ", prtag='" + prtag + '\'' +
                ", groupId='" + groupId + '\'' +
                ", discount='" + discount + '\'' +
                ", cabin='" + cabin + '\'' +
                ", ptype='" + ptype + '\'' +
                ", vppr=" + vppr +
                ", cabinType=" + cabinType +
                ", businessExtMap=" + businessExtMap +
                ", luggage='" + luggage + '\'' +
                ", price=" + price +
                ", pType='" + pType + '\'' +
                ", fuzzy='" + fuzzy + '\'' +
                ", basePrice=" + basePrice +
                ", afee='" + afee + '\'' +
                ", cardType=" + cardType +
                ", cabinCount='" + cabinCount + '\'' +
                ", it='" + it + '\'' +
                ", bprtag='" + bprtag + '\'' +
                ", policyId='" + policyId + '\'' +
                ", barePrice=" + barePrice +
                ", domain='" + domain + '\'' +
                ", policyType='" + policyType + '\'' +
                ", shareShowAct='" + shareShowAct + '\'' +
                ", sellPrice=" + sellPrice +
                '}';
    }
}
