package org.filtersaathi.app.ui.home;

import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentHomeBinding;

/**
 * HomeFragment for FilterSaathi Phase 1 (Step 2a + Step 2b).
 * Displays:
 * 1. 6dp tricolour strip
 * 2. Toolbar row with logo, brand and EN | ಕನ್ನಡ segmented toggle
 * 3. Hero container with headline, sub-headline, SumpHeroView, saffron CTA and ghost button
 * 4. "How it works" section with 3 cards (Panchayat -> FilterSaathi -> BWSSB or installer) and arrows
 * 5. Green notice "No payments in this app."
 * 6. Test strip promo banner with 4 colored pads and "Scan strip" CTA
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupLanguageToggle();
        setupNavigationButtons();
        setupHowItWorksSection();
        setupTestStripPromo();
    }

    private void setupLanguageToggle() {
        updateLanguageToggleUI();

        binding.btnLangEn.setOnClickListener(v -> setAppLanguage("en"));
        binding.btnLangKn.setOnClickListener(v -> setAppLanguage("kn"));
    }

    private void updateLanguageToggleUI() {
        String currentLang = viewModel.getCurrentLanguage();
        boolean isKannada = "kn".equalsIgnoreCase(currentLang);
        binding.btnLangKn.setSelected(isKannada);
        binding.btnLangEn.setSelected(!isKannada);
    }

    private void setAppLanguage(@NonNull String langCode) {
        if (langCode.equalsIgnoreCase(viewModel.getCurrentLanguage())) {
            return;
        }

        // 1. Save language choice to SharedPreferences with commit() BEFORE calling setApplicationLocales
        viewModel.saveLanguagePreference(langCode);

        // 2. Set application locales via AppCompatDelegate (triggers activity recreate cleanly)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langCode));
    }

    private void setupNavigationButtons() {
        binding.btnRequestInstallation.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_book);
        });

        binding.btnCheckVillageWater.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_water_check);
        });
    }

    private void setupHowItWorksSection() {
        // Adjust arrows based on screen orientation / width
        updateHowItWorksArrows();

        // Add press elevation / lift animation to the cards (skipped when animations are disabled)
        setupCardLift(binding.cardHowPanchayat, 2f, 6f);
        setupCardLift(binding.cardHowFilterSaathi, 4f, 8f);
        setupCardLift(binding.cardHowInstaller, 2f, 6f);
    }

    private void updateHowItWorksArrows() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int screenWidthDp = getResources().getConfiguration().screenWidthDp;

        // Wide layout (e.g. tablet or landscape): right arrow; stacked: down arrow
        int arrowRes = (isLandscape || screenWidthDp >= 600) ? R.drawable.ic_arrow_right : R.drawable.ic_arrow_down;
        binding.ivArrowHow1.setImageResource(arrowRes);
        binding.ivArrowHow2.setImageResource(arrowRes);
    }

    private void setupCardLift(@NonNull MaterialCardView card, float baseElevationDp, float liftElevationDp) {
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

    private void setupTestStripPromo() {
        binding.btnPromoScanStrip.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_test_strip);
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
