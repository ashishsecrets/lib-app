package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.controller.ScreeningAnswerController;
import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.payload.request.ScreeningAnswerRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.ScreeningQuestionResponse;
import com.ucsf.payload.response.StudyInfoData;
import com.ucsf.repository.UserScreeningStatusRepository;
import com.ucsf.service.AnswerSaveService;
import com.ucsf.service.LoggerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StudyAnswerImpl implements AnswerSaveService {


	@Autowired
	private LoggerService loggerService;

	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;


	@Autowired
	StudyInfoCheck screeningTest;

	private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

	@Override
	public ResponseEntity saveAnswer(ScreeningAnswerRequest answerRequest) {

		User user = null;

		//Enabling logging;

		loggerService.printLogs(log, "saveScreeningAnswers", "Saving screening Answers");

		//Calling studyAbstract's contructor to pass answerRequest to it.
		StudyAbstractCall studyAbstractCall = new StudyAbstractCall(answerRequest);

		Boolean isSuccess = false;

		ScreeningQuestionResponse response = new ScreeningQuestionResponse();

		JSONObject responseJson = new JSONObject();

		int indexValue;

		int questionDirection = studyAbstractCall.getQuestionDirection();

		//Getting user Details from Auth Token;

		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(studyAbstractCall.user != null){
			user = studyAbstractCall.user;
		}
		else {
			loggerService.printLogs(log, "saveScreeningAnswers", "Invalid JWT signature.");
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			ResponseEntity x = new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}

		UserScreeningStatus userScreeningStatus = studyAbstractCall.getUserScreeningStatus();

		//Updating screeningStatus to In Progress and setting index so next question is displayed.
		studyAbstractCall.updateUserScreeningStatus(UserScreeningStatus.UserScreenStatus.INPROGRESS, userScreeningStatus.getIndexValue() + questionDirection);

		Optional<ScreeningAnswers> lastSavedAnswer = studyAbstractCall.getLastSavedAnswer();
		
		studyAbstractCall.catchQuestionAnswerError();
		
		// You will get previous question or answer if you do index - questionDirection and next by index + questionDirection
		// It does not matter whether you are going forward or backward questionDirection takes care of that.

		indexValue = studyAbstractCall.getIndexValue();

		// Therefore, here we create two new ints for going next or previous question/answer --
		int previous = indexValue - questionDirection;
		int next = indexValue + questionDirection;
		int current = indexValue;


		ScreeningQuestions questionToDisplayToUser = studyAbstractCall.getQuestionToDisplayToUser(current);
		ScreeningAnswers answerToDisplayToUser = studyAbstractCall.getAnswerToDisplayToUser(current);


		// Below section helps put the response used to display question/answer & choices to user.

		try {
			Boolean isLastQuestion = studyAbstractCall.getIsLastQuestionBool();

			response = studyAbstractCall.displayQuesNAns();

			try {
				if (lastSavedAnswer != null) {
					StudyInfoData screenTestData = screeningTest.screenTest(lastSavedAnswer.get(), questionDirection);
					if(screenTestData == null){

						studyAbstractCall.setQuestionToDisplayToUser(current);

						response = studyAbstractCall.displayQuesNAns();

					}
					if (screenTestData != null) {
						if (screenTestData.isFinished) {

							response = studyAbstractCall.displayNullQuesNAns();

							response.setIsLastQuestion(screeningTest.screenTest(lastSavedAnswer.get(), questionDirection).isFinished);
							userScreeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.UNDER_REVIEW);


						} else if(!screenTestData.isFinished)  {

							if (!studyAbstractCall.findAnswerByIndex(3).getAnswerDescription().equals("Primary care doctor")) {


								//indexValue = userScreeningStatusRepository.findByUserId(user.getId()).getIndexValue() + questionDirection;

									studyAbstractCall.setQuestionToDisplayToUser(next);

									studyAbstractCall.displayQuesNAns();

									userScreeningStatus.setIndexValue(next);
									userScreeningStatusRepository.save(userScreeningStatus);
							}
						}
					}
				}
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		responseJson.put("data", response);
		userScreeningStatus.setIndexValue(indexValue);
		userScreeningStatusRepository.save(userScreeningStatus);

		return new ResponseEntity(responseJson.toMap(), HttpStatus.ACCEPTED);
	}
}