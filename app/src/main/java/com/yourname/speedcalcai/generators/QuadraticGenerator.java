package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;
import com.yourname.speedcalcai.utils.Constants;

public class QuadraticGenerator extends BaseGenerator {
    @Override
    public MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType) {
        int x = rand(2, 12);
        int y = rand(2, 12);
        int c1 = x * x;
        int c2 = y * y;
        String relation;
        if (x > y) relation = "x > y";
        else if (x < y) relation = "x < y";
        else relation = "x = y";
        String q = "I. x^2 - " + c1 + " = 0\nII. y^2 - " + c2 + " = 0\nCompare x and y";
        String[] options = {"x > y", "x < y", "x = y", "No relation"};
        return new MathQuestion(q, relation, "Quadratic Equation", difficulty, Constants.MULTIPLE_CHOICE, options);
    }
}
