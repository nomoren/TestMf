package cn.ssq.ticket.system.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ToolsUtil {
public static int getAge(String d) {  
    	
        Calendar cal = Calendar.getInstance();  
        Date birthDay = ToolsUtil.getStringToDate(d, "yyyy-MM-dd");
      
        int yearNow = cal.get(Calendar.YEAR);  
        int monthNow = cal.get(Calendar.MONTH);  
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
        cal.setTime(birthDay);  
  
        int yearBirth = cal.get(Calendar.YEAR);  
        int monthBirth = cal.get(Calendar.MONTH);  
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
  
        int age = yearNow - yearBirth;  
  
        if (monthNow <= monthBirth) {  
            if (monthNow == monthBirth) {  
                if (dayOfMonthNow < dayOfMonthBirth) age--;  
            }else{  
                age--;  
            }  
        }  
        return age;  
    } 
	public static byte[] InputStreamToBytes(InputStream in){
		
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream(); 
		byte[] buff = new byte[100]; //buff鐢ㄤ簬瀛樻斁寰幆璇诲彇鐨勪复鏃舵暟鎹�
		int rc = 0; 
		try {
			while ((rc = in.read(buff, 0, 100)) > 0) { 
			swapStream.write(buff, 0, rc); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		byte[] buffer = swapStream.toByteArray(); 
		return buffer;
	}
	public static String InputStreamToString(InputStream in){
		
		BufferedReader br = null;
		try {
			//br = new BufferedReader(new InputStreamReader(in));
			br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		try {
			
			while ((str = br.readLine()) != null) {
				stringBuffer.append(str);
				stringBuffer.append("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
	
	public static void InputStreamToFile(InputStream ins, File file) {
		try {
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 public static Date getStringToDate(String d,String patten){
			
			SimpleDateFormat sf  =new SimpleDateFormat(patten);
			Date date = null;
			try {
				date = sf.parse(d);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return date;
		}
	/**
	 * 璁＄畻鐩稿鏃ユ湡d鍋忕Щoffset澶╃殑鏃ユ湡锛坹yyy-MM-dd锛�
	 * @param d 鏃ユ湡
	 * @param offset 鍋忕Щ澶╂暟
	 * @return
	 */
	public static String getOffsetDate(Date d,int offset){
		
		GregorianCalendar gc =new GregorianCalendar();
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd");

		gc.setTime(d);
		gc.add(5,offset);
		

		return sf.format(gc.getTime());
	}
	public static String getOffsetDate(Date d,int offset,String pattern){
		
		GregorianCalendar gc =new GregorianCalendar();
		SimpleDateFormat sf  =new SimpleDateFormat(pattern);

		gc.setTime(d);
		gc.add(5,offset);
	
		return sf.format(gc.getTime());
	}
	
	public static String getDateToString(Date d,String pattern){
		
		SimpleDateFormat sf  =new SimpleDateFormat(pattern);
		return sf.format(d);
	}
	/**
	 * 璁＄畻鐩稿鏃ユ湡d鍋忕Щoffset澶╃殑鏃ユ湡锛坹yyy-MM-dd锛�
	 * @param d 鏃ユ湡
	 * @param offset 鍋忕Щ澶╂暟
	 * @return
	 */
	public static String getOffsetDate(String d,int offset){
		
		GregorianCalendar gc =new GregorianCalendar();
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sf.parse(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gc.setTime(date);
		gc.add(5,offset);
		

		return sf.format(gc.getTime());
	}
	 /**
     * <li>鍔熻兘鎻忚堪锛氭椂闂寸浉鍑忓緱鍒板ぉ鏁�
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long 
     * @author qzm
     */
    public static long getDaySub(String beginDateStr,String endDateStr)
    {
        long day=0;
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");    
        Date beginDate;
        Date endDate;
        try
        {
            beginDate = format.parse(beginDateStr);
            endDate= format.parse(endDateStr);    
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);    
            //System.out.println("鐩搁殧鐨勫ぉ鏁�"+day);   
        } catch (ParseException e)
        {
            // TODO 鑷姩鐢熸垚 catch 鍧�
            e.printStackTrace();
        }   
        return day;
    }

	public static String readFile(String pathName) throws FileNotFoundException{
		File mfile = new File(pathName);
		FileInputStream fis = new FileInputStream(mfile);
		return InputStreamToString(fis);
	}
	
	public static InputStream FileToInputStream(String pathName){
		File mfile = new File(pathName);
		try {
			FileInputStream fis = new FileInputStream(mfile);
			return fis;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
public static String InputStreamToString(InputStream in,String charset){
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(in,charset));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		try {
			
			while ((str = br.readLine()) != null) {
				stringBuffer.append(str);
				stringBuffer.append("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
	public static List<String> readFileToList(String name){
		
		List<String> list = new LinkedList<String>();
		try {
			File mfile = new File(name);
			FileInputStream fis = new FileInputStream(mfile);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
		
			String str = "";
	
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public static List<String> readFileToList(String name,String charset){
		
		List<String> list = new LinkedList<String>();
		try {
			File mfile = new File(name);
			FileInputStream fis = new FileInputStream(mfile);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,charset));
		
			String str = "";
	
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @param content 鏂囦欢鍐呭
	 * @param fileName 鏂囦欢鍚�
	 */
	public static void writeFile(String content,String fileName) {
		File file = new File(fileName);

		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;

		try {
			out = new FileOutputStream(file);
			writer = new OutputStreamWriter(out);
			bw = new BufferedWriter(writer);

			bw.write(content);
			bw.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * 
	 * @param content 鏂囦欢鍐呭
	 * @param fileName 鏂囦欢鍚�
	 * @param append 鏄惁杩藉姞鍐欏叆
	 */
	public static void writeFile(String content,String fileName,boolean append) {
		File file = new File(fileName);

		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;

		try {
			out = new FileOutputStream(file,append);
			writer = new OutputStreamWriter(out);
			bw = new BufferedWriter(writer);

			bw.write(content);
			bw.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	public static List<String> readFileToList(InputStream is){
		
		List<String> list = new LinkedList<String>();
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		
			String str = "";
	
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public static List<String> getFileList(File file) {  
		  
        List<String> result = new ArrayList<String>();  
  
        if (!file.isDirectory()) {  
            System.out.println(file.getAbsolutePath());  
            result.add(file.getAbsolutePath());  
        } else {  
            File[] directoryList = file.listFiles(new FileFilter() {  
                public boolean accept(File file) {  
                    if (file.isFile() && file.getName().indexOf("png") > -1) {  
                        return true;  
                    } else {  
                        return false;  
                    }  
                }  
            });  
            for (int i = 0; i < directoryList.length; i++) {  
                result.add(directoryList[i].getPath());  
            }  
        }  
  
        return result;  
    }  
    /** 
     * byte鏁扮粍杞琱ex瀛楃涓�br/> 
     * 涓�釜byte杞负2涓猦ex瀛楃 
     * @param src 
     * @return 
     */  
    public static String bytes2Hex(byte[] src){         
        char[] res = new char[src.length*2];    
        final char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};    
        for(int i=0,j=0; i<src.length; i++){    
            res[j++] = hexDigits[src[i] >>>4 & 0x0f];    
            res[j++] = hexDigits[src[i] & 0x0f];    
        }    
            
        return new String(res);    
    }   
    /** 
     * hex瀛楃涓茶浆byte鏁扮粍<br/> 
     * 2涓猦ex杞负涓�釜byte 
     * @param src 
     * @return 
     */  
    public static byte[] hex2Bytes(String src){  
        byte[] res = new byte[src.length()/2];        
        char[] chs = src.toCharArray();  
        for(int i=0,c=0; i<chs.length; i+=2,c++){  
            res[c] = (byte) (Integer.parseInt(new String(chs,i,2), 16));  
        }  
          
        return res;  
    }  
    
    /**
     * 搴忓垪鍖栨柟娉�
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void serializable(Object obj,String fileName) throws FileNotFoundException, IOException{
        
    	ObjectOutputStream outputStream=new ObjectOutputStream(new FileOutputStream(fileName));
        outputStream.writeObject(obj);      
    }
     
    /**
     * 鍙嶅簭鍒楀寲鐨勬柟娉�
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    public static Object deSerializable(String fileName) {
    	
        ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(fileName));
			return (Object) ois.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
        
    }
    
    public static Object deSerializable(InputStream in) {
    	
        ObjectInputStream ois;
		try {
			//InputStream in = new ByteArrayInputStream(content.getBytes()); 
			ois = new ObjectInputStream(in);
			return (Object) ois.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
        
    }
    
	 public static boolean isInteger(String str) {    
	    Pattern pattern = Pattern.compile("^\\d{4}$");    
	    return pattern.matcher(str).matches();    
	}  
	 public static String getRandomString(int length) {  
	        String str = "abcdefghijklmnopqrstuvwxyz0123456789";  
	        Random random = new Random();  
	        StringBuffer sb = new StringBuffer();  
	  
	        for (int i = 0; i < length; ++i) {  
	            int number = random.nextInt(36);// [0,62)  
	            sb.append(str.charAt(number));  
	        }  
	        return sb.toString();  
	    }  
	 public static String beanToXml(Object obj,Class<?> load) throws JAXBException{
         JAXBContext context = JAXBContext.newInstance(load);
         Marshaller marshaller = context.createMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
         marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
         StringWriter writer = new StringWriter();
         marshaller.marshal(obj,writer);
         return writer.toString();
     }
	 public static String MD5(String test){
			
			String re_md5 = new String();
			try{
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(test.getBytes("UTF-8"));
				byte b[] = md.digest();
				int i;
				StringBuffer buf = new StringBuffer("");
				for(int offset = 0;offset<b.length;offset++){
					i = b[offset];
					if(i<0)
						i+=256;
					if(i<16)
						buf.append("0");
					
					buf.append(Integer.toHexString(i));
				}
				re_md5 = buf.toString();
			}catch(Exception e){
				e.printStackTrace();
			}
			return re_md5;
		}
	 /** 
	     * xml转换成JavaBean 
	     * @param xml 
	     * @param c 
	     * @return 
	     */  
	    @SuppressWarnings("unchecked")  
	    public static <T> T converyToJavaBean(String xml, Class<T> c) {  
	        T t = null;  
	        try {  
	            JAXBContext context = JAXBContext.newInstance(c);  
	            Unmarshaller unmarshaller = context.createUnmarshaller();  
	            t = (T) unmarshaller.unmarshal(new StringReader(xml));  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	  
	        return t;  
	    }  
	    
		public static String getDateToString(Date d,String pattern,Locale locale){
			
			SimpleDateFormat sf  =new SimpleDateFormat(pattern, locale);
			return sf.format(d);
		}
}
