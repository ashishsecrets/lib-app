package com.ucsf.service;

import java.util.List;
import org.springframework.data.domain.Page;
import com.ucsf.auth.model.User;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.payload.request.AddUserRequest;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.payload.request.UserUpdateRequest;
import com.ucsf.payload.response.UserDataResponse;

public interface UserService {
	Page<User> findAll(int page, int size);
	List<User> getPatients();
	User save(SignUpRequest signUpRequest);
	User findByEmail(String email);
	User findById(Long id);
	User addUser(AddUserRequest user);
	User updateUser(Long userId,UserUpdateRequest updateUser);
	List<User> getApprovedPatients();
	UserScreeningStatus getUserStatus(Long userId);
	List<UserDataResponse> getUserById(Long userId);
	List<User> getDisapprovedPatients();
	List<User> getStudyTeam();
	User updateUserStatus(Long userId,Boolean status);
}
