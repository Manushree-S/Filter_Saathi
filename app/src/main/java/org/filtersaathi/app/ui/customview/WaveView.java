package org.filtersaathi.app.ui.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;

public class WaveView extends View {

    private Paint wavePaint;
    private Path wavePath;
    private float waveOffset = 0f;
    private ValueAnimator waveAnimator;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setColor(0x22FFFFFF); // Soft white wave tint on hero card
        wavePaint.setStyle(Paint.Style.FILL);
        wavePath = new Path();

        startWaveAnimation();
    }

    private void startWaveAnimation() {
        waveAnimator = ValueAnimator.ofFloat(0f, 1f);
        waveAnimator.setDuration(2400);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new LinearInterpolator());
        waveAnimator.addUpdateListener(animation -> {
            waveOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
        waveAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (waveAnimator != null) {
            waveAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;

        wavePath.reset();
        wavePath.moveTo(0, height);

        float waveLength = width * 0.75f;
        float amplitude = height * 0.25f;
        float baseHeight = height * 0.65f;

        float startX = -waveLength + (waveOffset * waveLength);

        for (float x = startX; x <= width + waveLength; x += 30) {
            float y = (float) (baseHeight + amplitude * Math.sin((x / waveLength) * 2 * Math.PI));
            if (x == startX) {
                wavePath.lineTo(x, y);
            } else {
                wavePath.lineTo(x, y);
            }
        }

        wavePath.lineTo(width, height);
        wavePath.lineTo(0, height);
        wavePath.close();

        canvas.drawPath(wavePath, wavePaint);
    }
}
