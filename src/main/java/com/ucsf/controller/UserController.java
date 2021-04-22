package com.ucsf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.job.LoadScreeningQuestions;
import com.ucsf.job.LoadSurveyQuestions;
import com.ucsf.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	LoadScreeningQuestions loadScreeningQuestions;
	
	@Autowired
	LoadSurveyQuestions loadSurveyQuestions;
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> getAllUsers() throws Exception {
		System.out.println("1111111111");
		return ResponseEntity.ok("success");
	}
	
	@RequestMapping(value = "/users/{page}/{size}", method = RequestMethod.GET)
	public Page<User> getUsers(@PathVariable int page, @PathVariable int size) {
		return userService.findAll(page, size);
	}

}
