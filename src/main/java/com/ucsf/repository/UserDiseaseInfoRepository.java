package com.ucsf.repository;
import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.UserDiseaseInfo;

public interface UserDiseaseInfoRepository extends CrudRepository<UserDiseaseInfo, Long> {
	
}