package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CreateSocietyActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Default activity code
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_society);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // -----

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set the top action bar the view in the activity.
        // Add a listener to the back button to navigate to the correct page.
        setSupportActionBar(findViewById(R.id.topBar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.create_society);

        Toolbar toolbar = findViewById(R.id.topBar);
        toolbar.setNavigationOnClickListener(view -> startActivity(new Intent(CreateSocietyActivity.this, MainActivity.class)));

        EditText title = findViewById(R.id.societyName);
        EditText email = findViewById(R.id.societyEmail);
        EditText description = findViewById(R.id.societyDescription);
        Button imageButton = findViewById(R.id.societyLogoButton);

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(view -> {
            String titleText = title.getText().toString();
            String emailText = email.getText().toString();
            String descText = description.getText().toString();

            // if all text views contain content, add the society to the database using the data given.
            if (!titleText.isEmpty() && !emailText.isEmpty() && !descText.isEmpty()) {
                HashMap<String, Object> data = getNewSociety(titleText, emailText, descText);

                // Add the data map to the database.
                // After, update the "subscribed societies" field in the user's database document to add the new society.
                db.collection("societies")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            db.collection("users")
                            .document(Objects.requireNonNull(auth.getUid()))
                            .update("subscribed societies", FieldValue.arrayUnion(documentReference.getId()));

                            startActivity(new Intent(CreateSocietyActivity.this, MainActivity.class));
                        });
            }
        });
    }

    @NonNull
    private HashMap<String, Object> getNewSociety(String titleText, String emailText, String descText) {
        HashMap<String, Object> data = new HashMap<>();

        data.put("title", titleText);
        data.put("email", emailText);
        data.put("description", descText);
        data.put("image", ""); //TODO: Image

        // By default, the creator of the society becomes the president of that society.
        ArrayList<String> memberList = new ArrayList<>();
        memberList.add(auth.getUid());
        data.put("Members", memberList);

        // Adds the current user to the member list, so they can receive posts/notifications.
        HashMap<String, String> adminList = new HashMap<>();
        adminList.put(auth.getUid(), "President");
        data.put("Admins", adminList);
        return data;
    }
}