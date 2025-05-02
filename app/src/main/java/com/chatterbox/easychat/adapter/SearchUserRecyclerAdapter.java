package com.chatterbox.easychat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatterbox.easychat.ChatActivity;
import com.chatterbox.easychat.R;
import com.chatterbox.easychat.model.ChatroomModel;
import com.chatterbox.easychat.model.UserModel;
import com.chatterbox.easychat.utils.AndroidUtil;
import com.chatterbox.easychat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Objects;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        String username = model.getUsername();

        if (FirebaseUtil.currentUserId() != null && Objects.equals(FirebaseUtil.currentUserId(), model.getUserId())) {
            username += " (Me)";
        }

        holder.usernameText.setText(username);
        holder.phoneText.setText(model.getPhone());

        // ✅ Load profile picture
        Glide.with(context)
                .load(model.getProfilePic())
                .placeholder(R.drawable.default_profile_pic)
                .error(R.drawable.default_profile_pic)
                .into(holder.profilePic);

        holder.itemView.setOnClickListener(v -> {
            String currentUserId = FirebaseUtil.currentUserId();
            if (currentUserId == null || model.getUserId() == null) {
                Log.e("SearchAdapter", "User IDs are null, cannot create chat");
                return;
            }

            String chatroomId = FirebaseUtil.getChatroomId(currentUserId, model.getUserId());

            ChatroomModel chatroomModel = new ChatroomModel(
                    chatroomId,
                    java.util.Arrays.asList(currentUserId, model.getUserId()),
                    null,
                    ""
            );

            chatroomModel.setOtherUserName(model.getUsername());
            chatroomModel.setOtherUserProfilePic(model.getProfilePic());

            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)
                    .addOnSuccessListener(unused -> Log.d("Chatroom", "Chatroom created/updated"))
                    .addOnFailureListener(e -> Log.e("Chatroom", "Failed to create chatroom", e));

            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }



    @NonNull
    @Override
    public  UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
        return new UserModelViewHolder(view);
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}