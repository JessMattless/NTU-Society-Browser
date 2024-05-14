package com.jessm.ntusocietybrowser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnteredSocietiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnteredSocietiesFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;

    public EnteredSocietiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment EnteredSocietiesFragment.
     */
    public static EnteredSocietiesFragment newInstance() {

        return new EnteredSocietiesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entered_societies, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // If the fragment manager has been destroyed then do not run any transactions.
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.isDestroyed()) return;
        if (auth.getUid() == null) return;

        // Remove any fragments current in the search fragment's results container
        // Used for if the user does something such as focus another app then resume this one
        FragmentTransaction removeTransaction = fragmentManager.beginTransaction();
        removeTransaction.setReorderingAllowed(true);
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment instanceof ResultFragment) {
                removeTransaction.remove(fragment);
            }
        }
        removeTransaction.commit();


        // Get each society the user is a member of from the database and create a fragment for each.
        // Add those fragments to the activity.
        db.collection("societies")
                .whereArrayContains("Members", auth.getUid())
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FragmentTransaction addTransaction = fragmentManager.beginTransaction();
                        for (DocumentSnapshot document : task.getResult()) {
                            addTransaction.add(R.id.societyList, ResultFragment.newInstance(
                                    document.getString("title"),
                                    document.getString("image"),
                                    ResultType.Society,
                                    document.getId()
                                    ));
                        }
                        addTransaction.commit();
                    }
                });

    }
}