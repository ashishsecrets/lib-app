package com.ucsf.repository;
import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.Question;

public interface QuestionRepository extends CrudRepository<Question, Long> {
	//List<Question> findAll();
}