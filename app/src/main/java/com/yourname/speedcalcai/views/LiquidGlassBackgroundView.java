package com.yourname.speedcalcai.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LiquidGlassBackgroundView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float phase = 0f;

    public LiquidGlassBackgroundView(Context context) {
        super(context);
        start();
    }

    public LiquidGlassBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        start();
    }

    private void start() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(9000L);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(a -> {
            phase = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        drawBubble(canvas, w * (0.22f + phase * 0.05f), h * 0.18f, w * 0.25f, Color.argb(34, 0, 169, 214));
        drawBubble(canvas, w * (0.82f - phase * 0.08f), h * 0.12f, w * 0.22f, Color.argb(30, 72, 96, 255));
        drawBubble(canvas, w * (0.14f + phase * 0.08f), h * 0.76f, w * 0.30f, Color.argb(28, 156, 39, 176));
        drawBubble(canvas, w * 0.84f, h * (0.72f + phase * 0.04f), w * 0.24f, Color.argb(30, 0, 201, 255));
    }

    private void drawBubble(Canvas canvas, float cx, float cy, float radius, int color) {
        paint.setColor(color);
        canvas.drawCircle(cx, cy, radius, paint);
    }
}
