package org.filtersaathi.app.ui.myfilters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.filtersaathi.app.R;
import org.filtersaathi.app.domain.model.FilterCareStatus;
import org.filtersaathi.app.domain.model.FilterDevice;
import org.filtersaathi.app.domain.model.FilterItemLife;

import java.util.ArrayList;
import java.util.List;

public class MyFiltersAdapter extends RecyclerView.Adapter<MyFiltersAdapter.ViewHolder> {

    public interface OnDeviceClickListener {
        void onDeviceClick(FilterDevice device);
    }

    private List<FilterDevice> devices = new ArrayList<>();
    private final OnDeviceClickListener listener;

    public MyFiltersAdapter(OnDeviceClickListener listener) {
        this.listener = listener;
    }

    public void setDevices(List<FilterDevice> devices) {
        this.devices = devices != null ? devices : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterDevice device = devices.get(position);
        holder.bind(device, listener);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDeviceName;
        private final TextView tvDeviceTypeAndInstall;
        private final TextView tvStatusChip;
        private final LinearLayout layoutLifeItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvDeviceTypeAndInstall = itemView.findViewById(R.id.tvDeviceTypeAndInstall);
            tvStatusChip = itemView.findViewById(R.id.tvStatusChip);
            layoutLifeItems = itemView.findViewById(R.id.layoutLifeItems);
        }

        public void bind(FilterDevice device, OnDeviceClickListener listener) {
            Context ctx = itemView.getContext();

            // Device Name
            int nameResId = ctx.getResources().getIdentifier(device.getNameKey(), "string", ctx.getPackageName());
            String displayName = nameResId != 0 ? ctx.getString(nameResId) : device.getNameFallback();
            tvDeviceName.setText(displayName);

            // Device Type
            int typeResId = ctx.getResources().getIdentifier(device.getTypeKey(), "string", ctx.getPackageName());
            String displayType = typeResId != 0 ? ctx.getString(typeResId) : device.getTypeKey();
            String typeAndInstall = displayType + " · " + ctx.getString(R.string.ui_installed_on, device.getInstallDate());
            tvDeviceTypeAndInstall.setText(typeAndInstall);

            // Overall Status Chip
            FilterCareStatus status = device.getOverallStatus(null);
            switch (status) {
                case URGENT:
                    tvStatusChip.setText(R.string.ui_status_urgent);
                    tvStatusChip.setBackgroundResource(R.drawable.bg_sample_tag);
                    tvStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.alert_red_bg)));
                    tvStatusChip.setTextColor(ContextCompat.getColor(ctx, R.color.alert_red_text));
                    break;
                case SOON:
                    tvStatusChip.setText(R.string.ui_status_soon);
                    tvStatusChip.setBackgroundResource(R.drawable.bg_sample_tag);
                    tvStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.alert_amber_bg)));
                    tvStatusChip.setTextColor(ContextCompat.getColor(ctx, R.color.alert_amber_text));
                    break;
                case OK:
                default:
                    tvStatusChip.setText(R.string.ui_status_ok);
                    tvStatusChip.setBackgroundResource(R.drawable.bg_sample_tag);
                    tvStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.alert_green_bg)));
                    tvStatusChip.setTextColor(ContextCompat.getColor(ctx, R.color.alert_green_text));
                    break;
            }

            // Populate Life Bars
            layoutLifeItems.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(ctx);

            for (FilterItemLife item : device.getItems()) {
                View barView = inflater.inflate(R.layout.item_life_bar, layoutLifeItems, false);
                TextView tvItemName = barView.findViewById(R.id.tvItemName);
                TextView tvDaysLeft = barView.findViewById(R.id.tvDaysLeft);
                ProgressBar pbLifeBar = barView.findViewById(R.id.pbLifeBar);

                int itemResId = ctx.getResources().getIdentifier(item.getItemKey(), "string", ctx.getPackageName());
                String itemDisplayName = itemResId != 0 ? ctx.getString(itemResId) : item.getItemKey();
                tvItemName.setText(itemDisplayName);

                int daysLeft = item.getDaysLeft(null);
                if (daysLeft < 0) {
                    tvDaysLeft.setText(ctx.getString(R.string.ui_overdue_days, Math.abs(daysLeft)));
                    tvDaysLeft.setTextColor(ContextCompat.getColor(ctx, R.color.status_danger));
                } else {
                    tvDaysLeft.setText(ctx.getString(R.string.ui_days_left, daysLeft));
                    if (daysLeft <= 7) {
                        tvDaysLeft.setTextColor(ContextCompat.getColor(ctx, R.color.status_danger));
                    } else if (daysLeft <= 30) {
                        tvDaysLeft.setTextColor(ContextCompat.getColor(ctx, R.color.status_warning));
                    } else {
                        tvDaysLeft.setTextColor(ContextCompat.getColor(ctx, R.color.status_success));
                    }
                }

                int percent = Math.max(0, Math.min(100, (int) ((daysLeft / (float) item.getServiceIntervalDays()) * 100)));
                pbLifeBar.setProgress(percent);

                layoutLifeItems.addView(barView);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeviceClick(device);
                }
            });
        }
    }
}
