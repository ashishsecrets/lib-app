package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EncryptStringServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@Service
class ResetPasswordTest {

    @Autowired
    EncryptStringServiceTest encryption;
    @Autowired
    UserRepository userRepository;


    LocalDateTime now = LocalDateTime.now();

    @Autowired
    public Boolean resetPass(){
        String link = "JIBTN98qT/Hd4qKeXNyRHXsNRDXXbN0ChP8js2mGLsovRLKFIboUP+Adp+bjrMC7iSUqUJfX1CY=";
        Boolean isValid = false;
        String string = "";
        try {
            string = encryption.decrypt(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] split = string.split(",");
        String date = split[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        String username = split[0];
        User user = userRepository.findByUsername(username);
        if (user != null || minutes < 15) {
            isValid = true;
            System.out.println(string);
            //user.setPassword(bcryptEncoder.encode(password));
            //userRepository.save(user);
        }

        return isValid;
    }

}