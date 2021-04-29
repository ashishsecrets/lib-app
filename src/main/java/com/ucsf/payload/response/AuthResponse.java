package com.ucsf.payload.response;

import java.io.Serializable;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

import com.ucsf.auth.model.User;

import lombok.Data;

@Data
public class AuthResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String authToken;
	private final String message;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String phoneNumber;
	private final Set authority;

	public AuthResponse(UserDetails userDetail, User user, String message) {
		this.authToken = user.getAuthToken();
		this.message = message;
		this.authority = (Set) userDetail.getAuthorities();
		this.email = userDetail.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.phoneNumber = user.getPhoneNumber();

	}
}