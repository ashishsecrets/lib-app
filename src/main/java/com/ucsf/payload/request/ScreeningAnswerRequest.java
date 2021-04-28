package com.ucsf.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class ScreeningAnswerRequest {

    public enum ForwardStatus {
        FALSE, TRUE, NONE
    }

    private Long studyId;
    private String answer;
    private String answerDescription;
    private ForwardStatus forward;
}
