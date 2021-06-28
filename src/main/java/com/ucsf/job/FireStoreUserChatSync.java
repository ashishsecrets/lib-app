package com.ucsf.job;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.ucsf.auth.model.Role;
import com.ucsf.auth.model.User;
import com.ucsf.payload.request.SignUpRequest;
import com.ucsf.repository.UserConsentRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Service
public class FireStoreUserChatSync {

    @Autowired
    FirebaseService firebaseService;

    @Autowired
    UserRepository userRepository;

    public void syncAllUsersToFirebase() throws FirebaseAuthException, ExecutionException, InterruptedException {

        Iterable<User> allUsers = userRepository.findAll();

        for(User user: allUsers){

            String uid = user.getId().toString() + "_" + firebaseService.getServerType();


            try {

                    for (Role role : user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<Role>()) {
                        if (role.getName().toString().equals("ADMIN") || role.getName().toString().equals("PHYSICIAN") || role.getName().toString().equals("STUDYTEAM")) {
                            SignUpRequest signUpRequest = new SignUpRequest();
                            signUpRequest.setPassword("123456");
                            ArrayList<String> x = new ArrayList<>();
                            x.add(role.getName().toString());

                            signUpRequest.setUserRoles(x);
                            firebaseService.createUser(user, signUpRequest);

                        } else if (role.getName().toString().equals("PRE_VERIFICATION_USER") || role.getName().toString().equals("PATIENT")) {

                            SignUpRequest signUpRequest = new SignUpRequest();
                            signUpRequest.setPassword("123456");
                            firebaseService.createUser(user, signUpRequest);
                            firebaseService.createChatRoom(user);

                        }
                    }

            } catch (FirebaseAuthException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

    }

}
