package com.ucsf.service;

import java.util.List;

import org.json.JSONObject;

import com.ucsf.auth.model.User;
import com.ucsf.payload.request.StudyRequest;
import com.ucsf.payload.request.StudyReviewRequest;
import com.ucsf.payload.response.StudyResponse;

public interface StudyService {
  
	public void save(StudyRequest study);
	public List<StudyResponse> getStudies(Long userId);
	int getImageCount(Long studyId, Long userId);
    
	void updateStudyStatus(Long userId, String status);
	JSONObject reviewStudy(StudyReviewRequest reviewStudy);
	List<User> getApprovedPatients();
    List<User> getDisapprovedPatients();
}
