package org.filtersaathi.app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Draws the Karnataka district map from assets/karnataka_districts.json.
 * Java is used on purpose: this is UI-layer code.
 */
public class DistrictMapView extends View {

    public interface Listener {
        void onDistrictSelected(@NonNull String districtKey);
    }

    private static final class District {
        final List<String> keys = new ArrayList<>();   // first key is the polygon's own district
        final List<String> namesEn = new ArrayList<>();
        final List<String> namesKn = new ArrayList<>();
        Path source;
        final Path scaled = new Path();
        final Region region = new Region();
        float labelX, labelY;
        int colorIndex;
        boolean showLabel;
    }

    private final List<District> districts = new ArrayList<>();
    private int[] palette = {0xFFCFE0F7, 0xFFD4EBDD, 0xFFFBE7C0, 0xFFE1D8F2};
    private float viewBoxW = 624f, viewBoxH = 820f, scale = 1f;

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix matrix = new Matrix();

    private String selectedKey;
    private boolean kannada;
    private Listener listener;
    @Nullable private Map<String, Integer> fillOverrides; // used by the officer coverage map

    public DistrictMapView(Context context) { this(context, null); }

    public DistrictMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        float d = getResources().getDisplayMetrics().density;
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStrokeWidth(1.5f * d);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        selectedStroke.setStyle(Paint.Style.STROKE);
        selectedStroke.setColor(0xFFE8801A);
        selectedStroke.setStrokeWidth(2.5f * d);
        selectedStroke.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        haloPaint.setTextAlign(Paint.Align.CENTER);
        haloPaint.setFakeBoldText(true);
        haloPaint.setStyle(Paint.Style.STROKE);
        haloPaint.setStrokeWidth(2.6f * d);
        haloPaint.setStrokeJoin(Paint.Join.ROUND);
        loadFromAssets("karnataka_districts.json");
    }

    private void loadFromAssets(String fileName) {
        try (InputStream in = getContext().getAssets().open(fileName)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
            JSONObject root = new JSONObject(new String(out.toByteArray(), StandardCharsets.UTF_8));
            viewBoxW = (float) root.getDouble("viewBoxWidth");
            viewBoxH = (float) root.getDouble("viewBoxHeight");
            JSONArray pal = root.getJSONArray("palette");
            palette = new int[pal.length()];
            for (int i = 0; i < pal.length(); i++) palette[i] = Color.parseColor(pal.getString(i));
            JSONArray arr = root.getJSONArray("districts");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                District d = new District();
                d.keys.add(o.getString("key"));
                JSONArray also = o.getJSONArray("alsoCovers");
                for (int k = 0; k < also.length(); k++) d.keys.add(also.getString(k));
                JSONArray en = o.getJSONArray("nameEn");
                JSONArray kn = o.getJSONArray("nameKn");
                for (int k = 0; k < en.length(); k++) { d.namesEn.add(en.getString(k)); d.namesKn.add(kn.getString(k)); }
                d.source = PathParser.createPathFromPathData(o.getString("path"));
                d.labelX = (float) o.getDouble("labelX");
                d.labelY = (float) o.getDouble("labelY");
                d.colorIndex = o.getInt("colorIndex");
                d.showLabel = o.getBoolean("showLabel");
                districts.add(d);
            }
        } catch (Exception e) {
            // Leave the map empty if the asset is missing or malformed.
        }
    }

    // ---- public API ----
    public void setListener(@Nullable Listener l) { listener = l; }

    public void setKannada(boolean value) { kannada = value; invalidate(); }

    public void setSelectedKey(@Nullable String key) { selectedKey = key; invalidate(); }

    @Nullable public String getSelectedKey() { return selectedKey; }

    /** Colours by district key. Pass null to go back to the political palette. */
    public void setFillOverrides(@Nullable Map<String, Integer> colors) { fillOverrides = colors; invalidate(); }

    // ---- layout ----
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int w = MeasureSpec.getSize(widthSpec);
        setMeasuredDimension(w, Math.round(w * viewBoxH / viewBoxW));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scale = Math.min(w / viewBoxW, h / viewBoxH);
        matrix.setScale(scale, scale);
        Region clip = new Region(0, 0, w, h);
        for (District d : districts) {
            if (d.source != null) {
                d.source.transform(matrix, d.scaled);
                d.region.setPath(d.scaled, clip);
            }
        }
        float sp = getResources().getDisplayMetrics().scaledDensity;
        float size = Math.max(9f * sp, 10f * scale);
        textPaint.setTextSize(size);
        haloPaint.setTextSize(size);
    }

    // ---- drawing ----
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        District selected = null;
        for (District d : districts) {
            if (selectedKey != null && d.keys.contains(selectedKey)) { selected = d; continue; }
            Integer override = fillOverrides == null ? null : fillOverrides.get(d.keys.get(0));
            fillPaint.setColor(override != null ? override : palette[d.colorIndex % palette.length]);
            canvas.drawPath(d.scaled, fillPaint);
            canvas.drawPath(d.scaled, strokePaint);
        }
        if (selected != null) {
            fillPaint.setColor(0xFF0F2A5C);
            canvas.drawPath(selected.scaled, fillPaint);
            canvas.drawPath(selected.scaled, selectedStroke);
        }
        for (District d : districts) {
            if (!d.showLabel) continue;
            boolean on = d == selected;
            haloPaint.setColor(on ? 0xFF0F2A5C : Color.WHITE);
            textPaint.setColor(on ? Color.WHITE : 0xFF17213A);
            String label = kannada ? d.namesKn.get(0) : d.namesEn.get(0);
            float x = d.labelX * scale;
            float y = d.labelY * scale - (textPaint.ascent() + textPaint.descent()) / 2f;
            canvas.drawText(label, x, y, haloPaint);
            canvas.drawText(label, x, y, textPaint);
        }
    }

    // ---- touch: tap selects; tapping a shared outline (Ballari / Vijayanagara) cycles through its districts ----
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) return true;
        if (e.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) e.getX(), y = (int) e.getY();
            for (District d : districts) {
                if (d.region.contains(x, y)) {
                    int i = selectedKey == null ? -1 : d.keys.indexOf(selectedKey);
                    selectedKey = d.keys.get((i + 1) % d.keys.size());
                    invalidate();
                    performClick();
                    if (listener != null) listener.onDistrictSelected(selectedKey);
                    return true;
                }
            }
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean performClick() { return super.performClick(); }
}
