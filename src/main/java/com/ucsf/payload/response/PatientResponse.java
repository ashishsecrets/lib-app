package com.ucsf.payload.response;

import lombok.Data;

@Data
public class PatientResponse {

	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String updatedAt;
	private String updatedBy;
	private Integer studyWeek;

}
