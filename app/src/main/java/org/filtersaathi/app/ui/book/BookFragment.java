package org.filtersaathi.app.ui.book;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentBookBinding;

/**
 * BookFragment (Screen 3):
 * - Step 2 of 3 (Register, Book, Track)
 * - If user is signed out, redirects to Register (then OTP, then returns to Book)
 * - District spinner built from ui_district_names (display) and ui_district_keys (stored value)
 * - Pre-fills district if selected from Home map
 * - All fields required; PIN exactly 6 digits
 * - On submit, generates booking (FS-2026-####) and navigates to Track (popUpTo Book inclusive)
 */
@AndroidEntryPoint
public class BookFragment extends Fragment {

    private FragmentBookBinding binding;
    private BookViewModel viewModel;

    private String[] districtNames;
    private String[] districtKeys;
    private String selectedDistrictKey = "";

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

            viewModel.submitBooking(districtToSubmit, taluk, village, sump, pin);
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
