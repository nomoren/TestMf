package cn.ssq.ticket.system.entity.importBean.TTsBean;

public class RnfRequestParam {

	private String version;
	
	private String notifyType;
	
	private String sign;
	
	private DataParam data;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public DataParam getData() {
		return data;
	}

	public void setData(DataParam data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RequestParam [version=" + version + ", notifyType="
				+ notifyType + ", sign=" + sign + ", data=" + data + "]";
	}
	
	
	
}
