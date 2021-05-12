package com.ucsf.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.Role;
import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.config.JwtConfig;
import com.ucsf.config.JwtTokenUtil;
import com.ucsf.payload.request.AddUserRequest;
import com.ucsf.payload.request.UserUpdateRequest;
import com.ucsf.payload.response.AuthResponse;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
@Api(tags = "Admin Controller")
public class AdminController {

	@Autowired
	UserService userService;

	@Autowired
	EmailService emailService;

	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	UserRepository userRepository;

	private static String ROLE_PREFIX = "ROLE_";

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	LoggerService loggerService;

	@Value("${spring.mail.from}")
	String fromEmail;

	private static Logger log = LoggerFactory.getLogger(ConsentController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "add users", notes = "add users", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Add users", response = User.class) })
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/addUsers", method = RequestMethod.POST)
	public ResponseEntity<?> addUsers(@RequestBody AddUserRequest signUpRequest) {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		String message = "User Registered by Admin";
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

		User user = userService.addUser(signUpRequest);
		for (Role role : user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<Role>()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName().toString()));
		}

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
				user.getPassword(), isEnable, isUserNotExpired, isCredentialNotExpired, isAccountNotLocked,
				grantedAuthorities);

		final String token = jwtTokenUtil.generateToken(userDetails);
		user.setAuthToken(token);
		try {
			emailService.sendCredsToUsersAddedByAdmin(fromEmail, user.getEmail(), "User Registered",
					user.getFirstName(), signUpRequest.getPassword());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		responseJson.put("data", new AuthResponse(userDetails, user, message));
		return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "getLoggedInUser", notes = "getLoggedInUser", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "getLoggedInUser", response = User.class) })
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/getLoggedInUser", method = RequestMethod.GET)
	public ResponseEntity<?> getLoggedInUser() {
		User user = null;
		JSONObject response = new JSONObject();
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			response.put("data", user);
			loggerService.printLogs(log, "getLoggedInUser", "getLoggedInUser " + user.getId());
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		} else {
			loggerService.printLogs(log, "getAllPatients", "Invalid JWT signature.");
			response.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(response.toMap(), HttpStatus.UNAUTHORIZED);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "Update users", notes = "Update users", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Update users", response = SuccessResponse.class) })
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/updateUser/{userId}", method = RequestMethod.POST)
	public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest updateUser) {
		User user = null;
		JSONObject responseJson = new JSONObject();

		try {
			user = userService.updateUser(userId, updateUser);
			if (user == null) {
				responseJson.put("error",
						new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(), Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
			} else {
				responseJson.put("data", new SuccessResponse(true, "User updated successfully!"));
				loggerService.printLogs(log, "updateUser",
						"User Updated succseefully with request " + updateUser.toString() + "of UserId " + userId);
				return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
			}

		} catch (Exception e) {
			loggerService.printErrorLogs(log, "updateUser",
					"Error while updating user with request " + updateUser.toString() + "of UserId " + userId);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
}
