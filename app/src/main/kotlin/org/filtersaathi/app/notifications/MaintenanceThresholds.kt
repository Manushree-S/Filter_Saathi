package org.filtersaathi.app.notifications

/**
 * Maintenance and reminder thresholds for FilterSaathi.
 *
 * NOTE: These threshold values and quiet-hour intervals are placeholders for the
 * Rural Drinking Water and Sanitation Department (RDWSD) / Panchayat administration
 * to configure and authorise based on official maintenance protocols and operating manuals.
 */
object MaintenanceThresholds {
    /** Filters with service due in less than this many days are marked Soon. */
    const val THRESHOLD_SOON_DAYS: Int = 30

    /** Filters with service due in less than this many days are marked Urgent. */
    const val THRESHOLD_URGENT_DAYS: Int = 7

    /** Start of quiet hours (21:00 / 9:00 PM). No notifications should be dispatched. */
    const val QUIET_HOUR_START: Int = 21

    /** End of quiet hours (07:00 / 7:00 AM). Notifications resume after this hour. */
    const val QUIET_HOUR_END: Int = 7
}
