package cn.ssq.ticket.system.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;






/**
 * 追位
 * 
 * @author Administrator
 * 
 */
public class CabinUtil {

	private static final Logger logger = Logger.getLogger(CabinUtil.class);

	public static String httpGet(String url){
        //String url = "http://xxxxxxx";  
        HttpGet httpGet = new HttpGet(url); 
        String result = "";  
       CloseableHttpClient httpClient = HttpClients.createDefault();  
       CloseableHttpResponse httpResponse =null;       
        try {
        	httpResponse = httpClient.execute(httpGet);          
            HttpEntity httpEntity = httpResponse.getEntity();  
            //String result = EntityUtils.toString(httpEntity, "utf-8");  
            InputStream is = null;  
            is = httpEntity.getContent();  
           // BufferedReader reader = new BufferedReader(new InputStreamReader(is));  
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "gbk"));

            String line = "";  
            while ((line = reader.readLine()) != null) {  
                result = result + line;  
            }  
            //System.out.println(result);  
        } catch (ConnectException se) {  
            se.printStackTrace(); 
//            Thread.sleep(1);
            return httpGet(url);
        }catch (Exception e) {  
            e.printStackTrace();  
        }finally{
        	try {
        		httpClient.close();
        		httpGet.abort();
        		httpResponse.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
        }
        return result;
    }
	
	public static String avhCmd(String uid,String qf)  {
    	String url1 = "http://14.215.118.230:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;

    	String res1 =  httpGet(url1);
    	return res1;
    	
	}
	
	private static int sleepTime = 20000;
	
	
	public static void main(String[] arge) {}
}

