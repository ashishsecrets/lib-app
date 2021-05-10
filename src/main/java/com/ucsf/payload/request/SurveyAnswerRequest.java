package com.ucsf.payload.request;

import lombok.Data;

@Data
public class SurveyAnswerRequest {

    public enum ForwardStatus {
        FALSE, TRUE, NONE
    }

    private Long surveyId;
    private String answer;
    private String answerDescription;
    private ForwardStatus forward;
}

