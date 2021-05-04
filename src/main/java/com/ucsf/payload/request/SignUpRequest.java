package com.ucsf.payload.request;

import java.util.List;

import lombok.Data;
@Data
public class SignUpRequest {
	
	// user Auth Attribute
	private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private String phoneCode;
    private String deviceId;
	List<String> userRoles;
	private UserMetadataRequest userMetadata;
}
