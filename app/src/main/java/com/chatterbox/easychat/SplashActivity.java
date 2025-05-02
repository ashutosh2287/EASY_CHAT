package com.chatterbox.easychat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.chatterbox.easychat.model.UserModel;
import com.chatterbox.easychat.utils.AndroidUtil;
import com.chatterbox.easychat.utils.FirebaseUtil;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("userId") != null) {
            // From notification
            String userId = getIntent().getExtras().getString("userId");

            assert userId != null;
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel model = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Firebase read failed (optional error handling)
                            startDefaultFlow();
                        }
                    });

        } else {
            startDefaultFlow();
        }
    }

    private void startDefaultFlow() {
        new Handler().postDelayed(() -> {
            if (FirebaseUtil.isLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
            }
            finish();
        }, 1000);
    }
}
