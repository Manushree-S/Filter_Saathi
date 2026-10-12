package org.filtersaathi.app.ui.home;

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
import androidx.navigation.Navigation;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentHomeBinding;

/**
 * HomeFragment for FilterSaathi Phase 1 (Step 2a).
 * Displays:
 * 1. 6dp tricolour strip
 * 2. Toolbar row with logo, brand and EN | ಕನ್ನಡ segmented toggle
 * 3. Hero container with headline, sub-headline, SumpHeroView, saffron CTA and ghost button
 * 4. Navigates to Book and Water check placeholder fragments
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
