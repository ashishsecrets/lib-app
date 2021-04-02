package com.ucsf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ucsf.config.JwtConfig;
import com.ucsf.config.JwtTokenUtil;
import com.ucsf.payload.AuthRequest;
import com.ucsf.payload.AuthResponse;
import com.ucsf.payload.UserDto;
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

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

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
	public ResponseEntity<?> saveUser(@RequestBody UserDto user) throws Exception {
		loggerService.printLogs(log, "saveUser", "Register User");
		return ResponseEntity.ok(userDetailsService.save(user));
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
