package com.ucsf.repository;
import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ScreeningQuestions;

public interface QuestionRepository extends CrudRepository<ScreeningQuestions, Long> {
	//List<Question> findAll();
}
