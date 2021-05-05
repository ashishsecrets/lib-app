package com.ucsf.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.ucsf.auth.model.User;
import com.ucsf.payload.request.AddUserRequest;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.payload.request.UserUpdateRequest;

public interface UserService {
	Page<User> findAll(int page, int size);
	User save(SignUpRequest signUpRequest);
	User findByEmail(String email);
	User addUser(AddUserRequest user);
	User updateUser(Long userId,UserUpdateRequest updateUser);
}
