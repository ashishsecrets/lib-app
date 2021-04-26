package com.ucsf.service.impl.AnswerImpl;

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
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service("answerSaveService")
public class AnswerSaveImpl implements AnswerSaveService {

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

	@Autowired
	ScreeningTest screeningTest;

	private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

	@Override
	public ResponseEntity saveAnswer(ScreeningAnswerRequest answerRequest) {
		loggerService.printLogs(log, "saveScreeningAnswers", "Saving screening Answers");
		User user = null;
		JSONObject responseJson = new JSONObject();
		Optional<ScreeningAnswers> screenAnswerOp = null;
		Boolean isNewStatus = false;
		Boolean isSuccess = false;
		int quesIncrement = 0;
		if (answerRequest.getForward() == ScreeningAnswerRequest.ForwardStatus.FALSE) {
			quesIncrement = -1;
		} else if (answerRequest.getForward() == ScreeningAnswerRequest.ForwardStatus.TRUE) {
			quesIncrement = 1;
		} else if (answerRequest.getForward() == ScreeningAnswerRequest.ForwardStatus.NONE) {
			quesIncrement = 0;
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
			userScreeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.INPROGRESS);
			userScreeningStatus.setIndexValue(userScreeningStatus.getIndexValue() + quesIncrement);
			userScreeningStatusRepository.save(userScreeningStatus);
		} else {
			userScreeningStatus = new UserScreeningStatus();
			userScreeningStatus.setStudyId(answerRequest.getStudyId());
			userScreeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.INPROGRESS);
			userScreeningStatus.setUserId(user.getId());
			userScreeningStatus.setIndexValue(1);
			quesIncrement = 1;
			userScreeningStatusRepository.save(userScreeningStatus);
			isNewStatus = true;
			loggerService.printLogs(log, "saveScreeningAnswers", "UserScreen Status updated");
		}

		try {
			if (!answerRequest.getAnswer().isEmpty()) {
				screenAnswerOp = Optional
						.ofNullable(
								screeningAnswerRepository
										.findByQuestionId(
												(screeningQuestionRepository
														.findByStudyIdAndIndexValue(answerRequest.getStudyId(),
																userScreeningStatus.getIndexValue() - quesIncrement)
														.getId())));
				ScreeningAnswers screenAnswer;
				if (screenAnswerOp.isPresent()) {
					screenAnswer = screeningAnswerRepository.findById(screenAnswerOp.get().getId()).get();
				} else {
					screenAnswer = new ScreeningAnswers();
				}
				screenAnswer.setAnswerDescription(answerRequest.getAnswerDescription().toString());
				screenAnswer.setAnswerChoice(answerRequest.getAnswer());
				screenAnswer.setQuestionId(
						(screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(),
								userScreeningStatus.getIndexValue() - quesIncrement).getId()));
				screenAnswer.setStudyId(answerRequest.getStudyId());
				screenAnswer.setAnsweredById(user.getId());
				screenAnswer.setIndexValue(userScreeningStatus.getIndexValue() - quesIncrement);
				screeningAnswerRepository.save(screenAnswer);
				isSuccess = true;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		// responseJson.put("data", new SuccessResponse(isSuccess, "Screening answer
		// saved successfully!")); }

		Optional<ScreeningQuestions> sq = Optional.ofNullable(screeningQuestionRepository
				.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue()));
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
			if (isSuccess) {
				string = "Last question saved";
			}
			if (userScreeningStatus.getIndexValue() > 0) {
				responseJson.remove("error");
				ScreeningQuestionResponse response = new ScreeningQuestionResponse();
				ScreeningQuestions sc = null;
				ScreeningAnswers sa = null;
				List<ScreeningAnsChoice> choices = null;
				response.setScreeningQuestions(sc);
				response.setScreeningAnswers(sa);
				response.setChoices(choices);
				response.setIsLastQuestion(true);
				responseJson.put("data", response);
				userScreeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.UNDER_REVIEW);
			} else if (userScreeningStatus.getIndexValue() <= 0) {
				responseJson.put("error",
						new ErrorResponse(200, "Cannot go further back! Answer first question" + " " + string));
				userScreeningStatus.setIndexValue(-1);
				userScreeningStatusRepository.save(userScreeningStatus);
			}
			userScreeningStatus.setIndexValue(userScreeningStatus.getIndexValue() - quesIncrement);
			userScreeningStatusRepository.save(userScreeningStatus);
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
		int indexValue = userScreeningStatusRepository.findByUserId(user.getId()).getIndexValue();
		if (userScreeningStatus.getIndexValue() == 4) {
			if (answerRequest.getAnswerDescription() != null
					&& !answerRequest.getAnswerDescription().equals("Primary care doctor")) {
				indexValue = userScreeningStatusRepository.findByUserId(user.getId()).getIndexValue() + 1;
				userScreeningStatus.setIndexValue(userScreeningStatus.getIndexValue()+1);
				userScreeningStatusRepository.save(userScreeningStatus);
			}
		}
		ScreeningQuestions sc = screeningQuestionRepository.findByStudyIdAndIndexValue(
				userScreeningStatusRepository.findByUserId(user.getId()).getStudyId(), indexValue);
		ScreeningAnswers sa = screeningAnswerRepository.findByQuestionIdAndAnsweredById(sc.getId(), user.getId());

		try {
			List<ScreeningAnsChoice> choices = choiceRepository.findByQuestionId(sc.getId());
			ScreeningQuestionResponse response = new ScreeningQuestionResponse();
			response.setScreeningQuestions(sc);
			if (sa == null) {
				sa = new ScreeningAnswers();
			}
			response.setScreeningAnswers(sa);
			response.setChoices(choices);
			response.setIsLastQuestion(!Optional.ofNullable(screeningQuestionRepository
					.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue() + 1))
					.isPresent());
			response.setMessage("");
			responseJson.put("data", response);
			try {
				if (screenAnswerOp != null) {
					ScreenTestData screenTestData = screeningTest.screenTest(screenAnswerOp.get());
					if (screenTestData != null && screenTestData.isFinished) {
						response.setScreeningQuestions(new ScreeningQuestions());
						response.setScreeningAnswers(new ScreeningAnswers());
						response.setChoices(new ArrayList<>());
						response.setMessage(screeningTest.screenTest(screenAnswerOp.get()).getMessage());
						response.setIsLastQuestion(screeningTest.screenTest(screenAnswerOp.get()).isFinished);
						userScreeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.UNDER_REVIEW);
						responseJson.put("data", response);
					}
				}
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return new ResponseEntity(responseJson.toMap(), HttpStatus.ACCEPTED);
	}
}
