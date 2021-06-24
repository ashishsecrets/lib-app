package com.ucsf.job;

import com.ucsf.auth.model.User;
import com.ucsf.model.Notifications;
import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserTasks;
import com.ucsf.payload.request.Note;
import com.ucsf.repository.NotificationsRepository;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.UserTasksRepository;
import com.ucsf.service.LoggerService;
import com.ucsf.service.PushNotificationService;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    UserRepository userRepository;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    NotificationsRepository notificationsRepository;

    @Autowired
    UserMetaDataRepository userMetaDataRepository;

    @Autowired
    private LoggerService loggerService;

    private static Logger log = LoggerFactory.getLogger(TasksTimeLine.class);


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
                userTask.setWeekCount(item.getWeekCount()+1);
                if(item.getDuration() != null){
                userTask.setDuration(item.getDuration());}
                else{userTask.setDuration(1);}
                userTask.setStudyId(item.getStudyId());
                userTask.setStartDate(dateNowPlus2);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.WEEK_OF_MONTH, item.getDuration());
                userTask.setEndDate(cal.getTime());
                userTasksRepository.save(userTask);

                User user = userRepository.findById(item.getUserId()).get();

                // Notify By Push Notification
                try {
                    Note note = new Note();
                    // Todo Dynamic Study name
                    note.setContent(
                            "Dear " + user.getFirstName() + "A new task "+ item.getTitle() + " has been allocated to you.");
                    note.setSubject("New Task");
                    String msgId = pushNotificationService.sendNotification(note, user.getDevideId());

                    //Adding sent notification to db
                    Notifications notification = new Notifications();
                    notification.setDate(new Date());
                    notification.setDescription(note.getContent());
                    notification.setType(Notifications.NotificationType.PUSH);
                    notification.setKind(Notifications.NotificationKind.APPROVEDINSTUDY);
                    notification.setKindDescription("New Task in task list.");
                    notification.setUserId(user.getId());
                    notificationsRepository.save(notification);

                    UserMetadata metaData = new UserMetadata();
                    metaData.setNotifiedBy(UserMetadata.StudyAcceptanceNotification.NOTIFIED_BY_PUSH);
                    metaData.setStudyStatus(UserMetadata.StudyStatus.ENROLLED);
                    userMetaDataRepository.save(metaData);
                    loggerService.printLogs(log, "sendNotifications", "New Task push notification sent to user: "
                            + user.getEmail() + "At: " + new Date() + "msgId = " + msgId);
                } catch (Exception e) {
                    loggerService.printErrorLogs(log, "sendNotifications",
                            "Error while sending new task push notification to user: " + user.getEmail()
                                    + "At: " + new Date());

                }

            } else if (dateNow.before(item.getEndDate())) {
                // it's before
            } else {
                // it's after
            }

        }

    }

}
