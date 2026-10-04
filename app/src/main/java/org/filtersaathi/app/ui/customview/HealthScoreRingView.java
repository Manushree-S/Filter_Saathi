package org.filtersaathi.app.ui.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import org.filtersaathi.app.R;

public class HealthScoreRingView extends View {

    private Paint backgroundArcPaint;
    private Paint progressArcPaint;
    private Paint scoreTextPaint;
    private Paint subTextPaint;
    private RectF arcBounds;

    private int score = 0;
    private float animatedScore = 0f;
    private float strokeWidth = 28f;
    private ValueAnimator animator;

    public HealthScoreRingView(Context context) {
        super(context);
        init();
    }

    public HealthScoreRingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HealthScoreRingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        strokeWidth = getResources().getDisplayMetrics().density * 10f;

        backgroundArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundArcPaint.setStyle(Paint.Style.STROKE);
        backgroundArcPaint.setStrokeWidth(strokeWidth);
        backgroundArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.aqua_tint));
        backgroundArcPaint.setStrokeCap(Paint.Cap.ROUND);

        progressArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressArcPaint.setStyle(Paint.Style.STROKE);
        progressArcPaint.setStrokeWidth(strokeWidth);
        progressArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.aqua));
        progressArcPaint.setStrokeCap(Paint.Cap.ROUND);

        scoreTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scoreTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.surface_card_light));
        scoreTextPaint.setTextAlign(Paint.Align.CENTER);
        scoreTextPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * 32f);
        scoreTextPaint.setFakeBoldText(true);

        subTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        subTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.surface_card_light));
        subTextPaint.setTextAlign(Paint.Align.CENTER);
        subTextPaint.setAlpha(200);
        subTextPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * 12f);

        arcBounds = new RectF();
    }

    public void setScore(int newScore, boolean animate) {
        this.score = Math.max(0, Math.min(100, newScore));

        if (score >= 80) {
            progressArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.aqua));
        } else if (score >= 50) {
            progressArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.status_warning));
        } else {
            progressArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.status_danger));
        }

        if (animate) {
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
            animator = ValueAnimator.ofFloat(0f, score);
            animator.setDuration(900);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                animatedScore = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        } else {
            this.animatedScore = score;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float pad = strokeWidth / 2f + 8f;
        arcBounds.set(pad, pad, w - pad, h - pad);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw track (full 360 circle)
        canvas.drawArc(arcBounds, 0, 360, false, backgroundArcPaint);

        // Draw active arc from 270 (top)
        float sweepAngle = (animatedScore / 100f) * 360f;
        canvas.drawArc(arcBounds, -90, sweepAngle, false, progressArcPaint);

        // Draw score number in center
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawText(String.valueOf((int) animatedScore), centerX, centerY + 8f, scoreTextPaint);
        canvas.drawText("/100", centerX, centerY + 34f, subTextPaint);
    }
}
