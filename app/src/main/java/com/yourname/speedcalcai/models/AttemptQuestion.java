package com.yourname.speedcalcai.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "attempt_questions")
public class AttemptQuestion {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int attemptId;
    public String moduleName;
    public String questionText;
    public String correctAnswer;
    public String userAnswer;
    public String status;
    public String questionType;
    public String difficulty;
    public long timeTakenMillis;
}
