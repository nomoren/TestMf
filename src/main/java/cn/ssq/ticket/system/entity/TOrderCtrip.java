package cn.ssq.ticket.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 订单_携程中间表(t_order_ctrip)
 * 
 * @author bianj
 * @version 1.0.0 2020-12-31
 */
@TableName("t_order_ctrip")
public class TOrderCtrip implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = 4732935606008144423L;

    /** 自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 订单表主键id */
    private Long orderId;

    /** 携程订单id */
    private String ctripId;

    /**
     * 获取自增主键
     * 
     * @return 自增主键
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * 设置自增主键
     * 
     * @param id
     *          自增主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取订单表主键id
     * 
     * @return 订单表主键id
     */
    public Long getOrderId() {
        return this.orderId;
    }

    /**
     * 设置订单表主键id
     *
     * @param orderId
     *          订单表主键id
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取携程订单id
     * 
     * @return 携程订单id
     */
    public String getCtripId() {
        return this.ctripId;
    }

    /**
     * 设置携程订单id
     * 
     * @param ctripId
     *          携程订单id
     */
    public void setCtripId(String ctripId) {
        this.ctripId = ctripId;
    }

}