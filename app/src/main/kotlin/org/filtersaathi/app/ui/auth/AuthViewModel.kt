package org.filtersaathi.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.UserProfile
import javax.inject.Inject

data class RegistrationValidation(
    val isNameValid: Boolean,
    val isPhoneValid: Boolean
) {
    val isValid: Boolean get() = isNameValid && isPhoneValid
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    val currentUser: LiveData<UserProfile?> = repository.currentUser

    // Holds pending inputs in memory during registration -> OTP flow
    var pendingName: String = ""
        private set
    var pendingPhone: String = ""
        private set
    var isFromBookFlow: Boolean = false

    private val phoneRegex = Regex("^[6-9]\\d{9}$")

    fun validateAndStoreRegistration(name: String, phone: String): RegistrationValidation {
        val trimmedName = name.trim()
        val trimmedPhone = phone.trim()

        val isNameValid = trimmedName.isNotEmpty()
        val isPhoneValid = trimmedPhone.matches(phoneRegex)

        if (isNameValid && isPhoneValid) {
            pendingName = trimmedName
            pendingPhone = trimmedPhone
        }
        return RegistrationValidation(isNameValid, isPhoneValid)
    }

    fun verifyOtp(otp: String): Boolean {
        val trimmedOtp = otp.trim()
        val isValidOtp = trimmedOtp.length == 6 && trimmedOtp.all { it.isDigit() }
        if (isValidOtp && pendingName.isNotEmpty() && pendingPhone.isNotEmpty()) {
            repository.registerOrLogin(pendingName, pendingPhone)
            return true
        }
        return false
    }

    fun clearPendingAuth() {
        pendingName = ""
        pendingPhone = ""
        isFromBookFlow = false
    }
}
