package org.filtersaathi.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.filtersaathi.app.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Deterministic in-memory fake repository for FilterSaathi Phase 1 Frontend.
 * Follows the specification strictly:
 * - No user-facing text in Kotlin (enums, numbers, and keys only).
 * - Starts signed out (currentUser is null).
 * - No booking at start (currentBooking is null).
 * - Stages 1 to 7 (7 = all done, advanceStageDemo stops at 7).
 * - Offline pendingSync flag.
 */
@Singleton
class FakeRepository @Inject constructor() {

    // --- Current User State (Starts signed out, no user) ---
    private val _currentUser = MutableLiveData<UserProfile?>(null)
    val currentUser: LiveData<UserProfile?> = _currentUser

    // --- Sample Installer (Keys & digits only) ---
    val sampleInstaller = Installer(
        nameKey = "ui_installer_name",
        phone = "+919876543210",
        expKey = "ui_installer_exp"
    )

    // --- Offline Mode Simulation State & Pending Sync Flag ---
    private val _isOfflineDemo = MutableLiveData(false)
    val isOfflineDemo: LiveData<Boolean> = _isOfflineDemo

    private var pendingSync = false

    // --- Top Banner Events ---
    private val _topBannerEvent = MutableLiveData<BannerEvent?>()
    val topBannerEvent: LiveData<BannerEvent?> = _topBannerEvent

    // --- Current Booking State (Starts null) ---
    private val _currentBooking = MutableLiveData<BookingRequest?>(null)
    val currentBooking: LiveData<BookingRequest?> = _currentBooking

    // --- Notifications State (3 notifications with keys, unread count = 3) ---
    private val _notifications = MutableLiveData<List<AppNotification>>(
        listOf(
            AppNotification("N1", "ui_nt1", "ui_tm1", false),
            AppNotification("N2", "ui_nt2", "ui_tm2", false),
            AppNotification("N3", "ui_nt3", "ui_tm3", false)
        )
    )
    val notifications: LiveData<List<AppNotification>> = _notifications

    private val _unreadNotificationCount = MutableLiveData(3)
    val unreadNotificationCount: LiveData<Int> = _unreadNotificationCount

    // --- Problem Reports Storage ---
    private val _problemReports = MutableLiveData<List<ProblemReport>>(emptyList())
    val problemReports: LiveData<List<ProblemReport>> = _problemReports

    // --- Fixed Test Strip Result (Phase 1 Simulation) ---
    val fixedTestStripResult = TestStripResult(
        tds = 312,
        tdsStatus = ReadingStatus.SAFE,
        ph = 7.2,
        phStatus = ReadingStatus.SAFE,
        chlorine = 0.1,
        chlorineStatus = ReadingStatus.WATCH,
        nitrate = 62,
        nitrateStatus = ReadingStatus.UNSAFE,
        warningKey = "ui_scan_warn"
    )

    // 31 Karnataka district keys matching ui_district_keys order
    private val districtKeys = listOf(
        "Bagalkot", "Ballari", "Belagavi",
        "Bengaluru_Rural".replace('_', ' '),
        "Bengaluru_South".replace('_', ' '),
        "Bengaluru_Urban".replace('_', ' '),
        "Bidar", "Chamarajanagar", "Chikkaballapura", "Chikkamagaluru", "Chitradurga",
        "Dakshina_Kannada".replace('_', ' '),
        "Davanagere", "Dharwad", "Gadag", "Hassan", "Haveri",
        "Kalaburagi", "Kodagu", "Kolar", "Koppal", "Mandya", "Mysuru", "Raichur",
        "Shivamogga", "Tumakuru", "Udupi",
        "Uttara_Kannada".replace('_', ' '),
        "Vijayanagara", "Vijayapura", "Yadgir"
    )

