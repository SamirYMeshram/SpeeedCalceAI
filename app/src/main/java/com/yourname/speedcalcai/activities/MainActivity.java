package com.yourname.speedcalcai.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.fragments.AccountFragment;
import com.yourname.speedcalcai.fragments.DashboardFragment;
import com.yourname.speedcalcai.fragments.PracticeFragment;
import com.yourname.speedcalcai.fragments.QuizFragment;
import com.yourname.speedcalcai.fragments.RevisionFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottom = findViewById(R.id.bottomNavigation);
        if (savedInstanceState == null) load(new PracticeFragment());
        bottom.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_practice) load(new PracticeFragment());
            else if (id == R.id.nav_revision) load(new RevisionFragment());
            else if (id == R.id.nav_quiz) load(new QuizFragment());
            else if (id == R.id.nav_dashboard) load(new DashboardFragment());
            else if (id == R.id.nav_account) load(new AccountFragment());
            return true;
        });
    }

    private void load(@NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
