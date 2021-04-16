package cn.ssq.ticket.system.enums;

public enum TicketPlace {
    PP(1,"鹏鹏"),
    ZHSL(2,"深航商旅"),
    FCQN(3,"反踩去哪"),
    WONIU(4,"反踩蜗牛");

    private Integer value;
    private String remark;

    TicketPlace(Integer value,String remark) {
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
