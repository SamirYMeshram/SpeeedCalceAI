package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;

public class SubtractionGenerator extends BaseGenerator {
    @Override
    public MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType) {
        int count = Math.max(2, numbersInQuestion);
        int answer = rand(from, to);
        StringBuilder question = new StringBuilder(String.valueOf(answer));
        for (int i = 1; i < count; i++) {
            int number = rand(from, to);
            answer -= number;
            question.append(" - ").append(number);
        }
        question.append(" = ?");
        String[] options = Constants.MULTIPLE_CHOICE.equals(questionType) ? generateOptions(answer) : null;
        return new MathQuestion(question.toString(), String.valueOf(answer), "Subtraction", difficulty, questionType, options);
    }
}
