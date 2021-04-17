package com.ucsf.controller;

import com.ucsf.auth.model.RoleName;
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
import com.ucsf.payload.request.AuthRequest;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.payload.response.AuthResponse;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.CustomUserDetailsService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.VerificationService;

import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class UcsfAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	VerificationService verificationService;

	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	private LoggerService loggerService;

	private static String ROLE_PREFIX = "ROLE_";

	private static Logger log = LoggerFactory.getLogger(UcsfAuthenticationController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest)
			throws Exception {
		loggerService.printLogs(log, "createAuthenticationToken", "Start user login and create auth token");
		JSONObject responseJson = new JSONObject();
		User user = userRepository.findByEmail(authenticationRequest.getEmail());
		if (user == null) {
			responseJson.put("error",
					new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(), Constants.USER_NOT_FOUND.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}

		UserDetails userDetails = userDetailsService.loadUserByEmail(authenticationRequest.getEmail(),false);

		authenticate(userDetails.getUsername(), authenticationRequest.getPassword());

		final String token = jwtTokenUtil.generateToken(userDetails);

		user.setAuthToken(token);

		userRepository.save(user);

		if (jwtConfig.getTwoFa()) {
			verificationService.sendVerificationCode(user);
		}
		if (jwtConfig.getTwoFa()) {
			responseJson.put("data", new AuthResponse(userDetails,user,"You have to be vrified by 2FA"));
			return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);
		}
		responseJson.put("data", new AuthResponse(userDetails,user,"You have to be vrified by 2FA"));
		return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody SignUpRequest signUpRequest) throws Exception {
		loggerService.printLogs(log, "saveUser", "Register User");

		JSONObject responseJson = new JSONObject();
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			responseJson.put("error",
					new ErrorResponse(ErrorCodes.EMAIL_ALREADY_USED.code(), Constants.EMAIL_ALREADY_USED.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}

		User user = userDetailsService.save(signUpRequest);

		if (jwtConfig.getTwoFa()) {
			verificationService.sendVerificationCode(user);
		}

		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		boolean isEnable = true;
		boolean isUserNotExpired = true;
		boolean isCredentialNotExpired = true;
		boolean isAccountNotLocked = true;
		jwtConfig.setTwoFa(true);
		grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + RoleName.PRE_VERIFICATION_USER.toString()));

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
				user.getPassword(), isEnable, isUserNotExpired, isCredentialNotExpired, isAccountNotLocked,
				grantedAuthorities);

		final String token = jwtTokenUtil.generateToken(userDetails);

		user.setAuthToken(token);
		responseJson.put("data", user);
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
