package com.ucsf.payload.response;

import lombok.Data;

@Data
public class StudyInfoData {
    //Data

    public enum StudyInfoSatus {
        TRUE, FALSE, NONE
    }

    public String message;
    public StudyInfoSatus isFinished;

}
