package com.jessm.ntusocietybrowser;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePostFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POST_TITLE = "postTitle";
    private static final String ARG_POST_SUBTEXT = "postSubtext";
    private static final String ARG_POST_CONTENT = "postContent";
    private static final String ARG_POST_IMAGE = "postImage";
    private static final String ARG_POST_TYPE = "postType";
    private static final String ARG_POST_DATE = "postDate";

    private String _postTitle;
    private String _postSubtext;
    private String _postContent;
    private String _postImage;
    private HomePostType _postType;
    private Date _postDate;

    public HomePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post The post that will be displayed;
     * @return A new instance of fragment HomePostFragment.
     */
    public static HomePostFragment newInstance(HomePost post) {
        HomePostFragment fragment = new HomePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_TITLE, post.getTitle());
        args.putString(ARG_POST_SUBTEXT, post.getSubtitle());
        args.putString(ARG_POST_CONTENT, post.getContent());
        args.putString(ARG_POST_IMAGE, post.getImage());
        args.putSerializable(ARG_POST_TYPE, post.getType());
        args.putLong(ARG_POST_DATE, post.getDate().getTime());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _postTitle = getArguments().getString(ARG_POST_TITLE);
            _postSubtext = getArguments().getString(ARG_POST_SUBTEXT);
            _postContent = getArguments().getString(ARG_POST_CONTENT);
            _postImage = getArguments().getString(ARG_POST_IMAGE);
            _postType = (HomePostType)getArguments().getSerializable(ARG_POST_TYPE);
            _postDate = new Date(getArguments().getLong(ARG_POST_DATE));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_post, container, false);

        TextView postTitle = view.findViewById(R.id.postTitle);
        postTitle.setText(_postTitle);

        ImageView alert = view.findViewById(R.id.postAlert);
        TextView postInfo = view.findViewById(R.id.postInfo);
        Button calendarButton = view.findViewById(R.id.calendarButton);

        /* Give some different views and styling depending on the type of post.
         * Default: No special styling.
         * Important: Make the post title colorError coloured and add an exclamation mark to the post.
         *            This gives the post priority in the home feed.
         * Dated: This post has a "time left" count in the corner of the post, useful for deadlines.
         * Calendar: This post has a given date, and an "Add to Calendar" button. Used for events and gauging interest.
        */
        switch (_postType) {
            case Important:
                postTitle.setTextColor(getResources().getColor(R.color.md_theme_error, requireActivity().getTheme()));
                alert.setVisibility(View.VISIBLE);
                break;
            case Calendar:
                postInfo.setVisibility(View.VISIBLE);

                String datePattern = "dd/MM/yyyy";
                DateFormat df = new SimpleDateFormat(datePattern, Locale.ENGLISH);
                Date date = new Date(_postDate.getTime());
                postInfo.setText(df.format(date));

                calendarButton.setVisibility(View.VISIBLE);
                break;
            case Dated:
                postInfo.setVisibility(View.VISIBLE);
                double dateDifference = (double) ((_postDate.getTime() - new Date().getTime()) / 1000) / 86400;
                postInfo.setText(String.format(Locale.ENGLISH, "%dd", Math.round(dateDifference)));
                break;
            case Default: default: break;
        }

        TextView postSubtext = view.findViewById(R.id.postSubtext);
        postSubtext.setText(_postSubtext);

        TextView postContent = view.findViewById(R.id.postContent);
        postContent.setText(_postContent);

        Button viewMoreButton = view.findViewById(R.id.viewMoreButton);

        // Check the content of the post, if it is too long for the short post, add a "View More" button that displays the rest of the text.
        ViewTreeObserver contentObserver = postContent.getViewTreeObserver();
        contentObserver.addOnGlobalLayoutListener(() -> {
            if (postContent.getLayout().getEllipsisCount(postContent.getLineCount() - 1) > 0) {
                viewMoreButton.setVisibility(View.VISIBLE);
            }
        });

        ImageView postImage = view.findViewById(R.id.postImage);
        if (!_postImage.isEmpty()) {
            //TODO: Set image resource from uploaded post data
//            postImage.setImageResource(R.drawable.ic_launcher_background);
            postImage.setClipToOutline(true);
            postImage.setVisibility(View.VISIBLE);
        }

        // When clicking the "View More" button, depending on the state of the post content, either expand the post or contract it
        viewMoreButton.setOnClickListener(l -> {
            if (postContent.getMaxLines() == 3) {
                ObjectAnimator animation = ObjectAnimator.ofInt(postContent, "maxLines", 10);
                animation.setDuration(100).start();
                viewMoreButton.setText(getResources().getString(R.string.view_less));
            }
            else {
                ObjectAnimator animation = ObjectAnimator.ofInt(postContent, "maxLines", 3);
                animation.setDuration(100).start();
                viewMoreButton.setText(getResources().getString(R.string.view_more));
            }
        });

        return view;
    }
}