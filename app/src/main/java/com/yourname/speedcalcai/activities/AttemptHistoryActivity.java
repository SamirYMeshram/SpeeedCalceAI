package com.yourname.speedcalcai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.adapters.AttemptHistoryAdapter;
import com.yourname.speedcalcai.database.AppDatabase;
import com.yourname.speedcalcai.models.Attempt;
import com.yourname.speedcalcai.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AttemptHistoryActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private Spinner spinner;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_history);
        recycler = findViewById(R.id.recyclerAttempts);
        spinner = findViewById(R.id.spFilter);
        TextView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"All modules", "Addition", "Subtraction", "Multiplication", "Division", "Simplification", "Quiz", "Workout"}));
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) { load(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        load();
    }

    private void load() {
        executor.execute(() -> {
            String filter = spinner.getSelectedItem() == null ? "All modules" : spinner.getSelectedItem().toString();
            List<Attempt> attempts = filter.equals("All modules") ? AppDatabase.getInstance(this).attemptDao().getAllAttempts() : AppDatabase.getInstance(this).attemptDao().getAttemptsByModule(filter);
            runOnUiThread(() -> recycler.setAdapter(new AttemptHistoryAdapter(new ArrayList<>(attempts), attempt -> {
                Intent intent = new Intent(this, AttemptReviewActivity.class);
                intent.putExtra(Constants.EXTRA_ATTEMPT_ID, attempt.attemptId);
                intent.putExtra(Constants.EXTRA_MODULE_NAME, attempt.moduleName);
                startActivity(intent);
            })));
        });
    }
}
