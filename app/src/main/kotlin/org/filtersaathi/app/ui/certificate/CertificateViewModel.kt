package org.filtersaathi.app.ui.certificate

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.BookingRequest
import javax.inject.Inject

@HiltViewModel
class CertificateViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    val currentBooking: LiveData<BookingRequest?> = repository.currentBooking
}
