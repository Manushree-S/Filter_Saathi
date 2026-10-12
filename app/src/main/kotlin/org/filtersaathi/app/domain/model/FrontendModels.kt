package org.filtersaathi.app.domain.model

/**
 * Clean domain models for FilterSaathi Phase 1 Frontend.
 * Adheres strictly to the user specification and corrections.
 */

// --- 1. User & Auth ---
data class UserProfile(
    val isLoggedIn: Boolean = false,
    val name: String = "",
    val phone: String = ""
)

// --- 2. Installer ---
data class Installer(
    val name: String = "Ramesh Gowda",
    val phone: String = "+91 98765 43210",
    val experienceYears: Int = 8,
    val installationsDone: String = "120+ installations"
)

// --- 3. Water Readings (Strictly TDS, pH, Chlorine, Nitrate only) ---
enum class ReadingStatus {
    SAFE,
    WATCH,
    UNSAFE
}

data class WaterReading(
    val name: String,
    val value: String,
    val unit: String,
    val status: ReadingStatus,
    val note: String = ""
)

// --- 4. District & Village Water Check ---
data class VillageWaterData(
    val district: String,
    val village: String,
    val taluk: String,
    val overallStatus: ReadingStatus,
    val statusMessage: String,
    val readings: List<WaterReading>,
    val hasReadings: Boolean = true
)

// --- 5. Installation Booking & 6-Stage Tracking ---
data class BookingRequest(
    val requestNumber: String,
    val district: String,
    val taluk: String,
    val village: String,
    val sumpLocation: String,
    val pinCode: String,
    val currentStage: Int = 1, // 1: Request received, 2: Verified, 3: Installer assigned, 4: Site survey, 5: Installation, 6: Live monitoring
    val installer: Installer = Installer(),
    val rating: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    val stageNameResKey: String
        get() = when (currentStage) {
            1 -> "ui_stg1" // Request received
            2 -> "ui_stg2" // Verified
            3 -> "ui_stg3" // Installer assigned
            4 -> "ui_stg4" // Site survey
            5 -> "ui_stg5" // Installation
            else -> "ui_stg6" // Live monitoring
        }
}

// --- 6. Problem Report ---
data class ProblemReport(
    val ticketNumber: String,
    val problemType: String, // Bad taste or smell, Filter not working, Leak, Other
    val village: String,
    val details: String = "",
    val hasPhotoAttached: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

// --- 7. Test Strip Simulation Result ---
data class TestStripResult(
    val tdsPpm: Int = 312,
    val ph: Double = 7.2,
    val chlorineMgL: Double = 0.1,
    val chlorineStatus: String = "Low",
    val nitrateMgL: Int = 62,
    val nitrateStatus: String = "Unsafe",
    val warningTextKey: String = "ui_scan_warn",
    val timestamp: Long = System.currentTimeMillis()
)

// --- 8. Officer Overview Models ---
data class OfficerKpi(
    val title: String,
    val value: String,
    val subtitle: String,
    val isPositive: Boolean
)

data class MonthBarData(
    val monthName: String,
    val value: Int
)

data class VillageStatusRow(
    val village: String,
    val district: String,
    val status: ReadingStatus,
    val primaryReading: String
)

data class OfficerOverviewData(
    val kpis: List<OfficerKpi>,
    val monthlyTrend: List<MonthBarData>,
    val alerts: List<String>,
    val villageStatuses: List<VillageStatusRow>,
    val sumpsMonitoredCount: Int = 48,
    val sumpAverageLevelPercent: Int = 76,
    val districtFillOverrides: Map<String, Int> = emptyMap()
)

// --- 9. Notifications ---
data class AppNotification(
    val id: String,
    val titleKey: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// --- 10. Top Banner State ---
enum class BannerType {
    BOOKED,
    SAVED_OFFLINE,
    SYNCED
}

data class BannerEvent(
    val type: BannerType,
    val requestNumber: String
)
