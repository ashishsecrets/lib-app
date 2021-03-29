package com.ucsf.payload;

import java.util.List;

import lombok.Data;
@Data
public class UserDto {
	
	// user Auth Attribute
	private String email;
    private String username;
    private String password;
	List<String> userRoles;
	private UserMetadataDto userMetadata;
}
