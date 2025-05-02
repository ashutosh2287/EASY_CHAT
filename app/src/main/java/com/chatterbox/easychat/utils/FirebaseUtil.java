package com.chatterbox.easychat.utils;

import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

public class FirebaseUtil {

    // Returns current user's ID or null if not authenticated
    public static String currentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();  // Correct way to get the UID from the FirebaseUser
        } else {
            Log.e("FirebaseUtil", "User not authenticated");
            return null;
        }
    }

    // Checks if a user is logged in
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    // Returns the current user's details document reference
    public static DocumentReference currentUserDetails() {
        String userId = currentUserId();
        if (userId == null) {
            Log.e("FirebaseUtil", "Cannot get user details, user is not authenticated");
            return null;
        }
        return FirebaseFirestore.getInstance().collection("users").document(userId);
    }

    // Returns the reference to the collection of all users
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // Returns the reference to a specific chatroom
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    // Returns the reference to messages in a chatroom
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    // Generates a chatroom ID based on the user IDs
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1 == null || userId2 == null) {
            Log.e("FirebaseUtil", "User IDs are null when generating chatroomId");
            return null;
        }

        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    // Returns reference to all chatrooms
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    // Gets the other user from a chatroom by the user IDs
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        String currentUserId = FirebaseUtil.currentUserId();
        if (currentUserId == null) {
            Log.e("FirebaseUtil", "Current user is not authenticated, cannot fetch other user");
            return null;
        }
        if (userIds.get(0).equals(currentUserId)) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    // Converts a timestamp to a string (formatted as HH:mm)
    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    // Logs out the current user
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // Returns the storage reference for the current user's profile picture
    public static StorageReference getCurrentUserProfilePicRef() {
        String userId = currentUserId();
        if (userId != null) {
            return FirebaseStorage.getInstance().getReference().child("profile_pics").child(userId + ".jpg");
        } else {
            Log.e("FirebaseUtil", "User not authenticated, cannot get profile pic reference");
            return null;
        }
    }

    // Returns the storage reference for another user's profile picture
    public static StorageReference getOtherUserProfilePicRef(String userId) {
        if (userId != null) {
            return FirebaseStorage.getInstance().getReference().child("profile_pics").child(userId + ".jpg");
        } else {
            Log.e("FirebaseUtil", "User ID is null, cannot get profile pic reference");
            return null;
        }
    }

    // Uploads the profile picture to Firebase Storage and returns the download URL
    public static void uploadProfilePicture(Uri profilePicUri, OnCompleteListener<Uri> listener) {
        StorageReference profilePicRef = getCurrentUserProfilePicRef();
        if (profilePicRef != null) {
            profilePicRef.putFile(profilePicUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return profilePicRef.getDownloadUrl();
                    })
                    .addOnCompleteListener(listener);
        }
    }

    // Updates the profile picture URL in Firestore
    public static void updateProfilePictureInFirestore(String imageUrl, OnCompleteListener<Void> listener) {
        DocumentReference userDocRef = currentUserDetails();
        if (userDocRef != null) {
            userDocRef.update("profilePic", imageUrl)
                    .addOnCompleteListener(listener);
        } else {
            Log.e("FirebaseUtil", "Error updating profile picture, user document is null");
        }
    }
}
