package cn.ssq.ticket.system.entity.importBean;



public class ListRespon {

	private String success;
	private int returnCode;
	private String errorMsg;
	private Datai data;
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public int getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public Datai getData() {
		return data;
	}
	public void setData(Datai data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ListRespon [success=" + success + ", returnCode=" + returnCode
				+ ", errorMsg=" + errorMsg + ", data=" + data + "]";
	}
	
}
