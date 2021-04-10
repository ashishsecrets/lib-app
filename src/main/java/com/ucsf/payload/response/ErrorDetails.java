package com.ucsf.payload.response;
import java.util.Date;

import lombok.Data;

@Data
public class ErrorDetails {
    private int status;
    private String message;
    private String details;

    public ErrorDetails(int status, String message, String details) {
        super();
        this.status = status;
        this.message = message;
        this.details = details;
    }
}
