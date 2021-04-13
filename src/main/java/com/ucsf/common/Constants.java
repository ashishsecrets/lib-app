package com.ucsf.common;

public enum Constants {
	
	USERNAME_ALREADY_USED("Username is already taken!"),
	EMAIL_ALREADY_USED("Email Address already in use!"),
	SIGNUP_SUCCESS("SignUp Successfully"),
	INVALID_INDEXVALUE("Invalid Index Value"),
	INVALID_AUTHORIZATION_HEADER("Invalid auth header"),
	QUESTION_NOT_FOUND("Question not found"),
	USER_NOT_FOUND("User not existed");
	
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
