package com.ucsf.controller;

import com.ucsf.job.LoadStudyInformatives;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.job.LoadConsentFormData;
import com.ucsf.job.LoadScreeningQuestions;
import com.ucsf.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
@Api(tags = "User Controller")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	LoadScreeningQuestions loadScreeningQuestions;

	@Autowired
	LoadStudyInformatives loadStudyInformatives;
	
	@Autowired
	LoadConsentFormData loadConsentFormData;
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> getAllUsers() throws Exception {
		loadScreeningQuestions.loadSheetContent();
		System.out.println("1111111111");
		//loading informatives
		loadStudyInformatives.loadSheetContent();
		//loadConsentFormData.loadFormContent();
		System.out.println("2222222222");
		return ResponseEntity.ok("success");
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "Get all users", notes = "Get all users", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all users", response = User.class) })
	@RequestMapping(value = "/users/{page}/{size}", method = RequestMethod.GET)
	public Page<User> getUsers(@PathVariable int page, @PathVariable int size) {
		//Todo get only patients not admin
		return userService.findAll(page, size);
	}

}
