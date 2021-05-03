package com.ucsf.service;

import java.util.List;

import com.ucsf.auth.model.User;
import com.ucsf.payload.request.StudyRequest;
import com.ucsf.payload.request.StudyReviewRequest;
import com.ucsf.payload.response.StudyResponse;
import com.ucsf.payload.response.StudyReviewResponse;

public interface StudyService {
  
	public void save(StudyRequest study);
	public List<StudyResponse> getStudies(Long userId);
	void updateStudyStatus(Long userId,String status);
	StudyReviewResponse reviewStudy(StudyReviewRequest reviewStudy);
	List<User> getApprovedPatients();

}
