package com.ucsf.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.UcsfStudy;
import com.ucsf.model.UcsfStudy.StudyFrequency;
import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserScreeningStatus.UserScreenStatus;
import com.ucsf.payload.request.StudyRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.StudyResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.ScreeningAnswerRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.StudyRepository;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.UserScreeningStatusRepository;
import com.ucsf.service.LoggerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin
@RequestMapping("/api/study")
@Api(tags = "Study Controller")
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
	
	@Autowired
	UserMetaDataRepository  userMetaDataRepository;
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	private static Logger log = LoggerFactory.getLogger(StudyController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Save study", notes = "Save study", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Study saved successfully", response = UcsfStudy.class) })
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
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
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

		responseJson.put("data", new SuccessResponse(true, "Study Saved"));
		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Get all studies", notes = "Get all studies", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all studies", response = StudyResponse.class) })
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
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		/*
		 * EntityManager entityManager = BeanUtil.getBean(EntityManager.class); Query
		 * query = entityManager.createQuery(
		 * "SELECT us,uss.userScreeningStatus,uss.indexValue FROM UcsfStudy us LEFT JOIN UserScreeningStatus uss ON us.id = uss.studyId and uss.userId ="
		 * +user.getId());
		 */
		//Iterable<UcsfStudy> study = studyRepository.findAll();
		List<Map<String,Object>> studies = jdbcTemplate.queryForList("SELECT us.*,uss.user_screening_status FROM ucsf_studies us LEFT JOIN user_screening_status uss ON us.study_id = uss.study_id and  uss.user_id="+user.getId()+";");
		System.out.println(studies);
		List<StudyResponse> listStudyResponse = new ArrayList<StudyResponse>();
		for(Map<String,Object> map :studies) {
			StudyResponse studyResponse = new StudyResponse();
			if(map.get("user_screening_status") == null) {
				studyResponse.setStudyStatus(UserScreenStatus.AVAILABLE.toString());
			}
			else {
				studyResponse.setStudyStatus(map.get("user_screening_status").toString());
			}
			studyResponse.setDescription(map.get("description").toString());
			studyResponse.setTitle(map.get("title").toString());
			studyResponse.setDefault(Boolean.parseBoolean(map.get("is_default") != null ?map.get("is_default").toString():""));
			studyResponse.setEnabled(Boolean.parseBoolean(map.get("is_enabled") != null ?map.get("is_enabled").toString():""));
			listStudyResponse.add(studyResponse);
		}
		responseJson.put("data", listStudyResponse);
		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Approve study", notes = "Approve study", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Study approved successfully", response = SuccessResponse.class) })
	@RequestMapping(value = "/approveStudy", method = RequestMethod.POST)
	public ResponseEntity<?> approveStudy(@PathVariable Long userId,@RequestParam String status) throws Exception {
		loggerService.printLogs(log, "approveStudy", "approve UCSF Study");
		User user = null;
		JSONObject responseJson = new JSONObject();

		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			String email = userDetail.getUsername();
			user = userRepository.findByEmail(email);
		} else {
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
		}
		
		 UserMetadata metaData = userMetaDataRepository.findByUserId(userId);
         if(metaData != null) {
        	 metaData.setStudyStatus(status);
        	 userMetaDataRepository.save(metaData);
        	 loggerService.printLogs(log, "approveStudy", "Updated Study approval status for user with id "+userId);
         }
		responseJson.put("data", new SuccessResponse(true, "Study approved"));
		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}

}
