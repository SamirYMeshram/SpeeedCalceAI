package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;

public class SimplificationGenerator extends BaseGenerator {
    @Override
    public MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType) {
        int[] percents = {10, 20, 25, 30, 40, 50, 60, 75};
        int p1 = percents[rand(0, percents.length - 1)];
        int p2 = percents[rand(0, percents.length - 1)];
        int n1 = rand(40, 200);
        int n2 = rand(40, 200);
        int left = (p1 * n1) / 100;
        int right = (p2 * n2) / 100;
        int answer = right - left;
        String q = p1 + "% of " + n1 + " + ? = " + p2 + "% of " + n2;
        String[] options = generateOptions(answer);
        return new MathQuestion(q, String.valueOf(answer), "Simplification", difficulty, Constants.MULTIPLE_CHOICE, options);
    }
}
