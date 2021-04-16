package cn.ssq.ticket.system.runnable;

import cn.ssq.ticket.system.entity.OrderOperateLog;
import cn.ssq.ticket.system.entity.Refund;
import cn.ssq.ticket.system.mapper.RefundMapper;
import cn.ssq.ticket.system.service.LogService;
import cn.ssq.ticket.system.util.EtermHttpUtil;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.List;


/**
 * @author Administrator
 *
 */
public class DeletePnr implements Runnable{

	
	private Logger logg = LoggerFactory.getLogger(this.getClass());
	
	private static LogService logService= SpringContextHolder.getBean(LogService.class);

	private static RefundMapper refundMapper = SpringContextHolder.getBean(RefundMapper.class);

	private String pnr;
	
	private String orderNo;

	private String flightNo;

	public DeletePnr(String pnr,String orderNo,String flightNo) {
		this.pnr=pnr;
		this.orderNo=orderNo;
		this.flightNo=flightNo;
	}
	@Override
	public void run() {
        OrderOperateLog log=new OrderOperateLog();
        try {
            Thread.sleep(3000);
            QueryWrapper<Refund> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("RET_NO",orderNo);
            queryWrapper.select("XEPNR_STATUS","RET_NO");
            List<Refund> refundsByQueryWapper = refundMapper.selectList(queryWrapper);
            if (refundsByQueryWapper.size()>0){
                Refund r = refundsByQueryWapper.get(refundsByQueryWapper.size()-1);
                logg.info("第0个"+refundsByQueryWapper.get(0).getXePnrStatus());
                logg.info("第n个"+r.getXePnrStatus());
                if("1".equals(r.getXePnrStatus())){
                    UpdateWrapper<Refund> updateWrapper=new UpdateWrapper<>();
                    updateWrapper.eq("RET_NO",orderNo);
                    Refund uR=new Refund();
                    uR.setXePnrStatus("1");
                    refundMapper.update(uR,updateWrapper);
                    return;
                }
            }
            String param="cmd=Xepnr&pnr="+pnr;
            String cid="apiyjsl3";
            if(flightNo.contains("MU") || flightNo.contains("HU")){
                cid="sz05";
            }
            String cmd=EtermHttpUtil.cmdChose(cid, param);
            logg.info(cmd);



            if(!cmd.contains("ret_value=\"0\"")){
                cmd =EtermHttpUtil.cmdChose(cid, param);
            }
			if(!cmd.contains("ret_value=\"0\"")){
                log.setContent("取消编码失败"+pnr);
            }else {
                log.setContent("取消编码成功"+pnr);
                Refund r=new Refund();
                r.setXePnrStatus("1");
                UpdateWrapper<Refund> updateWrapper=new UpdateWrapper<>();
                updateWrapper.eq("RET_NO",orderNo);
                refundMapper.update(r,updateWrapper);
            }
            log.setRetNo(orderNo);
            log.setOrderNo(orderNo);
            log.setType("订单处理");
			log.setName("SYSTEM");
			logg.info(pnr+":"+cmd);
        } catch (Exception e) {
            logg.info(pnr+"编码删除失败",e);
            log.setContent("取消编码失败"+pnr);
        }
        logService.saveLog(log);
    }
	public static String httpGet(String url){
        //String url = "http://xxxxxxx";  
        HttpGet httpGet = new HttpGet(url); 
        String result = "";  
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse=null;
        try {         
        	httpResponse = httpClient.execute(httpGet);          
            HttpEntity httpEntity = httpResponse.getEntity();  
            InputStream is = null;  
            is = httpEntity.getContent();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "gbk"));
            String line = "";  
            while ((line = reader.readLine()) != null) {  
                result = result + line;  
            }   
        } catch (ConnectException se) {  
            se.printStackTrace(); 
            return httpGet(url);
        }catch (Exception e) {  
        	
            e.printStackTrace();  
        }finally{
        	httpGet.releaseConnection();
        }
        return result;
    }
	
	public static void main(String[] args) {
	    String pnr="JQ7EMJ";
        String param="cmd=Xepnr\\&pnr="+pnr;
        String cid="apiyjsl3";
        String cmd=EtermHttpUtil.cmd(cid, param);
        System.out.println(cmd);
    }

}
