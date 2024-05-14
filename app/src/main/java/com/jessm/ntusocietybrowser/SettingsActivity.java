package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth auth;

    Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();

        setSupportActionBar(findViewById(R.id.topBar));

        Toolbar toolbar = findViewById(R.id.topBar);
        toolbar.setNavigationOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        });

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        }
        
        Bundle args = getIntent().getExtras();
        String pageTitle = args != null ? args.getString("pageTitle") : null;

        if (pageTitle != null) {
            switch (pageTitle) {
                case "Entered Societies":
                    selectedFragment = EnteredSocietiesFragment.newInstance();
                    getSupportActionBar().setTitle(R.string.societies_option);
                    break;
                case "Settings":
                    selectedFragment = new SettingsFragment();
                    getSupportActionBar().setTitle(R.string.settings_option);
                    break;
                case "Society Settings":
                    selectedFragment = SocietySettingsFragment.newInstance(args.getBoolean("admin"), args.getString("societyId"));
                    getSupportActionBar().setTitle(R.string.society_settings_option);
                default:
                    break;
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingsContainer, selectedFragment)
        .commit();
    }
}