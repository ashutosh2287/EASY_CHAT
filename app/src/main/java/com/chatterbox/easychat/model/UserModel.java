package com.chatterbox.easychat.model;

import com.chatterbox.easychat.utils.AndroidUtil;
import com.chatterbox.easychat.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import android.content.Context;

import java.util.Objects;

public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String profilePic;  // Profile picture URL

    // Default constructor
    public UserModel() {
    }

    // Constructor with user details
    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.profilePic = "profilePics/default_profile_pic.png"; // Default profile picture
    }

    // Getters and Setters
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    // Method to update profile picture in Firestore
    public void updateProfilePictureInFirestore(Context context, String imageUrl) {
        this.profilePic = imageUrl; // Update profile picture URL

        // Update Firestore with the new profile picture URL
        Objects.requireNonNull(FirebaseUtil.currentUserDetails()).set(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(context, "Profile picture updated successfully");
                    } else {
                        AndroidUtil.showToast(context, "Failed to update profile picture");
                    }
                });
    }
}
