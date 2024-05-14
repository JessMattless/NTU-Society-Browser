package com.jessm.ntusocietybrowser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key != null) {
                if (!key.equals("Dark Mode")) {
                    db.collection("users")
                            .document(auth.getUid())
                            .update(key, sharedPreferences.getString(key, ""));
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.d("Heko", "Yes");
//                                }
//                            });
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(sharedPreferences.getBoolean(key, false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
    }



//        db.collection("users")
//                .document(Objects.requireNonNull(auth.getUid()))
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
//
//                        DocumentSnapshot document = task.getResult();
//                        editor.putString("firstName", document.getString("First Name"));
//
//                        editor.apply();
//                    }
//                });

}