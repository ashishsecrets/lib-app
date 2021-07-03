package com.ucsf.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.ucsf.auth.model.Role;
import com.ucsf.auth.model.RoleName;
import com.ucsf.service.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.common.ErrorCodes;
import com.ucsf.common.Constants;

import com.ucsf.config.JwtConfig;
import com.ucsf.config.JwtTokenUtil;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.payload.request.AuthRequest;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.payload.response.AuthResponse;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.repository.StudyRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.UserScreeningStatusRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
@Api(tags = "Ucsf-Authentication Controller")
public class UcsfAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;

	@Autowired
	VerificationService verificationService;

	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	StudyRepository studyRepository;

	@Autowired
	FirebaseService firebaseService;

	@Autowired
	private LoggerService loggerService;

	private static String ROLE_PREFIX = "ROLE_";

	private static Logger log = LoggerFactory.getLogger(UcsfAuthenticationController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Authenticate user", notes = "Authenticate user with email and password", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User authenticated successfully", response = User.class) })
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest)
			throws Exception {
		loggerService.printLogs(log, "createAuthenticationToken", "Start user login and create auth token");
		JSONObject responseJson = new JSONObject();
		String message = "";
		String fireBaseSignIn;
		Boolean isVerified = false;
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		User user = userRepository.findByEmail(authenticationRequest.getEmail());
		if (user == null) {
			responseJson.put("error",
					new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(), Constants.USER_NOT_FOUND.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
		for (Role role : user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<Role>()) {
			if (role.getName().toString().equals("ADMIN")) {
				grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName().toString()));
				isVerified = true;
				jwtConfig.setTwoFa(false);
			}
		}

		UserDetails userDetails = userDetailsService.loadUserByEmail(authenticationRequest.getEmail(), isVerified);

		authenticate(userDetails.getUsername(), authenticationRequest.getPassword());

		final String token = jwtTokenUtil.generateToken(userDetails);

		user.setAuthToken(token);
		user.setDevideId(authenticationRequest.getDeviceId());
		user = userRepository.save(user);
		String status = "";
		UserScreeningStatus userStatus = userScreeningStatusRepository.findByUserId(user.getId());
		if(userStatus != null && userStatus.getUserScreeningStatus() != null) {
			 status = userStatus.getUserScreeningStatus().toString();
		}
		for (Role role : user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<Role>()) {
			if (role.getName().toString().equals("ADMIN")) {
				jwtConfig.setTwoFa(false);
			}
		}

		try {
			SignUpRequest signUpRequest = new SignUpRequest();
			signUpRequest.setPassword(authenticationRequest.getPassword());
			for (Role role : user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<Role>()) {
				if (role.getName().toString().equals("ADMIN") || role.getName().toString().equals("PHYSICIAN") || role.getName().toString().equals("STUDYTEAM")) {
					ArrayList<String> x = new ArrayList<>();
					x.add(role.getName().toString());

					signUpRequest.setUserRoles(x);
					firebaseService.createUser(user, signUpRequest);
				}
				else if (role.getName().toString().equals("PRE_VERIFICATION_USER") || role.getName().toString().equals("PATIENT")){
					try {
						firebaseService.createChatRoom(user);
						firebaseService.sendInitialMessage(user);
					} catch (InterruptedException e) {
						System.out.println(e);
					} catch (ExecutionException e) {
						System.out.println(e);
					}

					firebaseService.createUser(user, signUpRequest);
					firebaseService.updateFireUser(user, signUpRequest);
				}
			}
		}
		catch (FirebaseAuthException ex){
			System.out.println(ex);
			/*responseJson.put("error",
					new ErrorResponse(111, ex.getMessage()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);*/
		}

		if (jwtConfig.getTwoFa()) {
			message = "You have to be verified by 2FA";
			responseJson.put("data", new AuthResponse(userDetails, user, message,status, firebaseService.signInUser(user)));
			return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);
		} else {
			message = "User Authenticated Successfully!";
		}


		responseJson.put("data", new AuthResponse(userDetails, user, message,status, firebaseService.signInUser(user)));
		return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Register user", notes = "Register user", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User registered successfully", response = User.class) })
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody SignUpRequest signUpRequest) throws Exception {
		loggerService.printLogs(log, "saveUser", "Register User" + signUpRequest.toString());
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		String message = "";
		boolean isEnable = true;
		boolean isUserNotExpired = true;
		boolean isCredentialNotExpired = true;
		boolean isAccountNotLocked = true;
		jwtConfig.setTwoFa(true);

		JSONObject responseJson = new JSONObject();
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			responseJson.put("error",
					new ErrorResponse(ErrorCodes.EMAIL_ALREADY_USED.code(), Constants.EMAIL_ALREADY_USED.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}

		if (studyRepository.findAll().size() < 1) {
			responseJson.put("error",
					new ErrorResponse(ErrorCodes.NO_STUDY_FOUND.code(), Constants.NO_STUDY_FOUND.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}

		User user = userService.save(signUpRequest);
		if(user==null) {
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_DOB.code(), Constants.INVALID_DOB.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}

		for (Role role : user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<Role>()) {
			if (role.getName().toString().equals("ADMIN")) {
				jwtConfig.setTwoFa(false);
				grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName().toString()));
			}
		}

		if (jwtConfig.getTwoFa()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + RoleName.PRE_VERIFICATION_USER.toString()));
			message = "You have to be verified by 2FA";
		} else {
			message = "User Registered Succcessfully!";
		}

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
				user.getPassword(), isEnable, isUserNotExpired, isCredentialNotExpired, isAccountNotLocked,
				grantedAuthorities);

		try {
			firebaseService.createUser(user, signUpRequest);
			firebaseService.createChatRoom(user);
			firebaseService.sendInitialMessage(user);
		}
		catch (FirebaseAuthException ex){
			System.out.println(ex);
			responseJson.put("error",
					new ErrorResponse(ErrorCodes.INVALID_PHONE.code(), ex.getMessage()));
			responseJson.remove("data");
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}

		final String token = jwtTokenUtil.generateToken(userDetails);
		user.setAuthToken(token);
		responseJson.put("data", new AuthResponse(userDetails, user, message,null, firebaseService.signInUser(user)));
		loggerService.printLogs(log, "saveUser", "User Registered Successfully");




		return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
