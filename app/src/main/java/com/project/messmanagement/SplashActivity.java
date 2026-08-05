package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logo = findViewById(R.id.ivLogo);
        View name = findViewById(R.id.tvAppName);

        // 1. Zoom In Animation
        ScaleAnimation zoomIn = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomIn.setDuration(1000);
        logo.startAnimation(zoomIn);

        // 2. Fade In Animation
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1.0f);
        fadeIn.setDuration(1200);
        name.startAnimation(fadeIn);

        // 3. Navigate to Login after delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 2200);
    }
}
