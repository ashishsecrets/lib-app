package com.ucsf.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.Role;
import com.ucsf.auth.model.RoleName;
import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.config.JwtConfig;
import com.ucsf.config.JwtTokenUtil;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.payload.response.AuthResponse;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
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
	
	@Value("${spring.mail.from}")
	String fromEmail;


	@ApiOperation(value = "add users", notes = "add users", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Add users", response = User.class) })
    @PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/addUsers", method = RequestMethod.POST)
	public ResponseEntity<?> getUsers(@RequestBody SignUpRequest signUpRequest) {
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
			emailService.sendCredsToUsersAddedByAdmin(fromEmail,user.getEmail(),"User Registered",user.getFirstName(),user.getPassword());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		responseJson.put("data", new AuthResponse(userDetails, user, message));
		return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);
	}

}
