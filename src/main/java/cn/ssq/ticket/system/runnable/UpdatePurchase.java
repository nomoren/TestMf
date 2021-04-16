package cn.ssq.ticket.system.runnable;

import cn.ssq.ticket.system.entity.Purchase;
import cn.ssq.ticket.system.service.PurchaseService;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatePurchase implements Runnable{

    private String orderNo;

    private String userName;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static PurchaseService purchaseService = SpringContextHolder.getBean(PurchaseService.class);

    public UpdatePurchase(String orderNo, String userName) {
        this.orderNo = orderNo;
        this.userName = userName;
    }

    @Override
    public void run() {
        try {
            if(StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(orderNo)){
                Purchase purchase=new Purchase();
                purchase.setEmployeeName(userName);
                UpdateWrapper<Purchase> updateWrapper =new UpdateWrapper<>();
                updateWrapper.eq("order_no",orderNo);
                purchaseService.updatePurchase(purchase,updateWrapper);
                log.info(orderNo+"：修改姓名："+userName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(orderNo+"：修改姓名异常："+userName,e);
        }
    }


}
