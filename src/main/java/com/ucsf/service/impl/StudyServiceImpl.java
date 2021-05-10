package com.ucsf.service.impl;

import java.util.*;

import com.ucsf.model.*;
import com.ucsf.payload.response.StudyReviewData;
import com.ucsf.repository.*;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.UcsfStudy.StudyFrequency;
import com.ucsf.model.UserMetadata.StudyStatus;
import com.ucsf.model.UserScreeningStatus.UserScreenStatus;
import com.ucsf.payload.request.StudyRequest;
import com.ucsf.payload.request.StudyReviewRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.StudyResponse;
import com.ucsf.payload.response.StudyReviewResponse;
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

	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;

	@Autowired
	ImageRepository imageRepository;

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
			studyResponse.setId((Long) map.get("study_id"));
			studyResponse.setDefault(
					Boolean.parseBoolean(map.get("is_default") != null ? map.get("is_default").toString() : ""));
			studyResponse.setEnabled(
					Boolean.parseBoolean(map.get("is_enabled") != null ? map.get("is_enabled").toString() : ""));
			listStudyResponse.add(studyResponse);
		}
		return listStudyResponse;
	}

	@Override
	public int getImageCount(Long studyId, Long userId) {
		int totalCount = 0;
		List<StudyImages> list = null;

		if(studyId != null && userId != null){

			list = imageRepository.findByStudyIdAndUserId(studyId, userId);

		}

		for(StudyImages item: list){
			totalCount += item.getCount();
		}

		return totalCount;
	}

	@Override
	public void updateStudyStatus(Long userId, String status) {
		UserMetadata metaData = userMetaDataRepository.findByUserId(userId);
		if (metaData != null) {
			if(status != null && status.equals("approved")) {
				metaData.setStudyStatus(StudyStatus.APPROVED);
				userMetaDataRepository.save(metaData);
			}
			if(status != null && status.equals("disapproved")) {
				metaData.setStudyStatus(StudyStatus.DISAPPROVED);
				userMetaDataRepository.save(metaData);
			}
		}

	}

	@Override
	public StudyReviewResponse reviewStudy(StudyReviewRequest reviewStudy) {
		JSONObject responseJson = new JSONObject();
		StudyReviewResponse response = new StudyReviewResponse();
		if (reviewStudy != null) {
			if (reviewStudy.getType().equals("screening")) {

				List<ScreeningQuestions> questionsList = screeningQuestionRepository.findByStudyId(reviewStudy.getStudyId());

				String answer;

				List<StudyReviewData> newList = new ArrayList<>();
				for(ScreeningQuestions question : questionsList ){
					if(screeningAnswerRepository.findByIndexValueAndAnsweredById(question.getIndexValue(), reviewStudy.getUserId()) == null){
						answer = "User did not enter answer";
					}
					else{
						answer = screeningAnswerRepository.findByIndexValueAndAnsweredById(question.getIndexValue(), reviewStudy.getUserId()).getAnswerDescription();
					}
					newList.add(new StudyReviewData(question.getDescription(), answer));
				}
				response.setList(newList);
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
		//Need to get enrolled patients bcoz status updated after approval notification
		List<UserMetadata> userMetaData = userMetaDataRepository.findByStudyStatus(StudyStatus.APPROVED);
		if (userMetaData != null && userMetaData.size() > 0) {
			for (UserMetadata metaData : userMetaData) {
				user = userRepository.findById(metaData.getUserId());
				if (user.isPresent()) {
					User approvedUser = userRepository.findByEmail(user.get().getEmail());
					approvedUsers.add(user.get());
					approvedUsers.add(approvedUser);
				}
			}
		}
		return approvedUsers;
	}
}
