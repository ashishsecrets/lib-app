package com.ucsf.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class OverDuePatientTasksListResponse {

	List<OverdueTaskResponse> patientOverDueList;
	String patient;
	Long userId;
}
