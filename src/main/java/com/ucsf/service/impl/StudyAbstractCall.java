package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.controller.ScreeningAnswerController;
import com.ucsf.model.*;
import com.ucsf.payload.request.ScreeningAnswerRequest;
import com.ucsf.payload.request.SurveyAnswerRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.ScreeningQuestionResponse;
import com.ucsf.payload.response.SurveyQuestionResponse;
import com.ucsf.repository.*;
import com.ucsf.service.LoggerService;
import lombok.Data;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    SurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    SurveyAnswerRepository surveyAnswerRepository;

    @Autowired
    UserScreeningStatusRepository userScreeningStatusRepository;

    @Autowired
    UserSurveyStatusRepository userSurveyStatusRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyInfoCheck screeningTest;

    @Autowired
    ChoiceRepository choiceRepository;

    @Autowired
    SurveyChoiceRepository surveyChoiceRepository;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    InformativeRepository informativeRepository;

    @Autowired
    UserTasksRepository userTasksRepository;

    @Autowired
    SurveyRepository surveyRepository;


    private static Logger log = LoggerFactory.getLogger(ScreeningAnswerController.class);

    Boolean isSuccess = false;

    Long surveyTrueId;

    JSONObject responseJson = new JSONObject();
    JSONObject surveyresponseJson = new JSONObject();

    int quesIncrement = 0;
    int quesSurveyIncrement = 0;

    ScreeningQuestions questionToDisplayToUser;

    ScreeningQuestionResponse response = new ScreeningQuestionResponse();

    SurveyQuestionResponse surveyResponse = new SurveyQuestionResponse();


    User user = null;

    ScreeningAnswerRequest answerRequest = new ScreeningAnswerRequest();

    SurveyAnswerRequest surveyAnswerRequest = new SurveyAnswerRequest();

    UserScreeningStatus userScreeningStatus = new UserScreeningStatus();

    UserSurveyStatus userSurveyStatus = new UserSurveyStatus();

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

    public int getSurveyQuestionDirection(){
        if (surveyAnswerRequest.getForward() == SurveyAnswerRequest.ForwardStatus.FALSE) {
            quesSurveyIncrement = -1;
        } else if (surveyAnswerRequest.getForward() == SurveyAnswerRequest.ForwardStatus.TRUE) {
            quesSurveyIncrement = 1;
        } else if (surveyAnswerRequest.getForward() == SurveyAnswerRequest.ForwardStatus.NONE) {
            quesSurveyIncrement = 0;
        }
        return quesSurveyIncrement;
    }

    public void updateUserScreeningStatus(UserScreeningStatus.UserScreenStatus currentStatus, int newIndex){
        if (userScreeningStatus != null) {
            userScreeningStatus.setStudyId(answerRequest.getStudyId());
            userScreeningStatus.setUserScreeningStatus(currentStatus);
            userScreeningStatus.setIndexValue(newIndex);
            userScreeningStatusRepository.save(userScreeningStatus);
        }
    }

    public void updateUserSurveyStatus(UserSurveyStatus.SurveyStatus currentStatus, int newIndex){
        if (userSurveyStatus != null) {
            userSurveyStatus.setSurveyId(surveyAnswerRequest.getSurveyId());
            userSurveyStatus.setUserSurveyStatus(currentStatus);
            userSurveyStatus.setIndexValue(newIndex);
            userSurveyStatusRepository.save(userSurveyStatus);
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
                screenAnswerOp = Optional.ofNullable(screeningAnswerRepository.findByQuestionIdAndAnsweredByIdAndStudyId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue() - quesIncrement).getId()), user.getId(), answerRequest.getStudyId()));
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
                screenAnswerOp = Optional.of(screenAnswer);
                screeningAnswerRepository.save(screenAnswer);
                isSuccess = true;
            }
            else{
                screenAnswerOp = Optional.ofNullable(screeningAnswerRepository.findByQuestionIdAndAnsweredByIdAndStudyId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue() - quesIncrement).getId()), user.getId(), answerRequest.getStudyId()));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return  screenAnswerOp;
    }

    public Optional<SurveyAnswer> getLastSavedSurveyAnswer(){
        Optional<SurveyAnswer> surveyAnswerOp = null;
        try {
            if (!surveyAnswerRequest.getAnswer().isEmpty()) {
                surveyAnswerOp = Optional.ofNullable(surveyAnswerRepository.findByQuestionIdAndAnsweredByIdAndTaskTrueId((surveyQuestionRepository.findBySurveyIdAndIndexValue(surveyAnswerRequest.getSurveyId(), userSurveyStatus.getIndexValue() - quesSurveyIncrement).getId()), user.getId(), surveyTrueId));
                SurveyAnswer surveyAnswer;
                if (surveyAnswerOp.isPresent()) {
                    surveyAnswer = surveyAnswerRepository.findById(surveyAnswerOp.get().getId()).get();
                } else {
                    surveyAnswer = new SurveyAnswer();
                }
                surveyAnswer.setAnswerDescription(surveyAnswerRequest.getAnswerDescription().toString());
                surveyAnswer.setAnswerChoice(surveyAnswerRequest.getAnswer());
                surveyAnswer.setQuestionId(
                        (surveyQuestionRepository.findBySurveyIdAndIndexValue(surveyAnswerRequest.getSurveyId(),
                                userSurveyStatus.getIndexValue() - quesSurveyIncrement).getId()));
                surveyAnswer.setSurveyId(surveyAnswerRequest.getSurveyId());
                surveyAnswer.setAnsweredById(user.getId());
                surveyAnswer.setTaskTrueId(surveyTrueId);
                surveyAnswer.setIndexValue(userSurveyStatus.getIndexValue() - quesSurveyIncrement);
                surveyAnswerOp = Optional.of(surveyAnswer);
                surveyAnswerRepository.save(surveyAnswer);
                isSuccess = true;
            }
            else{
                surveyAnswerOp = Optional.ofNullable(surveyAnswerRepository.findByQuestionIdAndAnsweredByIdAndTaskTrueId((surveyQuestionRepository.findBySurveyIdAndIndexValue(surveyAnswerRequest.getSurveyId(), userSurveyStatus.getIndexValue() - quesSurveyIncrement).getId()), user.getId(), surveyTrueId));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return  surveyAnswerOp;
    }

    public int getIndexValue(){
        int indexValue = userScreeningStatus.getIndexValue();

        return indexValue;
    }

    public int getSurveyIndexValue(){
        int surveyIndexValue = userSurveyStatus.getIndexValue();

        return surveyIndexValue;
    }

    public JSONObject catchQuestionAnswerError() {

        Optional<ScreeningQuestions> sq = Optional.ofNullable(screeningQuestionRepository
                .findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue()));

        JSONObject responseEntity = new JSONObject();

            if (sq.isPresent()) {
                if (userScreeningStatus.getIndexValue() != sq.get().getId()) {
                    responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_INDEXVALUE.code(),
                            Constants.INVALID_INDEXVALUE.errordesc()));
                }
            } else {
                /*responseJson.put("error",
                        new ErrorResponse(ErrorCodes.QUESTION_NOT_FOUND.code(), Constants.QUESTION_NOT_FOUND.errordesc()));*/
                if (getIsLastQuestionBool()) {
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
                    response.setInformation(informativeRepository.findByIndexValueAndStudyId(0, answerRequest.getStudyId()).getInfoDescription());
                    response.setIsLastQuestion(false);
                    responseJson.put("data", response);
                    responseEntity = responseJson;
                    userScreeningStatus.setIndexValue(-1);
                    userScreeningStatusRepository.save(userScreeningStatus);
                }
                userScreeningStatus.setIndexValue(userScreeningStatus.getIndexValue() - quesIncrement);
                userScreeningStatusRepository.save(userScreeningStatus);
            }
        //returning responseJson
        return responseEntity;
    }

    public JSONObject catchSurveyQuestionAnswerError() {

        Optional<SurveyQuestion> sq = Optional.ofNullable(surveyQuestionRepository
                .findBySurveyIdAndIndexValue(surveyAnswerRequest.getSurveyId(), userSurveyStatus.getIndexValue()));

        JSONObject responseEntity = new JSONObject();

        if (sq.isPresent()) {
            if (userSurveyStatus.getIndexValue() != sq.get().getId()) {
                surveyresponseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_INDEXVALUE.code(),
                        Constants.INVALID_INDEXVALUE.errordesc()));
            }
        } else {
                /*responseJson.put("error",
                        new ErrorResponse(ErrorCodes.QUESTION_NOT_FOUND.code(), Constants.QUESTION_NOT_FOUND.errordesc()));*/
            if (getIsLastSurveyQuestionBool()) {
                surveyresponseJson.remove("error");
                surveyResponse = new SurveyQuestionResponse();
                SurveyQuestion sc = null;
                SurveyAnswer sa = null;
                List<SurveyAnswerChoice> choices = null;
                surveyResponse.setSurveyQuestion(sc);
                surveyResponse.setSurveyAnswer(sa);
                surveyResponse.setChoices(choices);
                surveyResponse.setMessage("Survey complete.");
                surveyResponse.setIsLastQuestion(true);
                surveyResponse.setInformation("");
                surveyresponseJson.put("data", surveyResponse);
                responseEntity = surveyresponseJson;
                userSurveyStatus.setUserSurveyStatus(UserSurveyStatus.SurveyStatus.UNDER_REVIEW);
                userSurveyStatus.setIndexValue(userSurveyStatus.getIndexValue() - quesSurveyIncrement);
            } else if (userSurveyStatus.getIndexValue() <= 0) {
                surveyresponseJson.remove("error");
                surveyResponse = new SurveyQuestionResponse();
                SurveyQuestion sc = null;
                SurveyAnswer sa = null;
                List<SurveyAnswerChoice> choices = null;
                surveyResponse.setSurveyQuestion(sc);
                surveyResponse.setSurveyAnswer(sa);
                surveyResponse.setChoices(choices);
                surveyResponse.setMessage("Please go forward and answer first question.");
                surveyResponse.setInformation(informativeRepository.findByIndexValueAndInfoTypeAndTypeId(0, "survey", surveyAnswerRequest.getSurveyId()).getInfoDescription());
                surveyResponse.setIsLastQuestion(false);
                surveyresponseJson.put("data", surveyResponse);
                responseEntity = surveyresponseJson;
                userSurveyStatus.setIndexValue(0);
                userSurveyStatusRepository.save(userSurveyStatus);
            }

            userSurveyStatusRepository.save(userSurveyStatus);
        }
        //returning responseJson
        return responseEntity;
    }

    public ScreeningQuestions getQuestionToDisplayToUser(int index) {

        return screeningQuestionRepository.findByStudyIdAndIndexValue(
                userScreeningStatusRepository.findByUserIdAndStudyId(user.getId(), answerRequest.getStudyId()).getStudyId(), index);
    }

    public void setQuestionToDisplayToUser(int index) {

      questionToDisplayToUser = screeningQuestionRepository.findByStudyIdAndIndexValue(
                userScreeningStatusRepository.findByUserId(user.getId()).getStudyId(), index);
    }

    public ScreeningAnswers getAnswerToDisplayToUser(Long index) {
        return screeningAnswerRepository.findByQuestionIdAndAnsweredByIdAndStudyId(index, user.getId(), answerRequest.getStudyId());
    }

    public Boolean getIsLastQuestionBool() {
        Boolean value;

        value = !Optional.ofNullable(screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue() + 1)).isPresent();

        return value;
    }

    public Boolean getIsLastSurveyQuestionBool() {
        Boolean value;

        value = !Optional.ofNullable(surveyQuestionRepository.findBySurveyIdAndIndexValue(surveyAnswerRequest.getSurveyId(), userSurveyStatus.getIndexValue() + 1)).isPresent();

        return value;
    }

    public SurveyQuestion getSurveyQuestionToDisplayToUser(int index) {

        return surveyQuestionRepository.findBySurveyIdAndIndexValue(surveyAnswerRequest.getSurveyId(), index);
    }

    public SurveyAnswer getSurveyAnswerToDisplayToUser(Long index) {
        return surveyAnswerRepository.findByQuestionIdAndAnsweredById(index, user.getId());
    }


    public ScreeningQuestionResponse displayQuesNAns(ScreeningQuestions questionToDisplayToUser, ScreeningAnswers answerToDisplayToUser) {

        List<ScreeningAnsChoice> choices = choiceRepository.findByQuestionIdAndStudyId(questionToDisplayToUser.getId(), answerRequest.getStudyId());
        response.setScreeningQuestions(questionToDisplayToUser);
        if (answerToDisplayToUser == null) {
            answerToDisplayToUser = new ScreeningAnswers();
        }
        response.setScreeningAnswers(answerToDisplayToUser);
        response.setChoices(choices);
        response.setIsLastQuestion(getIsLastQuestionBool());
        response.setMessage("");
        if(informativeRepository.findByIndexValueAndStudyId(getIndexValue(), answerRequest.getStudyId()) != null){
            response.setInformation(informativeRepository.findByIndexValueAndStudyId(getIndexValue(), answerRequest.getStudyId()).getInfoDescription());
        }
        else{
            response.setInformation("");
        }
        response.setIsDisqualified(userScreeningStatus.getUserScreeningStatus() == UserScreeningStatus.UserScreenStatus.DISQUALIFIED);

        return response;
    }

    public SurveyQuestionResponse displaySurveyNullQuesNAns(String message) {

        surveyResponse.setSurveyQuestion(new SurveyQuestion());
        surveyResponse.setSurveyAnswer(new SurveyAnswer());
        surveyResponse.setChoices(new ArrayList<>());
        surveyResponse.setMessage(message);
        surveyResponse.setInformation("");
        surveyResponse.setIsDisqualified(userScreeningStatus.getUserScreeningStatus() == UserScreeningStatus.UserScreenStatus.DISQUALIFIED);
        return surveyResponse;
    }

    public SurveyQuestionResponse displaySurveyQuesNAns(SurveyQuestion questionToDisplayToUser, SurveyAnswer answerToDisplayToUser) {

        List<SurveyAnswerChoice> choices = surveyChoiceRepository.findByQuestionId(questionToDisplayToUser.getId());
        surveyResponse.setSurveyQuestion(questionToDisplayToUser);
        if (answerToDisplayToUser == null) {
            answerToDisplayToUser = new SurveyAnswer();
        }
        surveyResponse.setSurveyAnswer(answerToDisplayToUser);
        surveyResponse.setChoices(choices);
        surveyResponse.setIsLastQuestion(getIsLastSurveyQuestionBool());
        surveyResponse.setMessage("");
        surveyResponse.setInformation("");
        surveyResponse.setIsDisqualified(userSurveyStatus.getUserSurveyStatus() == UserSurveyStatus.SurveyStatus.DISQUALIFIED);

        return surveyResponse;
    }

    public ScreeningQuestionResponse displayNullQuesNAns(String message) {

        response.setScreeningQuestions(new ScreeningQuestions());
        response.setScreeningAnswers(new ScreeningAnswers());
        response.setChoices(new ArrayList<>());
        response.setMessage(message);
        response.setInformation("");
        response.setIsDisqualified(userScreeningStatus.getUserScreeningStatus() == UserScreeningStatus.UserScreenStatus.DISQUALIFIED);
        return response;
    }

    public ScreeningAnswers findAnswerByIndex(int i) {

       return Optional.ofNullable(screeningAnswerRepository.findByQuestionIdAndAnsweredByIdAndStudyId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), i).getId()), user.getId(), answerRequest.getStudyId())).get();

    }

    public SurveyAnswer findSurveyAnswerByIndex(int i) {

        return Optional.ofNullable(surveyAnswerRepository.findByQuestionIdAndAnsweredByIdAndTaskTrueId((surveyQuestionRepository.findBySurveyIdAndIndexValue(surveyAnswerRequest.getSurveyId(), i).getId()), user.getId(), surveyTrueId)).get();

    }

    public int getTotalQuestionsCount() {
        List<ScreeningQuestions> list = screeningQuestionRepository.findByStudyId(answerRequest.getStudyId());

        return list.size();
    }

    public void correctSurveyId() {
        Optional<UserTasks> userTaskOp = userTasksRepository.findById(surveyAnswerRequest.getSurveyId());
        UserTasks userTask = userTaskOp.get();
        surveyTrueId = surveyAnswerRequest.getSurveyId();
        surveyAnswerRequest.setSurveyId(userTask.getTaskId());
    }

    public int getTotalSurveyQuestionsCount() {
        List<SurveyQuestion> list = surveyQuestionRepository.findBySurveyId(surveyAnswerRequest.getSurveyId());

        return list.size();
    }

    public Optional<ScreeningAnswers> getCurrentAnswer() {
        Optional<ScreeningAnswers> currentAnswerOp = Optional.ofNullable(screeningAnswerRepository.findByQuestionIdAndAnsweredByIdAndStudyId((screeningQuestionRepository.findByStudyIdAndIndexValue(answerRequest.getStudyId(), userScreeningStatus.getIndexValue()).getId()), user.getId(), answerRequest.getStudyId()));
        ScreeningAnswers currentAnswer = new ScreeningAnswers();
        if(!currentAnswerOp.isPresent()){
            currentAnswerOp = Optional.of(currentAnswer);
            currentAnswerOp.get().setAnswerDescription("");
        }
        return currentAnswerOp;
    }

    public int getSurveySkipCount() {

        return getMaxSurveyAnswerSaved() - getTotalSurveyAnswersCount();
    }

    private int getMaxSurveyAnswerSaved() {
        List<SurveyAnswer> answerList = surveyAnswerRepository.findByTaskTrueIdAndAnsweredById(surveyTrueId, user.getId());
        SurveyAnswer answer = answerList.get(answerList.size()-1);
        for(SurveyAnswer item : answerList){
            if(item.getIndexValue() > answer.getIndexValue()){
                answer = item;
            }
        }
        System.out.println(answer.getIndexValue());
        System.out.println(answerList.size());
        return answer.getIndexValue();
    }

    private int getTotalSurveyAnswersCount() {
        int totalAnswers = 0;
        List<SurveyAnswer> surveyAnswersList = surveyAnswerRepository.findByTaskTrueIdAndAnsweredById(surveyTrueId, user.getId());
        if(surveyAnswersList != null){totalAnswers = surveyAnswersList.size();}
        return totalAnswers;
    }
}
