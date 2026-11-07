package org.filtersaathi.app.ui.report;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentReportBinding;
import org.filtersaathi.app.domain.model.ProblemReport;

/**
 * ReportFragment (Screen 9):
 * - Public screen for reporting water or equipment problems
 * - Problem spinner (Bad taste or smell, Filter not working, Leak, Other)
 * - Village input and optional details
 * - Simulated photo attachment toggle
 * - Generates FS-R-#### ticket and shows Received -> In review -> Fixed stepper
 * - Pulsing animation on "In review" indicator (respects system animation settings)
 */
@AndroidEntryPoint
public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private ReportViewModel viewModel;
    private int selectedProblemIndex = -1;
    private ObjectAnimator inReviewPulseAnimator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        setupProblemDropdown();
        setupPhotoSwitch();
        setupActions();
        setupObservers();
    }

    private void setupProblemDropdown() {
        String[] problemOptions = new String[]{
                getString(R.string.ui_ty1),
                getString(R.string.ui_ty2),
                getString(R.string.ui_ty3),
                getString(R.string.ui_ty4)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                problemOptions
        );
        binding.actvProblemType.setAdapter(adapter);

        binding.actvProblemType.setOnItemClickListener((parent, view, position, id) -> {
            selectedProblemIndex = position;
        });
    }

    private void setupPhotoSwitch() {
        binding.switchPhoto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.tvPhotoLabel.setText(R.string.ui_rep_photo_on);
            } else {
                binding.tvPhotoLabel.setText(R.string.ui_rep_photo);
            }
        });
    }

    private void setupActions() {
        binding.btnSendReport.setOnClickListener(v -> {
            String village = binding.etVillage.getText() != null ? binding.etVillage.getText().toString() : "";
            String details = binding.etDetails.getText() != null ? binding.etDetails.getText().toString() : "";
            boolean hasPhoto = binding.switchPhoto.isChecked();

            viewModel.submitReport(selectedProblemIndex, village, details, hasPhoto);
        });

        binding.btnSendAnother.setOnClickListener(v -> {
            stopPulseAnimation();
            viewModel.resetReport();
            binding.etVillage.setText("");
            binding.etDetails.setText("");
            binding.actvProblemType.setText("", false);
            selectedProblemIndex = -1;
            binding.switchPhoto.setChecked(false);
        });
    }

    private void setupObservers() {
        viewModel.getErrorMessageRes().observe(getViewLifecycleOwner(), errRes -> {
            if (errRes != null) {
                binding.tvReportError.setText(errRes);
                binding.tvReportError.setVisibility(View.VISIBLE);
            } else {
                binding.tvReportError.setVisibility(View.GONE);
            }
        });

        viewModel.getLatestReport().observe(getViewLifecycleOwner(), report -> {
            if (report != null) {
                showReportResult(report);
            } else {
                showReportForm();
            }
        });
    }

    private void showReportForm() {
        stopPulseAnimation();
        binding.cardReportForm.setVisibility(View.VISIBLE);
        binding.cardReportResult.setVisibility(View.GONE);
    }

    private void showReportResult(@NonNull ProblemReport report) {
        binding.cardReportForm.setVisibility(View.GONE);
        binding.cardReportResult.setVisibility(View.VISIBLE);

        String bannerMsg = getString(R.string.ui_rep_done) + " " + report.getTicketNumber();
        binding.tvResultBanner.setText(bannerMsg);

        String ticketTitle = getString(R.string.ui_ticket) + " " + report.getTicketNumber();
        binding.tvTicketNumber.setText(ticketTitle);

        binding.tvTicketVillage.setText(report.getVillage());

        startPulseAnimation(binding.ivInReviewIndicator);
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
        stopPulseAnimation();
        if (!areAnimationsEnabled()) return;

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.25f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.25f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1.0f, 0.7f);

        inReviewPulseAnimator = ObjectAnimator.ofPropertyValuesHolder(target, scaleX, scaleY, alpha);
        inReviewPulseAnimator.setDuration(900);
        inReviewPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        inReviewPulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        inReviewPulseAnimator.start();
    }

    private void stopPulseAnimation() {
        if (inReviewPulseAnimator != null) {
            inReviewPulseAnimator.cancel();
            inReviewPulseAnimator = null;
        }
        if (binding != null) {
            binding.ivInReviewIndicator.setScaleX(1.0f);
            binding.ivInReviewIndicator.setScaleY(1.0f);
            binding.ivInReviewIndicator.setAlpha(1.0f);
        }
    }

    @Override
    public void onDestroyView() {
        stopPulseAnimation();
        super.onDestroyView();
        binding = null;
    }
}
