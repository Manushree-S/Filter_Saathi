package org.filtersaathi.app.ui.track;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentTrackBinding;
import org.filtersaathi.app.domain.model.BannerType;

/**
 * TrackFragment (Screen 4 foundation):
 * - Displays booking request number and location
 * - Shows green "Booked" banner or amber "Saved offline" banner based on topBannerEvent
 * - Supports demo actions: "Next stage" and "New request"
 */
@AndroidEntryPoint
public class TrackFragment extends Fragment {

    private FragmentTrackBinding binding;
    private TrackViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTrackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TrackViewModel.class);

        observeBooking();
        observeBannerEvent();
        setupActions();
    }

    private void observeBooking() {
        viewModel.getCurrentBooking().observe(getViewLifecycleOwner(), booking -> {
            if (booking != null) {
                String reqText = getString(R.string.ui_req_no) + " " + booking.getRequestNumber();
                binding.tvRequestNumber.setText(reqText);

                String locationText = booking.getVillage() + ", " + booking.getDistrict();
                binding.tvBookingLocation.setText(locationText);
            }
        });
    }

    private void observeBannerEvent() {
        viewModel.getTopBannerEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null && event.getReferenceNumber() != null) {
                binding.layoutTopBanner.setVisibility(View.VISIBLE);
                if (event.getType() == BannerType.SAVED_OFFLINE) {
                    binding.layoutTopBanner.setBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.alert_amber_bg));
                    binding.ivBannerIcon.setImageResource(R.drawable.ic_sync_offline);
                    binding.ivBannerIcon.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.alert_amber_text));
                    binding.tvBannerMessage.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.alert_amber_text));
                    binding.btnCloseBanner.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.alert_amber_text));

                    String msg = getString(R.string.ui_saved_offline) + " " + event.getReferenceNumber();
                    binding.tvBannerMessage.setText(msg);
                } else {
                    binding.layoutTopBanner.setBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.alert_green_bg));
                    binding.ivBannerIcon.setImageResource(R.drawable.ic_check);
                    binding.ivBannerIcon.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.alert_green_text));
                    binding.tvBannerMessage.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.alert_green_text));
                    binding.btnCloseBanner.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.alert_green_text));

                    String msg = getString(R.string.ui_banner) + " " + event.getReferenceNumber();
                    binding.tvBannerMessage.setText(msg);
                }
            } else {
                binding.layoutTopBanner.setVisibility(View.GONE);
            }
        });

        binding.btnCloseBanner.setOnClickListener(v -> viewModel.dismissBanner());
    }

    private void setupActions() {
        binding.btnNextStage.setOnClickListener(v -> viewModel.advanceStage());

        binding.btnNewRequest.setOnClickListener(v -> {
            viewModel.resetToNewRequest();
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_track_to_book);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
