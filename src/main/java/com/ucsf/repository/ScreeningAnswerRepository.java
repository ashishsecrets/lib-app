package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ScreeningAnswers;

import java.util.List;

public interface ScreeningAnswerRepository extends CrudRepository< ScreeningAnswers, Long> {
	ScreeningAnswers findByQuestionId(Long questionId);
	ScreeningAnswers deleteByQuestionId(Long id);
	List<ScreeningAnswers> findByStudyIdAndAnsweredById(Long userId, Long studyId);
	ScreeningAnswers findByQuestionIdAndAnsweredById(Long quesId, Long userId);
	ScreeningAnswers findByIndexValueAndAnsweredById(int indexValue, Long userId);
    ScreeningAnswers findByQuestionIdAndAnsweredByIdAndStudyId(Long id, Long id1, Long studyId);
}
