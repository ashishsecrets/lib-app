package com.ucsf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.UserMetadata;

public interface UserMetaDataRepository extends CrudRepository<UserMetadata, Long> {
	List<UserMetadata> findByIsStudyAccepted(Boolean isAccepted);
	UserMetadata findByUserId(Long userId);
}
