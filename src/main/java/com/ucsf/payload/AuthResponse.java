package com.ucsf.payload;

import java.io.Serializable;

public class AuthResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final boolean isVerifiedUser;

	public AuthResponse(String jwttoken, boolean isVerifiedUser) {
		this.jwttoken = jwttoken;
		this.isVerifiedUser = isVerifiedUser;
	}

	public String getToken() {
		return this.jwttoken;
	}

	public boolean isVerifiedUser() {
		return isVerifiedUser;
	}

}