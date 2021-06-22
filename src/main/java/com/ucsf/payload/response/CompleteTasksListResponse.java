package com.ucsf.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class CompleteTasksListResponse {

	List<TaskResponse> tasks;
	List<TaskResponse> overdueTasks;
	List<TaskResponse> currentTasks;
	List<TaskResponse> upcomingTasks;
	int completedProgress;
	int missingProgress;
	int upcomingProgress;

}
