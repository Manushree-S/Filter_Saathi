package org.filtersaathi.app.notifications

/**
 * Service reminder status evaluation for a water filter.
 */
enum class MaintenanceStatus {
    OK,
    SOON,
    URGENT
}

object MaintenanceReminderRule {

    /**
     * Evaluates maintenance status based on the number of days remaining until service.
     * - Urgent: under 7 days (or overdue <= 0)
     * - Soon: under 30 days (and >= 7 days)
     * - OK: 30 days or more
     */
    @JvmStatic
    fun evaluateStatus(daysRemaining: Int): MaintenanceStatus {
        return when {
            daysRemaining < MaintenanceThresholds.THRESHOLD_URGENT_DAYS -> MaintenanceStatus.URGENT
            daysRemaining < MaintenanceThresholds.THRESHOLD_SOON_DAYS -> MaintenanceStatus.SOON
            else -> MaintenanceStatus.OK
        }
    }

    /**
     * Checks if the given 24-hour hourOfDay (0-23) falls within quiet hours (21:00 to 07:00).
     */
    @JvmStatic
    fun isQuietHour(hourOfDay: Int): Boolean {
        // Quiet hours: 21:00 (inclusive) to 07:00 (exclusive)
        return hourOfDay >= MaintenanceThresholds.QUIET_HOUR_START || hourOfDay < MaintenanceThresholds.QUIET_HOUR_END
    }
}
