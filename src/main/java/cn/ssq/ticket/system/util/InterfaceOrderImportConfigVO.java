package cn.ssq.ticket.system.util;

import java.io.Serializable;

/** 
 * 订单导入接口配置信息
 */
public class InterfaceOrderImportConfigVO implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5603611267607445523L;
	
	/**
	 * 配置主键ID
	 */
	private Integer configId = -1;
	
	/**
	 * 订单导入来源
	 */
	private String source;
	
	/**
	 * 店铺名称
	 */
	private String shopName;
	
	/**
	 * agent用户ID
	 */
	private String agentId;
	
	/**
	 * 订单批量导入URL
	 */
	private String orderBatchImportURL;
	
	/**
	 * 订单导入URL
	 */
	private String orderImportURL;
	
	/**
	 * 更新票号URL
	 */
	private String orderUpdateTicketNoURL;
	
	/**
	 * 订单导入用户
	 */
	private String orderImportUser;
	
	/**
	 * 订单导入密码
	 */
	private String orderImportPassword;
	
	/**
	 * 更新票号用户
	 */
	private String orderUpdateTicketNoUser;
	
	/**
	 * 更新票号密码
	 */
	private String orderUpdateTicketNoPassword;
	
	/**
	 * appKey
	 */
	private String appKey;
	
	/**
	 * 店铺描述
	 */
	private String shopNameDesc;
	
	/**
	 * 激活开关
	 */
	private String active;
	
	/**
	 * 其他配置1
	 */
	private String otherConfig1;
	
	/**
	 * 其他配置2
	 */
	private String otherConfig2;
	
	/**
	 * 其他配置3
	 */
	private String otherConfig3;
	
	/**
	 * 其他配置4
	 */
	private String otherConfig4;
	
	/**
	 * 创建时间
	 */
	private String createDate;
	
	/**
	 * 创建人
	 */
	private String createBy;
	
	/**
	 * 最近一次修改时间
	 */
	private String modifyDate;
	
	/**
	 * 最近一次修改人
	 */
	private String modifyBy;
	
	
	/**
	 * 获取配置主键ID<p>
	 * @return  configId  配置主键ID<br>
	 */
	public Integer getConfigId()
	{
		return configId;
	}

	/**
	 * 设置配置主键ID<p>
	 * @param  configId  配置主键ID<br>
	 */
	public void setConfigId(Integer configId)
	{
		this.configId = configId;
	}

	/**
	 * 获取订单导入来源<p>
	 * @return  source  订单导入来源<br>
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * 设置订单导入来源<p>
	 * @param  source  订单导入来源<br>
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * 获取店铺名称<p>
	 * @return  shopName  店铺名称<br>
	 */
	public String getShopName()
	{
		return shopName;
	}

	/**
	 * 设置店铺名称<p>
	 * @param  shopName  店铺名称<br>
	 */
	public void setShopName(String shopName)
	{
		this.shopName = shopName;
	}

	/**
	 * 获取agent用户ID<p>
	 * @return  agentId  agent用户ID<br>
	 */
	public String getAgentId()
	{
		return agentId;
	}

	/**
	 * 设置agent用户ID<p>
	 * @param  agentId  agent用户ID<br>
	 */
	public void setAgentId(String agentId)
	{
		this.agentId = agentId;
	}

	/**
	 * 获取订单批量导入URL<p>
	 * @return  orderBatchImportURL  订单批量导入URL<br>
	 */
	public String getOrderBatchImportURL()
	{
		return orderBatchImportURL;
	}

	/**
	 * 设置订单批量导入URL<p>
	 * @param  orderBatchImportURL  订单批量导入URL<br>
	 */
	public void setOrderBatchImportURL(String orderBatchImportURL)
	{
		this.orderBatchImportURL = orderBatchImportURL;
	}

	/**
	 * 获取订单导入URL<p>
	 * @return  orderImportURL  订单导入URL<br>
	 */
	public String getOrderImportURL()
	{
		return orderImportURL;
	}

	/**
	 * 设置订单导入URL<p>
	 * @param  orderImportURL  订单导入URL<br>
	 */
	public void setOrderImportURL(String orderImportURL)
	{
		this.orderImportURL = orderImportURL;
	}

	/**
	 * 获取更新票号URL<p>
	 * @return  orderUpdateTicketNoURL  更新票号URL<br>
	 */
	public String getOrderUpdateTicketNoURL()
	{
		return orderUpdateTicketNoURL;
	}

	/**
	 * 设置更新票号URL<p>
	 * @param  orderUpdateTicketNoURL  更新票号URL<br>
	 */
	public void setOrderUpdateTicketNoURL(String orderUpdateTicketNoURL)
	{
		this.orderUpdateTicketNoURL = orderUpdateTicketNoURL;
	}

	/**
	 * 获取订单导入用户<p>
	 * @return  orderImportUser  订单导入用户<br>
	 */
	public String getOrderImportUser()
	{
		return orderImportUser;
	}

	/**
	 * 设置订单导入用户<p>
	 * @param  orderImportUser  订单导入用户<br>
	 */
	public void setOrderImportUser(String orderImportUser)
	{
		this.orderImportUser = orderImportUser;
	}

	/**
	 * 获取订单导入密码<p>
	 * @return  orderImportPassword  订单导入密码<br>
	 */
	public String getOrderImportPassword()
	{
		return orderImportPassword;
	}

	/**
	 * 设置订单导入密码<p>
	 * @param  orderImportPassword  订单导入密码<br>
	 */
	public void setOrderImportPassword(String orderImportPassword)
	{
		this.orderImportPassword = orderImportPassword;
	}

	/**
	 * 获取更新票号用户<p>
	 * @return  orderUpdateTicketNoUser  更新票号用户<br>
	 */
	public String getOrderUpdateTicketNoUser()
	{
		return orderUpdateTicketNoUser;
	}

	/**
	 * 设置更新票号用户<p>
	 * @param  orderUpdateTicketNoUser  更新票号用户<br>
	 */
	public void setOrderUpdateTicketNoUser(String orderUpdateTicketNoUser)
	{
		this.orderUpdateTicketNoUser = orderUpdateTicketNoUser;
	}

	/**
	 * 获取更新票号密码<p>
	 * @return  orderUpdateTicketNoPassword  更新票号密码<br>
	 */
	public String getOrderUpdateTicketNoPassword()
	{
		return orderUpdateTicketNoPassword;
	}

	/**
	 * 设置更新票号密码<p>
	 * @param  orderUpdateTicketNoPassword  更新票号密码<br>
	 */
	public void setOrderUpdateTicketNoPassword(String orderUpdateTicketNoPassword)
	{
		this.orderUpdateTicketNoPassword = orderUpdateTicketNoPassword;
	}

	/**
	 * 获取appKey<p>
	 * @return  appKey  appKey<br>
	 */
	public String getAppKey()
	{
		return appKey;
	}

	/**
	 * 设置appKey<p>
	 * @param  appKey  appKey<br>
	 */
	public void setAppKey(String appKey)
	{
		this.appKey = appKey;
	}

	/**
	 * 获取店铺描述<p>
	 * @return  shopNameDesc  店铺描述<br>
	 */
	public String getShopNameDesc()
	{
		return shopNameDesc;
	}

	/**
	 * 设置店铺描述<p>
	 * @param  shopNameDesc  店铺描述<br>
	 */
	public void setShopNameDesc(String shopNameDesc)
	{
		this.shopNameDesc = shopNameDesc;
	}

	/**
	 * 获取激活开关<p>
	 * @return  active  激活开关<br>
	 */
	public String getActive()
	{
		return active;
	}

	/**
	 * 设置激活开关<p>
	 * @param  active  激活开关<br>
	 */
	public void setActive(String active)
	{
		this.active = active;
	}

	/**
	 * 获取其他配置1<p>
	 * @return  otherConfig1  其他配置1<br>
	 */
	public String getOtherConfig1()
	{
		return otherConfig1;
	}

	/**
	 * 设置其他配置1<p>
	 * @param  otherConfig1  其他配置1<br>
	 */
	public void setOtherConfig1(String otherConfig1)
	{
		this.otherConfig1 = otherConfig1;
	}

	/**
	 * 获取其他配置2<p>
	 * @return  otherConfig2  其他配置2<br>
	 */
	public String getOtherConfig2()
	{
		return otherConfig2;
	}

	/**
	 * 设置其他配置2<p>
	 * @param  otherConfig2  其他配置2<br>
	 */
	public void setOtherConfig2(String otherConfig2)
	{
		this.otherConfig2 = otherConfig2;
	}

	/**
	 * 获取其他配置3<p>
	 * @return  otherConfig3  其他配置3<br>
	 */
	public String getOtherConfig3()
	{
		return otherConfig3;
	}

	/**
	 * 设置其他配置3<p>
	 * @param  otherConfig3  其他配置3<br>
	 */
	public void setOtherConfig3(String otherConfig3)
	{
		this.otherConfig3 = otherConfig3;
	}

	/**
	 * 获取其他配置4<p>
	 * @return  otherConfig4  其他配置4<br>
	 */
	public String getOtherConfig4()
	{
		return otherConfig4;
	}

	/**
	 * 设置其他配置4<p>
	 * @param  otherConfig4  其他配置4<br>
	 */
	public void setOtherConfig4(String otherConfig4)
	{
		this.otherConfig4 = otherConfig4;
	}

	/**
	 * 获取创建时间<p>
	 * @return  createDate  创建时间<br>
	 */
	public String getCreateDate()
	{
		return createDate;
	}

	/**
	 * 设置创建时间<p>
	 * @param  createDate  创建时间<br>
	 */
	public void setCreateDate(String createDate)
	{
		this.createDate = createDate;
	}

	/**
	 * 获取创建人<p>
	 * @return  createBy  创建人<br>
	 */
	public String getCreateBy()
	{
		return createBy;
	}

	/**
	 * 设置创建人<p>
	 * @param  createBy  创建人<br>
	 */
	public void setCreateBy(String createBy)
	{
		this.createBy = createBy;
	}

	/**
	 * 获取最近一次修改时间<p>
	 * @return  modifyDate  最近一次修改时间<br>
	 */
	public String getModifyDate()
	{
		return modifyDate;
	}

	/**
	 * 设置最近一次修改时间<p>
	 * @param  modifyDate  最近一次修改时间<br>
	 */
	public void setModifyDate(String modifyDate)
	{
		this.modifyDate = modifyDate;
	}

	/**
	 * 获取最近一次修改人<p>
	 * @return  modifyBy  最近一次修改人<br>
	 */
	public String getModifyBy()
	{
		return modifyBy;
	}

	/**
	 * 设置最近一次修改人<p>
	 * @param  modifyBy  最近一次修改人<br>
	 */
	public void setModifyBy(String modifyBy)
	{
		this.modifyBy = modifyBy;
	}
	
}
