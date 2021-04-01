package com.ucsf.controller;

import java.util.Date;

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

import com.ucsf.auth.model.TwoFactorAuthentication;
import com.ucsf.auth.model.User;
import com.ucsf.config.JwtTokenUtil;
import com.ucsf.payload.AuthRequest;
import com.ucsf.payload.AuthResponse;
import com.ucsf.payload.UserDto;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.VerificationRepository;
import com.ucsf.service.CustomUserDetailsService;
import com.ucsf.service.VerificationService;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class UcsfAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired VerificationRepository verificationRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	VerificationService verificationService;
	
	private boolean is2faEnabled = true;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest)
			throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		User user = userRepository.findByUsername(userDetails.getUsername());

		user.setAuthToken(token);

		userRepository.save(user);
		
		if(is2faEnabled) {
			TwoFactorAuthentication factorAuthentication = verificationRepository.findByUserId(user.getId());
			if(factorAuthentication != null) {
				verificationService.sendVerificationCode(user);
				factorAuthentication.setCode("1234");
				factorAuthentication.setExpiredAt(new Date()); // 15 mints
				factorAuthentication.setCreatedAt(new Date());
				verificationRepository.save(factorAuthentication);
			} else {
				factorAuthentication =  new TwoFactorAuthentication();
				verificationService.sendVerificationCode(user);
				factorAuthentication.setCode("1234");
				factorAuthentication.setExpiredAt(new Date());
				factorAuthentication.setCreatedAt(new Date());
				factorAuthentication.setUserId(user.getId());
				verificationRepository.save(factorAuthentication);
			}
		}
		
		if(is2faEnabled) {
			return ResponseEntity.ok(new AuthResponse(token, false));
		}
		return ResponseEntity.ok(new AuthResponse(token, true));
		
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDto user) throws Exception {
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
