package com.ucsf.repository;

import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.SurveyAnswer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SurveyAnswerRepository extends CrudRepository<SurveyAnswer, Long> {
	SurveyAnswer findByQuestionId(Long questionId);
	SurveyAnswer deleteByQuestionId(Long id);
	List<SurveyAnswer> findByStudyIdAndAnsweredById(Long userId, Long studyId);
	SurveyAnswer findByQuestionIdAndAnsweredById(Long quesId, Long userId);
	SurveyAnswer findByIndexValueAndAnsweredById(int indexValue, Long userId);
}
