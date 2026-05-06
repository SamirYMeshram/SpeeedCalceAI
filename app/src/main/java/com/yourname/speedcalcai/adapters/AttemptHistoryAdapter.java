package com.yourname.speedcalcai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.models.Attempt;
import com.yourname.speedcalcai.utils.DateUtils;
import java.util.List;
import java.util.Locale;

public class AttemptHistoryAdapter extends RecyclerView.Adapter<AttemptHistoryAdapter.AttemptViewHolder> {
    public interface OnReviewClickListener { void onReview(Attempt attempt); }
    private final List<Attempt> attempts;
    private final OnReviewClickListener listener;

    public AttemptHistoryAdapter(List<Attempt> attempts, OnReviewClickListener listener) {
        this.attempts = attempts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttemptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attempt_history, parent, false);
        return new AttemptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttemptViewHolder holder, int position) {
        Attempt a = attempts.get(position);
        holder.tvTitle.setText(DateUtils.formatTime(a.createdAt) + " - " + a.moduleName);
        holder.tvStats.setText(String.format(Locale.getDefault(), "Score %.1f   Accuracy %.1f%%   Time %s", a.score, a.accuracy, DateUtils.formatDuration(a.durationMillis)));
        holder.tvCounts.setText("Correct " + a.correctCount + "    Skipped " + a.skippedCount + "    Wrong " + a.wrongCount);
        holder.progress.setProgress((int) a.accuracy);
        holder.btnReview.setOnClickListener(v -> listener.onReview(a));
    }

    @Override public int getItemCount() { return attempts.size(); }

    static class AttemptViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStats, tvCounts;
        Button btnReview;
        ProgressBar progress;
        AttemptViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStats = itemView.findViewById(R.id.tvStats);
            tvCounts = itemView.findViewById(R.id.tvCounts);
            btnReview = itemView.findViewById(R.id.btnReview);
            progress = itemView.findViewById(R.id.progress);
        }
    }
}
