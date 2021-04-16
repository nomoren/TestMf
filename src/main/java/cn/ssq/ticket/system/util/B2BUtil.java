package cn.ssq.ticket.system.util;

import cn.ssq.ticket.system.entity.B2BRefund;
import cn.ssq.ticket.system.entity.B2BRefundDatail;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

public class B2BUtil {

    private static Logger logg = LoggerFactory.getLogger(PPUtil.class);

    private static String B2Busername = "ssq166";//ssq166
    private static String B2Bpwd = "ZHU159168";//zz159168R

    private static String ybusername = "284720694@qq.com";
    private static String ybpwd = "luo031215";

    private static String HXB2Busername = "DGSSQ";
    private static String HXB2Bpwd = "Qq_158166";

    private static String SHEB2Busername = "she262xr";
    private static String SHEB2Bpwd = "xr22529292";

    private static String SHEB2B_A_USER = "she260a";
    private static String SHEB2B_A_PWD = "ty@123456";
    private static String SHEB2B_B_USER = "she314";
    private static String SHEB2B_B_PWD = "08048751bb";
    private static String SHEB2B_C_USER = "SHE385";
    private static String SHEB2B_C_PWD = "SHE385";
    private static String SHEB2B_D_USER = "she262xr";
    private static String SHEB2B_D_PWD = "xr08310013";
    private static String SHEB2B_E_USER = "she335";
    private static String SHEB2B_E_PWD = "shexr08310013";


    public static String post(String url) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        //先解锁订单，如果需要
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClientL();
        try {
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String metadata = EntityUtils.toString(entity);
            System.out.println("sss");
            System.out.println(metadata);
            response.close();
            return metadata;
        } catch (Exception e) {

        } finally {
            httpClient.close();
            httpPost.abort();
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String cmd = "RefundTicket";
        String customersign ="YP10023937016";
        String requestid = UUID.randomUUID().toString().replace("-","");
        System.out.println(requestid);
        String aircode = "AIR_ZH";
        String b2buser = AEScrypt.encryptAES("username=" + B2Busername + "&pwd=" + B2Bpwd, "1ADA9935CCD149D3B62453138EDA9214");
        String pnr = "MCECHH";
        String orderid = "FX2020111948782387";
        String ticketno = "479-2194125582";
        String mobile = "18566154698";
        String reason = "自愿退票";
        String remark = ";自愿";
        String linkman = "John";
        StringBuffer hmacs = new StringBuffer();
        hmacs.append(cmd);
        hmacs.append(customersign);
        hmacs.append(requestid);
        hmacs.append(aircode);
        hmacs.append(b2buser);
        hmacs.append(pnr);
        hmacs.append(orderid);
        hmacs.append(ticketno);
        hmacs.append(reason);
        hmacs.append(mobile);
        hmacs.append(linkman);
        String hmac = hmacs.toString();
        hmac = DigestUtil.hmacSign(hmac, "1ADA9935CCD149D3B62453138EDA9214");
        reason = URLEncoder.encode(reason, "utf-8");
        remark = URLEncoder.encode(remark, "utf-8");
        String param = "cmd=" + cmd + "&customersign=" + customersign + "&requestid=" + requestid + "&aircode=" + aircode + "&b2buser=" + b2buser + "&pnr=" + pnr + "&orderid=" + orderid + "&ticketno=" + ticketno + "&reason=" + reason + "&mobile=" + mobile + "&linkman=" + linkman + "&hmac=" + hmac+"&remark="+remark;
        System.out.println(param);
        String result=sendPost(param);
        String str = URLDecoder.decode(result, "utf-8");
        System.out.println(str);


       /* B2BRefund refund = new B2BRefund();
        refund.setAirCode("AIR_ZH");
        String replace = UUID.randomUUID().toString().replace("-", "");
        refund.setMfOrderNo(replace);
        refund.setPnr("MZ6TY9");
        refund.setPolicyType("TTYZHB2B");
        refund.setReason("非自愿退票");
        refund.setTicketno("479-2194888840");
        refund.setOrderid("FX2021010749397657");
        refund.setRemark(";航班不正常");
        System.out.println(refund);
        String s = refundOrder(refund);
        System.out.println(s);*/



        /*B2BRefundDatail b2BRefundDatail = new B2BRefundDatail();
        b2BRefundDatail.setAirCode("AIR_ZH");
        b2BRefundDatail.setMfOrderNo("11");
        b2BRefundDatail.setPolicyType("ZHB2B");
        b2BRefundDatail.setRequestid("1010");
        String s = B2BUtil.refundOrderDatail(b2BRefundDatail);
        System.out.println(s);*/
    }


