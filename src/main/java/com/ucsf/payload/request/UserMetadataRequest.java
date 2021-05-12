package com.ucsf.payload.request;

import java.util.Date;

//user metadata attributes
public class UserMetadataRequest {
	private String race;
	private String age;
	private String zipCode;
	private String phone;
	private boolean acceptanceOpted;
	private Date acceptanceDate;
	private String dateOfBirth;
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isAcceptanceOpted() {
		return acceptanceOpted;
	}
	public void setAcceptanceOpted(boolean acceptanceOpted) {
		this.acceptanceOpted = acceptanceOpted;
	}
	public Date getAcceptanceDate() {
		return acceptanceDate;
	}
	public void setAcceptanceDate(Date acceptanceDate) {
		this.acceptanceDate = acceptanceDate;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
}
