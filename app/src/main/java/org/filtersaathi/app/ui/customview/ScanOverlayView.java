package org.filtersaathi.app.ui.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.filtersaathi.app.R;

/**
 * ScanOverlayView: Dark viewfinder overlay with four orange corner brackets
 * and an animated sweeping scan laser line for test strip scanning.
 */
public class ScanOverlayView extends View {

    private Paint maskPaint;
    private Paint bracketPaint;
    private Paint scanLinePaint;
    private Paint stripHolderPaint;
    private Paint[] padPaints;

    private RectF viewfinderRect;
    private RectF stripRect;
    private Path cornerPath;

    private boolean isScanning = false;
    private float scanProgress = 0f; // 0.0 to 1.0 (top to bottom)
    private ValueAnimator scanAnimator;
    private Runnable onCompleteCallback;

    public ScanOverlayView(Context context) {
        super(context);
        init();
    }

    public ScanOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScanOverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setColor(Color.parseColor("#E60D131F")); // Deep dark backdrop

        bracketPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bracketPaint.setStyle(Paint.Style.STROKE);
        bracketPaint.setStrokeWidth(density * 4f);
        bracketPaint.setColor(ContextCompat.getColor(getContext(), R.color.accent_saffron));
        bracketPaint.setStrokeCap(Paint.Cap.ROUND);
        bracketPaint.setStrokeJoin(Paint.Join.ROUND);

        scanLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scanLinePaint.setStyle(Paint.Style.STROKE);
        scanLinePaint.setStrokeWidth(density * 3f);
        scanLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.aqua));

        stripHolderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stripHolderPaint.setColor(Color.parseColor("#F1F5F9"));

        int[] padColors = new int[]{
                Color.parseColor("#E6C84A"), // Pad 1 (Yellow)
                Color.parseColor("#A8C25C"), // Pad 2 (Olive)
                Color.parseColor("#D88BA8"), // Pad 3 (Pink)
                Color.parseColor("#C4572A")  // Pad 4 (Rust)
        };
        padPaints = new Paint[padColors.length];
        for (int i = 0; i < padColors.length; i++) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(padColors[i]);
            padPaints[i] = p;
        }

        viewfinderRect = new RectF();
        stripRect = new RectF();
        cornerPath = new Path();
    }

    public void startScan(Runnable onComplete) {
        this.onCompleteCallback = onComplete;
        this.isScanning = true;
        this.scanProgress = 0f;

        if (!areAnimationsEnabled()) {
            this.isScanning = false;
            this.scanProgress = 1.0f;
            invalidate();
            if (onCompleteCallback != null) {
                post(onCompleteCallback);
            }
            return;
        }

        if (scanAnimator != null) {
            scanAnimator.cancel();
        }

        scanAnimator = ValueAnimator.ofFloat(0f, 1f);
        scanAnimator.setDuration(1800); // Spec: sweeping scan line for 1.8 seconds
        scanAnimator.setInterpolator(new LinearInterpolator());
        scanAnimator.addUpdateListener(animation -> {
            scanProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        scanAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isScanning = false;
                invalidate();
                if (onCompleteCallback != null) {
                    onCompleteCallback.run();
                }
            }
        });
        scanAnimator.start();
    }

    public void resetScan() {
        if (scanAnimator != null) {
            scanAnimator.cancel();
            scanAnimator = null;
        }
        isScanning = false;
        scanProgress = 0f;
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float density = getResources().getDisplayMetrics().density;

        float vfWidth = Math.min(w * 0.85f, density * 280f);
        float vfHeight = Math.min(h * 0.75f, density * 180f);

        float left = (w - vfWidth) / 2f;
        float top = (h - vfHeight) / 2f;
        viewfinderRect.set(left, top, left + vfWidth, top + vfHeight);

        // Simulated test strip horizontally centered inside viewfinder
        float stripW = vfWidth * 0.65f;
        float stripH = density * 28f;
        float sLeft = (w - stripW) / 2f;
        float sTop = (h - stripH) / 2f;
        stripRect.set(sLeft, sTop, sLeft + stripW, sTop + stripH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float density = getResources().getDisplayMetrics().density;
        float cornerLen = density * 24f;

        // 1. Draw dark background mask outside / inside viewfinder
        canvas.drawRoundRect(viewfinderRect, density * 12f, density * 12f, maskPaint);

        // 2. Draw simulated test strip with 4 colored pads inside
        canvas.drawRoundRect(stripRect, density * 6f, density * 6f, stripHolderPaint);
        float padSize = density * 16f;
        float padGap = (stripRect.width() - (padSize * 4f)) / 5f;
        for (int i = 0; i < 4; i++) {
            float pLeft = stripRect.left + padGap + i * (padSize + padGap);
            float pTop = stripRect.centerY() - (padSize / 2f);
            canvas.drawRoundRect(new RectF(pLeft, pTop, pLeft + padSize, pTop + padSize),
                    density * 3f, density * 3f, padPaints[i]);
        }

        // 3. Draw Four Orange Corner Brackets
        cornerPath.reset();

        // Top-Left
        cornerPath.moveTo(viewfinderRect.left, viewfinderRect.top + cornerLen);
        cornerPath.lineTo(viewfinderRect.left, viewfinderRect.top);
        cornerPath.lineTo(viewfinderRect.left + cornerLen, viewfinderRect.top);

        // Top-Right
        cornerPath.moveTo(viewfinderRect.right - cornerLen, viewfinderRect.top);
        cornerPath.lineTo(viewfinderRect.right, viewfinderRect.top);
        cornerPath.lineTo(viewfinderRect.right, viewfinderRect.top + cornerLen);

        // Bottom-Right
        cornerPath.moveTo(viewfinderRect.right, viewfinderRect.bottom - cornerLen);
        cornerPath.lineTo(viewfinderRect.right, viewfinderRect.bottom);
        cornerPath.lineTo(viewfinderRect.right - cornerLen, viewfinderRect.bottom);

        // Bottom-Left
        cornerPath.moveTo(viewfinderRect.left + cornerLen, viewfinderRect.bottom);
        cornerPath.lineTo(viewfinderRect.left, viewfinderRect.bottom);
        cornerPath.lineTo(viewfinderRect.left, viewfinderRect.bottom - cornerLen);

        canvas.drawPath(cornerPath, bracketPaint);

        // 4. Draw Sweeping Scan Laser Line if scanning
        if (isScanning) {
            float lineY = viewfinderRect.top + (scanProgress * viewfinderRect.height());
            canvas.drawLine(viewfinderRect.left + (density * 4f), lineY,
                    viewfinderRect.right - (density * 4f), lineY, scanLinePaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (scanAnimator != null) {
            scanAnimator.cancel();
            scanAnimator = null;
        }
        super.onDetachedFromWindow();
    }
}
