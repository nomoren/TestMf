package cn.ssq.ticket.system.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.DocumentException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;


public class EtermHttpUtil {
	
	public static String httpGet(String url){
        HttpGet httpGet = new HttpGet(url); 
        String result = "";  
        HttpClient httpClient = HttpClients.createDefault();  
        try {         
            HttpResponse httpResponse = httpClient.execute(httpGet);          
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
        }catch (Exception e) {  
            e.printStackTrace();  
        }     
        return result;
    }


    public static String cmdChose(String uid,String qf)  {
        String url1 = "http://14.215.118.230:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;
        if("sz05".equals(uid)){
            url1 = "http://14.152.95.93:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;
        }
        String res1 =  httpGet(url1);
        return res1;
    }
	
	public static String cmd(String uid,String qf)  {
    	String url1 = "http://14.215.118.230:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;
    	String res1 =  httpGet(url1);
    	return res1;
	}

	public static String cmd2(String uid,String qf)  {
    	String url1 = "http://14.152.95.93:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;
    	String res1 =  httpGet(url1);
    	return res1;
	}
	
	public static void main(String[] args) throws DocumentException {
		String pnr="KNL05G";
		//String param="cmd=rt_parse&pnr="+pnr;
		String param="cmd=Xepnr&pnr="+pnr;
		//String param="cmd=rt_parse&pnr="+pnr;
		//String param="cmd=PAT&pnr="+pnr;
		
		String RTCmd = EtermHttpUtil.cmd("apiyjsl3", param);
		System.out.println(RTCmd);
	
	}
	
	
	/*String company="深圳";getAdult  getCabin()
	String params1 = "cmd=AV&start_city=CAN&arrive_city=JJN&date=2020-07-04";
	String cmd = cmd("apiyjsl3", params1);
	Map<String, Map<String, String>> data=new HashMap<String, Map<String,String>>();
	try {
		if(cmd.contains("air_info ret_value")){
			Document doc = DocumentHelper.parseText(cmd);
			Element rootElement = doc.getRootElement();
			if("1".equals(rootElement.attributeValue("ret_value"))){
				List<Element> flightElementList = rootElement.elements("airline");
				for(Element e:flightElementList){
					String flightNo=e.attributeValue("line_number");
					flightNo=flightNo.replace("*", "").trim();
					String gs=e.attributeValue("company");
					if(!gs.contains(company)){
						continue;
					}
					Map<String, String> map=new HashMap<String, String>();
					List<Element> elements = e.elements("c");
					for(Element c:elements){
						String cabin=c.attributeValue("b");
						String seat=c.attributeValue("s");
						map.put(cabin, seat);
					}
					data.put(flightNo, map);
				}
			}
		}
		Map<String, String> map = data.get("ZH9835");
		map.forEach((k,v)->{
			System.out.println(k+":"+v);
		});
	} catch (Exception e) {
		// TODO: handle exception
	}*/
	
	
	
}
