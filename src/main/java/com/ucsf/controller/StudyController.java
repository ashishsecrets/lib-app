package com.ucsf.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.UcsfStudy;
import com.ucsf.model.UcsfStudy.StudyFrequency;
import com.ucsf.payload.request.StudyRequest;
import com.ucsf.payload.response.ApiError;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.ScreeningAnswerRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.StudyRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.UserScreeningStatusRepository;
import com.ucsf.service.LoggerService;

@RestController
@CrossOrigin
@RequestMapping("/api/study")
public class StudyController {

	@Autowired
	ScreeningAnswerRepository screeningAnswerRepository;

	@Autowired
	ScreeningQuestionRepository screeningQuestionRepository;

	@Autowired
	private LoggerService loggerService;

	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	StudyRepository studyRepository;

	private static Logger log = LoggerFactory.getLogger(StudyController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<?> saveStudy(@RequestBody StudyRequest studyRequest) throws Exception {
		loggerService.printLogs(log, "saveStudy", "Saving UCSF Study");
		User user = null;
		JSONObject responseJson = new JSONObject();

		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			String email = userDetail.getUsername();
			user = userRepository.findByEmail(email);
		} else {
			loggerService.printLogs(log, "saveScreeningAnswers", "Invalid JWT signature.");
			responseJson.put("error", new ApiError(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}

		UcsfStudy study = new UcsfStudy();
		study.setCustom_date(null);
		study.setEnabled(studyRequest.getEnabled());
		study.setDescription(studyRequest.getDescription());
		study.setTitle(studyRequest.getTitle());
		study.setFrequency(StudyFrequency.MONTHLY);
		study.setStartDate(new Date());
		study.setEndDate(DateUtils.addMonths(new Date(), 3));
		studyRepository.save(study);
		return ResponseEntity.ok(new SuccessResponse(true, "Study saved successfully!"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/fetch", method = RequestMethod.GET)
	public ResponseEntity<?> fetchAllStudies() throws Exception {
		loggerService.printLogs(log, "saveStudy", "Saving UCSF Study");
		User user = null;
		JSONObject responseJson = new JSONObject();

		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			String email = userDetail.getUsername();
			user = userRepository.findByEmail(email);
		} else {
			loggerService.printLogs(log, "saveScreeningAnswers", "Invalid JWT signature.");
			responseJson.put("error", new ApiError(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}

		Iterable<UcsfStudy> study = studyRepository.findAll();
		List<UcsfStudy> studies = new ArrayList<UcsfStudy>();
		study.forEach(studies::add);
		return new ResponseEntity<List<UcsfStudy>>(studies, HttpStatus.OK);
	}

}