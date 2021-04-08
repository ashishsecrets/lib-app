package com.ucsf.controller;

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
import com.ucsf.payload.response.ApiError;
import com.ucsf.payload.response.AuthResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.CustomUserDetailsService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.VerificationService;

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
	
	private static Logger log = LoggerFactory.getLogger(UcsfAuthenticationController.class);

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest)
			throws Exception {
		loggerService.printLogs(log, "createAuthenticationToken", "Start user login and create auth token");


		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		if(userDetails == null){
			userDetails = userDetailsService.loadUserByEmail(authenticationRequest.getUsername());
		}

		authenticate(userDetails.getUsername(), authenticationRequest.getPassword());


		final String token = jwtTokenUtil.generateToken(userDetails);

		User user = userRepository.findByUsername(userDetails.getUsername());

		user.setAuthToken(token);

		userRepository.save(user);

		if (jwtConfig.getTwoFa()) {
			verificationService.sendVerificationCode(user);
		}
		if (jwtConfig.getTwoFa()) {
			return ResponseEntity.ok(new AuthResponse(token, false,"Verify OTP"));
		}
		return ResponseEntity.ok(new AuthResponse(token, true,"Signed in Successfully!"));
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody SignUpRequest signUpRequest) throws Exception {
		loggerService.printLogs(log, "saveUser", "Register User");
		
		JSONObject responseJson = new JSONObject();
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			responseJson.put("error",
					new ApiError(ErrorCodes.USERNAME_ALREADY_USED.code(), Constants.USERNAME_ALREADY_USED.errordesc()));
			return new ResponseEntity(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			responseJson.put("error",
					new ApiError(ErrorCodes.EMAIL_ALREADY_USED.code(), Constants.EMAIL_ALREADY_USED.errordesc()));
			return new ResponseEntity(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}
		
		return ResponseEntity.ok(userDetailsService.save(signUpRequest));
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
