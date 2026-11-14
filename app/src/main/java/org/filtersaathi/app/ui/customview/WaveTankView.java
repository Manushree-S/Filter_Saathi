package org.filtersaathi.app.ui.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.filtersaathi.app.R;

/**
 * WaveTankView: Animated wave tank gauge displaying water level percentage.
 * Respects system remove-animations setting and pauses cleanly when detached.
 */
public class WaveTankView extends View {

    private Paint tankBorderPaint;
    private Paint tankBgPaint;
    private Paint waveBackPaint;
    private Paint waveFrontPaint;
    private Paint percentTextPaint;
    private Paint labelTextPaint;

    private Path tankClipPath;
    private Path waveBackPath;
    private Path waveFrontPath;
    private RectF tankBounds;

    private int levelPercent = 78;
    private float waveShift = 0f;
    private ValueAnimator waveAnimator;

    public WaveTankView(Context context) {
        super(context);
        init();
    }

    public WaveTankView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveTankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;

        tankBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tankBorderPaint.setStyle(Paint.Style.STROKE);
        tankBorderPaint.setStrokeWidth(density * 3f);
        tankBorderPaint.setColor(ContextCompat.getColor(getContext(), R.color.secondary_blue));

        tankBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tankBgPaint.setColor(ContextCompat.getColor(getContext(), R.color.surface_card_light));

        waveBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        waveBackPaint.setColor(ContextCompat.getColor(getContext(), R.color.aqua_tint));

        waveFrontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        waveFrontPaint.setColor(ContextCompat.getColor(getContext(), R.color.aqua));
        waveFrontPaint.setAlpha(170);

        percentTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        percentTextPaint.setTextAlign(Paint.Align.CENTER);
        percentTextPaint.setTextSize(scaledDensity * 26f);
        percentTextPaint.setFakeBoldText(true);
        percentTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary_light));

        labelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelTextPaint.setTextAlign(Paint.Align.CENTER);
        labelTextPaint.setTextSize(scaledDensity * 11f);
        labelTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary_light));

        tankClipPath = new Path();
        waveBackPath = new Path();
        waveFrontPath = new Path();
        tankBounds = new RectF();
    }

    public void setLevelPercent(int percent) {
        this.levelPercent = Math.max(0, Math.min(100, percent));
        invalidate();
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

    private void startWaveAnimation() {
        if (!areAnimationsEnabled()) return;

        if (waveAnimator != null) {
            waveAnimator.cancel();
        }

        waveAnimator = ValueAnimator.ofFloat(0f, (float) (2 * Math.PI));
        waveAnimator.setDuration(2200);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new LinearInterpolator());
        waveAnimator.addUpdateListener(a -> {
            waveShift = (float) a.getAnimatedValue();
            invalidate();
        });
        waveAnimator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startWaveAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float density = getResources().getDisplayMetrics().density;
        float pad = density * 4f;
        tankBounds.set(pad, pad, w - pad, h - pad);

        tankClipPath.reset();
        tankClipPath.addRoundRect(tankBounds, density * 16f, density * 16f, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.clipPath(tankClipPath);

        // Draw tank interior background
        canvas.drawRect(tankBounds, tankBgPaint);

        float density = getResources().getDisplayMetrics().density;
        float h = tankBounds.height();
        float w = tankBounds.width();

        // Water surface height based on percent
        float waterHeight = h * (levelPercent / 100f);
        float surfaceY = tankBounds.bottom - waterHeight;

        // Draw Back Wave
        waveBackPath.reset();
        waveBackPath.moveTo(tankBounds.left, tankBounds.bottom);
        waveBackPath.lineTo(tankBounds.left, surfaceY);
        float waveAmp = density * 5f;
        for (float x = 0; x <= w; x += 10) {
            float y = (float) (surfaceY + waveAmp * Math.sin((x / w) * 2 * Math.PI + waveShift));
            waveBackPath.lineTo(tankBounds.left + x, y);
        }
        waveBackPath.lineTo(tankBounds.right, tankBounds.bottom);
        waveBackPath.close();
        canvas.drawPath(waveBackPath, waveBackPaint);

        // Draw Front Wave
        waveFrontPath.reset();
        waveFrontPath.moveTo(tankBounds.left, tankBounds.bottom);
        waveFrontPath.lineTo(tankBounds.left, surfaceY);
        for (float x = 0; x <= w; x += 10) {
            float y = (float) (surfaceY + waveAmp * Math.sin((x / w) * 2 * Math.PI + waveShift + Math.PI / 2));
            waveFrontPath.lineTo(tankBounds.left + x, y);
        }
        waveFrontPath.lineTo(tankBounds.right, tankBounds.bottom);
        waveFrontPath.close();
        canvas.drawPath(waveFrontPath, waveFrontPaint);

        canvas.restore();

        // Draw tank border outline
        canvas.drawRoundRect(tankBounds, density * 16f, density * 16f, tankBorderPaint);

        // Center Percentage and Label
        float centerX = tankBounds.centerX();
        float centerY = tankBounds.centerY();

        percentTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary_light));
        labelTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary_light));

        canvas.drawText(levelPercent + "%", centerX, centerY + (density * 4f), percentTextPaint);
        canvas.drawText(getContext().getString(R.string.ui_sump_level), centerX, centerY + (density * 22f), labelTextPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (waveAnimator != null) {
            waveAnimator.cancel();
            waveAnimator = null;
        }
        super.onDetachedFromWindow();
    }
}
