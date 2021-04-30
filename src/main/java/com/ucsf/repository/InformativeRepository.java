package com.ucsf.repository;

import com.ucsf.model.StudyInformative;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InformativeRepository extends CrudRepository<StudyInformative, Long> {

    StudyInformative findByIndexValueAndStudyId(int indexValue, Long studyId);

    //Informative Repo

}