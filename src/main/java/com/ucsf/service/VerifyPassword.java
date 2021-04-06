package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class VerifyPassword {

    LocalDateTime now = LocalDateTime.now();

    @Autowired
    EncryptStringService encryption;

    @Autowired
    UserRepository userRepository;

    public Boolean verifyPass(String password, String link){
        Boolean isValid = false;
        String string = "";
        try {
            string = encryption.decrypt(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] split = string.split(",");
        String date = split[1];
        //String str = "2016-03-04 11:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        String username = split[0];
        User user = userRepository.findByUsername(username);
        if (user != null && minutes < 15) {
            isValid = true;
        }

        return isValid;
    }


}
