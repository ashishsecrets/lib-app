package com.ucsf.common;

public enum Constants {
	
	USERNAME_ALREADY_USED("Username is already taken!"),
	EMAIL_ALREADY_USED("Email Address already in use!"),
	SIGNUP_SUCCESS("SignUp Successfully");
	
		private Constants(String value) { 
	    this.errordesc = value; 
	    }
	private String errordesc = ""; 
	public String errordesc() {
	    return errordesc;
	}
	public void setValue(String errordesc) {
	    this.errordesc = errordesc;
	}
}
