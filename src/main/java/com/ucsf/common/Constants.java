package com.ucsf.common;

public enum Constants {
	
	USERNAME_ALREADY_USED("Username is already taken!"),
	EMAIL_ALREADY_USED("Email Address already in use!"),
	SIGNUP_SUCCESS("SignUp Successfully"),
	INVALID_INDEXVALUE("Invalid Index Value"),
	INVALID_AUTHORIZATION_HEADER("Invalid auth header"),
	QUESTION_NOT_FOUND("Question not found"),
	PASSWORD_NOT_MATCHING("Password and confirm password not matching"),
	LINK_EXPIRED("User not found or Link expired"),
	INVALID_CREDENTIALS("Invalid UserName/Password"),
	USER_NOT_FOUND("User not existed"),CODE_NOT_SENT("OTP not Sent"),
	NO_STUDY_FOUND("No study exist"),
	OTP_NOT_VERIFIED("Otp expired or wrong otp"),
	OTP_EXPIRED("Otp has been Expired"),
	INVALID_STUDY("Invalid Study"),
	OTP_INCORRECT("Invalid Otp"), 
	USER_AGE_NOT_SPECIFIED("Please add user age");
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
