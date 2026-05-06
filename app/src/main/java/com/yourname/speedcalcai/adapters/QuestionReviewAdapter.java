package com.yourname.speedcalcai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.DateUtils;
import java.util.List;

public class QuestionReviewAdapter extends RecyclerView.Adapter<QuestionReviewAdapter.ReviewViewHolder> {
    private final List<MathQuestion> questions;

    public QuestionReviewAdapter(List<MathQuestion> questions) { this.questions = questions; }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        MathQuestion q = questions.get(position);
        holder.tvQuestion.setText((position + 1) + ". " + q.getQuestionText());
        String user = q.getUserAnswer() == null || q.getUserAnswer().trim().isEmpty() ? "Skipped" : q.getUserAnswer();
        holder.tvAnswers.setText("Attempted: " + user + "    Correct: " + q.getCorrectAnswer());
        String status = q.isSkipped() ? "SKIPPED" : (q.isCorrect() ? "CORRECT" : "WRONG");
        holder.tvMeta.setText(status + " | " + DateUtils.formatQuestionTime(q.getTimeTakenMillis()));
        if (q.isSkipped()) holder.card.setBackgroundResource(R.drawable.bg_skipped_card);
        else if (q.isCorrect()) holder.card.setBackgroundResource(R.drawable.bg_success_card);
        else holder.card.setBackgroundResource(R.drawable.bg_error_card);
    }

    @Override public int getItemCount() { return questions.size(); }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        LinearLayout card;
        TextView tvQuestion, tvAnswers, tvMeta;
        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardReview);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswers = itemView.findViewById(R.id.tvAnswers);
            tvMeta = itemView.findViewById(R.id.tvMeta);
        }
    }
}
