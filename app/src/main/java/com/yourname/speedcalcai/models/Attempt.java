package com.yourname.speedcalcai.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "attempts")
public class Attempt {
    @PrimaryKey(autoGenerate = true)
    public int attemptId;

    public String userId;
    public String moduleName;
    public String mode;

    public int totalQuestions;
    public int correctCount;
    public int wrongCount;
    public int skippedCount;

    public double accuracy;
    public double score;

    public long durationMillis;
    public long createdAt;
}
