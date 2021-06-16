package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.UserScreeningStatus;

import java.util.List;

public interface UserScreeningStatusRepository extends CrudRepository<UserScreeningStatus, Long>{
	UserScreeningStatus findByUserId(Long userId);
	List<UserScreeningStatus> findByStudyId(Long studyId);

    UserScreeningStatus findByUserIdAndStudyId(Long userId, Long studyId);
}
