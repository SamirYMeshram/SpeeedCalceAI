package com.yourname.speedcalcai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.generators.AdditionGenerator;
import com.yourname.speedcalcai.generators.ComplexityGenerator;
import com.yourname.speedcalcai.generators.DivisionGenerator;
import com.yourname.speedcalcai.generators.MultiplicationGenerator;
import com.yourname.speedcalcai.generators.QuadraticGenerator;
import com.yourname.speedcalcai.generators.QuestionGenerator;
import com.yourname.speedcalcai.generators.SimplificationGenerator;
import com.yourname.speedcalcai.generators.SubtractionGenerator;
import com.yourname.speedcalcai.generators.WorkoutGenerator;
import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;
import com.yourname.speedcalcai.utils.DateUtils;
import java.util.ArrayList;
import java.util.Locale;

public class PracticeActivity extends AppCompatActivity {
    private TextView tvModule, tvCorrect, tvWrong, tvTimer, tvQuestion, tvAnswer;
    private GridLayout gridKeypad, gridOptions;
    private Button btnSubmit, btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    private Switch switchAuto;
    private final ArrayList<MathQuestion> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int correctCount = 0;
    private int wrongCount = 0;
    private int skippedCount = 0;
    private long sessionStart;
    private long questionStart;
    private boolean timerRunning = true;

