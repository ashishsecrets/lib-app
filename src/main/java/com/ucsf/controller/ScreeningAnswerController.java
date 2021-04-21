package com.ucsf.controller;

import java.util.List;
import java.util.Optional;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.ucsf.model.ScreeningAnsChoice;
import com.ucsf.payload.response.ScreeningQuestionResponse;
import com.ucsf.repository.*;
import org.json.JSONException;
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
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.service.LoggerService;

import javax.transaction.Transactional;

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

	@Autowired
	ChoiceRepository choiceRepository;

	private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<?> saveScreeningAnswers(@RequestBody ScreeningAnswerRequest answerRequest) throws Exception {
		loggerService.printLogs(log, "saveScreeningAnswers", "Saving screening Answers");
		User user = null;
		JSONObject responseJson = new JSONObject();
		Boolean isNewStatus = false;
		Boolean isSuccess = false;
		int quesIncrement = 1;
		if(!answerRequest.getForward()){
			quesIncrement = -1;
		}
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			String email = userDetail.getUsername();
			user = userRepository.findByEmail(email);
		} else {
			loggerService.printLogs(log, "saveScreeningAnswers", "Invalid JWT signature.");
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}

		UserScreeningStatus userScreeningStatus = userScreeningStatusRepository.findByUserId(user.getId());


		if (userScreeningStatus != null) {
			userScreeningStatus.setStudyId(answerRequest.getStudyId());
			userScreeningStatus.setUserScreeningStatus(UserScreenStatus.INPROGRESS);
			userScreeningStatus.setIndexValue(userScreeningStatus.getIndexValue()+quesIncrement);
			userScreeningStatusRepository.save(userScreeningStatus);
		} else {
			userScreeningStatus = new UserScreeningStatus();
			userScreeningStatus.setStudyId(answerRequest.getStudyId());
			userScreeningStatus.setUserScreeningStatus(UserScreenStatus.INPROGRESS);
			userScreeningStatus.setUserId(user.getId());
			userScreeningStatus.setIndexValue(1);
			userScreeningStatusRepository.save(userScreeningStatus);
			isNewStatus = true;
			loggerService.printLogs(log, "saveScreeningAnswers", "UserScreen Status updated");
		}

		ScreeningQuestions sc = screeningQuestionRepository.findByStudyIdAndIndexValue(userScreeningStatusRepository.findByUserId(user.getId()).getStudyId(), userScreeningStatusRepository.findByUserId(user.getId()).getIndexValue());

		Optional<ScreeningQuestions> sq = Optional.ofNullable(screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue()));
		if (sq.isPresent()) {
			if (userScreeningStatus.getIndexValue() != sq.get().getId()) {
				responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_INDEXVALUE.code(),
						Constants.INVALID_INDEXVALUE.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
			}
		} else {
			responseJson.put("error",
					new ErrorResponse(ErrorCodes.QUESTION_NOT_FOUND.code(), Constants.QUESTION_NOT_FOUND.errordesc()));
			String string = "";
			if(isSuccess){
				string = "Screening answer saved successfully!";
			}
			responseJson.put("next question", new SuccessResponse(isSuccess, "Last Question Index Reached !" + " " + string ));
			if(userScreeningStatus.getIndexValue() > 0){
				userScreeningStatus.setUserScreeningStatus(UserScreenStatus.COMPLETED);
			}
			else if (userScreeningStatus.getIndexValue() <= 0){
				responseJson.put("next question", new SuccessResponse(isSuccess, "Cannot go further back! Answer first question" + " " + string ));
				userScreeningStatus.setIndexValue(-1);
				userScreeningStatusRepository.save(userScreeningStatus);
			}
			userScreeningStatus.setIndexValue(userScreeningStatus.getIndexValue()-quesIncrement);
			userScreeningStatusRepository.save(userScreeningStatus);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}

		if(!answerRequest.getAnswer().isEmpty()){
			Optional<ScreeningAnswers> screenAnswerOp = Optional.ofNullable(screeningAnswerRepository.findByQuestionId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue()).getId() - 1)));
			ScreeningAnswers screenAnswer;
			if(screenAnswerOp.isPresent()){
				screenAnswer = 	screeningAnswerRepository.findById(screenAnswerOp.get().getId()).get();
			}
			else {
				screenAnswer = new ScreeningAnswers();
			}
			screenAnswer.setAnswerDescription(answerRequest.getAnswerDescription());
			screenAnswer.setAnswerChoice(answerRequest.getAnswer());
			screenAnswer.setQuestionId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue()).getId()-1));
			screenAnswer.setStudyId(answerRequest.getStudyId());
			screenAnswer.setIndexValue(userScreeningStatus.getIndexValue()-1);
			screeningAnswerRepository.save(screenAnswer);
			isSuccess = true;
			responseJson.put("data", new SuccessResponse(isSuccess, "Screening answer saved successfully!")); }


		try {
			List<ScreeningAnsChoice> choices = choiceRepository.findByQuestionId(sc.getId());
			ScreeningQuestionResponse response = new ScreeningQuestionResponse();
			response.setScreeningQuestions(sc);
			response.setChoices(choices);
			responseJson.put("next question", response);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}


		if(isNewStatus){
			responseJson.put("data", new SuccessResponse(true, "Please answer the first question."));
		}

		return new ResponseEntity(responseJson.toMap(), HttpStatus.ACCEPTED);
	}
}
