package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RESULT_TITLE = "resultTitle";
    private static final String ARG_RESULT_DESCRIPTION = "resultDesc";
    private static final String ARG_RESULT_IMAGE = "resultImage";
    private static final String ARG_RESULT_ID = "resultId";

    private String _resultTitle;
    private String _resultDesc;
    private String _resultImage;
    private String _resultId;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param result The search result to be used to populate the fragment.
     * @return A new instance of fragment SearchResultFragment.
     */
    public static SearchResultFragment newInstance(SearchResult result) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RESULT_TITLE, result.getTitle());
        args.putString(ARG_RESULT_DESCRIPTION, result.getDescription());
        args.putString(ARG_RESULT_IMAGE, result.getImage());
        args.putString(ARG_RESULT_ID, result.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _resultTitle = getArguments().getString(ARG_RESULT_TITLE);
            _resultDesc = getArguments().getString(ARG_RESULT_DESCRIPTION);
            _resultImage = getArguments().getString(ARG_RESULT_IMAGE);
            _resultId = getArguments().getString(ARG_RESULT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        TextView resultTitle = view.findViewById(R.id.societyName);
        resultTitle.setText(_resultTitle);

        TextView resultDesc = view.findViewById(R.id.societyDescription);
        resultDesc.setText(_resultDesc);

        ImageView resultImage = view.findViewById(R.id.adminImage);
//        resultImage.setImageResource(_resultImage);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(view1 -> {
            Intent socIntent = new Intent(requireActivity(), SocietyViewActivity.class);
            Bundle socArgs = new Bundle();
            socArgs.putString("Name", _resultTitle);
            socArgs.putString("Id", _resultId);

            socIntent.putExtras(socArgs);
            startActivity(socIntent);
        });

        return view;
    }
}