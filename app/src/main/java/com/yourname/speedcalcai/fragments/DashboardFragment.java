package com.yourname.speedcalcai.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.database.AppDatabase;
import com.yourname.speedcalcai.models.Attempt;
import com.yourname.speedcalcai.utils.ScoreCalculator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {
    private TextView tvSummary, tvReport;
    private PieChart pieChart;
    private LineChart lineChart;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tvSummary = view.findViewById(R.id.tvSummary);
        tvReport = view.findViewById(R.id.tvReport);
        pieChart = view.findViewById(R.id.pieChart);
        lineChart = view.findViewById(R.id.lineChart);
        load();
        return view;
    }

    @Override public void onResume() { super.onResume(); load(); }

    private void load() {
        if (getContext() == null) return;
        executor.execute(() -> {
            List<Attempt> attempts = AppDatabase.getInstance(requireContext()).attemptDao().getAllAttempts();
            if (getActivity() != null) getActivity().runOnUiThread(() -> render(attempts));
        });
    }

    private void render(List<Attempt> attempts) {
        int total = 0, correct = 0, wrong = 0, skipped = 0;
        long duration = 0;
        for (Attempt a : attempts) {
            total += a.totalQuestions;
            correct += a.correctCount;
            wrong += a.wrongCount;
            skipped += a.skippedCount;
            duration += a.durationMillis;
        }
        double accuracy = ScoreCalculator.accuracy(correct, total);
        double avgTime = total == 0 ? 0 : (duration / 1000.0) / total;
        tvSummary.setText(String.format(Locale.getDefault(), "Summary\nTotal Questions: %d\nCorrect Answers: %d\nWrong Answers: %d\nSkipped Answers: %d\nAccuracy: %.1f%%\nAverage Time: %.1f sec", total, correct, wrong, skipped, accuracy, avgTime));
        tvReport.setText(total == 0 ? "Complete your first practice session to unlock a performance report." : String.format(Locale.getDefault(), "You worked through %d questions with %.1f%% accuracy and %.1fs average response time. Keep practicing the weakest modules.", total, accuracy, avgTime));
        renderPie(attempts);
        renderLine(attempts);
    }

    private void renderPie(List<Attempt> attempts) {
        HashMap<String, Integer> map = new HashMap<>();
        for (Attempt a : attempts) map.put(a.moduleName, map.getOrDefault(a.moduleName, 0) + a.totalQuestions);
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> e : map.entrySet()) entries.add(new PieEntry(e.getValue(), e.getKey()));
        if (entries.isEmpty()) entries.add(new PieEntry(1, "No data"));
        PieDataSet set = new PieDataSet(entries, "Modules");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(requireContext(), R.color.primary_cyan));
        colors.add(ContextCompat.getColor(requireContext(), R.color.success_green));
        colors.add(ContextCompat.getColor(requireContext(), R.color.warning_orange));
        colors.add(ContextCompat.getColor(requireContext(), R.color.purple_glow));
        colors.add(ContextCompat.getColor(requireContext(), R.color.error_red));
        set.setColors(colors);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(14f);
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE);
        pieChart.invalidate();
    }

    private void renderLine(List<Attempt> attempts) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < attempts.size(); i++) entries.add(new Entry(i + 1, (float) attempts.get(attempts.size() - 1 - i).accuracy));
        if (entries.isEmpty()) entries.add(new Entry(1, 0));
        LineDataSet set = new LineDataSet(entries, "Accuracy");
        set.setColor(ContextCompat.getColor(requireContext(), R.color.success_green));
        set.setCircleColor(ContextCompat.getColor(requireContext(), R.color.primary_cyan));
        set.setValueTextColor(Color.WHITE);
        set.setLineWidth(3f);
        set.setDrawFilled(true);
        set.setFillColor(ContextCompat.getColor(requireContext(), R.color.success_green));
        lineChart.setData(new LineData(set));
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate();
    }
}
