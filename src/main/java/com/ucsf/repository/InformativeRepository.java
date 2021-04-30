package com.ucsf.repository;

import com.ucsf.model.StudyInformative;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InformativeRepository extends CrudRepository<StudyInformative, Long> {

    StudyInformative findByIndexValueAndStudyId(int indexValue, Long studyId);
    /*ScreeningAnswers findByQuestionId(Long questionId);
    ScreeningAnswers deleteByQuestionId(Long id);
    List<ScreeningAnswers> findByStudyIdAndAnsweredById(Long userId, Long studyId);
    ScreeningAnswers findByQuestionIdAndAnsweredById(Long quesId, Long userId);*/

}