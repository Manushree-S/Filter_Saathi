package org.filtersaathi.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.filtersaathi.app.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Deterministic in-memory fake repository for FilterSaathi Phase 1 Frontend.
 * Follows the user specification exactly.
 */
@Singleton
class FakeRepository @Inject constructor() {

    // --- Current User State (Whoever registers) ---
    private val _currentUser = MutableLiveData(
        UserProfile(
            isLoggedIn = true,
            name = "Ravi Kumar",
            phone = "+91 94481 23456"
        )
    )
    val currentUser: LiveData<UserProfile> = _currentUser

    // --- Sample Installer (Ramesh Gowda, 8 years, 120+ installations) ---
    val sampleInstaller = Installer(
        name = "Ramesh Gowda",
        phone = "+91 98765 43210",
        experienceYears = 8,
        installationsDone = "120+ installations"
    )

    // --- Offline Mode Simulation State ---
    private val _isOfflineDemo = MutableLiveData(false)
    val isOfflineDemo: LiveData<Boolean> = _isOfflineDemo

    // --- Top Banner Events (Booked, Saved offline, Synced) ---
    private val _topBannerEvent = MutableLiveData<BannerEvent?>()
    val topBannerEvent: LiveData<BannerEvent?> = _topBannerEvent

    // --- Current Booking & Tracking State ---
    private val _currentBooking = MutableLiveData<BookingRequest>(
        BookingRequest(
            requestNumber = "FS-2026-1042",
            district = "Tumakuru",
            taluk = "Gubbi",
            village = "Chikkanahalli",
            sumpLocation = "Primary Health Centre, Main Sump",
            pinCode = "572216",
            currentStage = 3, // Starts at stage 3 so installer card is visible in demo
            installer = sampleInstaller,
            rating = 0
        )
    )
    val currentBooking: LiveData<BookingRequest?> = _currentBooking

    // --- Notifications State ---
    private val _notifications = MutableLiveData<List<AppNotification>>(
        listOf(
            AppNotification(
                id = "NOTIF-1",
                titleKey = "ui_stg3",
                description = "Installer assigned: Ramesh Gowda (+91 98765 43210)",
                isRead = false
            ),
            AppNotification(
                id = "NOTIF-2",
                titleKey = "ui_a1",
                description = "Sample alert: Nitrate above limit (62 mg/L) in Doddabele",
                isRead = false
            )
        )
    )
    val notifications: LiveData<List<AppNotification>> = _notifications

    // --- Problem Reports Storage ---
    private val _problemReports = MutableLiveData<List<ProblemReport>>(emptyList())
    val problemReports: LiveData<List<ProblemReport>> = _problemReports

    // --- Fixed Test Strip Result (Phase 1 Simulation) ---
    val fixedTestStripResult = TestStripResult(
        tdsPpm = 312,
        ph = 7.2,
        chlorineMgL = 0.1,
        chlorineStatus = "Low",
        nitrateMgL = 62,
        nitrateStatus = "Unsafe",
        warningTextKey = "ui_scan_warn"
    )

    // --- Auth Actions ---
    fun registerOrLogin(name: String, phone: String) {
        _currentUser.value = UserProfile(
            isLoggedIn = true,
            name = name.trim().ifEmpty { "Guest User" },
            phone = phone.trim()
        )
    }

    fun logout() {
        _currentUser.value = UserProfile(
            isLoggedIn = false,
            name = "",
            phone = ""
        )
    }

    // --- Offline Simulation Toggle ---
    fun toggleOfflineDemo(): Boolean {
        val nextState = !(_isOfflineDemo.value ?: false)
        _isOfflineDemo.value = nextState
        val reqNo = _currentBooking.value?.requestNumber ?: "FS-2026-1042"
        if (!nextState) {
            // Reconnected online -> show Synced banner
            _topBannerEvent.value = BannerEvent(BannerType.SYNCED, reqNo)
        }
        return nextState
    }

    fun dismissTopBanner() {
        _topBannerEvent.value = null
    }

    // --- Booking Actions ---
    fun bookInstallation(
        district: String,
        taluk: String,
        village: String,
        sumpLocation: String,
        pinCode: String
    ): BookingRequest {
        val randomNum = Random.nextInt(1000, 9999)
        val reqNumber = "FS-2026-$randomNum"
        val newBooking = BookingRequest(
            requestNumber = reqNumber,
            district = district,
            taluk = taluk,
            village = village,
            sumpLocation = sumpLocation,
            pinCode = pinCode,
            currentStage = 1,
            installer = sampleInstaller
        )
        _currentBooking.value = newBooking

        if (_isOfflineDemo.value == true) {
            _topBannerEvent.value = BannerEvent(BannerType.SAVED_OFFLINE, reqNumber)
        } else {
            _topBannerEvent.value = BannerEvent(BannerType.BOOKED, reqNumber)
        }
        return newBooking
    }

