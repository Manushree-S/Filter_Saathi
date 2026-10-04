package org.filtersaathi.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.filtersaathi.app.databinding.ItemImpactStatBinding;
import org.filtersaathi.app.domain.model.ImpactStatItem;
import java.util.ArrayList;
import java.util.List;

public class ImpactStatsAdapter extends RecyclerView.Adapter<ImpactStatsAdapter.ViewHolder> {

    private final List<ImpactStatItem> items = new ArrayList<>();

    public void setItems(List<ImpactStatItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImpactStatBinding binding = ItemImpactStatBinding.inflate(
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
        private final ItemImpactStatBinding binding;

        ViewHolder(ItemImpactStatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ImpactStatItem item) {
            binding.tvStatTitle.setText(item.getTitleResId());
            binding.tvStatValue.setText(item.getValueString());
            binding.ivStatIcon.setImageResource(item.getIconResId());
        }
    }
}
