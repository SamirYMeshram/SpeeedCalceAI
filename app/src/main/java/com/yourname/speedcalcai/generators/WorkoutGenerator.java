package com.yourname.speedcalcai.generators;

import com.yourname.speedcalcai.models.MathQuestion;

public class WorkoutGenerator extends BaseGenerator {
    private final QuestionGenerator[] generators = {
            new AdditionGenerator(), new SubtractionGenerator(), new MultiplicationGenerator(), new DivisionGenerator(), new ComplexityGenerator()
    };

    @Override
    public MathQuestion generateQuestion(int from, int to, int numbersInQuestion, String difficulty, String questionType) {
        return generators[rand(0, generators.length - 1)].generateQuestion(from, to, numbersInQuestion, difficulty, questionType);
    }
}
