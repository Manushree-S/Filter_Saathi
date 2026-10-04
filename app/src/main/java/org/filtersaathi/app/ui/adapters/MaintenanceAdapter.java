package org.filtersaathi.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.ItemMaintenanceBinding;
import org.filtersaathi.app.domain.model.MaintenanceItem;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.ViewHolder> {

    private final List<MaintenanceItem> items = new ArrayList<>();

    public void setItems(List<MaintenanceItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMaintenanceBinding binding = ItemMaintenanceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMaintenanceBinding binding;

        ViewHolder(ItemMaintenanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MaintenanceItem item) {
            binding.tvComponentName.setText(item.getComponentName());
            binding.tvDaysRemaining.setText(item.getDaysRemaining() + " days left");

            if (item.isUrgent()) {
                binding.tvDaysRemaining.setTextColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.status_danger)
                );
                binding.progressBarMaintenance.setIndicatorColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.status_danger)
                );
            } else {
                binding.tvDaysRemaining.setTextColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.status_warning)
                );
                binding.progressBarMaintenance.setIndicatorColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.accent_saffron)
                );
            }

            binding.progressBarMaintenance.setProgress(item.getProgressPercent());
        }
    }
}
