package com.ucsf.service;

import com.ucsf.payload.request.ScreeningAnswerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface AnswerSaveService {

    ResponseEntity saveAnswer(ScreeningAnswerRequest screeningAnswerRequest);

}