    // --- Auth Actions ---
    fun registerOrLogin(name: String, phone: String) {
        _currentUser.value = UserProfile(
            isLoggedIn = true,
            name = name.trim(),
            phone = phone.trim()
        )
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- Offline Simulation Toggle ---
    fun toggleOfflineDemo(): Boolean {
        val nextState = !(_isOfflineDemo.value ?: false)
        _isOfflineDemo.value = nextState
        if (!nextState && pendingSync) {
            val reqNo = _currentBooking.value?.requestNumber ?: ""
            _topBannerEvent.value = BannerEvent(BannerType.SYNCED, reqNo)
            pendingSync = false
        }
        return nextState
    }

    fun dismissTopBanner() {
        _topBannerEvent.value = null
    }

    fun markNotificationsRead() {
        _unreadNotificationCount.value = 0
        _notifications.value = _notifications.value?.map { it.copy(isRead = true) }
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
            pendingSync = true
            _topBannerEvent.value = BannerEvent(BannerType.SAVED_OFFLINE, reqNumber)
        } else {
            _topBannerEvent.value = BannerEvent(BannerType.BOOKED, reqNumber)
        }
        return newBooking
    }

    // --- Stage Progression for Demo (Next stage demo) ---
    // Stages 1 to 6, plus 7 = all done. advanceStageDemo() stops at 7 (no wrap).
    fun advanceStageDemo(): Int {
        val current = _currentBooking.value ?: return 1
        val nextStage = if (current.currentStage < 7) current.currentStage + 1 else 7
        _currentBooking.value = current.copy(currentStage = nextStage)
        return nextStage
    }

    // --- Reset for Demo (New request) ---
    // Clears the booking and returns to Book
    fun resetToNewRequestDemo() {
        _currentBooking.value = null
    }

    fun submitRating(stars: Int) {
        val current = _currentBooking.value ?: return
        _currentBooking.value = current.copy(rating = stars)
    }

    // --- Status Calculation from Score ---
    private fun scoreToStatus(score: Int): ReadingStatus = when {
        score >= 80 -> ReadingStatus.SAFE
        score >= 50 -> ReadingStatus.WATCH
        else -> ReadingStatus.UNSAFE
    }

    // --- Public Water Check Query ---
    fun getWaterCheck(district: String): VillageWaterData {
        val clean = district.trim()
        val mysuruReadings = listOf(
            WaterReading(ReadingType.TDS, 312.0, ReadingStatus.SAFE),
            WaterReading(ReadingType.PH, 7.2, ReadingStatus.SAFE),
            WaterReading(ReadingType.CHLORINE, 0.5, ReadingStatus.SAFE),
            WaterReading(ReadingType.NITRATE, 12.0, ReadingStatus.SAFE)
        )

        return when {
            clean.equals("Mysuru", ignoreCase = true) -> VillageWaterData(
                district = "Mysuru",
                village = "Hosahalli",
                score = 91,
                status = scoreToStatus(91),
                testedDateKey = "ui_pub_date",
                readings = mysuruReadings,
                hasReadings = true
            )

            clean.equals("Tumakuru", ignoreCase = true) -> VillageWaterData(
                district = "Tumakuru",
                village = "Chikkanahalli",
                score = 68,
                status = scoreToStatus(68),
                testedDateKey = "ui_pub_date",
                readings = listOf(
                    WaterReading(ReadingType.TDS, 420.0, ReadingStatus.WATCH),
                    WaterReading(ReadingType.PH, 7.4, ReadingStatus.SAFE),
                    WaterReading(ReadingType.CHLORINE, 0.1, ReadingStatus.WATCH),
                    WaterReading(ReadingType.NITRATE, 18.0, ReadingStatus.SAFE)
                ),
                hasReadings = true
            )

            clean.equals("Chikkaballapura", ignoreCase = true) -> VillageWaterData(
                district = "Chikkaballapura",
                village = "Doddabele",
                score = 41,
                status = scoreToStatus(41),
                testedDateKey = "ui_pub_date",
                readings = listOf(
                    WaterReading(ReadingType.TDS, 480.0, ReadingStatus.WATCH),
                    WaterReading(ReadingType.PH, 7.6, ReadingStatus.SAFE),
                    WaterReading(ReadingType.CHLORINE, 0.3, ReadingStatus.SAFE),
                    WaterReading(ReadingType.NITRATE, 62.0, ReadingStatus.UNSAFE)
                ),
                hasReadings = true
            )

            clean.equals("Shivamogga", ignoreCase = true) -> VillageWaterData(
                district = "Shivamogga",
                village = "Bettahalli",
                score = 86,
                status = scoreToStatus(86),
                testedDateKey = "ui_pub_date",
                readings = mysuruReadings,
                hasReadings = true
            )

            clean.equals("Mandya", ignoreCase = true) -> VillageWaterData(
                district = "Mandya",
                village = "Kyathanahalli",
                score = 88,
                status = scoreToStatus(88),
                testedDateKey = "ui_pub_date",
                readings = mysuruReadings,
                hasReadings = true
            )

            else -> VillageWaterData(
                district = district,
                village = "",
                score = 0,
                status = ReadingStatus.WATCH,
                testedDateKey = "ui_pub_date",
                readings = emptyList(),
                hasReadings = false
            )
        }
    }

