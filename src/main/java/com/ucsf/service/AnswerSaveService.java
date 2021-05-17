package com.ucsf.service;

import com.ucsf.payload.request.ScreeningAnswerRequest;
import com.ucsf.payload.request.SurveyAnswerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface AnswerSaveService {

    ResponseEntity saveAnswer(ScreeningAnswerRequest screeningAnswerRequest);

    ResponseEntity saveSurveyAnswer(SurveyAnswerRequest answerRequest);
}
