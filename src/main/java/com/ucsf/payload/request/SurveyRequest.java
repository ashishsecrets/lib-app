package com.ucsf.payload.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequest {
	
	private String title;
	private String description;
	private Long studyId;
	private Boolean enabled;

}