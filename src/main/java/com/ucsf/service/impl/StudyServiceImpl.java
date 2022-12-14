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
import com.ucsf.service.StudyNotificationService;
import com.ucsf.service.StudyService;

@Service("studyService")
public class StudyServiceImpl implements StudyService {

	@Autowired
	StudyRepository studyRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	StudyNotificationService studyNotificationService;

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

		if (studyId != null && userId != null) {

			list = imageRepository.findByStudyIdAndUserId(studyId, userId);

		}

		for (StudyImages item : list) {
			totalCount += item.getCount();
		}

		return totalCount;
	}

	@Override
	public void updateStudyStatus(Long userId, String status) {
		UserScreeningStatus userStatus = userScreeningStatusRepository.findByUserId(userId);
		if (userStatus != null) {
			if (status != null && status.equals("approved")) {
				userStatus.setUserScreeningStatus(UserScreenStatus.APPROVED);
				userStatus.setStatusUpdatedDate(new Date());
				userScreeningStatusRepository.save(userStatus);
				studyNotificationService.sendApproveNotifications(userId);
			}
			if (status != null && status.equals("disapproved")) {
				userStatus.setUserScreeningStatus(UserScreenStatus.DISAPPROVED);
				userStatus.setStatusUpdatedDate(new Date());
				userScreeningStatusRepository.save(userStatus);
				studyNotificationService.sendDisapproveNotifications(userId);
			}
		}
	}

	@Override
	public JSONObject reviewStudy(StudyReviewRequest reviewStudy) {
		JSONObject responseJson = new JSONObject();
		List<StudyReviewData> newList = new ArrayList<>();
		if (reviewStudy != null) {
			if (reviewStudy.getType().equals("screening")) {

				List<ScreeningQuestions> questionsList = screeningQuestionRepository
						.findByStudyId(reviewStudy.getStudyId());
				
				if(questionsList.size() < 1) {
					responseJson.put("error",
							new ErrorResponse(ErrorCodes.INVALID_STUDY.code(), Constants.INVALID_STUDY.errordesc()));
				}
				
				for (ScreeningQuestions question : questionsList) {
					ScreeningAnswers screeningAnswer = screeningAnswerRepository.findByIndexValueAndAnsweredById(question.getIndexValue(), reviewStudy.getUserId());
					if (screeningAnswer != null) {
						String answer = screeningAnswer.getAnswerDescription();
						newList.add(new StudyReviewData(question.getDescription(), answer, null));
					} 					
				}
				
				List<StudyImages> imageUrlsList = new ArrayList<StudyImages>();
				if(newList.size() > 0) {
					List<StudyImages> images= imageRepository.findByStudyIdAndUserId(reviewStudy.getStudyId(), reviewStudy.getUserId());
					for(StudyImages image: images){
						if(image.getCount() > 0) {
							imageUrlsList.add(image);
						}
					}
					if(imageUrlsList.size() > 0)
						newList.add(new StudyReviewData("Photos", null, imageUrlsList));
				}	
			} else {
				responseJson.put("error",
						new ErrorResponse(ErrorCodes.INVALID_STUDY.code(), Constants.INVALID_STUDY.errordesc()));
			}
		} 
		responseJson.put("data", newList);

		return responseJson;
	}

	@Override
	public List<User> getApprovedPatients() {
		Optional<User> user = null;
		List<User> approvedUsers = new ArrayList<User>();
		// Need to get enrolled patients bcoz status updated after approval notification
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

	@Override
	public List<User> getDisapprovedPatients() {
		Optional<User> user = null;
		List<User> disapprovedUsers = new ArrayList<User>();
		// Need to get enrolled patients bcoz status updated after approval notification
		List<UserMetadata> userMetaData = userMetaDataRepository.findByStudyStatus(StudyStatus.DISAPPROVED);
		if (userMetaData != null && userMetaData.size() > 0) {
			for (UserMetadata metaData : userMetaData) {
				user = userRepository.findById(metaData.getUserId());
				if (user.isPresent()) {
					User approvedUser = userRepository.findByEmail(user.get().getEmail());
					disapprovedUsers.add(user.get());
					disapprovedUsers.add(approvedUser);
				}
			}
		}
		return disapprovedUsers;
	}

	@Override
	public UcsfStudy findById(Long studyId) {
		UcsfStudy study = studyRepository.findById(studyId).get();
		return study;
	}
}
