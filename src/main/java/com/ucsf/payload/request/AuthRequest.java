package com.ucsf.payload.request;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;
	
	private String email;
	private String deviceId;

	public AuthRequest(String username, String password, String deviceId) {
		this.email = username;
		this.password = password;
		this.deviceId = deviceId;
	}

	private String password;

}