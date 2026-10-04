package org.filtersaathi.app.domain.model

data class VillageSummary(
    val villageName: String,
    val overallHealthScore: Int,
    val scoreDelta: Int,
    val workingFiltersCount: Int,
    val totalFiltersCount: Int,
    val safeWaterLitres: Long,
    val avgDowntimeHours: Float,
    val unsafeTestsThisWeek: Int,
    val sumpDeviceStatus: SumpDeviceStatus
)

enum class SumpDeviceStatus {
    NOT_REQUESTED,
    REQUESTED,
    SCHEDULED,
    INSTALLED
}

data class MaintenanceItem(
    val componentName: String,
    val daysRemaining: Int,
    val totalLifeDays: Int,
    val isUrgent: Boolean
) {
    val progressPercent: Int
        get() = ((daysRemaining.toFloat() / totalLifeDays.toFloat()) * 100).toInt().coerceIn(0, 100)
}

data class ImpactStatItem(
    val titleResId: Int,
    val valueString: String,
    val iconResId: Int,
    val isPositive: Boolean
)

enum class WaterQualityStatus {
    SAFE,
    WATCH,
    UNSAFE
}

data class WaterQualityParameter(
    val name: String,
    val valueWithUnit: String,
    val status: WaterQualityStatus
)
