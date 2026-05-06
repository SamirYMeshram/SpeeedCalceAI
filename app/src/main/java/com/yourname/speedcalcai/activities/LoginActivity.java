package com.yourname.speedcalcai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yourname.speedcalcai.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btn = findViewById(R.id.btnPrimary);
        TextView switchAuth = findViewById(R.id.tvSwitchAuth);
        etName.setVisibility(View.GONE);
        btn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty() || etPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            prefs.edit().putBoolean("loggedIn", true).putString("email", email).putString("name", email.split("@")[0]).apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        switchAuth.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
