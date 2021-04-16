package cn.ssq.ticket.system.entity.woNiu;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Administrator on 2019/4/22 17:02
 * @param:
 * @return:
 * @Description:订单递交参数实体类
 **/
public class OrderParam implements Serializable{
    private static final long serialVersionUID = 5659194883432017105L;
    private String productTag;
    private boolean flyFund;
   // private boolean flyFund1;
    private boolean isUseBonus;
    private int printPrice;
    private int yPrice;
    private String contact;
    private String contactPreNum;
    private String contactMob;
    private int invoiceType;
    private String sjr;
    private String address="";//广东省东莞市东城区东城街道达鑫创富中心6楼615
    private String xcd;
    private String xcdMethod;
    private String bxInvoice;
    private JSONObject flightInfo;
    private String bookingTag;
    private String qt;
    private String source;
    private String clientSite;
    private String flightNum;
    private String flightType;
    private int passengerCount;
    private List<JSONObject> passengers;

    public String getProductTag() {
        return productTag;
    }

    public void setProductTag(String productTag) {
        this.productTag = productTag;
    }

    public boolean isFlyFund() {
        return flyFund;
    }

    public void setFlyFund(boolean flyFund) {
        this.flyFund = flyFund;
    }



    public boolean isUseBonus() {
        return isUseBonus;
    }

    public void setIsUseBonus(boolean useBonus) {
        isUseBonus = useBonus;
    }

    public int getPrintPrice() {
        return printPrice;
    }

    public void setPrintPrice(int printPrice) {
        this.printPrice = printPrice;
    }

    public int getyPrice() {
        return yPrice;
    }

    public void setyPrice(int yPrice) {
        this.yPrice = yPrice;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactPreNum() {
        return contactPreNum;
    }

    public void setContactPreNum(String contactPreNum) {
        this.contactPreNum = contactPreNum;
    }

    public String getContactMob() {
        return contactMob;
    }

    public void setContactMob(String contactMob) {
        this.contactMob = contactMob;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getSjr() {
        return sjr;
    }

    public void setSjr(String sjr) {
        this.sjr = sjr;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getXcd() {
        return xcd;
    }

    public void setXcd(String xcd) {
        this.xcd = xcd;
    }

    public String getXcdMethod() {
        return xcdMethod;
    }

    public void setXcdMethod(String xcdMethod) {
        this.xcdMethod = xcdMethod;
    }

    public String getBxInvoice() {
        return bxInvoice;
    }

    public void setBxInvoice(String bxInvoice) {
        this.bxInvoice = bxInvoice;
    }

    public JSONObject getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(JSONObject flightInfo) {
        this.flightInfo = flightInfo;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public List<JSONObject> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<JSONObject> passengers) {
        this.passengers = passengers;
    }

    public String getBookingTag() {
        return bookingTag;
    }

    public void setBookingTag(String bookingTag) {
        this.bookingTag = bookingTag;
    }

    public String getQt() {
        return qt;
    }

    public void setQt(String qt) {
        this.qt = qt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClientSite() {
        return clientSite;
    }

    public void setClientSite(String clientSite) {
        this.clientSite = clientSite;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    @Override
    public String toString() {
        return "OrderParam{" +
                "productTag='" + productTag + '\'' +
                ", flyFund=" + flyFund +
                ", isUseBonus=" + isUseBonus +
                ", printPrice=" + printPrice +
                ", yPrice=" + yPrice +
                ", contact='" + contact + '\'' +
                ", contactPreNum='" + contactPreNum + '\'' +
                ", contactMob='" + contactMob + '\'' +
                ", invoiceType=" + invoiceType +
                ", sjr='" + sjr + '\'' +
                ", address='" + address + '\'' +
                ", xcd='" + xcd + '\'' +
                ", xcdMethod='" + xcdMethod + '\'' +
                ", bxInvoice='" + bxInvoice + '\'' +
                ", flightInfo=" + flightInfo +
                ", bookingTag='" + bookingTag + '\'' +
                ", qt='" + qt + '\'' +
                ", source='" + source + '\'' +
                ", clientSite='" + clientSite + '\'' +
                ", flightNum='" + flightNum + '\'' +
                ", flightType='" + flightType + '\'' +
                ", passengerCount=" + passengerCount +
                ", passengers=" + passengers +
                '}';
    }
}
