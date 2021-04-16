package cn.ssq.ticket.system.entity.importBean.TTsBean;

/** 
 * 去哪儿乘客VO
 * 
 * @version 1.0<br>
 * @author Administrator<br>
 * @date 2013-4-5<br>
 */
public class QnrPassengerVO {

	/**
	 * 乘机人姓名
	 */
	private String name;
	
	/**
	 * 乘机人类型 0为成人,1为儿童
	 */
	private String ageType;
	
	/**
	 * 证件类型，
	 * NI=身份证
	 * PP=护照
	 * ID=其他
	 * HX=回乡证
	 * TB=台胞证
	 * GA=港澳通行证
	 * HY=国际海员证
	 */
	private String cardType;
	
	/**
	 * 实际最终销售价
	 */
	private String price;
	
	/**
	 * 价格类型
	 */
	private String priceType;
	
	/**
	 * 证件号码
	 */
	private String cardNum;
	
	/**
	 * 票号
	 */
	private String eticketNum;
	
	/**
	 * 保险份数
	 */
	private String insuranceCount;
	
	/**
	 * 保险公司
	 */
	private String bxSource;
	
	/**
	 * 保险产品名称
	 */
	private String bxName;
	
	/**
	 * 保险单号
	 */
	private String insuranceNo;
	
	/**
	 * 仅往返订单显示，第一程或第二程
	 */
	private String bxFlight;
	
	/**
	 * 保险状态
	 */
	private String bxStatus;
	
	/**
	 * 出票时间
	 */
	private String ticketTime;
	
	/**
	 * 出生日期
	 */
	private String birthday;
	
	/**
	 * 性别
	 */
	private String gender;

//	/**
//	 * 获取乘机人id<p>
//	 * @return  id  乘机人id<br>
//	 */
//	public String getId()
//	{
//		return id;
//	}
//
//	/**
//	 * 设置乘机人id<p>
//	 * @param  id  乘机人id<br>
//	 */
//	public void setId(String id)
//	{
//		this.id = id;
//	}

	/**
	 * 获取乘机人姓名<p>
	 * @return  name  乘机人姓名<br>
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 设置乘机人姓名<p>
	 * @param  name  乘机人姓名<br>
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 获取乘机人类型0为成人1为儿童<p>
	 * @return  ageType  乘机人类型0为成人1为儿童<br>
	 */
	public String getAgeType()
	{
		return ageType;
	}

	/**
	 * 设置乘机人类型0为成人1为儿童<p>
	 * @param  ageType  乘机人类型0为成人1为儿童<br>
	 */
	public void setAgeType(String ageType)
	{
		this.ageType = ageType;
	}

	/**
	 * 获取证件类型<p>
	 * @return  cardType  证件类型，NI=身份证PP=护照ID=其他HX=回乡证TB=台胞证GA=港澳通行证HY=国际海员证<br>
	 */
	public String getCardType()
	{
		return cardType;
	}

	/**
	 * 设置证件类型<p>
	 * @param  cardType  证件类型，NI=身份证PP=护照ID=其他HX=回乡证TB=台胞证GA=港澳通行证HY=国际海员证<br>
	 */
	public void setCardType(String cardType)
	{
		this.cardType = cardType;
	}

	/**
	 * 获取证件号码<p>
	 * @return  cardNum  证件号码<br>
	 */
	public String getCardNum()
	{
		return cardNum;
	}

	/**
	 * 设置证件号码<p>
	 * @param  cardNum  证件号码<br>
	 */
	public void setCardNum(String cardNum)
	{
		this.cardNum = cardNum;
	}

	/**
	 * 获取销售价<p>
	 * @return  price  销售价<br>
	 */
	public String getPrice()
	{
		return price;
	}

	/**
	 * 设置销售价<p>
	 * @param  price  销售价<br>
	 */
	public void setPrice(String price)
	{
		this.price = price;
	}

	/**
	 * 获取价格类型<p>
	 * @return  priceType  价格类型<br>
	 */
	public String getPriceType()
	{
		return priceType;
	}

