package com.chatterbox.easychat;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilePicViewActivity extends AppCompatActivity {

    private ImageView fullProfilePic;  // ImageView to show full profile picture

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic_view); // Inflate layout

        fullProfilePic = findViewById(R.id.full_profile_pic);  // Get reference to ImageView

        // Get the image URL passed from the previous activity
        String imageUrl = getIntent().getStringExtra("imgUrl");

        // Use Glide to load the image into the ImageView
        Glide.with(this)
                .load(imageUrl)  // Load the image URL
                .into(fullProfilePic);  // Display the image in the ImageView
    }
}
