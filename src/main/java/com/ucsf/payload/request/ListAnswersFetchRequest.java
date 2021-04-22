package com.ucsf.payload.request;

import lombok.Data;

@Data
public class ListAnswersFetchRequest {

    private Long studyId;
    private String type;
    //private Long typeId;
}