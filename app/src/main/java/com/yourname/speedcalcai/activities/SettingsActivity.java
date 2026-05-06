package com.yourname.speedcalcai.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.yourname.speedcalcai.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }
}
