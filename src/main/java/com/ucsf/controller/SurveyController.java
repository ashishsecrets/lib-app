package com.ucsf.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ucsf.model.UcsfSurvey;
import com.ucsf.payload.request.SurveyRequest;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.ChoiceRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.SurveyRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.LoggerService;

@RestController
@CrossOrigin
@RequestMapping("/api/survey")
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

	private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
}