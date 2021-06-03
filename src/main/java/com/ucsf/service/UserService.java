package com.ucsf.service;

import java.util.List;
import org.springframework.data.domain.Page;
import com.ucsf.auth.model.User;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.payload.request.AddUserRequest;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.payload.request.UserUpdateRequest;
import com.ucsf.payload.response.PatientResponse;
import com.ucsf.payload.response.UserDataResponse;

public interface UserService {
	Page<User> findAll(int page, int size);
	List<User> getPatients(Long studyId);
	User save(SignUpRequest signUpRequest);
	User findByEmail(String email);
	User findById(Long id);
	User addUser(AddUserRequest user);
	User updateUser(Long userId,UserUpdateRequest updateUser);
	List<PatientResponse> getApprovedPatients(Long studyId);
	UserScreeningStatus getUserStatus(Long userId);
	List<UserDataResponse> getUserById(Long userId);
	List<PatientResponse> getDisapprovedPatients(Long studyId);
	List<PatientResponse> getDisqualifiedPatients(Long studyId);
	List<User> getStudyTeam();
	User updateUserStatus(Long userId,Boolean status);
}
