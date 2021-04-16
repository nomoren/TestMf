package cn.ssq.ticket.system.util;

import action.RsaEnc;
import cn.ssq.ticket.system.entity.pp.HuiFuPay;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HuiFuUtil {

	private static Logger logger = LoggerFactory.getLogger(HuiFuUtil.class);

	public static final String merId="874027";
	public static final String payUsrId="ssq001";
    public static final String Version = "10";
    public static final String PartnerCode = "F0";
    public static final String key = "123456";

	public static final String passWord="654321";//hB2QKcehD7Ijyf1oI7hs4Q==
	public static final String merKeyUrl="D:\\HUIFU\\PgPubk.key";

	public static final String url="https://mas.chinapnr.com/gar/entry.do";

	public static final String B2BRETURL="http://14.152.95.93:9111/pp/notic/payNotifyHuiFuB2B";
    public static final String b2burl = "http://airpay.chinapnr.com:8034";
    public static int readSize = 1024 * 4;
    private static String B2Busername = "ssq166";
    private static String B2Bpwd = "SSQCAN421";
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

	public static String pay(HuiFuPay HFpay) {
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient=HttpClients.createDefault();
		try {
			List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
			parameterList.add(new BasicNameValuePair("Version",HFpay.getVersion()));
			parameterList.add(new BasicNameValuePair("CmdId",HFpay.getCmdId()));
			parameterList.add(new BasicNameValuePair("MerId",HFpay.getMerId()));
			parameterList.add(new BasicNameValuePair("OrdId",HFpay.getOrdId()));
			parameterList.add(new BasicNameValuePair("OrdAmt",HFpay.getOrdAmt()));
			parameterList.add(new BasicNameValuePair("RetUrl",HFpay.getRetUrl()));
			parameterList.add(new BasicNameValuePair("EtclientFlag",HFpay.getEtclientFlag()));
			parameterList.add(new BasicNameValuePair("EtclientUsrId",HFpay.getEtclientUsrId()));
			parameterList.add(new BasicNameValuePair("PayInfo",HFpay.getPayInfo()));
			parameterList.add(new BasicNameValuePair("TrueGateId",HFpay.getTrueGateId()));
			parameterList.add(new BasicNameValuePair("PartnerCode",HFpay.getPartnerCode()));
			parameterList.add(new BasicNameValuePair("CurCode",HFpay.getCurCode()));
			parameterList.add(new BasicNameValuePair("MerPriv",HFpay.getMerPriv()));
			parameterList.add(new BasicNameValuePair("ChkValue",HFpay.getChkValue()));
			parameterList.add(new BasicNameValuePair("PnrNum",HFpay.getPnrNum()));
			parameterList.add(new BasicNameValuePair("BgRetUrl",HFpay.getBgRetUrl()));

			parameterList.add(new BasicNameValuePair("Pid",""));
			parameterList.add(new BasicNameValuePair("GateId",""));
			parameterList.add(new BasicNameValuePair("UsrMp",""));
			parameterList.add(new BasicNameValuePair("DivDetails",""));
			parameterList.add(new BasicNameValuePair("OrderType",""));
			parameterList.add(new BasicNameValuePair("PayUsrId",""));


			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(parameterList,"utf-8");
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
            httpPost.setEntity(uefEntity);
			response =httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
			return metadata;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付发生异常",e);
		}finally {
			try {
				response.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
			try {
				httpClient.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * 从支付连接获取订单编号
	 * @param url 鹏鹏返回的支付连接
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getNbJylsh(String url) {
		String nb_jylsh="";
		if(StringUtils.isEmpty(url)) {
			return nb_jylsh;
		}
		HttpGet httpGet=new HttpGet(url);
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			httpGet.setHeader("Host","www.airpp.net");
			httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			HttpParams params = new BasicHttpParams();
            params.setParameter("http.protocol.handle-redirects", false);
			httpGet.setParams(params);
			response =httpClient.execute(httpGet);
			Header headers = response.getFirstHeader("Location");
			String location = headers.getValue();
			String paramByUrl = getParamByUrl(location, "nb_jylsh");
			return paramByUrl;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取nb_jylsh异常",e);
		}finally {
            try {
				response.close();
				httpGet.abort();
				httpClient.close();
			} catch (Exception e2) {

			}
		}
		return nb_jylsh;
	}

	/**
	 * 获取支付表单,转成实体对象
	 * @param nb_jylsh 上一步获取的订单编号
	 * @throws Exception
	 */
	public static HuiFuPay getPayFrom(String nb_jylsh) throws Exception{
		if(StringUtils.isEmpty(nb_jylsh)) {
			return null;
		}
		CloseableHttpResponse response=null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://www.airpp.net/airs/common/topay!pay.shtml?isJl=false&overtime=0");
		try {
			List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
			parameterList.add(new BasicNameValuePair("nb_jylsh",nb_jylsh));
			parameterList.add(new BasicNameValuePair("zffs","312013304"));
			parameterList.add(new BasicNameValuePair("djdm","1006394"));
			UrlEncodedFormEntity uefEntity =new UrlEncodedFormEntity(parameterList,"utf-8");
            httpPost.setEntity(uefEntity);
			httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			httpPost.setHeader("Host","www.airpp.net");
			httpPost.setHeader("Origin","http://www.airpp.net");
			//httpPost.setHeader("Upgrade-Insecure-Requests","1");
			//httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
			//httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
			//httpPost.setHeader("Accept-Encoding","gzip, deflate");
			//httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.9");
			//httpPost.setHeader("Referer","http://www.airpp.net/airsb/cg/jp_dd!forZf.shtml?jp_dd.ddbh=JP20111611F9");
			//httpPost.setHeader("Cookie",Cookie);
			response =httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String metadata = EntityUtils.toString(entity);
			Document parse = Jsoup.parse(metadata);
			Element versionE = parse.getElementById("Version");
			String version=versionE.val();
			Element merIdE = parse.getElementById("MerId");
			String merId=merIdE.val();
			Element ordIdE = parse.getElementById("OrdId");
			String ordId=ordIdE.val();
			Element ordAmtE = parse.getElementById("OrdAmt");
			String ordAmt=ordAmtE.val();
			Element curCodeE = parse.getElementById("CurCode");
			String curCode=curCodeE.val();
			Element retUrlE = parse.getElementById("RetUrl");
			String retUrl=retUrlE.val();
			Element pnrNumE = parse.getElementById("PnrNum");
			String pnrNum=pnrNumE.val();
			Element bgRetUrlE = parse.getElementById("BgRetUrl");
			String bgRetUrl=bgRetUrlE.val();
			Element chkValueE = parse.getElementById("ChkValue");
			String chkValue=chkValueE.val();
			HuiFuPay pay=new HuiFuPay();
			pay.setVersion(version);
			pay.setMerId(merId);
			pay.setOrdId(ordId);
			pay.setOrdAmt(ordAmt);
			pay.setCurCode(curCode);
			pay.setRetUrl(retUrl);
			pay.setBgRetUrl(bgRetUrl);
			pay.setPnrNum(pnrNum);
			pay.setChkValue(chkValue);
			return pay;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取支付表单异常",e);
			return null;
		}finally{
			if(response!=null){
				response.close();
			}
			httpPost.abort();
			httpClient.close();
		}

	}

    /**
     * 加密支付账号密码
     *
     * @param merId
     * @param ordId
     * @param ordAmt
     * @return
     */
    public static String getPayInfo(String merId, String ordId, String ordAmt) {
		String cValue="";
		RsaEnc rsaEnc=new RsaEnc();
		int ret =rsaEnc.rsaEncrypt(merId, ordId, ordAmt, payUsrId, passWord, merKeyUrl);
		if(ret == 0){
			cValue = rsaEnc.getRasStr();
        }
        return cValue;

    }

    /**
	 * 获取指定url中的某个参数
	 * @param url
	 * @param name
	 * @return
	 */
    public static String getParamByUrl(String url, String name) {
        try {
            url += "&";
            String pattern = "(\\?|&){1}#{0,1}" + name + "=[a-zA-Z0-9]*(&{1})";

            Pattern r = Pattern.compile(pattern);

            Matcher m = r.matcher(url);
            if (m.find()) {
                return m.group(0).split("=")[1].replace("&", "");
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取政策
     * @param pnr
     * @param flightNo
     * @param policyType
     * @return
     */
    public static org.dom4j.Element getB2BPolicyPrice(String pnr, String flightNo, String policyType) {
        try {
            String[] split = getb2bUernamePwd(flightNo, policyType).split(":");
            String username = split[0];
            String pwd = split[1];
            String signStr = key + Version + "I" + pnr + "N" + flightNo + username + pwd + PartnerCode ;
            String ChkValue = Md5Util.Md5Encode(signStr);
            String param = "VERSION="+Version+"&REQTYPE=I&PNRNO=" + pnr + "&ISSMALLPNR=N&AIRLINES=ZH&USERNAME=" + username + "&B2BPSWD=" + pwd + "&PARTNERCODE=F0&RSV1=&RSV2=&AirTickType=D&CHKVALUE=" + ChkValue;
            String message = 0 + "" + param.length() + param;
            String s = sendMsg(message);
            if(!s.contains("xml"))   {
                return null;
            }
            s=s.substring(4);
            //System.out.println("获取到的入库价格"+s);
            org.dom4j.Document document = DocumentHelper.parseText(s);
            org.dom4j.Element rootElement = document.getRootElement();
            return  rootElement;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 支付b2b订单
     * @param pnr
     * @param flightNo
     * @param policyType
     * @return
     */
    public static String payB2B(String pnr, String flightNo, String policyType,String orderNo,String faceValue,String rebates) {
        try {
            String[] split = getb2bUernamePwd(flightNo, policyType).split(":");
            String username = split[0];
            String pwd = split[1];
            String CPNRPSWD = Md5Util.Md5Encode("654321");
            String signStr = key + Version+ "Q"+ pnr+"N"+orderNo+flightNo+faceValue+rebates+username+pwd+"2"+payUsrId+CPNRPSWD+B2BRETURL+PartnerCode;
            String ChkValue = Md5Util.Md5Encode(signStr);
            String param = "VERSION="+Version+"&REQTYPE=Q&PNRNO="+pnr+"&ISSMALLPNR=N&AIRLINES="+flightNo+"&FACEVALUE="+faceValue+"&AGENTPCT="+rebates+"&USERNAME="+username+"&B2BPSWD="+pwd+"&PARTNERCODE="+PartnerCode+"&RETURL="+B2BRETURL+"&AirTickType=D&GUID="+orderNo+"&AGENTPCT=&PAYTYPE=2&CPNROPER="+payUsrId+"&CPNRPSWD="+CPNRPSWD+"&CHKVALUE="+ChkValue;
            String message = 0 + "" + param.length() + param;
            String s = sendMsg(message);
            System.out.println(s);
            return  s;
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 获取b2b订单详情
     * @param pnr
     * @param flightNo
     * @param policyType
     * @return
     */
    public static String getB2BOrderDetail(String pnr, String flightNo, String policyType) {
        try {
            String[] split = getb2bUernamePwd(flightNo, policyType).split(":");
            String username = split[0];
            String pwd = split[1];
            String signStr = key + Version + "S" + pnr + "N" + flightNo + username + pwd + PartnerCode ;
            String ChkValue = Md5Util.Md5Encode(signStr);
            String param = "VERSION="+Version+"&REQTYPE=S&PNRNO="+pnr+"&ISSMALLPNR=N&AIRLINES="+flightNo+"&USERNAME="+username+"&B2BPSWD="+pwd+"&PARTNERCODE="+PartnerCode+"&AirTickType=D&CHKVALUE="+ChkValue;
            String message = 0 + "" + param.length() + param;
            String s = sendMsg(message);
           System.out.println(s);
            if(!s.contains("xml"))   {
                return "";
            }
            return  s;
        } catch (Exception e) {
            return "";
        }
    }



    public static void main(String[] args) {
        try {
            //getB2BPolicyPrice("MBNJC6", "ZH", "AYYZHB2B");
            // payB2B("MEGQ0J","ZH","TTZHSHEB2B-E","655615931","610","");
          //  Thread.sleep(10*1000);
          // getB2BOrderDetail("MEGQ0J", "ZH", "TTZHSHEB2B-E");

            //sendS();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getb2bUernamePwd(String flightNo, String policyType) {
        String username = "", pwd = "";
        if (flightNo.equals("ZH")) {
            if (policyType.contains("SHEB2B")) {
                if (policyType.endsWith("A")) {
                    username = SHEB2B_A_USER;
                    pwd = SHEB2B_A_PWD;
                } else if (policyType.endsWith("B")) {
                    username = SHEB2B_B_USER;
                    pwd = SHEB2B_B_PWD;
                } else if (policyType.endsWith("C")) {
                    username = SHEB2B_C_USER;
                    pwd = SHEB2B_C_PWD;
                } else if (policyType.endsWith("D")) {
                    username = SHEB2B_D_USER;
                    pwd = SHEB2B_D_PWD;
                } else if (policyType.endsWith("E")) {
                    username = SHEB2B_E_USER;
                    pwd = SHEB2B_E_PWD;
                } else if (policyType.endsWith("F")) {
                    username = SHEB2B_D_USER;
                    pwd = SHEB2B_D_PWD;
                }
            } else {
                username = B2Busername;
                pwd = B2Bpwd;
            }
        }
        return username + ":" + pwd;

    }

    public static String sendMsg(String msg) {
        Socket socket = null;
        BufferedReader br = null;
        InputStream is = null;
        PrintWriter pw = null;
        OutputStream os = null;
        try {
            InetAddress addr = InetAddress.getByName("airpay.chinapnr.com");
            //1.建立客户端socket连接，指定服务器位置及端口
            socket = new Socket(addr, 8034); //IP端口
            //2.得到socket读写流
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            //输入流
            is = socket.getInputStream();
            InputStreamReader isr=new InputStreamReader(is,"GBK");
            br = new BufferedReader(isr);
            //3.利用流按照一定的操作，对socket进行读写操作
            pw.write(msg);
            pw.flush();
            //接收服务器的相应
            String replyInfo = null;
            StringBuilder sb = new StringBuilder();
            while (!((replyInfo = br.readLine()) == null)) {
                sb.append(replyInfo);
            }
            //System.out.println(sb.toString());
            return  sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
             return "";           
        } finally {
            //4.关闭资源
            if (socket != null) {
                try {
                    socket.shutdownOutput();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }


    /**
     * @param pnr
     * @param flightNo
     * @param policyType
     * @return
     */
    public static JSONObject getB2BPolicyPriceOLD(String pnr, String flightNo, String policyType) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(b2burl);
        try {
            String[] split = getb2bUernamePwd(flightNo, policyType).split(":");
            String username = split[0];
            String pwd = split[1];
            List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
            parameterList.add(new BasicNameValuePair("Version", Version));
            parameterList.add(new BasicNameValuePair("ReqType", "I"));
            parameterList.add(new BasicNameValuePair("PNRNo", pnr));
            parameterList.add(new BasicNameValuePair("IsSmallPnr", "Y"));
            parameterList.add(new BasicNameValuePair("Airlines", flightNo));
            parameterList.add(new BasicNameValuePair("Username", username));
            parameterList.add(new BasicNameValuePair("PartnerCode", PartnerCode));
            parameterList.add(new BasicNameValuePair("FaceValue", ""));
            parameterList.add(new BasicNameValuePair("AgentPct", ""));
            parameterList.add(new BasicNameValuePair("Rsv1", ""));
            parameterList.add(new BasicNameValuePair("Rsv2", ""));
            parameterList.add(new BasicNameValuePair("AirlineInfo", ""));
            String signStr = key + Version + "I" + pnr + "Y" + flightNo + "" + "" + username + pwd + PartnerCode + "" + "" + "";
            String ChkValue = Md5Util.md5(signStr + "10IMWDJGYNHOtesttest01test1test2");
            parameterList.add(new BasicNameValuePair("ChkValue", ChkValue));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameterList, "gbk");
            httpPost.setEntity(uefEntity);
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String metadata = EntityUtils.toString(entity);

            System.out.println(metadata);
        } catch (Exception e) {
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpPost.abort();
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;

    }


}
