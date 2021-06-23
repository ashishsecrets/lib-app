package com.ucsf.payload.response;

import lombok.Data;

import java.util.Map;

@Data
public class ChatRoom {

    String createdAt;
    Map<String, String> lastMessage;
    Map<String, Boolean> users;

}
