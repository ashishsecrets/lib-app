package com.ucsf.payload;

import java.io.Serializable;

import lombok.Data;

@Data
public class AuthResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final boolean isVerifiedUser;
	private final String errorMessage;

	public AuthResponse(String jwttoken, boolean isVerifiedUser,String errorMessage) {
		this.jwttoken = jwttoken;
		this.isVerifiedUser = isVerifiedUser;
		this.errorMessage = errorMessage;
	}
}