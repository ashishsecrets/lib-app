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
				else {
					newUser.getRoles().add(roleRepository.findByName(RoleName.PATIENT));
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

		//New list updated by client
		/*“Full body front”, ” Full body back ", "Front Trunk", ”Back Trunk” , "Front of Arms", ”Back of Arms”, ”Front of Hands”, "Back of Hands", "Front of Legs", ”Back of Legs”, "Front of Feet", ”Back of Feet” "Special areas of interest”*/
		//Making the changes below
		StudyImages full_body_front = new StudyImages();
		full_body_front.setName("full_body_front");
		full_body_front.setDescription("Full body front");
		full_body_front.setStudyId(1l);
		full_body_front.setImageUrl("body_parts/full_body_front"+"/"+savedUser.getId());
		full_body_front.setUserId(savedUser.getId());
		full_body_front.setCount(0);
		imageRepository.save(full_body_front);

		StudyImages full_body_back = new StudyImages();
		full_body_back.setName("full_body_back");
		full_body_back.setDescription("Full body back");
		full_body_back.setStudyId(1l);
		full_body_back.setImageUrl("body_parts/full_body_back"+"/"+savedUser.getId());
		full_body_back.setUserId(savedUser.getId());
		full_body_back.setCount(0);
		imageRepository.save(full_body_back);

		StudyImages front_trunk = new StudyImages();
		front_trunk.setName("front_trunk");
		front_trunk.setDescription("Front Trunk");
		front_trunk.setStudyId(1l);
		front_trunk.setImageUrl("body_parts/front_trunk"+"/"+savedUser.getId());
		front_trunk.setUserId(savedUser.getId());
		front_trunk.setCount(0);
		imageRepository.save(front_trunk);

		StudyImages back_trunk = new StudyImages();
		back_trunk.setName("back_trunk");
		back_trunk.setDescription("Back Trunk");
		back_trunk.setStudyId(1l);
		back_trunk.setImageUrl("body_parts/back_trunk"+"/"+savedUser.getId());
		back_trunk.setUserId(savedUser.getId());
		back_trunk.setCount(0);
		imageRepository.save(back_trunk);

		StudyImages front_of_arms = new StudyImages();
		front_of_arms.setName("front_of_arms");
		front_of_arms.setDescription("Front of Arms");
		front_of_arms.setStudyId(1l);
		front_of_arms.setImageUrl("body_parts/front_of_arms"+"/"+savedUser.getId());
		front_of_arms.setUserId(savedUser.getId());
		front_of_arms.setCount(0);
		imageRepository.save(front_of_arms);

		StudyImages back_of_arms = new StudyImages();
		back_of_arms.setName("back_of_arms");
		back_of_arms.setDescription("Back of Arms");
		back_of_arms.setStudyId(1l);
		back_of_arms.setImageUrl("body_parts/front_of_arms"+"/"+savedUser.getId());
		back_of_arms.setUserId(savedUser.getId());
		back_of_arms.setCount(0);
		imageRepository.save(back_of_arms);

		StudyImages front_of_hands = new StudyImages();
		front_of_hands.setName("front_of_hands");
		front_of_hands.setDescription("Front of Hands");
		front_of_hands.setStudyId(1l);
		front_of_hands.setImageUrl("body_parts/front_of_hands"+"/"+savedUser.getId());
		front_of_hands.setUserId(savedUser.getId());
		front_of_hands.setCount(0);
		imageRepository.save(front_of_hands);

		StudyImages back_of_hands = new StudyImages();
		back_of_hands.setName("back_of_hands");
		back_of_hands.setDescription("Back of Hands");
		back_of_hands.setStudyId(1l);
		back_of_hands.setImageUrl("body_parts/back_of_hands"+"/"+savedUser.getId());
		back_of_hands.setUserId(savedUser.getId());
		back_of_hands.setCount(0);
		imageRepository.save(back_of_hands);

		StudyImages front_of_legs = new StudyImages();
		front_of_legs.setName("front_of_legs");
		front_of_legs.setDescription("Front of Legs");
		front_of_legs.setStudyId(1l);
		front_of_legs.setImageUrl("body_parts/front_of_legs"+"/"+savedUser.getId());
		front_of_legs.setUserId(savedUser.getId());
		front_of_legs.setCount(0);
		imageRepository.save(front_of_legs);

		StudyImages back_of_legs = new StudyImages();
		back_of_legs.setName("back_of_legs");
		back_of_legs.setDescription("Back of Legs");
		back_of_legs.setStudyId(1l);
		back_of_legs.setImageUrl("body_parts/back_of_legs"+"/"+savedUser.getId());
		back_of_legs.setUserId(savedUser.getId());
		back_of_legs.setCount(0);
		imageRepository.save(back_of_legs);

		StudyImages front_of_feet = new StudyImages();
		front_of_feet.setName("front_of_feet");
		front_of_feet.setDescription("Front of Feet");
		front_of_feet.setStudyId(1l);
		front_of_feet.setImageUrl("body_parts/front_of_feet"+"/"+savedUser.getId());
		front_of_feet.setUserId(savedUser.getId());
		front_of_feet.setCount(0);
		imageRepository.save(front_of_feet);

		StudyImages back_of_feet = new StudyImages();
		back_of_feet.setName("back_of_feet");
		back_of_feet.setDescription("Back of Feet");
		back_of_feet.setStudyId(1l);
		back_of_feet.setImageUrl("body_parts/back_of_feet"+"/"+savedUser.getId());
		back_of_feet.setUserId(savedUser.getId());
		back_of_feet.setCount(0);
		imageRepository.save(back_of_feet);

		StudyImages special_areas = new StudyImages();
		special_areas.setName("special_areas");
		special_areas.setDescription("Special Areas of Interest");
		special_areas.setStudyId(1l);
		special_areas.setImageUrl("body_parts/special_areas"+"/"+savedUser.getId());
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
		List<Map<String, Object>> patientList = jdbcTemplate.queryForList("SELECT * FROM user_roles ur JOIN user_metadata umd ON ur.user_id = umd.user_id and  umd.study_status = 0 and ur.role_id = 2;");
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
