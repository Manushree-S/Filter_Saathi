package org.filtersaathi.app.ui.home

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.BannerEvent
import org.filtersaathi.app.domain.model.BookingRequest
import org.filtersaathi.app.domain.model.UserProfile
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fakeRepository: FakeRepository
) : ViewModel() {

    private val prefs = context.getSharedPreferences("filtersaathi_app_prefs", Context.MODE_PRIVATE)

    val currentUser: LiveData<UserProfile?> = fakeRepository.currentUser
    val currentBooking: LiveData<BookingRequest?> = fakeRepository.currentBooking
    val isOfflineDemo: LiveData<Boolean> = fakeRepository.isOfflineDemo
    val topBannerEvent: LiveData<BannerEvent?> = fakeRepository.topBannerEvent

    fun saveLanguagePreference(langCode: String) {
        prefs.edit()
            .putBoolean("key_language_chosen", true)
            .putString("key_selected_locale", langCode)
            .commit()
    }

    fun getCurrentLanguage(): String {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (!appLocales.isEmpty) {
            val tag = appLocales[0]?.language
            if (!tag.isNullOrEmpty()) {
                return tag
            }
        }
        val saved = prefs.getString("key_selected_locale", null)
        if (!saved.isNullOrEmpty()) {
            return saved
        }
        return context.resources.configuration.locales[0].language
    }
}
