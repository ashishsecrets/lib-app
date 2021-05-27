package com.ucsf.payload.response;

import com.ucsf.model.Notifications;
import com.ucsf.model.UcsfStudy.StudyFrequency;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NotificationListResponse {

	List<Notifications> notificationsList;
}
