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
import com.ucsf.repository.*;
import com.ucsf.service.LoggerService;
import lombok.Data;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Data
public class StudyAbstractCall {

    @Autowired
    ScreeningAnswerRepository screeningAnswerRepository;

    @Autowired
    ScreeningQuestionRepository screeningQuestionRepository;

    @Autowired
    UserScreeningStatusRepository userScreeningStatusRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyInfoCheck screeningTest;

    @Autowired
    ChoiceRepository choiceRepository;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    InformativeRepository informativeRepository;


    private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

    Boolean isSuccess = false;

    JSONObject responseJson = new JSONObject();

    int quesIncrement = 0;

    ScreeningQuestions questionToDisplayToUser;

    ScreeningQuestionResponse response = new ScreeningQuestionResponse();

    User user = null;

    ScreeningAnswerRequest answerRequest = new ScreeningAnswerRequest();

    UserScreeningStatus userScreeningStatus = new UserScreeningStatus();

    public User getUserDetails(String email){

            user = userRepository.findByEmail(email);

        return user;
    }

    public int getQuestionDirection(){
        if (answerRequest.getForward() == ScreeningAnswerRequest.ForwardStatus.FALSE) {
            quesIncrement = -1;
        } else if (answerRequest.getForward() == ScreeningAnswerRequest.ForwardStatus.TRUE) {
            quesIncrement = 1;
        } else if (answerRequest.getForward() == ScreeningAnswerRequest.ForwardStatus.NONE) {
            quesIncrement = 0;
        }
        return quesIncrement;
    }

    public void updateUserScreeningStatus(UserScreeningStatus.UserScreenStatus currentStatus, int newIndex){
        if (userScreeningStatus != null) {
            userScreeningStatus.setStudyId(answerRequest.getStudyId());
            userScreeningStatus.setUserScreeningStatus(currentStatus);
            userScreeningStatus.setIndexValue(newIndex);
            userScreeningStatusRepository.save(userScreeningStatus);
        }
    }

    public UserScreeningStatus getUserScreeningStatus(){
        return userScreeningStatus;
    }

    public void setUserScreeningStatus(UserScreeningStatus userScreeningStatus){
        userScreeningStatus = userScreeningStatus;
    }

