package com.ucsf.controller;

import com.ucsf.common.ErrorCodes;
import com.ucsf.model.Auditable;
import com.ucsf.payload.request.ListAnswersFetchRequest;
import com.ucsf.payload.request.ScreeningAnswerRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.ListAnswersFetchResponse;
import com.ucsf.repository.ScreeningAnswerRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.service.AnswerSaveService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/physician")
public class ListAnswersSendController {

    @Autowired
    ScreeningAnswerRepository screeningAnswerRepository;

    @Autowired
    ScreeningQuestionRepository screeningQuestionRepository;


    @Transactional
    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "/fetch", method = RequestMethod.POST)
    public ResponseEntity<?> sendSavedAnswers(@RequestBody ListAnswersFetchRequest fetchRequest) throws Exception {

        JSONObject responseJson = new JSONObject();
        ListAnswersFetchResponse response = new ListAnswersFetchResponse();
        if (fetchRequest != null) {
            if (fetchRequest.getType().equals("screening")) {

                List<?> questionsList = screeningQuestionRepository.findByStudyId(fetchRequest.getStudyId());
                List<?> answersList = screeningAnswerRepository.findByStudyId(fetchRequest.getStudyId());
                response.setQuestions(questionsList);
                response.setAnswers(answersList);
            } else {
                responseJson.put("error", new ErrorResponse(ErrorCodes.QUESTION_NOT_FOUND.code(), "Questions not Found")); }
        } else {
            // responseJson.put("error", )
            }
        responseJson.put("data", response);

        return new ResponseEntity(responseJson.toMap(), HttpStatus.ACCEPTED);
    }
}

