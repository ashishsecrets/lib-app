package com.ucsf.payload.request;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;
	
	private String email;

	public AuthRequest(String username, String password) {
		this.email = username;
		this.password = password;
	}

	private String password;

}