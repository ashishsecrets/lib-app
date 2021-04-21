package com.ucsf.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class ScreeningAnswerRequest {

    private Long studyId;
    private String answer;
    private List<String> answerDescription;
    private Boolean forward;
}
