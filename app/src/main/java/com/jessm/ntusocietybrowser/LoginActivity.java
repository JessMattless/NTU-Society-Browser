package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // If a user is already logged in, go to the home activity.
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        TextView signUpText = findViewById(R.id.subtext);
        Button loginButton = findViewById(R.id.confirmButton);

        // Set up a spannable string for text in the login activity.
        // When the highlighted text is clicked, execute the "onClick".
        // Update the draw state to show the link text.
        SpannableString signUpString = new SpannableString(getResources().getString(R.string.account_question_sign_up));
        signUpString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, 23, 30, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        signUpText.setText(signUpString);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
        signUpText.setHighlightColor(Color.TRANSPARENT);

        // If the email and login are bot not empty, attempt a login
        loginButton.setOnClickListener(v -> {
            EditText email = findViewById(R.id.emailInput);
            EditText password = findViewById(R.id.passwordInput);

            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();

            if (!emailString.isEmpty() && !passwordString.isEmpty()) {
                login(email.getText().toString(), password.getText().toString());
            }
        });
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                });
    }

}