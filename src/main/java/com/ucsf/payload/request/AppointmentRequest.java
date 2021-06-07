package com.ucsf.payload.request;

import java.util.Date;

import lombok.Data;

@Data
public class AppointmentRequest {

	private String email;
	
	private String title;
	
	private String description;
	
	private Date startDate;
	
	private Date endDate;
	
}
