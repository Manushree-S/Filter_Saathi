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

import java.util.ArrayList;
import java.util.List;

/**
 * BarChartView: Renders a 6-month trend bar chart (May to October).
 * Animates bar height on load; the latest bar is darker.
 */
public class BarChartView extends View {

    public static class BarItem {
        public final String label;
        public final int value; // 0..100

        public BarItem(String label, int value) {
            this.label = label;
            this.value = value;
        }
    }

    private final List<BarItem> items = new ArrayList<>();
    private Paint barPaint;
    private Paint latestBarPaint;
    private Paint textPaint;
    private Paint valuePaint;

    private float animationProgress = 0f;
    private ValueAnimator animator;

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(ContextCompat.getColor(getContext(), R.color.secondary_blue));

        latestBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        latestBarPaint.setColor(ContextCompat.getColor(getContext(), R.color.primary_navy));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(scaledDensity * 12f);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary_light));

        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTextSize(scaledDensity * 11f);
        valuePaint.setFakeBoldText(true);
        valuePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary_light));

        // Default sample data May-Oct
        items.add(new BarItem(getContext().getString(R.string.ui_m_may), 88));
        items.add(new BarItem(getContext().getString(R.string.ui_m_jun), 90));
        items.add(new BarItem(getContext().getString(R.string.ui_m_jul), 91));
        items.add(new BarItem(getContext().getString(R.string.ui_m_aug), 92));
        items.add(new BarItem(getContext().getString(R.string.ui_m_sep), 93));
        items.add(new BarItem(getContext().getString(R.string.ui_m_oct), 94));
    }

    public void setData(List<BarItem> newItems) {
        if (newItems != null && !newItems.isEmpty()) {
            items.clear();
            items.addAll(newItems);
        }
        startAnimation();
    }

    public void startAnimation() {
        if (!areAnimationsEnabled()) {
            animationProgress = 1.0f;
            invalidate();
            return;
        }

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(900);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> {
            animationProgress = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = (int) (getResources().getDisplayMetrics().density * 180f);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (items.isEmpty()) return;

        // Update colors based on current theme
        barPaint.setColor(ContextCompat.getColor(getContext(), R.color.secondary_blue));
        latestBarPaint.setColor(ContextCompat.getColor(getContext(), R.color.primary_navy));
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary_light));
        valuePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary_light));

        float density = getResources().getDisplayMetrics().density;
        float w = getWidth();
        float h = getHeight();

        float labelBottomMargin = density * 18f;
        float valueTopMargin = density * 18f;
        float chartTop = valueTopMargin;
        float chartBottom = h - labelBottomMargin;
        float chartHeight = chartBottom - chartTop;

        int count = items.size();
        float slotWidth = w / count;
        float barWidth = Math.min(slotWidth * 0.55f, density * 32f);
        float cornerRadius = density * 5f;

        for (int i = 0; i < count; i++) {
            BarItem item = items.get(i);
            float slotCenterX = (i * slotWidth) + (slotWidth / 2f);
            float left = slotCenterX - (barWidth / 2f);
            float right = slotCenterX + (barWidth / 2f);

            float targetHeight = (item.value / 100f) * chartHeight;
            float currentBarHeight = targetHeight * animationProgress;
            float top = chartBottom - currentBarHeight;

            RectF rect = new RectF(left, top, right, chartBottom);
            Paint paint = (i == count - 1) ? latestBarPaint : barPaint;
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

            // Month Label below bar
            canvas.drawText(item.label, slotCenterX, h - (density * 2f), textPaint);

            // Value text above bar (e.g. "94%")
            if (animationProgress > 0.4f) {
                String valText = item.value + "%";
                canvas.drawText(valText, slotCenterX, Math.max(valueTopMargin, top - (density * 4f)), valuePaint);
            }
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
