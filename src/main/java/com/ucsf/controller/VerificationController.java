package com.ucsf.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucsf.auth.model.User;
import com.ucsf.config.JwtConfig;
import com.ucsf.config.JwtTokenUtil;
import com.ucsf.payload.AuthResponse;
import com.ucsf.payload.VerifyRequest;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.CustomUserDetailsService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.VerificationService;

@RestController
@RequestMapping("/api")
public class VerificationController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	VerificationService verificationService;
	
	@Autowired
	JwtConfig jwtConfig;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	
	@Autowired 
	private LoggerService loggerService;
	
	private static Logger log = LoggerFactory.getLogger(VerificationController.class);
	
	@PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
	@RequestMapping(value = "/verifyOtp", method = RequestMethod.POST)
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyRequest verifyRequest) {
		
		loggerService.printLogs(log, "verifyOtp", "Verify Otp sent by Authy to user's phone number");
		
		JSONObject jsonObject = null;
		 String token = "";
		if (verifyRequest.getUsername() != null && verifyRequest.getCode() != null
				&& verifyRequest.getCode().length() > 0) {
			User user = userRepository.findByUsername(verifyRequest.getUsername());
			if (user == null) {
				return ResponseEntity.ok(new AuthResponse(null, false,"User not Found"));
			}
			try {
				jsonObject = verificationService.otpCodeVerification(user, verifyRequest.getCode());
               // to do check if verification response is success
				user.setIsVerified(true);
				userRepository.save(user);
				final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
			    token = jwtTokenUtil.generateToken(userDetails);
			    user.setAuthToken(token);
				userRepository.save(user);

				System.out.println(jsonObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok(new AuthResponse(token, true,"OTP verified successfully!"));
	}

}
