package com.ucsf.payload.response;

public class ApiError {

	public int code;
	public String message;

	public ApiError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getDescription() {
		return message;
	}

	public int getCode() {
		return code;
	}

		@Override
		public String toString() {
			return code + ": " + message;
		}
		  
		  
}