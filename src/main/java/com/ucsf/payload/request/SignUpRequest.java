package com.ucsf.payload.request;

import java.util.List;
import java.util.Set;

import com.ucsf.auth.model.Role;
import com.ucsf.auth.model.RoleName;

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
	List<String> userRoles;
	private UserMetadataRequest userMetadata;
}
