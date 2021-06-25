package com.ucsf.payload.response;

import com.google.cloud.firestore.FieldValue;
import lombok.Data;

import java.util.Map;

@Data
public class Message {

    FieldValue createdAt;
    String text;
    String userId;
    String firstName;
    String lastName;
    String imgPath;

}
