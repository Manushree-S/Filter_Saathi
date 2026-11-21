package org.filtersaathi.app.domain.engine

import org.filtersaathi.app.domain.model.FilterCareStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object FilterCareRuleEngine {

    // Thresholds (7 days for Urgent, 30 days for Soon) are placeholders for the department to set.
    const val URGENT_THRESHOLD_DAYS = 7
    const val SOON_THRESHOLD_DAYS = 30

    private const val DATE_FORMAT = "yyyy-MM-dd"

    fun calculateDaysSince(lastServicedDate: String, todayDateStr: String? = null): Int {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            val lastDate = sdf.parse(lastServicedDate) ?: return 0
            val todayDate = if (todayDateStr != null) sdf.parse(todayDateStr) else Date()
            val diffMs = (todayDate?.time ?: System.currentTimeMillis()) - lastDate.time
            TimeUnit.MILLISECONDS.toDays(diffMs).toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun calculateDaysLeft(serviceIntervalDays: Int, lastServicedDate: String, todayDateStr: String? = null): Int {
        val daysSince = calculateDaysSince(lastServicedDate, todayDateStr)
        return serviceIntervalDays - daysSince
    }

    fun evaluateStatus(daysLeft: Int): FilterCareStatus {
        return when {
            daysLeft <= URGENT_THRESHOLD_DAYS -> FilterCareStatus.URGENT // Overdue or <= 7 days
            daysLeft <= SOON_THRESHOLD_DAYS -> FilterCareStatus.SOON    // <= 30 days
            else -> FilterCareStatus.OK                                 // > 30 days
        }
    }

    fun getNextServiceDate(lastServicedDate: String, serviceIntervalDays: Int): String {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            val lastDate = sdf.parse(lastServicedDate) ?: return lastServicedDate
            val cal = Calendar.getInstance()
            cal.time = lastDate
            cal.add(Calendar.DAY_OF_YEAR, serviceIntervalDays)
            sdf.format(cal.time)
        } catch (e: Exception) {
            lastServicedDate
        }
    }

    fun getTodayString(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
        return sdf.format(Date())
    }
}
