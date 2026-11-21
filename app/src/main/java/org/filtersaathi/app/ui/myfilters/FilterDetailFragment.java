package org.filtersaathi.app.ui.myfilters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentFilterDetailBinding;
import org.filtersaathi.app.domain.model.FilterDevice;
import org.filtersaathi.app.domain.model.FilterItemLife;
import org.filtersaathi.app.domain.model.ServiceHistoryEntry;

import java.util.List;

/**
 * FilterDetailFragment:
 * - Displays details of a filter device including items, life bars, next service date, history.
 * - Allows marking items as serviced today with confirmation dialog.
 * - Navigates to Report a Problem with device name prefilled.
 */
@AndroidEntryPoint
public class FilterDetailFragment extends Fragment {

    private FragmentFilterDetailBinding binding;
    private MyFiltersViewModel viewModel;
    private String deviceId = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deviceId = getArguments().getString("deviceId", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFilterDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MyFiltersViewModel.class);

        viewModel.getFilterDevices().observe(getViewLifecycleOwner(), devices -> {
            FilterDevice device = viewModel.getDeviceById(deviceId);
            if (device != null) {
                renderDeviceDetails(device);
            }
        });
    }

    private void renderDeviceDetails(@NonNull FilterDevice device) {
        Context ctx = requireContext();

        // Device Name
        int nameResId = ctx.getResources().getIdentifier(device.getNameKey(), "string", ctx.getPackageName());
        String displayName = nameResId != 0 ? ctx.getString(nameResId) : device.getNameFallback();
        binding.tvDetailName.setText(displayName);

        // Device Type & Install
        int typeResId = ctx.getResources().getIdentifier(device.getTypeKey(), "string", ctx.getPackageName());
        String displayType = typeResId != 0 ? ctx.getString(typeResId) : device.getTypeKey();
        String typeAndInstall = displayType + " · " + ctx.getString(R.string.ui_installed_on, device.getInstallDate());
        binding.tvDetailTypeAndInstall.setText(typeAndInstall);

        // Life Items
        binding.layoutDetailItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        for (FilterItemLife item : device.getItems()) {
            View itemView = inflater.inflate(R.layout.item_detail_life_row, binding.layoutDetailItems, false);
            TextView tvDetailItemName = itemView.findViewById(R.id.tvDetailItemName);
            TextView tvDetailItemDays = itemView.findViewById(R.id.tvDetailItemDays);
            ProgressBar pbDetailItemBar = itemView.findViewById(R.id.pbDetailItemBar);
            TextView tvDetailLastServiced = itemView.findViewById(R.id.tvDetailLastServiced);
            TextView tvDetailNextService = itemView.findViewById(R.id.tvDetailNextService);
            TextView btnMarkServiced = itemView.findViewById(R.id.btnMarkServiced);

            int itemResId = ctx.getResources().getIdentifier(item.getItemKey(), "string", ctx.getPackageName());
            String itemDisplayName = itemResId != 0 ? ctx.getString(itemResId) : item.getItemKey();
            tvDetailItemName.setText(itemDisplayName);

            int daysLeft = item.getDaysLeft(null);
            if (daysLeft < 0) {
                tvDetailItemDays.setText(ctx.getString(R.string.ui_overdue_days, Math.abs(daysLeft)));
                tvDetailItemDays.setTextColor(ContextCompat.getColor(ctx, R.color.status_danger));
            } else {
                tvDetailItemDays.setText(ctx.getString(R.string.ui_days_left, daysLeft));
                if (daysLeft <= 7) {
                    tvDetailItemDays.setTextColor(ContextCompat.getColor(ctx, R.color.status_danger));
                } else if (daysLeft <= 30) {
                    tvDetailItemDays.setTextColor(ContextCompat.getColor(ctx, R.color.status_warning));
                } else {
                    tvDetailItemDays.setTextColor(ContextCompat.getColor(ctx, R.color.status_success));
                }
            }

            int percent = Math.max(0, Math.min(100, (int) ((daysLeft / (float) item.getServiceIntervalDays()) * 100)));
            pbDetailItemBar.setProgress(percent);

            tvDetailLastServiced.setText(ctx.getString(R.string.ui_last_serviced, item.getLastServicedDate()));
            tvDetailNextService.setText(ctx.getString(R.string.ui_next_service, item.getNextServiceDate()));

            btnMarkServiced.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.ui_confirm_service_title)
                        .setMessage(getString(R.string.ui_confirm_service_msg, itemDisplayName))
                        .setPositiveButton(R.string.ui_confirm_btn, (dialog, which) -> {
                            viewModel.markItemServiced(device.getId(), item.getItemKey());
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            });

            binding.layoutDetailItems.addView(itemView);
        }

        // Service History List
        binding.layoutHistoryList.removeAllViews();
        List<ServiceHistoryEntry> history = device.getHistory();
        if (history.isEmpty()) {
            binding.tvNoHistory.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoHistory.setVisibility(View.GONE);
            for (ServiceHistoryEntry entry : history) {
                View histView = inflater.inflate(R.layout.item_history_row, binding.layoutHistoryList, false);
                TextView tvHistoryItem = histView.findViewById(R.id.tvHistoryItem);
                TextView tvHistoryDate = histView.findViewById(R.id.tvHistoryDate);

                int itemResId = ctx.getResources().getIdentifier(entry.getItemKey(), "string", ctx.getPackageName());
                String itemDisplayName = itemResId != 0 ? ctx.getString(itemResId) : entry.getItemKey();
                tvHistoryItem.setText(itemDisplayName);
                tvHistoryDate.setText(entry.getDate());

                binding.layoutHistoryList.addView(histView);
            }
        }

        // Report a Problem Button
        binding.btnReportProblem.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("prefilledDeviceName", displayName);
            Navigation.findNavController(requireView()).navigate(R.id.navigation_report, args);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
