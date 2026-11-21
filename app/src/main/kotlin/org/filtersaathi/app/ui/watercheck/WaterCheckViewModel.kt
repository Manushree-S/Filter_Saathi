package org.filtersaathi.app.ui.watercheck

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.BookingRequest
import org.filtersaathi.app.domain.model.VillageWaterData
import javax.inject.Inject

@HiltViewModel
class WaterCheckViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    val currentBooking: LiveData<BookingRequest?> = repository.currentBooking
    val selectedDistrictFromRepo: LiveData<String?> = repository.selectedDistrict

    private val _selectedDistrict = MutableLiveData<String?>(null)
    val selectedDistrict: LiveData<String?> = _selectedDistrict

    private val _waterData = MutableLiveData<VillageWaterData?>(null)
    val waterData: LiveData<VillageWaterData?> = _waterData

    fun selectDistrict(district: String) {
        _selectedDistrict.value = district
        val data = repository.getWaterCheck(district)
        _waterData.value = data
    }
}
