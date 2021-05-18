package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.model.SurveyQuestion;
import com.ucsf.model.UserSurveyStatus;
import com.ucsf.model.UserTasks;
import com.ucsf.payload.response.TaskResponse;
import com.ucsf.repository.SurveyQuestionRepository;
import com.ucsf.repository.TasksRepository;
import com.ucsf.repository.UserSurveyStatusRepository;
import com.ucsf.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("TaskService")
public class TaskServiceImpl implements TaskService {

    @Autowired
    TasksRepository tasksRepository;

    @Autowired
    UserSurveyStatusRepository userSurveyStatusRepository;

    @Autowired
    SurveyQuestionRepository surveyQuestionRepository;


    @Override
    public void updateSurveyStatuses(User user) {

        List<UserTasks> surveyTasklist = null;

        if(tasksRepository.findByUserIdAndTaskType(user.getId(), "survey") != null)
        surveyTasklist = tasksRepository.findByUserIdAndTaskType(user.getId(), "survey");


        assert surveyTasklist != null;
        for(UserTasks task: surveyTasklist){

            if(userSurveyStatusRepository.findByUserIdAndSurveyId(user.getId(), task.getTaskId()) == null){

                UserSurveyStatus userSurveyStatus = new UserSurveyStatus();
                userSurveyStatus.setSurveyId(task.getTaskId());
                userSurveyStatus.setUserSurveyStatus(UserSurveyStatus.SurveyStatus.NEWLY_ADDED);
                userSurveyStatus.setUserId(user.getId());
                userSurveyStatus.setIndexValue(1);
                userSurveyStatusRepository.save(userSurveyStatus);

            }

        }

    }

    @Override
    public List<UserTasks> getTaskList(User user) {

        List<UserTasks> tasksList = null;

        if(tasksRepository.findByUserId(user.getId())!= null)
        tasksList = tasksRepository.findByUserId(user.getId());

        return tasksList;
    }

    @Override
    public int getTotalProgress(List<TaskResponse> alteredTaskList) {
        int totalProgress = 0;
        // This can be done from repository instead as UserTasks table also has stored progress values
        for(TaskResponse item : alteredTaskList){
            totalProgress += item.getTaskPercentage();
        }

        return totalProgress;
    }

    @Override
    public List<TaskResponse> getAlteredTaskList(List<UserTasks> tasks) {

        List<TaskResponse> taskResponseList = null;

        for(UserTasks task : tasks){
            TaskResponse taskResponse = new TaskResponse();
            taskResponse.setTaskId(task.getTaskId());
            taskResponse.setTaskName(task.getTitle());
            taskResponse.setStartDate(task.getStartDate());
            taskResponse.setDueDate(task.getEndDate());
            taskResponse.setTaskStatus(getTaskStatus(task.getStartDate(), task.getEndDate()));
            taskResponse.setTaskPercentage(getTaskProgress(task.getTaskId(), task.getUserId(), task.getTaskType()));
            taskResponseList.add(taskResponse);

        }


        return taskResponseList;
    }

    @Override
    public int getTaskProgress(Long taskId, Long userId, String taskType) {

        int percentage = 0;
        UserSurveyStatus surveyStatus = null;
        List<SurveyQuestion> surveyQuestionList = null;

        if(userSurveyStatusRepository.findByUserIdAndSurveyId(userId, taskId) != null){
            surveyStatus = userSurveyStatusRepository.findByUserIdAndSurveyId(userId, taskId);
            surveyQuestionList = surveyQuestionRepository.findBySurveyId(taskId);

       percentage = surveyStatus.getIndexValue()/surveyQuestionList.size()*100;

        Optional<UserTasks> taskOp = tasksRepository.findById(tasksRepository.findByUserIdAndTaskId(userId, taskId).getId());
        UserTasks task = taskOp.get();
        task.setProgress(percentage);
        tasksRepository.save(task);
        }
        else{
            percentage = tasksRepository.findByUserIdAndTaskType(userId, taskType).get(0).getProgress();
        }

       return percentage;

    }

    @Override
    public String getTaskStatus(Date startDate, Date endDate) {

        String status = "";

        Date todaysDate = new Date();

    if (todaysDate.after(startDate) && todaysDate.before(endDate)){

        status = "withinDateRange";

    }
    else if(todaysDate.after(endDate)){
        status = "overdue";
    }
    else if(todaysDate.before(startDate)){
        status = "advanced";
    }

        return status;
    }
}
