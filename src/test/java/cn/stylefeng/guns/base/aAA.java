package cn.stylefeng.guns.base;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class aAA {

	public static int j=1;
    private static SimpleDateFormat SDF =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static Queue<String> queue=new LinkedList<String>();


    static {
        queue.offer("13265278717");
        queue.offer("17338146572");
        queue.offer("18145883071");
        queue.offer("18617245717");
    }
	public static void main(String[] args) throws Exception{



//        String payId="newttsqnf,ALIPAY2021032411144949069852;";
//
//        String[] split = payId.split(",");
//        String str = split[1];
//        payId= str.substring(0,str.length()-1);
//        System.out.println(payId);
//        String cardNo="445222199502253515";
//        String bir=cardNo.substring(6,10)+"-"+cardNo.substring(10,12)+"-"+ cardNo.substring(12,14);
//        System.out.println(bir);

        //奇数为男性，偶数为女性

     /*   Date now =new Date();
        Date lastTime = SDF.parse("2021-03-19 10:37:00");
        long diff = lastTime.getTime() - now.getTime();
        long minute = diff/60/1000;
        System.out.println(minute);*/
      /*  String dateReg="^(\\d{4})-([0-1]\\d)-([0-3]\\d)\\s([0-5]\\d):([0-5]\\d):([0-5]\\d)$";
        System.out.println("2021-03-22 20:40:00".matches(dateReg));*/

      /*  String s=" 3.  ZH9366 L   TU02MAR  FUNNNG    1050 1330          E --T2 S ";
        System.out.println(s.matches(".*UN[0-9].*"));*/

      /*  String content ="\u7f3a\u5c11\u53c2\u6570\u6216\u53c2\u6570\u9a8c\u8bc1\u5931\u8d25";

        String result = java.net.URLDecoder.decode(content.toString());
        System.out.println(result);*/
		/*System.out.println("eeeeeeee");
        System.out.println(toUTF8("达到".getBytes()));
        System.out.println(toUTF8("达到"));*/
	}


    public static boolean belongCalendar(String nowTime, String beginTime,String endTime) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar date = Calendar.getInstance();
            date.setTime(sdf.parse(nowTime+" 00:00:01"));

            Calendar begin = Calendar.getInstance();
            begin.setTime(sdf.parse(beginTime.split(" ")[0]+" 00:00:00"));

            Calendar end = Calendar.getInstance();
            end.setTime(sdf.parse(endTime.split(" ")[0]+" 23:59:59"));

            if (date.after(begin) && date.before(end)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
	private static String changeCharSet(
			String str, String newCharset) throws UnsupportedEncodingException {
		if (str != null) {
			// 用默认字符编码解码字符串。
			byte[] bs = str.getBytes();
			// 用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return str;
	}

	/**
	 * 字节数组转化为UTF-8
	 * @param bty
	 * @return
	 */
	public static String toUTF8(byte[] bty){
		try {
			if (bty.length > 0) {
				return new String(bty, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new String(bty);
	}


	/**
	 * 字符串转化为UTF-8
	 * @param str
	 * @return
	 */
	public static String toUTF8(String str){
		String result = str;
		try {
			result = changeCharSet(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}


	public static void doPolicysync(String type) {
		String finaUrl = "http://api.airpp.net/outface/policysync.shtml?businessNo=DGSSQ&signKey=37bdc3546692bfd07eaf251f9709e99c&operateTime=2020-08-1311:19:50&exporttype=FULL";
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpClient =getHttpClient();
			HttpPost httpPost = new HttpPost(finaUrl);
			httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
			response = httpClient.execute(httpPost);
			InputStream instreams = response.getEntity().getContent();
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != response)
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static CloseableHttpClient getHttpClient(){
		String proxyServer="14.152.95.93";
		CredentialsProvider credsProvider = new BasicCredentialsProvider();  
		credsProvider.setCredentials(new AuthScope(proxyServer, 30000),new UsernamePasswordCredentials("ff53719", "ff53719"));
		HttpHost proxy = new HttpHost(proxyServer, 30000 );
		RequestConfig globalConfig = RequestConfig.custom() 
				.setSocketTimeout(50000)
				.setConnectTimeout(50000)
				.setConnectionRequestTimeout(50000).setProxy(proxy)
				.build(); 
		CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCredentialsProvider(credsProvider)  
				.build();

		return build;
	}
}
