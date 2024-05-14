package com.jessm.ntusocietybrowser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationItemFragment extends Fragment {

    private static final String ARG_NOTIFICATION_TITLE = "notificationTitle";
    private static final String ARG_NOTIFICATION_CONTENT = "notificationContent";
    private static final String ARG_NOTIFICATION_IMAGE = "notificationImage";
    private static final String ARG_NOTIFICATION_TIME = "notificationTime";
    private static final String ARG_NOTIFICATION_READ = "notificationRead";

    private String _notificationTitle;
    private String _notificationContent;
    private String _notificationImage;
    private int _notificationTime;
    private boolean _notificationRead;

    public NotificationItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param notificationTitle The title of the notification.
     * @param notificationContent The content displayed on the notification.
     * @param notificationImage The society image displayed on the notification.
     * @param notificationTime The time since the notification has been received
     * @param notificationRead If the notification has been read or not;
     * @return A new instance of fragment NotificationItemFragment.
     */
    public static NotificationItemFragment newInstance(String notificationTitle, String notificationContent, String notificationImage, int notificationTime, boolean notificationRead) {
        NotificationItemFragment fragment = new NotificationItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOTIFICATION_TITLE, notificationTitle);
        args.putString(ARG_NOTIFICATION_CONTENT, notificationContent);
        args.putString(ARG_NOTIFICATION_IMAGE, notificationImage);
        args.putInt(ARG_NOTIFICATION_TIME, notificationTime);
        args.putBoolean(ARG_NOTIFICATION_READ, notificationRead);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _notificationTitle = getArguments().getString(ARG_NOTIFICATION_TITLE);
            _notificationContent = getArguments().getString(ARG_NOTIFICATION_CONTENT);
            _notificationImage = getArguments().getString(ARG_NOTIFICATION_IMAGE);
            _notificationTime = getArguments().getInt(ARG_NOTIFICATION_TIME);
            _notificationRead = getArguments().getBoolean(ARG_NOTIFICATION_READ);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_item, container, false);

        TextView notificationTitle = view.findViewById(R.id.societyName);
        notificationTitle.setText(_notificationTitle);

        TextView notificationDesc = view.findViewById(R.id.notificationDesc);
        notificationDesc.setText(_notificationContent);

        ImageView notificationImage = view.findViewById(R.id.adminImage);
//        resultImage.setImageResource(_notificationImage);

        TextView notificationTime = view.findViewById(R.id.notificationTime);

        String setString;
        int actualTime = _notificationTime;

        // If longer than a day
        if (actualTime > 86400) setString = String.format(Locale.ENGLISH, "%dd", actualTime / 86400);
        // If longer than an hour
        else if (actualTime > 3600) setString = String.format(Locale.ENGLISH, "%dh", ((actualTime % 86400) / 60) / 60);
        // If longer than a minute
        else if (actualTime > 60) setString = String.format(Locale.ENGLISH, "%dm", (actualTime % 86400) / 60);
        // If less than a minute
        else setString = String.format(Locale.ENGLISH, "%ds", actualTime);

        notificationTime.setText(setString);

        FrameLayout layout = view.findViewById(R.id.notification);
        if (!_notificationRead) layout.setBackgroundColor(getResources().getColor(R.color.md_theme_surfaceContainer, requireActivity().getTheme()));

        return view;
    }
}