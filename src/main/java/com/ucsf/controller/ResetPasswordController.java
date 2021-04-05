package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.payload.ResetPasswordResponse;
import com.ucsf.payload.VerifyPasswordRequest;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/api/resetpassword")
public class ResetPasswordController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestParam String email) throws IOException, MessagingException {
        User user = userRepository.findByEmail(email);
        if (email != null && !email.equals("")) {

            if (user == null) {
                return ResponseEntity.ok(new ResetPasswordResponse(false, "There is no user with this email."));
            }
        }
        emailService.sendmail(user);
        return ResponseEntity.ok(new ResetPasswordResponse(true, "A reset password email has been sent."));
    }


    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ResponseEntity<VerifyPasswordRequest> verifyPassword(@RequestParam String password) {

        if (password != null && !password.equals("")) {
            User user = userRepository.findByEmail(password);
            if (user == null) {
                return ResponseEntity.ok(new VerifyPasswordRequest());
            }
        }
        return ResponseEntity.ok(new VerifyPasswordRequest());
    }


}