    // --- Stage Progression for Demo ("Next stage (demo)") ---
    fun advanceStageDemo(): Int {
        val current = _currentBooking.value ?: return 1
        val nextStage = if (current.currentStage < 6) current.currentStage + 1 else 1
        _currentBooking.value = current.copy(currentStage = nextStage)
        return nextStage
    }

    // --- Reset for Demo ("New request") ---
    fun resetToNewRequestDemo() {
        val randomNum = Random.nextInt(1000, 9999)
        val reqNumber = "FS-2026-$randomNum"
        _currentBooking.value = BookingRequest(
            requestNumber = reqNumber,
            district = "Tumakuru",
            taluk = "Gubbi",
            village = "Chikkanahalli",
            sumpLocation = "Main Community Sump",
            pinCode = "572216",
            currentStage = 1,
            installer = sampleInstaller
        )
    }

    fun submitRating(stars: Int) {
        val current = _currentBooking.value ?: return
        _currentBooking.value = current.copy(rating = stars)
    }

    // --- Public Water Check Query ---
    fun getWaterCheck(district: String): VillageWaterData {
        val clean = district.trim()
        return when {
            clean.equals("Mysuru", ignoreCase = true) -> VillageWaterData(
                district = "Mysuru",
                village = "Hosahalli",
                taluk = "Mysore Taluk",
                overallStatus = ReadingStatus.SAFE,
                statusMessage = "Safe to drink",
                readings = listOf(
                    WaterReading("TDS", "142", "ppm", ReadingStatus.SAFE, "Ideal minerals"),
                    WaterReading("pH", "7.2", "", ReadingStatus.SAFE, "Balanced"),
                    WaterReading("Chlorine", "0.4", "mg/L", ReadingStatus.SAFE, "Adequate"),
                    WaterReading("Nitrate", "12", "mg/L", ReadingStatus.SAFE, "Well below limit")
                ),
                hasReadings = true
            )

            clean.equals("Tumakuru", ignoreCase = true) -> VillageWaterData(
                district = "Tumakuru",
                village = "Chikkanahalli",
                taluk = "Gubbi",
                overallStatus = ReadingStatus.SAFE,
                statusMessage = "Safe to drink",
                readings = listOf(
                    WaterReading("TDS", "210", "ppm", ReadingStatus.SAFE, "Normal"),
                    WaterReading("pH", "7.4", "", ReadingStatus.SAFE, "Balanced"),
                    WaterReading("Chlorine", "0.3", "mg/L", ReadingStatus.SAFE, "Normal"),
                    WaterReading("Nitrate", "18", "mg/L", ReadingStatus.SAFE, "Safe")
                ),
                hasReadings = true
            )

            clean.equals("Chikkaballapura", ignoreCase = true) -> VillageWaterData(
                district = "Chikkaballapura",
                village = "Doddabele",
                taluk = "Chikkaballapura",
                overallStatus = ReadingStatus.UNSAFE,
                statusMessage = "Do not drink · Nitrate above limit",
                readings = listOf(
                    WaterReading("TDS", "450", "ppm", ReadingStatus.WATCH, "High minerals"),
                    WaterReading("pH", "7.8", "", ReadingStatus.WATCH, "Slightly alkaline"),
                    WaterReading("Chlorine", "0.1", "mg/L", ReadingStatus.WATCH, "Low disinfection"),
                    WaterReading("Nitrate", "62", "mg/L", ReadingStatus.UNSAFE, "Above safe limit")
                ),
                hasReadings = true
            )

            clean.equals("Shivamogga", ignoreCase = true) -> VillageWaterData(
                district = "Shivamogga",
                village = "Bettahalli",
                taluk = "Thirthahalli",
                overallStatus = ReadingStatus.SAFE,
                statusMessage = "Safe to drink",
                readings = listOf(
                    WaterReading("TDS", "95", "ppm", ReadingStatus.SAFE, "Pure natural water"),
                    WaterReading("pH", "6.9", "", ReadingStatus.SAFE, "Neutral"),
                    WaterReading("Chlorine", "0.5", "mg/L", ReadingStatus.SAFE, "Optimal"),
                    WaterReading("Nitrate", "8", "mg/L", ReadingStatus.SAFE, "Low")
                ),
                hasReadings = true
            )

            clean.equals("Mandya", ignoreCase = true) -> VillageWaterData(
                district = "Mandya",
                village = "Kyathanahalli",
                taluk = "Pandavapura",
                overallStatus = ReadingStatus.SAFE,
                statusMessage = "Safe to drink",
                readings = listOf(
                    WaterReading("TDS", "180", "ppm", ReadingStatus.SAFE, "Normal"),
                    WaterReading("pH", "7.1", "", ReadingStatus.SAFE, "Balanced"),
                    WaterReading("Chlorine", "0.3", "mg/L", ReadingStatus.SAFE, "Normal"),
                    WaterReading("Nitrate", "15", "mg/L", ReadingStatus.SAFE, "Safe")
                ),
                hasReadings = true
            )

            else -> VillageWaterData(
                district = district,
                village = "",
                taluk = "",
                overallStatus = ReadingStatus.WATCH,
                statusMessage = "No readings yet for this district.",
                readings = emptyList(),
                hasReadings = false
            )
        }
    }

