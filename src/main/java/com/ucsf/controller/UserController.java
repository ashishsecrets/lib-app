package com.ucsf.controller;

import com.ucsf.job.*;
import com.ucsf.job.LoadStudyInformatives;
import com.ucsf.payload.response.ErrorResponse;

import com.ucsf.service.FirebaseService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.job.LoadConsentFormData;
import com.ucsf.job.LoadScreeningQuestions;
import com.ucsf.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.concurrent.ExecutionException;

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

	@Autowired
	LoadSurveyQuestions loadSurveyQuestions;


	@Autowired
	LoadStudyTasks loadStudyTasks;

	@Autowired
	FirebaseService firebaseService;

	@PutMapping("/updateMessage")
	public String updatePatient(@RequestBody String message ) throws InterruptedException, ExecutionException {
		return firebaseService.updateMessageDetails(message);
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> getAllUsers() throws Exception {
		loadScreeningQuestions.loadSheetContent();
		System.out.println("1111111111");
		// loading informatives
		loadStudyInformatives.loadSheetContent();
		loadConsentFormData.loadFormContent();
		System.out.println("2222222222");
		loadStudyTasks.loadSheetContent();
		loadSurveyQuestions.loadSheetContent();
		System.out.println("3333333333");
		return ResponseEntity.ok("success");
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "Get all users", notes = "Get all users", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all users", response = User.class) })
	@RequestMapping(value = "/users/{page}/{size}", method = RequestMethod.GET)
	public Page<User> getUsers(@PathVariable int page, @PathVariable int size) {
		return userService.findAll(page, size);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "Get user by id", notes = "Get user by id", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User with Id", response = User.class) })
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getUserById(@PathVariable Long id) {

		JSONObject response = new JSONObject();
		User user = null;
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
		} else {
			response.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(response.toMap(), HttpStatus.UNAUTHORIZED);
		}
		try {
			user = userService.findById(id);
			response.put("data", user);
			return new ResponseEntity(response.toMap(), HttpStatus.OK);
		}
		catch (Exception e) {
			response.put("error", e.getMessage());
			return new ResponseEntity(response.toMap(), HttpStatus.BAD_REQUEST);
		}
	}

}
