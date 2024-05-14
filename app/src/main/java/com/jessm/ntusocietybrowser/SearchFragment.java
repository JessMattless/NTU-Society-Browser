package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final String ARG_SEARCH_TEXT = "searchText";

    private String _searchText;

    FirebaseAuth auth;
    FirebaseFirestore db;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param search The text used to search.
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance(String search) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TEXT, search);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            _searchText = getArguments().getString(ARG_SEARCH_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText searchBar = view.findViewById(R.id.searchBar);
        searchBar.setText(_searchText);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                _searchText = searchBar.getText().toString();

                // Used to display a message if no results are found during a search
                int totalCount = 0;
                int hiddenCount = 0;

                TextView emptySearch = requireView().findViewById(R.id.emptySearch);

                // Check each fragment, if the fragment is a search result check the name in the result.
                // If the name includes the search string, then show the result, if not hide it.
                FragmentManager fragmentManager = getParentFragmentManager();
                if (!fragmentManager.isDestroyed()) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    for (Fragment fragment : fragmentManager.getFragments()) {
                        if (fragment instanceof SearchResultFragment) {
                            totalCount++;
                            TextView societyNameView = fragment.requireView().findViewById(R.id.societyName);
                            String societyName = societyNameView.getText().toString();

                            if (societyName.toLowerCase().contains(_searchText.toLowerCase())) transaction.show(fragment);
                            else {
                                transaction.hide(fragment);
                                hiddenCount++;
                            }

                        }
                    }
                    transaction.commit();

                    if (totalCount == hiddenCount) emptySearch.setVisibility(View.VISIBLE);
                    else emptySearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        FragmentManager fragmentManager = getParentFragmentManager();

        if (auth.getUid() != null) {
            db.collection("societies")
                    .orderBy("title")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FragmentTransaction addTransaction = fragmentManager.beginTransaction();
                            addTransaction.setReorderingAllowed(true);
                            for (DocumentSnapshot document : task.getResult()) {
                                SearchResult result = document.toObject(SearchResult.class);
                                result.setId(document.getId());
                                if (result != null)
                                    addTransaction.add(R.id.searchResults, SearchResultFragment.newInstance(result));
                            }
                            addTransaction.commit();
                        }
                    });
        }

        TextView societyStringView = view.findViewById(R.id.societyString);

        SpannableString societyString = new SpannableString(getResources().getString(R.string.new_society_hint));
        societyString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(requireActivity(), CreateSocietyActivity.class));
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, 43, 53, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        societyStringView.setText(societyString);
        societyStringView.setMovementMethod(LinkMovementMethod.getInstance());
        societyStringView.setHighlightColor(Color.TRANSPARENT);

        return view;
    }
}