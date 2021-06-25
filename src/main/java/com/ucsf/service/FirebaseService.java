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
import com.ucsf.payload.response.ChatRoom;
import com.ucsf.payload.response.Message;
import com.ucsf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    @Value("${server.port}")
    int serverPort;

    @Autowired
    UserRepository userRepository;

    public static final String COL_NAME="chatrooms";

    String getServerType(){
        String serverType = "none";

        if(serverPort == 8181){
            serverType = "test";
        }
        else if(serverPort == 8182){
            serverType = "prod";
        }
        else if(serverPort == 8081){
            serverType = "local";
        }

        return serverType;
    }

    public String createChatRoom(User user) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        ChatRoom chatRoom = new ChatRoom();

        chatRoom.setCreatedAt(new Date().toString());

        Map<String, String> one = new HashMap<>();
        one.put("text", user.getLastName());
        one.put("userName", user.getFirstName());
        chatRoom.setLastMessage(one);

        Map<String, Boolean> two = new HashMap<>();
        two.put(user.getId().toString(), true);
        chatRoom.setUsers(two);

        Message message = new Message();
        User user2 = userRepository.findByEmail("skin@yopmail.com");
        message.setUserId(user2.getId().toString() + "_" + getServerType());
        message.setCreatedAt(new Date().toString());
        message.setText("Welcome !");
        message.setFirstName(user2.getFirstName());
        message.setLastName(user2.getLastName());
        message.setImgPath("");

        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document(user.getId().toString() + "_" + getServerType()).create(chatRoom);
        dbFirestore.collection(COL_NAME).document(user.getId().toString() + "_" + getServerType()).collection("messages").document().create(message);
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
                .setUid(user.getId().toString() + "_" + getServerType())
                .setEmail(user.getEmail())
                .setEmailVerified(emailVerified)
                .setPassword(signUpRequest.getPassword())
                //.setPhoneNumber(user.getPhoneCode() + user.getPhoneNumber())
                .setDisplayName(user.getFirstName() + " " + user.getLastName())
                .setPhotoUrl("http://www.example.com/12345678/photo.png")
                .setDisabled(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        // See the UserRecord reference doc for the contents of userRecord.
        System.out.println("Created and fetched user on firebase" + userRecord.getUid());
    }

    public String signInUser(User user) throws FirebaseAuthException {
        String uid = user.getId().toString() + "_" + getServerType();

        String customToken = FirebaseAuth.getInstance().createCustomToken(uid);
        // Send token back to client
        return customToken;
    }


    public void deleteInitialMsg(User user) {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        dbFirestore.collection(COL_NAME).document(user.getId().toString() + "_" + getServerType()).collection("messages").document().delete();

    }
}
