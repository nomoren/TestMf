package cn.ssq.ticket.system.runnable;

import java.net.URLEncoder;

import cn.ssq.ticket.system.util.SendQQMsgUtil;

public class SendQQmsg implements Runnable{

	private String msg;
	
	private String qqNumber;
	
	public SendQQmsg(String msg,String qqNumber) {
		this.msg=msg;
		this.qqNumber=qqNumber;
	}
	@Override
	public void run() {
		try {
			String encode = URLEncoder.encode(msg, "UTF-8");
			SendQQMsgUtil.send2Qun(encode,qqNumber);
		} catch (Exception e) {
			
		}
		
	}
	public static void main(String[] args) {
		String msg="KY往返订单,订单号:607067913,最晚出票时限:"+"2020-08-24 16:35:00";
		SendQQmsg send=new SendQQmsg(msg, "723125962");
		new Thread(send).start();
	}

}
