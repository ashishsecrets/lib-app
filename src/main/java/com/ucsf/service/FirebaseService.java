package com.ucsf.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.ucsf.auth.model.User;
import com.ucsf.payload.request.AuthRequest;
import com.ucsf.payload.request.SignUpRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    public static final String COL_NAME="messages";

    public String updateMessageDetails(String message) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document("W8YowaPsCXnZKJ27VnZA\n").update("text", message);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public void createUser(User user, SignUpRequest signUpRequest) throws FirebaseAuthException {

        /*String result = "";

        UserRecord userRecord = FirebaseAuth.getInstance().getUserByPhoneNumber(user.getPhoneCode() + user.getPhoneNumber());

        if()*/

        boolean emailVerified = false;

        if(signUpRequest.getUserRoles() != null){
        if(signUpRequest.getUserRoles().get(0).equals("ADMIN")){
            emailVerified = true;
        }}

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setUid(user.getId().toString())
                .setEmail(user.getEmail())
                .setEmailVerified(emailVerified)
                .setPassword(signUpRequest.getPassword())
                .setPhoneNumber(user.getPhoneCode() + user.getPhoneNumber())
                .setDisplayName(user.getFirstName() + " " + user.getLastName())
                .setPhotoUrl("http://www.example.com/12345678/photo.png")
                .setDisabled(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        // See the UserRecord reference doc for the contents of userRecord.
        System.out.println("Created and fetched user on firebase" + userRecord.getUid());
    }

    public String signInUser(User user) throws FirebaseAuthException {
        String uid = user.getId().toString();

        String customToken = FirebaseAuth.getInstance().createCustomToken(uid);
        // Send token back to client
        return customToken;
    }



}
