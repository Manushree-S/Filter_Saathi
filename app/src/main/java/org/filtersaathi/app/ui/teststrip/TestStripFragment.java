package org.filtersaathi.app.ui.teststrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentTestStripBinding;
import org.filtersaathi.app.domain.model.TestStripResult;

/**
 * TestStripFragment (Screen 7):
 * - Dark viewfinder with four orange corner brackets
 * - Sweeping scan laser animation for 1.8 seconds (simulated, no camera permissions)
 * - Four result rings: TDS 312 ppm Safe, pH 7.2 Safe, Chlorine 0.1 mg/L Low, Nitrate 62 mg/L Unsafe
 * - High-visibility red warning when nitrate exceeds safe thresholds
 */
@AndroidEntryPoint
public class TestStripFragment extends Fragment {

    private FragmentTestStripBinding binding;
    private TestStripViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTestStripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TestStripViewModel.class);

        setupObservers();
        setupActions();
    }

    private void setupObservers() {
        viewModel.isScanning().observe(getViewLifecycleOwner(), isScanning -> {
            if (isScanning != null && isScanning) {
                binding.btnScanStrip.setEnabled(false);
                binding.btnScanStrip.setAlpha(0.6f);
                binding.tvScanningStatus.setVisibility(View.VISIBLE);
                binding.layoutResults.setVisibility(View.GONE);
            } else {
                binding.btnScanStrip.setEnabled(true);
                binding.btnScanStrip.setAlpha(1.0f);
                binding.tvScanningStatus.setVisibility(View.GONE);
            }
        });

        viewModel.getHasResults().observe(getViewLifecycleOwner(), hasResults -> {
            if (hasResults != null && hasResults) {
                displayResults();
            } else {
                binding.layoutResults.setVisibility(View.GONE);
            }
        });
    }

    private void setupActions() {
        binding.btnScanStrip.setOnClickListener(v -> {
            viewModel.onScanStarted();
            binding.scanOverlayView.startScan(() -> viewModel.onScanCompleted());
        });

        binding.btnScanAgain.setOnClickListener(v -> {
            viewModel.resetScan();
            binding.scanOverlayView.resetScan();
        });
    }

    private void displayResults() {
        binding.layoutResults.setVisibility(View.VISIBLE);

        int successColor = ContextCompat.getColor(requireContext(), R.color.status_success);
        int warningColor = ContextCompat.getColor(requireContext(), R.color.status_warning);
        int dangerColor = ContextCompat.getColor(requireContext(), R.color.status_danger);

        TestStripResult data = viewModel.getTestStripResult();

        // 1. TDS (312 ppm, Safe)
        binding.ringTds.setReading(
                getString(R.string.ui_r_tds),
                String.valueOf(data.getTds()),
                getString(R.string.ui_unit_ppm),
                getString(R.string.ui_s_safe),
                successColor,
                0.62f,
                true
        );

        // 2. pH (7.2, Safe)
        binding.ringPh.setReading(
                getString(R.string.ui_r_ph),
                String.valueOf(data.getPh()),
                "",
                getString(R.string.ui_s_safe),
                successColor,
                0.72f,
                true
        );

        // 3. Chlorine (0.1 mg/L, Low)
        binding.ringChlorine.setReading(
                getString(R.string.ui_r_cl),
                String.valueOf(data.getChlorine()),
                getString(R.string.ui_unit_mgl),
                getString(R.string.ui_s_low),
                warningColor,
                0.20f,
                true
        );

        // 4. Nitrate (62 mg/L, Unsafe)
        binding.ringNitrate.setReading(
                getString(R.string.ui_r_ni),
                String.valueOf(data.getNitrate()),
                getString(R.string.ui_unit_mgl),
                getString(R.string.ui_tag_unsafe),
                dangerColor,
                0.85f,
                true
        );

        // Scroll to reveal results smoothly
        binding.getRoot().post(() -> binding.getRoot().smoothScrollTo(0, binding.layoutResults.getTop()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
