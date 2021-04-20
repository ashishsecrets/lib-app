package com.ucsf.payload.request;

import lombok.Data;

@Data
public class ScreeningAnswerRequest {

    private Long studyId;
    private String answer;
    private String answerDescription;
}
