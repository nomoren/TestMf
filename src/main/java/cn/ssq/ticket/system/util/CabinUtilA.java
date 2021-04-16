package cn.ssq.ticket.system.util;

import cn.ssq.ticket.system.entity.FlightInfo;
import cn.ssq.ticket.system.entity.Info;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;






public class CabinUtilA {
	
	

	private static final Logger logger = Logger.getLogger(CabinUtilA.class);

	public static String cmd(String uid,String qf)  {
		String url1 = "http://14.215.118.230:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;

		String res1 =  CabinUtil.httpGet(url1);
		return res1;

	}

	/**
	 * 沈阳配置
	 * @param uid
	 * @param qf
	 * @return
	 */
	public static String avhCmdSHE(String uid,String qf)  {
		String url1 ="http://14.152.95.93:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;
		String xml = null;
		String res1=null;
		try {
			res1 =  CabinUtil.httpGet(url1);
			org.dom4j.Document doc1 = null;
			Element el1 = null;
			doc1 = DocumentHelper.parseText(res1);
			el1 = doc1.getRootElement();
			if(el1.attribute("ret_value").getText().equals("1")){
				byte[] buffer = ToolsUtil.hex2Bytes(el1.getTextTrim());
				xml = new String(buffer,"GBK");

				return xml;
			}else{
				System.out.println(res1);
				return null;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		System.out.println(res1);
		return xml;
	}

	public static String avhCmd(String qf)  {
        String uid = "apiyjsl3";
		String ZHPZ = "http://14.215.118.230:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;
		String res1 =  CabinUtil.httpGet(ZHPZ);
		org.dom4j.Document doc1 = null;
		Element el1 = null;
		String xml = null;
		try {
			doc1 = DocumentHelper.parseText(res1);
			el1 = doc1.getRootElement();
			if(el1.attribute("ret_value").getText().equals("1")){
				byte[] buffer = ToolsUtil.hex2Bytes(el1.getTextTrim());
				xml = new String(buffer,"GBK");

				return xml;
			}else{
				//System.out.println(res1);
				return null;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		//System.out.println(res1);
		return xml;
	}


    public static String shenCmd(String qf)  {
        String uid="sz05";
        String url1 = "http://14.152.95.93:352/ib_tranx_req.asp?uid="+uid+"&sessionid="+uid+"price&termid="+uid+"&verify=0&"+qf;
        String res1 =  CabinUtil.httpGet(url1);
        org.dom4j.Document doc1 = null;
        Element el1 = null;
        String xml = null;
        try {
            doc1 = DocumentHelper.parseText(res1);
            el1 = doc1.getRootElement();
            if(el1.attribute("ret_value").getText().equals("1")){
                byte[] buffer = ToolsUtil.hex2Bytes(el1.getTextTrim());
                xml = new String(buffer,"GBK");

                return xml;
            }else{
                //System.out.println(res1);
                return null;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //System.out.println(res1);
        return xml;
    }

	public static Map<String,FlightInfo> analyavh(String dpt, String arr, String date, String xml){
		Map<String,FlightInfo> map = new HashMap<String, FlightInfo>();
		if(!xml.contains(dpt.toUpperCase()+arr.toUpperCase())){
			System.out.println(xml);
		}
		String[] raws = xml.split("\n");
		for(int i=1;i<raws.length;i++){
			String raw = raws[i];
			if(raw.length()>49&&raw.substring(49).contains(dpt.toUpperCase()+arr.toUpperCase()+" ")){
				FlightInfo fi = new FlightInfo();
				fi.setDate(date);
				fi.setDpt(dpt);
				fi.setArr(arr);
				fi.setFlightNo(raw.substring(7, 14).replace(" ", ""));
				fi.setStime(raw.substring(58, 62));

				String cabins = raw.substring(20, 49)+" "+raws[i+1].substring(20, 70);

				String[] cbs = cabins.split(" +");

				Map<String,String> mmap = new HashMap<String,String>();
				for(String cabin:cbs){
					mmap.put(cabin.substring(0, 1), cabin.substring(1));
				}
				fi.setMap(mmap);
				map.put(fi.getFlightNo(), fi);

			}
		}
		return map;
	}
	
	
	public static String getAvStatus(String dep,String arr,String depDate,String depTime,String flightNo,String cabin) {
		try {
			depTime = depTime.replaceAll(":", "").substring(0, 4);
            if(flightNo.startsWith("KY")){
                String key=dep+arr+flightNo;
                String flightValue = PPUtil.KY_ZH.get(key);
                if(StringUtils.isNotEmpty(flightValue)){
                    flightNo=flightValue;
                }else{
                    flightNo=flightNo.replace("KY","ZH");
                }
            }
			Info ii = new Info(dep+","+arr+","+depDate+","+depTime+","+flightNo);
			String params = "cmd=raw&ins="+ToolsUtil.bytes2Hex(ii.getAvh().getBytes("GBK"));
            String sb="";
			if(flightNo.contains("U")){
                sb=CabinUtilA.shenCmd(params);
            }else{
                sb=CabinUtilA.avhCmd(params);
            }

			Map<String,FlightInfo> cabinInfo = analyavh(dep,arr,depDate,sb);
			FlightInfo flightInfo = cabinInfo.get(flightNo);
			String cabinNum = flightInfo.getMap().get(cabin);
			return cabinNum;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] arge){
		String avStatus = getAvStatus("JJN", "CSX","2021-03-19", "1335", "ZH8767","K");

		System.out.println(avStatus);
		
		
	}
}
