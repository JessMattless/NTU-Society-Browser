package com.jessm.ntusocietybrowser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocietyPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocietyPostFragment extends DialogFragment {

    private static final String ARG_SOCIETY_ID = "societyId";

    private String _societyId;

    FirebaseFirestore db;

    public SocietyPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param societyId The id of the society making the post.
     * @return A new instance of fragment SocietyPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocietyPostFragment newInstance(String societyId) {
        SocietyPostFragment fragment = new SocietyPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SOCIETY_ID, societyId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            _societyId = getArguments().getString(ARG_SOCIETY_ID);
        }

        db = FirebaseFirestore.getInstance();

        // Shown dialogue, if the user confirms then they are logged out, if not the dialogue is closed.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_society_post, null);

        Button confirmButton = view.findViewById(R.id.confirmButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        Calendar calendar = Calendar.getInstance();
        CalendarView calendarView = view.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener((calendarView1, year, month, day) -> calendar.set(year, month, day));

        confirmButton.setOnClickListener(view1 -> {
            EditText title = view.findViewById(R.id.postTitle);
            EditText content = view.findViewById(R.id.postContent);

            RadioButton defaultButton = view.findViewById(R.id.defaultButton);
            RadioButton importantButton = view.findViewById(R.id.importantButton);
            RadioButton datedButton = view.findViewById(R.id.datedButton);
            RadioButton calendarButton = view.findViewById(R.id.calendarButton);

            Timestamp selectedDate = new Timestamp(calendar.getTimeInMillis() / 1000, 0);

            Map<String, Object> data = new HashMap<>();
            data.put("content", content.getText().toString());
            data.put("date", selectedDate);
            data.put("timestamp", Timestamp.now());
            data.put("title", title.getText().toString());

            if (defaultButton.isChecked()) data.put("type", "Default");
            else if (importantButton.isChecked()) data.put("type", "Important");
            else if (datedButton.isChecked()) data.put("type", "Dated");
            else if (calendarButton.isChecked()) data.put("type", "Calendar");

            //TODO: Image
            data.put("image", "");

            ArrayList<String> subbedMembers = new ArrayList<>();
            db.collection("users")
                    .whereArrayContains("subscribed societies", _societyId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                subbedMembers.add(document.getId());
                            }

                            data.put("subscribed members", subbedMembers);

                            db.collection("societies")
                                    .document(_societyId)
                                    .get()
                                    .addOnSuccessListener(socDocument -> {
                                        data.put("subtitle", socDocument.getString("title"));

                                        db.collection("societies")
                                                .document(_societyId)
                                                .collection("Posts")
                                                .add(data);
                                    });
                        }
                    });

            this.dismiss();
        });
        cancelButton.setOnClickListener(view2 -> {
            this.dismiss();
        });

        builder.setView(view)
                .setMessage(R.string.create_post);


        return builder.create();
    }
}