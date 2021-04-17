package com.ucsf.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.SurveyAnswerChoice;

public interface SurveyChoiceRepository extends CrudRepository<SurveyAnswerChoice, Long> {
	
	List<SurveyAnswerChoice> findByQuestionId(Long questionId);
}

