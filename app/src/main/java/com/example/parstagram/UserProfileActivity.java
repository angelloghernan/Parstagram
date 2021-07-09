package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parstagram.wrappers.UserWrapper;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;


// Note: this activity is quite messy as of now, there's code I would definitely refactor if I were working on a
// bigger project. Mostly this is because of reused code that could be separated into other classes, otherwise it's manageable.
public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    Button btnLogOut;
    Button btnFollow;
    TextView tvName;
    TextView tvBio;
    TextView tvNumPosts;
    TextView tvNumFollowers;
    TextView tvNumFollowing;
    ImageView ivProfileDetailsPicture;
    UserWrapper user;
    Context context;
    FollowerTableObject followEntry;
    int followerCount = 0;
    int followsCount = 0;
    boolean following = false;
    boolean followsQueryFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_profile);
        context = this;

        btnLogOut = findViewById(R.id.btnLogOut);
        btnFollow = findViewById(R.id.btnEditProfile);
        ivProfileDetailsPicture = findViewById(R.id.ivProfileDetailsPicture);
        tvNumFollowers = findViewById(R.id.tvNumFollowers);
        tvNumFollowing = findViewById(R.id.tvNumFollowing);
        tvNumPosts = findViewById(R.id.tvNumPosts);
        tvName = findViewById(R.id.tvName);
        tvBio = findViewById(R.id.tvBio);

        btnLogOut.setVisibility(View.GONE);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        ParseUser displayedUser = ParseUser.createWithoutData(ParseUser.class, user.objectId);

        ParseQuery<FollowerTableObject> followsQuery = ParseQuery.getQuery(FollowerTableObject.class);
        followsQuery.whereEqualTo("followed", displayedUser);
        followsQuery.whereEqualTo("follower", ParseUser.getCurrentUser());

        btnFollow.setText("");

        followsQuery.findInBackground(new FindCallback<FollowerTableObject>() {
            @Override
            public void done(List<FollowerTableObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        following = true;
                        followsQueryFinished = true;
                        btnFollow.setText("Unfollow");
                        return;
                    }
                    followsQueryFinished = true;
                    btnFollow.setText("Follow");
                    return;
                }
                Log.e(TAG, e.toString());
            }
        });

        tvName.setText(user.name);
        tvBio.setText(user.bio);

        if (user.profilePictureUrl.equals("null")) {
            Glide.with(this)
                    .load(ResourcesCompat.getDrawable(this.getResources(),
                            R.drawable.instagram_profile_default, this.getTheme()))
                    .circleCrop()
                    .into(ivProfileDetailsPicture);
        } else {
            Glide.with(this)
                    .load(user.profilePictureUrl)
                    .circleCrop()
                    .into(ivProfileDetailsPicture);
        }

        // Note: for now, the following code is copy-pasted from MyProfileFragment
        // I wanted to make each one its own individual function so I wouldn't have to rewrite it twice,
        // however I couldn't find a suitable way to do so,
        // as it would require a lot of multithreading work that I don't think I'm ready for (main hangup is being unable to
        // edit views within runnables)

        ParseQuery<FollowerTableObject> followerQuery = ParseQuery.getQuery(FollowerTableObject.class);
        followsQuery = ParseQuery.getQuery(FollowerTableObject.class);

        // Query for followers using async count
        followerQuery.whereEqualTo(FollowerTableObject.KEY_FOLLOWED, displayedUser);
        followerQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    followerCount = count;
                    tvNumFollowers.setText(String.format(Locale.ENGLISH, "%d\nFollowers", count));
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });

        // Query for follows using async count
        followsQuery.whereEqualTo(FollowerTableObject.KEY_FOLLOWER, displayedUser);
        followsQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    followsCount = count;
                    tvNumFollowing.setText(String.format(Locale.ENGLISH, "%d\nFollowing", count));
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });

        // Query for number of posts using async callback
        ParseQuery<Post> postParseQuery = ParseQuery.getQuery(Post.class);
        postParseQuery.whereEqualTo(Post.KEY_USER, displayedUser);
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

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followClickHandler();
            }
        });

    }

    // Stuff to do when the follow button is clicked
    public void followClickHandler() {
        // If the check to see if the user is following is finished and they are not following,
        // make a new entry in the follow table so that the user is now following
        // otherwise, if they are following, then delete the table entry so that they can unfollow

        ParseUser displayedUser = ParseUser.createWithoutData(ParseUser.class, user.objectId);

        if (followsQueryFinished && !following) {
            FollowerTableObject followEntry = new FollowerTableObject();
            followEntry.setFollower(ParseUser.getCurrentUser());
            followEntry.setFollowed(displayedUser);

            followEntry.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        btnFollow.setText("Unfollow");
                        Toast.makeText(context, "User followed!", Toast.LENGTH_SHORT).show();
                        // Temporary way to update follower count, should be redone later
                        followerCount++;
                        tvNumFollowers.setText(String.format(Locale.ENGLISH, "%d\nFollowers", followerCount));

                        following = true;
                    } else {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        } else if (followsQueryFinished) {
            ParseQuery<FollowerTableObject> unfollowQuery = ParseQuery.getQuery(FollowerTableObject.class);
            unfollowQuery.whereEqualTo("followed", displayedUser);
            unfollowQuery.whereEqualTo("follower", ParseUser.getCurrentUser());
            unfollowQuery.getFirstInBackground(new GetCallback<FollowerTableObject>() {
                @Override
                public void done(FollowerTableObject object, ParseException e) {
                    try {
                        object.delete();
                        object.saveInBackground();
                        Toast.makeText(context, "User unfollowed", Toast.LENGTH_SHORT).show();
                        // Temporary way to update follower count, should be redone later
                        followerCount--;
                        tvNumFollowers.setText(String.format(Locale.ENGLISH, "%d\nFollowers", followerCount));

                        following = false;
                        btnFollow.setText("Follow");
                    } catch (ParseException parseException) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }
}