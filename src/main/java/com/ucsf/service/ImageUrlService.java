package com.ucsf.service;

import com.ucsf.model.StudyImages;
import com.ucsf.payload.response.StudyBodyPartsResponse;

import java.util.List;

public interface ImageUrlService {

    List<StudyImages> getImageUrls(Long studyId, Long userId);

}
