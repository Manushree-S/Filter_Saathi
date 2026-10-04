package org.filtersaathi.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.filtersaathi.app.data.local.entity.FilterEntity
import org.filtersaathi.app.data.repository.FilterRepository
import org.filtersaathi.app.domain.model.ImpactStatItem
import org.filtersaathi.app.domain.model.MaintenanceItem
import org.filtersaathi.app.domain.model.VillageSummary
import org.filtersaathi.app.domain.model.WaterQualityParameter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FilterRepository
) : ViewModel() {

    // Role state: CARETAKER vs OFFICER vs ENGINEER
    private val _userRole = MutableLiveData("CARETAKER")
    val userRole: LiveData<String> = _userRole

    // Urgent banner visibility state (can be dismissed by user)
    private val _isAlertDismissed = MutableLiveData(false)
    val isAlertDismissed: LiveData<Boolean> = _isAlertDismissed

    // Loading State
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Expose LiveData directly for clean Java consumption
    val villageSummary: LiveData<VillageSummary> = repository.villageSummary
    val upcomingMaintenance: LiveData<List<MaintenanceItem>> = repository.upcomingMaintenance
    val impactStats: LiveData<List<ImpactStatItem>> = repository.impactStats
    val latestWaterParams: LiveData<List<WaterQualityParameter>> = repository.latestWaterParams
    val mostUrgentFilter: LiveData<FilterEntity?> = repository.mostUrgentFilter
    val isOffline: LiveData<Boolean> = repository.isOffline

    init {
        viewModelScope.launch {
            repository.seedRoomDatabaseIfEmpty()
        }
    }

    fun dismissAlert() {
        _isAlertDismissed.value = true
    }

    fun toggleRole() {
        _userRole.value = if (_userRole.value == "CARETAKER") "OFFICER" else "CARETAKER"
    }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.refreshData()
            _isLoading.value = false
        }
    }
}
