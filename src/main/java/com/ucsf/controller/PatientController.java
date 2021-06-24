package com.ucsf.controller;

import java.util.ArrayList;
import java.util.List;

import com.ucsf.model.UserTasks;
import com.ucsf.payload.response.*;
import com.ucsf.repository.SurveyRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.TaskService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.UserScreeningStatus;
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

	@Autowired
	UserRepository userRepository;

	@Autowired
	TaskService taskService;

	private static final Logger log = LoggerFactory.getLogger(PatientController.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	@ApiOperation(value = "Get all patients", notes = "Get all patients", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all patients", response = User.class) })
	@RequestMapping(value = "/getAllPatients/{studyId}", method = RequestMethod.GET)
	public ResponseEntity<?> getPatients(@PathVariable Long studyId) {
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
			patients = userService.getPatients(studyId);
			loggerService.printLogs(log, "getPatients", "Fetched patients successfully!");
			response.put("data", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			response.put("error", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	@ApiOperation(value = "Get approved patients", notes = "Get approved patients", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of approved patients", response = User.class) })
	@RequestMapping(value = "/getApprovedPatients/{studyId}", method = RequestMethod.GET)
	public ResponseEntity<?> getApprovedPatients(@PathVariable Long studyId) {
		List<PatientResponse> patients = new ArrayList<PatientResponse>();
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
			patients = userService.getApprovedPatients(studyId);
			loggerService.printLogs(log, "getPatients", "Fetched approved patients successfully!");
			response.put("data", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			response.put("error", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	@ApiOperation(value = "Get disapproved patients", notes = "Get disapproved patients", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of disapproved patients", response = User.class) })
	@RequestMapping(value = "/getDisapprovedPatients/{studyId}", method = RequestMethod.GET)
	public ResponseEntity<?> getDisapprovedPatients(@PathVariable Long studyId) {
		List<PatientResponse> patients = new ArrayList<PatientResponse>();
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
			patients = userService.getDisapprovedPatients(studyId);
			loggerService.printLogs(log, "getDisapprovedPatients", "Fetched disapproved patients successfully!");
			response.put("data", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			response.put("error", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	@ApiOperation(value = "Get disqualified patients", notes = "Get disqualified patients", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of disqualified patients", response = User.class) })
	@RequestMapping(value = "/getDisqualifiedPatients/{studyId}", method = RequestMethod.GET)
	public ResponseEntity<?> getDisqualifiedPatients(@PathVariable Long studyId) {
		List<PatientResponse> patients = new ArrayList<PatientResponse>();
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
			patients = userService.getDisqualifiedPatients(studyId);
			loggerService.printLogs(log, "getDisapprovedPatients", "Fetched disapproved patients successfully!");
			response.put("data", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			response.put("error", patients);
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	@ApiOperation(value = "Get complete list of tasks for study", notes = "Get complete list of tasks by user for study", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all tasks by user including missing", response = CompleteTasksListResponse.class) })
	@RequestMapping(value = "/complete-task-list/{studyId}/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> getTasksForStudyComplete(@PathVariable Long studyId, @PathVariable Long userId) {

		CompleteTasksListResponse response = new CompleteTasksListResponse();

		JSONObject responseJson = new JSONObject();

		User user = null;

		Boolean isSuccess = false;

		try {
			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (userDetail != null && userDetail.getUsername() != null) {
				user = userRepository.findById(userId).get();
				isSuccess = true;

			} else {
				loggerService.printLogs(log, "notificationsService", "Invalid User");
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
						Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		if(!isSuccess){
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
		try {

			List<UserTasks> tasks = taskService.getTaskList(user);

			taskService.updateSurveyStatuses(user);

			if(taskService.getTaskList(user) != null && !taskService.getTaskList(user).isEmpty()){
				response.setTasks(taskService.getAlteredTaskList(tasks));
				response.setOverdueTasks(taskService.getOverDueTaskList(taskService.getAlteredTaskList(tasks)));
				response.setCurrentTasks(taskService.getCurrentTaskList(taskService.getAlteredTaskList(tasks)));
				response.setUpcomingTasks(taskService.getUpcomingTaskList(taskService.getAlteredTaskList(tasks)));
				response.setCompletedProgress(taskService.getTotalProgress(user));
				response.setMissingProgress(taskService.getMissingProgress(user));
				response.setUpcomingProgress(taskService.getUpcomingProgress(user));
				responseJson.put("data", response);
				return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
			}
			else{
				responseJson.put("error", new ErrorResponse(ErrorCodes.NO_STUDY_FOUND.code(), Constants.NO_STUDY_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.NO_CONTENT);
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	@ApiOperation(value = "Get list of tasks/surveys for study", notes = "Get list of tasks/surveys by user for study", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all tasks by user", response = TasksListResponse.class) })
	@RequestMapping(value = "/task-list/{studyId}/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> getTasksForStudy(@PathVariable Long studyId, @PathVariable Long userId) {

		TasksListResponse response = new TasksListResponse();

		JSONObject responseJson = new JSONObject();

		User user = null;

		Boolean isSuccess = false;

		try {
			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (userDetail != null && userDetail.getUsername() != null) {
				user = userRepository.findById(userId).get();
				isSuccess = true;

			} else {
				loggerService.printLogs(log, "notificationsService", "Invalid User");
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
						Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		if(!isSuccess){
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
		try {

			List<UserTasks> tasks = taskService.getTaskList(user);

			taskService.updateSurveyStatuses(user);

			if(taskService.getTaskList(user) != null && !taskService.getTaskList(user).isEmpty()){
				response.setList(taskService.getAlteredTaskList(tasks));
				response.setTotalProgress(taskService.getTotalProgress(user));
				responseJson.put("data", response);
				return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
			}
			else{
				responseJson.put("error", new ErrorResponse(ErrorCodes.NO_STUDY_FOUND.code(), Constants.NO_STUDY_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.NO_CONTENT);
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	@ApiOperation(value = "Get list of tasks/surveys for study", notes = "Get list of tasks/surveys by user for study", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all tasks by user", response = OverDuePatientTasksListResponse.class) })
	@RequestMapping(value = "/overdue-task-list/{studyId}", method = RequestMethod.GET)
	public ResponseEntity<?> getOverDueTasksForStudyList(@PathVariable Long studyId) {

		JSONObject responseJson = new JSONObject();

		User user = null;

		Boolean isSuccess = false;

		try {
			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (userDetail != null && userDetail.getUsername() != null) {
				String email = userDetail.getUsername();
				user = userRepository.findByEmail(email);
				isSuccess = true;

			} else {
				loggerService.printLogs(log, "notificationsService", "Invalid User");
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
						Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		if(!isSuccess){
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
		try {


			taskService.updateSurveyStatuses(user);

			if(taskService.getAlteredTaskListStudy() != null && !taskService.getAlteredTaskListStudy().isEmpty()){
				List<OverDuePatientTasksListResponse> response = taskService.getAlteredTaskListStudy();
				responseJson.put("data", response);
				return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
			}
			else{
				responseJson.put("error", new ErrorResponse(ErrorCodes.NO_STUDY_FOUND.code(), Constants.NO_STUDY_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.NO_CONTENT);
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
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
