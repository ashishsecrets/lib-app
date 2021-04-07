package com.ucsf.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class ResetPasswordRequest {

    // user password verification Attribute
    private String password;
    private String confirmPassword;
    private String link;

}