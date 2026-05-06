package com.yourname.speedcalcai.utils;

import com.yourname.speedcalcai.models.Attempt;
import java.util.List;

public class StatsCalculator {
    public static int totalQuestions(List<Attempt> attempts) {
        int total = 0;
        for (Attempt attempt : attempts) total += attempt.totalQuestions;
        return total;
    }

    public static int totalCorrect(List<Attempt> attempts) {
        int total = 0;
        for (Attempt attempt : attempts) total += attempt.correctCount;
        return total;
    }

    public static int totalWrong(List<Attempt> attempts) {
        int total = 0;
        for (Attempt attempt : attempts) total += attempt.wrongCount;
        return total;
    }

    public static int totalSkipped(List<Attempt> attempts) {
        int total = 0;
        for (Attempt attempt : attempts) total += attempt.skippedCount;
        return total;
    }
}
