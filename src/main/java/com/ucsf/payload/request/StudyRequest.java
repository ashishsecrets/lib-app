package com.ucsf.payload.request;

import java.util.Date;
import com.ucsf.model.UcsfStudy.StudyFrequency;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudyRequest {
	
	private String title;
    private String description;
    private Boolean enabled;
    private Date startDate;
    private Date endDate;
    private StudyFrequency frequency;
	private Date custom_date;
}
