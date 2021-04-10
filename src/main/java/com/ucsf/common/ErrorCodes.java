package com.ucsf.common;

public enum ErrorCodes {

	USERNAME_ALREADY_USED(101), EMAIL_ALREADY_USED(102),SIGNUP_SUCCESS(200),INVALID_CREDENTIALS(401);

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
