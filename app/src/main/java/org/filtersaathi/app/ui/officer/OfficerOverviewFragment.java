package org.filtersaathi.app.ui.officer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentOfficerOverviewBinding;
import org.filtersaathi.app.domain.model.CoverageStatus;
import org.filtersaathi.app.domain.model.MonthBarItem;
import org.filtersaathi.app.domain.model.OfficerOverviewData;
import org.filtersaathi.app.ui.customview.BarChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OfficerOverviewFragment (Screen 10):
 * - Four KPI cards: 1,248 monitored, 90.2% working, 94.2% safe tests, 37 alerts
 * - 6-month BarChartView growing on load (May-Oct 88, 90, 91, 92, 93, 94)
 * - "Needs action" alerts with tags
 * - Village status table via RecyclerView
 * - Sump installations bars and WaveTankView at 78%
 * - DistrictMapView with fill overrides and coverage legend
 */
@AndroidEntryPoint
public class OfficerOverviewFragment extends Fragment {

    private FragmentOfficerOverviewBinding binding;
    private OfficerOverviewViewModel viewModel;
    private VillageTableAdapter villageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOfficerOverviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(OfficerOverviewViewModel.class);

        setupRecyclerView();
        setupLanguageForMap();
        setupObservers();
    }

    private void setupRecyclerView() {
        villageAdapter = new VillageTableAdapter();
        binding.rvVillageStatus.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvVillageStatus.setAdapter(villageAdapter);
    }

    private void setupLanguageForMap() {
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        boolean isKannada = false;
        if (!locales.isEmpty()) {
            isKannada = "kn".equalsIgnoreCase(locales.get(0).getLanguage());
        } else {
            isKannada = "kn".equalsIgnoreCase(getResources().getConfiguration().locale.getLanguage());
        }
        binding.mapDistrictCoverage.setKannada(isKannada);
    }

    private void setupObservers() {
        viewModel.getOfficerData().observe(getViewLifecycleOwner(), this::bindData);
    }

    private void bindData(@NonNull OfficerOverviewData data) {
        // 1. KPIs
        binding.tvKpiMonitored.setText(String.format("%,d", data.getKpis().getMonitoredCount()));
        binding.tvKpiWorking.setText(String.format("%.1f%%", data.getKpis().getWorkingPercent()));
        binding.tvKpiSafeTests.setText(String.format("%.1f%%", data.getKpis().getSafeTestsPercent()));
        binding.tvKpiAlerts.setText(String.valueOf(data.getKpis().getOpenAlertsCount()));

        // 2. Bar Chart
        List<BarChartView.BarItem> barItems = new ArrayList<>();
        for (MonthBarItem m : data.getMonthlyTrend()) {
            int resId = getResources().getIdentifier(m.getMonthKey(), "string", requireContext().getPackageName());
            String monthName = resId != 0 ? getString(resId) : m.getMonthKey();
            barItems.add(new BarChartView.BarItem(monthName, m.getPercent()));
        }
        binding.barChartView.setData(barItems);

        // 3. Village Table
        villageAdapter.setRows(data.getVillageRows());

        // 4. Wave Tank Gauge
        binding.waveTankView.setLevelPercent(data.getSumpStats().getTankLevelPercent());

        // 5. District Coverage Map with fill overrides
        Map<String, Integer> colorOverrides = new HashMap<>();
        for (Map.Entry<String, CoverageStatus> entry : data.getCoverageMap().entrySet()) {
            colorOverrides.put(entry.getKey(), entry.getValue().getColorHex());
        }
        binding.mapDistrictCoverage.setFillOverrides(colorOverrides);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
