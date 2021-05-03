package com.ucsf.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.UcsfStudy;
import com.ucsf.model.UcsfStudy.StudyFrequency;
import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserScreeningStatus.UserScreenStatus;
import com.ucsf.payload.request.StudyRequest;
import com.ucsf.payload.request.StudyReviewRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.StudyResponse;
import com.ucsf.payload.response.StudyReviewResponse;
import com.ucsf.repository.ScreeningAnswerRepository;
import com.ucsf.repository.ScreeningQuestionRepository;
import com.ucsf.repository.StudyRepository;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.StudyService;

@Service("studyService")
public class StudyServiceImpl implements StudyService {

	@Autowired
	StudyRepository studyRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UserMetaDataRepository userMetaDataRepository;

	@Autowired
	ScreeningQuestionRepository screeningQuestionRepository;

	@Autowired
	ScreeningAnswerRepository screeningAnswerRepository;

	@Override
	public void save(StudyRequest studyRequest) {
		UcsfStudy study = new UcsfStudy();
		study.setCustom_date(null);
		study.setEnabled(studyRequest.getEnabled());
		study.setDescription(studyRequest.getDescription());
		study.setTitle(studyRequest.getTitle());
		study.setFrequency(StudyFrequency.MONTHLY);
		study.setStartDate(new Date());
		study.setEndDate(DateUtils.addMonths(new Date(), 3));
		studyRepository.save(study);
	}

	@Override
	public List<StudyResponse> getStudies(Long userId) {
		List<Map<String, Object>> studies = jdbcTemplate.queryForList(
				"SELECT us.*,uss.user_screening_status FROM ucsf_studies us LEFT JOIN user_screening_status uss ON us.study_id = uss.study_id and  uss.user_id="
						+ userId + ";");
		List<StudyResponse> listStudyResponse = new ArrayList<StudyResponse>();
		for (Map<String, Object> map : studies) {
			StudyResponse studyResponse = new StudyResponse();
			if (map.get("user_screening_status") == null) {
				studyResponse.setStudyStatus(UserScreenStatus.AVAILABLE.toString());
			} else {
				studyResponse.setStudyStatus(map.get("user_screening_status").toString());
			}
			studyResponse.setDescription(map.get("description").toString());
			studyResponse.setTitle(map.get("title").toString());
			studyResponse.setDefault(
					Boolean.parseBoolean(map.get("is_default") != null ? map.get("is_default").toString() : ""));
			studyResponse.setEnabled(
					Boolean.parseBoolean(map.get("is_enabled") != null ? map.get("is_enabled").toString() : ""));
			listStudyResponse.add(studyResponse);
		}
		return listStudyResponse;
	}

	@Override
	public void updateStudyStatus(Long userId, String status) {
		UserMetadata metaData = userMetaDataRepository.findByUserId(userId);
		if (metaData != null) {
			metaData.setStudyStatus(status);
			userMetaDataRepository.save(metaData);
		}

	}

	@Override
	public StudyReviewResponse reviewStudy(StudyReviewRequest reviewStudy) {
		JSONObject responseJson = new JSONObject();
		StudyReviewResponse response = new StudyReviewResponse();
		if (reviewStudy != null) {
			if (reviewStudy.getType().equals("screening")) {

				List<?> questionsList = screeningQuestionRepository.findByStudyId(reviewStudy.getStudyId());
				List<?> answersList = screeningAnswerRepository.findByStudyIdAndAnsweredById(reviewStudy.getStudyId(),
						reviewStudy.getUserId());
				response.setQuestions(questionsList);
				response.setAnswers(answersList);
			} else {
				responseJson.put("error",
						new ErrorResponse(ErrorCodes.INVALID_STUDY.code(), Constants.INVALID_STUDY.errordesc()));
			}
		} else {
			// responseJson.put("error", )
		}
		responseJson.put("data", response);

		return response;
	}

	@Override
	public List<User> getApprovedPatients() {
		Optional<User> user = null;
		List<User> approvedUsers = new ArrayList<User>();
		List<UserMetadata> userMetaData = userMetaDataRepository.findByStudyStatus("approved");
		if (userMetaData != null && userMetaData.size() > 0) {
			for (UserMetadata metaData : userMetaData) {
				user = userRepository.findById(metaData.getUserId());
				if (user.isPresent()) {
					//User approvedUser = userRepository.findByEmail(user.get().getEmail());
					//approvedUsers.add(user.get());
					//approvedUsers.add(approvedUser);
				}
			}
		}
		return approvedUsers;
	}
}
