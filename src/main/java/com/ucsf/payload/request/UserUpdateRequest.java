package com.ucsf.payload.request;

import java.util.List;

import lombok.Data;
@Data
public class UserUpdateRequest {
	private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private String address;
    private String country;
    private String state;
	List<String> userRoles;
}
