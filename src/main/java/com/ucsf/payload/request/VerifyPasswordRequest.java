package com.ucsf.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class VerifyPasswordRequest {

    // user password verification Attribute
    private String password;
    private String link;

}