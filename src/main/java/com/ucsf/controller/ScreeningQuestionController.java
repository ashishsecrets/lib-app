package com.ucsf.controller;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ucsf.model.ScreeningAnsChoice;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.payload.response.ApiError;
import com.ucsf.payload.response.ScreeningQuestionResponse;
import com.ucsf.repository.ChoiceRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.LoggerService;

@RestController
@CrossOrigin
@RequestMapping("/api/questions")
public class ScreeningQuestionController {

	@Autowired
	ScreeningQuestionRepository screeningQuestionRepository;

	@Autowired
	ChoiceRepository choiceRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private LoggerService loggerService;

	private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/question/{studyId}/{indexValue}", method = RequestMethod.GET)
	public ResponseEntity<?> createAuthenticationToken(@PathVariable Long studyId, @PathVariable int indexValue)
			throws Exception {
		
		User user = null;
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		JSONObject responseJson = new JSONObject();

		if (userDetail != null && userDetail.getUsername() != null) {
			String userName = userDetail.getUsername();
			user = userRepository.findByEmail(userName);
		} else {
			loggerService.printLogs(log, "saveScreeningAnswers", "Invalid JWT signature.");
			responseJson.put("error", new ApiError(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		
		ScreeningQuestions sc = screeningQuestionRepository.findByStudyIdAndIndexValue(studyId, indexValue);
		List<ScreeningAnsChoice> choices = choiceRepository.findByQuestionId(sc.getId());
		ScreeningQuestionResponse response = new ScreeningQuestionResponse();
		response.setScreeningQuestions(sc);
		response.setChoices(choices);
		return new ResponseEntity(response, HttpStatus.OK);
	}
}
