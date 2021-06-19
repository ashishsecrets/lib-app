package com.ucsf.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    public static final String COL_NAME="messages";

    public String updateMessageDetails(String message) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COL_NAME).document("lCE7DvfeG5aQryoVGatn").update("text", message);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

}
