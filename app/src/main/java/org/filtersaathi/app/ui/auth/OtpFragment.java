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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentOtpBinding;

/**
 * OtpFragment (Screen 2):
 * - Step 1 of 3 verification
 * - Accepts any 6 digits in demo mode (with note: "Demo: any 6 digits work.")
 * - On success, returns user to Book (if redirected from Book) or Home
 */
@AndroidEntryPoint
public class OtpFragment extends Fragment {

    private FragmentOtpBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupSubtitle();
        setupTextWatcher();
        setupVerifyButton();
        setupCardElevation(binding.cardOtp, 3f, 6f);
    }

    private void setupSubtitle() {
        String phone = viewModel.getPendingPhone();
        String subtitle = getString(R.string.ui_otp_sent) + " +91 " + phone;
        binding.tvOtpSubtitle.setText(subtitle);
    }

    private void setupTextWatcher() {
        binding.etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                binding.tilOtp.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupVerifyButton() {
        binding.btnVerify.setOnClickListener(v -> {
            String otp = binding.etOtp.getText() != null ? binding.etOtp.getText().toString() : "";
            boolean success = viewModel.verifyOtp(otp);

            if (success) {
                binding.tilOtp.setError(null);
                NavController navController = Navigation.findNavController(v);
                if (viewModel.isFromBookFlow()) {
                    viewModel.clearPendingAuth();
                    navController.navigate(R.id.action_otp_to_book);
                } else {
                    viewModel.clearPendingAuth();
                    navController.navigate(R.id.action_otp_to_home);
                }
            } else {
                binding.tilOtp.setError(getString(R.string.ui_err_otp));
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
