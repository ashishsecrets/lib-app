package com.ucsf.payload.response;

import java.util.Date;

import lombok.Data;


public class AppointmentResponse {
	private Long Id;
	private Date StartTime;
	private Date EndTime;
	private String Title;
	private String Description;
	private String Patient;
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public Date getStartTime() {
		return StartTime;
	}
	public void setStartTime(Date startTime) {
		StartTime = startTime;
	}
	public Date getEndTime() {
		return EndTime;
	}
	public void setEndTime(Date endTime) {
		EndTime = endTime;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getPatient() {
		return Patient;
	}
	public void setPatient(String patient) {
		Patient = patient;
	}
	
}
