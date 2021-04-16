package cn.ssq.ticket.system.entity.importBean;

public class TicketLockItem {

	private String currentTime;
	
	private boolean locked;
	
	private String userName;

	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public String toString() {
		return "TicketLockItem [currentTime=" + currentTime + ", locked="
				+ locked + ", userName=" + userName + "]";
	}
	
	
}
