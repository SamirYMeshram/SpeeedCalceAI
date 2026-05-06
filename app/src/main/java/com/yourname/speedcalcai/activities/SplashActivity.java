package com.yourname.speedcalcai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.yourname.speedcalcai.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            boolean loggedIn = prefs.getBoolean("loggedIn", false);
            startActivity(new Intent(this, loggedIn ? MainActivity.class : LoginActivity.class));
            finish();
        }, 1000L);
    }
}
