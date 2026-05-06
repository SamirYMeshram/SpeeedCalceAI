package com.yourname.speedcalcai.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.activities.LoginActivity;
import com.yourname.speedcalcai.activities.SettingsActivity;
import com.yourname.speedcalcai.database.AppDatabase;
import com.yourname.speedcalcai.models.Attempt;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountFragment extends Fragment {
    private TextView tvName, tvEmail, tvLevel, tvInsights;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvInsights = view.findViewById(R.id.tvInsights);
        Button settings = view.findViewById(R.id.btnSettings);
        Button signOut = view.findViewById(R.id.btnSignOut);
        SharedPreferences prefs = requireContext().getSharedPreferences("auth", 0);
        tvName.setText(prefs.getString("name", "Student"));
        tvEmail.setText(prefs.getString("email", "offline@student.com"));
        settings.setOnClickListener(v -> startActivity(new Intent(requireContext(), SettingsActivity.class)));
        signOut.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        loadStats();
        return view;
    }

    private void loadStats() {
        executor.execute(() -> {
            List<Attempt> attempts = AppDatabase.getInstance(requireContext()).attemptDao().getAllAttempts();
            int total = 0;
            for (Attempt a : attempts) total += a.correctCount;
            String level = level(total);
            String insights = "Learning Insights\nWeekly: " + total + " correct answers\nDaily Quiz: 0 attempts\nWorkout: 0 sessions\nBattles: 0/0";
            if (getActivity() != null) getActivity().runOnUiThread(() -> {
                tvLevel.setText(level + "  |  " + total + " pts");
                tvInsights.setText(insights);
            });
        });
    }

    private String level(int points) {
        if (points < 100) return "Beginner";
        if (points < 500) return "Learner";
        if (points < 1000) return "Fast Solver";
        if (points < 5000) return "Expert";
        return "Master";
    }
}
