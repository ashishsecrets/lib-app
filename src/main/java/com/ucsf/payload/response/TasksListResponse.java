package com.ucsf.payload.response;

import com.ucsf.model.Notifications;
import lombok.Data;

import java.util.List;

@Data
public class TasksListResponse {

	List<TaskResponse> list;
	int totalProgress;
	int currentWeek;
}
