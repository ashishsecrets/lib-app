package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;
import com.ucsf.model.UserConsent;

public interface UserConsentRepository extends CrudRepository<UserConsent, Long> {
	
}