	/**
	 * 设置价格类型<p>
	 * @param  priceType  价格类型<br>
	 */
	public void setPriceType(String priceType)
	{
		this.priceType = priceType;
	}

	/**
	 * 获取票号<p>
	 * @return  eticketNum  票号<br>
	 */
	public String getEticketNum()
	{
		return eticketNum;
	}

	/**
	 * 设置票号<p>
	 * @param  eticketNum  票号<br>
	 */
	public void setEticketNum(String eticketNum)
	{
		this.eticketNum = eticketNum;
	}

	/**
	 * 获取保险数量<p>
	 * @return  insuranceCount  保险数量<br>
	 */
	public String getInsuranceCount()
	{
		return insuranceCount;
	}

	/**
	 * 设置保险数量<p>
	 * @param  insuranceCount  保险数量<br>
	 */
	public void setInsuranceCount(String insuranceCount)
	{
		this.insuranceCount = insuranceCount;
	}

	/**
	 * 获取保险公司<p>
	 * @return  bxSource  保险公司<br>
	 */
	public String getBxSource()
	{
		return bxSource;
	}

	/**
	 * 设置保险公司<p>
	 * @param  bxSource  保险公司<br>
	 */
	public void setBxSource(String bxSource)
	{
		this.bxSource = bxSource;
	}

	/**
	 * 获取保险产品名称<p>
	 * @return  bxName  保险产品名称<br>
	 */
	public String getBxName()
	{
		return bxName;
	}

	/**
	 * 设置保险产品名称<p>
	 * @param  bxName  保险产品名称<br>
	 */
	public void setBxName(String bxName)
	{
		this.bxName = bxName;
	}
	
	/**
	 * 获取保险单号<p>
	 * @return  insuranceNo  保险单号<br>
	 */
	public String getInsuranceNo()
	{
		return insuranceNo;
	}

	/**
	 * 设置保险单号<p>
	 * @param  insuranceNo  保险单号<br>
	 */
	public void setInsuranceNo(String insuranceNo)
	{
		this.insuranceNo = insuranceNo;
	}

	/**
	 * 获取保险状态<p>
	 * @return  bxStatus  保险状态<br>
	 */
	public String getBxStatus()
	{
		return bxStatus;
	}

	/**
	 * 设置保险状态<p>
	 * @param  bxStatus  保险状态<br>
	 */
	public void setBxStatus(String bxStatus)
	{
		this.bxStatus = bxStatus;
	}

	public String getBxFlight() {
		return bxFlight;
	}

	public void setBxFlight(String bxFlight) {
		this.bxFlight = bxFlight;
	}

	/**
	 * 获取出票时间<p>
	 * @return  ticketTime  出票时间<br>
	 */
	public String getTicketTime()
	{
		return ticketTime;
	}

	/**
	 * 设置出票时间<p>
	 * @param  ticketTime  出票时间<br>
	 */
	public void setTicketTime(String ticketTime)
	{
		this.ticketTime = ticketTime;
	}

	/**
	 * 获取出生日期<p>
	 * @return  birthday  出生日期<br>
	 */
	public String getBirthday()
	{
		return birthday;
	}

	/**
	 * 设置出生日期<p>
	 * @param  birthday  出生日期<br>
	 */
	public void setBirthday(String birthday)
	{
		this.birthday = birthday;
	}

	/**
	 * 获取性别<p>
	 * @return  gender  性别<br>
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 * 设置性别<p>
	 * @param  gender  性别<br>
	 */
	public void setGender(String gender)
	{
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "QnrPassengerVO [name=" + name + ", ageType=" + ageType
				+ ", cardType=" + cardType + ", price=" + price
				+ ", priceType=" + priceType + ", cardNum=" + cardNum
				+ ", eticketNum=" + eticketNum + ", insuranceCount="
				+ insuranceCount + ", bxSource=" + bxSource + ", bxName="
				+ bxName + ", insuranceNo=" + insuranceNo + ", bxFlight="
				+ bxFlight + ", bxStatus=" + bxStatus + ", ticketTime="
				+ ticketTime + ", birthday=" + birthday + ", gender=" + gender
				+ "]";
	}

}
