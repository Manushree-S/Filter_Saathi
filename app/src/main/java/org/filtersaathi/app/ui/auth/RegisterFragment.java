package org.filtersaathi.app.ui.auth;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentRegisterBinding;

/**
 * RegisterFragment (Screen 1):
 * - Step 1 of 3 (Register, Book, Track)
 * - Full name and +91 mobile input (digits only, max 10, validated against ^[6-9]\d{9}$)
 * - Shows ui_err_* strings on validation failure
 * - Navigates to OTP on valid submission
 */
@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        if (getArguments() != null && getArguments().containsKey("fromBook")) {
            viewModel.setFromBookFlow(getArguments().getBoolean("fromBook", false));
        }

        setupTextWatchers();
        setupSubmitButton();
        setupCardElevation(binding.cardRegister, 3f, 6f);
    }

    private void setupTextWatchers() {
        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                binding.tilName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                binding.tilPhone.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSubmitButton() {
        binding.btnSendOtp.setOnClickListener(v -> {
            String name = binding.etName.getText() != null ? binding.etName.getText().toString() : "";
            String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString() : "";

            RegistrationValidation result = viewModel.validateAndStoreRegistration(name, phone);

            if (!result.isNameValid()) {
                binding.tilName.setError(getString(R.string.ui_err_reg));
            } else {
                binding.tilName.setError(null);
            }

            if (!result.isPhoneValid()) {
                binding.tilPhone.setError(getString(R.string.ui_err_reg));
            } else {
                binding.tilPhone.setError(null);
            }

            if (result.isValid()) {
                Navigation.findNavController(v).navigate(R.id.action_register_to_otp);
            }
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
