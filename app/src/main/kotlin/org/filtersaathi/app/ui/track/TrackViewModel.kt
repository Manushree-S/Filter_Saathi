package org.filtersaathi.app.ui.track

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.BannerEvent
import org.filtersaathi.app.domain.model.BookingRequest
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    val currentBooking: LiveData<BookingRequest?> = repository.currentBooking
    val topBannerEvent: LiveData<BannerEvent?> = repository.topBannerEvent
    val isOfflineDemo: LiveData<Boolean> = repository.isOfflineDemo

    fun advanceStage(): Int = repository.advanceStageDemo()

    fun resetToNewRequest() = repository.resetToNewRequestDemo()

    fun submitRating(stars: Int) = repository.submitRating(stars)

    fun dismissBanner() = repository.dismissTopBanner()
}