    public Optional<ScreeningAnswers> getLastSavedAnswer(){
        Optional<ScreeningAnswers> screenAnswerOp = null;
        try {
            if (!answerRequest.getAnswer().isEmpty()) {
                screenAnswerOp = Optional.ofNullable(screeningAnswerRepository.findByQuestionId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue() - quesIncrement).getId())));
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
            else{
                screenAnswerOp = Optional.ofNullable(screeningAnswerRepository.findByQuestionId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue() - quesIncrement).getId())));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return  screenAnswerOp;
    }

    public int getIndexValue(){
        int indexValue = userScreeningStatus.getIndexValue();

        return indexValue;
    }

    public JSONObject catchQuestionAnswerError(Long studyId, int indexValue) {

        Optional<ScreeningQuestions> sq = Optional.ofNullable(screeningQuestionRepository
                .findByStudyIdAndIndexValue(studyId, indexValue));

        JSONObject responseEntity = null;

            if (sq.isPresent()) {
                if (userScreeningStatus.getIndexValue() != sq.get().getId()) {
                    responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_INDEXVALUE.code(),
                            Constants.INVALID_INDEXVALUE.errordesc()));
                }
            } else {
                /*responseJson.put("error",
                        new ErrorResponse(ErrorCodes.QUESTION_NOT_FOUND.code(), Constants.QUESTION_NOT_FOUND.errordesc()));*/
                if (userScreeningStatus.getIndexValue() > 0) {
                    responseJson.remove("error");
                    response = new ScreeningQuestionResponse();
                    ScreeningQuestions sc = null;
                    ScreeningAnswers sa = null;
                    List<ScreeningAnsChoice> choices = null;
                    response.setScreeningQuestions(sc);
                    response.setScreeningAnswers(sa);
                    response.setChoices(choices);
                    response.setMessage("Screening complete.");
                    response.setIsLastQuestion(true);
                    response.setInformation("");
                    responseJson.put("data", response);
                    responseEntity = responseJson;
                    userScreeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.UNDER_REVIEW);
                } else if (userScreeningStatus.getIndexValue() <= 0) {
                    responseJson.remove("error");
                    response = new ScreeningQuestionResponse();
                    ScreeningQuestions sc = null;
                    ScreeningAnswers sa = null;
                    List<ScreeningAnsChoice> choices = null;
                    response.setScreeningQuestions(sc);
                    response.setScreeningAnswers(sa);
                    response.setChoices(choices);
                    response.setMessage("Please go forward and answer first question.");
                    response.setInformation(informativeRepository.findByIndexValueAndStudyId(0, 1l).getInfoDescription());
                    response.setIsLastQuestion(false);
                    responseJson.put("data", response);
                    responseEntity = responseJson;
                    userScreeningStatus.setIndexValue(-1);
                    userScreeningStatusRepository.save(userScreeningStatus);
                }
                userScreeningStatus.setIndexValue(userScreeningStatus.getIndexValue() - quesIncrement);
                userScreeningStatusRepository.save(userScreeningStatus);
            }

        return responseEntity;
    }

    public ScreeningQuestions getQuestionToDisplayToUser(int index) {

        return screeningQuestionRepository.findByStudyIdAndIndexValue(
                userScreeningStatusRepository.findByUserId(user.getId()).getStudyId(), index);
    }

    public void setQuestionToDisplayToUser(int index) {

      questionToDisplayToUser = screeningQuestionRepository.findByStudyIdAndIndexValue(
                userScreeningStatusRepository.findByUserId(user.getId()).getStudyId(), index);
    }

    public ScreeningAnswers getAnswerToDisplayToUser(Long index) {
        return screeningAnswerRepository.findByQuestionIdAndAnsweredById(index, user.getId());
    }

    public Boolean getIsLastQuestionBool() {
        Boolean value = false;

        value = !Optional.ofNullable(screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue() + 1)).isPresent();

        return value;
    }

    public ScreeningQuestionResponse displayQuesNAns(ScreeningQuestions questionToDisplayToUser, ScreeningAnswers answerToDisplayToUser) {

        List<ScreeningAnsChoice> choices = choiceRepository.findByQuestionId(questionToDisplayToUser.getId());
        response.setScreeningQuestions(questionToDisplayToUser);
        if (answerToDisplayToUser == null) {
            answerToDisplayToUser = new ScreeningAnswers();
        }
        response.setScreeningAnswers(answerToDisplayToUser);
        response.setChoices(choices);
        response.setIsLastQuestion(getIsLastQuestionBool());
        response.setMessage("");
        if(informativeRepository.findByIndexValueAndStudyId(getIndexValue(), 1l) != null){
            response.setInformation(informativeRepository.findByIndexValueAndStudyId(getIndexValue(), 1l).getInfoDescription());
        }
        else{
            response.setInformation("");
        }

        return response;
    }

    public ScreeningQuestionResponse displayNullQuesNAns(String message) {

        response.setScreeningQuestions(new ScreeningQuestions());
        response.setScreeningAnswers(new ScreeningAnswers());
        response.setChoices(new ArrayList<>());
        response.setMessage(message);
        response.setMessage("");
        if(informativeRepository.findByIndexValueAndStudyId(getIndexValue(), 1l) != null){
            response.setInformation(informativeRepository.findByIndexValueAndStudyId(getIndexValue(), 1l).getInfoDescription());
        }
        else{
            response.setInformation("");
        }
        return response;
    }

    public ScreeningAnswers findAnswerByIndex(int i) {

       return Optional.ofNullable(screeningAnswerRepository.findByQuestionId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), i).getId()))).get();

    }
}
