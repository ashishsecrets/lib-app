package com.ucsf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserMetadata.StudyStatus;

public interface UserMetaDataRepository extends CrudRepository<UserMetadata, Long> {
	List<UserMetadata> findByStudyStatus(StudyStatus status);
	UserMetadata findByUserId(Long userId);
	UserMetadata findByStudyStatusAndUserId(StudyStatus status,Long userId);
}
