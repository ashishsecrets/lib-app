package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ScreeningAnswers;

public interface ScreeningAnswerRepository extends CrudRepository< ScreeningAnswers, Long> {
	ScreeningAnswers findByQuestionId(Long questionId);
	
}
