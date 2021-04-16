package cn.ssq.ticket.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_import")
public class Import implements Serializable{
	private static final long serialVersionUID = 1L;

	@TableId(value = "import_id", type = IdType.AUTO)
	private Long importId;
	
	private String name;
	
	private String count;
	
	private String success;
	
	private String fail;
	
	private String type;
	
	private String remark;
	
	private Date importDate;

	public Long getImportId() {
		return importId;
	}

	public void setImportId(Long importId) {
		this.importId = importId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	@Override
	public String toString() {
		return "Import [importId=" + importId + ", name=" + name + ", count="
				+ count + ", success=" + success + ", fail=" + fail + ", type="
				+ type + ", remark=" + remark + ", importDate=" + importDate
				+ "]";
	}
	
	
}
