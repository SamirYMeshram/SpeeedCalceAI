package com.yourname.speedcalcai.utils;

public class ScoreCalculator {
    public static double accuracy(int correct, int total) {
        if (total <= 0) return 0.0;
        return (correct * 100.0) / total;
    }

    public static double score(int correct, int wrong) {
        return (correct * 2.0) - (wrong * 0.5);
    }
}
