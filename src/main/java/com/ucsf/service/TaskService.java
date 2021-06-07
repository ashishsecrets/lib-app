package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.model.UserTasks;
import com.ucsf.payload.response.SurveyResponse;
import com.ucsf.payload.response.TaskResponse;

import java.util.Date;
import java.util.List;

public interface TaskService {


    void updateSurveyStatuses(User user);

    List<UserTasks> getTaskList(User user);

    int getTotalProgress(List<TaskResponse> alteredTaskList);

    List<TaskResponse> getAlteredTaskList(List<UserTasks> tasks);

    List<SurveyResponse> getAlteredTaskListSurvey(List<UserTasks> tasks);

    int getTaskProgress(Long taskId, Long userId, String taskType, Long taskTrueId);

    String getTaskStatus(Date startDate, Date endDate, int taskProgress);
}
