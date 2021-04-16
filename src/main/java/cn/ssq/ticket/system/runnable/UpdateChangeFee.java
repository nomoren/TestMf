package cn.ssq.ticket.system.runnable;

import cn.ssq.ticket.system.entity.Change;
import cn.ssq.ticket.system.entity.OrderOperateLog;
import cn.ssq.ticket.system.service.ChangeService;
import cn.ssq.ticket.system.service.LogService;
import cn.ssq.ticket.system.service.OrderImport.impl.TTsOrderService;
import cn.ssq.ticket.system.service.OrderImport.impl.TcOrderService;
import cn.ssq.ticket.system.util.DictUtils;
import cn.ssq.ticket.system.util.InterfaceConstant;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class UpdateChangeFee implements Runnable {

    private static TTsOrderService ttsOrderService = SpringContextHolder.getBean(TTsOrderService.class);
    private static TcOrderService tcOrderService = SpringContextHolder.getBean(TcOrderService.class);
    private static LogService logService = SpringContextHolder.getBean(LogService.class);
    private static ChangeService changeService = SpringContextHolder.getBean(ChangeService.class);
    private List<Change> list;
    private Logger logg = LoggerFactory.getLogger(this.getClass());

    public UpdateChangeFee(List<Change> list) {
        this.list = list;
    }

    @Override
    public void run() {
        OrderOperateLog log = new OrderOperateLog();
        try {
            Change change = list.get(0);
            String orderNo = change.getOrderNo();
            String changeNo = change.getNewCOrderNo();
            log.setOrderNo(orderNo);
            log.setType("订单处理");
            log.setName("SYSTEM");
            String orderSource = change.getOrderSource();
            // String orderShop=change.getOrderShop();
            StringBuilder sb=new StringBuilder();
            if (InterfaceConstant.ORDER_SOURCE_QNR.equals(orderSource)) {
                JSONObject json = ttsOrderService.getChangeOrders(orderNo, orderNo.substring(0, 3),
                        change.getChangeDate().split(" ")[0]);
                if (json.getInt("totalCount") > 0) {
                    JSONArray orderList = json.getJSONArray("orderList");
                    for (int i = 0; i < orderList.size(); i++) {
                        JSONObject changeJson = orderList.getJSONObject(0);
                        String gqId = changeJson.getString("gqId");
                        if(changeNo.equals(gqId)){
                            JSONArray passengerArr = changeJson.getJSONArray("ttsPassengerList");
                            String revenuePrice = new BigDecimal(changeJson.getString("allPrices"))
                                    .divide(new BigDecimal(passengerArr.size())).toString();
                            for (Change c : list) {
                                Change cc = new Change();
                                cc.setChangeId(c.getChangeId());
                                cc.setRevenuePrice(revenuePrice);
                                changeService.updateById(cc);
                                sb.append("修改改签费用-->" + c.getRevenuePrice()+":"+revenuePrice);
                            }

                        }
                    }
                }
            }else if (InterfaceConstant.ORDER_SOURCE_TC.equals(orderSource)) {
                String user = DictUtils.getDictCode("order_import_cfgtc", "user");
                String pass = DictUtils.getDictCode("order_import_cfgtc", "pass");
                JSONObject changeDetail = tcOrderService.getChangeDetail(changeNo, user, pass);
                if (changeDetail != null && changeDetail.getBoolean("isSuccess")) {
                    JSONObject data = changeDetail.getJSONObject("data");
                    JSONArray changeList = data.getJSONArray("changeTicketList");
                    for (int i = 0; i < changeList.size(); i++) {
                        JSONObject changes = changeList.getJSONObject(i);
                        if (1 == changes.getInt("ticketType")) {
                            BigDecimal changeFee = new BigDecimal(changes.getDouble("changeFee"));
                            BigDecimal upgradeFee = new BigDecimal(changes.getDouble("upgradeFee"));
                            String revenuePrice = changeFee.add(upgradeFee).toString();
                            for (Change c : list) {
                                if (changes.getString("passName").equals(c.getPassengerName())) {
                                    Change cc = new Change();
                                    cc.setChangeId(c.getChangeId());
                                    cc.setRevenuePrice(revenuePrice);
                                    changeService.updateById(cc);
                                    sb.append("修改改签费用-->" + c.getRevenuePrice()+":"+revenuePrice);
                                }
                            }

                        }
                    }
                }

            }
            log.setContent(sb.toString());
        } catch (Exception e) {
            logg.error("修改改签费用失败", e);
            log.setContent("修改改签费用失败");
        } finally {
            if(StringUtils.isNotEmpty(log.getContent())){
                logService.saveLog(log);
            }
        }

    }

}
