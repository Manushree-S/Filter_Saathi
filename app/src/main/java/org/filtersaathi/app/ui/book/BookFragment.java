package org.filtersaathi.app.ui.book;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentBookBinding;
import org.filtersaathi.app.util.ResolvedAddress;

/**
 * BookFragment (Screen 3):
 * - Step 2 of 3 (Register, Book, Track)
 * - If user is signed out, redirects to Register (then OTP, then returns to Book)
 * - District spinner built from ui_district_names (display) and ui_district_keys (stored value)
 * - Pre-fills district if selected from Home map
 * - All fields required; PIN exactly 6 digits
 * - On submit, generates booking (FS-2026-####) and navigates to Track (popUpTo Book inclusive)
 * - Full-screen Map Location Picker for selecting sump position
 */
@AndroidEntryPoint
public class BookFragment extends Fragment {

    private FragmentBookBinding binding;
    private BookViewModel viewModel;

    private String[] districtNames;
    private String[] districtKeys;
    private String selectedDistrictKey = "";

    private Double selectedLatitude = null;
    private Double selectedLongitude = null;

    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if ((fineGranted != null && fineGranted) || (coarseGranted != null && coarseGranted)) {
                        openMapPicker();
                    } else {
                        handlePermissionDenied();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // Check authentication state: if signed out, redirect to Register
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null || !user.isLoggedIn()) {
                Bundle args = new Bundle();
                args.putBoolean("fromBook", true);
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_book_to_register, args);
            }
        });

        setupDistrictDropdown();
        setupTextWatchers();
        setupSubmitButton();
        setupLocationButton();
        setupCardElevation(binding.cardBook, 3f, 6f);
    }

    private void setupDistrictDropdown() {
        districtNames = getResources().getStringArray(R.array.ui_district_names);
        districtKeys = getResources().getStringArray(R.array.ui_district_keys);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                districtNames
        );
        binding.actvDistrict.setAdapter(adapter);

        binding.actvDistrict.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < districtKeys.length) {
                selectedDistrictKey = districtKeys[position];
                binding.tilDistrict.setError(null);
                binding.tvBookError.setVisibility(View.GONE);
            }
        });

        // Check if pre-filled district is available from Home map
        viewModel.getSelectedDistrict().observe(getViewLifecycleOwner(), prefilled -> {
            if (prefilled != null && !prefilled.trim().isEmpty()) {
                for (int i = 0; i < districtKeys.length; i++) {
                    if (districtKeys[i].equalsIgnoreCase(prefilled.trim())
                            || districtNames[i].equalsIgnoreCase(prefilled.trim())) {
                        binding.actvDistrict.setText(districtNames[i], false);
                        selectedDistrictKey = districtKeys[i];
                        break;
                    }
                }
            }
        });
    }

    private void setupTextWatchers() {
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                binding.tvBookError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etTaluk.addTextChangedListener(clearErrorWatcher);
        binding.etVillage.addTextChangedListener(clearErrorWatcher);
        binding.etSumpLocation.addTextChangedListener(clearErrorWatcher);
        binding.etPinCode.addTextChangedListener(clearErrorWatcher);
    }

    private void setupLocationButton() {
        binding.btnUseCurrentLocation.setOnClickListener(v -> {
            boolean fineGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            boolean coarseGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (fineGranted || coarseGranted) {
                openMapPicker();
            } else {
                locationPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        });
    }

    private void openMapPicker() {
        MapPickerDialogFragment picker = MapPickerDialogFragment.newInstance();
        picker.setOnLocationSelectedListener((lat, lng, resolved) -> {
            selectedLatitude = lat;
            selectedLongitude = lng;
            if (resolved != null) {
                processResolvedAddress(resolved);
            } else {
                binding.noticeLocationFilled.setVisibility(View.VISIBLE);
            }
        });
        picker.show(getChildFragmentManager(), "MapPickerDialog");
    }

    private void handlePermissionDenied() {
        boolean showFineRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean showCoarseRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (!showFineRationale && !showCoarseRationale) {
            new AlertDialog.Builder(requireContext())
                    .setMessage(R.string.ui_location_perm_settings)
                    .setPositiveButton(R.string.ui_location_open_settings_btn, (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        } else {
            Toast.makeText(requireContext(), R.string.ui_location_perm_denied, Toast.LENGTH_LONG).show();
        }
    }

    private void processResolvedAddress(@NonNull ResolvedAddress resolved) {
        if (resolved.isOutsideKarnataka()) {
            Toast.makeText(requireContext(), R.string.ui_location_outside_karnataka, Toast.LENGTH_LONG).show();
            return;
        }

        boolean filledAny = false;

        // Village / Gram Panchayat
        String currentVillage = binding.etVillage.getText() != null ? binding.etVillage.getText().toString().trim() : "";
        if (currentVillage.isEmpty() && resolved.getVillageOrPanchayat() != null) {
            binding.etVillage.setText(resolved.getVillageOrPanchayat());
            filledAny = true;
        }

        // Sump location suggestion
        String currentSump = binding.etSumpLocation.getText() != null ? binding.etSumpLocation.getText().toString().trim() : "";
        if (currentSump.isEmpty() && resolved.getSumpLocation() != null) {
            binding.etSumpLocation.setText(resolved.getSumpLocation());
            filledAny = true;
        }

        // PIN Code
        String currentPin = binding.etPinCode.getText() != null ? binding.etPinCode.getText().toString().trim() : "";
        if (currentPin.isEmpty() && resolved.getPostalCode() != null) {
            binding.etPinCode.setText(resolved.getPostalCode());
            filledAny = true;
        }

        // District
        String currentDistrict = selectedDistrictKey;
        if (currentDistrict.isEmpty() && binding.actvDistrict.getText() != null) {
            currentDistrict = binding.actvDistrict.getText().toString().trim();
        }
        if (currentDistrict.isEmpty() && resolved.getDistrict() != null) {
            String targetDistrictKey = resolved.getDistrict();
            for (int i = 0; i < districtKeys.length; i++) {
                if (districtKeys[i].equalsIgnoreCase(targetDistrictKey)) {
                    binding.actvDistrict.setText(districtNames[i], false);
                    selectedDistrictKey = districtKeys[i];
                    filledAny = true;
                    break;
                }
            }
        }

        if (filledAny) {
            binding.noticeLocationFilled.setVisibility(View.VISIBLE);
        }
    }

    private void setupSubmitButton() {
        binding.btnBook.setOnClickListener(v -> {
            String taluk = binding.etTaluk.getText() != null ? binding.etTaluk.getText().toString() : "";
            String village = binding.etVillage.getText() != null ? binding.etVillage.getText().toString() : "";
            String sump = binding.etSumpLocation.getText() != null ? binding.etSumpLocation.getText().toString() : "";
            String pin = binding.etPinCode.getText() != null ? binding.etPinCode.getText().toString() : "";

            // Use selected district key or fallback to text if valid
            String districtToSubmit = selectedDistrictKey;
            if (districtToSubmit.isEmpty()) {
                String typedDistrict = binding.actvDistrict.getText().toString().trim();
                for (int i = 0; i < districtNames.length; i++) {
                    if (districtNames[i].equalsIgnoreCase(typedDistrict)) {
                        districtToSubmit = districtKeys[i];
                        break;
                    }
                }
            }

            BookValidationResult validation = viewModel.validate(districtToSubmit, taluk, village, sump, pin);

            if (!validation.isValid()) {
                binding.tvBookError.setVisibility(View.VISIBLE);
                if (!validation.isDistrictValid()) {
                    binding.tilDistrict.setError(getString(R.string.ui_err_book));
                } else {
                    binding.tilDistrict.setError(null);
                }
                if (!validation.isTalukValid()) {
                    binding.tilTaluk.setError(getString(R.string.ui_err_book));
                } else {
                    binding.tilTaluk.setError(null);
                }
                if (!validation.isVillageValid()) {
                    binding.tilVillage.setError(getString(R.string.ui_err_book));
                } else {
                    binding.tilVillage.setError(null);
                }
                if (!validation.isSumpValid()) {
                    binding.tilSumpLocation.setError(getString(R.string.ui_err_book));
                } else {
                    binding.tilSumpLocation.setError(null);
                }
                if (!validation.isPinValid()) {
                    binding.tilPinCode.setError(getString(R.string.ui_err_book));
                } else {
                    binding.tilPinCode.setError(null);
                }
                return;
            }

            // Valid submission
            binding.tilDistrict.setError(null);
            binding.tilTaluk.setError(null);
            binding.tilVillage.setError(null);
            binding.tilSumpLocation.setError(null);
            binding.tilPinCode.setError(null);
            binding.tvBookError.setVisibility(View.GONE);

            viewModel.submitBooking(districtToSubmit, taluk, village, sump, pin, selectedLatitude, selectedLongitude);
            viewModel.clearSelectedDistrict();

            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_book_to_track);
        });
    }

    private void setupCardElevation(@NonNull MaterialCardView card, float baseElevationDp, float liftElevationDp) {
        if (!areAnimationsEnabled()) {
            return;
        }

        float density = getResources().getDisplayMetrics().density;
        float basePx = baseElevationDp * density;
        float liftPx = liftElevationDp * density;

        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ObjectAnimator.ofFloat(card, "cardElevation", basePx, liftPx)
                            .setDuration(120)
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    ObjectAnimator.ofFloat(card, "cardElevation", liftPx, basePx)
                            .setDuration(150)
                            .start();
                    break;
            }
            return false;
        });
    }

    private boolean areAnimationsEnabled() {
        try {
            float scale = Settings.Global.getFloat(
                    requireContext().getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
            return scale > 0.0f;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
