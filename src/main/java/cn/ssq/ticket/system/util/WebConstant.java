package cn.ssq.ticket.system.util;

public interface WebConstant {


	//订单支付状态
	/**
	 * 订单支付状态 - 未支付
	 */
	final static String PAYMENT_UNTREATED = "0";

	/**
	 * 订单支付状态 - 已支付
	 */
	final static String PAYMENT_ALREADY = "1";

	/**
	 * 行程单配送方式 - 快递
	 */
	final static String SENDWAY_KD = "01";

	/**
	 * 行程单配送方式 - 挂号信
	 */
	final static String SENDWAY_GHX = "02";

	//航程类型
	/**
	 * 航程类型 - 单程
	 */
	final static String FLIGHT_TYPE_ONEWAY = "0";

	/**
	 * 航程类型 - 往返
	 */
	final static String FLIGHT_TYPE_GOBACK = "1";

	// 乘机人类型
	/**
	 * 乘机人类型 - 成人
	 */
	final static String PASSENGER_TYPE_ADULT = "0";

	/**
	 * 乘机人类型 - 儿童
	 */
	final static String PASSENGER_TYPE_CHILD = "1";

	/**
	 * 乘机人类型 - 婴儿
	 */
	final static String PASSENGER_TYPE_BABY = "2";


	//退款状态
	/**
     * 退款状态 - 未退票
     */
    final static String REFUND_UNTREATED = "-1";
	/**
	 * 退款状态 - 申请中
	 */
	final static String REFUND_APPLY = "0";

	/**
     * 退款状态 - 已退款
     */
    final static String REFUND_ALREADY_RETURN = "1";

    /**
     * 退款状态 - 已拒绝
     */
    final static String REFUND_ALREADY_REFUSE = "2";

    /**
     * 退款状态 - 已退票待退款
     */
    //final static String REFUND_TICKET_NO_MONEY = "5";

    /**
     * 退款状态 - 提交失败
     */
    final static String REFUND_APPLY_FAIL = "6";


    /**
     * 退款状态 - 审核中
     */
    final static String REFUND_CHECKING = "7";

    /**
     * 退款状态 - 审核失败
     */
    final static String REFUND_CHECKING_FAIL = "8";




    //改签状态
    /**
     * 改签状态 - 未改签
     */
    final static String CHANGE_UNTREATED = "1";

    /**
     * 改签状态 - 已改签
     */
    final static String CHANGE_ALREADY = "2";

    /**
     * 改签状态 - 拒绝
     */
    final static String CHANGE_REFUSE = "3";

    //行程单状态
    /**
     * 行程单状态 - 未通知
     */
    final static String TRAVEL_UNTREATED = "0";

    /**
     * 行程单状态 - 已通知
     */
    final static String TRAVEL_ALREADY = "1";

    //订单状态
    /**
     * 订单状态 - 未出票
     */
    final static String ORDER_NO_TICKET = "1";

    /**
     * 订单状态 - 已出票
     */
    final static String ORDER_PRINT_TICKET = "3";

    /**
     * 订单状态 - 已取消
     */
    final static String ORDER_CANCEL = "4";

    /**
     * 订单状态 - 申请取消
     */
    final static String APPLY_ORDER_CANCEL = "99";
    /**
     * 订单状态 - 已驳回
     */
    final static String REFUSE_ORDER_CANCEL = "98";

    /**
     * 订单状态 - 客票异常
     */
    final static String ORDER_ERROR = "5";

    /**
     * 订单状态 - 出票中
     */
    final static String ORDER_PRINT = "2";
    /**
     * 订单状态 - 未出票申请退款
     */
    final static String ORDER_NOTICK_REFUND = "6";



    //更新票号状态
    /**
     * 更新票号状态 - 更新成功
     */
    final static String UPDATE_TICKET_NO_OK = "0";

    /**
     * 更新票号状态 - 正在更新
     */
    final static String UPDATE_TICKET_NO_RUN = "1";

    /**
     * 更新票号状态 - 更新失败
     */
    final static String UPDATE_TICKET_NO_ERROR = "2";

    /**
     * 更新票号状态 - 不更新
     */
    final static String UPDATE_TICKET_NO_DISABLE = "3";

    // 采购地
    /**
     * 采购地 - 网赢
     */
    final static String PURCH_PALSE_ETWIN = "03";

    /**
     * 采购地 - 517na
     */
    final static String PURCH_PALSE_517NA = "20";

    /**
     * 采购地 - 百拓
     */
    final static String PURCH_PALSE_BAITOUR = "01";

    /**
     * 采购地 - 易行
     */
    final static String PURCH_PALSE_YEEXING = "05";

    /**
     * 采购地 - 380
     */
    final static String PURCH_PALSE_380 = "07";

    /**
     * 采购地 - 19e
     */
    final static String PURCH_PALSE_19E = "09";

    /**
     * 采购地 - 51book
     */
    final static String PURCH_PALSE_51BOOK = "24";

    // 支付卡
    /**
     * 支付卡 - 网赢
     */
    final static String PAY_CARD_ETWIN = "1";

    /**
     * 支付卡 - 517na
     */
    final static String PAY_CARD_517NA = "2";

    // 政策来源
    /**
     * 政策来源 - 网赢系统
     */
    final static String POLICY_FROM_ETWIN_SYSTEM = "01";

    /**
     * 政策来源 - 网赢人工
     */
    final static String POLICY_FROM_ETWIN_PERSON = "05";

    // 订单导入方式
    /**
     * 订单导入方式 - 接口导入
     */
    final static String INTERFACE_IMPORT_YES = "1";

    /**
     * 订单导入方式 - 非接口导入
     */
    final static String INTERFACE_IMPORT_NO = "0";

    /**
     * 订单导入方式 - 接口导入(需要更新采购信息)
     */
    final static String INTERFACE_IMPORT_UPDATE = "2";

    //客票状态
    /**
     * 未出票
     */
    final static String NO_TICKET = "0";
    /**
     * 客票有效
     */
    final static String OK_TICKET = "1";
    /**
     * 客票异常
     */
    final static String ERROR_TICKET = "2";
    /**
     * 退款申请中
     */
    final static String APPLY_REFUND = "3";
    /**
     * 已退款
     */
    final static String YET_REFUND = "4";
    /**
     * 等航变
     */
    final static String WAIT_FLIGHT = "5";
    /**
     * 取消作废
     */
    final static String CANCEL = "6";
    /**
     * 拒绝退款
     */
    final static String REFUCE_REFUND = "7";
    /**
     * 改签申请中
     */
    final static String APPLY_CHANGE = "8";
    /**
     * 改签完成
     */
    final static String YET_CHANGE = "9";
    /**
     * 拒绝改签
     */
    final static String REFUCE_CHANGE = "10";


    /**
     * 退款申请类型
     */
    final static String REFUND_TYPE_ZIYUAN = "0";

    final static String REFUND_TYPE_NOZIYUAN = "1";

}
