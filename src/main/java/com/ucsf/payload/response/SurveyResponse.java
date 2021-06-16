package com.ucsf.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class SurveyResponse {

    Long surveyTrueId;
    String surveyName;
    Date startDate;
    Date dueDate;
    String surveyStatus;
    int surveyPercentage;
}
