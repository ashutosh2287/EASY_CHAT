package com.chatterbox.easychat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chatterbox.easychat.model.UserModel;
import com.chatterbox.easychat.utils.AndroidUtil;
import com.chatterbox.easychat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUserModel;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));

        logoutBtn.setOnClickListener((v) -> {
            FirebaseUtil.logout();
            Intent intent = new Intent(getContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            uploadProfilePicToFirebase(selectedImageUri);
                        }
                    }
                });

        profilePic.setOnClickListener(v -> {
            openImagePicker();
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadProfilePicToFirebase(Uri imageUri) {
        setInProgress(true);

        // Get a reference to Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageReference.child("profilePics/" + currentUserModel.getUserId() + ".jpg");

        // Upload the image
        profilePicRef.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                profilePicRef.getDownloadUrl().addOnCompleteListener(downloadUrlTask -> {
                    if (downloadUrlTask.isSuccessful()) {
                        String imageUrl = downloadUrlTask.getResult().toString();
                        updateProfilePictureInFirestore(imageUrl);
                    } else {
                        AndroidUtil.showToast(getContext(), "Failed to get image URL");
                        setInProgress(false);
                    }
                });
            } else {
                AndroidUtil.showToast(getContext(), "Image upload failed");
                setInProgress(false);
            }
        });
    }

    private void updateProfilePictureInFirestore(String imageUrl) {
        currentUserModel.setProfilePic(imageUrl);
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Profile picture updated successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Failed to update profile picture");
                    }
                });
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);
        updateToFirestore();
    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Updated successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Update failed");
                    }
                });
    }

    void getUserData() {
        setInProgress(true);

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            if (currentUserModel != null) {
                usernameInput.setText(currentUserModel.getUsername());
                phoneInput.setText(currentUserModel.getPhone());

                // Load profile picture from Firestore or use default
                if (currentUserModel.getProfilePic() != null) {
                    // Load image using a library like Glide or Picasso
                    Glide.with(getContext())
                            .load(currentUserModel.getProfilePic())
                            .into(profilePic);
                }
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    public void loadOtherUserProfile(String userId) {
        // Fetch user details from Firestore using the user's ID
        FirebaseUtil.allUserCollectionReference().document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        UserModel user = task.getResult().toObject(UserModel.class);
                        if (user != null) {
                            // Get the profile picture URL
                            String profilePicUrl = user.getProfilePic();

                            // Load it using Glide into the ImageView
                            Glide.with(requireContext())
                                    .load(profilePicUrl)  // URL of the profile pic
                                    .placeholder(R.drawable.default_profile_pic)  // Optional placeholder
                                    .into(profilePic);  // ImageView where you want to display the profile pic
                        }
                    } else {
                        AndroidUtil.showToast(getContext(), "Failed to load user data");
                    }
                });
    }

}
