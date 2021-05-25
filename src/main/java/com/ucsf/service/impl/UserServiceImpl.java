package com.ucsf.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import com.ucsf.model.StudyImages;
import com.ucsf.model.UserSurveyStatus;
import com.ucsf.repository.*;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
import com.ucsf.payload.response.PatientResponse;
import com.ucsf.payload.response.UserDataResponse;
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
	UserSurveyStatusRepository userSurveyStatusRepository;

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
				} else {
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
		if (user.getDateOfBirth() != null) {
			metadata.setDateOfBith(user.getDateOfBirth());
			metadata.setAge(AppUtil.getAge(user.getDateOfBirth()));
		}
		userMetaDataRepository.save(metadata);
		// save metadata in metadatarepo
		// newUser.setMetadata(metadata);

		UserScreeningStatus userScreeningStatus = new UserScreeningStatus();
		userScreeningStatus.setStudyId(1l);
		userScreeningStatus.setUserScreeningStatus(UserScreenStatus.NEWLY_ADDED);
		userScreeningStatus.setIndexValue(1);
		userScreeningStatus.setUserId(savedUser.getId());
		userScreeningStatusRepository.save(userScreeningStatus);

		/*
		 * UserSurveyStatus userSurveyStatus = new UserSurveyStatus();
		 * userSurveyStatus.setSurveyId(1l);
		 * userSurveyStatus.setUserSurveyStatus(UserSurveyStatus.SurveyStatus.
		 * NEWLY_ADDED); userSurveyStatus.setIndexValue(1);
		 * userSurveyStatus.setUserId(savedUser.getId());
		 * userSurveyStatusRepository.save(userSurveyStatus);
		 */

		// New list updated by client
		/*
		 * “Full body front”, ” Full body back ", "Front Trunk", ”Back Trunk” , "Front
		 * of Arms", ”Back of Arms”, ”Front of Hands”, "Back of Hands", "Front of
		 * Legs", ”Back of Legs”, "Front of Feet", ”Back of Feet” "Special areas of
		 * interest”
		 */
		// Making the changes below
		StudyImages full_body_front = new StudyImages();
		full_body_front.setName("Full body front");
		full_body_front.setDescription("");
		full_body_front.setStudyId(1l);
		full_body_front.setImageUrl("body_parts/full_body_front" + "/" + savedUser.getId());
		full_body_front.setUserId(savedUser.getId());
		full_body_front.setCount(0);
		imageRepository.save(full_body_front);

		StudyImages full_body_back = new StudyImages();
		full_body_back.setName("Full body back");
		full_body_back.setDescription("");
		full_body_back.setStudyId(1l);
		full_body_back.setImageUrl("body_parts/full_body_back" + "/" + savedUser.getId());
		full_body_back.setUserId(savedUser.getId());
		full_body_back.setCount(0);
		imageRepository.save(full_body_back);

		StudyImages front_trunk = new StudyImages();
		front_trunk.setName("Front Trunk");
		front_trunk.setDescription("");
		front_trunk.setStudyId(1l);
		front_trunk.setImageUrl("body_parts/front_trunk" + "/" + savedUser.getId());
		front_trunk.setUserId(savedUser.getId());
		front_trunk.setCount(0);
		imageRepository.save(front_trunk);

		StudyImages back_trunk = new StudyImages();
		back_trunk.setName("Back Trunk");
		back_trunk.setDescription("");
		back_trunk.setStudyId(1l);
		back_trunk.setImageUrl("body_parts/back_trunk" + "/" + savedUser.getId());
		back_trunk.setUserId(savedUser.getId());
		back_trunk.setCount(0);
		imageRepository.save(back_trunk);

		StudyImages front_of_arms = new StudyImages();
		front_of_arms.setName("Front of Arms");
		front_of_arms.setDescription("");
		front_of_arms.setStudyId(1l);
		front_of_arms.setImageUrl("body_parts/front_of_arms" + "/" + savedUser.getId());
		front_of_arms.setUserId(savedUser.getId());
		front_of_arms.setCount(0);
		imageRepository.save(front_of_arms);

		StudyImages back_of_arms = new StudyImages();
		back_of_arms.setName("Back of Arms");
		back_of_arms.setDescription("");
		back_of_arms.setStudyId(1l);
		back_of_arms.setImageUrl("body_parts/front_of_arms" + "/" + savedUser.getId());
		back_of_arms.setUserId(savedUser.getId());
		back_of_arms.setCount(0);
		imageRepository.save(back_of_arms);

		StudyImages front_of_hands = new StudyImages();
		front_of_hands.setName("Front of Hands");
		front_of_hands.setDescription("");
		front_of_hands.setStudyId(1l);
		front_of_hands.setImageUrl("body_parts/front_of_hands" + "/" + savedUser.getId());
		front_of_hands.setUserId(savedUser.getId());
		front_of_hands.setCount(0);
		imageRepository.save(front_of_hands);

		StudyImages back_of_hands = new StudyImages();
		back_of_hands.setName("Back of Hands");
		back_of_hands.setDescription("");
		back_of_hands.setStudyId(1l);
		back_of_hands.setImageUrl("body_parts/back_of_hands" + "/" + savedUser.getId());
		back_of_hands.setUserId(savedUser.getId());
		back_of_hands.setCount(0);
		imageRepository.save(back_of_hands);

		StudyImages front_of_legs = new StudyImages();
		front_of_legs.setName("Front of Legs");
		front_of_legs.setDescription("");
		front_of_legs.setStudyId(1l);
		front_of_legs.setImageUrl("body_parts/front_of_legs" + "/" + savedUser.getId());
		front_of_legs.setUserId(savedUser.getId());
		front_of_legs.setCount(0);
		imageRepository.save(front_of_legs);

		StudyImages back_of_legs = new StudyImages();
		back_of_legs.setName("Back of Legs");
		back_of_legs.setDescription("");
		back_of_legs.setStudyId(1l);
		back_of_legs.setImageUrl("body_parts/back_of_legs" + "/" + savedUser.getId());
		back_of_legs.setUserId(savedUser.getId());
		back_of_legs.setCount(0);
		imageRepository.save(back_of_legs);

		StudyImages front_of_feet = new StudyImages();
		front_of_feet.setName("Front of Feet");
		front_of_feet.setDescription("");
		front_of_feet.setStudyId(1l);
		front_of_feet.setImageUrl("body_parts/front_of_feet" + "/" + savedUser.getId());
		front_of_feet.setUserId(savedUser.getId());
		front_of_feet.setCount(0);
		imageRepository.save(front_of_feet);

		StudyImages back_of_feet = new StudyImages();
		back_of_feet.setName("Back of Feet");
		back_of_feet.setDescription("");
		back_of_feet.setStudyId(1l);
		back_of_feet.setImageUrl("body_parts/back_of_feet" + "/" + savedUser.getId());
		back_of_feet.setUserId(savedUser.getId());
		back_of_feet.setCount(0);
		imageRepository.save(back_of_feet);

		StudyImages special_areas = new StudyImages();
		special_areas.setName("Special Areas of Interest");
		special_areas.setDescription("");
		special_areas.setStudyId(1l);
		special_areas.setImageUrl("body_parts/special_areas" + "/" + savedUser.getId());
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
		newUser.setEmail(user.getEmail());
		newUser.setPassword(bcryptEncoder.encode("12345"));
		newUser.setPhoneCode(user.getPhoneCode());
		newUser.setPhoneNumber(user.getPhone());
		// Add Role
		if (user.getUserRoles() != null && user.getUserRoles() != "") {
			if (user.getUserRoles().equals("PHYSICIAN")) {
				newUser.getRoles().add(roleRepository.findByName(RoleName.PHYSICIAN));
			}
			if (user.getUserRoles().equals("STUDYTEAM")) {
				newUser.getRoles().add(roleRepository.findByName(RoleName.STUDY_TEAM));
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
		Optional<User> existed = userRepository.findById(userId);
		User user = null;
		if (existed.isEmpty()) {
			return user;
		} else {
			user = existed.get();
			user.setEmail(updateRequest.getEmail() != null ? updateRequest.getEmail() : user.getEmail());
			user.setFirstName(
					updateRequest.getFirstName() != null ? updateRequest.getFirstName() : user.getFirstName());
			user.setLastName(updateRequest.getLastName() != null ? updateRequest.getLastName() : user.getLastName());
			if (updateRequest.getUserRoles() != null) {
				Set<Role> newRole = new HashSet<Role>();
				if (updateRequest.getUserRoles().equals("PHYSICIAN")) {
					newRole.add(roleRepository.findByName(RoleName.PHYSICIAN));
					user.setRoles(newRole);
				}
				if (updateRequest.getUserRoles().equals("STUDY_TEAM")) {
					newRole.add(roleRepository.findByName(RoleName.STUDY_TEAM));
					user.setRoles(newRole);
				}
			}
			user.setPhoneCode(
					updateRequest.getPhoneCode() != null ? updateRequest.getPhoneCode() : user.getPhoneCode());
			user.setPhoneNumber(updateRequest.getPhone() != null ? updateRequest.getPhone() : user.getPhoneNumber());
			userRepository.save(user);
			return user;
		}
	}

	@Override
	public List<User> getPatients() {
		List<Map<String, Object>> patientList = jdbcTemplate.queryForList(
				"SELECT * FROM user_roles ur JOIN user_screening_status uss ON ur.user_id = uss.user_id and  uss.user_screening_status = 2 and ur.role_id = 2 ORDER BY ur.user_id DESC;");
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

	@Override
	public List<User> getStudyTeam() {
		List<Map<String, Object>> studyTeam = jdbcTemplate.queryForList(
				"SELECT * FROM user_roles ur JOIN users u ON ur.user_id = u.user_id  and ur.role_id IN(3,4) ORDER BY ur.user_id DESC");
		List<User> users = new ArrayList<User>();
		Long userId = 0l;
		Optional<User> user = null;
		User teamMember = null;
		for (Map<String, Object> map : studyTeam) {
			if (map.get("user_id") != null) {
				userId = Long.parseLong(map.get("user_id").toString());
				user = userRepository.findById(userId);
				if (user.isPresent()) {
					teamMember = user.get();
					users.add(teamMember);
				}
			}
		}
		return users;
	}

	@Override
	public List<PatientResponse> getApprovedPatients() {

		List<Map<String, Object>> patientList = jdbcTemplate.queryForList(
				"SELECT * FROM user_roles ur JOIN user_screening_status uss ON ur.user_id = uss.user_id and  uss.user_screening_status = 3 and ur.role_id = 2 ORDER BY uss.last_modified_date DESC;");
		List<PatientResponse> patients = new ArrayList<PatientResponse>();
		Long userId = 0l;
		String updatedAt = "";
		String updatedBy = "";
		Optional<User> user = null;
		for (Map<String, Object> map : patientList) {
			if (map.get("user_id") != null) {
				userId = Long.parseLong(map.get("user_id").toString());
				updatedBy = map.get("last_modified_by") != null ? map.get("last_modified_by").toString() : "";
				System.out.println(updatedBy);
				updatedAt = map.get("status_updated_date") != null ? map.get("status_updated_date").toString() : "";
				System.out.println(updatedAt);
				int weeks = 1;
				user = userRepository.findById(userId);
				if (user.isPresent()) {
					PatientResponse patient = new PatientResponse();
					try {
						User exited = user.get();
						patient.setEmail(exited.getEmail());
						patient.setId(exited.getId());
						patient.setFirstName(exited.getFirstName());
						patient.setLastName(exited.getLastName());
						patient.setPhoneNumber(exited.getPhoneCode() + exited.getPhoneNumber());
						patient.setUpdatedAt(updatedAt);
						patient.setUpdatedBy(updatedBy);
						patient.setStudyWeek(weeks + 1);
						
						if (map.get("status_updated_date") != null) {

							DateTime statusUpdateDate = new DateTime(new SimpleDateFormat("yyyy-MM-dd")
									.parse(map.get("status_updated_date").toString()));
							DateTime currDate = new DateTime(new Date());
							weeks = Weeks.weeksBetween(statusUpdateDate, currDate).getWeeks();
						}
						patients.add(patient);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
		return patients;
	}

	@Override
	public List<PatientResponse> getDisapprovedPatients() {

		List<Map<String, Object>> patientList = jdbcTemplate.queryForList(
				"SELECT * FROM user_roles ur JOIN user_screening_status uss ON ur.user_id = uss.user_id and  uss.user_screening_status = 8 and ur.role_id = 2 ORDER BY uss.last_modified_date DESC;");
		List<PatientResponse> patients = new ArrayList<PatientResponse>();
		Long userId = 0l;
		String updatedAt = "";
		String updatedBy = "";
		Optional<User> user = null;
		User exited = new User();
		for (Map<String, Object> map : patientList) {
			if (map.get("user_id") != null) {
				userId = Long.parseLong(map.get("user_id").toString());
				updatedBy = map.get("last_modified_by") != null ? map.get("last_modified_by").toString() : "";
				System.out.println(updatedBy);
				updatedAt = map.get("status_updated_date") != null ? map.get("status_updated_date").toString() : "";
				System.out.println(updatedAt);
				int weeks = 1;
				user = userRepository.findById(userId);
				if (user.isPresent()) {
					PatientResponse patient = new PatientResponse();
					try {
						patient.setEmail(user.get().getEmail());
						patient.setId(user.get().getId());
						patient.setFirstName(user.get().getFirstName());
						patient.setLastName(user.get().getLastName());
						patient.setPhoneNumber(user.get().getPhoneCode() + user.get().getPhoneNumber());
						patient.setUpdatedAt(updatedAt);
						patient.setUpdatedBy(updatedBy);
						patient.setStudyWeek(weeks + 1);
						if (map.get("status_updated_date") != null) {

							DateTime statusUpdateDate = new DateTime(new SimpleDateFormat("yyyy-MM-dd")
									.parse(map.get("status_updated_date").toString()));
							DateTime currDate = new DateTime(new Date());
							weeks = Weeks.weeksBetween(statusUpdateDate, currDate).getWeeks();
						}
						patients.add(patient);
					} 
					
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
			
		}
		return patients;
	}

	@Override
	public UserScreeningStatus getUserStatus(Long userId) {
		UserScreeningStatus status = new UserScreeningStatus();
		// metadata =
		// userMetaDataRepository.findByStudyStatusAndUserId(StudyStatus.ENROLLED,userId);
		status = userScreeningStatusRepository.findByUserId(userId);
		if (status == null) {
			return null;
		} else {
			return status;
		}
	}

	@Override
	public List<UserDataResponse> getUserById(Long userId) {
		String sql = "SELECT * FROM users u JOIN user_metadata um ON u.user_id = um.user_id JOIN user_screening_status uss ON u.user_id = uss.user_id JOIN ucsf_studies us ON us.study_id = uss.study_id where u.user_id = "
				+ userId;
		List<UserDataResponse> patientList = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<UserDataResponse>(UserDataResponse.class));

		return patientList;
	}

	@Override
	public User findById(Long id) {
		Optional<User> user = null;
		user = userRepository.findById(id);
		if (user.isPresent()) {
			return user.get();
		}
		return user.get();
	}

	@Override
	public User updateUserStatus(Long userId, Boolean status) {
		Optional<User> user = null;
		User existed = null;
		user = userRepository.findById(userId);
		if (user.isPresent()) {
			existed = user.get();
			if (status) {
				existed.setUserStatus(UserStatus.ACTIVE);
			} else {
				existed.setUserStatus(UserStatus.DEACTIVE);
			}
			if (existed != null) {
				userRepository.save(existed);
			}
		}
		return existed;
	}
}
