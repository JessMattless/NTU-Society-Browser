package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Fragment selectedFragment;
    BottomNavigationView bottomNav;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the preference for dark mode and use it to change the app theme if needed
        // If the preference cannot be found then the default is light mode theme.
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        int darkMode = sharedPreferences.getBoolean("Dark Mode", false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.topBar));
        ActionBar actionBar = getSupportActionBar();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNav = findViewById(R.id.bottomBar);
        bottomNav.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            // I would use a switch statement here if I could but it complains about the IDs not being constant
            // Set the selected fragment and activity title to match the selected item in the bottom navigation bar
            if (actionBar != null) {
                if (itemId == R.id.search) {
                    selectedFragment = new SearchFragment();
                    actionBar.setTitle(R.string.search_page_title);
                }
                else if (itemId == R.id.notifications) {
                    selectedFragment = new NotificationsFragment();
                    actionBar.setTitle(R.string.notification_page_title);
                }
                else if (itemId == R.id.calendar) {
                    selectedFragment = new CalendarFragment();
                    actionBar.setTitle(R.string.calendar_page_title);
                }
                else {
                    selectedFragment = HomeFragment.newInstance();
                    actionBar.setTitle(R.string.home_page_title);
                }
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();

            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If the user is not logged in, return them to the Login activity
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }

        // Set the user's preferences (Used for the settings page)
        db.collection("users")
                .document(Objects.requireNonNull(auth.getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

                        DocumentSnapshot document = task.getResult();
                        editor.putString("Description", document.getString("Description"));
                        editor.putString("First Name", document.getString("First Name"));
                        editor.putString("Surname", document.getString("Surname"));
                        editor.putString("NT Number", document.getString("NT Number"));
                        editor.putString("Email", document.getString("Email"));

                        editor.apply();
                    }
                });


        bottomNav.setSelectedItemId(R.id.home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Depending on the menu item selected from the top app bar, send the user to a different activity or open a dialog.
        if (itemId == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            Bundle args = new Bundle();
            args.putString("userId", auth.getUid());
            args.putBoolean("selfProfile", true);

            intent.putExtras(args);
            startActivity(intent);
        }
        else if (itemId == R.id.entered_societies) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

            Bundle args = new Bundle();
            args.putString("pageTitle", getResources().getString(R.string.societies_option));
            intent.putExtras(args);

            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

            Bundle args = new Bundle();
            args.putString("pageTitle", getResources().getString(R.string.settings_option));
            intent.putExtras(args);

            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.log_out) {
            ConfirmLogoutFragment logoutFragment = new ConfirmLogoutFragment();
            logoutFragment.show(getSupportFragmentManager(), getResources().getString(R.string.logout_confirmation_text));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
