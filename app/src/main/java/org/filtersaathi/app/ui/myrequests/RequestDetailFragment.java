package org.filtersaathi.app.ui.myrequests;

import android.content.Context;
import android.content.res.ColorStateList;
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
import org.filtersaathi.app.databinding.FragmentRequestDetailBinding;
import org.filtersaathi.app.domain.model.BookingRequest;
import org.filtersaathi.app.domain.model.FilterCareStatus;
import org.filtersaathi.app.domain.model.FilterDevice;
import org.filtersaathi.app.domain.model.FilterItemLife;
import org.filtersaathi.app.domain.model.ProblemReport;
import org.filtersaathi.app.domain.model.ServiceHistoryEntry;

import java.util.List;

/**
 * RequestDetailFragment:
 * - Details view for a selected installation request or problem report.
 * - Displays request overview, stage stepper, installer info, rating/certificate.
 * - Displays "Your device care" card for Stage 6 requests with component life bars,
 *   next service date, history, and "Mark as serviced" actions.
 */
@AndroidEntryPoint
public class RequestDetailFragment extends Fragment {

    private FragmentRequestDetailBinding binding;
    private MyRequestsViewModel viewModel;
    private String requestId = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestId = getArguments().getString("requestId", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MyRequestsViewModel.class);

        BookingRequest booking = viewModel.getBooking(requestId);
        if (booking != null) {
            renderBookingDetails(booking);
        } else {
            ProblemReport report = viewModel.getReport(requestId);
            if (report != null) {
                renderReportDetails(report);
            }
        }
    }

    private void renderBookingDetails(@NonNull BookingRequest booking) {
        Context ctx = requireContext();

        binding.tvDetailId.setText(booking.getRequestNumber());
        binding.tvDetailLocation.setText(booking.getVillage() + ", " + booking.getDistrict());

        int stageResId = ctx.getResources().getIdentifier(booking.getStageKey(), "string", ctx.getPackageName());
        String stageText = stageResId != 0 ? ctx.getString(stageResId) : booking.getStageKey();
        binding.tvDetailStatusChip.setText(stageText);
        binding.tvDetailStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.secondary_blue)));

        String typeAndDate = ctx.getString(R.string.ui_tag_request) + " · PIN " + booking.getPinCode();
        binding.tvDetailTypeAndDate.setText(typeAndDate);

        // Stage 6 Device Care Card
        if (booking.getShowDeviceCareCard()) {
            binding.cardDeviceCare.setVisibility(View.VISIBLE);
            FilterDevice device = viewModel.getDeviceCare(booking.getRequestNumber());
            if (device != null) {
                renderDeviceCare(device);
            }
        } else {
            binding.cardDeviceCare.setVisibility(View.GONE);
        }
    }

    private void renderReportDetails(@NonNull ProblemReport report) {
        Context ctx = requireContext();

        binding.tvDetailId.setText(report.getTicketNumber());
        binding.tvDetailLocation.setText(report.getVillage());

        int probResId = ctx.getResources().getIdentifier(report.getProblemType().getKey(), "string", ctx.getPackageName());
        String probText = probResId != 0 ? ctx.getString(probResId) : report.getProblemType().name();
        binding.tvDetailStatusChip.setText(probText);
        binding.tvDetailStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.accent_saffron)));

        String typeAndDate = ctx.getString(R.string.ui_tag_report) + (report.getDetails().isEmpty() ? "" : " · " + report.getDetails());
        binding.tvDetailTypeAndDate.setText(typeAndDate);

        binding.cardDeviceCare.setVisibility(View.GONE);
    }

    private void renderDeviceCare(@NonNull FilterDevice device) {
        Context ctx = requireContext();

        binding.layoutDeviceCareItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        for (FilterItemLife item : device.getItems()) {
            View itemView = inflater.inflate(R.layout.item_detail_life_row, binding.layoutDeviceCareItems, false);
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
                            FilterDevice updated = viewModel.getDeviceCare(device.getId());
                            if (updated != null) {
                                renderDeviceCare(updated);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            });

            binding.layoutDeviceCareItems.addView(itemView);
        }

        // History
        binding.layoutDeviceHistory.removeAllViews();
        List<ServiceHistoryEntry> history = device.getHistory();
        if (history.isEmpty()) {
            TextView emptyTv = new TextView(ctx);
            emptyTv.setText(R.string.ui_no_history);
            emptyTv.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary_light));
            binding.layoutDeviceHistory.addView(emptyTv);
        } else {
            for (ServiceHistoryEntry entry : history) {
                View histView = inflater.inflate(R.layout.item_history_row, binding.layoutDeviceHistory, false);
                TextView tvHistoryItem = histView.findViewById(R.id.tvHistoryItem);
                TextView tvHistoryDate = histView.findViewById(R.id.tvHistoryDate);

                int itemResId = ctx.getResources().getIdentifier(entry.getItemKey(), "string", ctx.getPackageName());
                String itemDisplayName = itemResId != 0 ? ctx.getString(itemResId) : entry.getItemKey();
                tvHistoryItem.setText(itemDisplayName);
                tvHistoryDate.setText(entry.getDate());

                binding.layoutDeviceHistory.addView(histView);
            }
        }

        // Report a Problem Button
        binding.btnReportDeviceProblem.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("prefilledDeviceName", device.getId());
            Navigation.findNavController(requireView()).navigate(R.id.navigation_report, args);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
