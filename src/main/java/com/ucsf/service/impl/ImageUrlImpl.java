package com.ucsf.service.impl;

import com.ucsf.model.StudyImages;
import com.ucsf.payload.response.StudyBodyPartsResponse;
import com.ucsf.repository.ImageRepository;
import com.ucsf.service.ImageUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ImageUrlService")
public class ImageUrlImpl implements ImageUrlService {

    @Autowired
    ImageRepository imageRepository;

    @Override
    public List<StudyImages> getImageUrls(Long studyId, Long userId) {

        List<StudyImages> list = null;

        if(studyId != null && userId != null){

            list = imageRepository.findByStudyIdAndUserId(studyId, userId);

        }

        return list;

    }


}
