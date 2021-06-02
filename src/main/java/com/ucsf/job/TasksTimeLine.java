package com.ucsf.job;

import com.ucsf.model.UserTasks;
import com.ucsf.repository.UserTasksRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EnableScheduling
@Service
public class TasksTimeLine {

    @Autowired
    UserTasksRepository userTasksRepository;

    //needs to run once daily before sunrise
    // at 12:00 AM every day
    @Scheduled(cron="0 0 0 * * ?")
    public void addTasksWithFrequency(){

        Date dateNow = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date dateNowPlus2 = calendar.getTime();

        List<UserTasks> list = userTasksRepository.findAll();

        for(UserTasks item : list){

            if (DateUtils.isSameDay(dateNowPlus2, item.getEndDate())) {
                // it's same
                UserTasks userTask = new UserTasks();
                userTask.setUserId(item.getUserId());
                userTask.setTaskId(item.getTaskId());
                userTask.setTaskType(item.getTaskType());
                userTask.setProgress(0);
                userTask.setDescription(item.getDescription());
                userTask.setTitle(item.getTitle());
                if(item.getDuration() != null){
                userTask.setDuration(item.getDuration());}
                else{userTask.setDuration(1);}
                userTask.setStudyId(item.getStudyId());
                userTask.setStartDate(dateNowPlus2);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.WEEK_OF_MONTH, item.getDuration());
                userTask.setEndDate(cal.getTime());
                userTasksRepository.save(userTask);
            } else if (dateNow.before(item.getEndDate())) {
                // it's before
            } else {
                // it's after
            }

        }

    }

}
