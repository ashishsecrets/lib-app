package com.ucsf.payload;

import java.util.List;

import lombok.Data;
@Data
public class UserDto {
	
	// user Auth Attribute
	private String email;
    private String username;
    private String password;
    private String phone;
    private String phoneCode;
	List<String> userRoles;
	private UserMetadataDto userMetadata;
}
