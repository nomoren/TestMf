package cn.ssq.ticket.system.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取配置文件
 * @author Administrator
 *
 */
public class ConfigUtils {

	public static String fileName="application.properties";
	
	public static String getParam(String name) {
		Properties prop = new Properties();
		InputStream in = ConfigUtils.class.getResourceAsStream("/" + fileName);
		try {
			prop.load(new InputStreamReader(in, "utf-8"));
			return prop.getProperty(name);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void writeParam(String parameterName, String parameterValue) {
		InputStream in = null;
		java.io.OutputStream fos = null;
		Properties prop = new Properties();
		String filePath = ConfigUtils.class.getClassLoader().getResource(fileName).getPath();
		try {
			in = new java.io.FileInputStream(filePath);
			prop.load(in);
			prop.setProperty(parameterName, parameterValue);
			fos = new java.io.FileOutputStream(filePath);
			prop.store(fos, "application");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
