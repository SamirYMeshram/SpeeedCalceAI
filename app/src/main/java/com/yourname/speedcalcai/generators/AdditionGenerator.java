package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;

public class AdditionGenerator extends BaseGenerator {
    @Override
    public MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType) {
        int sum = 0;
        StringBuilder question = new StringBuilder();
        int count = Math.max(2, numbersInQuestion);
        for (int i = 0; i < count; i++) {
            int number = rand(from, to);
            sum += number;
            question.append(number);
            if (i < count - 1) question.append(" + ");
        }
        question.append(" = ?");
        String[] options = Constants.MULTIPLE_CHOICE.equals(questionType) ? generateOptions(sum) : null;
        return new MathQuestion(question.toString(), String.valueOf(sum), "Addition", difficulty, questionType, options);
    }
}
