package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;

public class DivisionGenerator extends BaseGenerator {
    @Override
    public MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType) {
        int answer = Math.max(1, rand(Math.max(1, from), Math.max(2, to)));
        int divisor = Math.max(1, rand(Math.max(1, from), Math.max(2, to)));
        int dividend = answer * divisor;
        String question = dividend + " / " + divisor + " = ?";
        String[] options = Constants.MULTIPLE_CHOICE.equals(questionType) ? generateOptions(answer) : null;
        return new MathQuestion(question, String.valueOf(answer), "Division", difficulty, questionType, options);
    }
}
