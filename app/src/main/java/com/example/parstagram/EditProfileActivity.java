package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.parstagram.wrappers.UserWrapper;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    public static final int SELECT_PHOTO_REQUEST_CODE = 101;
    ImageView ivEditProfileView;
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

        // When the submit button is pressed, load the information from the text input boxes
        // and send the new data to the server
        btnEditSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newBio = etEditBio.getText().toString();
                String newUsername = etEditUsername.getText().toString();
                String newName = etEditName.getText().toString();
                ParseUser currentUser = ParseUser.getCurrentUser();

                currentUser.setUsername(newUsername);
                currentUser.put("bio", newBio);
                currentUser.put("name", newName);

                try {
                    currentUser.save();
                    Log.i(TAG, "user info saved successfully");
                    user.username = currentUser.getUsername();
                    user.bio = newBio;
                    user.name = newName;
                    Intent intent = new Intent();
                    intent.putExtra("user", Parcels.wrap(user));
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (ParseException e) {
                    Log.e(TAG, e.toString());
                    finish();
                }


            }
        });

    }
}