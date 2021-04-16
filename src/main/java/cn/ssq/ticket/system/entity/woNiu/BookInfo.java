package cn.ssq.ticket.system.entity.woNiu;

import java.io.Serializable;

public class BookInfo implements Serializable{
    private static final long serialVersionUID = 6675182431918109452L;

    private String productTag;
    private int printPrice;
    private int ticketPrice;
    private int invoiceType;
    private String bookingTag;
    private String qt;
    private String clientId;
    private String flightNum;
    private int flightType;
    private int stops;
    private String cabin;
    private String childCabin;
    private String aduTag;
    private String dpt;
    private String arr;
    private String dptCity;
    private String arrCity;
    private String dptDate;
    private String dptTime;
    private String arrTime;


    public String getDptDate() {
        return dptDate;
    }

    public void setDptDate(String dptDate) {
        this.dptDate = dptDate;
    }

    public String getDptTime() {
        return dptTime;
    }

    public void setDptTime(String dptTime) {
        this.dptTime = dptTime;
    }

    public String getArrTime() {
        return arrTime;
    }

    public void setArrTime(String arrTime) {
        this.arrTime = arrTime;
    }

    public String getDpt() {
        return dpt;
    }

    public void setDpt(String dpt) {
        this.dpt = dpt;
    }

    public String getArr() {
        return arr;
    }

    public void setArr(String arr) {
        this.arr = arr;
    }

    public String getDptCity() {
        return dptCity;
    }

    public void setDptCity(String dptCity) {
        this.dptCity = dptCity;
    }

    public String getArrCity() {
        return arrCity;
    }

    public void setArrCity(String arrCity) {
        this.arrCity = arrCity;
    }

    public String getProductTag() {
        return productTag;
    }

    public void setProductTag(String productTag) {
        this.productTag = productTag;
    }

    public int getPrintPrice() {
        return printPrice;
    }

    public void setPrintPrice(int printPrice) {
        this.printPrice = printPrice;
    }

    public int getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(int ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public int getFlightType() {
        return flightType;
    }

    public void setFlightType(int flightType) {
        this.flightType = flightType;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public String getChildCabin() {
        return childCabin;
    }

    public void setChildCabin(String childCabin) {
        this.childCabin = childCabin;
    }

    public String getAduTag() {
        return aduTag;
    }

    public void setAduTag(String aduTag) {
        this.aduTag = aduTag;
    }


    @Override
    public String toString() {
        return "BookInfo{" +
                "productTag='" + productTag + '\'' +
                ", printPrice=" + printPrice +
                ", ticketPrice=" + ticketPrice +
                ", invoiceType=" + invoiceType +
                ", bookingTag='" + bookingTag + '\'' +
                ", qt='" + qt + '\'' +
                ", clientId='" + clientId + '\'' +
                ", flightNum='" + flightNum + '\'' +
                ", flightType=" + flightType +
                ", stops=" + stops +
                ", cabin='" + cabin + '\'' +
                ", childCabin='" + childCabin + '\'' +
                ", aduTag='" + aduTag + '\'' +
                ", dpt='" + dpt + '\'' +
                ", arr='" + arr + '\'' +
                ", dptCity='" + dptCity + '\'' +
                ", arrCity='" + arrCity + '\'' +
                ", dptDate='" + dptDate + '\'' +
                ", dptTime='" + dptTime + '\'' +
                ", arrTime='" + arrTime + '\'' +
                '}';
    }
}
