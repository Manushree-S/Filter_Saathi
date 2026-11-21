package org.filtersaathi.app.ui.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.filtersaathi.app.R;

/**
 * RingView: Circular indicator for water quality parameters (TDS, pH, Chlorine, Nitrate).
 * Displays a parameter label, a circular progress arc, value, unit, and localized status.
 */
public class RingView extends View {

    private Paint trackPaint;
    private Paint progressPaint;
    private Paint labelPaint;
    private Paint valuePaint;
    private Paint unitPaint;
    private Paint statusPaint;
    private RectF arcBounds;

    private String label = "";
    private String value = "";
    private String unit = "";
    private String statusText = "";
    private int statusColor;
    private float targetPercent = 0f;
    private float animatedPercent = 0f;
    private float strokeWidth = 16f;

    private ValueAnimator animator;

    public RingView(Context context) {
        super(context);
        init();
    }

    public RingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        strokeWidth = density * 6f;

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(strokeWidth);
        trackPaint.setColor(ContextCompat.getColor(getContext(), R.color.divider_light));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        statusColor = ContextCompat.getColor(getContext(), R.color.status_success);
        progressPaint.setColor(statusColor);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(scaledDensity * 12f);
        labelPaint.setFakeBoldText(true);
        labelPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary_light));

        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTextSize(scaledDensity * 16f);
        valuePaint.setFakeBoldText(true);
        valuePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary_light));

        unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setTextAlign(Paint.Align.CENTER);
        unitPaint.setTextSize(scaledDensity * 10f);
        unitPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary_light));

        statusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        statusPaint.setTextAlign(Paint.Align.CENTER);
        statusPaint.setTextSize(scaledDensity * 11f);
        statusPaint.setFakeBoldText(true);
        statusPaint.setColor(statusColor);

        arcBounds = new RectF();
    }

    public void setReading(String label, String value, String unit, String statusText, int color, float percent, boolean animate) {
        this.label = label != null ? label : "";
        this.value = value != null ? value : "";
        this.unit = unit != null ? unit : "";
        this.statusText = statusText != null ? statusText : "";
        this.statusColor = color;
        this.targetPercent = Math.max(0.05f, Math.min(1.0f, percent));

        progressPaint.setColor(statusColor);
        statusPaint.setColor(statusColor);

        // Update colors based on current theme
        labelPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary_light));
        valuePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary_light));
        unitPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary_light));
        trackPaint.setColor(ContextCompat.getColor(getContext(), R.color.divider_light));

        String unitSpace = this.unit.isEmpty() ? "" : (" " + this.unit);
        setContentDescription(this.label + ": " + this.value + unitSpace + ", " + this.statusText);

        boolean animationsEnabled = areAnimationsEnabled();
        if (animate && animationsEnabled) {
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
            animator = ValueAnimator.ofFloat(0f, targetPercent);
            animator.setDuration(800);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(a -> {
                animatedPercent = (float) a.getAnimatedValue();
                invalidate();
            });
            animator.start();
        } else {
            this.animatedPercent = targetPercent;
            invalidate();
        }
    }

    private boolean areAnimationsEnabled() {
        try {
            float durationScale = Settings.Global.getFloat(
                    getContext().getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
            return durationScale > 0;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) (getResources().getDisplayMetrics().density * 90f);
        int desiredHeight = (int) (getResources().getDisplayMetrics().density * 110f);

        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float density = getResources().getDisplayMetrics().density;
        float labelHeight = density * 18f;
        float statusHeight = density * 18f;

        float availableHeight = h - labelHeight - statusHeight;
        float diameter = Math.min(w - (strokeWidth * 2f) - (density * 8f), availableHeight - (strokeWidth * 2f));
        if (diameter < 10) diameter = 10;

        float left = (w - diameter) / 2f;
        float top = labelHeight + ((availableHeight - diameter) / 2f);
        arcBounds.set(left, top, left + diameter, top + diameter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float density = getResources().getDisplayMetrics().density;

        // 1. Draw parameter label at top
        if (!label.isEmpty()) {
            canvas.drawText(label, centerX, density * 14f, labelPaint);
        }

        // 2. Draw circular track and progress
        canvas.drawArc(arcBounds, 0, 360, false, trackPaint);
        float sweepAngle = animatedPercent * 360f;
        canvas.drawArc(arcBounds, -90, sweepAngle, false, progressPaint);

        // 3. Draw center value and unit
        float centerY = arcBounds.centerY();
        if (unit.isEmpty()) {
            canvas.drawText(value, centerX, centerY + (density * 5f), valuePaint);
        } else {
            canvas.drawText(value, centerX, centerY - (density * 1f), valuePaint);
            canvas.drawText(unit, centerX, centerY + (density * 11f), unitPaint);
        }

        // 4. Draw status at bottom
        if (!statusText.isEmpty()) {
            float bottomY = getHeight() - (density * 4f);
            canvas.drawText(statusText, centerX, bottomY, statusPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        super.onDetachedFromWindow();
    }
}
