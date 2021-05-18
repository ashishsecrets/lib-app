package com.ucsf.controller;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.ucsf.payload.response.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.UcsfStudy;
import com.ucsf.payload.request.StudyRequest;
import com.ucsf.payload.request.StudyReviewRequest;
import com.ucsf.service.LoggerService;
import com.ucsf.service.StudyService;
import com.ucsf.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin
@RequestMapping("/api/study")
@Api(tags = "Study Controller")
public class StudyController {

	@Autowired
	private LoggerService loggerService;

	@Autowired
	UserService userService;

	@Autowired
	StudyService studyService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	private static Logger log = LoggerFactory.getLogger(StudyController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Save study", notes = "Save study", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Study saved successfully", response = UcsfStudy.class) })
	@PreAuthorize("hasRole('PATIENT')")

	@RequestMapping(value = "/saveStudy", method = RequestMethod.POST)
	public ResponseEntity<?> saveStudy(@RequestBody StudyRequest studyRequest) throws Exception {
		User user = null;
		JSONObject responseJson = new JSONObject();

		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			String email = userDetail.getUsername();
			user = userService.findByEmail(email);
			loggerService.printLogs(log, "saveStudy",
					"Saving UCSF Study with user " + user.getEmail() + "studyRequest " + studyRequest.toString());
		} else {
			loggerService.printLogs(log, "saveScreeningAnswers", "Invalid JWT signature.");
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		try {
			studyService.save(studyRequest);
			loggerService.printLogs(log, "saveStudy", "Study " + studyRequest.getTitle() + "saved Successfully!");
			responseJson.put("data", new SuccessResponse(true, "Study Saved Successfully"));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "saveStudy", "Error while saving " + studyRequest.getTitle());
			responseJson.put("data", new SuccessResponse(true, "Error While saving Study"));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Get all studies", notes = "Get all studies", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all studies", response = StudyResponse.class) })
	@PreAuthorize("hasRole('PATIENT')")
	@RequestMapping(value = "/fetchStudies", method = RequestMethod.GET)
	public ResponseEntity<?> fetchAllStudies() throws Exception {
		loggerService.printLogs(log, "saveStudy", "Fetch UCSF Studies");
		User user = null;
		JSONObject responseJson = new JSONObject();

		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			String email = userDetail.getUsername();
			user = userService.findByEmail(email);
		} else {
			loggerService.printLogs(log, "fetchAllStudies", "Invalid JWT signature.");
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		List<StudyResponse> listStudyResponse = new ArrayList<StudyResponse>();
		List<StudyFetchResponse> listResponse = new ArrayList<>();
		try {
			listStudyResponse = studyService.getStudies(user.getId());
			loggerService.printLogs(log, "fetchAllStudies", "Studies fetched successfully for user " + user.getEmail());
			for(int i = 0; i < listStudyResponse.size(); i++){
				listResponse.add(new StudyFetchResponse());
				listResponse.get(i).setStudy(listStudyResponse.get(i));
				listResponse.get(i).setUserImageCount(studyService.getImageCount(listStudyResponse.get(i).getId(), user.getId()));
			}
			responseJson.put("data", listResponse);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		}
		 catch (Exception e) {
			loggerService.printErrorLogs(log, "fetchAllStudies",
					"Failed Studies fetch request for user " + user.getEmail());
			for(int i = 0; i < listStudyResponse.size(); i++){
				listResponse.add(new StudyFetchResponse());
				listResponse.get(i).setStudy(listStudyResponse.get(i));
				listResponse.get(i).setUserImageCount(studyService.getImageCount(listStudyResponse.get(i).getId(), user.getId()));
			}
			responseJson.put("data", listResponse);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Update Study Status", notes = "Approve study", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Study Status Updated successfully", response = SuccessResponse.class) })
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/updateStudyStatus/{userId}", method = RequestMethod.POST)
	public ResponseEntity<?> updateStudyStatus(@PathVariable Long userId, @RequestParam String status)
			throws Exception {
		User user = null;
		JSONObject responseJson = new JSONObject();

		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			if (user != null) {
				loggerService.printLogs(log, "updateStudyStatus", "Update Study Status of userId " + userId);
			}
		} else {
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		try {
			studyService.updateStudyStatus(userId, status);
			loggerService.printLogs(log, "updateStudyStatus", "Study " + status + "  for user with id " + userId);
			responseJson.put("data", new SuccessResponse(true, "Study " + status));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			loggerService.printErrorLogs(log, "updateStudyStatus",
					"Error while updating Study " + status + "  for user with id " + userId);
			responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
					Constants.USER_NOT_FOUND.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		}
	}

	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Review Patient's Study", notes = "Review Patient's Study", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Study reviewed successfully", response = StudyReviewResponse.class) })
	@RequestMapping(value = "/reviewStudy", method = RequestMethod.POST)
	public ResponseEntity<?> reviewStudy(@RequestBody StudyReviewRequest reviewStudy) throws Exception {

		JSONObject responseJson = new JSONObject();
		StudyReviewResponse response = null;
		try {
			response = studyService.reviewStudy(reviewStudy);
			loggerService.printLogs(log, "reviewStudy",
					"Study " + reviewStudy.getStudyId() + "  for user with id " + reviewStudy.getUserId());
			responseJson.put("data", response);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "reviewStudy", "Error while reviewing Study " + reviewStudy.getStudyId()
					+ "  for user with id " + reviewStudy.getUserId());
			responseJson.put("data", response);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		}
	}

	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/*
	 * @ApiOperation(value = "Get Approved Patients", notes =
	 * "Get Approved Patients", code = 200, httpMethod = "GET", produces =
	 * "application/json")
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(code = 200, message = "Approved Patients fetched successfully",
	 * response = User.class) })
	 */	@RequestMapping(value = "/approvedPatients", method = RequestMethod.GET)
	public ResponseEntity<?> fetchApprovedPatients() throws Exception {

		JSONObject responseJson = new JSONObject();
		StudyApprovedPatientsResponse response = new StudyApprovedPatientsResponse();
		User user = null;
		List<User> approvedPatients = new ArrayList<User>();
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			if (user != null) {
				loggerService.printLogs(log, "fetchApprovedPatients", "Approved patients fetched for Physicain  " + user.getEmail());
			}
		} else {
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		try {
			approvedPatients = studyService.getApprovedPatients();
			response.setList(approvedPatients);
			loggerService.printLogs(log, "fetchApprovedPatients", "Approved patients fetched Successfully for Physicain "+user.getEmail());
			responseJson.put("data", response);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "fetchApprovedPatients", "Error while fetching approved patients fetched  for Physicain "+user.getEmail());
			responseJson.put("data", response);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		}
	}

	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/*
	 * @ApiOperation(value = "Get Approved Patients", notes =
	 * "Get Approved Patients", code = 200, httpMethod = "GET", produces =
	 * "application/json")
	 *
	 * @ApiResponses(value = {
	 *
	 * @ApiResponse(code = 200, message = "Approved Patients fetched successfully",
	 * response = User.class) })
	 */	@RequestMapping(value = "/disapprovedPatients", method = RequestMethod.GET)
	public ResponseEntity<?> fetchDisapprovedPatients() throws Exception {

		JSONObject responseJson = new JSONObject();
		StudyApprovedPatientsResponse response = new StudyApprovedPatientsResponse();
		User user = null;
		List<User> disapprovedPatients = new ArrayList<User>();
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			if (user != null) {
				loggerService.printLogs(log, "fetchDisapprovedPatients", "Disapproved patients fetched for Physician  " + user.getEmail());
			}
		} else {
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		try {
			disapprovedPatients = studyService.getDisapprovedPatients();
			response.setList(disapprovedPatients);
			loggerService.printLogs(log, "fetchApprovedPatients", "Disapproved patients fetched Successfully for Physicain "+user.getEmail());
			responseJson.put("data", response);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "fetchApprovedPatients", "Error while fetching disapproved patients fetched  for Physicain "+user.getEmail());
			responseJson.put("data", response);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
		}
	}

}
