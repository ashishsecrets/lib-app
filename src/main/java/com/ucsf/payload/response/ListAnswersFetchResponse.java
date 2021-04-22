package com.ucsf.payload.response;

import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.ScreeningQuestions;
import lombok.Data;

import java.util.List;

@Data
public class ListAnswersFetchResponse {

    private List<?> questions;
    private List<?> answers;
}
