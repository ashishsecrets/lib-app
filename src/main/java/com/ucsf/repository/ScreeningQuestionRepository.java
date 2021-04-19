package com.ucsf.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ScreeningQuestions;

public interface ScreeningQuestionRepository extends CrudRepository<ScreeningQuestions, Long> {
	ScreeningQuestions findByStudyIdAndIndexValue(Long studyId, int index);

  // @Query("SELECT q FROM Questions q where q.studyId = ?1 ORDER BY indexValue Desc LIMIT 1")
   ScreeningQuestions findByStudyIdOrderByIndexValueDesc(Long studyId);
}
