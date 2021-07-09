package com.example.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.Post;
import com.example.parstagram.PostAdapter;
import com.example.parstagram.PostDetailsActivity;
import com.example.parstagram.R;
import com.example.parstagram.UserProfileActivity;
import com.example.parstagram.wrappers.PostWrapper;
import com.example.parstagram.wrappers.UserWrapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "TimelineActivity";
    protected PostAdapter adapter;
    protected List<Post> allPosts;

    private SwipeRefreshLayout swipeContainer;

    RecyclerView rvTimeline;
    BottomNavigationView bottomNavigationView;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int DETAILS_REQUEST_CODE = 20;
    public static final int PROFILE_REQUEST_CODE = 21;

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // boiler plate code for fragment
        // could delete, but if i ever want to use parameters when creating a new fragment instance
        // i would need this
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvTimeline = view.findViewById(R.id.rvTimeline);
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(view.getContext(), allPosts, callback);

        rvTimeline.setAdapter(adapter);
        rvTimeline.setLayoutManager(new LinearLayoutManager(view.getContext()));

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        queryPosts();
    }

    PostAdapter.AdapterCallback callback = new PostAdapter.AdapterCallback() {
        @Override
        public void onAdapterSelected(int pos, Post post) {
            Intent intent = new Intent(getContext(), PostDetailsActivity.class);
            ParseUser user = post.getUser();

            UserWrapper parcelableUser = new UserWrapper(user.getObjectId(), user.getUsername(),
                    getProfilePictureUrl(user));
            PostWrapper parcelablePost = new PostWrapper(post.getDescription(), post.getObjectId(), post.getImage().getUrl(),
                    user.getObjectId(), Post.calculateTimeAgo(post.getCreatedAt()));

            intent.putExtra("user", Parcels.wrap(parcelableUser));
            intent.putExtra("post", Parcels.wrap(parcelablePost));

            // Using startActivityForResult if I ever get to implement likes/comments
            // (so we know if a post was liked/commented on immediately)
            startActivityForResult(intent, DETAILS_REQUEST_CODE);
        }

        @Override
        public void onUserSelected(ParseUser user) {
            Intent intent = new Intent(getContext(), UserProfileActivity.class);

            UserWrapper parcelableUser = new UserWrapper(user.getObjectId(), user.getUsername(),
                    getProfilePictureUrl(user));

            // could change later to async callback in UserProfileActivity if it becomes troublesome,
            // currently this takes very little time to execute
            parcelableUser.name = user.getString("name");
            parcelableUser.bio = user.getString("bio");

            intent.putExtra("user", Parcels.wrap(parcelableUser));

            // Using startActivityForResult if I ever get to implement following user
            // (so we can know the user was followed immediately)
            startActivityForResult(intent, PROFILE_REQUEST_CODE);
        }
    };

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.clear();
                adapter.addAll(posts);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    // small helper function to get profile picture url when passing info into intents
    public String getProfilePictureUrl(ParseUser user) {
        String profilePictureURL = "null";
        if (user.getParseFile("profilePicture") != null) {
            profilePictureURL = Objects.requireNonNull(user.getParseFile("profilePicture")).getUrl();
        }
        return profilePictureURL;
    }
}