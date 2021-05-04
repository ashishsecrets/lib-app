package com.ucsf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ConsentForms;
import com.ucsf.model.UserConsent;

public interface ConsentFormRepository extends CrudRepository<ConsentForms, Long> {
	List<ConsentForms> findAll();
}
