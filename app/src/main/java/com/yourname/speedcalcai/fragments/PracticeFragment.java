package com.yourname.speedcalcai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.activities.AiReportActivity;
import com.yourname.speedcalcai.adapters.ModuleAdapter;
import com.yourname.speedcalcai.database.AppDatabase;
import com.yourname.speedcalcai.models.Attempt;
import com.yourname.speedcalcai.models.ModuleItem;
import com.yourname.speedcalcai.utils.Constants;
import com.yourname.speedcalcai.utils.ScoreCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PracticeFragment extends Fragment {
    private TextView tvTodayStats;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice, container, false);
        tvTodayStats = view.findViewById(R.id.tvTodayStats);
        Button ai = view.findViewById(R.id.btnAiReport);
        RecyclerView recycler = view.findViewById(R.id.recyclerModules);
        recycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recycler.setAdapter(new ModuleAdapter(createModules(), item -> PracticeSetupBottomSheet.newInstance(item).show(getParentFragmentManager(), "setup")));
        ai.setOnClickListener(v -> startActivity(new Intent(requireContext(), AiReportActivity.class)));
        loadStats();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        if (getContext() == null) return;
        executor.execute(() -> {
            List<Attempt> attempts = AppDatabase.getInstance(requireContext()).attemptDao().getAllAttempts();
            int total = 0;
            int correct = 0;
            for (Attempt a : attempts) {
                total += a.totalQuestions;
                correct += a.correctCount;
            }
            double accuracy = ScoreCalculator.accuracy(correct, total);
            String text = String.format(Locale.getDefault(), "Attempted %d  |  Accuracy %.1f%%  |  Streak %dd", total, accuracy, attempts.isEmpty() ? 0 : 1);
            if (getActivity() != null) getActivity().runOnUiThread(() -> tvTodayStats.setText(text));
        });
    }

    private List<ModuleItem> createModules() {
        ArrayList<ModuleItem> list = new ArrayList<>();
        list.add(new ModuleItem("simplification", "Simplification", "Miscellaneous", "%", Constants.MULTIPLE_CHOICE, "Medium"));
        list.add(new ModuleItem("series", "Series", "Miscellaneous", "S", Constants.MULTIPLE_CHOICE, "Medium"));
        list.add(new ModuleItem("quadratic", "Quadratic Equation", "Miscellaneous", "x2", Constants.MULTIPLE_CHOICE, "Medium"));
        list.add(new ModuleItem("mix", "Miscellaneous Mix", "Miscellaneous", "Mix", Constants.MULTIPLE_CHOICE, "Hard"));
        list.add(new ModuleItem("square", "Square", "Quick Recall", "x2", Constants.DIRECT_INPUT, "Easy"));
        list.add(new ModuleItem("cube", "Cube", "Quick Recall", "x3", Constants.DIRECT_INPUT, "Easy"));
        list.add(new ModuleItem("root", "Square Root", "Quick Recall", "sqrt", Constants.DIRECT_INPUT, "Easy"));
        list.add(new ModuleItem("cube_root", "Cube Root", "Quick Recall", "cbrt", Constants.DIRECT_INPUT, "Easy"));
        list.add(new ModuleItem("table", "Table", "Quick Recall", "T", Constants.DIRECT_INPUT, "Easy"));
        list.add(new ModuleItem("trig", "Trigonometry", "Quick Recall", "sin", Constants.MULTIPLE_CHOICE, "Medium"));
        list.add(new ModuleItem("percentage", "Percentage", "Quick Recall", "%", Constants.MULTIPLE_CHOICE, "Medium"));
        list.add(new ModuleItem("fraction", "Fraction", "Quick Recall", "2/3", Constants.MULTIPLE_CHOICE, "Medium"));
        list.add(new ModuleItem("di", "DI Addition", "Quick Recall", "+", Constants.DIRECT_INPUT, "Medium"));
        list.add(new ModuleItem("quick_workout", "Quick Recall Workout", "Quick Recall", "Q", Constants.MULTIPLE_CHOICE, "Hard"));
        list.add(new ModuleItem("addition", "Addition", "Basics", "+", Constants.DIRECT_INPUT, "Medium"));
        list.add(new ModuleItem("subtraction", "Subtraction", "Basics", "-", Constants.DIRECT_INPUT, "Medium"));
        list.add(new ModuleItem("multiplication", "Multiplication", "Basics", "x", Constants.DIRECT_INPUT, "Hard"));
        list.add(new ModuleItem("division", "Division", "Basics", "/", Constants.DIRECT_INPUT, "Medium"));
        list.add(new ModuleItem("complexity", "Complexity", "Basics", "+x", Constants.MULTIPLE_CHOICE, "Hard"));
        list.add(new ModuleItem("basics_workout", "Basics Workout", "Basics", "BW", Constants.MULTIPLE_CHOICE, "Medium"));
        return list;
    }
}
