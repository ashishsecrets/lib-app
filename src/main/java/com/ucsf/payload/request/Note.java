package com.ucsf.payload.request;

import java.util.Map;

import lombok.Data;

@Data
public class Note {
    private String subject;
    private String content;
    private String type;
    private Map<String, String> data;
    private String image;
}
