package com.yourname.speedcalcai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.adapters.QuestionReviewAdapter;
import com.yourname.speedcalcai.database.AppDatabase;
import com.yourname.speedcalcai.models.Attempt;
import com.yourname.speedcalcai.models.AttemptQuestion;
import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;
import com.yourname.speedcalcai.utils.DateUtils;
import com.yourname.speedcalcai.utils.ScoreCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ArrayList<MathQuestion> questions;
    private String moduleName;
    private long duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        moduleName = getIntent().getStringExtra(Constants.EXTRA_MODULE_NAME);
        duration = getIntent().getLongExtra(Constants.EXTRA_DURATION, 0L);
        questions = (ArrayList<MathQuestion>) getIntent().getSerializableExtra(Constants.EXTRA_QUESTIONS);
        if (questions == null) questions = new ArrayList<>();
        render();
        saveAttempt();
    }

    private void render() {
        TextView title = findViewById(R.id.tvResultTitle);
        TextView stats = findViewById(R.id.tvResultStats);
        ProgressBar progress = findViewById(R.id.progressAccuracy);
        Button history = findViewById(R.id.btnHistory);
        RecyclerView recycler = findViewById(R.id.recyclerReview);
        int correct = 0, wrong = 0, skipped = 0;
        for (MathQuestion q : questions) {
            if (q.isSkipped()) skipped++;
            else if (q.isCorrect()) correct++;
            else wrong++;
        }
        int total = questions.size();
        double accuracy = ScoreCalculator.accuracy(correct, total);
        double score = ScoreCalculator.score(correct, wrong);
        title.setText(moduleName + " Summary");
        stats.setText(String.format(Locale.getDefault(), "Total: %d\nCorrect: %d\nWrong: %d\nSkipped: %d\nAccuracy: %.1f%%\nScore: %.1f\nDuration: %s", total, correct, wrong, skipped, accuracy, score, DateUtils.formatDuration(duration)));
        progress.setProgress((int) accuracy);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new QuestionReviewAdapter(questions));
        history.setOnClickListener(v -> startActivity(new Intent(this, AttemptHistoryActivity.class)));
    }

    private void saveAttempt() {
        executor.execute(() -> {
            int correct = 0, wrong = 0, skipped = 0;
            for (MathQuestion q : questions) {
                if (q.isSkipped()) skipped++;
                else if (q.isCorrect()) correct++;
                else wrong++;
            }
            Attempt attempt = new Attempt();
            attempt.userId = "offline-user";
            attempt.moduleName = moduleName;
            attempt.mode = "PRACTICE";
            attempt.totalQuestions = questions.size();
            attempt.correctCount = correct;
            attempt.wrongCount = wrong;
            attempt.skippedCount = skipped;
            attempt.accuracy = ScoreCalculator.accuracy(correct, questions.size());
            attempt.score = ScoreCalculator.score(correct, wrong);
            attempt.durationMillis = duration;
            attempt.createdAt = System.currentTimeMillis();
            long id = AppDatabase.getInstance(this).attemptDao().insert(attempt);
            List<AttemptQuestion> rows = new ArrayList<>();
            for (MathQuestion q : questions) {
                AttemptQuestion aq = new AttemptQuestion();
                aq.attemptId = (int) id;
                aq.moduleName = q.getModuleName();
                aq.questionText = q.getQuestionText();
                aq.correctAnswer = q.getCorrectAnswer();
                aq.userAnswer = q.getUserAnswer();
                aq.status = q.isSkipped() ? "SKIPPED" : (q.isCorrect() ? "CORRECT" : "WRONG");
                aq.questionType = q.getQuestionType();
                aq.difficulty = q.getDifficulty();
                aq.timeTakenMillis = q.getTimeTakenMillis();
                rows.add(aq);
            }
            AppDatabase.getInstance(this).attemptQuestionDao().insertAll(rows);
        });
    }
}
