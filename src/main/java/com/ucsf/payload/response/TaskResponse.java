package com.ucsf.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class TaskResponse {

    int taskId;
    String taskName;
    Date startDate;
    Date dueDate;
    String taskStatus;
    int taskPercentage;
}
