package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;
import com.ucsf.model.UcsfSurvey;

public interface SurveyRepository extends CrudRepository<UcsfSurvey, Long>{
}
