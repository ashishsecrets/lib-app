package com.ucsf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.Choices;

public interface ChoiceRepository extends CrudRepository<Choices, Long> {
	
	List<Choices> findByQuestionId(Long questionId);
}
