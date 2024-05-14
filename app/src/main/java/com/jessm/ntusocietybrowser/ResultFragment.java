package com.jessm.ntusocietybrowser;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {

    private static final String ARG_RESULT_TITLE = "title";
    private static final String ARG_RESULT_IMAGE = "image";
    private static final String ARG_RESULT_TYPE = "type";
    private static final String ARG_RESULT_ID = "id";

    private String _title;
    private String _image;
    private ResultType _type;
    private String _id;

    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @param image Parameter 2.
     * @return A new instance of fragment SocietyResultFragment.
     */
    public static ResultFragment newInstance(String title, String image, ResultType type, String id) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RESULT_TITLE, title);
        args.putString(ARG_RESULT_IMAGE, image);
        args.putSerializable(ARG_RESULT_TYPE, type);
        args.putString(ARG_RESULT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _title = getArguments().getString(ARG_RESULT_TITLE);
            _image = getArguments().getString(ARG_RESULT_IMAGE);
            _type = (ResultType) getArguments().getSerializable(ARG_RESULT_TYPE);
            _id = getArguments().getString(ARG_RESULT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        Button button = view.findViewById(R.id.button);
        String titleText = _title;
        if (_type == ResultType.Admin) titleText += "\n" + "Test";
        button.setText(titleText);

        button.setOnClickListener(buttonView -> {
            switch (_type) {
                case Society:
                    Intent socIntent = new Intent(requireActivity(), SocietyViewActivity.class);
                    Bundle socArgs = new Bundle();
                    socArgs.putString("Name", _title);
                    socArgs.putString("Id", _id);

                    socIntent.putExtras(socArgs);
                    startActivity(socIntent);
                    break;
                case Admin: case User:
                    Intent userIntent = new Intent(requireActivity(), ProfileActivity.class);
                    Bundle userArgs = new Bundle();
                    userArgs.putString("userId", _id);
                    userArgs.putBoolean("selfProfile", false);

                    userIntent.putExtras(userArgs);
                    startActivity(userIntent);
                    break;
            }

        });

        ImageView image = view.findViewById(R.id.adminImage);
        //TODO: Change Image

        return view;
    }
}