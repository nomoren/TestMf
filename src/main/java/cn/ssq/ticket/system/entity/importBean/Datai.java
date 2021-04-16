package cn.ssq.ticket.system.entity.importBean;

import java.util.List;

public class Datai {

	private int count;
	private List<Row> rows;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<Row> getRows() {
		return rows;
	}
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
	@Override
	public String toString() {
		return "Datai [count=" + count + ", rows=" + rows + "]";
	}
	
}
