package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;

public class MultiplicationGenerator extends BaseGenerator {
    @Override
    public MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType) {
        int a = rand(from, to);
        int b = rand(from, to);
        int answer = a * b;
        String question = a + " x " + b + " = ?";
        String[] options = Constants.MULTIPLE_CHOICE.equals(questionType) ? generateOptions(answer) : null;
        return new MathQuestion(question, String.valueOf(answer), "Multiplication", difficulty, questionType, options);
    }
}
