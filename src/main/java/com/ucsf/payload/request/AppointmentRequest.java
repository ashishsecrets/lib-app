package com.ucsf.payload.request;

import java.util.Date;

import lombok.Data;

@Data
public class AppointmentRequest {

	private Long physicianId;
	
	private Long patientId;
	
	private String appointmentTitle;
	
	private String appointmentDesc;
	
	private Date appointmentDate;
}
