package com.ucsf.payload.response;

import java.util.Date;

import javax.persistence.Column;

import com.ucsf.model.UcsfStudy.StudyFrequency;

import lombok.Data;
@Data
public class StudyResponse {

	private Long id;

	private String title;

	private String description;

	private Boolean enabled;
	
	private boolean isDefault;

	private Date startDate;

	private Date endDate;
	
	private StudyFrequency frequency;
	
	private Date customDate;

	private String studyStatus;
}
