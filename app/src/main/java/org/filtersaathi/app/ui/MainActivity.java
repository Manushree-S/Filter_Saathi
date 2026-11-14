package org.filtersaathi.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.data.repository.FakeRepository;
import org.filtersaathi.app.databinding.ActivityMainBinding;
import org.filtersaathi.app.domain.model.BannerEvent;
import org.filtersaathi.app.domain.model.BannerType;
import org.filtersaathi.app.domain.model.UserProfile;

import java.util.Locale;

import javax.inject.Inject;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    FakeRepository fakeRepository;

    private ActivityMainBinding binding;
    private NavController navController;
    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initTextToSpeech();
        setupNavigation();
        setupToolbarControls();
        observeRepositoryState();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                boolean isWelcome = destination.getId() == R.id.navigation_welcome;
                int visibility = isWelcome ? View.GONE : View.VISIBLE;
                binding.layoutTopAppArea.setVisibility(visibility);
                binding.fabSpeaker.setVisibility(visibility);
                binding.bottomNavigation.setVisibility(visibility);
            });
        }
    }

    private void setupToolbarControls() {
        // 1. Offline Mode Chip
        binding.chipOfflineMode.setOnClickListener(v -> fakeRepository.toggleOfflineDemo());

        // 2. Notification Bell
        binding.btnNotificationBell.setOnClickListener(v -> {
            binding.tvNotificationBadge.setVisibility(View.GONE);
            showNotificationsBottomSheet();
        });

        // 3. Accessibility "Aa"
        binding.btnAccessibility.setOnClickListener(v -> showAccessibilityBottomSheet());

        // 4. Dark Mode Toggle
        binding.btnDarkModeToggle.setOnClickListener(v -> toggleDarkMode());

        // 5. Language Segmented Toggle
        updateLanguageToggleState();
        binding.mainBtnLangEn.setOnClickListener(v -> setAppLanguage("en"));
        binding.mainBtnLangKn.setOnClickListener(v -> setAppLanguage("kn"));

        // 6. Register / Login / User Button
        binding.btnAuthStatus.setOnClickListener(v -> {
            UserProfile user = fakeRepository.getCurrentUser().getValue();
            if (user != null && user.isLoggedIn()) {
                fakeRepository.logout();
            } else {
                if (navController != null) {
                    navController.navigate(R.id.navigation_register);
                }
            }
        });

        // 7. Overflow Menu (Officer overview)
        binding.btnOverflowMenu.setOnClickListener(this::showOverflowMenu);

        // 8. Floating Speaker Button
        binding.fabSpeaker.setOnClickListener(v -> speakCurrentScreen());

        // 9. Global Banner Close
        binding.btnGlobalBannerClose.setOnClickListener(v -> fakeRepository.dismissTopBanner());
    }

    private void observeRepositoryState() {
        // Observe Offline Mode
        fakeRepository.isOfflineDemo().observe(this, isOffline -> {
            if (isOffline != null && isOffline) {
                binding.viewOfflineDot.setBackgroundResource(R.drawable.bg_stage_current);
                binding.tvOfflineStatus.setText(R.string.ui_offline);
                binding.chipOfflineMode.setContentDescription(getString(R.string.ui_offline));
                binding.layoutOfflineBar.setVisibility(View.VISIBLE);
            } else {
                binding.viewOfflineDot.setBackgroundResource(R.drawable.bg_stage_done);
                binding.tvOfflineStatus.setText(R.string.ui_online);
                binding.chipOfflineMode.setContentDescription(getString(R.string.ui_online));
                binding.layoutOfflineBar.setVisibility(View.GONE);
            }
        });

        // Observe Current User
        fakeRepository.getCurrentUser().observe(this, user -> {
            if (user != null && user.isLoggedIn()) {
                String label = getString(R.string.ui_hi) + " " + user.getName() + " (" + getString(R.string.ui_logout) + ")";
                binding.btnAuthStatus.setText(label);
                binding.btnAuthStatus.setBackgroundResource(R.drawable.bg_lang_toggle_container);
                binding.btnAuthStatus.setTextColor(ContextCompat.getColor(this, R.color.text_primary_light));
            } else {
                binding.btnAuthStatus.setText(R.string.ui_login);
                binding.btnAuthStatus.setBackgroundResource(R.drawable.bg_btn_saffron);
                binding.btnAuthStatus.setTextColor(ContextCompat.getColor(this, R.color.hero_btn_text));
            }
        });

        // Observe Unified Top Banner System
        fakeRepository.getTopBannerEvent().observe(this, this::updateTopBanner);
    }

    private void updateTopBanner(@Nullable BannerEvent event) {
        if (event == null || event.getReferenceNumber() == null || event.getReferenceNumber().isEmpty()) {
            binding.layoutGlobalTopBanner.setVisibility(View.GONE);
            return;
        }

        binding.layoutGlobalTopBanner.setVisibility(View.VISIBLE);
        if (event.getType() == BannerType.SAVED_OFFLINE) {
            binding.layoutGlobalTopBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.alert_amber_bg));
            binding.ivGlobalBannerIcon.setImageResource(R.drawable.ic_sync_offline);
            binding.ivGlobalBannerIcon.setColorFilter(ContextCompat.getColor(this, R.color.alert_amber_text));
            binding.tvGlobalBannerMessage.setTextColor(ContextCompat.getColor(this, R.color.alert_amber_text));
            binding.btnGlobalBannerClose.setTextColor(ContextCompat.getColor(this, R.color.alert_amber_text));

            String msg = getString(R.string.ui_saved_offline) + " " + event.getReferenceNumber();
            binding.tvGlobalBannerMessage.setText(msg);
        } else {
            binding.layoutGlobalTopBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.alert_green_bg));
            binding.ivGlobalBannerIcon.setImageResource(R.drawable.ic_check);
            binding.ivGlobalBannerIcon.setColorFilter(ContextCompat.getColor(this, R.color.alert_green_text));
            binding.tvGlobalBannerMessage.setTextColor(ContextCompat.getColor(this, R.color.alert_green_text));
            binding.btnGlobalBannerClose.setTextColor(ContextCompat.getColor(this, R.color.alert_green_text));

            String msg;
            if (event.getType() == BannerType.SYNCED) {
                msg = getString(R.string.ui_synced) + " " + event.getReferenceNumber();
            } else if (event.getType() == BannerType.REPORT_SENT) {
                msg = getString(R.string.ui_rep_done) + " " + event.getReferenceNumber();
            } else {
                msg = getString(R.string.ui_banner) + " " + event.getReferenceNumber();
            }
            binding.tvGlobalBannerMessage.setText(msg);
        }
    }

    private void showNotificationsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.sheet_notifications, null);
        sheetView.findViewById(R.id.btnCloseNotifications).setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void showAccessibilityBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.sheet_accessibility, null);
        sheetView.findViewById(R.id.btnReadAloud).setOnClickListener(v -> {
            dialog.dismiss();
            speakCurrentScreen();
        });
        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void toggleDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void updateLanguageToggleState() {
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        String currentLang = !locales.isEmpty() ? locales.get(0).getLanguage() : getResources().getConfiguration().locale.getLanguage();
        boolean isKannada = "kn".equalsIgnoreCase(currentLang);
        binding.mainBtnLangKn.setSelected(isKannada);
        binding.mainBtnLangEn.setSelected(!isKannada);
    }

    private void setAppLanguage(String languageTag) {
        SharedPreferences prefs = getSharedPreferences("filtersaathi_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("app_language", languageTag).commit();
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag));
        updateLanguageToggleState();
    }

    private void showOverflowMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, R.string.ui_ov_title);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1 && navController != null) {
                navController.navigate(R.id.navigation_officer_overview);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true;
                setupTtsLanguage();
            }
        });
    }

    private void setupTtsLanguage() {
        if (!isTtsReady || textToSpeech == null) return;
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        String lang = !locales.isEmpty() ? locales.get(0).getLanguage() : "en";
        Locale targetLocale = "kn".equalsIgnoreCase(lang) ? new Locale("kn", "IN") : new Locale("en", "IN");
        int result = textToSpeech.setLanguage(targetLocale);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            textToSpeech.setLanguage(Locale.US);
        }
    }

    private void speakCurrentScreen() {
        if (navController == null) return;
        NavDestination current = navController.getCurrentDestination();
        if (current == null) return;

        CharSequence label = current.getLabel();
        String textToSpeak = label != null ? label.toString() : getString(R.string.ui_brand);
        String fullSpeech = getString(R.string.ui_brand) + ". " + textToSpeak;

        if (isTtsReady && textToSpeech != null) {
            setupTtsLanguage();
            textToSpeech.speak(fullSpeech, TextToSpeech.QUEUE_FLUSH, null, "FilterSaathi_TTS");
        } else {
            Toast.makeText(this, R.string.ui_voice_note, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
        binding = null;
    }
}
