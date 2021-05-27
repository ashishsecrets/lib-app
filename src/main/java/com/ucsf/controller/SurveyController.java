package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.UserTasks;
import com.ucsf.payload.request.SurveyAnswerRequest;
import com.ucsf.payload.response.*;
import com.ucsf.service.AnswerSaveService;
import com.ucsf.service.TaskService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ucsf.model.UcsfSurvey;
import com.ucsf.payload.request.SurveyRequest;
import com.ucsf.repository.ChoiceRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.SurveyRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.LoggerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/survey")
@Api(tags = "Survey Controller")
public class SurveyController {

	@Autowired
	ScreeningQuestionRepository screeningQuestionRepository;

	@Autowired
	ChoiceRepository choiceRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SurveyRepository surveyRepository;
	
	@Autowired
	private LoggerService loggerService;

	@Autowired
	TaskService taskService;

	private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Save survey", notes = "Save survey", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Survey saved successfully", response = UcsfSurvey.class) })
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody SurveyRequest surveyRequest)
			throws Exception {
		
		/*
		 * User user = null; UserDetails userDetail = (UserDetails)
		 * SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		 */
		JSONObject responseJson = new JSONObject();

		/*
		 * if (userDetail != null && userDetail.getUsername() != null) { String userName
		 * = userDetail.getUsername(); user = userRepository.findByEmail(userName); }
		 * else { loggerService.printLogs(log, "createAuthenticationToken",
		 * "Invalid JWT signature."); responseJson.put("error", new
		 * ApiError(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
		 * Constants.INVALID_AUTHORIZATION_HEADER.errordesc())); return new
		 * ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED); }
		 */
		
		UcsfSurvey survey = new UcsfSurvey();
		survey.setDescription(surveyRequest.getDescription());
		survey.setEnabled(surveyRequest.getEnabled());
		survey.setStudyId(surveyRequest.getStudyId());
		survey.setTitle(surveyRequest.getTitle());
		//Save Survey Rating
		surveyRepository.save(survey);
		responseJson.put("data", new SuccessResponse(true, "Survey saved successfully!"));
		return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
	}

	@Autowired
	AnswerSaveService answerSaveService;

	@Transactional
	@ApiOperation(value = "Save answer", notes = "Save survey answer", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Answer saved successfully", response = SurveyQuestionResponse.class) })
	@RequestMapping(value = "/answer-save", method = RequestMethod.POST)
	public ResponseEntity<?> saveSurveyAnswers(@RequestBody SurveyAnswerRequest answerRequest) throws Exception {
		return answerSaveService.saveSurveyAnswer(answerRequest);

	}

	@ApiOperation(value = "Get list of tasks/surveys", notes = "Get list of tasks/surveys by user", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all tasks by user", response = TasksListResponse.class) })
	@RequestMapping(value = "/task-list", method = RequestMethod.GET)
	public ResponseEntity<?> getTasks() {

		TasksListResponse response = new TasksListResponse();

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

			List<UserTasks> tasks = taskService.getTaskList(user);

			taskService.updateSurveyStatuses(user);

			if(taskService.getTaskList(user) != null && !taskService.getTaskList(user).isEmpty()){
				response.setList(taskService.getAlteredTaskList(tasks));
				response.setTotalProgress(taskService.getTotalProgress(taskService.getAlteredTaskList(tasks)));
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
}
