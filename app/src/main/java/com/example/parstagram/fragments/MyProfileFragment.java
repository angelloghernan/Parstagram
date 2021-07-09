package com.example.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parstagram.BitmapScaler;
import com.example.parstagram.EditProfileActivity;
import com.example.parstagram.FollowerTableObject;
import com.example.parstagram.LoginActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.example.parstagram.wrappers.UserWrapper;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "MyProfileFragment";
    public static final String KEY_NAME = "name";
    public static final String KEY_BIO = "bio";

    public static final int EDIT_PROFILE_REQUEST_CODE = 99;

    private String mParam1;
    private String mParam2;

    Button btnLogOut;
    Button btnEditProfile;
    ImageView ivProfileDetailsPicture;
    TextView tvNumFollowers;
    TextView tvNumFollowing;
    TextView tvNumPosts;
    TextView tvName;
    TextView tvBio;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    // function for creating a new instance of this fragment, unused boilerplate
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        ivProfileDetailsPicture = view.findViewById(R.id.ivProfileDetailsPicture);
        tvNumFollowers = view.findViewById(R.id.tvNumFollowers);
        tvNumFollowing = view.findViewById(R.id.tvNumFollowing);
        tvNumPosts = view.findViewById(R.id.tvNumPosts);
        tvName = view.findViewById(R.id.tvName);
        tvBio = view.findViewById(R.id.tvBio);

        ParseUser user = ParseUser.getCurrentUser();
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    tvBio.setText(user.getString(KEY_BIO));
                    tvName.setText(user.getString(KEY_NAME));
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });

        ParseFile profilePicture = user.getParseFile("profilePicture");
        // if their profile picture doesn't exist, load the default "empty" profile picture
        // else, load their profile picture
        if (profilePicture != null) {
            Glide.with(view.getContext())
                    .load(Objects.requireNonNull(profilePicture.getUrl()))
                    .placeholder(ResourcesCompat.getDrawable(view.getResources(),
                            R.drawable.instagram_profile_default, view.getContext().getTheme()))
                    .circleCrop()
                    .into(ivProfileDetailsPicture);
        } else {
            Glide.with(view.getContext())
                    .load(ResourcesCompat.getDrawable(view.getResources(),
                            R.drawable.instagram_profile_default, view.getContext().getTheme()))
                    .circleCrop()
                    .into(ivProfileDetailsPicture);
        }

        // Query the follower table for follows, followers, and number of posts
        // Normally, I would make separate columns in the user database for the number of followers
        // and follows they have, but I lack the expertise to know how to update that on the server itself.
        // Plus, this gave me more practice with queries
        ParseQuery<FollowerTableObject> followerQuery = ParseQuery.getQuery(FollowerTableObject.class);
        ParseQuery<FollowerTableObject> followsQuery = ParseQuery.getQuery(FollowerTableObject.class);

        // Query for followers using async count
        followerQuery.whereEqualTo(FollowerTableObject.KEY_FOLLOWED, user);
        followerQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    tvNumFollowers.setText(String.format(Locale.ENGLISH, "%d\nFollowers", count));
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });

        // Query for follows using async count
        followsQuery.whereEqualTo(FollowerTableObject.KEY_FOLLOWER, user);
        followsQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    tvNumFollowing.setText(String.format(Locale.ENGLISH, "%d\nFollowing", count));
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });

        // Query for number of posts using async callback
        ParseQuery<Post> postParseQuery = ParseQuery.getQuery(Post.class);
        postParseQuery.whereEqualTo(Post.KEY_USER, user);
        postParseQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    tvNumPosts.setText(String.format(Locale.ENGLISH, "%d\nPosts", count));
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), EditProfileActivity.class);

                // Wrap user info so it can be parceled and passed to intent
                String profilePictureURL = "null";
                if (user.getParseFile("profilePicture") != null) {
                    profilePictureURL = Objects.requireNonNull(user.getParseFile("profilePicture")).getUrl();
                }
                UserWrapper parcelableUser = new UserWrapper(user.getObjectId(), user.getUsername(),
                        profilePictureURL);
                parcelableUser.bio = user.getString("bio");
                parcelableUser.name = user.getString("name");

                // Finally, put the user in the intent and begin the activity
                i.putExtra("user", Parcels.wrap(parcelableUser));
                startActivityForResult(i, EDIT_PROFILE_REQUEST_CODE);
            }
        });

        // Logout user and go back to log in activity when log out button is clicked
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(view.getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            UserWrapper userWrapper = Parcels.unwrap(data.getParcelableExtra("user"));
            tvBio.setText(userWrapper.bio);
            tvName.setText(userWrapper.name);
        }
    }

}