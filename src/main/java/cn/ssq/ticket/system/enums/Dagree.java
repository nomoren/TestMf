package cn.ssq.ticket.system.enums;

/**
 * 订单紧急程度
 */
public enum Dagree {

    RED(10,"<span style='background-color: #f30;'>临近超时(%s)</span>"),//10分钟
    RED1(10,"<span style='background-color: #f30;'>已超时</span>"),
    YELLOW(9,"<span style='background-color: #ff0;'>临近超时(%s)</span>"),//30分钟
    YELLOW1(9,"<span style='background-color: #ff0;'>座位数不足</span>"),
    ORANGE(8,"<span style='background-color: #f90;'>多人订单</span>"),
    DEFAULT(1,"");

    private Integer value;
    private String remark;


    Dagree(Integer value,String remark) {
        this.value=value;
        this.remark=remark;
    }


    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static void main(String[] args) {
        System.out.println(TicketPlace.FCQN.getRemark());
    }


}
