package com.ucsf.payload.request;

import java.util.List;

import lombok.Data;
@Data
public class AddUserRequest {
	
	// user Auth Attribute
	private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String phoneCode;
    private String userRoles;
}
