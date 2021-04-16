package cn.ssq.ticket.system.entity;

import java.io.Serializable;
/**
 * 响应对象
 * @author Administrator
 *
 * @param <T>
 */
public class ResponseResult<T> implements Serializable{
private static final long serialVersionUID = 1L;
	
	private Integer code=0;			
	private String msg;
	private Integer count;
	private T data;					
	

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public ResponseResult() {
	}
	
	public ResponseResult(Integer code,String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public ResponseResult(Integer code,Exception e) {
		this.code = code;
		
		if(e.getMessage()==""||e.getMessage()==null){
			this.msg=e.toString().substring(e.toString().indexOf(":")+1);
		}else{
			this.msg = e.getMessage();
		}
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void setMsg(RuntimeException e) {
		this.msg = e.getMessage();
	}
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ResponseResult [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}

}
