package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.payload.request.ForgetPasswordRequest;
import com.ucsf.payload.request.ResetPasswordRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
import com.ucsf.service.LoggerService;

import com.ucsf.service.PasswordResetLinkService;
import com.ucsf.service.ResetPassword;
import com.ucsf.service.VerificationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
@Api(tags = "Reset-Password Controller")
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
	@ApiOperation(value = "Forgot password email", notes = "Send forgot password email", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Email sent successfully", response = SuccessResponse.class) })
	@RequestMapping(value = "/forget-password", method = RequestMethod.POST)
	public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordRequest request) {
		loggerService.printLogs(log, "forgetPassword", "Sending forget password email");
		JSONObject responseJson = new JSONObject();
		User user = null;
		if (request.getEmail() != null && !request.getEmail().equals("")) {
			user = userRepository.findByEmail(request.getEmail());
			if (user == null) {
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
						Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
			}
		}
		try {
			emailService.sendResetPasswordEmail(fromEmail, user.getEmail(), "Reset your Skin Tracker account password",
					user.getFirstName() + " " + user.getLastName(), passResetLinkService.createPasswordResetLink(user), "classpath:template/passwordResetEmail.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseJson.put("data", new SuccessResponse(true, "Email sent"));
		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Reset password", notes = "Reset password", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Password reset successfully", response = SuccessResponse.class) })
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
	
}
