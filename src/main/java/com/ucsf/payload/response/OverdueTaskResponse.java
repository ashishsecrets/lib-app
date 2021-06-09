package com.ucsf.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class OverdueTaskResponse {

    Long taskId;
    String taskName;
    Date startDate;
    Date dueDate;
    String taskStatus;
}
