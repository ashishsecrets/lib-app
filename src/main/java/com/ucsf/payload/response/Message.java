package com.ucsf.payload.response;

import lombok.Data;

import java.util.Map;

@Data
public class Message {

    String createdAt;
    String text;
    String userId;
    String firstName;
    String lastName;
    String imgPath;

}
