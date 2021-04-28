package com.ucsf.common;

public enum ErrorCodes {

	USERNAME_ALREADY_USED(101), EMAIL_ALREADY_USED(102),
	QUESTION_NOT_FOUND(103),INVALID_INDEXVALUE(104),
	INVALID_AUTHORIZATION_HEADER(105),PASSWORD_NOT_MATCHING(106),LINK_EXPIRED(107),
	SIGNUP_SUCCESS(200),INVALID_CREDENTIALS(401),
	USER_NOT_FOUND(106),CODE_NOT_SENT(107);

	private ErrorCodes(int value) {
		this.code = value;
	}

	private int code;

	public int code() {
		return code;
	}

	public void setValue(int code) {
		this.code = code;
	}

}
