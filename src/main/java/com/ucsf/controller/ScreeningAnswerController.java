package com.ucsf.controller;

import com.ucsf.payload.response.ScreeningQuestionResponse;
import com.ucsf.service.AnswerSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ucsf.payload.request.ScreeningAnswerRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.transaction.Transactional;

@RestController
@CrossOrigin
@RequestMapping("/api/answers")
@Api(tags = "Screening-Answer Controller")
public class ScreeningAnswerController {

	@Autowired
	AnswerSaveService answerSaveService;

	@Transactional
	@ApiOperation(value = "Save answer", notes = "Save screening answer", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Answer saved successfully", response = ScreeningQuestionResponse.class) })
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<?> saveScreeningAnswers(@RequestBody ScreeningAnswerRequest answerRequest) throws Exception {
		return answerSaveService.saveAnswer(answerRequest);

	}
}
