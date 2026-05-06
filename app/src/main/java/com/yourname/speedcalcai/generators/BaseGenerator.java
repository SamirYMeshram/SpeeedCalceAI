package com.yourname.speedcalcai.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public abstract class BaseGenerator implements QuestionGenerator {
    protected final Random random = new Random();

    protected int rand(int from, int to) {
        if (to < from) {
            int tmp = from;
            from = to;
            to = tmp;
        }
        return random.nextInt(to - from + 1) + from;
    }

    protected String[] generateOptions(int correct) {
        ArrayList<String> options = new ArrayList<>();
        options.add(String.valueOf(correct));
        int spread = Math.max(4, Math.abs(correct / 8));
        while (options.size() < 4) {
            int delta = rand(-spread, spread);
            if (delta == 0) delta = options.size() + 1;
            String candidate = String.valueOf(correct + delta);
            if (!options.contains(candidate)) options.add(candidate);
        }
        Collections.shuffle(options);
        return options.toArray(new String[0]);
    }
}
