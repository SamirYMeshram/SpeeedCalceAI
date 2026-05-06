package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;

public class ComplexityGenerator extends BaseGenerator {
    @Override
    public MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType) {
        int a = rand(from, to);
        int b = rand(from, to);
        int c = rand(2, 12);
        int op = rand(0, 3);
        int answer;
        String q;
        if (op == 0) { answer = a + b * c; q = a + " + " + b + " x " + c + " = ?"; }
        else if (op == 1) { answer = (a + b) * c; q = "(" + a + " + " + b + ") x " + c + " = ?"; }
        else if (op == 2) { answer = a * c - b; q = a + " x " + c + " - " + b + " = ?"; }
        else { answer = a + b - c; q = a + " + " + b + " - " + c + " = ?"; }
        String[] options = Constants.MULTIPLE_CHOICE.equals(questionType) ? generateOptions(answer) : null;
        return new MathQuestion(q, String.valueOf(answer), "Complexity", difficulty, questionType, options);
    }
}
