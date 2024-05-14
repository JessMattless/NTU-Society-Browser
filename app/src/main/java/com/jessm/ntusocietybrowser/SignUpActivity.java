package com.jessm.ntusocietybrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        }

        TextView loginText = findViewById(R.id.subtext);
        Button signUpButton = findViewById(R.id.confirmButton);

        SpannableString loginString = new SpannableString(getResources().getString(R.string.account_question_login));
        loginString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, 25, 31, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        loginText.setText(loginString);
        loginText.setMovementMethod(LinkMovementMethod.getInstance());
        loginText.setHighlightColor(Color.TRANSPARENT);

        signUpButton.setOnClickListener(v -> {
            EditText email = findViewById(R.id.emailInput);
            EditText password = findViewById(R.id.passwordInput);
            EditText confirmPassword = findViewById(R.id.confirmPasswordInput);
            EditText ntNumber = findViewById(R.id.ntInput);
//            EditText fName = findViewById(R.id.firstNameInput);
//            EditText lName = findViewById(R.id.lastNameInput);

            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();
            String confirmPasswordString = confirmPassword.getText().toString();
            String ntNumberString = ntNumber.getText().toString();
//            String fNameString = fName.getText().toString();
//            String lNameString = lName.getText().toString();

            Pattern pattern = Pattern.compile("^(N|T)[0-9]{7}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(ntNumberString);
            boolean match = matcher.find();

            if (passwordString.equals(confirmPasswordString) && match) {
                signUp(emailString, passwordString);

//                Map<String, Object> userMap = new HashMap<>();
//                userMap.put("Email", emailString);
//                userMap.put("N/T Number", ntNumberString);
//                userMap.put("First Name", fNameString);
//                userMap.put("Surname", lNameString);

//                db.collection("users").document(user.getUid())
//                        .set(userMap)
//                        .addOnSuccessListener(documentReference -> {
//
//                        });
            }
            else if (!match) {
                //TODO: Make this not throw an error.

//                Snackbar.make(findViewById(R.id.ntInput), R.string.nt_number_warning, Snackbar.LENGTH_SHORT).show();
            }
            else {
//                Snackbar.make(findViewById(R.id.confirmPasswordInput), R.string.password_warning, Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void signUp(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();

                        EditText ntNumber = findViewById(R.id.ntInput);
                        EditText fName = findViewById(R.id.firstNameInput);
                        EditText lName = findViewById(R.id.lastNameInput);

                        String ntNumberString = ntNumber.getText().toString();
                        String fNameString = fName.getText().toString();
                        String lNameString = lName.getText().toString();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("Email", email);
                        userMap.put("NT Number", ntNumberString);
                        userMap.put("First Name", fNameString);
                        userMap.put("Surname", lNameString);
                        userMap.put("Profile Image", "");
                        userMap.put("Description", "");
                        userMap.put("subscribed societies", new ArrayList<String>());

                        db.collection("users").document(user.getUid())
                                .set(userMap)
                                .addOnSuccessListener(documentReference -> {

                                });

                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    }
                });
    }
}