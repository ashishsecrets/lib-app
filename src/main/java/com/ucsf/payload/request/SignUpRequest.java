package com.ucsf.payload.request;

import java.util.List;

import lombok.Data;
@Data
public class SignUpRequest {
	
	// user Auth Attribute
	private String email;
    private String username;
    private String password;
    private String phone;
    private String phoneCode;
	List<String> userRoles;
	private UserMetadataRequest userMetadata;
}