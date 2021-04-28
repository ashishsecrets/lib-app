package com.ucsf.repository;
import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.ScreeningQuestions;

import java.util.List;

public interface ScreeningQuestionRepository extends CrudRepository<ScreeningQuestions, Long> {
	ScreeningQuestions findByStudyIdAndIndexValue(Long studyId, int index);
	List<ScreeningQuestions> findByStudyId(Long studyId);
	/*
	 * @Query("select q from questions q where q.study_id = '?1' order by q.indexValue desc"
	 * ) ScreeningQuestions findByStudyIdOrderByIndexValueDesc(Long studyId);
	 */
}
