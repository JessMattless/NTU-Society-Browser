package com.jessm.ntusocietybrowser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

public class ConfirmLogoutFragment extends DialogFragment {

    FirebaseAuth auth;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();

        // Show a dialog popup. Giving 2 buttons.
        // If confirm is clicked, log the user out and return to the home page.
        // If cancel is clicked, close the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_dialogue_text)
                .setPositiveButton(R.string.confirm_logout_text, (dialogInterface, i) -> {
                    auth.signOut();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                })
                .setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> this.dismiss());

        return builder.create();
    }
}