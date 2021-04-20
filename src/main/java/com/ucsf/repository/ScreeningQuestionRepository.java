package com.ucsf.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ScreeningQuestions;

public interface ScreeningQuestionRepository extends CrudRepository<ScreeningQuestions, Long> {
	ScreeningQuestions findByStudyIdAndIndexValue(Long studyId, int index);

	ScreeningQuestions findFirstByStudyIdOrderByIndexValueDesc(Long id);

}
