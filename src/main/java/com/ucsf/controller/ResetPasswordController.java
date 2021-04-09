package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.payload.ResetPasswordResponse;
import com.ucsf.payload.request.ResetPasswordRequest;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
import com.ucsf.service.LoggerService;

import com.ucsf.service.PasswordResetLinkService;
import com.ucsf.service.ResetPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/password")
public class ResetPasswordController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	PasswordResetLinkService passResetLinkService;

	@Autowired
	ResetPassword resetPassword;

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
					user.getUsername() + " " + user.getUsername(), passResetLinkService.createPasswordResetLink(user));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(new ResetPasswordResponse(true, "A reset password email has been sent."));
	}

	@RequestMapping(value = "/reset-password", method = RequestMethod.POST)
	public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws Exception {
		loggerService.printLogs(log, "resetPassword", "Reseting Password");
		if (resetPasswordRequest.getPassword() != null && !resetPasswordRequest.getPassword().equals("")) {

			String password = resetPasswordRequest.getPassword();
			String confirmPassword = resetPasswordRequest.getConfirmPassword();
			String link = resetPasswordRequest.getLink();

			if((password != null & !password.equals("")) && (confirmPassword != null & !confirmPassword.equals("")) && (link != null & !link.equals(""))) {

				if (!password.equals(confirmPassword)) {
					return ResponseEntity.ok(new ResetPasswordResponse(false, "Password & confirmed password don't match."));
				}

				if (!resetPassword.resetPass(password, link)) {
					return ResponseEntity.ok(new ResetPasswordResponse(false, "User not found or link expired."));
				}
			}
		}
		return ResponseEntity.ok(new ResetPasswordResponse(true, "Password Reset."));
	}



}
