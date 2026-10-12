package org.filtersaathi.app.ui.welcome;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentWelcomeBinding;
import org.filtersaathi.app.ui.state.WelcomeViewModel;

@AndroidEntryPoint
public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;
    private WelcomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WelcomeViewModel.class);

        // If language has already been selected on a previous launch, skip straight to Home
        if (viewModel.isLanguageAlreadyChosen()) {
            navigateToHome();
            return;
        }

        binding.btnKannada.setOnClickListener(v -> selectLanguage("kn"));
        binding.btnEnglish.setOnClickListener(v -> selectLanguage("en"));
    }

    private void selectLanguage(@NonNull String langCode) {
        // (a) Save language preference BEFORE calling setApplicationLocales, as setApplicationLocales recreates the activity
        viewModel.setLanguageChosen(langCode);
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langCode));
        navigateToHome();
    }

    private void navigateToHome() {
        if (getView() != null) {
            NavController navController = Navigation.findNavController(requireView());
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.navigation_welcome) {
                navController.navigate(R.id.action_welcome_to_home);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
