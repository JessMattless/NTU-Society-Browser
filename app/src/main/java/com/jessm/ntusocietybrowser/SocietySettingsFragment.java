package com.jessm.ntusocietybrowser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocietySettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocietySettingsFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;

    private static final String ARG_ADMIN = "isAdmin";
    private static final String ARG_SOCIETY_ID = "societyId";

    private boolean _isAdmin;
    private String _societyId;

    HashMap<String, String> adminList;

    public SocietySettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isAdmin Is the user an admin?
     * @return A new instance of fragment SocietySettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocietySettingsFragment newInstance(boolean isAdmin, String societyId) {
        SocietySettingsFragment fragment = new SocietySettingsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ADMIN, isAdmin);
        args.putString(ARG_SOCIETY_ID, societyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _isAdmin = getArguments().getBoolean(ARG_ADMIN);
            _societyId = getArguments().getString(ARG_SOCIETY_ID);
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_society_settings, container, false);

        LinearLayout adminPanel = view.findViewById(R.id.admin);
        if (!_isAdmin) adminPanel.setVisibility(View.GONE);
        else {
            db.collection("societies")
                    .document(_societyId)
                    .get()
                    .addOnSuccessListener(document -> {
                        adminList = (HashMap<String, String>) document.get("Admins");

                        db.collection("users")
                                .whereIn(FieldPath.documentId(), new ArrayList<>(adminList.keySet()))
                                .get()
                                .addOnCompleteListener(task -> {
                                   if (task.isSuccessful()) {
                                       FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                       for (DocumentSnapshot userDocument : task.getResult()) {
                                           String username = userDocument.getString("First Name") + " " + userDocument.getString("Surname");
                                           transaction.add(R.id.adminList, CommitteeMemberFragment.newInstance(username, adminList.get(userDocument.getId()), userDocument.getString("Profile Image")));
                                       }
                                       transaction.commit();
                                   }
                                });
                    });
        }

        return view;
    }
}