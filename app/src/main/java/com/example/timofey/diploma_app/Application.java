package com.example.timofey.diploma_app;

import android.content.Intent;

import com.example.timofey.diploma_app.activities.MainActivity;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by timofey on 30.01.2016.
 */


    public class Application extends android.app.Application {
        VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
            @Override
            public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
                if (newToken == null) {
// VKAccessToken is invalid
                    Intent intent = new Intent(Application.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };


        @Override
        public void onCreate() {
            super.onCreate();
            vkAccessTokenTracker.startTracking();
            VKSdk.initialize(this);
        }
    }