    // --- Report Problem Submission ---
    fun submitProblemReport(
        problemType: ProblemType,
        village: String,
        details: String,
        hasPhoto: Boolean
    ): ProblemReport {
        val randomNum = Random.nextInt(1000, 9999)
        val ticketNum = "FS-R-$randomNum"
        val ticket = ProblemReport(
            ticketNumber = ticketNum,
            problemType = problemType,
            village = village,
            details = details,
            hasPhoto = hasPhoto
        )
        val currentList = _problemReports.value ?: emptyList()
        _problemReports.value = listOf(ticket) + currentList
        _topBannerEvent.value = BannerEvent(BannerType.REPORT_SENT, ticketNum)
        return ticket
    }

    // --- Officer Overview Sample Data ---
    fun getOfficerOverviewData(): OfficerOverviewData {
        val coverage = mutableMapOf<String, CoverageStatus>()
        districtKeys.forEachIndexed { i, key ->
            val status = when ((i * 7 + 3) % 4) {
                0 -> CoverageStatus.INSTALLED
                1 -> CoverageStatus.SCHEDULED
                2 -> CoverageStatus.REQUESTED
                else -> CoverageStatus.NO_REQUEST
            }
            coverage[key] = status
        }

        return OfficerOverviewData(
            kpis = OfficerKpis(
                monitoredCount = 1248,
                workingPercent = 90.2,
                safeTestsPercent = 94.2,
                openAlertsCount = 37
            ),
            monthlyTrend = listOf(
                MonthBarItem("ui_m_may", 88),
                MonthBarItem("ui_m_jun", 90),
                MonthBarItem("ui_m_jul", 91),
                MonthBarItem("ui_m_aug", 92),
                MonthBarItem("ui_m_sep", 93),
                MonthBarItem("ui_m_oct", 94)
            ),
            alerts = listOf(
                OfficerAlert("ui_a1", "Doddabele", "ui_tag_unsafe", ReadingStatus.UNSAFE),
                OfficerAlert("ui_a2", "Chikkanahalli", "ui_tag_urgent", ReadingStatus.UNSAFE),
                OfficerAlert("ui_a3", "Hosahalli", "ui_tag_soon", ReadingStatus.SAFE)
            ),
            villageRows = listOf(
                OfficerVillageRow("Hosahalli", 14, 91, ReadingStatus.SAFE),
                OfficerVillageRow("Chikkanahalli", 9, 68, ReadingStatus.WATCH),
                OfficerVillageRow("Doddabele", 12, 41, ReadingStatus.UNSAFE),
                OfficerVillageRow("Bettahalli", 6, 86, ReadingStatus.SAFE),
                OfficerVillageRow("Kyathanahalli", 7, 88, ReadingStatus.SAFE)
            ),
            sumpStats = SumpStats(
                requestedCount = 42,
                scheduledCount = 18,
                installedCount = 96,
                totalCount = 156,
                tankLevelPercent = 78
            ),
            coverageMap = coverage
        )
    }
}
