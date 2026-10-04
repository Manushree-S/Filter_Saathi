package org.filtersaathi.app.util;

import android.content.Context;
import androidx.core.content.ContextCompat;
import org.filtersaathi.app.R;
import org.filtersaathi.app.domain.model.WaterQualityStatus;

/**
 * Utility class in Java for mapping water parameters and health scores to colors and themes.
 */
public final class ColorMapper {

    private ColorMapper() {
        // Prevent instantiation
    }

    public static int getColorForHealthScore(Context context, int score) {
        if (score >= 80) {
            return ContextCompat.getColor(context, R.color.status_success);
        } else if (score >= 50) {
            return ContextCompat.getColor(context, R.color.status_warning);
        } else {
            return ContextCompat.getColor(context, R.color.status_danger);
        }
    }

    public static int getColorForQualityStatus(Context context, WaterQualityStatus status) {
        if (status == null) {
            return ContextCompat.getColor(context, R.color.text_secondary_light);
        }
        switch (status) {
            case SAFE:
                return ContextCompat.getColor(context, R.color.status_success);
            case WATCH:
                return ContextCompat.getColor(context, R.color.status_warning);
            case UNSAFE:
            default:
                return ContextCompat.getColor(context, R.color.status_danger);
        }
    }

    public static int getTintForQualityStatus(Context context, WaterQualityStatus status) {
        if (status == null) {
            return ContextCompat.getColor(context, R.color.divider_light);
        }
        switch (status) {
            case SAFE:
                return ContextCompat.getColor(context, R.color.status_success_tint);
            case WATCH:
                return ContextCompat.getColor(context, R.color.status_warning_tint);
            case UNSAFE:
            default:
                return ContextCompat.getColor(context, R.color.status_danger_tint);
        }
    }
}
