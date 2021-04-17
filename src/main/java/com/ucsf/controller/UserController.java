package com.ucsf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.job.LoadScreeningQuestions;
import com.ucsf.job.LoadSurveyQuestions;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	LoadScreeningQuestions loadScreeningQuestions;
	
	@Autowired
	LoadSurveyQuestions loadSurveyQuestions;
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> createAuthenticationToken() throws Exception {
		System.out.println("1111111111");
		//loadScreeningQuestions.loadSheetContent();
		loadSurveyQuestions.loadSheetContent();
		return ResponseEntity.ok("success");
	}

}
