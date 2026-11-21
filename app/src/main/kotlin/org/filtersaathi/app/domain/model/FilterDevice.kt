package org.filtersaathi.app.domain.model

import org.filtersaathi.app.domain.engine.FilterCareRuleEngine

data class ServiceHistoryEntry(
    val date: String,
    val itemKey: String
)

data class FilterItemLife(
    val itemKey: String,
    val serviceIntervalDays: Int,
    val lastServicedDate: String
) {
    fun getDaysLeft(todayDateStr: String? = null): Int {
        return FilterCareRuleEngine.calculateDaysLeft(serviceIntervalDays, lastServicedDate, todayDateStr)
    }

    fun getStatus(todayDateStr: String? = null): FilterCareStatus {
        return FilterCareRuleEngine.evaluateStatus(getDaysLeft(todayDateStr))
    }

    fun getNextServiceDate(): String {
        return FilterCareRuleEngine.getNextServiceDate(lastServicedDate, serviceIntervalDays)
    }
}

enum class FilterCareStatus(val priority: Int, val stringKey: String) {
    URGENT(3, "ui_status_urgent"),
    SOON(2, "ui_status_soon"),
    OK(1, "ui_status_ok")
}

data class FilterDevice(
    val id: String,
    val nameKey: String,
    val nameFallback: String,
    val typeKey: String,
    val installDate: String,
    val items: List<FilterItemLife>,
    val history: List<ServiceHistoryEntry> = emptyList()
) {
    fun getOverallStatus(todayDateStr: String? = null): FilterCareStatus {
        return items.map { it.getStatus(todayDateStr) }.maxByOrNull { it.priority } ?: FilterCareStatus.OK
    }

    fun getMostUrgentDaysLeft(todayDateStr: String? = null): Int {
        return items.minOfOrNull { it.getDaysLeft(todayDateStr) } ?: 999
    }
}
