package org.filtersaathi.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.ItemWaterParamBinding;
import org.filtersaathi.app.domain.model.WaterQualityParameter;
import org.filtersaathi.app.util.ColorMapper;
import java.util.ArrayList;
import java.util.List;

public class WaterParamsAdapter extends RecyclerView.Adapter<WaterParamsAdapter.ViewHolder> {

    private final List<WaterQualityParameter> items = new ArrayList<>();

    public void setItems(List<WaterQualityParameter> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWaterParamBinding binding = ItemWaterParamBinding.inflate(
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
        private final ItemWaterParamBinding binding;

        ViewHolder(ItemWaterParamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WaterQualityParameter item) {
            binding.tvParamName.setText(item.getName());
            binding.tvParamValue.setText(item.getValueWithUnit());
            binding.tvStatusBadge.setText(item.getStatus().name());

            int statusColor = ColorMapper.getColorForQualityStatus(itemView.getContext(), item.getStatus());
            binding.tvStatusBadge.setBackgroundColor(statusColor);
            binding.tvStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.tricolour_white));
        }
    }
}
