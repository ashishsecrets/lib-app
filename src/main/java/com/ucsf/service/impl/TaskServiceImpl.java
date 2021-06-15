package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.model.SurveyAnswer;
import com.ucsf.model.SurveyQuestion;
import com.ucsf.model.UserSurveyStatus;
import com.ucsf.model.UserTasks;
import com.ucsf.payload.response.OverDuePatientTasksListResponse;
import com.ucsf.payload.response.OverdueTaskResponse;
import com.ucsf.payload.response.SurveyResponse;
import com.ucsf.payload.response.TaskResponse;
import com.ucsf.repository.*;
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

    @Autowired
    SurveyAnswerRepository surveyAnswerRepository;


    @Autowired
    UserRepository userRepository;


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
                userSurveyStatus.setIndexValue(0);
                userSurveyStatus.setMaxIndexValue(0);
                userSurveyStatus.setSkipCount(0);
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
            taskResponse.setTaskType(task.getTaskType());
            int taskProgress = getTaskProgress(task.getTaskId(), task.getUserId(), task.getTaskType(), task.getTaskTrueId());
            taskResponse.setTaskPercentage(taskProgress);
            taskResponse.setTaskStatus(getTaskStatus(task.getStartDate(), task.getEndDate(), taskProgress));
            taskResponseList.add(taskResponse);

        }


        return taskResponseList;
    }
    @Override
    public List<SurveyResponse> getAlteredTaskListSurvey(List<UserTasks> tasks) {

        List<SurveyResponse> taskResponseList = new ArrayList<>();

        for(UserTasks task : tasks){
            if(task.getTaskType().equals("survey")) {
                SurveyResponse surveyResponse = new SurveyResponse();
                surveyResponse.setSurveyTrueId(task.getTaskTrueId());
                surveyResponse.setSurveyName(task.getTitle());
                surveyResponse.setStartDate(task.getStartDate());
                surveyResponse.setDueDate(task.getEndDate());
                int surveyProgress = getTaskProgress(task.getTaskId(), task.getUserId(), task.getTaskType(), task.getTaskTrueId());
                String surveyStatus = getTaskStatus(task.getStartDate(), task.getEndDate(), surveyProgress);
                surveyResponse.setSurveyPercentage(surveyProgress);

                if(surveyStatus.equals("upcoming")){
                surveyResponse.setSurveyStatus("Yet to Start");
                taskResponseList.add(surveyResponse);
                }
                else if(surveyStatus.equals("current")){
                    surveyResponse.setSurveyStatus("current");
                    taskResponseList.add(surveyResponse);
                }

            }
        }


        return taskResponseList;
    }

    @Override
    public List<OverDuePatientTasksListResponse> getAlteredTaskListStudy() {

        List<OverDuePatientTasksListResponse> overDuePatientList = new ArrayList<>();



        Iterable<User> userList = userRepository.findAll();

        Long userId = null;

        for(User user : userList) {

            List<UserTasks> userTasks = userTasksRepository.findByUserId(user.getId());

            if(userTasks != null || !userTasks.isEmpty()) {

                List<OverdueTaskResponse> taskResponseList = new ArrayList<>();

                for (UserTasks item : userTasks) {

                    Date todaysDate = new Date();

                    if (todaysDate.after(item.getEndDate())) {

                        OverdueTaskResponse taskResponse = new OverdueTaskResponse();
                        taskResponse.setTaskId(item.getTaskTrueId());
                        taskResponse.setTaskName(item.getTitle());
                        taskResponse.setStartDate(item.getStartDate());
                        taskResponse.setDueDate(item.getEndDate());
                        taskResponse.setTaskStatus("overdue");
                        taskResponseList.add(taskResponse);
                        userId = item.getUserId();
                    }


                }

                OverDuePatientTasksListResponse overDuePatientResponse = new OverDuePatientTasksListResponse();
                if(!taskResponseList.isEmpty()){
                overDuePatientResponse.setPatientOverDueList(taskResponseList);
                overDuePatientResponse.setUserId(userId);
                overDuePatientResponse.setPatient(userRepository.findById(userId).get().getFirstName());
                overDuePatientList.add(overDuePatientResponse);}

            }


        }



        return overDuePatientList;

    }

    @Override
    public int getTaskProgress(Long taskId, Long userId, String taskType, Long taskTrueId) {

        float percentage = 0;
        float totalAnswers = 0;
        UserSurveyStatus surveyStatus = null;
        List<SurveyQuestion> surveyQuestionList = null;
        List<SurveyAnswer> surveyAnswersList = null;

        if(userSurveyStatusRepository.findByUserIdAndSurveyIdAndTaskTrueId(userId, taskId, taskTrueId) != null){
            surveyStatus = userSurveyStatusRepository.findByUserIdAndSurveyIdAndTaskTrueId(userId, taskId, taskTrueId);
            surveyQuestionList = surveyQuestionRepository.findBySurveyId(taskId);
            surveyAnswersList = surveyAnswerRepository.findByTaskTrueIdAndAnsweredById(taskTrueId, userId);
            if(surveyAnswersList != null){totalAnswers = surveyAnswersList.size();}

       percentage = (float) totalAnswers/surveyQuestionList.size()*100;

            if(surveyStatus.getSkipCount() == 1 && percentage == (float) (surveyQuestionList.size()-1)/surveyQuestionList.size()*100){
                percentage = 100;
            }

        //Optional<UserTasks> taskOp = userTasksRepository.findById(userTasksRepository.findByUserIdAndTaskId(userId, taskId).getTaskTrueId());
            Optional<UserTasks> taskOp = userTasksRepository.findById(taskTrueId);

            UserTasks task = taskOp.get();
            task.setProgress(Math.round(percentage));
            userTasksRepository.save(task);
            }
        else{
            // To be changed when we start saving other tasks
            percentage = userTasksRepository.findByUserIdAndTaskType(userId, taskType).get(0).getProgress();
        }

       return Math.round(percentage);

    }

    /*@Override
    public int getTaskProgress(Long taskId, Long userId, String taskType, Long taskTrueId) {

        float percentage = 0;
        UserSurveyStatus surveyStatus = null;
        List<SurveyQuestion> surveyQuestionList = null;

        if(userSurveyStatusRepository.findByUserIdAndSurveyIdAndTaskTrueId(userId, taskId, taskTrueId) != null){
            surveyStatus = userSurveyStatusRepository.findByUserIdAndSurveyIdAndTaskTrueId(userId, taskId, taskTrueId);
            surveyQuestionList = surveyQuestionRepository.findBySurveyId(taskId);

       percentage = (float) surveyStatus.getIndexValue()/surveyQuestionList.size()*100;

        //Optional<UserTasks> taskOp = userTasksRepository.findById(userTasksRepository.findByUserIdAndTaskId(userId, taskId).getTaskTrueId());
            Optional<UserTasks> taskOp = userTasksRepository.findById(taskTrueId);

            UserTasks task = taskOp.get();
            task.setProgress(Math.round(percentage));
            userTasksRepository.save(task);
            }
        else{
            // To be changed when we start saving other tasks
            percentage = userTasksRepository.findByUserIdAndTaskType(userId, taskType).get(0).getProgress();
        }

       return Math.round(percentage);

    }*/

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
