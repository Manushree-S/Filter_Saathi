package org.filtersaathi.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.filtersaathi.app.R
import org.filtersaathi.app.data.local.dao.FilterDao
import org.filtersaathi.app.data.local.dao.WaterTestDao
import org.filtersaathi.app.data.local.entity.FilterEntity
import org.filtersaathi.app.data.local.entity.WaterTestReadingEntity
import org.filtersaathi.app.data.remote.api.FilterSaathiApiService
import org.filtersaathi.app.domain.engine.MaintenanceRuleEngine
import org.filtersaathi.app.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterRepository @Inject constructor(
    private val filterDao: FilterDao,
    private val waterTestDao: WaterTestDao,
    private val apiService: FilterSaathiApiService
) {
    // Village summary LiveData for Java consumers
    private val _villageSummary = MutableLiveData<VillageSummary>()
    val villageSummary: LiveData<VillageSummary> = _villageSummary

    // Maintenance items LiveData
    private val _upcomingMaintenance = MutableLiveData<List<MaintenanceItem>>()
    val upcomingMaintenance: LiveData<List<MaintenanceItem>> = _upcomingMaintenance

    // Impact stats LiveData
    private val _impactStats = MutableLiveData<List<ImpactStatItem>>()
    val impactStats: LiveData<List<ImpactStatItem>> = _impactStats

    // Latest water quality parameters LiveData
    private val _latestWaterParams = MutableLiveData<List<WaterQualityParameter>>()
    val latestWaterParams: LiveData<List<WaterQualityParameter>> = _latestWaterParams

    // Urgent banner task LiveData
    val mostUrgentFilter: LiveData<FilterEntity?> = filterDao.getMostUrgentFilterLiveData()

    // Offline / Sync status LiveData
    private val _isOffline = MutableLiveData(false)
    val isOffline: LiveData<Boolean> = _isOffline

    init {
        populateInitialMockData()
    }

    private fun populateInitialMockData() {
        _villageSummary.postValue(
            VillageSummary(
                villageName = "Hosahalli Gram Panchayat",
                overallHealthScore = 88,
                scoreDelta = 4,
                workingFiltersCount = 14,
                totalFiltersCount = 16,
                safeWaterLitres = 42500,
                avgDowntimeHours = 1.8f,
                unsafeTestsThisWeek = 0,
                sumpDeviceStatus = SumpDeviceStatus.SCHEDULED
            )
        )

        _upcomingMaintenance.postValue(
            listOf(
                MaintenanceItem("Sediment Cartridge", 4, 90, true),
                MaintenanceItem("UV Lamp", 18, 365, false),
                MaintenanceItem("RO Membrane", 42, 730, false)
            )
        )

        _impactStats.postValue(
            listOf(
                ImpactStatItem(R.string.stat_working_filters, "14 / 16", R.drawable.ic_check, true),
                ImpactStatItem(R.string.stat_safe_water, "42,500 L", R.drawable.ic_water_drop, true),
                ImpactStatItem(R.string.stat_avg_downtime, "1.8 hrs", R.drawable.ic_report_fault, true),
                ImpactStatItem(R.string.stat_unsafe_readings, "0 this week", R.drawable.ic_test_strip, true)
            )
        )

        // Evaluate sample water tests through rule engine
        _latestWaterParams.postValue(
            listOf(
                MaintenanceRuleEngine.evaluateTds(142f),
                MaintenanceRuleEngine.evaluatePh(7.2f),
                MaintenanceRuleEngine.evaluateChlorine(0.4f),
                MaintenanceRuleEngine.evaluateNitrate(12f)
            )
        )
    }

    suspend fun refreshData() = withContext(Dispatchers.IO) {
        // In real execution, calls Room & Retrofit.
        // Falls back seamlessly to offline cache if network fails.
        try {
            // Placeholder: Check remote backend health
            apiService.checkHealth()
            _isOffline.postValue(false)
        } catch (e: Exception) {
            _isOffline.postValue(true)
        }
    }

    suspend fun seedRoomDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val existing = filterDao.getAllFilters()
        if (existing.isEmpty()) {
            filterDao.insertFilters(
                listOf(
                    FilterEntity(
                        id = "F-01",
                        locationName = "Anganwadi RO-2",
                        filterType = "RO",
                        healthScore = 42,
                        cartridgeDaysLeft = 2,
                        uvLampDaysLeft = 14,
                        membraneDaysLeft = 1,
                        lastCheckTimestamp = System.currentTimeMillis() - 86400000L,
                        isUrgent = true,
                        urgentIssue = "Anganwadi RO-2 membrane blocked. Flow rate < 20%."
                    ),
                    FilterEntity(
                        id = "F-02",
                        locationName = "Govt High School",
                        filterType = "RO + UV",
                        healthScore = 92,
                        cartridgeDaysLeft = 45,
                        uvLampDaysLeft = 120,
                        membraneDaysLeft = 210,
                        lastCheckTimestamp = System.currentTimeMillis(),
                        isUrgent = false,
                        urgentIssue = null
                    )
                )
            )

            waterTestDao.insertReading(
                WaterTestReadingEntity(
                    id = "TEST-101",
                    filterId = "F-02",
                    timestamp = System.currentTimeMillis(),
                    location = "Primary Health Centre",
                    tdsPpm = 142f,
                    ph = 7.2f,
                    chlorineMgL = 0.4f,
                    nitrateMgL = 12f,
                    isSafe = true,
                    syncedWithBackend = true
                )
            )
        }
    }
}
