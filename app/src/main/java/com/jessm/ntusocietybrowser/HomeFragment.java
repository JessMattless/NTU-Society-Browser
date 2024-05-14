package com.jessm.ntusocietybrowser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentManager fragmentManager = getParentFragmentManager();


        //TODO: Fix this as it duplicates results when closing and re-opening the app
        //TODO: When switching windows quickly it can error out not finding R.id.postView for the new fragments

        // Get all posts that contain the current user as a subscribed member.
        // Order them by date to show thew most recent first.
        if (auth.getUid() != null) {
            db.collectionGroup("Posts")
                    .where(Filter.arrayContains("subscribed members", auth.getUid()))
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!fragmentManager.isDestroyed()) {
                                FragmentTransaction addTransaction = fragmentManager.beginTransaction();
                                addTransaction.setReorderingAllowed(true);

                                // Check if the post is of type "important", if it is, add it to the feed first.
                                // Important is ignored more than a week after posting though.
                                ArrayList<HomePostFragment> unimportantPosts = new ArrayList<>();
                                for (DocumentSnapshot innerDocument : task.getResult()) {
                                    HomePost post = innerDocument.toObject(HomePost.class);
                                    if (post != null) {
                                        if (post.getType() != HomePostType.Important
                                                || (post.getTimestamp().getTime() < System.currentTimeMillis() - (86400 * 7 * 1000)))
                                            unimportantPosts.add(HomePostFragment.newInstance(post));
                                        else addTransaction.add(R.id.postView, HomePostFragment.newInstance(post));
                                    }
                                }

                                // Add all "unimportant" posts to the page
                                for (HomePostFragment fragment : unimportantPosts) {
                                    addTransaction.add(R.id.postView, fragment);
                                }
                                addTransaction.commit();
                            }
                        }
                    });

        }

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}