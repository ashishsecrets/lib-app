package com.ucsf.controller;

import java.io.IOException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.config.JwtConfig;
import com.ucsf.config.JwtTokenUtil;
import com.ucsf.payload.request.VerifyRequest;
import com.ucsf.payload.response.AuthResponse;
import com.ucsf.payload.response.ErrorResponse;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
	@RequestMapping(value = "/verifyOtp", method = RequestMethod.POST)
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyRequest verifyRequest) {

		loggerService.printLogs(log, "verifyOtp", "Verify Otp sent by Authy to user's phone number");
		User user = null;
		UserDetails userDetails = null;
		JSONObject jsonObject = null;
		JSONObject responseJson = new JSONObject();
		String token = "";
		if (verifyRequest.getEmail() != null && verifyRequest.getCode() != null
				&& verifyRequest.getCode().length() > 0) {
			user = userRepository.findByEmail(verifyRequest.getEmail());
			if (user == null) {
				responseJson.put("error",
						new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(), Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
			}
			try {
				jsonObject = verificationService.otpCodeVerification(user, verifyRequest.getCode());
				if (jsonObject.get("success").equals(true)) {

					userDetails = userDetailsService.loadUserByEmail(user.getEmail(),true);
					token = jwtTokenUtil.generateToken(userDetails);
					user.setAuthToken(token);
					userRepository.save(user);

				} else {
					responseJson.put("error",
							new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(), Constants.USER_NOT_FOUND.errordesc()));
					return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		responseJson.put("data", new AuthResponse(userDetails,user, "User verified"));
		return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);

	}
}
