package com.ucsf.payload.request;

import lombok.Data;
@Data
public class UserUpdateRequest {
	private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private String phoneCode;
    private String userRoles;
}
