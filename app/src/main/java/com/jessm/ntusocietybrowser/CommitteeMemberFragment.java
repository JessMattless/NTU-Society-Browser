package com.jessm.ntusocietybrowser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommitteeMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommitteeMemberFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "name";
    private static final String ARG_ROLE = "role";
    private static final String ARG_IMAGE = "image";

    private String _name;
    private String _role;
    private String _image;

    public CommitteeMemberFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name The name of the admin.
     * @param role The role of the admin.
     * @param image The profile image of the admin.
     * @return A new instance of fragment CommitteeMemberFragment.
     */
    public static CommitteeMemberFragment newInstance(String name, String role, String image) {
        CommitteeMemberFragment fragment = new CommitteeMemberFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_ROLE, role);
        args.putString(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _name = getArguments().getString(ARG_NAME);
            _role = getArguments().getString(ARG_ROLE);
            _image = getArguments().getString(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_committee_member, container, false);

        TextView role = view.findViewById(R.id.adminRole);
        TextView name = view.findViewById(R.id.adminName);
        ImageView image = view.findViewById(R.id.adminImage);
        //TODO: Image

        role.setText(_role);
        name.setText(_name);

        return view;
    }
}