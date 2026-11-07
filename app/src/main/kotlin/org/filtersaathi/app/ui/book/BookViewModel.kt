package org.filtersaathi.app.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.BookingRequest
import org.filtersaathi.app.domain.model.UserProfile
import javax.inject.Inject

data class BookValidationResult(
    val isDistrictValid: Boolean,
    val isTalukValid: Boolean,
    val isVillageValid: Boolean,
    val isSumpValid: Boolean,
    val isPinValid: Boolean
) {
    val isValid: Boolean
        get() = isDistrictValid && isTalukValid && isVillageValid && isSumpValid && isPinValid
}

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    val currentUser: LiveData<UserProfile?> = repository.currentUser
    val selectedDistrict: LiveData<String?> = repository.selectedDistrict
    val isOfflineDemo: LiveData<Boolean> = repository.isOfflineDemo

    fun validate(
        district: String,
        taluk: String,
        village: String,
        sumpLocation: String,
        pinCode: String
    ): BookValidationResult {
        val isDistrictValid = district.trim().isNotEmpty()
        val isTalukValid = taluk.trim().isNotEmpty()
        val isVillageValid = village.trim().isNotEmpty()
        val isSumpValid = sumpLocation.trim().isNotEmpty()
        val isPinValid = pinCode.trim().length == 6 && pinCode.trim().all { it.isDigit() }

        return BookValidationResult(
            isDistrictValid = isDistrictValid,
            isTalukValid = isTalukValid,
            isVillageValid = isVillageValid,
            isSumpValid = isSumpValid,
            isPinValid = isPinValid
        )
    }

    fun submitBooking(
        district: String,
        taluk: String,
        village: String,
        sumpLocation: String,
        pinCode: String
    ): BookingRequest {
        return repository.bookInstallation(
            district = district.trim(),
            taluk = taluk.trim(),
            village = village.trim(),
            sumpLocation = sumpLocation.trim(),
            pinCode = pinCode.trim()
        )
    }

    fun clearSelectedDistrict() {
        repository.selectDistrict(null)
    }
}
