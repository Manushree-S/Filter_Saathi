package org.filtersaathi.app.ui.myfilters

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.FilterDevice
import javax.inject.Inject

@HiltViewModel
class MyFiltersViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    val filterDevices: LiveData<List<FilterDevice>> = repository.filterDevices

    fun getSortedDevices(): List<FilterDevice> {
        val list = filterDevices.value ?: emptyList()
        return list.sortedWith(
            compareByDescending<FilterDevice> { it.getOverallStatus().priority }
                .thenBy { it.getMostUrgentDaysLeft() }
        )
    }

    fun getDeviceById(id: String): FilterDevice? {
        return repository.getFilterDeviceById(id)
    }

    fun markItemServiced(deviceId: String, itemKey: String) {
        repository.markItemServiced(deviceId, itemKey)
    }
}
