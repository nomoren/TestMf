package cn.ssq.ticket.system.entity.importBean;

public class Person {

	private int personId;
	private int personType;
	private String passengerName;
	private int sex;
	private int Age;
	private String birthday;
	private int identityType;
	private String identityNo;
	
	public int getPersonId() {
		return personId;
	}
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	public int getPersonType() {
		return personType;
	}
	public void setPersonType(int personType) {
		this.personType = personType;
	}
	public String getPassengerName() {
		return passengerName;
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getAge() {
		return Age;
	}
	public void setAge(int age) {
		Age = age;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getIdentityType() {
		return identityType;
	}
	public void setIdentityType(int identityType) {
		this.identityType = identityType;
	}
	public String getIdentityNo() {
		return identityNo;
	}
	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}
	@Override
	public String toString() {
		return "Person [personId=" + personId + ", personType=" + personType
				+ ", passengerName=" + passengerName + ", sex=" + sex
				+ ", Age=" + Age + ", birthday=" + birthday + ", identityType="
				+ identityType + ", identityNo=" + identityNo + "]";
	}
	
}
