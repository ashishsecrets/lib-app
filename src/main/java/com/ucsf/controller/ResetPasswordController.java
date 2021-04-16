package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.payload.request.ResetPasswordRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
import com.ucsf.service.LoggerService;

import com.ucsf.service.PasswordResetLinkService;
import com.ucsf.service.ResetPassword;
import com.ucsf.service.VerificationService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
	
	@Autowired
	VerificationService verificationService;

	private static Logger log = LoggerFactory.getLogger(ResetPasswordController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/forget-password", method = RequestMethod.POST)
	public ResponseEntity<?> forgetPassword(@RequestParam String email) {
		loggerService.printLogs(log, "forgetPassword", "Sending forget password email");
		JSONObject responseJson = new JSONObject();
		User user = null;
		if (email != null && !email.equals("")) {
			user = userRepository.findByEmail(email);
			if (user == null) {
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
						Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
			}
		}
		try {
			emailService.sendResetPasswordEmail(fromEmail, user.getEmail(), "Reset your UCSF account password",
					user.getFirstName() + " " + user.getLastName(), passResetLinkService.createPasswordResetLink(user));
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseJson.put("data", new SuccessResponse(true, "Email sent"));
		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/reset-password", method = RequestMethod.POST)
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws Exception {
		loggerService.printLogs(log, "resetPassword", "Reseting Password");
		JSONObject responseJson = new JSONObject();
		if (resetPasswordRequest.getPassword() != null && !resetPasswordRequest.getPassword().equals("")) {

			String password = resetPasswordRequest.getPassword();
			String confirmPassword = resetPasswordRequest.getConfirmPassword();
			String link = resetPasswordRequest.getLink();

			if ((password != null & !password.equals("")) && (confirmPassword != null & !confirmPassword.equals(""))
					&& (link != null & !link.equals(""))) {

				if (!password.equals(confirmPassword)) {
					responseJson.put("error", new ErrorResponse(ErrorCodes.PASSWORD_NOT_MATCHING.code(),
							Constants.PASSWORD_NOT_MATCHING.errordesc()));
					return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
				}

				if (!resetPassword.resetPass(password, link)) {
					responseJson.put("error",
							new ErrorResponse(ErrorCodes.LINK_EXPIRED.code(), Constants.LINK_EXPIRED.errordesc()));
					return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
				}
			}
		}
		responseJson.put("data", new SuccessResponse(true, "Password Reset."));
		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/resend-code", method = RequestMethod.POST)
	public ResponseEntity<?> resendCode(@RequestParam String email) throws Exception {
		loggerService.printLogs(log, "resendCode", "Resend Password");
		JSONObject responseJson = new JSONObject();
		JSONObject jsonObject = null;
		User user = userRepository.findByEmail(email);
		if(user == null) {
			responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
					Constants.USER_NOT_FOUND.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
		jsonObject = verificationService.sendVerificationCode(user);
		if (jsonObject.get("success").equals(true)) {
			responseJson.put("data", new SuccessResponse(true, "Code Sent."));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		}
		else {
			responseJson.put("error", new ErrorResponse(ErrorCodes.CODE_NOT_SENT.code(),
					Constants.CODE_NOT_SENT.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
}
