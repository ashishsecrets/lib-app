package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;
import com.ucsf.model.UserConsent;

public interface ConsentFormRepository extends CrudRepository<UserConsent, Long> {
	
}
