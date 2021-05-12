package com.ucsf.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import com.ucsf.service.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucsf.auth.model.User;
import com.ucsf.auth.model.UserOtp;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.config.JwtConfig;
import com.ucsf.config.JwtTokenUtil;
import com.ucsf.payload.request.VerifyRequest;
import com.ucsf.payload.response.ApiError;
import com.ucsf.payload.response.AuthResponse;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.CustomUserDetailsService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.VerificationService;
import com.ucsf.util.AppUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/otp")
@Api(tags = "Otp Controller")
public class OtpController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	VerificationService verificationService;

	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	EmailService emailService;

	@Value("${spring.mail.from}")
	String fromEmail;

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@Autowired
	private LoggerService loggerService;

	@Autowired
	private OtpService otpService;

	private static Logger log = LoggerFactory.getLogger(OtpController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
	@ApiOperation(value = "Send otp", notes = "Send otp", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Otp Sent successfully", response = User.class) })
	@RequestMapping(value = "/sendOtp", method = RequestMethod.POST)
	public ResponseEntity<?> sendOtp(@RequestParam String type) {
		loggerService.printLogs(log, "sendOtp", " Otp send by Email to user's Email");
		UserDetails userDetails = null;
		JSONObject responseJson = new JSONObject();
		User user = null;
		String message = "";
		String otpCode = "";
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (userDetail != null && userDetail.getUsername() != null) {
			String userName = userDetail.getUsername();
			user = userRepository.findByEmail(userName);
		} else {
			loggerService.printLogs(log, "sendOtp", "Invalid JWT signature.");
			responseJson.put("error", new ApiError(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}

		if (jwtConfig.getTwoFa()) {
			UserOtp otp = otpService.findByUserId(user.getId());
			if (otp == null) {
				otp = new UserOtp();
			}
			if (type != null && !type.equals("") && type.equals("email")) {
				otpCode = AppUtil.generateOtpCode(4);
				LocalDateTime expiryTime = LocalDateTime.now();
				expiryTime = expiryTime.plusMinutes(10);
				// expiryTime = expiryTime.plusHours(24);
				long otpExpiry = Timestamp.valueOf(expiryTime).getTime();
				try {
					emailService.sendOtpEmail(fromEmail, user.getEmail(), "Otp for Skin Tracker verification",
							user.getFirstName() + " " + user.getLastName(), otpCode);
					otp.setOtpCode(otpCode);
					otp.setOtpExpiry(otpExpiry);
					otp.setType(type);
					otp.setUserId(user.getId());
					otpService.saveOtp(otp);
					loggerService.printLogs(log, "sendOtp",
							"Otp sent to registered email: " + user.getEmail() + "At: " + new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendOtp",
							"Otp sent to registered email: " + user.getEmail() + "At: " + new Date());
				}
				message = "Otp sent to registered email: " + user.getEmail();
			} else {
				otp.setOtpExpiry(null);
				otp.setOtpCode(null);
				otp.setType(type);
				otp.setUserId(user.getId());
				otpService.saveOtp(otp);
				try {
					JSONObject jsonObject = null;
					jsonObject = verificationService.sendVerificationCode(user);
					if (jsonObject.get("success").equals(true)) {
						loggerService.printLogs(log, jsonObject.toString(), user.getEmail());
						responseJson.put("data", new SuccessResponse(true, "Code Sent."));
						return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
					} else {
						responseJson.put("error", new ErrorResponse(ErrorCodes.CODE_NOT_SENT.code(),
								Constants.CODE_NOT_SENT.errordesc()));
						return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
					}
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendOtp",
							"Otp sent to registered phone Number: " + user.getPhoneNumber() + "At: " + new Date());
				}
				message = "Otp sent to registered phone number: " + user.getPhoneNumber();
			}
		}

		responseJson.put("data", new SuccessResponse(true, "Otp Sent"));
		return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
	@ApiOperation(value = "Verify otp", notes = "Verify otp", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User verified successfully", response = User.class) })
	@RequestMapping(value = "/verifyOtp", method = RequestMethod.POST)
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyRequest verifyRequest) {

		loggerService.printLogs(log, "verifyOtp", "In verifyy otp request");
		User user = null;
		UserDetails userDetails = null;
		JSONObject jsonObject = null;
		JSONObject responseJson = new JSONObject();
		String token = "";
		String message = "Otp Verified";
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (userDetail != null && userDetail.getUsername() != null) {
			String userName = userDetail.getUsername();
			user = userRepository.findByEmail(userName);
		} else {
			loggerService.printLogs(log, "sendOtp", "Invalid JWT signature.");
			responseJson.put("error", new ApiError(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}

		if (verifyRequest.getCode() != null && verifyRequest.getCode().length() > 0) {
			UserOtp otp = otpService.findByUserId(user.getId());

			// Verify BY SMS AUTHY
			if (otp.getType() != null && !otp.getType().equals("") && otp.getType().equals("sms")) {
				try {
					jsonObject = verificationService.otpCodeVerification(user, verifyRequest.getCode());
					if (jsonObject.get("success").equals(true)) {
						userDetails = userDetailsService.loadUserByEmail(user.getEmail(), true);
						token = jwtTokenUtil.generateToken(userDetails);
						user.setAuthToken(token);
						userRepository.save(user);

					} else {
						responseJson.put("error", new ErrorResponse(ErrorCodes.OTP_NOT_VERIFIED.code(),
								Constants.OTP_NOT_VERIFIED.errordesc()));
						return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// Verify BY EMAIL
				long currentTime = Timestamp.valueOf(LocalDateTime.now()).getTime();
				if (currentTime >= otp.getOtpExpiry()) {
					responseJson.put("error",
							new ErrorResponse(ErrorCodes.OTP_EXPIRED.code(), Constants.OTP_EXPIRED.errordesc()));
					return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
				} else if (!otp.getOtpCode().equals(verifyRequest.getCode())) {
					responseJson.put("error",
							new ErrorResponse(ErrorCodes.OTP_INCORRECT.code(), Constants.OTP_INCORRECT.errordesc()));
					return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
				} else {
					userDetails = userDetailsService.loadUserByEmail(user.getEmail(), true);
					token = jwtTokenUtil.generateToken(userDetails);
					user.setAuthToken(token);
					userRepository.save(user);
				}
			}
		}
		if (verifyRequest.getIsNew()) {
			try {
				emailService.sendResetPasswordEmail(fromEmail, user.getEmail(), "Welcome to Skintracker.",
						user.getFirstName() + " " + user.getLastName(), user.getFirstName(),
						"classpath:template/signUpEmail.html");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		responseJson.put("data", new AuthResponse(userDetails, user, message));
		return new ResponseEntity<>(responseJson.toMap(), HttpStatus.OK);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Resend code", notes = "Resend verification code", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Code sent successfully", response = SuccessResponse.class) })
	@RequestMapping(value = "/resend-code", method = RequestMethod.POST)
	public ResponseEntity<?> resendCode() throws Exception {
		loggerService.printLogs(log, "resendCode", "Resend Password");
		User user = null;
		JSONObject jsonObject = null;
		JSONObject responseJson = new JSONObject();
		String otpCode = "";
		String message = "";
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (userDetail != null && userDetail.getUsername() != null) {
			String userName = userDetail.getUsername();
			user = userRepository.findByEmail(userName);
		} else {
			loggerService.printLogs(log, "sendOtp", "Invalid JWT signature.");
			responseJson.put("error", new ApiError(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		// Check type selected
		UserOtp otp = otpService.findByUserId(user.getId());

		if (otp.getType() != null && !otp.getType().equals("") && otp.getType().equals("sms")) {
			jsonObject = verificationService.sendVerificationCode(user);
			if (jsonObject.get("success").equals(true)) {
				message = "Otp Resent to registered phone: " + user.getPhoneNumber();
				loggerService.printLogs(log, jsonObject.toString(), user.getEmail());
				responseJson.put("data", new SuccessResponse(true, message));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
			} else {
				responseJson.put("error",
						new ErrorResponse(ErrorCodes.CODE_NOT_SENT.code(), Constants.CODE_NOT_SENT.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
			}
		} else {
			otpCode = AppUtil.generateOtpCode(4);
			LocalDateTime expiryTime = LocalDateTime.now();
			expiryTime = expiryTime.plusMinutes(10);
			// expiryTime = expiryTime.plusHours(24);
			long otpExpiry = Timestamp.valueOf(expiryTime).getTime();
			try {
				emailService.sendOtpEmail(fromEmail, user.getEmail(), " Skin tracker Otp for verification",
						user.getFirstName() + " " + user.getLastName(), otpCode);
				otp.setOtpCode(otpCode);
				otp.setOtpExpiry(otpExpiry);
				otp.setUserId(user.getId());
				otpService.saveOtp(otp);
				loggerService.printLogs(log, "sendOtp",
						"Otp Resent to registered email: " + user.getEmail() + "At: " + new Date());
			} catch (Exception e) {
				loggerService.printErrorLogs(log, "sendOtp",
						" Error while resending Otp to registered email: " + user.getEmail() + "At: " + new Date());
			}
			message = "Otp Resent to registered email: " + user.getEmail();
			responseJson.put("data", new SuccessResponse(true, message));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		}
	}
}
