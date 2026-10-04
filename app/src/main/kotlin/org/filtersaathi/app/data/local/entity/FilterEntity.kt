package org.filtersaathi.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filters")
data class FilterEntity(
    @PrimaryKey
    val id: String,
    val locationName: String,
    val filterType: String, // "RO", "UV", "Community RO Plant"
    val healthScore: Int,   // 0 - 100
    val cartridgeDaysLeft: Int,
    val uvLampDaysLeft: Int,
    val membraneDaysLeft: Int,
    val lastCheckTimestamp: Long,
    val isUrgent: Boolean,
    val urgentIssue: String?
)
