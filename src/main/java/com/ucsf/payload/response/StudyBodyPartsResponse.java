package com.ucsf.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class StudyBodyPartsResponse {

    private List<StudyImageUrlData> list;

}
