package com.yourname.speedcalcai.models;

import java.io.Serializable;

public class MathQuestion implements Serializable {
    private String questionText;
    private String correctAnswer;
    private String userAnswer;
    private String moduleName;
    private String difficulty;
    private String questionType;
    private long timeTakenMillis;
    private boolean skipped;
    private boolean correct;
    private String[] options;

    public MathQuestion() {}

    public MathQuestion(String questionText, String correctAnswer, String moduleName,
                        String difficulty, String questionType, String[] options) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.moduleName = moduleName;
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.options = options;
    }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public long getTimeTakenMillis() { return timeTakenMillis; }
    public void setTimeTakenMillis(long timeTakenMillis) { this.timeTakenMillis = timeTakenMillis; }
    public boolean isSkipped() { return skipped; }
    public void setSkipped(boolean skipped) { this.skipped = skipped; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }
}
