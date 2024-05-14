package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class SocietyViewActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;

    boolean isAdmin = false;
    String societyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_society_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) startActivity(new Intent(SocietyViewActivity.this, LoginActivity.class));

        Bundle givenArgs = getIntent().getExtras();
        if (givenArgs == null) return;

        societyId = givenArgs.getString("Id");

        setSupportActionBar(findViewById(R.id.topBar));
        getSupportActionBar().setTitle(givenArgs.getString("Name"));

        Toolbar toolbar = findViewById(R.id.topBar);
        toolbar.setNavigationOnClickListener(view -> startActivity(new Intent(SocietyViewActivity.this, MainActivity.class)));

        Button createPostButton = findViewById(R.id.createPostButton);
        createPostButton.setOnClickListener(view -> {
            SocietyPostFragment newPostFragment = SocietyPostFragment.newInstance(givenArgs.getString("Id"));
            newPostFragment.show(getSupportFragmentManager(), getResources().getString(R.string.create_post));
        });

        db.collection("societies")
                .document(societyId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        ImageView logo = findViewById(R.id.adminImage);
                        //TODO: Logo Here

                        TextView description = findViewById(R.id.description);
                        description.setText(document.getString("description"));

                        HashMap<String, String> adminList = (HashMap<String, String>) document.get("Admins");
//                        ArrayList<String> adminArray = (ArrayList<String>) adminList.values();

                        if (adminList != null) {
                            if (new ArrayList<>(adminList.keySet()).contains(auth.getUid())) isAdmin = true;

                            db.collection("users")
                                    .whereIn(FieldPath.documentId(), new ArrayList<>(adminList.keySet()))
//                                    .orderBy("First Name", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(userTask -> {
                                        if (userTask.isSuccessful()) {
                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                                            for (DocumentSnapshot userDocument : userTask.getResult()) {
                                                String username = adminList.get(userDocument.getId()) + "\n" + userDocument.getString("First Name") + " " + userDocument.getString("Surname");
                                                transaction.add(R.id.adminsView, ResultFragment.newInstance(
                                                        username,
                                                        userDocument.getString("Profile Image"),
                                                        ResultType.Admin,
                                                        userDocument.getId()
                                                ));
                                            }

                                            transaction.commit();
                                        }
                                    });
                        }

                        if (isAdmin) createPostButton.setVisibility(View.VISIBLE);
                    }
                });

        db.collection("users")
                .whereArrayContains("subscribed societies", societyId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        for (DocumentSnapshot document : task.getResult()) {
                            String username = document.getString("First Name") + " " + document.getString("Surname");
                            transaction.add(R.id.membersView, ResultFragment.newInstance(
                                    username,
                                    document.getString("Profile Image"),
                                    ResultType.User,
                                    document.getId()));
                        }
                        transaction.commit();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu_committee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settings) {
            Intent intent = new Intent(SocietyViewActivity.this, SettingsActivity.class);

            Bundle args = new Bundle();
            args.putString("pageTitle", getResources().getString(R.string.society_settings_option));
            args.putBoolean("admin", isAdmin);
            args.putString("societyId", societyId);
            intent.putExtras(args);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}