package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.entity.ExportBean.*;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.ExportExcel;
import cn.ssq.ticket.system.util.InterfaceConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * 报表导出
 *
 * @author Administrator
 */
@RequestMapping("/export")
@Controller
public class ExportController {

    private String PREFIX = "/modular/system/export/";

    @Autowired
    private OrderService orderService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private PurchaseService purchService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ChangeService changeService;

    /**
     * 采购报表页
     */
    @RequestMapping("/toExportPrintTicket")
    public String toExportPrintTicket() {
        return PREFIX + "export_print_ticket.html";
    }

    /**
     * 财务报表页
     *
     * @return
     */
    @RequestMapping("/toExportFinance")
    public String toExportFinance() {
        return PREFIX + "export_finance.html";
    }

    /**
     * 收款报表页
     *
     * @return
     */
    @RequestMapping("/toExportReceipt")
    public String toExportReceipt() {
        return PREFIX + "export_receipt.html";
    }

    /**
     * 退票报表页
     *
     * @return
     */
    @RequestMapping("/toExportRefund")
    public String toExportRefund() {
        return PREFIX + "export_refund.html";
    }

    /**
     * 改签报表页
     *
     * @return
     */
    @RequestMapping("/toExportChange")
    public String toExportChange() {
        return PREFIX + "export_change.html";
    }

    /**
     * 手工报表
     *
     * @return
     */
    @RequestMapping("/toExportSG")
    public String toExportSG() {
        return PREFIX + "export_SG.html";
    }

