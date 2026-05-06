package com.yourname.speedcalcai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.activities.PracticeActivity;
import com.yourname.speedcalcai.utils.Constants;

public class QuizFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        Button workout = view.findViewById(R.id.btnWorkout);
        Button error = view.findViewById(R.id.btnErrorPractice);
        workout.setOnClickListener(v -> open("Basics Workout"));
        error.setOnClickListener(v -> open("Error Practice"));
        return view;
    }

    private void open(String module) {
        Intent intent = new Intent(requireContext(), PracticeActivity.class);
        intent.putExtra(Constants.EXTRA_MODULE_NAME, module);
        intent.putExtra(Constants.EXTRA_FROM, 2);
        intent.putExtra(Constants.EXTRA_TO, 99);
        intent.putExtra(Constants.EXTRA_NUMBERS, 2);
        intent.putExtra(Constants.EXTRA_DIFFICULTY, "Medium");
        intent.putExtra(Constants.EXTRA_QUESTION_TYPE, Constants.MULTIPLE_CHOICE);
        intent.putExtra(Constants.EXTRA_TOTAL, 20);
        startActivity(intent);
    }
}
