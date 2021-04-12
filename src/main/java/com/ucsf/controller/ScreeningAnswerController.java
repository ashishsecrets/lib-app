package com.ucsf.controller;

import java.util.Optional;

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

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.model.UserScreeningStatus.UserScreenStatus;
import com.ucsf.payload.request.ScreeningAnswerRequest;
import com.ucsf.payload.response.ApiError;
import com.ucsf.payload.response.ScreeningAnswerResponse;
import com.ucsf.repository.ScreeningAnswerRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.UserScreeningStatusRepository;
import com.ucsf.service.LoggerService;

@RestController
@CrossOrigin
@RequestMapping("/api/answers")
public class ScreeningAnswerController {

	@Autowired
	ScreeningAnswerRepository screeningAnswerRepository;

	@Autowired
	ScreeningQuestionRepository screeningQuestionRepository;

	@Autowired
	private LoggerService loggerService;

	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;
	
	@Autowired
	UserRepository userRepository;

	private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<?> saveScreeningAnswers(@RequestBody ScreeningAnswerRequest answerRequest) throws Exception {
		loggerService.printLogs(log, "saveScreeningAnswers", "Saving screening Answers");
		User user = null;
		JSONObject responseJson = new JSONObject();

		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			String email = userDetail.getUsername();
			user = userRepository.findByEmail(email);
		} else {
			loggerService.printLogs(log, "saveScreeningAnswers", "Invalid JWT signature.");
			responseJson.put("error", new ApiError(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}

		Optional<ScreeningQuestions> sq = screeningQuestionRepository.findById(answerRequest.getQuestionId());
		if (sq.isPresent()) {
			if (answerRequest.getIndexValue() != sq.get().getId()) {
				responseJson.put("error",
						new ApiError(ErrorCodes.INVALID_INDEXVALUE.code(), Constants.INVALID_INDEXVALUE.errordesc()));
				return new ResponseEntity(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
		} else {
			responseJson.put("error",
					new ApiError(ErrorCodes.QUESTION_NOT_FOUND.code(), Constants.QUESTION_NOT_FOUND.errordesc()));
			return new ResponseEntity(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}

		ScreeningAnswers screenAnswer = new ScreeningAnswers();
		screenAnswer.setAnswerDescription(answerRequest.getAnswerDescription());
		screenAnswer.setAnswerChoice(answerRequest.getAnswer());
		screenAnswer.setQuestionId(answerRequest.getQuestionId());
		screenAnswer.setStudyId(answerRequest.getStudyId());
		screenAnswer.setIndexValue(answerRequest.getIndexValue());
		screeningAnswerRepository.save(screenAnswer);

		UserScreeningStatus userScreeningStatus = new UserScreeningStatus();
		userScreeningStatus.setStudyId(answerRequest.getStudyId());
		userScreeningStatus.setUserScreeningStatus(UserScreenStatus.INPROGRESS);
		userScreeningStatus.setUserId(user.getId());
		userScreeningStatusRepository.save(userScreeningStatus);
		loggerService.printLogs(log, "saveScreeningAnswers", "UserScreen Status updated");

		return ResponseEntity.ok(new ScreeningAnswerResponse(true, "Screening answer saved successfully!"));
	}
}
