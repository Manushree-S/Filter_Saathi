package org.filtersaathi.app.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.filtersaathi.app.R;

/**
 * Custom animated canvas view showing:
 * 1. 3D isometric sump tank with animated water ripples.
 * 2. Sensor on tank lid with pulsing rings and dashed animated signal to "Live readings: TDS, pH, level" box.
 * 3. Dashed flowing pipes through 4-stage filter column: Sediment, Carbon, RO filter, UV light (glowing UV indicator).
 * 4. Safe water drop with glowing halo.
 * 5. Localized labels (ui_g_tank, ui_g_fil, ui_g_safe, ui_g_live, ui_g_tds).
 * 6. Driven by ValueAnimator, stopped on onDetachedFromWindow() and when system animations are disabled.
 * 7. 8dp floating translation animation.
 */
public class SumpHeroView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dashedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path tankBodyPath = new Path();
    private final Path pipePath = new Path();
    private final Path outletPath = new Path();
    private final Path signalPath = new Path();
    private final Path dropPath = new Path();

    private final RectF ovalRect = new RectF();
    private final RectF badgeRect = new RectF();
    private final RectF filterRect = new RectF();
    private final RectF stageRect = new RectF();

    private ValueAnimator animator;
    private float animProgress = 0f;
    private float density = 1f;

    public SumpHeroView(Context context) {
        super(context);
        init();
    }

    public SumpHeroView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SumpHeroView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        strokePaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Content description for accessibility
        setContentDescription(getContext().getString(R.string.ui_g_tank) + ", "
                + getContext().getString(R.string.ui_g_fil) + ", "
                + getContext().getString(R.string.ui_g_safe));
    }

    private boolean areAnimationsEnabled() {
        try {
            float scale = Settings.Global.getFloat(
                    getContext().getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
            return scale > 0.0f;
        } catch (Exception e) {
            return true;
        }
    }

    private void startAnimation() {
        if (!areAnimationsEnabled()) {
            stopAnimation();
            animProgress = 0.5f;
            updateFloating();
            invalidate();
            return;
        }

        if (animator == null) {
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(2400);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animation -> {
                animProgress = (float) animation.getAnimatedValue();
                updateFloating();
                invalidate();
            });
        }

        if (!animator.isRunning()) {
            animator.start();
        }
    }

    private void stopAnimation() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    private void updateFloating() {
        // 8dp peak-to-peak (-4dp to +4dp) floating translation animation
        float floatOffset = 0f;
        if (areAnimationsEnabled()) {
            floatOffset = (float) (Math.sin(animProgress * 2.0 * Math.PI) * (4f * density));
        }

        View parent = (View) getParent();
        if (parent != null) {
            parent.setTranslationY(floatOffset);
        } else {
            setTranslationY(floatOffset);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Localized string labels
        String strLive = getContext().getString(R.string.ui_g_live);
        String strTds = getContext().getString(R.string.ui_g_tds);
        String strTank = getContext().getString(R.string.ui_g_tank);
        String strFilter = getContext().getString(R.string.ui_g_fil);
        String strSafe = getContext().getString(R.string.ui_g_safe);
        String strSed = getContext().getString(R.string.ui_g_sed);
        String strCar = getContext().getString(R.string.ui_g_car);
        String strRo = getContext().getString(R.string.ui_g_ro);
        String strUv = getContext().getString(R.string.ui_g_uv);

        // --- 1. Sump Tank Dimensions (Isometric) ---
        float tankCx = w * 0.20f;
        float tankTopY = h * 0.44f;
        float tankRx = Math.min(w * 0.13f, 42f * density);
        float tankRy = tankRx * 0.40f;
        float tankH = 62f * density;
        float tankBottomY = tankTopY + tankH;

        // Tank Body Path
        tankBodyPath.reset();
        tankBodyPath.moveTo(tankCx - tankRx, tankTopY);
        tankBodyPath.lineTo(tankCx - tankRx, tankBottomY);
        ovalRect.set(tankCx - tankRx, tankBottomY - tankRy, tankCx + tankRx, tankBottomY + tankRy);
        tankBodyPath.arcTo(ovalRect, 180f, -180f, false);
        tankBodyPath.lineTo(tankCx + tankRx, tankTopY);
        ovalRect.set(tankCx - tankRx, tankTopY - tankRy, tankCx + tankRx, tankTopY + tankRy);
        tankBodyPath.arcTo(ovalRect, 0f, 180f, false);
        tankBodyPath.close();

        // Tank Body Fill & Stroke
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(
                tankCx - tankRx, tankTopY, tankCx + tankRx, tankBottomY,
                0xFF1E293B, 0xFF0F172A, Shader.TileMode.CLAMP));
        canvas.drawPath(tankBodyPath, paint);
        paint.setShader(null);

        strokePaint.setColor(0x4038BDF8);
        strokePaint.setStrokeWidth(1.5f * density);
        canvas.drawPath(tankBodyPath, strokePaint);

        // Water level inside tank (semi-transparent blue volume)
        float waterTopY = tankTopY + tankH * 0.38f;
        Path waterPath = new Path();
        waterPath.moveTo(tankCx - tankRx, waterTopY);
        waterPath.lineTo(tankCx - tankRx, tankBottomY);
        ovalRect.set(tankCx - tankRx, tankBottomY - tankRy, tankCx + tankRx, tankBottomY + tankRy);
        waterPath.arcTo(ovalRect, 180f, -180f, false);
        waterPath.lineTo(tankCx + tankRx, waterTopY);
        ovalRect.set(tankCx - tankRx, waterTopY - tankRy, tankCx + tankRx, waterTopY + tankRy);
        waterPath.arcTo(ovalRect, 0f, 180f, false);
        waterPath.close();

        paint.setColor(0x400284C7);
        canvas.drawPath(waterPath, paint);

        // Water surface ellipse
        ovalRect.set(tankCx - tankRx * 0.96f, waterTopY - tankRy * 0.96f,
                tankCx + tankRx * 0.96f, waterTopY + tankRy * 0.96f);
        paint.setColor(0x5500E5FF);
        canvas.drawOval(ovalRect, paint);

        // Animated Water Ripples (2 expanding ellipses)
        for (int i = 0; i < 2; i++) {
            float phase = (animProgress + i * 0.5f) % 1.0f;
            float rW = (0.25f + 0.70f * phase) * (tankRx * 0.96f);
            float rH = rW * 0.40f;
            int alpha = (int) (180 * (1.0f - phase));
            strokePaint.setColor(Color.argb(alpha, 0, 229, 255));
            strokePaint.setStrokeWidth(1.2f * density);
            ovalRect.set(tankCx - rW, waterTopY - rH, tankCx + rW, waterTopY + rH);
            canvas.drawOval(ovalRect, strokePaint);
        }

        // Tank Top Lid Ellipse
        ovalRect.set(tankCx - tankRx, tankTopY - tankRy, tankCx + tankRx, tankTopY + tankRy);
        paint.setColor(0xFF1D4ED8);
        canvas.drawOval(ovalRect, paint);
        strokePaint.setColor(0xFF60A5FA);
        strokePaint.setStrokeWidth(1.5f * density);
        canvas.drawOval(ovalRect, strokePaint);

        // Tank Label Below
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(11f * density);
        textPaint.setFakeBoldText(true);
        canvas.drawText(strTank, tankCx, tankBottomY + tankRy + 14f * density, textPaint);

        // --- 2. Sensor on Tank Lid ---
        // Sensor device body
        paint.setColor(0xFF0F172A);
        canvas.drawCircle(tankCx, tankTopY, 6f * density, paint);
        strokePaint.setColor(0xFF38BDF8);
        strokePaint.setStrokeWidth(1.5f * density);
        canvas.drawCircle(tankCx, tankTopY, 6f * density, strokePaint);

        // Sensor pulsing rings (2 rings)
        for (int i = 0; i < 2; i++) {
            float phase = (animProgress + i * 0.5f) % 1.0f;
            float ringRadius = 6f * density + 16f * density * phase;
            int ringAlpha = (int) (220 * (1.0f - phase));
            strokePaint.setColor(Color.argb(ringAlpha, 56, 189, 248));
            strokePaint.setStrokeWidth(1.2f * density);
            canvas.drawCircle(tankCx, tankTopY, ringRadius, strokePaint);
        }

        // Inner glowing green sensor dot
        paint.setColor(0xFF10B981);
        canvas.drawCircle(tankCx, tankTopY, 2.5f * density, paint);

        // --- 3. "Live readings" Badge & Dashed Signal Line ---
        float badgeW = 108f * density;
        float badgeH = 34f * density;
        float badgeLeft = Math.max(8f * density, tankCx - badgeW * 0.45f);
        float badgeTop = Math.max(6f * density, tankTopY - 48f * density);
        badgeRect.set(badgeLeft, badgeTop, badgeLeft + badgeW, badgeTop + badgeH);

        // Dashed signal line from sensor to badge
        signalPath.reset();
        signalPath.moveTo(tankCx, tankTopY - 6f * density);
        signalPath.lineTo(badgeLeft + badgeW * 0.5f, badgeTop + badgeH);

        dashedPaint.setColor(0xFF38BDF8);
        dashedPaint.setStrokeWidth(1.5f * density);
        dashedPaint.setPathEffect(new DashPathEffect(
                new float[]{5f * density, 5f * density}, -animProgress * 10f * density));
        canvas.drawPath(signalPath, dashedPaint);
        dashedPaint.setPathEffect(null);

        // Badge Container (Dark translucent glass with cyan stroke)
        paint.setColor(0xE60B132B);
        canvas.drawRoundRect(badgeRect, 8f * density, 8f * density, paint);
        strokePaint.setColor(0x4D38BDF8);
        strokePaint.setStrokeWidth(1f * density);
        canvas.drawRoundRect(badgeRect, 8f * density, 8f * density, strokePaint);

        // Blinking green dot inside badge
        int dotAlpha = (int) (140 + 115 * Math.abs(Math.sin(animProgress * Math.PI)));
        paint.setColor(Color.argb(dotAlpha, 16, 185, 129));
        canvas.drawCircle(badgeLeft + 10f * density, badgeTop + 12f * density, 3f * density, paint);

        // "Live readings" text
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(0xFF38BDF8);
        textPaint.setTextSize(9.5f * density);
        textPaint.setFakeBoldText(true);
        canvas.drawText(strLive, badgeLeft + 17f * density, badgeTop + 15f * density, textPaint);

        // "TDS · pH · level" text
        textPaint.setColor(0xFFCBD5E1);
        textPaint.setTextSize(8.5f * density);
        textPaint.setFakeBoldText(false);
        canvas.drawText(strTds, badgeLeft + 10f * density, badgeTop + 27f * density, textPaint);

        // --- 4. 4-Stage Filter Column (Center-Right) ---
        float filterX = w * 0.53f;
        float filterW = Math.min(w * 0.18f, 54f * density);
        float filterH = 96f * density;
        float filterTopY = h * 0.30f;
        filterRect.set(filterX, filterTopY, filterX + filterW, filterTopY + filterH);

        // Filter Column Body & Border
        paint.setColor(0xE60F172A);
        canvas.drawRoundRect(filterRect, 8f * density, 8f * density, paint);
        strokePaint.setColor(0xFF475569);
        strokePaint.setStrokeWidth(1.5f * density);
        canvas.drawRoundRect(filterRect, 8f * density, 8f * density, strokePaint);

        // 4 Stages
        float stageH = filterH / 4f;

        // Stage 1: Sediment (Amber #D97706)
        stageRect.set(filterX + 2f * density, filterTopY + 2f * density,
                filterX + filterW - 2f * density, filterTopY + stageH);
        paint.setColor(0xCCB45309);
        canvas.drawRoundRect(stageRect, 6f * density, 6f * density, paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(0xFFFDE68A);
        textPaint.setTextSize(8f * density);
        textPaint.setFakeBoldText(true);
        canvas.drawText(strSed, filterX + filterW * 0.5f, filterTopY + stageH * 0.65f, textPaint);

        // Stage 2: Carbon (Dark slate #334155)
        stageRect.set(filterX + 2f * density, filterTopY + stageH,
                filterX + filterW - 2f * density, filterTopY + stageH * 2f);
        paint.setColor(0xCC334155);
        canvas.drawRect(stageRect, paint);
        textPaint.setColor(0xFFE2E8F0);
        canvas.drawText(strCar, filterX + filterW * 0.5f, filterTopY + stageH * 1.65f, textPaint);

        // Stage 3: RO Filter (Blue #0284C7)
        stageRect.set(filterX + 2f * density, filterTopY + stageH * 2f,
                filterX + filterW - 2f * density, filterTopY + stageH * 3f);
        paint.setColor(0xCC0284C7);
        canvas.drawRect(stageRect, paint);
        textPaint.setColor(0xFFBAE6FD);
        canvas.drawText(strRo, filterX + filterW * 0.5f, filterTopY + stageH * 2.65f, textPaint);

        // Stage 4: UV Light (Violet #7C3AED with glowing UV indicator)
        stageRect.set(filterX + 2f * density, filterTopY + stageH * 3f,
                filterX + filterW - 2f * density, filterTopY + filterH - 2f * density);
        paint.setColor(0xCC6D28D9);
        canvas.drawRoundRect(stageRect, 6f * density, 6f * density, paint);

        // Glowing UV indicator pulse
        int uvPulseAlpha = (int) (140 + 100 * Math.abs(Math.sin(animProgress * Math.PI)));
        paint.setShader(new RadialGradient(
                filterX + filterW * 0.5f, filterTopY + stageH * 3.5f, filterW * 0.45f,
                Color.argb(uvPulseAlpha, 192, 132, 252), Color.argb(0, 109, 40, 217),
                Shader.TileMode.CLAMP));
        canvas.drawRoundRect(stageRect, 6f * density, 6f * density, paint);
        paint.setShader(null);

        textPaint.setColor(0xFFF3E8FF);
        canvas.drawText(strUv, filterX + filterW * 0.5f, filterTopY + stageH * 3.65f, textPaint);

        // Filter Column Label Below
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(11f * density);
        textPaint.setFakeBoldText(true);
        canvas.drawText(strFilter, filterX + filterW * 0.5f, filterTopY + filterH + 14f * density, textPaint);

        // --- 5. Connecting Pipe (Tank -> Filter) ---
        float pipeStartY = tankTopY + tankH * 0.65f;
        float pipeEndY = filterTopY + 12f * density;
        pipePath.reset();
        pipePath.moveTo(tankCx + tankRx, pipeStartY);
        pipePath.lineTo(tankCx + tankRx + 14f * density, pipeStartY);
        pipePath.lineTo(filterX - 10f * density, pipeEndY);
        pipePath.lineTo(filterX, pipeEndY);

        // Outer pipe tube
        strokePaint.setColor(0x33FFFFFF);
        strokePaint.setStrokeWidth(5f * density);
        canvas.drawPath(pipePath, strokePaint);

        // Inner dashed flowing water
        dashedPaint.setColor(0xFF38BDF8);
        dashedPaint.setStrokeWidth(2.5f * density);
        dashedPaint.setPathEffect(new DashPathEffect(
                new float[]{8f * density, 8f * density}, -animProgress * 16f * density));
        canvas.drawPath(pipePath, dashedPaint);
        dashedPaint.setPathEffect(null);

        // --- 6. Safe Water Drop (Far Right) ---
        float dropCx = w * 0.85f;
        float dropCy = h * 0.48f;
        float dropR = 15f * density;

        // Outlet pipe from UV filter stage to Safe Water Drop
        float outletStartY = filterTopY + filterH - 12f * density;
        outletPath.reset();
        outletPath.moveTo(filterX + filterW, outletStartY);
        outletPath.lineTo(filterX + filterW + 12f * density, outletStartY);
        outletPath.lineTo(dropCx - dropR - 6f * density, dropCy);
        outletPath.lineTo(dropCx - dropR, dropCy);

        // Outer tube
        strokePaint.setColor(0x33FFFFFF);
        strokePaint.setStrokeWidth(5f * density);
        canvas.drawPath(outletPath, strokePaint);

        // Inner pure water dashed flow
        dashedPaint.setColor(0xFF00E5FF);
        dashedPaint.setStrokeWidth(2.5f * density);
        dashedPaint.setPathEffect(new DashPathEffect(
                new float[]{8f * density, 8f * density}, -animProgress * 16f * density));
        canvas.drawPath(outletPath, dashedPaint);
        dashedPaint.setPathEffect(null);

        // Safe Water Drop Pulsing Halo
        float haloPhase = (animProgress + 0.3f) % 1.0f;
        float haloRadius = dropR + 8f * density * haloPhase;
        int haloAlpha = (int) (160 * (1.0f - haloPhase));
        strokePaint.setColor(Color.argb(haloAlpha, 0, 229, 255));
        strokePaint.setStrokeWidth(1.2f * density);
        canvas.drawCircle(dropCx, dropCy, haloRadius, strokePaint);

        // Teardrop Shape
        dropPath.reset();
        dropPath.moveTo(dropCx, dropCy - dropR * 1.35f);
        dropPath.cubicTo(dropCx + dropR * 1.05f, dropCy,
                dropCx + dropR * 0.9f, dropCy + dropR * 0.95f,
                dropCx, dropCy + dropR * 0.95f);
        dropPath.cubicTo(dropCx - dropR * 0.9f, dropCy + dropR * 0.95f,
                dropCx - dropR * 1.05f, dropCy,
                dropCx, dropCy - dropR * 1.35f);
        dropPath.close();

        // Teardrop Fill & Highlight
        paint.setShader(new LinearGradient(
                dropCx, dropCy - dropR, dropCx, dropCy + dropR,
                0xFF00E5FF, 0xFF0284C7, Shader.TileMode.CLAMP));
        canvas.drawPath(dropPath, paint);
        paint.setShader(null);

        // Teardrop Glare Specular Reflection
        paint.setColor(0x80FFFFFF);
        canvas.drawCircle(dropCx - dropR * 0.3f, dropCy - dropR * 0.2f, 2.5f * density, paint);

        // Safe Water Label Below
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(11f * density);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(strSafe, dropCx, dropCy + dropR + 14f * density, textPaint);
    }
}
