package com.ucsf.payload.response;

import java.io.Serializable;
import java.util.Set;

import com.ucsf.auth.model.Role;

import lombok.Data;

@Data
public class RegisterResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    private final boolean isVerifiedUser;
    private final String message;
    private final Set<Role> authority;

    public RegisterResponse(String jwttoken, boolean isVerifiedUser,String message,Set<Role> authority) {
        this.jwttoken = jwttoken;
        this.isVerifiedUser = isVerifiedUser;
        this.message = message;
        this.authority = authority;
    }
}