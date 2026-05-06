package com.yourname.speedcalcai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.activities.PracticeActivity;
import com.yourname.speedcalcai.models.ModuleItem;
import com.yourname.speedcalcai.utils.Constants;

public class PracticeSetupBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_NAME = "moduleName";
    private static final String ARG_TYPE = "questionType";
    private String moduleName;
    private String defaultQuestionType;

    public static PracticeSetupBottomSheet newInstance(ModuleItem item) {
        PracticeSetupBottomSheet sheet = new PracticeSetupBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, item.getModuleName());
        args.putString(ARG_TYPE, item.getQuestionType());
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moduleName = getArguments().getString(ARG_NAME, "Addition");
            defaultQuestionType = getArguments().getString(ARG_TYPE, Constants.DIRECT_INPUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_practice_setup, container, false);
        TextView title = view.findViewById(R.id.tvSheetTitle);
        EditText etFrom = view.findViewById(R.id.etFrom);
        EditText etTo = view.findViewById(R.id.etTo);
        EditText etTotal = view.findViewById(R.id.etTotalQuestions);
        Spinner spNumbers = view.findViewById(R.id.spNumbers);
        Spinner spDifficulty = view.findViewById(R.id.spDifficulty);
        RadioButton rbDirect = view.findViewById(R.id.rbDirect);
        RadioButton rbMcq = view.findViewById(R.id.rbMcq);
        Button cancel = view.findViewById(R.id.btnCancel);
        Button start = view.findViewById(R.id.btnStart);

        title.setText(moduleName + " Setup");
        spNumbers.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"2", "3", "4", "5"}));
        spDifficulty.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"Easy", "Medium", "Hard"}));
        spDifficulty.setSelection(1);

        if (Constants.MULTIPLE_CHOICE.equals(defaultQuestionType)) rbMcq.setChecked(true);
        else rbDirect.setChecked(true);

        if (moduleName.equals("Simplification") || moduleName.equals("Quadratic Equation")) {
            rbMcq.setChecked(true);
            rbDirect.setEnabled(false);
        }

        cancel.setOnClickListener(v -> dismiss());
        start.setOnClickListener(v -> {
            int from = parseInt(etFrom.getText().toString(), 2);
            int to = parseInt(etTo.getText().toString(), 99);
            int total = parseInt(etTotal.getText().toString(), 10);
            int numbers = Integer.parseInt(spNumbers.getSelectedItem().toString());
            String difficulty = spDifficulty.getSelectedItem().toString();
            String type = rbMcq.isChecked() ? Constants.MULTIPLE_CHOICE : Constants.DIRECT_INPUT;

            Intent intent = new Intent(requireContext(), PracticeActivity.class);
            intent.putExtra(Constants.EXTRA_MODULE_NAME, moduleName);
            intent.putExtra(Constants.EXTRA_FROM, from);
            intent.putExtra(Constants.EXTRA_TO, to);
            intent.putExtra(Constants.EXTRA_NUMBERS, numbers);
            intent.putExtra(Constants.EXTRA_DIFFICULTY, difficulty);
            intent.putExtra(Constants.EXTRA_QUESTION_TYPE, type);
            intent.putExtra(Constants.EXTRA_TOTAL, Math.max(1, Math.min(200, total)));
            startActivity(intent);
            dismiss();
        });
        return view;
    }

    private int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value.trim()); }
        catch (Exception e) { return fallback; }
    }
}
