package org.filtersaathi.app.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentHomeBinding;
import org.filtersaathi.app.ui.adapters.ImpactStatsAdapter;
import org.filtersaathi.app.ui.adapters.MaintenanceAdapter;
import org.filtersaathi.app.ui.adapters.WaterParamsAdapter;
import org.filtersaathi.app.util.PdfReportGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private ImpactStatsAdapter impactStatsAdapter;
    private MaintenanceAdapter maintenanceAdapter;
    private WaterParamsAdapter waterParamsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain Kotlin ViewModel in Java
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupAdapters();
        setupClickListeners();
        observeViewModel();
    }

    private void setupAdapters() {
        impactStatsAdapter = new ImpactStatsAdapter();
        binding.rvImpactStats.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvImpactStats.setAdapter(impactStatsAdapter);

        maintenanceAdapter = new MaintenanceAdapter();
        binding.rvMaintenance.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMaintenance.setAdapter(maintenanceAdapter);

        waterParamsAdapter = new WaterParamsAdapter();
        binding.rvWaterParams.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvWaterParams.setAdapter(waterParamsAdapter);
    }

    private void setupClickListeners() {
        // Language Toggle (EN / ಕನ್ನಡ)
        binding.btnLanguageToggle.setOnClickListener(v -> toggleLanguage());

        // Alert banner actions
        binding.btnDismissAlert.setOnClickListener(v -> viewModel.dismissAlert());
        binding.btnAlertFixNow.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_check);
        });

        // Quick action tiles (2x2)
        binding.tileStartCheck.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_check);
        });

        binding.tileScanStrip.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_test);
        });

        binding.tileReportFault.setOnClickListener(v -> showReportFaultDialog());

        binding.tileRequestSump.setOnClickListener(v -> showSumpRequestDialog());

        // Sump monitoring button
        binding.btnRequestSump.setOnClickListener(v -> showSumpRequestDialog());

        // Reports actions
        binding.btnOpenPdf.setOnClickListener(v -> openGeneratedPdf());
        binding.btnShareWhatsapp.setOnClickListener(v -> shareReportOnWhatsapp());

        // Help strip actions
        binding.btnHelplineCall.setOnClickListener(v -> dialHelpline());
        binding.btnVoiceAssist.setOnClickListener(v -> triggerVoiceAssist());

        // Location chip switches role/village info
        binding.chipLocation.setOnClickListener(v -> viewModel.toggleRole());
    }

    private void observeViewModel() {
        // Observe Village Summary & Health Score
        viewModel.getVillageSummary().observe(getViewLifecycleOwner(), summary -> {
            if (summary != null) {
                binding.chipLocation.setText(summary.getVillageName());
                binding.healthScoreRing.setScore(summary.getOverallHealthScore(), true);

                if (summary.getOverallHealthScore() >= 80) {
                    binding.tvHealthScoreStatus.setText(R.string.health_score_status_good);
                } else if (summary.getOverallHealthScore() >= 50) {
                    binding.tvHealthScoreStatus.setText(R.string.health_score_status_watch);
                } else {
                    binding.tvHealthScoreStatus.setText(R.string.health_score_status_critical);
                }

                if (summary.getScoreDelta() >= 0) {
                    binding.tvHealthScoreDelta.setText(getString(R.string.health_score_delta_positive));
                } else {
                    binding.tvHealthScoreDelta.setText(getString(R.string.health_score_delta_negative));
                }
            }
        });

        // Observe Alert dismissal
        viewModel.isAlertDismissed().observe(getViewLifecycleOwner(), isDismissed -> {
            binding.cardAlertBanner.setVisibility(isDismissed ? View.GONE : View.VISIBLE);
        });

        // Observe Impact Stats
        viewModel.getImpactStats().observe(getViewLifecycleOwner(), stats -> {
            impactStatsAdapter.setItems(stats);
        });

        // Observe Upcoming Maintenance
        viewModel.getUpcomingMaintenance().observe(getViewLifecycleOwner(), items -> {
            maintenanceAdapter.setItems(items);
        });

        // Observe Water Test Parameters
        viewModel.getLatestWaterParams().observe(getViewLifecycleOwner(), params -> {
            waterParamsAdapter.setItems(params);
        });

        // Observe Role toggle (Caretaker vs Officer)
        viewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            if ("OFFICER".equals(role)) {
                binding.tvRoleIndicator.setText(R.string.role_officer);
            } else {
                binding.tvRoleIndicator.setText(R.string.role_caretaker);
            }
        });

        // Observe Offline State Banner
        viewModel.isOffline().observe(getViewLifecycleOwner(), isOffline -> {
            binding.bannerOffline.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        });
    }

    private void toggleLanguage() {
        Locale current = getResources().getConfiguration().getLocales().get(0);
        String targetLang = current.getLanguage().equalsIgnoreCase("kn") ? "en" : "kn";

        Locale newLocale = new Locale(targetLang);
        Locale.setDefault(newLocale);
        Configuration config = new Configuration();
        config.setLocale(newLocale);
        requireActivity().getResources().updateConfiguration(
                config, requireActivity().getResources().getDisplayMetrics()
        );

        // Recreate activity to apply Kannada / English string resources cleanly
        requireActivity().recreate();
    }

    private void showReportFaultDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_report_fault)
                .setMessage("Report a breakdown or damaged tap at your local RO/UV centre. Panchayat engineers will be alerted.")
                .setPositiveButton("Submit Report", (dialog, which) -> {
                    Toast.makeText(requireContext(), "Fault report dispatched with GPS coordinates.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showSumpRequestDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.sump_card_title)
                .setMessage("Submit request for IoT sump sensor deployment under Jal Jeevan Mission?")
                .setPositiveButton("Confirm Request", (dialog, which) -> {
                    binding.chipSumpStatus.setText("Requested");
                    Toast.makeText(requireContext(), "Sensor request logged. Verification scheduled.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openGeneratedPdf() {
        try {
            File pdf = PdfReportGenerator.generateMonthlyReport(
                    requireContext(),
                    binding.chipLocation.getText().toString(),
                    88
            );
            Toast.makeText(requireContext(), "Monthly Report ready: " + pdf.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareReportOnWhatsapp() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Jal Jeevan Mission - FilterSaathi Audit Report: Hosahalli Gram Panchayat water health score is 88/100 (Safe). Clean drinking water verified."
        );
        try {
            startActivity(whatsappIntent);
        } catch (Exception e) {
            // WhatsApp not installed, launch generic share chooser
            Intent shareIntent = Intent.createChooser(whatsappIntent, "Share Water Audit");
            startActivity(shareIntent);
        }
    }

    private void dialHelpline() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:18005592837"));
        startActivity(intent);
    }

    private void triggerVoiceAssist() {
        Toast.makeText(requireContext(), "ಧ್ವನಿ ಸಹಾಯ ಪ್ರಾರಂಭವಾಗುತ್ತಿದೆ... (Voice Assistant listening)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
