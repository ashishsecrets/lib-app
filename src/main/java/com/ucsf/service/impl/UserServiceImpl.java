package com.ucsf.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.ucsf.model.StudyImages;
import com.ucsf.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.Role;
import com.ucsf.auth.model.RoleName;
import com.ucsf.auth.model.User;
import com.ucsf.auth.model.User.UserStatus;
import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.model.UserMetadata.StudyAcceptanceNotification;
import com.ucsf.model.UserMetadata.StudyStatus;
import com.ucsf.model.UserScreeningStatus.UserScreenStatus;
import com.ucsf.payload.request.AddUserRequest;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.payload.request.UserUpdateRequest;
import com.ucsf.payload.response.StudyResponse;
import com.ucsf.repository.RoleRepository;
import com.ucsf.repository.StudyRepository;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.UserScreeningStatusRepository;
import com.ucsf.service.EmailService;
import com.ucsf.service.UserService;
import com.ucsf.util.AppUtil;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	StudyRepository studyRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserMetaDataRepository userMetaDataRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;

	@Autowired
	ImageRepository imageRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public Page<User> findAll(int page, int size) {
		Page<User> users = userRepository.findAll(PageRequest.of(page, size));
		return users;
	}

	@Override
	public User save(SignUpRequest user) {
		User newUser = new User();
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setEmail(user.getEmail());
		newUser.setPhoneNumber(user.getPhone());
		newUser.setPhoneCode(user.getPhoneCode());

		// Add Role
		if (user.getUserRoles() != null && user.getUserRoles().size() > 0) {
			for (String role : user.getUserRoles()) {
				if (role.equals("ADMIN")) {
					newUser.getRoles().add(roleRepository.findByName(RoleName.ADMIN));
				}
			}
		} else {
			newUser.getRoles().add(roleRepository.findByName(RoleName.PATIENT));
		}
		newUser.setUserStatus(UserStatus.ACTIVE);
		newUser.setDevideId(user.getDeviceId());
		User savedUser = userRepository.save(newUser);
		UserMetadata metadata = new UserMetadata();
		metadata.setConsentAccepted(false);
		metadata.setStudyStatus(StudyStatus.NEWLY_ADDED);
		metadata.setUserId(savedUser.getId());
		metadata.setNotifiedBy(StudyAcceptanceNotification.NOT_APPROVED);
		metadata.setDateOfBith(user.getDateOfBirth());
		metadata.setAge(AppUtil.getAge(user.getDateOfBirth()));
		userMetaDataRepository.save(metadata);
		// save metadata in metadatarepo
		// newUser.setMetadata(metadata);

		UserScreeningStatus userScreeningStatus = new UserScreeningStatus();
		userScreeningStatus.setStudyId(1l);
		userScreeningStatus.setUserScreeningStatus(UserScreenStatus.NEWLY_ADDED);
		userScreeningStatus.setIndexValue(1);
		userScreeningStatus.setUserId(savedUser.getId());
		userScreeningStatusRepository.save(userScreeningStatus);

		StudyImages upper_front = new StudyImages();
		upper_front.setName("upper_front");
		upper_front.setDescription("");
		upper_front.setStudyId(1l);
		upper_front.setImageUrl("body_parts/upper_front");
		upper_front.setUserId(savedUser.getId());
		upper_front.setCount(0);
		imageRepository.save(upper_front);

		StudyImages upper_back = new StudyImages();
		upper_back.setName("upper_back");
		upper_back.setDescription("");
		upper_back.setStudyId(1l);
		upper_back.setImageUrl("body_parts/upper_back");
		upper_back.setUserId(savedUser.getId());
		upper_back.setCount(0);
		imageRepository.save(upper_back);

		StudyImages lower_front = new StudyImages();
		lower_front.setName("lower_front");
		lower_front.setDescription("");
		lower_front.setStudyId(1l);
		lower_front.setImageUrl("body_parts/lower_front");
		lower_front.setUserId(savedUser.getId());
		lower_front.setCount(0);
		imageRepository.save(lower_front);

		StudyImages lower_back = new StudyImages();
		lower_back.setName("lower_back");
		lower_back.setDescription("");
		lower_back.setStudyId(1l);
		lower_back.setImageUrl("body_parts/lower_back");
		lower_back.setUserId(savedUser.getId());
		lower_back.setCount(0);
		imageRepository.save(lower_back);

		StudyImages full_back = new StudyImages();
		full_back.setName("full_back");
		full_back.setDescription("");
		full_back.setStudyId(1l);
		full_back.setImageUrl("body_parts/full_back");
		full_back.setUserId(savedUser.getId());
		full_back.setCount(0);
		imageRepository.save(full_back);

		StudyImages special_areas = new StudyImages();
		special_areas.setName("special_areas");
		special_areas.setDescription("");
		special_areas.setStudyId(1l);
		special_areas.setImageUrl("body_parts/special_areas");
		special_areas.setUserId(savedUser.getId());
		special_areas.setCount(0);
		imageRepository.save(special_areas);

		return savedUser;
	}

	@Override
	public User addUser(AddUserRequest user) {
		User newUser = new User();
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setEmail(user.getEmail());
		newUser.setPhoneNumber(user.getPhone());
		newUser.setPhoneCode(user.getPhoneCode());

		// Add Role
		if (user.getUserRoles() != null && user.getUserRoles().size() > 0) {
			for (String role : user.getUserRoles()) {
				if (role.equals("PHYSICIAN")) {
					newUser.getRoles().add(roleRepository.findByName(RoleName.PHYSICIAN));
				}
				if (role.equals("STUDYTEAM")) {
					newUser.getRoles().add(roleRepository.findByName(RoleName.STUDY_TEAM));
				}
			}
		} else {
			newUser.getRoles().add(roleRepository.findByName(RoleName.PATIENT));
		}
		newUser.setUserStatus(UserStatus.ACTIVE);
		User savedUser = userRepository.save(newUser);
		return savedUser;
	}

	@PostConstruct
	public void saveRole() {
		Role admin = roleRepository.findByName(RoleName.ADMIN);
		if (admin == null) {
			Role role = new Role();
			role.setName(RoleName.ADMIN);
			roleRepository.save(role);
		}
		Role patient = roleRepository.findByName(RoleName.PATIENT);
		if (patient == null) {
			Role role = new Role();
			role.setName(RoleName.PATIENT);
			roleRepository.save(role);
		}
		Role physian = roleRepository.findByName(RoleName.PHYSICIAN);
		if (physian == null) {
			Role role = new Role();
			role.setName(RoleName.PHYSICIAN);
			roleRepository.save(role);
		}
		Role studyTeam = roleRepository.findByName(RoleName.STUDY_TEAM);
		if (studyTeam == null) {
			Role role = new Role();
			role.setName(RoleName.STUDY_TEAM);
			roleRepository.save(role);
		}
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User updateUser(Long userId, UserUpdateRequest updateRequest) {
		// TODO Auto-generated method stub
		Optional<User> existed = userRepository.findById(userId);
		User user = null;
		if (existed.isEmpty()) {
			return user;
		} else {
			user = existed.get();
			user.setEmail(updateRequest.getEmail());
			user.setFirstName(updateRequest.getFirstName());
			user.setLastName(updateRequest.getLastName());
			if (updateRequest.getUserRoles() != null && updateRequest.getUserRoles().size() > 0) {
				for (String role : updateRequest.getUserRoles()) {
					if (role.equals("PHYSICIAN")) {
						user.getRoles().add(roleRepository.findByName(RoleName.PHYSICIAN));
					}
					if (role.equals("STUDYTEAM")) {
						user.getRoles().add(roleRepository.findByName(RoleName.STUDY_TEAM));
					}
				}
			}
			user.setPassword(bcryptEncoder.encode(updateRequest.getPassword()));
			user.setPhoneNumber(updateRequest.getPhone());
			userRepository.save(user);
			return user;
		}
	}

	@Override
	public List<User> getPatients() {
		List<Map<String, Object>> patientList = jdbcTemplate.queryForList("select * from user_roles where role_id = 2");
		List<User> patients = new ArrayList<User>();
		Long userId = 0l;
		Optional<User> user = null;
		User patient = null;
		for (Map<String, Object> map : patientList) {
			if (map.get("user_id") != null) {
				userId = Long.parseLong(map.get("user_id").toString());
				user = userRepository.findById(userId);
				if (user.isPresent()) {
					patient = user.get();
					patients.add(patient);
				}
			}
		}
		return patients;
	}
}
