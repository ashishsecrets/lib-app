package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.model.SurveyQuestion;
import com.ucsf.model.UserSurveyStatus;
import com.ucsf.model.UserTasks;
import com.ucsf.payload.response.TaskResponse;
import com.ucsf.repository.SurveyQuestionRepository;
import com.ucsf.repository.UserTasksRepository;
import com.ucsf.repository.UserSurveyStatusRepository;
import com.ucsf.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("TaskService")
public class TaskServiceImpl implements TaskService {

    @Autowired
    UserTasksRepository userTasksRepository;

    @Autowired
    UserSurveyStatusRepository userSurveyStatusRepository;

    @Autowired
    SurveyQuestionRepository surveyQuestionRepository;


    @Override
    public void updateSurveyStatuses(User user) {

        List<UserTasks> surveyTasklist = null;

        if(userTasksRepository.findByUserIdAndTaskType(user.getId(), "survey") != null)
        surveyTasklist = userTasksRepository.findByUserIdAndTaskType(user.getId(), "survey");


        assert surveyTasklist != null;
        for(UserTasks task: surveyTasklist){

            if(userSurveyStatusRepository.findByUserIdAndSurveyIdAndTaskTrueId(user.getId(), task.getTaskId(), task.getTaskTrueId()) == null){

                UserSurveyStatus userSurveyStatus = new UserSurveyStatus();
                userSurveyStatus.setSurveyId(task.getTaskId());
                userSurveyStatus.setUserSurveyStatus(UserSurveyStatus.SurveyStatus.NEWLY_ADDED);
                userSurveyStatus.setUserId(user.getId());
                userSurveyStatus.setTaskTrueId(task.getTaskTrueId());
                userSurveyStatus.setIndexValue(1);
                userSurveyStatusRepository.save(userSurveyStatus);

            }

        }

    }

    @Override
    public List<UserTasks> getTaskList(User user) {

        List<UserTasks> tasksList = null;

        if(userTasksRepository.findByUserId(user.getId())!= null)
        tasksList = userTasksRepository.findByUserId(user.getId());

        return tasksList;
    }

    @Override
    public int getTotalProgress(List<TaskResponse> alteredTaskList) {
        int totalProgress = 0;
        // This can be done from repository also as UserTasks table also has stored progress values
        for(TaskResponse item : alteredTaskList){
            totalProgress += item.getTaskPercentage();
        }
        totalProgress = totalProgress/alteredTaskList.size();

        return totalProgress;
    }

    @Override
    public List<TaskResponse> getAlteredTaskList(List<UserTasks> tasks) {

        List<TaskResponse> taskResponseList = new ArrayList<>();

        for(UserTasks task : tasks){
            TaskResponse taskResponse = new TaskResponse();
            taskResponse.setTaskId(task.getTaskTrueId());
            taskResponse.setTaskName(task.getTitle());
            taskResponse.setStartDate(task.getStartDate());
            taskResponse.setDueDate(task.getEndDate());
            int taskProgress = getTaskProgress(task.getTaskId(), task.getUserId(), task.getTaskType());
            taskResponse.setTaskPercentage(taskProgress);
            taskResponse.setTaskStatus(getTaskStatus(task.getStartDate(), task.getEndDate(), taskProgress));
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

        Optional<UserTasks> taskOp = userTasksRepository.findById(userTasksRepository.findByUserIdAndTaskId(userId, taskId).getTaskTrueId());
        UserTasks task = taskOp.get();
        task.setProgress(percentage);
        userTasksRepository.save(task);
        }
        else{
            // To be changed when we start saving other tasks
            percentage = userTasksRepository.findByUserIdAndTaskType(userId, taskType).get(0).getProgress();
        }

       return percentage;

    }

    @Override
    public String getTaskStatus(Date startDate, Date endDate, int taskProgress) {

        String status = "";

        Date todaysDate = new Date();

        //Another can be added as completedOld which can be removed the list above
        //it can be calculated based on the endDate and progress value

    if (todaysDate.after(startDate) && todaysDate.before(endDate) && taskProgress < 100){
        status = "current";
    }
    else if(todaysDate.after(startDate) && todaysDate.before(endDate) && taskProgress == 100){
        status = "completed";
    }
    else if(todaysDate.after(endDate)){
        status = "overdue";
    }
    else if(todaysDate.before(startDate)){
        status = "upcoming";
    }

        return status;
    }
}
