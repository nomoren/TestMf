package cn.ssq.ticket.system.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SendQQMsgUtil {

	private static String URL="http://14.215.118.230:919/SendIM.do?RobotQQ=1010932962&Key=ff53719&QQ=992587712&Message=";
		
	private static String QURL="http://14.215.118.230:919/SendDiscussionIM.do?RobotQQ=123456&Key=123456&GroupId=958271461&Message=";
	
	

	public static void main(String[] args) throws UnsupportedEncodingException {
		String encode = URLEncoder.encode("@登高 ", "utf-8");
		SendQQMsgUtil.send2Qun(encode,"912704517");
	}
	
	public static void send2Qun(String message,String qq){
		CloseableHttpResponse response =null;
		CloseableHttpClient createDefault = HttpClients.createDefault();
		try {
			String URL="http://14.215.118.230:919/SendClusterIM.do?RobotQQ=1010932962&Key=ff53719&GroupId="+qq+"&Message="+message;
			HttpGet get=new HttpGet(URL);
			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(5000).setConnectTimeout(5000).build();  
			get.setConfig(requestConfig); 
			response = createDefault.execute(get);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				createDefault.close();
			} catch (IOException e1) {
			}
		}
	}
	
	public static void send2(String message,String qq){
		CloseableHttpResponse response =null;
		CloseableHttpClient createDefault = HttpClients.createDefault();
		try {
			String URL="http://14.215.118.230:919/SendIM.do?RobotQQ=1010932962&Key=ff53719&QQ="+qq+"&Message=";
			HttpGet get=new HttpGet(URL+message);
			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(5000).setConnectTimeout(5000).build();  
			get.setConfig(requestConfig); 
			response = createDefault.execute(get);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				createDefault.close();
			} catch (IOException e) {
				
			}
		}
	}

	
	
	public static void send(String message){
		CloseableHttpResponse response =null;
		CloseableHttpClient createDefault = HttpClients.createDefault();
		try {
			HttpGet get=new HttpGet(URL+message);
			RequestConfig requestConfig = RequestConfig.custom()  
					.setSocketTimeout(5000).setConnectTimeout(5000).build();  
			get.setConfig(requestConfig); 
			response = createDefault.execute(get);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				createDefault.close();
			} catch (IOException e) {
				
			}
		}
	}
}
