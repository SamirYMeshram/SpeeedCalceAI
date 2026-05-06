package com.yourname.speedcalcai.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.adapters.QuestionReviewAdapter;
import com.yourname.speedcalcai.database.AppDatabase;
import com.yourname.speedcalcai.models.AttemptQuestion;
import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AttemptReviewActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_review);
        TextView title = findViewById(R.id.tvTitle);
        TextView back = findViewById(R.id.btnBack);
        recycler = findViewById(R.id.recyclerReview);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        back.setOnClickListener(v -> finish());
        int attemptId = getIntent().getIntExtra(Constants.EXTRA_ATTEMPT_ID, -1);
        String module = getIntent().getStringExtra(Constants.EXTRA_MODULE_NAME);
        title.setText(module == null ? "Attempt Review" : module + " Review");
        load(attemptId);
    }

    private void load(int attemptId) {
        executor.execute(() -> {
            List<AttemptQuestion> rows = AppDatabase.getInstance(this).attemptQuestionDao().getQuestionsForAttempt(attemptId);
            ArrayList<MathQuestion> questions = new ArrayList<>();
            for (AttemptQuestion row : rows) {
                MathQuestion q = new MathQuestion(row.questionText, row.correctAnswer, row.moduleName, row.difficulty, row.questionType, null);
                q.setUserAnswer(row.userAnswer);
                q.setTimeTakenMillis(row.timeTakenMillis);
                q.setSkipped("SKIPPED".equals(row.status));
                q.setCorrect("CORRECT".equals(row.status));
                questions.add(q);
            }
            runOnUiThread(() -> recycler.setAdapter(new QuestionReviewAdapter(questions)));
        });
    }
}
