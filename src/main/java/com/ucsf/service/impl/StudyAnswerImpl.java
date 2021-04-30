package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.controller.ScreeningAnswerController;
import com.ucsf.model.ScreeningAnsChoice;
import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.payload.request.ScreeningAnswerRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.ScreeningQuestionResponse;
import com.ucsf.payload.response.StudyInfoData;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.UserScreeningStatusRepository;
import com.ucsf.service.AnswerSaveService;
import com.ucsf.service.LoggerService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StudyAnswerImpl implements AnswerSaveService {


	@Autowired
	private LoggerService loggerService;

	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;

	@Autowired
	ScreeningQuestionRepository screeningQuestionRepository;

	@Autowired
	StudyInfoCheck screeningTest;

	@Autowired
	UserRepository userRepository;

	@Autowired
	StudyAbstractCall studyAbstractCall;


	private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

	@Override
	public ResponseEntity saveAnswer(ScreeningAnswerRequest answerRequest) {

		User user = null;

		//Enabling logging;

		loggerService.printLogs(log, "saveScreeningAnswers", "Saving screening Answers");

		//Calling studyAbstract's member to pass request
		studyAbstractCall.answerRequest = answerRequest;

		Boolean isSuccess = false;

		ScreeningQuestionResponse response = new ScreeningQuestionResponse();

		JSONObject responseJson = new JSONObject();

		int indexValue;

		int questionDirection = studyAbstractCall.getQuestionDirection();

		//Getting user Details from Auth Token;

		try {
			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (userDetail != null && userDetail.getUsername() != null) {
				String email = userDetail.getUsername();
				user = userRepository.findByEmail(email);
				studyAbstractCall.user = user;
				isSuccess = true;
			} else {
				loggerService.printLogs(log, "saveScreeningAnswers", "Invalid JWT signature.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
						Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
				return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
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
		 studyAbstractCall.userScreeningStatus = userScreeningStatusRepository.findByUserId(user.getId());
	 } catch (NullPointerException e) {
		 e.printStackTrace();
	 }

		try {
		//Updating screeningStatus to In Progress and setting index so next question is displayed.
		studyAbstractCall.updateUserScreeningStatus(UserScreeningStatus.UserScreenStatus.INPROGRESS, studyAbstractCall.userScreeningStatus.getIndexValue() + questionDirection);
	} catch (NullPointerException e) {
		e.printStackTrace();
	}

		//Getting lastSaved Answer below ;;


		Optional<ScreeningAnswers> lastSavedAnswer = studyAbstractCall.getLastSavedAnswer();

    	// CHecking for any errors:
		try {
			if (studyAbstractCall.catchQuestionAnswerError(answerRequest.getStudyId(), studyAbstractCall.userScreeningStatus.getIndexValue()) != null) {
				return new ResponseEntity(studyAbstractCall.catchQuestionAnswerError(answerRequest.getStudyId(), studyAbstractCall.userScreeningStatus.getIndexValue()).toMap(), HttpStatus.ACCEPTED);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}


		// You will get previous question or answer if you do index - questionDirection and next by index + questionDirection
		// It does not matter whether you are going forward or backward questionDirection takes care of that.

		indexValue = studyAbstractCall.getIndexValue();

		// Therefore, here we create two new ints for going next or previous question/answer --
		// not used currently -> int previous = indexValue - questionDirection;
		int next = indexValue + questionDirection;
		int current = indexValue;

		try {
		ScreeningQuestions questionToDisplayToUser = studyAbstractCall.getQuestionToDisplayToUser(current);
		ScreeningAnswers answerToDisplayToUser = studyAbstractCall.getAnswerToDisplayToUser(questionToDisplayToUser.getId());


		// Below section helps put the response used to display question/answer & choices to user.


			Boolean isLastQuestion = studyAbstractCall.getIsLastQuestionBool();

			response = studyAbstractCall.displayQuesNAns(questionToDisplayToUser, answerToDisplayToUser);

			try {
				if (lastSavedAnswer != null) {
					StudyInfoData screenTestData = screeningTest.screenTest(lastSavedAnswer.get(), questionDirection);
					if(screenTestData.isFinished == StudyInfoData.StudyInfoSatus.NONE){

						studyAbstractCall.setQuestionToDisplayToUser(current);

						response = studyAbstractCall.displayQuesNAns(questionToDisplayToUser, answerToDisplayToUser);
						studyAbstractCall.userScreeningStatus.setIndexValue(current);
						userScreeningStatusRepository.save(studyAbstractCall.userScreeningStatus);

					}
						if (screenTestData.isFinished == StudyInfoData.StudyInfoSatus.TRUE) {

							response = studyAbstractCall.displayNullQuesNAns(screenTestData.getMessage());

							if(screenTestData.isFinished == StudyInfoData.StudyInfoSatus.TRUE){
							response.setIsLastQuestion(true);}
							else if(screenTestData.isFinished == StudyInfoData.StudyInfoSatus.FALSE){
								response.setIsLastQuestion(false);
							}

							studyAbstractCall.userScreeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.UNDER_REVIEW);
							studyAbstractCall.userScreeningStatus.setIndexValue(current);
							userScreeningStatusRepository.save(studyAbstractCall.userScreeningStatus);

						} else if(screenTestData.isFinished == StudyInfoData.StudyInfoSatus.FALSE)  {

							if (!studyAbstractCall.findAnswerByIndex(3).getAnswerDescription().equals("Primary care doctor")) {


								if((questionDirection == 1 && studyAbstractCall.userScreeningStatus.getIndexValue() == 4) || (questionDirection == -1 && studyAbstractCall.userScreeningStatus.getIndexValue() == 4)) {

									questionToDisplayToUser = studyAbstractCall.getQuestionToDisplayToUser(next);
									answerToDisplayToUser = studyAbstractCall.getAnswerToDisplayToUser(questionToDisplayToUser.getId());

									studyAbstractCall.displayQuesNAns(questionToDisplayToUser, answerToDisplayToUser);

									studyAbstractCall.userScreeningStatus.setIndexValue(next);
									userScreeningStatusRepository.save(studyAbstractCall.userScreeningStatus);
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

		return new ResponseEntity(responseJson.toMap(), HttpStatus.ACCEPTED);
	}
}