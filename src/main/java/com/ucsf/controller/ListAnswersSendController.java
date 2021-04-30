package com.ucsf.controller;

import com.ucsf.common.ErrorCodes;
import com.ucsf.model.Auditable;
import com.ucsf.payload.request.ListAnswersFetchRequest;
import com.ucsf.payload.request.ScreeningAnswerRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.ListAnswersFetchResponse;
import com.ucsf.repository.ScreeningAnswerRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
@Api(tags = "List-Answers-Send Controller")
public class ListAnswersSendController {

    @Autowired
    ScreeningAnswerRepository screeningAnswerRepository;

    @Autowired
    ScreeningQuestionRepository screeningQuestionRepository;


    @Transactional
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ApiOperation(value = "Send saves answers", notes = "Send saves answers", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Data fetched successfully", response = ListAnswersFetchResponse.class) })
    @RequestMapping(value = "/fetch", method = RequestMethod.POST)
    public ResponseEntity<?> sendSavedAnswers(@RequestBody ListAnswersFetchRequest fetchRequest) throws Exception {

        JSONObject responseJson = new JSONObject();
        ListAnswersFetchResponse response = new ListAnswersFetchResponse();
        if (fetchRequest != null) {
            if (fetchRequest.getType().equals("screening")) {

                List<?> questionsList = screeningQuestionRepository.findByStudyId(fetchRequest.getStudyId());
                List<?> answersList = screeningAnswerRepository.findByStudyIdAndAnsweredById(fetchRequest.getUserId(), fetchRequest.getStudyId());
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