    public static String refundOrder(B2BRefund refund) {
        String str = "";
        try {
            String cmd = "RefundTicket";
            String customersign = "YP10023937016";
            String requestid = UUID.randomUUID().toString().replace("-","");
            refund.setRequestid(requestid);
            String aircode =refund.getAirCode();
            String policyType = refund.getPolicyType();
            String b2buser = "";
            if (refund.getAirCode().equals("AIR_ZH")) {
                if (policyType.contains("SHEB2B")) {
                    if (policyType.endsWith("A")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_A_USER + "&pwd=" + SHEB2B_A_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("B")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_B_USER + "&pwd=" + SHEB2B_B_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("C")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_C_USER + "&pwd=" + SHEB2B_C_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("D")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_D_USER + "&pwd=" + SHEB2B_D_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("E")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_E_USER + "&pwd=" + SHEB2B_E_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("F")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_E_USER + "&pwd=" + SHEB2B_E_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    }
                } else {
                    b2buser = AEScrypt.encryptAES(getutf8("username=" + B2Busername + "&pwd=" + B2Bpwd), "1ADA9935CCD149D3B62453138EDA9214");
                }
            }
            String pnr = refund.getPnr();
            String mobile = "18566154698";
            String linkman = "John";
            String orderid = refund.getOrderid();
            String ticketno = refund.getTicketno();
            String reason = refund.getReason();
            String remark = refund.getRemark();

            StringBuffer hmacs = new StringBuffer();
            hmacs.append(cmd);
            hmacs.append(customersign);
            hmacs.append(requestid);
            hmacs.append(aircode);
            hmacs.append(b2buser);
            hmacs.append(pnr);
            hmacs.append(orderid);
            hmacs.append(ticketno);
            hmacs.append(reason);
            hmacs.append(mobile);
            hmacs.append(linkman);
            String hmac = hmacs.toString();
            hmac = DigestUtil.hmacSign(hmac, "1ADA9935CCD149D3B62453138EDA9214");
            orderid = URLEncoder.encode(orderid, "utf-8");
            reason = URLEncoder.encode(reason, "utf-8");
            remark = URLEncoder.encode(remark, "utf-8");
            String param = "cmd=" + cmd + "&customersign=" + customersign + "&requestid=" + requestid + "&aircode=" + aircode + "&b2buser=" + b2buser + "&pnr=" + pnr + "&orderid=" + orderid + "&ticketno=" + ticketno + "&reason=" + reason + "&mobile=" + mobile + "&linkman=" + linkman + "&hmac=" + hmac+"&remark="+remark;
            String result = sendPost(param);
            str = URLDecoder.decode(result, "utf-8");
        } catch (Exception e) {
            logg.error(refund.getMfOrderNo() + "退票异常", e);
        }
        return str;

    }


    public static String refundOrderDatail(B2BRefundDatail refundDatail) {
        String str = "";
        try {
            String cmd = "QueryRefundTicket";
            String customersign = "YP10023937016";
            String requestid = refundDatail.getRequestid();
            String aircode = refundDatail.getAirCode();
            String policyType = refundDatail.getPolicyType();
            String b2buser = "";
            if (refundDatail.getAirCode().equals("AIR_ZH")) {
                if (policyType.contains("SHEB2B")) {
                    if (policyType.endsWith("A")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_A_USER + "&pwd=" + SHEB2B_A_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("B")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_B_USER + "&pwd=" + SHEB2B_B_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("C")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_C_USER + "&pwd=" + SHEB2B_C_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("D")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_D_USER + "&pwd=" + SHEB2B_D_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("E")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_E_USER + "&pwd=" + SHEB2B_E_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    } else if (policyType.endsWith("F")) {
                        b2buser = AEScrypt.encryptAES(getutf8("username=" + SHEB2B_E_USER + "&pwd=" + SHEB2B_E_PWD), "1ADA9935CCD149D3B62453138EDA9214");
                    }
                } else {
                    b2buser = AEScrypt.encryptAES(getutf8("username=" + B2Busername + "&pwd=" + B2Bpwd), "1ADA9935CCD149D3B62453138EDA9214");
                }
            }

            StringBuffer hmacs = new StringBuffer();
            hmacs.append(cmd);
            hmacs.append(customersign);
            hmacs.append(requestid);
            hmacs.append(aircode);
            hmacs.append(b2buser);
            String hmac = hmacs.toString();
            hmac = DigestUtil.hmacSign(hmac, "1ADA9935CCD149D3B62453138EDA9214");

            String param = "cmd=" + cmd + "&customersign=" + customersign + "&requestid=" + requestid + "&aircode=" + aircode + "&b2buser=" + b2buser + "&hmac=" + hmac;
            String result = sendPost(param);
            str = URLDecoder.decode(result, "utf-8");
        } catch (Exception e) {
            logg.error(refundDatail.getMfOrderNo() + "查询退票异常", e);
        }
        return str;

    }


    public static String getutf8(String str) {
        try {
            str = new String(str.getBytes("gbk"), "utf-8");
            return str;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }


    public static String sendPost(String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
           // String encode = URLEncoder.encode("https://airt.yeepay.com/air-ticket-api/airb2b/command.action?" + param, "utf-8");
            URL realUrl = new URL("https://airt.yeepay.com/air-ticket-api/airb2b/command.action");
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
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

    public static CloseableHttpClient getHttpClientL() {
        RequestConfig globalConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .build();
        CloseableHttpClient build = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .build();

        return build;
    }


}
