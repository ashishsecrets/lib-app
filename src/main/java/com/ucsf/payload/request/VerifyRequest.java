package com.ucsf.payload.request;

import java.io.Serializable;

public class VerifyRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String username;
	private String code;

	public VerifyRequest() {
	}

	public VerifyRequest(String username, String code) {
		this.setUsername(username);
		this.setCode(code);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}