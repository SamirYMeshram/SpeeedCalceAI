package com.yourname.speedcalcai.utils;

import android.os.Handler;
import android.os.Looper;

public class TimerManager {
    public interface TickListener { void onTick(long elapsedMillis); }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private long startTime;
    private TickListener listener;
    private final Runnable runnable = new Runnable() {
        @Override public void run() {
            if (listener != null) listener.onTick(System.currentTimeMillis() - startTime);
            handler.postDelayed(this, 50L);
        }
    };

    public void start(TickListener listener) {
        this.listener = listener;
        startTime = System.currentTimeMillis();
        handler.post(runnable);
    }

    public long stop() {
        handler.removeCallbacks(runnable);
        return System.currentTimeMillis() - startTime;
    }
}
