package org.filtersaathi.app.ui.state

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("filtersaathi_app_prefs", Context.MODE_PRIVATE)

    private val _isLanguageChosen = MutableLiveData(prefs.getBoolean("key_language_chosen", false))
    val isLanguageChosen: LiveData<Boolean> = _isLanguageChosen

    fun isLanguageAlreadyChosen(): Boolean {
        return prefs.getBoolean("key_language_chosen", false)
    }

    fun setLanguageChosen(langCode: String) {
        prefs.edit()
            .putBoolean("key_language_chosen", true)
            .putString("key_selected_locale", langCode)
            .commit()
        _isLanguageChosen.value = true
    }
}
