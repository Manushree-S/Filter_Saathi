package org.filtersaathi.app.ui.officer;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.filtersaathi.app.R;
import org.filtersaathi.app.domain.model.OfficerVillageRow;

import java.util.ArrayList;
import java.util.List;

public class VillageTableAdapter extends RecyclerView.Adapter<VillageTableAdapter.ViewHolder> {

    private final List<OfficerVillageRow> rows = new ArrayList<>();

    public void setRows(List<OfficerVillageRow> newRows) {
        rows.clear();
        if (newRows != null) {
            rows.addAll(newRows);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_officer_village_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OfficerVillageRow item = rows.get(position);
        Context context = holder.itemView.getContext();

        holder.tvVillageName.setText(item.getVillage());
        String filterCountText = item.getFiltersCount() + " " + context.getString(R.string.ui_th_filters).toLowerCase();
        holder.tvFilterCount.setText(filterCountText);

        int score = item.getScore();
        holder.pbScore.setProgress(score);
        holder.tvScoreValue.setText(String.valueOf(score));

        // Status tag styling
        GradientDrawable tagBg = new GradientDrawable();
        tagBg.setCornerRadius(context.getResources().getDisplayMetrics().density * 12f);

        if (score >= 80) {
            // Good
            holder.tvStatusTag.setText(R.string.ui_st_good);
            holder.tvStatusTag.setTextColor(ContextCompat.getColor(context, R.color.alert_green_text));
            tagBg.setColor(ContextCompat.getColor(context, R.color.alert_green_bg));
        } else if (score >= 50) {
            // Watch
            holder.tvStatusTag.setText(R.string.ui_st_watch);
            holder.tvStatusTag.setTextColor(ContextCompat.getColor(context, R.color.alert_amber_text));
            tagBg.setColor(ContextCompat.getColor(context, R.color.alert_amber_bg));
        } else {
            // Action needed
            holder.tvStatusTag.setText(R.string.ui_st_action);
            holder.tvStatusTag.setTextColor(ContextCompat.getColor(context, R.color.alert_red_text));
            tagBg.setColor(ContextCompat.getColor(context, R.color.alert_red_bg));
        }
        holder.tvStatusTag.setBackground(tagBg);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVillageName;
        TextView tvFilterCount;
        ProgressBar pbScore;
        TextView tvScoreValue;
        TextView tvStatusTag;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVillageName = itemView.findViewById(R.id.tvVillageName);
            tvFilterCount = itemView.findViewById(R.id.tvFilterCount);
            pbScore = itemView.findViewById(R.id.pbScore);
            tvScoreValue = itemView.findViewById(R.id.tvScoreValue);
            tvStatusTag = itemView.findViewById(R.id.tvStatusTag);
        }
    }
}
