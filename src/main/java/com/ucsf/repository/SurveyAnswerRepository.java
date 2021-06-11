package com.ucsf.repository;

import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.SurveyAnswer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SurveyAnswerRepository extends CrudRepository<SurveyAnswer, Long> {
	SurveyAnswer findByQuestionId(Long questionId);
	SurveyAnswer deleteByQuestionId(Long id);
	List<SurveyAnswer> findBySurveyIdAndAnsweredById(Long surveyId, Long userId);
	SurveyAnswer findByQuestionIdAndAnsweredById(Long quesId, Long userId);
	SurveyAnswer findByIndexValueAndAnsweredById(int indexValue, Long userId);

    SurveyAnswer findByQuestionIdAndAnsweredByIdAndTaskTrueId(Long quesId, Long userId, Long surveyTrueId);

	List<SurveyAnswer> findByTaskTrueIdAndAnsweredById(Long taskTrueId, Long userId);

	//SurveyAnswer findTopByOrderByTaskTrueIdAndAnsweredByIdDesc(Long surveyTrueId, Long userId);
}
