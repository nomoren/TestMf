package cn.ssq.ticket.system.controller;

import cn.ssq.ticket.system.entity.*;
import cn.ssq.ticket.system.runnable.RefundSyncInfo;
import cn.ssq.ticket.system.service.*;
import cn.ssq.ticket.system.util.*;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


/**
 * 文件上传控制层
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/import")
public class ImportController {

	private Logger logg = LoggerFactory.getLogger(this.getClass());

	private String PREFIX = "/modular/system/import/";
	private String PREFIX2 = "/modular/system/checking/";

	@Autowired
	private OrderService orderService;

	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	private ImportServcie importService;

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private RefundService refundSevice;

	@Autowired
	MongoTemplate mongoTemplate;

	@RequestMapping("/index")
	public String index(){
		return PREFIX + "import_index.html";
	}

	@RequestMapping("/zz")
	public String zz(){
		return PREFIX2 + "import_zz.html";
	}

	/**
	 * 易宝支出账单对账
	 * @param file
	 * @param request
	 * @return
	 */
	@PostMapping("/uploadYiBaoOrderChecking")
	@ResponseBody
	public Object uploadYiBaoChecking(@RequestParam("file") MultipartFile file, HttpServletRequest request){
		ResponseResult<Void> result=new ResponseResult<Void>(0, "");
		List<YiBaoPurchChecking>  importResult=new ArrayList<YiBaoPurchChecking>();//对账失败的记录放这里
		Import im=new Import();//本次导入记录
		Integer success=0;//成功数
		Integer fail=0;//失败数
		try {
			ImportExcel ei = new ImportExcel(file, 0,0);
			List<YiBaoPurchChecking> dataList = ei.getDataList(YiBaoPurchChecking.class,null);
			im.setCount(dataList.size()+"");
			String startDate=DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			String endDate=DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			if(dataList.size()>0) {
				if(StringUtils.isNotBlank(dataList.get(0).getDate()) && StringUtils.isNotBlank(dataList.get(dataList.size()-1).getDate())) {
					String checkDateStart=dataList.get(0).getDate();
					String date1=checkDateStart.trim();
					String checkDateEnd=dataList.get(dataList.size()-1).getDate();
					String date2=checkDateEnd.trim();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date sd1=df.parse(date1);
					Date sd2=df.parse(date2);
					if(sd1.before(sd2)) {
						startDate=date1;
						endDate=date2;
					}else {
						endDate=date1;
						startDate=date2;
					}
				}
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate));
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate));
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			}
		
			for(YiBaoPurchChecking checking:dataList) {
				try {
					if(StringUtils.isEmpty(checking.getBussinsNo())){
						continue;
					}
					//根据商户号匹配
					Purchase purchase=purchaseService.getPurchsByTradeNo(checking.getBussinsNo(), startDate, endDate);
					//根据流水号匹配
					if(purchase==null){
						purchase = purchaseService.getPurchsByTradeNo(checking.getTradeNo(), startDate, endDate);
					}
					if(purchase==null) {
						//app支付根据乘客名匹配,否者根据票号
						Passenger passenger=null;
						if(checking.getOrderName().contains("手机客户端")){
							if(StringUtils.isNotBlank(checking.getExtendInfo())) {
								//李传僮,闫心栋,彭星翔
								String  passName= checking.getExtendInfo().split(",")[0];
								QueryWrapper<Passenger> query=new QueryWrapper<Passenger>();
								query.eq("name", passName);
								query.between("print_ticket_date", startDate, endDate);
								List<Passenger> findByQueryWapper = passengerService.findByQueryWapper(query);
								if(findByQueryWapper.size()>0){
									if(findByQueryWapper.size()==1){
										passenger=findByQueryWapper.get(0);
									}else{
										//匹配到多个乘客，获取出票时间与报表时间最近的那一个
										Map<Long,Passenger> map=new HashMap<Long, Passenger>();
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										long completeDate = sdf.parse(checking.getCompleteDate()).getTime();
										for(Passenger p:findByQueryWapper){
											String pDate=p.getPrintTicketDate();
											if(StringUtils.isEmpty(pDate)){
												break;
											}
											long time = sdf.parse(p.getPrintTicketDate()).getTime();
											long diff=time-completeDate;
											map.put(diff, p);
										}
										HashMap<Long, Passenger> sortHashMap = sortHashMap(map);
										Entry<Long, Passenger> next = sortHashMap.entrySet().iterator().next();
										passenger=next.getValue();
									}
								}
							}
						}else{
							if(StringUtils.isNotBlank(checking.getStartTicket())) {
								//String startTicket2=checking.getStartTicket().replace("-", "");
								//尝试根据票号匹配
								QueryWrapper<Passenger> query=new QueryWrapper<Passenger>();
								query.eq("ticket_no", checking.getStartTicket());
								query.between("print_ticket_date", startDate, endDate);
								List<Passenger> findByQueryWapper = passengerService.findByQueryWapper(query);
								if(findByQueryWapper.size()>0){
									passenger=findByQueryWapper.get(0);
								}

							}
						}
						if(passenger!=null) {
							QueryWrapper<Purchase> purchQuery=new QueryWrapper<Purchase>();
							purchQuery.eq("order_id", passenger.getOrderId());
							purchQuery.eq("flag", "0");
							purchQuery.between("print_ticket_date", startDate, endDate);
							List<Purchase> byQueryWapper = purchaseService.getByQueryWapper(purchQuery);
							if(byQueryWapper.size()>0) {
								if(byQueryWapper.size()==1) {
									purchase=byQueryWapper.get(0);
								}else {
									for(Purchase p:byQueryWapper) {
										if(p.getPassengerNames().contains(passenger.getName())) {
											purchase=p;
											break;
										}
									}
								}
							}
						}
					}
					//对账
					if(purchase!=null) {
						Double payAmount = purchase.getPayAmount();
						Double realPay=Double.valueOf(checking.getOut());
						purchase.setRealPay(realPay);
						purchase.setBusinessNo(checking.getBussinsNo());
						if(payAmount.doubleValue()!=realPay.doubleValue()) {
							purchase.setPayAmount(realPay);
							purchase.setProfit(new BigDecimal(purchase.getCustomerAmount()).subtract(new BigDecimal(purchase.getPayAmount())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
						}
						purchaseService.updatePurchase(purchase);
						success++;
						ThreadPoolUtil.execute(new RefundSyncInfo(purchase));
					}else {
						checking.setResult("没有匹配到采购信息");
						importResult.add(checking);
						fail++;
					}
				} catch (Exception e) {
					logg.error(e.getMessage()+"易宝支出账单对账异常"+checking.getBussinsNo(),e);
					checking.setResult("没有匹配到采购信息");
					importResult.add(checking);
					fail++;
					continue;
				}
			}
			im.setFail(fail+"");
			im.setSuccess(success+"");
			String name=ShiroKit.getSubject().getPrincipal().toString();			
			im.setRemark("已处理");
			im.setName(name);
			im.setType("11");//易宝账单对账
			im.setImportDate(new Date());
			importService.insert(im);
			if(importResult.size()>0) {
				importYiBaoPurchCheckingRecord(importResult,im.getImportId());
			}
		} catch (Exception e) {
			im.setName(ShiroKit.getSubject().getPrincipal().toString());
			im.setType("11");
			im.setImportDate(new Date());
			im.setFail(fail+"");
			im.setSuccess(success+"");
			im.setRemark("处理失败，请导入正确的账单");
			importService.insert(im);
			e.printStackTrace();
			logg.error(e.getMessage()+"易宝支出账单对账异常",e);
		}
		return result;
	}

	/**
	 * 易宝退款账单对账
	 * @param file
	 * @param request
	 * @return
	 */
	@PostMapping("/uploadYiBaoRefundChecking")
	@ResponseBody
	public Object uploadYiBaoRefundChecking(@RequestParam("file") MultipartFile file, HttpServletRequest request){
		ResponseResult<Void> result=new ResponseResult<Void>(0, "");
		List<YiBaoRefundChecking>  importResult=new ArrayList<YiBaoRefundChecking>();//对账失败的记录放这里
		Import im=new Import();//本次导入记录
		Integer success=0;//成功数
		Integer fail=0;//失败数
		try {
			ImportExcel ei = new ImportExcel(file, 0,0);
			List<YiBaoRefundChecking> dataList = ei.getDataList(YiBaoRefundChecking.class,null);
			im.setCount(dataList.size()+"");
			String startDate=DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			String endDate=DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			if(dataList.size()>0) {
				if(StringUtils.isNotBlank(dataList.get(0).getRefundDate()) && StringUtils.isNotBlank(dataList.get(dataList.size()-1).getRefundDate())) {
					String checkDateStart=dataList.get(0).getRefundDate();
					String date1=checkDateStart.trim();
					String checkDateEnd=dataList.get(dataList.size()-1).getRefundDate();
					String date2=checkDateEnd.trim();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date sd1=df.parse(date1);
					Date sd2=df.parse(date2);
					if(sd1.before(sd2)) {
						startDate=date1;
						endDate=date2;
					}else {
						endDate=date1;
						startDate=date2;
					}
				}
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate));
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate));
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			}
			Map<String, YiBaoRefundChecking> map=new HashMap<String, YiBaoRefundChecking>();
			for(YiBaoRefundChecking checking:dataList) {
				String bussinsNo=checking.getBussinsNo();
				if(map.containsKey(bussinsNo)) {
					YiBaoRefundChecking originalChecking = map.get(bussinsNo);
					Double alldMoney = Double.valueOf(originalChecking.getRefundMoney());
					Double refundMoney=Double.valueOf(checking.getRefundMoney());
					alldMoney=new BigDecimal(alldMoney).add(new BigDecimal(refundMoney)).doubleValue();
					originalChecking.setRefundMoney(String.valueOf(alldMoney));
					originalChecking.setMerge(true);
					/*int count = originalChecking.getCount();
					count++;
					originalChecking.setCount(count);*/
				}else {
					map.put(bussinsNo,checking);
				}
			}
			Set<Entry<String, YiBaoRefundChecking>> sets=map.entrySet();	
			for(Entry<String, YiBaoRefundChecking> entry:sets){
				String bussinsNo=entry.getKey();
				YiBaoRefundChecking chekcing=entry.getValue();
				//优先根据商户订单号匹配
				QueryWrapper<Refund> refundQuery=new QueryWrapper<Refund>();
				refundQuery.eq("business_no", bussinsNo);
				List<Refund> refundList = refundSevice.getRefundsByQueryWapper(refundQuery);

				if(refundList.size()==0) {
					//根据商户号查不到退单，商户号查询关联的采购单，根据采购单乘客名匹配
					QueryWrapper<Purchase> purchQuery=new QueryWrapper<Purchase>();
					purchQuery.eq("flag", 0);
					purchQuery.eq("business_no", chekcing.getBussinsNo());
					List<Purchase> purchases = purchaseService.getByQueryWapper(purchQuery);
					if(purchases.size()>0) {
						Purchase purchase = purchases.get(0);
						String[] names=purchase.getPassengerNames().split(",");
						for(String name:names) {
							QueryWrapper<Refund> refundQuery3=new QueryWrapper<Refund>();
							refundQuery3.eq("passenger_Name", name);
							refundQuery3.eq("order_id", purchase.getOrderId());
							List<Refund> refunds = refundSevice.getRefundsByQueryWapper(refundQuery3);
							if(refunds.size()>0) {
								refundList.add(refunds.get(0));
							}
						}
					}
				}
				if(refundList.size()==0) {
					//商户号查不到，有票号根据票号匹配
					if(StringUtils.isNotBlank(chekcing.getStartTicket())) {
						String startTicketNo=chekcing.getStartTicket().replace("-", "");
						QueryWrapper<Refund> refundQuery2=new QueryWrapper<Refund>();
						refundQuery2.eq("ticket_no", chekcing.getStartTicket());
						refundList = refundSevice.getRefundsByQueryWapper(refundQuery2);
						if(!chekcing.getStartTicket().equals(chekcing.getEndTicket())){
							if(refundList.size()>0) {
								String retNo = refundList.get(0).getRetNo();
								refundList.clear();
								QueryWrapper<Refund> q=new QueryWrapper<Refund>();
								q.eq("RET_NO", retNo);
								List<Refund> queryRefunds = refundSevice.getRefundsByQueryWapper(q);
								long start=Long.valueOf(startTicketNo);
								long end=Long.valueOf(chekcing.getEndTicket().replace("-", ""));
								for(Refund r:queryRefunds){
									long ticketNo=Long.valueOf(r.getTicketNo().replace("-",""));
									if(ticketNo>=start&&ticketNo<=end){
										refundList.add(r);
									}
								}
							}
						}
					}
				}


				//对账
				if(refundList.size()>0) {
					BigDecimal allMoney=new BigDecimal(chekcing.getRefundMoney());
					BigDecimal refundMoney = allMoney.divide(new BigDecimal(refundList.size()),2, BigDecimal.ROUND_HALF_UP);
					for(Refund refund:refundList) {
						refund.setIsRefund("1");
						refund.setAirRealPrice(refundMoney);
						refundSevice.updateById(refund);
					}
					success++;
				}else {
					chekcing.setResult("没有匹配到退款单");
					importResult.add(chekcing);
					fail++;
				}
			}
			im.setFail(fail+"");
			im.setSuccess(success+"");
			String name=ShiroKit.getSubject().getPrincipal().toString();				
			im.setRemark("已处理");
			im.setName(name);
			im.setType("12");//易宝账单对账
			im.setImportDate(new Date());
			importService.insert(im);
			if(importResult.size()>0) {
				importYiBaoRefundChekcing(importResult,im.getImportId());
			}
		} catch (Exception e) {
			im.setName(ShiroKit.getSubject().getPrincipal().toString());
			im.setType("12");
			im.setImportDate(new Date());
			im.setFail(fail+"");
			im.setSuccess(success+"");
			e.printStackTrace();
			im.setRemark("处理失败，请导入正确的账单");
			importService.insert(im);
			logg.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 支付宝对账
	 * @param file
	 * @param request
	 * @return
	 */
	@PostMapping("/uploadAliPayChecking")
	@ResponseBody
	public Object uploadAliPayChecking(@RequestParam("file") MultipartFile file, HttpServletRequest request){
		ResponseResult<Void> result=new ResponseResult<Void>(0, "");
		List<AliPayChecking>  importResult=new ArrayList<AliPayChecking>();//对账失败的记录放这里
		Import im=new Import();//本次导入记录
		Integer success=0;//成功数
		Integer fail=0;//失败数
		try {
			// 用来保存数据  
			ArrayList<String[]> csvFileList = new ArrayList<String[]>();  
			// 创建CSV读对象 例如:CsvReader(文件路径，分隔符，编码格式);  
			CsvReader reader = new CsvReader(file.getInputStream(), ',', Charset.forName("gbk"));  
			// 跳过表头 如果需要表头的话，这句可以忽略  
			//reader.readHeaders();  
			// 逐行读入数据  
			while (reader.readRecord()) {  
				csvFileList.add(reader.getValues());   
			}  
			reader.close();
			String checkDate="";//账单日期
			//所有账单条目
			List<String[]> list=new ArrayList<>();
			// 遍历读取的CSV文件  
			for (int row = 0; row < csvFileList.size(); row++) {
				if(csvFileList.get(row)[0].trim().startsWith("起始日期")) {
					checkDate=csvFileList.get(row)[0];
				}
				if(csvFileList.get(row)[0].trim().matches("[0-9]+\\w+")) {
					list.add(csvFileList.get(row));
				}
			}
			im.setCount(list.size()+"");
			String startDate=null;
			String endDate=null;
			if(StringUtils.isNotBlank(checkDate)){
				int first=checkDate.indexOf("[");
				int first2=checkDate.indexOf("]");
				int last=checkDate.lastIndexOf("[");
				int last2=checkDate.lastIndexOf("]");
				startDate=checkDate.substring(first+1, first2);
				endDate=checkDate.substring(last+1,last2);
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate));
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				startDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate));
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				endDate=DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
			}
			Map<String, List<AliPayChecking>> map = createAlipyCheckingEntity(list);
			List<AliPayChecking> outList = map.get("out");
			List<AliPayChecking> intList = map.get("in");
			//支出
			for (AliPayChecking outChecking : outList) {
				Purchase purchase = purchaseService.getPurchsByTradeNo(outChecking.getTradeNo().trim(), startDate, endDate);
				if(purchase==null){
					if(StringUtils.isNotBlank(outChecking.getBussinsNo())){						
						purchaseService.getPurchsByTradeNo(outChecking.getBussinsNo().trim(), startDate, endDate);
					}
				}
				if(purchase!=null){
					Double payAmount = purchase.getPayAmount();
					Double realPay=Double.valueOf(outChecking.getMoney());
					purchase.setRealPay(realPay);
					purchase.setBusinessNo(outChecking.getBussinsNo());
					if(payAmount.doubleValue()!=realPay.doubleValue()) {
						purchase.setPayAmount(realPay);
						purchase.setProfit(new BigDecimal(purchase.getCustomerAmount()).subtract(new BigDecimal(purchase.getPayAmount())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					purchaseService.updatePurchase(purchase);
					success++;
					ThreadPoolUtil.execute(new RefundSyncInfo(purchase));
				}else{
					outChecking.setResult("没有匹配到采购信息");
					importResult.add(outChecking);
					fail++;
				}
			}

			//退款
			for (AliPayChecking inChecking : intList) {
				//优先根据商户订单号匹配
				QueryWrapper<Refund> refundQuery=new QueryWrapper<Refund>();
				refundQuery.eq("business_no", inChecking.getBussinsNo());
				List<Refund> refundList = refundSevice.getRefundsByQueryWapper(refundQuery);
				if(refundList.size()==0) {
					//根据商户号都查不到，查询关联的采购单，根据采购单乘客名匹配
					QueryWrapper<Purchase> purchQuery=new QueryWrapper<Purchase>();
					purchQuery.eq("flag", 0);
					purchQuery.eq("business_no", inChecking.getBussinsNo());
					List<Purchase> purchases = purchaseService.getByQueryWapper(purchQuery);
					if(purchases.size()>0) {
						Purchase purchase = purchases.get(0);
						String[] names=purchase.getPassengerNames().split(",");
						for(String name:names) {
							QueryWrapper<Refund> refundQuery3=new QueryWrapper<Refund>();
							refundQuery3.eq("passenger_Name", name);
							refundQuery3.eq("order_id", purchase.getOrderId());
							List<Refund> refunds = refundSevice.getRefundsByQueryWapper(refundQuery3);
							if(refunds.size()>0) {
								refundList.add(refunds.get(0));
							}
						}
					}
				}
				//对账
				if(refundList.size()>0) {
					BigDecimal allMoney=new BigDecimal(inChecking.getMoney());
					BigDecimal refundMoney = allMoney.divide(new BigDecimal(refundList.size()),2, BigDecimal.ROUND_HALF_UP);
					for(Refund refund:refundList) {
						refund.setIsRefund("1");
						refund.setAirRealPrice(refundMoney);
						refundSevice.updateById(refund);
					}
					success++;
				}else {
					inChecking.setResult("没有匹配到退款单");
					importResult.add(inChecking);
					fail++;
				}
			}
			im.setFail(fail+"");
			im.setSuccess(success+"");
			String name=ShiroKit.getSubject().getPrincipal().toString();
			im.setRemark("已处理");
			im.setName(name);
			im.setType("10");//支付宝账单对账
			im.setImportDate(new Date());
			importService.insert(im);
			if(importResult.size()>0) {
				importAliPayCheckingRecord(importResult,im.getImportId());
			}
		} catch (Exception e) {  
			e.printStackTrace();
			im.setName(ShiroKit.getSubject().getPrincipal().toString());
			im.setType("10");
			im.setImportDate(new Date());
			im.setFail(fail+"");
			im.setSuccess(success+"");
			e.printStackTrace();
			im.setRemark("处理失败，请导入正确的账单");
			importService.insert(im);
			logg.error(e.getMessage(),e);
		}  
		return result;
	}

	/**
	 * 采购单导入
	 * @param file
	 * @param request
	 * @return
	 */
	@PostMapping("/uploadOrderReport")
	@ResponseBody
	public Object importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request){
		List<PurchImport> reportList = null;
		Import im=new Import();//本次导入记录
		Integer success=0;//导入成功数
		Integer fail=0;//导入失败数
		ResponseResult<Void> result=new ResponseResult<Void>(0, "");
		List<PurchImport>  importResult=new ArrayList<PurchImport>();//导入失败的记录放这里
		try {
			ImportExcel ei = new ImportExcel(file, 0,0);
			reportList = ei.getDataList(PurchImport.class, null);
			im.setCount(reportList.size()+"");
			for(PurchImport data:reportList){
				String orderNo=data.getOrderNo();
				if(StringUtils.isBlank(orderNo)){
					fail++;
					continue;
				}
				OrderVO orderVo=orderService.selectOrderDetails(orderNo,null,null);
				if(orderVo==null){
					data.setResult("订单不存在！");
					importResult.add(data);
					fail++;
					continue;
				}
				String passengerName=data.getPassengerName();
				String[] names=passengerName.trim().replaceAll("，", ",").split(",");
				List<String> nameList=new ArrayList<String>();//采购单乘客
				for(String name:names){
					nameList.add(name.trim());
				}
				List<String> realList=new ArrayList<String>();//原訂單所有乘客
				for(Passenger p:orderVo.getPassengetList()){
					realList.add(p.getName());
				}
				if(!realList.containsAll(nameList)){
					data.setResult("乘机人姓名不匹配！");
					importResult.add(data);
					fail++;
					continue;
				}
				List<List<String>> lists = purchaseService.getPassengerNames(orderNo);
				boolean exist=false;
				if(lists!=null){
					for(List<String> purchaseNames:lists){
						if(Util.isListEqual(purchaseNames, nameList)){
							data.setResult("采购信息已存在！请手动录入。");
							importResult.add(data);
							fail++;
							exist=true;
							break;
						}
					}
					if(exist){
						continue;
					}
				}
				Purchase purchase=new Purchase();
				if(nameList.size()==realList.size()){
					purchase.setCustomerAmount(Double.valueOf(orderVo.getTotalPrice()));
				}else{
					BigDecimal customerAmount=new BigDecimal(0);
					for(String name:nameList){
						for(Passenger p: orderVo.getPassengetList()){
							if(name.equals(p.getName())){
								customerAmount=customerAmount.add(new BigDecimal(p.getActualPrice()));
								break;
							}
						}
					}
					purchase.setCustomerAmount(customerAmount.doubleValue());
				}
				purchase.setOrderId(orderVo.getOrderId());
				purchase.setOrderNo(orderVo.getOrderNo());
				purchase.setcOrderNo(orderVo.getcOrderNo());
				purchase.setOrderSource(DictUtils.getDictCode("order_source", orderVo.getOrderSource()));
				purchase.setOrderShop(orderVo.getOrderShop());
				purchase.setcAddDate(orderVo.getcAddDate());
				purchase.setFlightDate(orderVo.getFlightDate());
				purchase.setFlag("0");
				if(StringUtils.isNoneBlank(orderVo.getPassengetList().get(0).getPrintTicketBy())){
					purchase.setEmployeeName(orderVo.getPassengetList().get(0).getPrintTicketBy());
					purchase.setPrintTicketDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(orderVo.getPassengetList().get(0).getPrintTicketDate()));
				}else{
					purchase.setEmployeeName(ShiroKit.getLocalName());
					purchase.setPrintTicketDate(new Date());
					List<Passenger> passengetList = orderVo.getPassengetList();
					for(Passenger p:passengetList){
						Passenger nP=new Passenger();
						nP.setPassengerId(p.getPassengerId());
						nP.setPrintTicketBy(purchase.getEmployeeName());
						nP.setPrintTicketDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purchase.getPrintTicketDate()));
						passengerService.updateById(nP);
					}
				}
				purchase.setPayAmount(Double.valueOf(data.getPurchasePrice()));
				String payWay=DictUtils.getDictCode("order_recipt_way", StringUtils.stripToEmpty(data.getPayWay()));
				purchase.setPayWay(payWay);
				String purchaseSite=DictUtils.getDictCode("order_purch_place", StringUtils.stripToEmpty(data.getPurchaseSite()));
				purchase.setSupplier(purchaseSite);
				purchase.setPassengerNames(nameList.toString().replaceAll("\\[|\\]", "").trim());
				purchase.setRemark(data.getRemarks());
				purchase.setTradeNo(data.getTradeNo());
				purchase.setProfit(new BigDecimal(purchase.getCustomerAmount()).subtract(new BigDecimal(purchase.getPayAmount())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
				purchaseService.insertPurchase(purchase);
				success++;
			}
			im.setFail(fail+"");
			im.setSuccess(success+"");
			String name=ShiroKit.getSubject().getPrincipal().toString();
			im.setRemark("已处理");
			im.setName(name);
			im.setType("1");
			im.setImportDate(new Date());
			importService.insert(im);
			if(importResult.size()!=0){
				//记录这些导入错误的到mongo
				this.importRecord(importResult, im.getImportId());
			}
		} catch (Exception e) {
			im.setName(ShiroKit.getSubject().getPrincipal().toString());
			im.setType("1");
			im.setImportDate(new Date());
			im.setFail(fail+"");
			im.setSuccess(success+"");
			e.printStackTrace();
			im.setRemark("处理失败，请导入正确的文件");
			importService.insert(im);
			logg.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 获取导入记录列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/getImportHistory")
	public Object getImportHistory(String type){
		ResponseResult<List<Import>> result=new ResponseResult<List<Import>>();
		try {
			Map<String, Object> map = importService.getList(type);
			result.setCount((int)map.get("count"));
			result.setData((List<Import>)map.get("data"));
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg("查无数据");
		}
		return result;
	}

	/**
	 * 下载采购单导入结果
	 * @param importId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@GetMapping(value="/downloadImportRecord")
	public void downloadImportRecord(@RequestParam(required=true)String importId,HttpServletResponse response){
		try {
			String fileName = "importRecord.xlsx";
			Query query=new Query();
			query.addCriteria(Criteria.where("importId").is(Integer.valueOf(importId)));
			List<PurchImport> list = mongoTemplate.find(query, PurchImport.class, "importResult");
			ExportExcel exportExcel=new ExportExcel("", PurchImport.class,1);
			exportExcel.setDataList(list).write(response, fileName).dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 下载账单对账结果
	 * @param importId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@GetMapping(value="/downloadAliPayCheckingRecord")
	public void downloadAliPayCheckingRecord(@RequestParam(required=true)String importId,int type,HttpServletResponse response){
		try {
			if(type==10) {
				String fileName = "AliPayChecking.csv";
				response.reset();
				response.setContentType("application/octet-stream; charset=utf-8");
				response.setHeader("Content-Disposition", "attachment; filename="+Encodes.urlEncode(fileName));
				// 创建CSV写对象 例如:CsvWriter(文件路径，分隔符，编码格式);
				CsvWriter csvWriter = new CsvWriter(response.getOutputStream(), ',', Charset.forName("gbk"));
				// 写表头
				String[] csvHeaders = { "流水号", "商家订单号", "交易创建时间","付款时间","最近修改时间","交易来源地 ","类型","交易对方","商品名称","金额（元）","收/支 ","交易状态","服务费（元）","成功退款（元）","备注","资金状态","对账结果"};
				csvWriter.writeRecord(csvHeaders);
				// 写内容
				Query query=new Query();
				query.addCriteria(Criteria.where("importId").is(Integer.valueOf(importId)));
				List<AliPayChecking> list = mongoTemplate.find(query, AliPayChecking.class,"aliPayChecking");
				for (AliPayChecking checking:list) {
					String[] csvContent = {checking.getTradeNo(),checking.getBussinsNo(),checking.getCreateDate(),checking.getPayDate(),
							checking.getAlterDate(),checking.getTradeSource(),checking.getType(),checking.getCounterparty(),checking.getName(),
							checking.getMoney()+"",checking.getInOrOut(),checking.getTradeStatus(),checking.getServiceCharge(),checking.getSuccessRefund(),
							checking.getRemark(),checking.getMoneyStatus(),checking.getResult()};
					csvWriter.writeRecord(csvContent,true);
				}
				csvWriter.close();
			}else if(type==11) {
				String fileName = "YiBaoPurchChecking.xlsx";
				Query query=new Query();
				query.addCriteria(Criteria.where("importId").is(Integer.valueOf(importId)));
				List<YiBaoPurchChecking> list = mongoTemplate.find(query, YiBaoPurchChecking.class, "yiBaoPurchChecking");
				ExportExcel exportExcel=new ExportExcel("", YiBaoPurchChecking.class,1);
				exportExcel.setDataList(list).write(response, fileName).dispose();
			}else if(type==12) {
				String fileName = "YiBaoRefundChecking.xlsx";
				Query query=new Query();
				query.addCriteria(Criteria.where("importId").is(Integer.valueOf(importId)));
				List<YiBaoRefundChecking> list = mongoTemplate.find(query, YiBaoRefundChecking.class, "yiBaoRefundChecking");
				ExportExcel exportExcel=new ExportExcel("", YiBaoRefundChecking.class,1);
				exportExcel.setDataList(list).write(response, fileName).dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 记录处理失败的数据，用于下载
	 * @param list
	 * @param importId
	 */
	private void importRecord(List<PurchImport> list,Long importId){
		try {
			for(PurchImport report:list){
				report.setImportId(importId);
			}
			mongoTemplate.insert(list, "importResult");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void importAliPayCheckingRecord(List<AliPayChecking> list,Long importId){
		try {
			for(AliPayChecking report:list){
				report.setImportId(importId);
			}
			mongoTemplate.insert(list,"aliPayChecking");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void importYiBaoPurchCheckingRecord(List<YiBaoPurchChecking> list,Long importId){
		try {
			for(YiBaoPurchChecking report:list){
				report.setImportId(importId);
			}
			mongoTemplate.insert(list,"yiBaoPurchChecking");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void importYiBaoRefundChekcing(List<YiBaoRefundChecking> list,Long importId) {
		try {
			for(YiBaoRefundChecking report:list){
				report.setImportId(importId);
			}
			mongoTemplate.insert(list,"yiBaoRefundChecking");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 支付宝账单数组转换成实体类
	 * @param
	 * @return
	 */
	private Map<String, List<AliPayChecking>> createAlipyCheckingEntity(List<String[]> list){
		Map<String, List<AliPayChecking>> map=new HashMap<String, List<AliPayChecking>>();
		//支出账单
		List<AliPayChecking> purchList=new ArrayList<AliPayChecking>();
		//退款账单
		List<AliPayChecking> refundList=new ArrayList<AliPayChecking>();
		try {
			for(String[] str:list){
				AliPayChecking checking=new AliPayChecking();
				checking.setTradeNo(str[0]);
				checking.setBussinsNo(str[1]);
				checking.setCreateDate(str[2]);
				checking.setPayDate(str[3]);
				checking.setAlterDate(str[4]);
				checking.setTradeSource(str[5]);
				checking.setType(str[6]);
				checking.setCounterparty(str[7]);
				checking.setName(str[8]);
				checking.setMoney(Double.valueOf(str[9]));
				checking.setInOrOut(str[10]);
				checking.setTradeStatus(str[11]);
				checking.setServiceCharge(str[12]);
				checking.setSuccessRefund(str[13]);
				checking.setRemark(str[14]);
				checking.setMoneyStatus(str[15]);
				if("支出".equals(checking.getInOrOut())){
					purchList.add(checking);
				}else{
					refundList.add(checking);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		map.put("out", purchList);
		map.put("in", refundList);
		return map;
	}


	/**
	 * 根据key排序hashmap
	 * @param map
	 * @return
	 */
	public static HashMap<Long,Passenger> sortHashMap(Map<Long,Passenger> map){
		//從HashMap中恢復entry集合，得到全部的鍵值對集合
		Set<Map.Entry<Long,Passenger>> entey = map.entrySet();
		//將Set集合轉為List集合，為了實用工具類的排序方法
		List<Map.Entry<Long,Passenger>> list = new ArrayList<Map.Entry<Long,Passenger>>(entey);
		//使用Collections工具類對list進行排序
		Collections.sort(list, new Comparator<Map.Entry<Long, Passenger>>() {
			@Override
			public int compare(Map.Entry<Long, Passenger> o1, Map.Entry<Long, Passenger> o2) {
				//按照key升敘排列
				return (int)(o1.getKey()-o2.getKey());
			}
		});
		//創建一個HashMap的子類LinkedHashMap集合
		LinkedHashMap<Long, Passenger> linkedHashMap = new LinkedHashMap<Long, Passenger>();
		//將list中的數據存入LinkedHashMap中
		for(Map.Entry<Long,Passenger> entry:list){
			linkedHashMap.put(entry.getKey(),entry.getValue());
		}
		return linkedHashMap;
	}

}

