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
import com.chatterbox.easychat.ProfilePicViewActivity;  // Import ProfilePicViewActivity
import com.chatterbox.easychat.R;
import com.chatterbox.easychat.model.ChatroomModel;
import com.chatterbox.easychat.model.UserModel;
import com.chatterbox.easychat.utils.AndroidUtil;
import com.chatterbox.easychat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Objects;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        Objects.requireNonNull(FirebaseUtil.getOtherUserFromChatroom(model.getUserIds()))
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        // Step 1: Add a check for null or empty userId
                        assert otherUserModel != null;
                        holder.usernameText.setText(otherUserModel.getUsername());
                        if (lastMessageSentByMe)
                            holder.lastMessageText.setText("You : " + model.getLastMessage());
                        else
                            holder.lastMessageText.setText(model.getLastMessage());
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        // Step 2: Load profile picture using Glide
                        Glide.with(context)
                                .load(otherUserModel.getProfilePic())
                                .circleCrop() // Optional, to make the profile picture circular
                                .placeholder(R.drawable.default_profile_pic)
                                .into(holder.profilePic);

                        // Step 3: Set an OnClickListener to open ProfilePicViewActivity when the profile picture is clicked
                        holder.profilePic.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ProfilePicViewActivity.class);
                            intent.putExtra("imgUrl", otherUserModel.getProfilePic());  // Pass the image URL to the activity
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Ensure it's a new task
                            context.startActivity(intent);  // Start the activity
                        });

                        holder.itemView.setOnClickListener(v -> {
                            // Navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
