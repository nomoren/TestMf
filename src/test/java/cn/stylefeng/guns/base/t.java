package cn.stylefeng.guns.base;

import cn.ssq.ticket.system.entity.Order;
import cn.ssq.ticket.system.util.SendQQMsgUtil;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class t {
    public static  Map<String,String> KY_ZH = getKYZH();


    public static  Map<String,String> getKYZH(){
        Map<String,String> KY_ZH=new HashMap<>();
        try {
            File file=new File("D:\\KY-ZH.txt");
            List<String> list = FileUtils.readLines(file, "UTF-8");
            for (String s : list) {
                if(StringUtils.isNotEmpty(s)){
                    String[] split = s.split(",");
                    KY_ZH.put(split[0],split[1]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        KY_ZH.forEach((k,v)->{
            System.out.println(k+","+v);
        });
        return KY_ZH;

    }

	public static void main(String[] args) throws UnsupportedEncodingException {
        getKYZH();
        //String url="https://www.tuniu.cn/restful/login/logon";
		/*String map = getMap(url, null);
		JSONObject data = JSONObject.fromObject(Base64Util.getFromBase64(map));
		System.out.println(data.getJSONObject("data"));*/
		/*MongoClient mongoClient =new MongoClient("192.168.1.177",27000);  
		 MongoDatabase database = mongoClient.getDatabase("db4"); 
		MongoCollection collection = database.getCollection("airLine");
		BasicDBObject query = new BasicDBObject();
		query.put("airlineCode", "CA");
		 MongoCursor<Document> cursor = collection.find(query).iterator();
		String next = cursor.next().toJson().toString();
		System.out.println(next);*/
	}


    public static void tt(Order order) {
        order.setOrderNo("aaa");
    }

	public static String getMap(String url,Map<String,String> map) throws UnsupportedEncodingException{
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet get = new HttpGet(url+"?"+"eyJyIjowLjYxMTMxNjI3NTQ5NDE1NywiaWQiOjczNTA4NDgzMywidHlwZSI6MSwibWV0aG9kIjoiUE9TVCIsInN5c0ZsYWciOiJBT1AiLCJ1cmwiOiIvYW9wL25iL29yZGVyL2Zyb250L3RpY2tldC9kZXRhaWwifQ==");
			get.addHeader("accept-encoding", "gzip, deflate");
			get.addHeader("accept-language", "zh-CN,zh");
			get.addHeader("cache-control", "no-cache");
			get.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			get.addHeader("Accept", "text/plain, */*");
			get.addHeader("cookie", "version_page=20191101105833; JSESSIONIDNB=F8C317151647B9840FD15D549876D50C8aa8b2a06e10ad7e016e4f61366463e9; JSESSIONIDBB=F8C317151647B9840FD15D549876D50C8aa8b2a06e10ad7e016e4f61366463e9; lang=zh-CN; _pk_id.3.2a8b=1971e0e37c810423.1573197836.9.1573533902.1573533902.; _pk_ses.3.2a8b=*");
			get.addHeader("pragma", "no-cache");							   
			get.addHeader("x-requested-with", "XMLHttpRequest");
			get.addHeader("referer", "https://www.tuniu.cn/nbooking/main.html");
			get.addHeader("Connection", "Keep-Alive");    
			get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");
			response = httpClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK){				
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity,"UTF-8");
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				httpClient.close();
				if(response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}



	public static String doPost(String url){

		String result = null;
		// 获取httpclient
		CloseableHttpClient httpclient = HttpClients.custom()
				.build();
		CloseableHttpResponse response = null;
		try {
			//创建post请求
			HttpPost httpPost = new HttpPost(url);



			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(2000).setConnectTimeout(2000).build();  
			httpPost.setConfig(requestConfig); 
			httpPost.addHeader("Accept-Encoding", "gzip, deflate");
			httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
			httpPost.addHeader("Accept", "application/json, text/plain, */*");
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpPost.addHeader("Connection", "keep-alive");
			httpPost.addHeader("Host", "jpebook.ly.com");
			httpPost.addHeader("Origin","http://jpebook.ly.com");
			httpPost.addHeader("cookie", "_dx_uzZo5y=03d6a5c63b44a0c873d73725e91c5154faf97607ef9cd9b444987c6b1c6e1f15e32635c6; _dx_app_bc4b3ca6ae27747981b43e9f4a6aa769=5dfb29a1SCeqMz2zf982Xvy7aRKzQdzJF5Qjpvj1; _dx_captcha_vid=D21D5107D6B72B62646121D297EB3CE102D90ED34DDCBAC040A13652D018015FBB43051BA7105DC00A75BC2C217997E7FC29FE0E6A66F9F287B3F8985CA7980FBF4D8AFB75A3E6F8FAB818FD28B3FA89; .AspNetCore.Cookies=CfDJ8FJwBssWzBRKtPl5G0Zn1Pc3h9Z48NAflex8LkT6hhJthVWTvt0eMuvDQVUUKVthyvFZRPSk2zm9WEi2lAZI6LXo6Jbghr4wPX_3ev0jiGW6aLjGB7pCr8bteYIfX_fnjhLtMEaIGYWYkmvwjaqRphELU-cvpRLN9SjlqfunI1yfqJQ5rDQi4P5vyS5Pnidar41gr46Aiclpm2UQ9eixeZ2x_aucER8kdQ-JcRirIaiQ4RkrHaw6DyI4Clb_619j3lCpECDW4YindV6nU7MmfxWa0kPnd7wq2aq6_MvHbPBh");
			httpPost.addHeader("Pragma", "no-cache");
			//httpPost.addHeader("x-requested-with", "XMLHttpRequest");
			httpPost.addHeader("Referer", "http://jpebook.ly.com/suppliersharing/SupplierOrder");
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");

	
			JSONObject param=new JSONObject();
			param.accumulate("orderSerialNo", "ONGFT8ESD10MFB107806");
			httpPost.setHeader("Content-Type", "application/json");
			StringEntity se = new StringEntity(param.toString(), "utf-8");
            httpPost.setEntity(se);
			
			response = httpclient.execute(httpPost);

			// 得到响应信息
			int statusCode = response.getStatusLine().getStatusCode();
			// 判断响应信息是否正确
			if (statusCode != HttpStatus.SC_OK) {
				// 终止并抛出异常
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			/*Header[] headers = response.getHeaders("set-cookie");
			System.out.println(headers[0].getValue());*/
			HttpEntity entity = response.getEntity();	        
			result = EntityUtils.toString(entity, "UTF-8");
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			//关闭所有资源连接
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;

	}






	@Test
	public void tt(){
		HttpClient httpClient=HttpClients.createDefault();
		HttpGet get=new HttpGet("http://fuwu.qunar.com/orderadmin/unlockorder/unlock?orderNo=rnf191213091606294001&domain=rnf.trade.qunar.com");
		String qnrCookie = "QN1=?; _i=?; QN99=5153; fid=?; QN601=251d1f1c782962c380784d5e8ed88165; QN29=aefc0ddc55724943871a041d9e1216a2; QN300=auto_4e0d874a; QunarGlobal=10.86.213.151_-3028d076_16eb18206b0_-2aec|1574936315378; QN269=?; QN48=?; QN43=6; QN42=18566235767; _q=U.ypysqab5101; _t=?; csrfToken=?; _s=?; _v=?; _vi=?; Hm_lvt_1e0dbf0beefb08ca0c09587ffacf3da1=1574731517,1574761092,1575450736,1576204281; QN201=c2VydmljZTExLnFjYy5xdW5hci5jb20%3D; bathe=4f51f4459c68e6bab7398429e9ae473acd07a599799d67d0f2c82202a7e5b1ba5c93c617092e49fe89f6ff7d0038acb6; _mdp=A64F58EFB5DE4842661AA512784DFD57; Hm_lpvt_1e0dbf0beefb08ca0c09587ffacf3da1=1576205205; _f_rnf=1; JSESSIONID=?";
		get.setHeader("Cookie",qnrCookie);
		try {
			HttpResponse execute = httpClient.execute(get);
			HttpEntity entity = execute.getEntity();
			String result = EntityUtils.toString(entity,"UTF-8");
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/**
	 * 
	 * 
	QN1=0000198027c41a9edd703b00; _i=VInJOyhjphJCjptxZ84Ihi5RZH3q; QN99=5153; fid=86db0d7b-413d-48b6-bac1-6c23e774064d; QN601=251d1f1c782962c380784d5e8ed88165; QN29=aefc0ddc55724943871a041d9e1216a2; QN300=auto_4e0d874a; QunarGlobal=10.86.213.151_-3028d076_16eb18206b0_-2aec|1574936315378; QN269=CE368C02EE2611E9864EFA163EF0DCE5; QN48=9fc376fe-91a0-4e03-8375-3d47ec6461ff; QN43=6; QN42=18566235767; _q=U.ypysqab5101; _t=26378466; csrfToken=asxzzhjvtgXpbytFiqOSw8sUQwosXzOb; _s=s_7AVH37OVPWUCXFWC433AQCZM44; _v=X-HsLQ4VijG619z61Low5OEMXdSepw-dd75IrCVx1ZhG71q9uFe1N8DAvkGcFiZujXCiyDzylXfYrTHKiRpFwJFRhKiWfK5mWpbx-s-Zp6b4Ndz30aRB-odgT71yq-eHfXutVI1y_dG2gwKq73fvT63OZIL52d5Wim9eIQNxxqFN; _vi=AOcI1vlwZS6wRpmVhowj6M9ZXDY6j-7JIuIaZ6Z_Cj9z8HtpmAYmOn5yOpbSUqZ0HLERXnBbJLBnVgqDEAK_o2BfvrE-ydBMqZfNx9VzynPuPfkrQqNRZVGJMhVImfEbKmB-obByBB59a4Pz7J8o_hCKeM5iaGiRE8mjVNmhrrW0; _uf=ypysqab5101; QN238=zh_cn; Hm_lvt_1e0dbf0beefb08ca0c09587ffacf3da1=1574731517,1574761092,1575450736,1576204281; QN201=c2VydmljZTExLnFjYy5xdW5hci5jb20%3D; bathe=4f51f4459c68e6bab7398429e9ae473acd07a599799d67d0f2c82202a7e5b1ba5c93c617092e49fe89f6ff7d0038acb6; _mdp=A64F58EFB5DE4842661AA512784DFD57; Hm_lpvt_1e0dbf0beefb08ca0c09587ffacf3da1=1576205205; _f_rnf=1; JSESSIONID=1D47FF58040A61F92EC13545CCF6BD8B

	 * 
	 * 
	 * @throws Exception
	 */

	@Test
	public void t() throws Exception{
	
	}
	@Test
	public void t2(){
		//735084833
		//eyJyIjowLjYxMTMxNjI3NTQ5NDE1NywiaWQiOiIxNTM5NjI1NTYiLCJ0eXBlIjoxLCJtZXRob2QiOiJQT1NUIiwic3lzRmxhZyI6IkFPUCIsInVybCI6Ii9hb3AvbmIvb3JkZXIvZnJvbnQvdGlja2V0L2RldGFpbCJ9
		//System.out.println(Base64Util.getFromBase64("eyJyIjowLjYxMTMxNjI3NTQ5NDE1NywiaWQiOiIxNTM5NjI1NTYiLCJ0eXBlIjoxLCJtZXRob2QiOiJQT1NUIiwic3lzRmxhZyI6IkFPUCIsInVybCI6Ii9hb3AvbmIvb3JkZXIvZnJvbnQvdGlja2V0L2RldGFpbCJ9"));
		//String jsonParam=String.format("{\"r\":0.611316275494157,\"id\":%s,\"type\":1,\"method\":\"POST\",\"sysFlag\":\"AOP\",\"url\":\"/aop/nb/order/front/ticket/detail\"}", "121231");
		/*String doPost = doPost("https://www.tuniu.cn/restful/login/logon");
		System.out.println(Base64Util.getFromBase64(doPost));
		System.out.println(doPost);*/
		//TuniuOrderService.getTicketNoByOid("1861375422");
	}




	@Test
	public void t4() throws UnsupportedEncodingException{

		String mes="...";
		SendQQMsgUtil.send(mes);
		
	}




}

