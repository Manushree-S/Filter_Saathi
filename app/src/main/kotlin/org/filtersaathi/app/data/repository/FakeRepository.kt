package org.filtersaathi.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.filtersaathi.app.domain.engine.FilterCareRuleEngine
import org.filtersaathi.app.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Deterministic in-memory fake repository for FilterSaathi Phase 1 Frontend.
 * Follows the specification strictly:
 * - Starts signed out (currentUser is null).
 * - Stores ownerPhone for bookings and problem reports.
 * - Shows only items belonging to the current signed-in user.
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

    // --- Selected District from Map (pre-fill for Book) ---
    private val _selectedDistrict = MutableLiveData<String?>(null)
    val selectedDistrict: LiveData<String?> = _selectedDistrict

    fun selectDistrict(district: String?) {
        _selectedDistrict.value = district
    }

    // --- Notifications State ---
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

    // --- Demo Seed User Phone ---
    private val DEMO_PHONE = "+919876543210"

    // --- Sample Bookings List ---
    private val _allBookings = MutableLiveData<List<BookingRequest>>(
        listOf(
            BookingRequest(
                requestNumber = "FS-2026-1024",
                district = "Bengaluru Urban",
                taluk = "South",
                village = "Gram Panchayat Sump",
                sumpLocation = "Main Tank Area, MG Road",
                pinCode = "560001",
                currentStage = 6, // Stage 6 (Active / Device Installed)
                ownerPhone = DEMO_PHONE,
                installer = sampleInstaller,
                timestamp = System.currentTimeMillis() - 86400000L * 5
            ),
            BookingRequest(
                requestNumber = "FS-2026-1088",
                district = "Mysuru",
                taluk = "Nanjangud",
                village = "School Sump",
                sumpLocation = "School Playground Sump",
                pinCode = "570001",
                currentStage = 2, // Stage 2 (Panchayat Verified)
                ownerPhone = DEMO_PHONE,
                installer = sampleInstaller,
                timestamp = System.currentTimeMillis() - 86400000L * 2
            )
        )
    )

    // --- Sample Problem Reports List ---
    private val _problemReports = MutableLiveData<List<ProblemReport>>(
        listOf(
            ProblemReport(
                ticketNumber = "FS-R-3012",
                problemType = ProblemType.BAD_TASTE_SMELL,
                village = "Gram Panchayat Sump",
                details = "Slight taste alteration noticed after rain.",
                hasPhoto = false,
                ownerPhone = DEMO_PHONE,
                timestamp = System.currentTimeMillis() - 86400000L * 1
            )
        )
    )
    val problemReports: LiveData<List<ProblemReport>> = _problemReports

    // --- Filter Devices Storage (for Stage 6 requests) ---
    private val initialDevices = listOf(
        FilterDevice(
            id = "FS-2026-1024",
            nameKey = "ui_dev_school_ro",
            nameFallback = "School sump: RO plant",
            typeKey = "ui_type_ro",
            installDate = "2026-03-15",
            items = listOf(
                FilterItemLife("ui_item_cartridge", 90, "2026-10-20"),
                FilterItemLife("ui_item_uv_lamp", 180, "2026-05-10"),
                FilterItemLife("ui_item_membrane", 365, "2026-01-10")
            ),
            history = listOf(
                ServiceHistoryEntry("2026-05-10", "ui_item_uv_lamp"),
                ServiceHistoryEntry("2026-01-10", "ui_item_membrane")
            )
        )
    )

    private val _filterDevices = MutableLiveData<List<FilterDevice>>(initialDevices)

    fun getFilterDeviceById(deviceId: String): FilterDevice? {
        return _filterDevices.value?.firstOrNull { it.id == deviceId }
    }

    fun markItemServiced(deviceId: String, itemKey: String) {
        val currentList = _filterDevices.value ?: return
        val todayStr = FilterCareRuleEngine.getTodayString()

        val updatedList = currentList.map { device ->
            if (device.id == deviceId) {
                val updatedItems = device.items.map { item ->
                    if (item.itemKey == itemKey) {
                        item.copy(lastServicedDate = todayStr)
                    } else {
                        item
                    }
                }
                val updatedHistory = listOf(ServiceHistoryEntry(todayStr, itemKey)) + device.history
                device.copy(items = updatedItems, history = updatedHistory)
            } else {
                device
            }
        }
        _filterDevices.value = updatedList
    }

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

    // 31 Karnataka district keys
    private val districtKeys = listOf(
        "Bagalkot", "Ballari", "Belagavi", "Bengaluru Rural", "Bengaluru South",
        "Bengaluru Urban", "Bidar", "Chamarajanagar", "Chikkaballapura", "Chikkamagaluru",
        "Chitradurga", "Dakshina Kannada", "Davanagere", "Dharwad", "Gadag",
        "Hassan", "Haveri", "Kalaburagi", "Kodagu", "Kolar",
        "Koppal", "Mandya", "Mysuru", "Raichur", "Shivamogga",
        "Tumakuru", "Udupi", "Uttara Kannada", "Vijayanagara", "Vijayapura", "Yadgir"
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
        pinCode: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): BookingRequest {
        val randomNum = Random.nextInt(1000, 9999)
        val reqNumber = "FS-2026-$randomNum"
        val owner = _currentUser.value?.phone

        val newBooking = BookingRequest(
            requestNumber = reqNumber,
            district = district,
            taluk = taluk,
            village = village,
            sumpLocation = sumpLocation,
            pinCode = pinCode,
            latitude = latitude,
            longitude = longitude,
            currentStage = 1,
            ownerPhone = owner,
            installer = sampleInstaller
        )
        _currentBooking.value = newBooking

        val currentList = _allBookings.value ?: emptyList()
        _allBookings.value = listOf(newBooking) + currentList

        if (_isOfflineDemo.value == true) {
            pendingSync = true
            _topBannerEvent.value = BannerEvent(BannerType.SAVED_OFFLINE, reqNumber)
        } else {
            _topBannerEvent.value = BannerEvent(BannerType.BOOKED, reqNumber)
        }
        return newBooking
    }

    fun submitProblemReport(
        problemType: ProblemType,
        village: String,
        details: String,
        hasPhoto: Boolean
    ): ProblemReport {
        val randomNum = Random.nextInt(1000, 9999)
        val ticketNo = "FS-R-$randomNum"
        val owner = _currentUser.value?.phone

        val newReport = ProblemReport(
            ticketNumber = ticketNo,
            problemType = problemType,
            village = village,
            details = details,
            hasPhoto = hasPhoto,
            ownerPhone = owner
        )

        val currentList = _problemReports.value ?: emptyList()
        _problemReports.value = listOf(newReport) + currentList
        _topBannerEvent.value = BannerEvent(BannerType.REPORT_SENT, ticketNo)
        return newReport
    }

    fun getOfficerOverviewData(): OfficerOverviewData {
        val kpis = OfficerKpis()
        val monthlyTrend = listOf(
            MonthBarItem("Jun", 82),
            MonthBarItem("Jul", 88),
            MonthBarItem("Aug", 85),
            MonthBarItem("Sep", 91),
            MonthBarItem("Oct", 94)
        )
        val alerts = listOf(
            OfficerAlert("ui_scan_warn", "Gram Panchayat Sump", "Nitrate", ReadingStatus.UNSAFE)
        )
        val villageRows = listOf(
            OfficerVillageRow("Gram Panchayat Sump", 12, 94, ReadingStatus.SAFE),
            OfficerVillageRow("School Sump", 8, 88, ReadingStatus.SAFE),
            OfficerVillageRow("Anganwadi Sump", 6, 72, ReadingStatus.WATCH)
        )
        val sumpStats = SumpStats()
        val coverageMap = districtKeys.associateWith { CoverageStatus.INSTALLED }

        return OfficerOverviewData(
            kpis = kpis,
            monthlyTrend = monthlyTrend,
            alerts = alerts,
            villageRows = villageRows,
            sumpStats = sumpStats,
            coverageMap = coverageMap
        )
    }

    // --- Ownership Filter for My Requests ---
    fun getUserRequestsAndReports(): List<RequestOrReportItem> {
        val userPhone = _currentUser.value?.phone ?: return emptyList()

        val userBookings = (_allBookings.value ?: emptyList())
            .filter { it.ownerPhone == userPhone }
            .map { RequestOrReportItem.Booking(it) }

        val userReports = (_problemReports.value ?: emptyList())
            .filter { it.ownerPhone == userPhone }
            .map { RequestOrReportItem.Report(it) }

        return (userBookings + userReports).sortedByDescending { it.timestamp }
    }

    fun getBookingByRequestNumber(reqNumber: String): BookingRequest? {
        return (_allBookings.value ?: emptyList()).firstOrNull { it.requestNumber == reqNumber }
            ?: _currentBooking.value?.takeIf { it.requestNumber == reqNumber }
    }

    fun getReportByTicketNumber(ticketNumber: String): ProblemReport? {
        return (_problemReports.value ?: emptyList()).firstOrNull { it.ticketNumber == ticketNumber }
    }

    // --- Stage Progression for Demo ---
    fun advanceStageDemo(): Int {
        val current = _currentBooking.value ?: return 1
        val nextStage = if (current.currentStage < 7) current.currentStage + 1 else 7
        val updated = current.copy(currentStage = nextStage)
        _currentBooking.value = updated

        // Update in _allBookings as well
        val list = _allBookings.value ?: emptyList()
        _allBookings.value = list.map { if (it.requestNumber == current.requestNumber) updated else it }

        // If reached Stage 6, create a device record if not already present
        if (nextStage >= 6) {
            val existing = getFilterDeviceById(current.requestNumber)
            if (existing == null) {
                val todayStr = FilterCareRuleEngine.getTodayString()
                val newDevice = FilterDevice(
                    id = current.requestNumber,
                    nameKey = "ui_dev_school_ro",
                    nameFallback = "${current.village}: RO Plant",
                    typeKey = "ui_type_ro",
                    installDate = todayStr,
                    items = listOf(
                        FilterItemLife("ui_item_cartridge", 90, todayStr),
                        FilterItemLife("ui_item_uv_lamp", 180, todayStr),
                        FilterItemLife("ui_item_membrane", 365, todayStr)
                    ),
                    history = listOf(ServiceHistoryEntry(todayStr, "ui_item_cartridge"))
                )
                val devices = _filterDevices.value ?: emptyList()
                _filterDevices.value = listOf(newDevice) + devices
            }
        }

        return nextStage
    }

    fun resetToNewRequestDemo() {
        _currentBooking.value = null
    }

    fun submitRating(stars: Int) {
        val current = _currentBooking.value ?: return
        _currentBooking.value = current.copy(rating = stars)
    }
}
