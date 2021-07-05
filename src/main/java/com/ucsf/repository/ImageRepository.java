package com.ucsf.repository;

import com.ucsf.model.StudyImages;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageRepository extends CrudRepository<StudyImages, Long> {

    List<StudyImages> findByStudyIdAndUserId(Long studyId, Long userId);

    List<StudyImages> findByUserIdAndImageType(Long userId, StudyImages.StudyImageType imageType);

}