    /**
     * 导出旧模板手工报表
     *
     * @param startDate
     * @param endDate
     * @param name      出票员账号
     * @param response
     * @param requext
     */
    @ResponseBody
    @RequestMapping("/exportPrintSG")
    public void exportPrintSG(String startDate, String endDate, String name, HttpServletResponse response, HttpServletRequest requext) {
        try {
            //new TongChengSchednule().updateTicketNo();
            //new CtripSchednule().updateTicketNo();
        } catch (Exception e) {

        }
        try {
            String fileName = name + "手工报表" + DateFormatUtils.format(new Date(), "yyyyMMdd") + ".xlsx";
            QueryWrapper<Purchase> query = new QueryWrapper<Purchase>();
            query.between("c_add_date", startDate, endDate);
            query.eq("employee_name", name.trim());
            query.eq("flag", 0);
            List<Purchase> purchData = purchService.getByQueryWapper(query);
            List<ShouGReport> list = new ArrayList<ShouGReport>();
            for (Purchase purchase : purchData) {
                ShouGReport report = new ShouGReport();
                BeanUtils.copyProperties(purchase, report);
                String passengerNames = purchase.getPassengerNames();
                String[] split = passengerNames.split(",");
                List<Long> tlist = new ArrayList<Long>();
                for (String pname : split) {
                    QueryWrapper<Passenger> pQuery = new QueryWrapper<Passenger>();
                    pQuery.eq("name", pname);
                    pQuery.eq("order_no", purchase.getOrderNo());
                    List<Passenger> findByQueryWapper = passengerService.findByQueryWapper(pQuery);
                    String tiketNo = findByQueryWapper.get(0).getTicketNo();
                    Long lticket = Long.valueOf(tiketNo.replace("-", ""));
                    tlist.add(lticket);
                }
                Collections.sort(tlist);
                String ticketFirst = String.valueOf(tlist.get(0));
                String start = ticketFirst.substring(0, 3);
                String end = ticketFirst.substring(3, ticketFirst.length());
                ticketFirst = start + "-" + end;
                if (tlist.size() > 1) {
                    String ticketEnd = String.valueOf(tlist.get(tlist.size() - 1));
                    String last2 = ticketEnd.substring(ticketEnd.length() - 2);
                    String ticketNos = ticketFirst + "-" + last2;
                    report.setTicketNos(ticketNos);
                } else {
                    report.setTicketNos(ticketFirst);
                }
                list.add(report);
            }
            ExportExcel exportExcel = new ExportExcel(null, ShouGReport.class);
            exportExcel.setDataList(list);
            exportExcel.write(response, fileName, requext).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 导出采购报表
     *
     * @param startDate
     * @param endDate
     * @param orderSource
     * @param orderShop
     * @param response
     */
    @ResponseBody
    @RequestMapping("/exportPrintTicket")
    public void exportPrintTicket(String startDate, String endDate, String orderSource,
                                  String orderShop, HttpServletResponse response, HttpServletRequest requext) {
        try {
            String fileName = "采购报表" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx";
            List<Purchase> purchData = purchService.getPurchData(startDate, endDate, orderSource, orderShop);
            List<PrintTicketReport> list = new ArrayList<PrintTicketReport>();
            for (Purchase purchase : purchData) {
                PrintTicketReport report = new PrintTicketReport();
                BeanUtils.copyProperties(purchase, report);
                list.add(report);
            }
            ExportExcel exportExcel = new ExportExcel(null, PrintTicketReport.class);
            exportExcel.setDataList(list);
            exportExcel.write(response, fileName, requext).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 导出改签报表
     *
     * @param startDate
     * @param endDate
     * @param
     * @param
     * @param response
     */
    @ResponseBody
    @RequestMapping("/exportChangeReport")
    public void exportChangeReport(String startDate, String endDate,
                                   HttpServletResponse response, HttpServletRequest requext) {
        try {
            List<AirLine> airList = mongoTemplate.findAll(AirLine.class);
            Map<String, AirLine> map = new HashMap<String, AirLine>();
            for (AirLine air : airList) {
                map.put(air.getAirlineCode(), air);
            }
            String fileName = "改签报表" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx";
            List<Purchase> purchData = purchService.getPurchDataByState(startDate, endDate, "2");
            List<ChangeReport> list = new ArrayList<ChangeReport>();
            //d
            for (Purchase purchase : purchData) {
                purchase.setPassengerNum(purchase.getPassengerNames().split(",").length);
                String[] str = purchase.getPassengerNames().split(",");
                int i = 0;
                for (String name : str) {
                    purchase.setPassengerNames(name);
                    ChangeReport report = new ChangeReport();
                    if (i == 0) {
                        report.setOne(true);
                    }
                    QueryWrapper<Change> cquery = new QueryWrapper<Change>();
                    cquery.eq("order_no", purchase.getOrderNo());
                    cquery.eq("passenger_Name", name);
                    Change change = new Change();
                    List<Change> changes = changeService.selectByQuery(cquery);
                    if (changes != null && changes.size() > 0) {
                        change = changes.get(0);
                    }
                    String revenuePrice = change.getRevenuePrice();
                    purchase.setCustomerAmount(StringUtils.isEmpty(revenuePrice) ? 0 : Double.valueOf(revenuePrice));
                    BeanUtils.copyProperties(purchase, report);
                    String flightNo = change.getFlightNo();
                    if (StringUtils.isNotEmpty(flightNo)) {
                        report.setAirlineCode(flightNo.substring(0, 2));
                    }
                    QueryWrapper<Passenger> pquery = new QueryWrapper<Passenger>();
                    pquery.eq("order_no", purchase.getOrderNo());
                    pquery.eq("name", name);
                    Passenger passenger = passengerService.findByQueryWapper(pquery).get(0);
                    report.setsTicketNo(passenger.getTicketNo());
                    report.setsFlightNo(passenger.getFlightNo());
                    report.setsCabin(passenger.getCabin());
                    report.setNewFlightNo(change.getFlightNo());
                    report.setNewFlightDate(change.getFlightDate());
                    report.setNewCabin(change.getCabin());
                    report.setApplyDate(change.getChangeDate());
                    report.setLastModDate(passenger.getPrintTicketDate());
                    report.setPolicyCode(passenger.getPolicyType());
                    String purchPalseName = DictUtils.getDictName("order_purch_place", report.getPurchPalse());
                    if ("1".equals(StringUtils.stripToEmpty(report.getPurchPalse())) || "5".equals(StringUtils.stripToEmpty(report.getPurchPalse()))) {
                        if (map.containsKey(report.getAirlineCode())) {
                            report.setSupplier(map.get(report.getAirlineCode()).getShortName() + purchPalseName);
                        } else {
                            report.setSupplier(report.getAirlineCode() + purchPalseName);
                        }
                    } else {
                        report.setSupplier(purchPalseName);
                    }
                    list.add(report);
                    i++;
                }
            }
            ExportExcel exportExcel = new ExportExcel(null, ChangeReport.class);
            exportExcel.setDataList(list);
            exportExcel.write(response, fileName, requext).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 导出收款报表
     *
     * @param startDate
     * @param endDate
     * @param orderSource
     * @param orderShop
     * @param response
     */
    @RequestMapping("/exportReceipt")
    @ResponseBody
    public void exportReceipt(String startDate, String endDate, String orderSource,
                              String orderShop, HttpServletResponse response, HttpServletRequest requext) {
        try {
            String fileName = "收款报表" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx";
            List<OrderVO> orderVoList = orderService.getOrderVoList(startDate, endDate, orderSource, orderShop);
            List<ReceiptReport> list = new ArrayList<ReceiptReport>();
            for (OrderVO orderVo : orderVoList) {
                ReceiptReport report = new ReceiptReport();
                BeanUtils.copyProperties(orderVo, report);
                list.add(report);
            }
            ExportExcel exportExcel = new ExportExcel(null, ReceiptReport.class);
            exportExcel.setDataList(list);
            exportExcel.write(response, fileName, requext).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 导出退票报表
     *
     * @param startDate
     * @param endDate
     * @param orderSource
     * @param orderShop
     * @param response
     */
    @RequestMapping("/exportRefund")
    @ResponseBody
    public void exportRefund(String startDate, String endDate, String orderSource,
                             String orderShop, HttpServletResponse response, HttpServletRequest requext) {
        try {
            List<AirLine> airList = mongoTemplate.findAll(AirLine.class);
            Map<String, AirLine> map = new HashMap<String, AirLine>();
            for (AirLine air : airList) {
                map.put(air.getAirlineCode(), air);
            }
            String fileName = "退票报表" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx";
            List<RefundVO> refundList = refundService.getDataForReport(startDate, endDate, orderSource, orderShop);
            List<RefundTicketReport> list = new ArrayList<RefundTicketReport>();
            for (RefundVO refund : refundList) {
                RefundTicketReport report = new RefundTicketReport();
                List<Purchase> purchaseList = purchService.getPurchaseList(refund.getOrderSource(), refund.getOrderShop(), refund.getOrderNo());
                if(purchaseList.size()!=0){
                    if (purchaseList.size() > 1) {
                        for (Purchase purch : purchaseList) {
                            List<String> nameList = Arrays.asList(purch.getPassengerNames().split(","));
                            if (nameList.contains(refund.getPassengerName())) {
                                String purchPalseName = DictUtils.getDictName("order_purch_place", purch.getSupplier());
                                if (map.containsKey(refund.getAirlineCode())) {
                                    purch.setSupplier(map.get(refund.getAirlineCode()).getShortName() + purchPalseName);
                                } else {
                                    purch.setSupplier(refund.getAirlineCode() + purchPalseName);
                                }
                                refund.setPurchase(purch);
                                break;
                            }
                        }
                    } else {
                        Purchase purchase = purchaseList.get(0);
                        String purchPalseName = DictUtils.getDictName("order_purch_place", purchase.getSupplier());
                        if (map.containsKey(refund.getAirlineCode())) {
                            purchase.setSupplier(map.get(refund.getAirlineCode()).getShortName() + purchPalseName);
                        } else {
                            purchase.setSupplier(refund.getAirlineCode() + purchPalseName);
                        }
                        refund.setPurchase(purchase);
                    }
                }
                BeanUtils.copyProperties(refund, report);
                list.add(report);
            }
            ExportExcel exportExcel = new ExportExcel(null, RefundTicketReport.class);
            exportExcel.setDataList(list);
            exportExcel.write(response, fileName, requext).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出财务报表
     *
     * @param startDate
     * @param endDate
     * @param orderSource
     * @param orderShop
     * @param response
     */
    @ResponseBody
    @RequestMapping("/exportFinance")
    public void exportFinance(String startDate, String endDate, String orderSource,
                              String orderShop, HttpServletResponse response, HttpServletRequest requext) {
        try {
            List<AirLine> airList = mongoTemplate.findAll(AirLine.class);
            Map<String, AirLine> map = new HashMap<String, AirLine>();
            for (AirLine air : airList) {
                map.put(air.getAirlineCode(), air);
            }
            String fileName = "财务报表" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx";
            List<Purchase> purchData = purchService.getPurchData(startDate, endDate, orderSource, orderShop);
            if (InterfaceConstant.ORDER_SOURCE_CTRIP.equals(orderSource)) {//携程报表
                List<XieCReport> list = new ArrayList<XieCReport>();
                for (Purchase purchase : purchData) {
                    purchase.setPassengerNum(purchase.getPassengerNames().split(",").length);
                    if (purchase.getPassengerNum() > 1) {//一条采购单若包含多个人的采购信息，要拆分出每个乘客为一行
                        String[] str = purchase.getPassengerNames().split(",");
                        int i = 0;
                        for (String name : str) {
                            XieCReport xieC = new XieCReport();
                            if (i == 0) {
                                xieC.setOne(true);
                            }
                            BeanUtils.copyProperties(purchase, xieC);
                            xieC.setPassengerNames(name);
                            String purchPalseName = DictUtils.getDictName("order_purch_place", xieC.getPurchPalse());
                            if ("1".equals(StringUtils.stripToEmpty(xieC.getPurchPalse()))) {
                                if (map.containsKey(xieC.getAirlineCode())) {
                                    xieC.setSupplier(map.get(xieC.getAirlineCode()).getShortName() + purchPalseName);
                                } else {
                                    xieC.setSupplier(xieC.getAirlineCode() + purchPalseName);
                                }
                            } else {
                                xieC.setSupplier(purchPalseName);
                            }
                            list.add(xieC);
                            i++;
                        }
                    } else {
                        XieCReport xieC = new XieCReport();
                        BeanUtils.copyProperties(purchase, xieC);
                        String purchPalseName = DictUtils.getDictName("order_purch_place", xieC.getPurchPalse());
                        if ("1".equals(StringUtils.stripToEmpty(xieC.getPurchPalse()))) {
                            if (map.containsKey(xieC.getAirlineCode())) {
                                xieC.setSupplier(map.get(xieC.getAirlineCode()).getShortName() + purchPalseName);
                            } else {
                                xieC.setSupplier(xieC.getAirlineCode() + purchPalseName);
                            }
                        } else {
                            xieC.setSupplier(purchPalseName);
                        }
                        list.add(xieC);
                    }
                }
                ExportExcel exportExcel = new ExportExcel(null, XieCReport.class);
                exportExcel.setDataList(list);
                exportExcel.write(response, fileName, requext).dispose();
            } else if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)) {//去哪儿报表
                List<TTSReport> list = new ArrayList<TTSReport>();
                for (Purchase purchase : purchData) {
                    purchase.setPassengerNum(purchase.getPassengerNames().split(",").length);
                    if (purchase.getPassengerNum() > 1) {
                        String[] str = purchase.getPassengerNames().split(",");
                        int i = 0;
                        for (String name : str) {
                            TTSReport ttsR = new TTSReport();
                            if (i == 0) {
                                ttsR.setOne(true);
                            }
                            BeanUtils.copyProperties(purchase, ttsR);
                            ttsR.setPassengerNames(name);
                            String purchPalseName = DictUtils.getDictName("order_purch_place", ttsR.getPurchPalse());
                            if ("1".equals(StringUtils.stripToEmpty(ttsR.getPurchPalse()))) {
                                if (map.containsKey(ttsR.getAirlineCode())) {
                                    ttsR.setSupplier(map.get(ttsR.getAirlineCode()).getShortName() + purchPalseName);
                                } else {
                                    ttsR.setSupplier(ttsR.getAirlineCode() + purchPalseName);
                                }
                            } else {
                                ttsR.setSupplier(purchPalseName);
                            }
                            list.add(ttsR);
                            i++;
                        }
                    } else {
                        TTSReport ttsR = new TTSReport();
                        BeanUtils.copyProperties(purchase, ttsR);
                        String purchPalseName = DictUtils.getDictName("order_purch_place", ttsR.getPurchPalse());
                        if ("1".equals(StringUtils.stripToEmpty(ttsR.getPurchPalse()))) {
                            if (map.containsKey(ttsR.getAirlineCode())) {
                                ttsR.setSupplier(map.get(ttsR.getAirlineCode()).getShortName() + purchPalseName);
                            } else {
                                ttsR.setSupplier(ttsR.getAirlineCode() + purchPalseName);
                            }
                        } else {
                            ttsR.setSupplier(purchPalseName);
                        }
                        list.add(ttsR);
                    }

                }
                ExportExcel exportExcel = new ExportExcel(null, TTSReport.class);
                exportExcel.setDataList(list);
                exportExcel.write(response, fileName, requext).dispose();
            } else if (InterfaceConstant.ORDER_SOURCE_TB.equals(orderSource)) {//淘宝报表
                List<TbReport> list = new ArrayList<TbReport>();
                for (Purchase purchase : purchData) {
                    purchase.setPassengerNum(purchase.getPassengerNames().split(",").length);
                    if (purchase.getPassengerNum() > 1) {
                        String[] str = purchase.getPassengerNames().split(",");
                        int i = 0;
                        for (String name : str) {
                            TbReport tb = new TbReport();
                            if (i == 0) {
                                tb.setOne(true);
                            }
                            BeanUtils.copyProperties(purchase, tb);
                            tb.setPassengerNames(name);
                            String purchPalseName = DictUtils.getDictName("order_purch_place", tb.getPurchPalse());
                            if ("1".equals(StringUtils.stripToEmpty(tb.getPurchPalse()))) {
                                if (map.containsKey(tb.getAirlineCode())) {
                                    tb.setSupplier(map.get(tb.getAirlineCode()).getShortName() + purchPalseName);
                                } else {
                                    tb.setSupplier(tb.getAirlineCode() + purchPalseName);
                                }
                            } else {
                                tb.setSupplier(purchPalseName);
                            }
                            list.add(tb);
                            i++;
                        }
                    } else {
                        TbReport tb = new TbReport();
                        BeanUtils.copyProperties(purchase, tb);
                        String purchPalseName = DictUtils.getDictName("order_purch_place", tb.getPurchPalse());
                        if ("1".equals(StringUtils.stripToEmpty(tb.getPurchPalse()))) {
                            if (map.containsKey(tb.getAirlineCode())) {
                                tb.setSupplier(map.get(tb.getAirlineCode()).getShortName() + purchPalseName);
                            } else {
                                tb.setSupplier(tb.getAirlineCode() + purchPalseName);
                            }
                        } else {
                            tb.setSupplier(purchPalseName);
                        }
                        list.add(tb);
                    }
                }
                ExportExcel exportExcel = new ExportExcel(null, TbReport.class);
                exportExcel.setDataList(list);
                exportExcel.write(response, fileName, requext).dispose();
            } else {//其他报表
                List<OtherReport> list = new ArrayList<OtherReport>();
                for (Purchase purchase : purchData) {
                    purchase.setPassengerNum(purchase.getPassengerNames().split(",").length);

                    if (purchase.getPassengerNum() > 1) {
                        String[] str = purchase.getPassengerNames().split(",");
                        int i = 0;
                        for (String name : str) {
                            OtherReport other = new OtherReport();
                            if (i == 0) {
                                other.setOne(true);
                            }
                            BeanUtils.copyProperties(purchase, other);
                            other.setPassengerNames(name);
                            String purchPalseName = DictUtils.getDictName("order_purch_place", other.getPurchPalse());
                            if ("1".equals(StringUtils.stripToEmpty(other.getPurchPalse()))) {
                                if (map.containsKey(other.getAirlineCode())) {
                                    other.setSupplier(map.get(other.getAirlineCode()).getShortName() + purchPalseName);
                                } else {
                                    other.setSupplier(other.getAirlineCode() + purchPalseName);
                                }
                            } else {
                                other.setSupplier(purchPalseName);
                            }
                            list.add(other);
                            i++;
                        }
                    } else {
                        OtherReport other = new OtherReport();
                        BeanUtils.copyProperties(purchase, other);
                        String purchPalseName = DictUtils.getDictName("order_purch_place", other.getPurchPalse());
                        if ("1".equals(StringUtils.stripToEmpty(other.getPurchPalse()))) {
                            if (map.containsKey(other.getAirlineCode())) {
                                other.setSupplier(map.get(other.getAirlineCode()).getShortName() + purchPalseName);
                            } else {
                                other.setSupplier(other.getAirlineCode() + purchPalseName);
                            }
                        } else {
                            other.setSupplier(purchPalseName);
                        }
                        list.add(other);
                    }
                }

                ExportExcel exportExcel = new ExportExcel(null, OtherReport.class);
                exportExcel.setDataList(list);
                exportExcel.write(response, fileName, requext).dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出采购单报表模板
     */
    @RequestMapping("/exportPurchReportModel")
    @ResponseBody
    public void exportPrintTicketModel(HttpServletResponse response) {
        try {
            String fileName = "采购单报表导入模板.xlsx";
            List<PurchImport> list = new ArrayList<PurchImport>();
            PurchImport purchImport = new PurchImport();
            purchImport.setOrderNo("2020111222333");
            purchImport.setTradeNo("DZFC200306813657");
            purchImport.setPassengerName("张三,李四");
            purchImport.setPurchaseSite("官网");
            purchImport.setPurchasePrice("1000");
            purchImport.setPayWay("支付宝");
            purchImport.setRemarks("13888888888,123456");
            purchImport.setResult("(不用填写)");
            list.add(purchImport);
            ExportExcel exportExcel = new ExportExcel(null, PurchImport.class, 2);
            exportExcel.setDataList(list);
            exportExcel.write(response, fileName).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
