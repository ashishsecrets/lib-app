package com.ucsf.payload.response;

import com.ucsf.model.*;
import lombok.Data;

import java.util.List;

@Data
public class SurveyQuestionResponse {

    //response sent to user in survey ans save api
    SurveyQuestion surveyQuestion;
    SurveyAnswer surveyAnswer;
    List<SurveyAnswerChoice> choices;
    Boolean isLastQuestion;
    String message;
    String information;
    Boolean isDisqualified;
    int totalQuestions;
    int skipCount;
}
