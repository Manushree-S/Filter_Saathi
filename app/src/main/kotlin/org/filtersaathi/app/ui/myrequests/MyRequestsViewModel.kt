package org.filtersaathi.app.ui.myrequests

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.*
import javax.inject.Inject

enum class RequestFilterType {
    ALL,
    REQUESTS,
    REPORTS
}

@HiltViewModel
class MyRequestsViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    val currentUser: LiveData<UserProfile?> = repository.currentUser

    fun getFilteredItems(filter: RequestFilterType): List<RequestOrReportItem> {
        val allItems = repository.getUserRequestsAndReports()
        return when (filter) {
            RequestFilterType.ALL -> allItems
            RequestFilterType.REQUESTS -> allItems.filterIsInstance<RequestOrReportItem.Booking>()
            RequestFilterType.REPORTS -> allItems.filterIsInstance<RequestOrReportItem.Report>()
        }
    }

    fun getBooking(id: String): BookingRequest? {
        return repository.getBookingByRequestNumber(id)
    }

    fun getReport(id: String): ProblemReport? {
        return repository.getReportByTicketNumber(id)
    }

    fun getDeviceCare(id: String): FilterDevice? {
        return repository.getFilterDeviceById(id)
    }

    fun markItemServiced(deviceId: String, itemKey: String) {
        repository.markItemServiced(deviceId, itemKey)
    }
}
