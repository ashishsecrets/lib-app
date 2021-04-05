package com.ucsf.payload.response;

public class ApiError {
	
	private int code;
	private String message;
	
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