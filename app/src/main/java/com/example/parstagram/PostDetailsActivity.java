package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


// Note that this activity is quite redundant since I have copied Instagram's
// timeline layout, however it is listed as a requirement for the assignment.
public class PostDetailsActivity extends AppCompatActivity {

    ImageView ivProfilePicture;
    TextView tvUsername;
    ImageView ivPostImage;
    ImageButton ibLike;
    ImageButton ibReply;
    ImageButton ibDM;
    TextView tvDescriptionUsername;
    TextView tvDescription;
    TextView tvTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // unfortunately, Parcel doesn't work with Parse classes so I am doing this manually.
        // Technically, I could create a class to hold the same data, but this
        // activity is low priority (redundant) as noted above
        String username = getIntent().getStringExtra("username");
        String description = getIntent().getStringExtra("description");
        String profilePictureURL = getIntent().getStringExtra("profilePictureURL");
        String imageURL = getIntent().getStringExtra("imageURL");
        String timestamp = getIntent().getStringExtra("timestamp");

        ivProfilePicture = findViewById(R.id.ivDetailsProfilePicture);
        tvUsername = findViewById(R.id.tvDetailsUsername);
        ivPostImage = findViewById(R.id.ivDetailsPostImage);
        ibLike = findViewById(R.id.ibDetailsLike);
        ibReply = findViewById(R.id.ibDetailsReply);
        ibDM = findViewById(R.id.ibDetailsDM);
        tvDescriptionUsername = findViewById(R.id.tvDetailsDescriptionUsername);
        tvDescription = findViewById(R.id.tvDetailsDescription);
        tvTimestamp = findViewById(R.id.tvDetailsTimestamp);

        Glide.with(this)
                .load(imageURL)
                .into(ivPostImage);

        if (profilePictureURL.equals("null")) {
            Glide.with(this)
                    .load(ResourcesCompat.getDrawable(this.getResources(),
                            R.drawable.instagram_profile_default, this.getTheme()))
                    .circleCrop()
                    .into(ivProfilePicture);
        } else {
            Glide.with(this)
                    .load(profilePictureURL)
                    .circleCrop()
                    .into(ivProfilePicture);
        }

        tvUsername.setText(username);
        tvDescriptionUsername.setText(username);
        tvDescription.setText(description);
        tvTimestamp.setText(timestamp);
    }
}