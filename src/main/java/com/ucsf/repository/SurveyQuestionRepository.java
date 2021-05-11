package com.ucsf.repository;


import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.SurveyQuestion;

public interface SurveyQuestionRepository extends CrudRepository<SurveyQuestion, Long>{
    SurveyQuestion findBySurveyIdAndIndexValue(Long surveyId, int index);

}