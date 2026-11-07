package org.filtersaathi.app.ui.watercheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentWaterCheckBinding;
import org.filtersaathi.app.domain.model.ReadingStatus;
import org.filtersaathi.app.domain.model.ReadingType;
import org.filtersaathi.app.domain.model.VillageWaterData;
import org.filtersaathi.app.domain.model.WaterReading;

/**
 * WaterCheckFragment (Screen 8):
 * - Public screen (no login required)
 * - District selector from localized district names
 * - Shows ONLY the selected district's village as a chip
 * - High-contrast status banner (Safe, Use with care, Do not drink) with icon + text
 * - 4 parameter reading rings (TDS, pH, Chlorine, Nitrate)
 * - "How your water becomes safe" diagram
 * - Pre-fills district if previously booked or selected on map
 */
@AndroidEntryPoint
public class WaterCheckFragment extends Fragment {

    private FragmentWaterCheckBinding binding;
    private WaterCheckViewModel viewModel;
    private String[] districtNames;
    private String[] districtKeys;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWaterCheckBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WaterCheckViewModel.class);
        districtNames = getResources().getStringArray(R.array.ui_district_names);
        districtKeys = getResources().getStringArray(R.array.ui_district_keys);

        setupDistrictDropdown();
        setupObservers();
    }

    private void setupDistrictDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                districtNames
        );
        binding.actvDistrict.setAdapter(adapter);

        binding.actvDistrict.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < districtKeys.length) {
                String key = districtKeys[position];
                viewModel.selectDistrict(key);
            }
        });
    }

    private void setupObservers() {
        // Pre-fill if user has a booking or selected a district from map
        viewModel.getCurrentBooking().observe(getViewLifecycleOwner(), booking -> {
            if (booking != null && binding.actvDistrict.getText().toString().isEmpty()) {
                prefillDistrict(booking.getDistrict());
            }
        });

        viewModel.getSelectedDistrictFromRepo().observe(getViewLifecycleOwner(), mapDistrict -> {
            if (mapDistrict != null && binding.actvDistrict.getText().toString().isEmpty()) {
                prefillDistrict(mapDistrict);
            }
        });

        viewModel.getWaterData().observe(getViewLifecycleOwner(), this::updateUiWithWaterData);
    }

    private void prefillDistrict(String targetDistrict) {
        if (targetDistrict == null || targetDistrict.isEmpty()) return;

        for (int i = 0; i < districtKeys.length; i++) {
            if (districtKeys[i].equalsIgnoreCase(targetDistrict) || districtNames[i].equalsIgnoreCase(targetDistrict)) {
                binding.actvDistrict.setText(districtNames[i], false);
                viewModel.selectDistrict(districtKeys[i]);
                break;
            }
        }
    }

    private void updateUiWithWaterData(@Nullable VillageWaterData data) {
        if (data == null) {
            binding.cardInitialPick.setVisibility(View.VISIBLE);
            binding.cardNoReadings.setVisibility(View.GONE);
            binding.layoutActiveWaterData.setVisibility(View.GONE);
            return;
        }

        if (!data.getHasReadings()) {
            binding.cardInitialPick.setVisibility(View.GONE);
            binding.cardNoReadings.setVisibility(View.VISIBLE);
            binding.layoutActiveWaterData.setVisibility(View.GONE);
            return;
        }

        // Active data with village readings
        binding.cardInitialPick.setVisibility(View.GONE);
        binding.cardNoReadings.setVisibility(View.GONE);
        binding.layoutActiveWaterData.setVisibility(View.VISIBLE);

        // 1. Village Chip
        binding.chipVillage.setText(data.getVillage());

        // 2. Status Banner (icon + color + text)
        int score = data.getScore();
        if (score >= 80) {
            // Safe
            binding.cardStatusBanner.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.alert_green_bg));
            binding.cardStatusBanner.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.status_success));
            binding.ivStatusIcon.setImageResource(R.drawable.ic_check);
            binding.ivStatusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.alert_green_text));
            binding.tvStatusTitle.setText(R.string.ui_pub_safe);
            binding.tvStatusTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_green_text));
            binding.tvScoreBadge.setText(score + "/100");
            binding.tvScoreBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_green_text));
            binding.tvNextStep.setText(R.string.ui_pub_safe_txt);
            binding.tvNextStep.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_green_text));
            binding.tvLastTested.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_green_text));
        } else if (score >= 50) {
            // Watch / Use with care
            binding.cardStatusBanner.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.alert_amber_bg));
            binding.cardStatusBanner.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.status_warning));
            binding.ivStatusIcon.setImageResource(R.drawable.ic_warning);
            binding.ivStatusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.alert_amber_text));
            binding.tvStatusTitle.setText(R.string.ui_pub_watch);
            binding.tvStatusTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_amber_text));
            binding.tvScoreBadge.setText(score + "/100");
            binding.tvScoreBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_amber_text));
            binding.tvNextStep.setText(R.string.ui_pub_watch_txt);
            binding.tvNextStep.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_amber_text));
            binding.tvLastTested.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_amber_text));
        } else {
            // Unsafe / Do not drink
            binding.cardStatusBanner.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.alert_red_bg));
            binding.cardStatusBanner.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.status_danger));
            binding.ivStatusIcon.setImageResource(R.drawable.ic_warning);
            binding.ivStatusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.alert_red_text));
            binding.tvStatusTitle.setText(R.string.ui_pub_unsafe);
            binding.tvStatusTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_red_text));
            binding.tvScoreBadge.setText(score + "/100");
            binding.tvScoreBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_red_text));
            binding.tvNextStep.setText(R.string.ui_pub_unsafe_txt);
            binding.tvNextStep.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_red_text));
            binding.tvLastTested.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_red_text));
        }

        String lastTestedText = getString(R.string.ui_pub_last) + ": " + getString(R.string.ui_pub_date);
        binding.tvLastTested.setText(lastTestedText);

        // 3. Four Reading Rings
        bindReadingRings(data);
    }

    private void bindReadingRings(@NonNull VillageWaterData data) {
        int successColor = ContextCompat.getColor(requireContext(), R.color.status_success);
        int warningColor = ContextCompat.getColor(requireContext(), R.color.status_warning);
        int dangerColor = ContextCompat.getColor(requireContext(), R.color.status_danger);

        double tdsVal = 312;
        double phVal = 7.2;
        double clVal = 0.5;
        double niVal = 12;
        ReadingStatus tdsStat = ReadingStatus.SAFE;
        ReadingStatus phStat = ReadingStatus.SAFE;
        ReadingStatus clStat = ReadingStatus.SAFE;
        ReadingStatus niStat = ReadingStatus.SAFE;

        for (WaterReading r : data.getReadings()) {
            if (r.getType() == ReadingType.TDS) {
                tdsVal = r.getValue();
                tdsStat = r.getStatus();
            } else if (r.getType() == ReadingType.PH) {
                phVal = r.getValue();
                phStat = r.getStatus();
            } else if (r.getType() == ReadingType.CHLORINE) {
                clVal = r.getValue();
                clStat = r.getStatus();
            } else if (r.getType() == ReadingType.NITRATE) {
                niVal = r.getValue();
                niStat = r.getStatus();
            }
        }

        binding.ringTds.setReading(
                getString(R.string.ui_r_tds),
                String.valueOf((int) tdsVal),
                getString(R.string.ui_unit_ppm),
                getStatusString(tdsStat),
                getStatusColor(tdsStat, successColor, warningColor, dangerColor),
                (float) Math.min(1.0, tdsVal / 500.0),
                true
        );

        binding.ringPh.setReading(
                getString(R.string.ui_r_ph),
                String.valueOf(phVal),
                "",
                getStatusString(phStat),
                getStatusColor(phStat, successColor, warningColor, dangerColor),
                (float) (phVal / 10.0),
                true
        );

        binding.ringChlorine.setReading(
                getString(R.string.ui_r_cl),
                String.valueOf(clVal),
                getString(R.string.ui_unit_mgl),
                getStatusString(clStat),
                getStatusColor(clStat, successColor, warningColor, dangerColor),
                (float) Math.min(1.0, clVal / 1.0),
                true
        );

        binding.ringNitrate.setReading(
                getString(R.string.ui_r_ni),
                String.valueOf((int) niVal),
                getString(R.string.ui_unit_mgl),
                getStatusString(niStat),
                getStatusColor(niStat, successColor, warningColor, dangerColor),
                (float) Math.min(1.0, niVal / 80.0),
                true
        );
    }

    private String getStatusString(ReadingStatus status) {
        if (status == ReadingStatus.SAFE) {
            return getString(R.string.ui_s_safe);
        } else if (status == ReadingStatus.WATCH) {
            return getString(R.string.ui_s_low);
        } else {
            return getString(R.string.ui_tag_unsafe);
        }
    }

    private int getStatusColor(ReadingStatus status, int success, int warning, int danger) {
        if (status == ReadingStatus.SAFE) {
            return success;
        } else if (status == ReadingStatus.WATCH) {
            return warning;
        } else {
            return danger;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
