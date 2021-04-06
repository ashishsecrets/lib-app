package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.config.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PasswordResetLinkService {

    @Autowired
    EncryptStringService encryption;


    private LocalDateTime now = LocalDateTime.now();

    public String createPasswordResetLink(User user){
        String link = "";
        String string = user.getUsername()+"|"+now.toString()+"|"+"ucsfredblinkcheckpoint";

        try {
            link = encryption.encrypt(string);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return link;

    }

}
