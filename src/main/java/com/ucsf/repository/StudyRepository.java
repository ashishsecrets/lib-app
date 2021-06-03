package com.ucsf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.UcsfStudy;

public interface StudyRepository extends CrudRepository<UcsfStudy, Long> {
	//Iterable<UcsfStudy> findAll();
	List<UcsfStudy> findAll();
	Optional<UcsfStudy> findById(Long studyId);
}
