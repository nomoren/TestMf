package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

/**
 * @Author: Administrator on 2019/4/22 16:24
 * @param:
 * @return:
 * @Description:Booking参数实体类
 **/
public class Booking implements Serializable{
    private static final long serialVersionUID = -3534684604280524872L;
    private String ticketPrice;
    private String barePrice;
    private String price;
    private String basePrice;
    private String businessExt;
    private String tag;
    private String carrier;
    private String flightNum;
    private String cabin;
    private String from;
    private String to;
    private String policyType;
    private String wrapperId;
    private String startTime;
    private String domain;
    private String policyId;
    private String dptTime;
    private String flightType;
    private String userName;
    private String client;

    public String getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getBarePrice() {
        return barePrice;
    }

    public void setBarePrice(String barePrice) {
        this.barePrice = barePrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public String getBusinessExt() {
        return businessExt;
    }

    public void setBusinessExt(String businessExt) {
        this.businessExt = businessExt;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public void setWrapperId(String wrapperId) {
        this.wrapperId = wrapperId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getDptTime() {
        return dptTime;
    }

    public void setDptTime(String dptTime) {
        this.dptTime = dptTime;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "OrderSearch{" +
                "ticketPrice='" + ticketPrice + '\'' +
                ", barePrice='" + barePrice + '\'' +
                ", price='" + price + '\'' +
                ", basePrice='" + basePrice + '\'' +
                ", businessExt='" + businessExt + '\'' +
                ", tag='" + tag + '\'' +
                ", carrier='" + carrier + '\'' +
                ", flightNum='" + flightNum + '\'' +
                ", cabin='" + cabin + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", policyType='" + policyType + '\'' +
                ", wrapperId='" + wrapperId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", domain='" + domain + '\'' +
                ", policyId='" + policyId + '\'' +
                ", dptTime='" + dptTime + '\'' +
                ", flightType='" + flightType + '\'' +
                ", userName='" + userName + '\'' +
                ", client='" + client + '\'' +
                '}';
    }
}
