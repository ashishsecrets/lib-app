package com.ucsf.repository;


import com.ucsf.model.ScreeningQuestions;
import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.SurveyQuestion;
import com.ucsf.model.UcsfSurvey;

public interface SurveyQuestionRepository extends CrudRepository<SurveyQuestion, Long>{
    SurveyQuestion findByStudyIdAndIndexValue(Long studyId, int index);

}