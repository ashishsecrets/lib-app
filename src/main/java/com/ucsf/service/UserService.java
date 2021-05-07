package com.ucsf.service;

import java.util.List;

import org.springframework.data.domain.Page;
import com.ucsf.auth.model.User;
import com.ucsf.payload.request.AddUserRequest;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.payload.request.UserUpdateRequest;

public interface UserService {
	Page<User> findAll(int page, int size);
	List<User> getPatients();
	User save(SignUpRequest signUpRequest);
	User findByEmail(String email);
	User addUser(AddUserRequest user);
	User updateUser(Long userId,UserUpdateRequest updateUser);
	List<User> getApprovedPatients();
	Boolean isApproved(Long userId);
}
