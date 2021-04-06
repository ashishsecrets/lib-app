package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.payload.ResetPasswordResponse;
import com.ucsf.payload.VerifyPasswordRequest;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
import com.ucsf.service.LoggerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class ResetPasswordController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailService emailService;

	@Value("${spring.mail.from}")
	String fromEmail;

	@Autowired
	private LoggerService loggerService;

	private static Logger log = LoggerFactory.getLogger(ResetPasswordController.class);

	@RequestMapping(value = "/forget-password", method = RequestMethod.POST)
	public ResponseEntity<ResetPasswordResponse> forgetPassword(@RequestParam String email) {
		loggerService.printLogs(log, "forgetPassword", "Sending forget password email");

		User user = null;
		if (email != null && !email.equals("")) {
			user = userRepository.findByEmail(email);
			if (user == null) {
				return ResponseEntity.ok(new ResetPasswordResponse(false, "There is no user with this email."));
			}
		}
		try {
			emailService.sendResetPasswordEmail(fromEmail, user.getEmail(), "Reset your UCSF account password",
					user.getUsername() + " " + user.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(new ResetPasswordResponse(true, "A reset password email has been sent."));
	}

	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public ResponseEntity<VerifyPasswordRequest> verifyPassword(@RequestParam String password) {

		if (password != null && !password.equals("")) {
			User user = userRepository.findByEmail(password);
			if (user == null) {
				return ResponseEntity.ok(new VerifyPasswordRequest());
			}
		}
		return ResponseEntity.ok(new VerifyPasswordRequest());
	}

}
