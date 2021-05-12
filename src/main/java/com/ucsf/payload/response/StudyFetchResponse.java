package com.ucsf.payload.response;

import com.ucsf.model.UcsfStudy;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StudyFetchResponse {

    private StudyResponse study;

    private int userImageCount;

    /*listResponse.get(i).setId(listStudyResponse.get(i).getId());
				listResponse.get(i).setTitle(listStudyResponse.get(i).getTitle());
				listResponse.get(i).setDescription(listStudyResponse.get(i).getDescription());
				listResponse.get(i).setEnabled(listStudyResponse.get(i).getEnabled());
				listResponse.get(i).setDefault(listStudyResponse.get(i).isDefault());
				listResponse.get(i).setStartDate(listStudyResponse.get(i).getStartDate());
				listResponse.get(i).setEndDate(listStudyResponse.get(i).getEndDate());
				listResponse.get(i).setFrequency(listStudyResponse.get(i).getFrequency());
				listResponse.get(i).setCustomDate(listStudyResponse.get(i).getCustomDate());
				listResponse.get(i).setStudyStatus(listStudyResponse.get(i).getStudyStatus());
				listResponse.get(i).setUserImageCount(studyService.getImageCount(listStudyResponse.get(i).getId(), user.getId()));*/
}
