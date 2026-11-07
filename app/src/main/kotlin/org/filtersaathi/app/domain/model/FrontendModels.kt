package org.filtersaathi.app.domain.model

/**
 * Clean domain models for FilterSaathi Phase 1 Frontend.
 * Enums, numbers, and string resource keys only.
 * No hardcoded user-facing English text.
 */

// --- 1. User & Auth (Starts signed out) ---
data class UserProfile(
    val isLoggedIn: Boolean = false,
    val name: String = "",
    val phone: String = ""
)

// --- 2. Installer (Resource keys & digits only) ---
data class Installer(
    val nameKey: String = "ui_installer_name",
    val phone: String = "+919876543210",
    val expKey: String = "ui_installer_exp"
)

// --- 3. Readings & Statuses ---
enum class ReadingStatus {
    SAFE,
    WATCH,
    UNSAFE
}

enum class ReadingType(val key: String, val unitKey: String) {
    TDS("ui_r_tds", "ui_unit_ppm"),
    PH("ui_r_ph", ""),
    CHLORINE("ui_r_cl", "ui_unit_mgl"),
    NITRATE("ui_r_ni", "ui_unit_mgl")
}

data class WaterReading(
    val type: ReadingType,
    val value: Double,
    val status: ReadingStatus
)

// --- 4. District & Village Water Check ---
data class VillageWaterData(
    val district: String,
    val village: String,
    val score: Int,
    val status: ReadingStatus,
    val testedDateKey: String = "ui_pub_date",
    val readings: List<WaterReading>,
    val hasReadings: Boolean = true
)

// --- 5. Installation Booking & 6/7-Stage Tracking ---
data class BookingRequest(
    val requestNumber: String,
    val district: String,
    val taluk: String,
    val village: String,
    val sumpLocation: String,
    val pinCode: String,
    val currentStage: Int = 1, // 1..6, 7 = all done
    val installer: Installer = Installer(),
    val rating: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    val stageKey: String
        get() = when (currentStage) {
            1 -> "ui_stg1"
            2 -> "ui_stg2"
            3 -> "ui_stg3"
            4 -> "ui_stg4"
            5 -> "ui_stg5"
            6 -> "ui_stg6"
            else -> "ui_stg_done"
        }

    val showInstallerCard: Boolean
        get() = currentStage >= 3

    val showRatingAndCertificate: Boolean
        get() = currentStage >= 6
}

// --- 6. Problem Report ---
enum class ProblemType(val key: String) {
    BAD_TASTE_SMELL("ui_ty1"),
    FILTER_NOT_WORKING("ui_ty2"),
    LEAK("ui_ty3"),
    OTHER("ui_ty4")
}

data class ProblemReport(
    val ticketNumber: String,
    val problemType: ProblemType,
    val village: String,
    val details: String = "",
    val hasPhoto: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

// --- 7. Top Banners ---
enum class BannerType {
    BOOKED,
    SAVED_OFFLINE,
    SYNCED,
    REPORT_SENT
}

data class BannerEvent(
    val type: BannerType,
    val referenceNumber: String
)

// --- 8. Test Strip Result ---
data class TestStripResult(
    val tds: Int = 312,
    val tdsStatus: ReadingStatus = ReadingStatus.SAFE,
    val ph: Double = 7.2,
    val phStatus: ReadingStatus = ReadingStatus.SAFE,
    val chlorine: Double = 0.1,
    val chlorineStatus: ReadingStatus = ReadingStatus.WATCH,
    val nitrate: Int = 62,
    val nitrateStatus: ReadingStatus = ReadingStatus.UNSAFE,
    val warningKey: String = "ui_scan_warn"
)

// --- 9. Notifications ---
data class AppNotification(
    val id: String,
    val titleKey: String,
    val timeKey: String,
    val isRead: Boolean = false
)

// --- 10. Officer Overview ---
enum class CoverageStatus(val stringKey: String, val colorHex: Int) {
    INSTALLED("ui_s_ins", 0xFF18803A.toInt()),
    SCHEDULED("ui_s_sch", 0xFF1D4896.toInt()),
    REQUESTED("ui_s_req", 0xFFD99A0B.toInt()),
    NO_REQUEST("ui_none", 0xFFE3E9F4.toInt())
}

data class OfficerKpis(
    val monitoredCount: Int = 1248,
    val workingPercent: Double = 90.2,
    val safeTestsPercent: Double = 94.2,
    val openAlertsCount: Int = 37
)

data class MonthBarItem(
    val monthKey: String,
    val percent: Int
)

data class OfficerAlert(
    val titleKey: String,
    val village: String,
    val tagKey: String,
    val status: ReadingStatus
)

data class OfficerVillageRow(
    val village: String,
    val filtersCount: Int,
    val score: Int,
    val status: ReadingStatus
)

data class SumpStats(
    val requestedCount: Int = 42,
    val scheduledCount: Int = 18,
    val installedCount: Int = 96,
    val totalCount: Int = 156,
    val tankLevelPercent: Int = 78
)

data class OfficerOverviewData(
    val kpis: OfficerKpis,
    val monthlyTrend: List<MonthBarItem>,
    val alerts: List<OfficerAlert>,
    val villageRows: List<OfficerVillageRow>,
    val sumpStats: SumpStats,
    val coverageMap: Map<String, CoverageStatus>
)
