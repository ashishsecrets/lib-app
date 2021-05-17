package com.ucsf.controller;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.StudyStatusResponse;
import com.ucsf.service.LoggerService;
import com.ucsf.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin
@RequestMapping("/api/patient")
@Api(tags = "Patient Controller")
public class PatientController {

	@Autowired
	UserService userService;

	@Autowired
	private LoggerService loggerService;

	private static final Logger log = LoggerFactory.getLogger(PatientController.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "Get all patients", notes = "Get all patients", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all patients", response = User.class) })
	@RequestMapping(value = "/getAllPatients", method = RequestMethod.GET)
	public ResponseEntity<?> getPatients() {
		List<User> patients = new ArrayList<User>();
		JSONObject response = new JSONObject();
		User user = null;
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			loggerService.printLogs(log, "getAllPatients", "Saving user consent for user " + user.getId());
		} else {
			loggerService.printLogs(log, "getAllPatients", "Invalid JWT signature.");
			response.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(response.toMap(), HttpStatus.UNAUTHORIZED);
		}
		try {
			patients = userService.getPatients();
			loggerService.printLogs(log, "getPatients", "Fetched patients successfully!");
			response.put("data", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			response.put("error", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "Get approved patients", notes = "Get approved patients", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of approved patients", response = User.class) })
	@RequestMapping(value = "/getApprovedPatients", method = RequestMethod.GET)
	public ResponseEntity<?> getApprovedPatients() {
		List<User> patients = new ArrayList<User>();
		JSONObject response = new JSONObject();
		User user = null;
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			loggerService.printLogs(log, "getAllPatients", "Saving user consent for user " + user.getId());
		} else {
			loggerService.printLogs(log, "getAllPatients", "Invalid JWT signature.");
			response.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(response.toMap(), HttpStatus.UNAUTHORIZED);
		}
		try {
			patients = userService.getApprovedPatients();
			loggerService.printLogs(log, "getPatients", "Fetched approved patients successfully!");
			response.put("data", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			response.put("error", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "Get disapproved patients", notes = "Get disapproved patients", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of disapproved patients", response = User.class) })
	@RequestMapping(value = "/getDisapprovedPatients", method = RequestMethod.GET)
	public ResponseEntity<?> getDisapprovedPatients() {
		List<User> patients = new ArrayList<User>();
		JSONObject response = new JSONObject();
		User user = null;
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			loggerService.printLogs(log, "getDisapprovedPatients", "getting list of disapproved patients");
		} else {
			loggerService.printLogs(log, "getDisapprovedPatients", "Invalid JWT signature.");
			response.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(response.toMap(), HttpStatus.UNAUTHORIZED);
		}
		try {
			patients = userService.getDisapprovedPatients();
			loggerService.printLogs(log, "getDisapprovedPatients", "Fetched disapproved patients successfully!");
			response.put("data", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			response.put("error", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('ROLE_PATIENT')")
	@ApiOperation(value = "Check patient approval", notes = "Check Patient Approved or not", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Fetched Approved Patient", response = StudyStatusResponse.class) })
	@RequestMapping(value = "/getPatientStudyStatus", method = RequestMethod.GET)
	public ResponseEntity<?> checkPatientApproval() {
		JSONObject response = new JSONObject();
		User user = null;
        UserScreeningStatus studyStatus = null;
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			loggerService.printLogs(log, "checkPatientApproval", "Checking if user is approved or not for study " + user.getEmail());
		} else {
			loggerService.printLogs(log, "getAllPatients", "Invalid JWT signature.");
			response.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(response.toMap(), HttpStatus.UNAUTHORIZED);
		}
		try {
			studyStatus = userService.getUserStatus(user.getId());
			if(studyStatus != null) {
				response.put("data", new StudyStatusResponse(true,studyStatus.getUserScreeningStatus()));
				return new ResponseEntity(response.toMap(), HttpStatus.OK);
			}
			else {
				response.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(response.toMap(), HttpStatus.OK);
			}
		} catch (Exception e) {
			response.put("data", new ErrorResponse(403,e.getMessage()));
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
}
