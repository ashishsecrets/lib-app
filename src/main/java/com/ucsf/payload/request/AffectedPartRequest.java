package com.ucsf.payload.request;

import com.ucsf.model.StudyImages;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AffectedPartRequest {
    private StudyImages.StudyImageType imageType;
    private String partImage;
    private String description;
}

