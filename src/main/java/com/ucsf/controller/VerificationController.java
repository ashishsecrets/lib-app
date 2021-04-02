package com.ucsf.controller;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.payload.VerifyRequest;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.VerificationRepository;
import com.ucsf.service.VerificationService;

@RestController
@RequestMapping("/api")
public class VerificationController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	VerificationRepository verificationRepository;

	@Autowired
	VerificationService verificationService;

	@PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public String verify(@RequestBody VerifyRequest verifyRequest) {
		JSONObject jsonObject = null;
		if (verifyRequest.getUsername() != null && verifyRequest.getCode() != null
				&& verifyRequest.getCode().length() > 0) {
			User user = userRepository.findByUsername(verifyRequest.getUsername());
			if (user == null) {
				return "fail";
			}
			try {
				jsonObject = verificationService.otpCodeVerification(user, verifyRequest.getCode());
				//update user role
				System.out.println(jsonObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "Success";
	}

}
