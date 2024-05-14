package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar(findViewById(R.id.topBar));
        getSupportActionBar().setTitle(R.string.profile_page_title);

        Toolbar toolbar = findViewById(R.id.topBar);
        toolbar.setNavigationOnClickListener(view -> {
            Bundle args = getIntent().getExtras();
            if (args.getBoolean("selfProfile")) startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            else {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);

                Bundle sendArgs = new Bundle();
                sendArgs.putString("pageTitle", getResources().getString(R.string.societies_option));
                intent.putExtras(sendArgs);

                startActivity(intent);
            }
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView name = findViewById(R.id.username);
        TextView ntNumber = findViewById(R.id.ntNumber);
        TextView description = findViewById(R.id.description);
        ImageView profileImage = findViewById(R.id.adminImage);

        Bundle args = getIntent().getExtras();
        String userId = "";
        if (args != null) {
            userId = args.getString("userId");
        }

        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       DocumentSnapshot document = task.getResult();
                       if (document.exists()) {
                           String nameString = document.getString("First Name") + " " + document.getString("Surname");
                           name.setText(nameString);

                           ntNumber.setText(document.getString("NT Number"));
                           description.setText(document.getString("Description"));
                       }
                   }
                });

    }
}