    // --- Report Problem Submission ---
    fun submitProblemReport(
        problemType: String,
        village: String,
        details: String,
        hasPhoto: Boolean
    ): ProblemReport {
        val randomNum = Random.nextInt(1000, 9999)
        val ticket = ProblemReport(
            ticketNumber = "FS-R-$randomNum",
            problemType = problemType,
            village = village,
            details = details,
            hasPhotoAttached = hasPhoto
        )
        val currentList = _problemReports.value ?: emptyList()
        _problemReports.value = listOf(ticket) + currentList
        return ticket
    }

    // --- Officer Overview Sample Data ---
    fun getOfficerOverviewData(): OfficerOverviewData {
        val colorSafe = 0xFFD4EBDD.toInt()   // light green
        val colorBlue = 0xFFCFE0F7.toInt()   // light blue
        val colorWatch = 0xFFFBE7C0.toInt()  // light amber
        val colorUnsafe = 0xFFFDECEC.toInt() // light red

        val fillOverrides = mapOf(
            "Mysuru" to colorSafe,
            "Tumakuru" to colorSafe,
            "Shivamogga" to colorSafe,
            "Mandya" to colorSafe,
            "Chikkaballapura" to colorUnsafe,
            "Bengaluru Urban" to colorBlue,
            "Belagavi" to colorBlue,
            "Dakshina Kannada" to colorBlue,
            "Udupi" to colorSafe,
            "Ballari" to colorWatch,
            "Kalaburagi" to colorWatch
        )

        return OfficerOverviewData(
            kpis = listOf(
                OfficerKpi("Active filters", "1,420", "+28 this month", true),
                OfficerKpi("Verified safe", "94.2%", "+1.4% improvement", true),
                OfficerKpi("Pending surveys", "18", "Scheduled this week", false),
                OfficerKpi("Offline sensors", "3", "Attention required", false)
            ),
            monthlyTrend = listOf(
                MonthBarData("Oct", 185),
                MonthBarData("Nov", 210),
                MonthBarData("Dec", 245),
                MonthBarData("Jan", 290),
                MonthBarData("Feb", 340),
                MonthBarData("Mar", 380)
            ),
            alerts = listOf(
                "Doddabele Anganwadi: Nitrate level high (62 mg/L)",
                "Chikkanahalli PHC: Sensor telemetry delayed"
            ),
            villageStatuses = listOf(
                VillageStatusRow("Doddabele", "Chikkaballapura", ReadingStatus.UNSAFE, "Nitrate 62 mg/L"),
                VillageStatusRow("Hosahalli", "Mysuru", ReadingStatus.SAFE, "TDS 142 ppm"),
                VillageStatusRow("Chikkanahalli", "Tumakuru", ReadingStatus.SAFE, "TDS 210 ppm"),
                VillageStatusRow("Bettahalli", "Shivamogga", ReadingStatus.SAFE, "TDS 95 ppm"),
                VillageStatusRow("Kyathanahalli", "Mandya", ReadingStatus.SAFE, "TDS 180 ppm")
            ),
            sumpsMonitoredCount = 48,
            sumpAverageLevelPercent = 76,
            districtFillOverrides = fillOverrides
        )
    }
}
