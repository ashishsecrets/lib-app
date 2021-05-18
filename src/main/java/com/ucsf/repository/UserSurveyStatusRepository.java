package com.ucsf.repository;

import com.ucsf.model.UserScreeningStatus;
import com.ucsf.model.UserSurveyStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserSurveyStatusRepository extends CrudRepository<UserSurveyStatus, Long>{
	UserSurveyStatus findByUserId(Long userId);
	List<UserSurveyStatus> findBySurveyId(Long studyId);

    UserSurveyStatus findByUserIdAndSurveyId(Long userId, Long taskId);
}