    private String moduleName;
    private int from, to, numbers, total;
    private String difficulty, questionType;
    private QuestionGenerator generator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        bind();
        readIntent();
        generator = generatorFor(moduleName);
        tvModule.setText(moduleName);
        sessionStart = SystemClock.elapsedRealtime();
        setupKeypad();
        setupButtons();
        nextQuestion();
        runTimer();
    }

    private void bind() {
        tvModule = findViewById(R.id.tvModuleName);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvWrong = findViewById(R.id.tvWrong);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvAnswer = findViewById(R.id.tvAnswerInput);
        gridKeypad = findViewById(R.id.gridKeypad);
        gridOptions = findViewById(R.id.gridOptions);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);
        switchAuto = findViewById(R.id.switchAuto);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSkip).setOnClickListener(v -> skipQuestion());
        findViewById(R.id.btnHint).setOnClickListener(v -> Toast.makeText(this, "Think in parts and reduce steps.", Toast.LENGTH_SHORT).show());
    }

    private void readIntent() {
        Intent i = getIntent();
        moduleName = i.getStringExtra(Constants.EXTRA_MODULE_NAME);
        if (moduleName == null) moduleName = "Addition";
        from = i.getIntExtra(Constants.EXTRA_FROM, 2);
        to = i.getIntExtra(Constants.EXTRA_TO, 99);
        numbers = i.getIntExtra(Constants.EXTRA_NUMBERS, 2);
        difficulty = i.getStringExtra(Constants.EXTRA_DIFFICULTY);
        if (difficulty == null) difficulty = "Medium";
        questionType = i.getStringExtra(Constants.EXTRA_QUESTION_TYPE);
        if (questionType == null) questionType = Constants.DIRECT_INPUT;
        total = i.getIntExtra(Constants.EXTRA_TOTAL, 10);
    }

    private QuestionGenerator generatorFor(String module) {
        if (module.equals("Addition")) return new AdditionGenerator();
        if (module.equals("Subtraction")) return new SubtractionGenerator();
        if (module.equals("Multiplication")) return new MultiplicationGenerator();
        if (module.equals("Division")) return new DivisionGenerator();
        if (module.equals("Simplification")) return new SimplificationGenerator();
        if (module.equals("Quadratic Equation")) return new QuadraticGenerator();
        if (module.equals("Complexity")) return new ComplexityGenerator();
        return new WorkoutGenerator();
    }

    private void setupKeypad() {
        for (int c = 0; c < gridKeypad.getChildCount(); c++) {
            View child = gridKeypad.getChildAt(c);
            if (child instanceof Button) {
                Button b = (Button) child;
                b.setOnClickListener(v -> onKey(b.getText().toString()));
            }
        }
    }

    private void setupButtons() {
        btnSubmit.setOnClickListener(v -> submitAnswer(tvAnswer.getText().toString()));
        View.OnClickListener optionListener = v -> submitAnswer(((Button) v).getText().toString());
        btnOpt1.setOnClickListener(optionListener);
        btnOpt2.setOnClickListener(optionListener);
        btnOpt3.setOnClickListener(optionListener);
        btnOpt4.setOnClickListener(optionListener);
    }

    private void onKey(String key) {
        String current = tvAnswer.getText().toString();
        if (key.equals("\u232b") || key.equals("BACK") || key.equals("DEL")) {
            if (current.length() > 0) tvAnswer.setText(current.substring(0, current.length() - 1));
            return;
        }
        if (key.equals("-") && current.contains("-")) return;
        if (key.equals("-") && current.length() > 0) return;
        tvAnswer.setText(current + key);
        MathQuestion q = questions.get(currentIndex);
        if (switchAuto.isChecked() && (current + key).equals(q.getCorrectAnswer())) submitAnswer(current + key);
    }

    private void nextQuestion() {
        if (currentIndex >= total) {
            finishSession();
            return;
        }
        MathQuestion question = generator.generateQuestion(from, to, numbers, difficulty, questionType);
        questions.add(question);
        questionStart = SystemClock.elapsedRealtime();
        tvQuestion.setText(question.getQuestionText());
        tvAnswer.setText("");
        boolean mcq = Constants.MULTIPLE_CHOICE.equals(question.getQuestionType()) && question.getOptions() != null;
        gridOptions.setVisibility(mcq ? View.VISIBLE : View.GONE);
        gridKeypad.setVisibility(mcq ? View.GONE : View.VISIBLE);
        btnSubmit.setVisibility(mcq ? View.GONE : View.VISIBLE);
        if (mcq) {
            String[] o = question.getOptions();
            btnOpt1.setText(o[0]);
            btnOpt2.setText(o[1]);
            btnOpt3.setText(o[2]);
            btnOpt4.setText(o[3]);
        }
        updateCounts();
    }

    private void submitAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            Toast.makeText(this, "Enter an answer", Toast.LENGTH_SHORT).show();
            return;
        }
        MathQuestion q = questions.get(currentIndex);
        q.setUserAnswer(answer.trim());
        q.setTimeTakenMillis(SystemClock.elapsedRealtime() - questionStart);
        boolean correct = q.getUserAnswer().equals(q.getCorrectAnswer());
        q.setCorrect(correct);
        if (correct) correctCount++; else wrongCount++;
        currentIndex++;
        nextQuestion();
    }

    private void skipQuestion() {
        MathQuestion q = questions.get(currentIndex);
        q.setSkipped(true);
        q.setCorrect(false);
        q.setUserAnswer("");
        q.setTimeTakenMillis(SystemClock.elapsedRealtime() - questionStart);
        skippedCount++;
        currentIndex++;
        nextQuestion();
    }

    private void updateCounts() {
        tvCorrect.setText("Correct " + correctCount);
        tvWrong.setText("Wrong " + wrongCount);
    }

    private void runTimer() {
        tvTimer.postDelayed(new Runnable() {
            @Override public void run() {
                if (!timerRunning) return;
                long elapsed = SystemClock.elapsedRealtime() - sessionStart;
                long minutes = elapsed / 60000;
                long seconds = (elapsed / 1000) % 60;
                long ms = (elapsed % 1000) / 10;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, ms));
                tvTimer.postDelayed(this, 50);
            }
        }, 50);
    }

    private void finishSession() {
        timerRunning = false;
        long duration = SystemClock.elapsedRealtime() - sessionStart;
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Constants.EXTRA_MODULE_NAME, moduleName);
        intent.putExtra(Constants.EXTRA_DURATION, duration);
        intent.putExtra(Constants.EXTRA_QUESTIONS, questions);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        timerRunning = false;
        super.onBackPressed();
    }
}
