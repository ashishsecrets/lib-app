package com.ucsf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ScreeningAnsChoice;

public interface ChoiceRepository extends CrudRepository<ScreeningAnsChoice, Long> {
	
	List<ScreeningAnsChoice> findByQuestionId(Long questionId);
}
