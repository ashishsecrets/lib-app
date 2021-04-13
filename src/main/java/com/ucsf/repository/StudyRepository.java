package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.UcsfStudy;

public interface StudyRepository extends CrudRepository<UcsfStudy, Long> {
	Iterable<UcsfStudy> findAll();
}