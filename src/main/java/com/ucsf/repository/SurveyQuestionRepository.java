package com.ucsf.repository;


import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.SurveyQuestion;

import java.util.List;

public interface SurveyQuestionRepository extends CrudRepository<SurveyQuestion, Long>{
    SurveyQuestion findBySurveyIdAndIndexValue(Long surveyId, int index);

    List<SurveyQuestion> findBySurveyId(Long taskId);
}