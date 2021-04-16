package cn.ssq.ticket.system.util;

import cn.stylefeng.roses.core.util.SpringContextHolder;
import net.sf.json.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

/**
 * 非常准接口工具类
 */
public class FCZutil {

    private static int timeout = 60000;

    private static RedisTemplate<Object,Object> redisTemplate = SpringContextHolder.getBean("redisTemplate");

    public static JSONArray getFlightData(String dep,String arr,String flightDate) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse execute = null;
        HttpGet http = null;
        try {
            String s = Md5Util.stringToMD5(Md5Util.stringToMD5("appid=11002&arr="+arr+"&date="+flightDate+"&dep="+dep+"5fc0809e0bd62"));
            String url = "https://open-al.variflight.com/api/flight?appid=11002&arr="+arr+"&date="+flightDate+"&dep="+dep+"&token=" + s;
            http = new HttpGet(url);
            execute = httpClient.execute(http);
            HttpEntity entity = execute.getEntity();
            String metadata = EntityUtils.toString(entity, "utf-8");
            System.out.println(metadata);
            JSONArray jsonArray = JSONArray.fromObject(metadata);
            redisTemplate.opsForValue().set(dep+"-"+arr+"-"+flightDate,jsonArray);
            return  jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                execute.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                http.abort();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    public static JSONArray getFlightDataByFlightNo(String flightNo,String flightDate,String dep,String arr) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse execute = null;
        HttpGet http = null;
        try {
            String s = Md5Util.stringToMD5(Md5Util.stringToMD5("appid=11002&date="+flightDate+"&fnum="+flightNo+"5fc0809e0bd62"));
            String url = "https://open-al.variflight.com/api/flight?appid=11002&date="+flightDate+"&fnum="+flightNo+"&token=" + s;
            http = new HttpGet(url);
            execute = httpClient.execute(http);
            HttpEntity entity = execute.getEntity();
            String metadata = EntityUtils.toString(entity, "utf-8");
            System.out.println(metadata);
            JSONArray jsonArray = JSONArray.fromObject(metadata);
            redisTemplate.opsForValue().set(flightNo+"-"+dep+"-"+arr+"-"+flightDate,jsonArray);
            return  jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                execute.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                http.abort();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public static CloseableHttpClient getHttpClient() {
        String proxyServer = "14.152.95.93";// ConfigUtils.getParam("uploadProxyIp1");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxyServer, 30000),
                new UsernamePasswordCredentials("ff53719", "ff53719"));
        HttpHost proxy = new HttpHost(proxyServer, 30000);
        RequestConfig globalConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setProxy(proxy).build();
        CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .setDefaultCredentialsProvider(credsProvider).build();

        return build;
    }


    public static void main(String[] args) {

    }

}
