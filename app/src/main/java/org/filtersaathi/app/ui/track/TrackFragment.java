package org.filtersaathi.app.ui.track;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import org.filtersaathi.app.domain.model.BookingRequest;

/**
 * TrackFragment (Screen 4):
 * - Displays 6-stage vertical stepper with animated pulsing ring on active stage
 * - Shows Request number, district, and village
 * - Shows installer card from Stage 3 with "Sample details" and dialer intent
 * - Shows 5-star rating and "View certificate" from Stage 6
 * - Handles empty state when no booking exists
 * - Respects system "remove animations" setting
 */
@AndroidEntryPoint
public class TrackFragment extends Fragment {

    private FragmentTrackBinding binding;
    private TrackViewModel viewModel;
    private ObjectAnimator currentPulseAnimator;

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
            if (booking == null) {
                showEmptyState();
            } else {
                showBookingDetails(booking);
            }
        });
    }

    private void showEmptyState() {
        stopPulseAnimation();
        binding.layoutEmptyState.setVisibility(View.VISIBLE);
        binding.layoutBookingContent.setVisibility(View.GONE);
        binding.layoutTopBanner.setVisibility(View.GONE);

        binding.btnEmptyBook.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_track_to_book);
        });
    }

    private void showBookingDetails(@NonNull BookingRequest booking) {
        binding.layoutEmptyState.setVisibility(View.GONE);
        binding.layoutBookingContent.setVisibility(View.VISIBLE);

        // Request number and location
        String reqText = getString(R.string.ui_req_no) + " " + booking.getRequestNumber();
        binding.tvRequestNumber.setText(reqText);

        String locationText = booking.getVillage() + ", " + booking.getDistrict();
        binding.tvBookingLocation.setText(locationText);

        // Update 6-stage Stepper
        updateStepper(booking.getCurrentStage());

        // Update Installer Card (From Stage 3)
        if (booking.getShowInstallerCard()) {
            binding.cardInstaller.setVisibility(View.VISIBLE);
            binding.tvInstallerName.setText(getString(R.string.ui_installer_name));
            binding.tvInstallerExp.setText(getString(R.string.ui_installer_exp));
            binding.tvInstallerPhone.setText(booking.getInstaller().getPhone());

            binding.btnCallInstaller.setOnClickListener(v -> {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + booking.getInstaller().getPhone()));
                startActivity(dialIntent);
            });
        } else {
            binding.cardInstaller.setVisibility(View.GONE);
        }

        // Update Rating & Certificate Section (From Stage 6)
        if (booking.getShowRatingAndCertificate()) {
            binding.cardRatingAndCert.setVisibility(View.VISIBLE);
            updateStarsDisplay(booking.getRating());

            setupStarRatingClicks();

            binding.btnViewCertificate.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_track_to_certificate);
            });
        } else {
            binding.cardRatingAndCert.setVisibility(View.GONE);
        }
    }

    private void updateStepper(int currentStage) {
        stopPulseAnimation();

        int successColor = ContextCompat.getColor(requireContext(), R.color.status_success);
        int dividerColor = ContextCompat.getColor(requireContext(), R.color.divider_light);
        int primaryTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary_light);
        int secondaryTextColor = ContextCompat.getColor(requireContext(), R.color.text_secondary_light);
        int saffronColor = ContextCompat.getColor(requireContext(), R.color.accent_saffron);

        View activeViewToPulse = null;

        // Stage 1
        activeViewToPulse = updateStageRow(1, currentStage,
                binding.ivStage1Indicator, binding.tvStage1Title, getString(R.string.ui_stg1),
                binding.viewLine1, successColor, dividerColor, primaryTextColor, secondaryTextColor, saffronColor, activeViewToPulse);

        // Stage 2
        activeViewToPulse = updateStageRow(2, currentStage,
                binding.ivStage2Indicator, binding.tvStage2Title, getString(R.string.ui_stg2),
                binding.viewLine2, successColor, dividerColor, primaryTextColor, secondaryTextColor, saffronColor, activeViewToPulse);

        // Stage 3
        activeViewToPulse = updateStageRow(3, currentStage,
                binding.ivStage3Indicator, binding.tvStage3Title, getString(R.string.ui_stg3),
                binding.viewLine3, successColor, dividerColor, primaryTextColor, secondaryTextColor, saffronColor, activeViewToPulse);

        // Stage 4
        activeViewToPulse = updateStageRow(4, currentStage,
                binding.ivStage4Indicator, binding.tvStage4Title, getString(R.string.ui_stg4),
                binding.viewLine4, successColor, dividerColor, primaryTextColor, secondaryTextColor, saffronColor, activeViewToPulse);

        // Stage 5
        activeViewToPulse = updateStageRow(5, currentStage,
                binding.ivStage5Indicator, binding.tvStage5Title, getString(R.string.ui_stg5),
                binding.viewLine5, successColor, dividerColor, primaryTextColor, secondaryTextColor, saffronColor, activeViewToPulse);

        // Stage 6 (no trailing line)
        activeViewToPulse = updateStageRow(6, currentStage,
                binding.ivStage6Indicator, binding.tvStage6Title, getString(R.string.ui_stg6),
                null, successColor, dividerColor, primaryTextColor, secondaryTextColor, saffronColor, activeViewToPulse);

        if (activeViewToPulse != null) {
            startPulseAnimation(activeViewToPulse);
        }
    }

    private View updateStageRow(int stageIndex, int currentStage,
                                ImageView ivIndicator, android.widget.TextView tvTitle, String titleText,
                                @Nullable View trailingLine,
                                int successColor, int dividerColor,
                                int primaryTextColor, int secondaryTextColor, int saffronColor,
                                View currentActiveView) {
        if (currentStage > stageIndex) {
            // Done
            ivIndicator.setImageResource(R.drawable.bg_stage_done);
            ivIndicator.setContentDescription(titleText + ", " + getString(R.string.ui_stg_done));
            tvTitle.setTextColor(primaryTextColor);
            if (trailingLine != null) {
                trailingLine.setBackgroundColor(successColor);
            }
            return currentActiveView;
        } else if (currentStage == stageIndex) {
            // Current (Active)
            ivIndicator.setImageResource(R.drawable.bg_stage_current);
            ivIndicator.setContentDescription(titleText + ", active");
            tvTitle.setTextColor(saffronColor);
            if (trailingLine != null) {
                trailingLine.setBackgroundColor(dividerColor);
            }
            return ivIndicator;
        } else {
            // Pending
            ivIndicator.setImageResource(R.drawable.bg_stage_pending);
            ivIndicator.setContentDescription(titleText + ", pending");
            tvTitle.setTextColor(secondaryTextColor);
            if (trailingLine != null) {
                trailingLine.setBackgroundColor(dividerColor);
            }
            return currentActiveView;
        }
    }

    private void setupStarRatingClicks() {
        ImageView[] stars = new ImageView[]{
                binding.ivStar1,
                binding.ivStar2,
                binding.ivStar3,
                binding.ivStar4,
                binding.ivStar5
        };

        for (int i = 0; i < stars.length; i++) {
            final int starRating = i + 1;
            stars[i].setOnClickListener(v -> viewModel.submitRating(starRating));
        }
    }

    private void updateStarsDisplay(int rating) {
        ImageView[] stars = new ImageView[]{
                binding.ivStar1,
                binding.ivStar2,
                binding.ivStar3,
                binding.ivStar4,
                binding.ivStar5
        };

        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_outline);
            }
        }

        if (rating > 0) {
            binding.tvRatingThanks.setVisibility(View.VISIBLE);
        } else {
            binding.tvRatingThanks.setVisibility(View.GONE);
        }
    }

    private boolean areAnimationsEnabled() {
        if (getContext() == null) return false;
        try {
            float durationScale = Settings.Global.getFloat(
                    requireContext().getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
            return durationScale > 0;
        } catch (Exception e) {
            return true;
        }
    }

    private void startPulseAnimation(@NonNull View target) {
        if (!areAnimationsEnabled()) return;

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.25f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.25f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1.0f, 0.7f);

        currentPulseAnimator = ObjectAnimator.ofPropertyValuesHolder(target, scaleX, scaleY, alpha);
        currentPulseAnimator.setDuration(900);
        currentPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        currentPulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        currentPulseAnimator.start();
    }

    private void stopPulseAnimation() {
        if (currentPulseAnimator != null) {
            currentPulseAnimator.cancel();
            currentPulseAnimator = null;
        }
        if (binding != null) {
            ImageView[] indicators = new ImageView[]{
                    binding.ivStage1Indicator,
                    binding.ivStage2Indicator,
                    binding.ivStage3Indicator,
                    binding.ivStage4Indicator,
                    binding.ivStage5Indicator,
                    binding.ivStage6Indicator
            };
            for (ImageView iv : indicators) {
                iv.setScaleX(1.0f);
                iv.setScaleY(1.0f);
                iv.setAlpha(1.0f);
            }
        }
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
        stopPulseAnimation();
        super.onDestroyView();
        binding = null;
    }
}
