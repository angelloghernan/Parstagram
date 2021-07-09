package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parstagram.wrappers.PostWrapper;
import com.example.parstagram.wrappers.UserWrapper;
import com.parse.ParseUser;

import org.parceler.Parcels;


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

        UserWrapper user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        PostWrapper post = Parcels.unwrap(getIntent().getParcelableExtra("post"));

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
                .load(post.imageUrl)
                .into(ivPostImage);

        if (user.profilePictureUrl.equals("null")) {
            Glide.with(this)
                    .load(ResourcesCompat.getDrawable(this.getResources(),
                            R.drawable.instagram_profile_default, this.getTheme()))
                    .circleCrop()
                    .into(ivProfilePicture);
        } else {
            Glide.with(this)
                    .load(user.profilePictureUrl)
                    .circleCrop()
                    .into(ivProfilePicture);
        }

        tvUsername.setText(user.username);
        tvDescriptionUsername.setText(user.username);
        tvDescription.setText(post.description);
        tvTimestamp.setText(post.createdAt);
    }
}