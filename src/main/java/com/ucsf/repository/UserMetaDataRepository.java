package com.ucsf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.UserMetadata;

public interface UserMetaDataRepository extends CrudRepository<UserMetadata, Long> {
	List<UserMetadata> findByStudyStatus(String status);
	UserMetadata findByUserId(Long userId);
}
