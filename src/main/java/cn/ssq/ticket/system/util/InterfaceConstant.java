package cn.ssq.ticket.system.util;

/** 
 * 外部接口常量类
 */
public class InterfaceConstant{
	
	/* ============================== 订单来源 Begin =================================== */
	/**
	 * 订单来源 - 途牛
	 */
	public static final String ORDER_SOURCE_TN = "06";
	/**
	 * 订单来源 - 淘宝
	 */
	public static final String ORDER_SOURCE_TB = "04";
	
	/**
	 * 订单来源 - 去哪儿
	 */
	public static final String ORDER_SOURCE_QNR = "02";
	
	/**
	 * 订单来源 - 酷讯
	 */
	public static final String ORDER_SOURCE_KX = "03";
	
	/**
	 * 订单来源 - 携程
	 */
	public static final String ORDER_SOURCE_CTRIP = "1";
	
	/**
	 * 订单来源 - 同程
	 */
	public static final String ORDER_SOURCE_TC = "05";
	
	/**
	 * 订单来源 - 就旅行
	 */
	public static final String ORDER_SOURCE_JIU = "07";
	
/*	 ============================== 订单来源 End ===================================== 
	
	
	
	 ============================== 外部政策来源 Begin =============================== 
	*//**
	 * 政策来源：ETWIN
	 *//*
	public final static String POLICY_SOURCE_ETWIN = "ETWIN";
	
	*//**
	 * 政策来源：本票通
	 *//*
	public final static String POLICY_SOURCE_ALIDZ = "ALIDZ";
	
	*//**
	 * 政策来源：517NA
	 *//*
	public final static String POLICY_SOURCE_517NA = "517NA";
	
	*//**
	 * 政策来源：baitour
	 *//*
	public final static String POLICY_SOURCE_BAITOUR = "baitour";
	
	*//**
	 * 政策来源：yeexing
	 *//*
	public final static String POLICY_SOURCE_YEEXING = "yeexing";
	
	*//**
	 * 政策来源：380
	 *//*
	public final static String POLICY_SOURCE_380 = "380";
	
	*//**
	 * 政策来源：19E
	 *//*
	public final static String POLICY_SOURCE_19E = "19E";
	*//**
	 * 政策来源：51book
	 *//*
	public final static String POLICY_SOURCE_51book = "51book";
	
	
	*//**
	 * 政策来源： 51bookteam
	 *//*
	public final static String POLICY_SOURCE_51bookteam = "51bookteam" ;
	
	*//**
	 * 政策来源： 8000yi
	 *//*
	public final static String POLICY_SOURCE_8000yi = "8000yi" ;
	
	*//**
	 * 政策来源： 联合800
	 *//*
	public final static String POLICY_SOURCE_lianhe800 = "lianhe800" ;
	
	*//**
	 * 政策来源： 票盟
	 *//*
	public final static String POLICY_SOURCE_piaoMeng = "piaoMeng" ;
	 ============================== 外部政策来源 End ================================ */
	
	
	
	/* ============================== 操作日志 Begin =================================== */
	/**
	 * 淘宝订单批量导入
	 *//*
	public final static String LOG_TAOBAO_BATCH_IMPORT = "TAOBAO_BATCH_IMPORT";
	
	*//**
	 * 去哪儿订单批量导入
	 *//*
	public final static String LOG_QUNAR_BATCH_IMPORT = "QUNAR_BATCH_IMPORT";
	
	*//**
	 * 酷讯订单批量导入
	 *//*
	public final static String LOG_KUXUN_BATCH_IMPORT = "KUXUN_BATCH_IMPORT";
	
	*//**
	 * 淘宝成功操作日志
	 *//*
	public final static String SUCCESS_LOG_TAOBAO = "SUCCESS_LOG_TAOBAO";
	
	*//**
	 * 淘宝失败操作日志
	 *//*
	public final static String ERROR_LOG_TAOBAO = "ERROR_LOG_TAOBAO";
	
	
	*//**
	 * 去哪儿成功操作日志
	 *//*
	public final static String SUCCESS_LOG_QUNAR = "SUCCESS_LOG_QUNAR";
	
	*//**
	 * 去哪儿失败操作日志
	 *//*
	public final static String ERROR_LOG_QUNAR = "ERROR_LOG_QUNAR";
	
	*//**
	 * 酷讯成功操作日志
	 *//*
	public final static String SUCCESS_LOG_KUXUN = "SUCCESS_LOG_KUXUN";
	
	*//**
	 * 酷讯失败操作日志
	 *//*
	public final static String ERROR_LOG_KUXUN = "ERROR_LOG_KUXUN";
	
	*//**
	 * 网赢成功操作日志
	 *//*
	public final static String SUCCESS_LOG_ETWIN = "SUCCESS_LOG_ETWIN";
	
	*//**
	 * 网赢失败操作日志
	 *//*
	public final static String ERROR_LOG_ETWIN = "ERROR_LOG_ETWIN";
	
	*//**
	 * 517na成功操作日志
	 *//*
	public final static String SUCCESS_LOG_517NA = "SUCCESS_LOG_517NA";
	
	*//**
	 * 本票通成功操作日志
	 *//*
	public final static String SUCCESS_LOG_ALIDZ = "SUCCESS_LOG_ALIDZ";
	
	*//**
	 * 517na失败操作日志
	 *//*
	public final static String ERROR_LOG_517NA = "ERROR_LOG_517NA";
	*//**
	 * 本票通失败操作日志
	 *//*
	public final static String ERROR_LOG_ALIDZ = "ERROR_LOG_ALIDZ";
	
	*//**
	 * 517na成功操作日志
	 *//*
	public final static String SUCCESS_LOG_51book = "SUCCESS_LOG_51book";
	
	*//**
	 * 517na失败操作日志
	 *//*
	public final static String ERROR_LOG_51book = "ERROR_LOG_51book";
	
	*//**
	 * 接口更新票号成功操作日志
	 *//*
	public final static String SUCCESS_LOG_UPDATE_TICKET_NO = "SUCCESS_LOG_UPDATE_TICKET_NO";
	
	*//**
	 * 接口更新票号失败操作日志
	 *//*
	public final static String ERROR_LOG_UPDATE_TICKET_NO = "ERROR_LOG_UPDATE_TICKET_NO";
	
	*//**
	 * 接口更新采购信息成功操作日志
	 *//*
	public final static String SUCCESS_LOG_UPDATE_PURCHASE = "SUCCESS_LOG_UPDATE_PURCHASE";
	
	*//**
	 * 接口更新采购信息失败操作日志
	 *//*
	public final static String ERROR_LOG_UPDATE_PURCHASE = "ERROR_LOG_UPDATE_PURCHASE";
	
	*//**
	 * 获取C站航班价格成功操作日志
	 *//*
	public final static String SUCCESS_LOG_FLIGHT_PRICE = "SUCCESS_LOG_FLIGHT_PRICE";
	
	*//**
	 * 获取C站航班价格失败操作日志
	 *//*
	public final static String ERROR_LOG_FLIGHT_PRICE = "ERROR_LOG_FLIGHT_PRICE";*/
	
	
	/* ============================== 操作日志 Begin =================================== */
	
	
	
}
