package com.ucsf.service;

import org.springframework.data.domain.Page;

import com.ucsf.auth.model.User;
import com.ucsf.payload.request.SignUpRequest;

public interface UserService {
	Page<User> findAll(int page, int size);
	User save(SignUpRequest signUpRequest);
}
