package com.ucsf.payload;

import com.ucsf.payload.UserMetadataDto;

public class UserDto {
	
	// user Auth Attribute
	private String email;
    private String username;
    private String password;
    private String[] role;
    
    private UserMetadataDto userMetadata;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String[] getRole() {
		return role;
	}
	public void setRole(String[] role) {
		this.role = role;
	}
	public UserMetadataDto getUserMetadata() {
		return userMetadata;
	}
	public void setUserMetadata(UserMetadataDto userMetadata) {
		this.userMetadata = userMetadata;
	}
	

}
