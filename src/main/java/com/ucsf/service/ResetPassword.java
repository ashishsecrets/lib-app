package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.controller.ResetPasswordController;
import com.ucsf.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class ResetPassword {

    LocalDateTime now = LocalDateTime.now();

    @Autowired
    EncryptStringService encryption;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bcryptEncoder;

    @Autowired
	private LoggerService loggerService;

	private static Logger log = LoggerFactory.getLogger(ResetPasswordController.class);
	
    public Boolean resetPass(String password, String link){
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
        if (user != null && minutes < 15) {
            isValid = true;
            user.setPassword(bcryptEncoder.encode(password));
            loggerService.printLogs(log, "resetPass", "Updated Password");
            userRepository.save(user);
        }

        return isValid;
    }


}
