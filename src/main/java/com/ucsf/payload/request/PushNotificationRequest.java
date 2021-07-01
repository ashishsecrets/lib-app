package com.ucsf.payload.request;

import lombok.Data;

@Data
public class PushNotificationRequest {

    private String text;
    private String firstName;
}