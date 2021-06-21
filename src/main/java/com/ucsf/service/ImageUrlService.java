package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.model.StudyImages;
import com.ucsf.payload.request.BodyPartRequest;
import com.ucsf.payload.response.StudyBodyPartsResponse;

import java.io.File;
import java.util.List;

public interface ImageUrlService {

    List<StudyImages> getImageUrls(Long studyId, Long userId);
    String saveFile(File file, String string);
    void saveImage(User user, BodyPartRequest request);

}