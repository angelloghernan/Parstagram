package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.parstagram.wrappers.UserWrapper;

import org.parceler.Parcels;

public class EditProfileActivity extends AppCompatActivity {

    ImageView ivEditProfileView;
    Button btnEditProfilePicture;
    Button btnEditSubmit;
    EditText etEditName;
    EditText etEditUsername;
    EditText etEditBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        UserWrapper user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        ivEditProfileView = findViewById(R.id.ivEditProfileView);
        btnEditProfilePicture = findViewById(R.id.btnEditProfilePicture);
        btnEditSubmit = findViewById(R.id.btnEditSubmit);
        etEditName = findViewById(R.id.etEditName);
        etEditUsername = findViewById(R.id.etEditUsername);
        etEditBio = findViewById(R.id.etEditBio);

        etEditName.setText(user.name);
        etEditUsername.setText(user.username);
        etEditBio.setText(user.bio);

        if (user.profilePictureUrl.equals("null")) {
            Glide.with(this)
                    .load(ContextCompat.getDrawable(this, R.drawable.instagram_profile_default))
                    .circleCrop()
                    .into(ivEditProfileView);
        } else {
            Glide.with(this)
                    .load(user.profilePictureUrl)
                    .into(ivEditProfileView);
        }

    }
}