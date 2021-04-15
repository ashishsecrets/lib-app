package com.ucsf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.job.LoadScreeningQuestions;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	LoadScreeningQuestions loadScreeningQuestions;
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> createAuthenticationToken() throws Exception {
		loadScreeningQuestions.loadSheetContent();
		return ResponseEntity.ok("success");
	}

}
