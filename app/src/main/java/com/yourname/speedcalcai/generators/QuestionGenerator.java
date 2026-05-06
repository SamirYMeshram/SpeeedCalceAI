package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;

public interface QuestionGenerator {
    MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType);
}
