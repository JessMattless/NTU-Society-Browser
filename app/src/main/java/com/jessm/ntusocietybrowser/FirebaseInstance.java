package com.jessm.ntusocietybrowser;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseInstance extends FirebaseMessagingService {

    FirebaseFirestore db;
    FirebaseAuth auth;

    /*
     * Used when a new messaging token is generated by the user automatically.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String Uid = auth.getUid();

        // Update the user document in the database with the new token.
        // TODO: Currently Unused
        if (Uid != null) {
            db.collection("users")
                    .document(auth.getUid())
                    .update("token", token);
        }


        Log.d("Messaging Token", token);
    }
}