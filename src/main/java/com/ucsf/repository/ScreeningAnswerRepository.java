package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ScreeningAnswers;

import java.util.List;

public interface ScreeningAnswerRepository extends CrudRepository< ScreeningAnswers, Long> {
	ScreeningAnswers findByQuestionId(Long questionId);
	ScreeningAnswers deleteByQuestionId(Long id);
	List<ScreeningAnswers> findByStudyId(Long studyId);
	
}
