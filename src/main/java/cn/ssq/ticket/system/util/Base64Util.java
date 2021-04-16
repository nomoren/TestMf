package cn.ssq.ticket.system.util;

import sun.misc.BASE64Decoder;
import cn.hutool.core.codec.Base64Encoder;
@SuppressWarnings("restriction")
public class Base64Util {


	//BASE64 解密
	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	//BASE64 加密
	public static String generateBase64Str(String s){
		String base64=null;
		if(s!=null){
			base64=Base64Encoder.encode(s.getBytes());
		}
		return base64;
	}
	
	public static void main(String[] args) {
		String generateBase64Str = base64Decode("785214");
		System.out.println(generateBase64Str);
	}

	static final int base64[] = { 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64,
		64, 64, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64,
		64, 64, 64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
		16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64, 64, 26,
		27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
		44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64 };

	public static String base64Decode(String orig) {
		char chars[] = orig.toCharArray();
		StringBuffer sb = new StringBuffer();
		int i = 0;
		int shift = 0; // # of excess bits stored in accum
		int acc = 0;

		for (i = 0; i < chars.length; i++) {
			int v = base64[chars[i] & 0xFF];

			if (v >= 64) {
				if (chars[i] != '=')
					System.out.println("Wrong char in base64: " + chars[i]);
			} else {
				acc = (acc << 6) | v;
				shift += 6;
				if (shift >= 8) {
					shift -= 8;
					sb.append((char) ((acc >> shift) & 0xff));
				}
			}
		}
		return sb.toString();
	}


}
