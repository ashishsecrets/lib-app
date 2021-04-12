package com.ucsf.payload.request;

import lombok.Data;

@Data
public class ScreeningAnswerRequest {

	private Long questionId;
    private Long studyId;
    private int indexValue;
    private String answer;
    private String answerDescription;
}
